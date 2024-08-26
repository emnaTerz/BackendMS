package com.emna.micro_service4.ReconciliationProcess;

import com.emna.micro_service3.dto.UpdateStatusRequest;
import com.emna.micro_service3.model.IndexConfigurationAttributeToAdd;
import com.emna.micro_service4.Repository.AttributesToReconciliationRepository;
import com.emna.micro_service4.Repository.ReconciliationResultsRepository;
import com.emna.micro_service4.client.IndexConfigurationInterface;
import com.emna.micro_service4.client.MatchingResultsClient;
import com.emna.micro_service4.model.*;
import com.emna.micro_service4.service.ReconciliationConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.*;

@Service
public class OneToOneReconciliationProcess implements ReconciliationProcess {

    @Autowired
    private ReconciliationResultsRepository reconciliationResultsRepository;

    @Autowired
    private AttributesToReconciliationRepository attributesToReconciliationRepository;

    @Autowired
    private MatchingResultsClient matchingResultsClient;

    @Autowired
    private IndexConfigurationInterface indexConfigurationInterface;

    @Autowired
    private FormulaEvaluator formulaEvaluator;

    @Autowired
    private ReconciliationConfigurationService reconciliationConfigurationService;

    @Override
    public void process(ReconciliationConfiguration reconciliationConfiguration) throws ScriptException, IOException {
        System.out.println("Starting reconciliation process for Configuration ID: " + reconciliationConfiguration.getId());

        // Step 1: Retrieve matching results from the other microservice
        List<MatchingResult> matchingResults = matchingResultsClient.getMatchingResultsByConfigurationIdAndStatus(reconciliationConfiguration.getMatchingConfigurationId(), "MATCHED");
        System.out.println("Retrieved " + matchingResults.size() + " matching results.");

        // Step 2: Iterate over each MatchingResult
        for (MatchingResult matchingResult : matchingResults) {
            System.out.println("Processing MatchingResult ID: " + matchingResult.getId());

            // Extract the message IDs using the keys from the map
            String sourceMessageId = matchingResult.getSourceMessages().keySet().iterator().next();
            String targetMessageId = matchingResult.getTargetMessages().keySet().iterator().next();

            // Add the logging statement here
            System.out.println("Querying with: reconciliationConfigurationId = " + reconciliationConfiguration.getId() +
                    ", sourceMessageId = " + sourceMessageId +
                    ", targetMessageId = " + targetMessageId);

            // Check if the reconciliation result already exists for this pair
            if (reconciliationConfigurationService.doesPairExist(reconciliationConfiguration.getId(), sourceMessageId, targetMessageId)) {
                System.out.println("Skipping already processed pair.");
                continue; // Skip the pair as it is already processed.
            } else {
                System.out.println("Pair has not been processed yet. Proceeding with reconciliation.");
            }

            Map<String, String> reconciledAttributes = new HashMap<>();
            Map<String, String> unreconciledAttributes = new HashMap<>();

            // Step 3: Perform reconciliation for each matched pair
            boolean isReconciled = reconcileAttributes(matchingResult, reconciliationConfiguration.getId(), reconciledAttributes, unreconciledAttributes, reconciliationConfiguration.getTolerance());

            // Create and save ReconciliationResult
            ReconciliationResult reconciliationResult = new ReconciliationResult();
            reconciliationResult.setReconciliationConfigurationId(reconciliationConfiguration.getId());
            reconciliationResult.setSourceMessages(matchingResult.getSourceMessages());
            reconciliationResult.setTargetMessages(matchingResult.getTargetMessages());
            reconciliationResult.setTimestamp(new Date());

            if (isReconciled) {
                System.out.println("Reconciliation successful for pair: " + matchingResult.getId());
                reconciliationResult.setReconciliationStatus("Reconciled");
                reconciliationResult.setReconciliationDetails("Source and Target messages were successfully reconciled.");
                reconciliationResult.setReconciledAttributes(new HashMap<>(reconciledAttributes));
                reconciliationResult.setUnreconciledAttributes(new HashMap<>());
            } else {
                System.out.println("Reconciliation failed for pair: " + matchingResult.getId());
                reconciliationResult.setReconciliationStatus("Not Reconciled");
                reconciliationResult.setReconciliationDetails("Source and Target messages could not be reconciled.");
                reconciliationResult.setReconciledAttributes(new HashMap<>(reconciledAttributes));
                reconciliationResult.setUnreconciledAttributes(new HashMap<>(unreconciledAttributes));
            }

            reconciliationResultsRepository.save(reconciliationResult);

            updateReconciliationStatus(matchingResult, isReconciled);
        }

        System.out.println("Reconciliation process completed for Configuration ID: " + reconciliationConfiguration.getId());
    }

    private boolean reconcileAttributes(MatchingResult matchingResult, String reconciliationConfigId, Map<String, String> reconciledAttributes, Map<String, String> unreconciledAttributes, String tolerance) throws ScriptException {
        System.out.println("Reconciling attributes for MatchingResult ID: " + matchingResult.getId());

        List<AttributesToReconciliation> attributesToReconciliationList = getAttributesToReconciliationByReconciliationConfigId(reconciliationConfigId);

        if (attributesToReconciliationList == null || attributesToReconciliationList.isEmpty()) {
            System.err.println("Error: No attributes to reconcile found for reconciliation configuration ID: " + reconciliationConfigId);
            return false;
        }

        boolean overallReconciliation = true;

        for (AttributesToReconciliation attributesToReconciliation : attributesToReconciliationList) {
            // Fetch values for the source and target attributes specifically expected for reconciliation
            Map<String, String> sourceValues = getValuesForPendingMessage(matchingResult.getSourceMessages(), attributesToReconciliation.getSourceAttributes());
            Map<String, String> targetValues = getValuesForPendingMessage(matchingResult.getTargetMessages(), attributesToReconciliation.getTargetAttributes());

            // Log the source and target values fetched
            System.out.println("Source values: " + sourceValues);
            System.out.println("Target values: " + targetValues);

            // Ensure that all expected values are present
            for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : attributesToReconciliation.getSourceAttributes().entrySet()) {
                if (!sourceValues.containsKey(entry.getValue().getAttributeToAddKey())) {
                    System.err.println("Error: Missing value for source attribute key: " + entry.getValue().getAttributeToAddKey());
                }
            }
            for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : attributesToReconciliation.getTargetAttributes().entrySet()) {
                if (!targetValues.containsKey(entry.getValue().getAttributeToAddKey())) {
                    System.err.println("Error: Missing value for target attribute key: " + entry.getValue().getAttributeToAddKey());
                }
            }

            // Build and evaluate the formula
            String formula = attributesToReconciliation.buildFormula(sourceValues, targetValues);
            System.out.println("Evaluating formula: " + formula);

            boolean match = formulaEvaluator.evaluateWithTolerance(formula, tolerance);
            System.out.println("Reconciliation result for formula [" + formula + "]: " + match);

            if (match) {
                reconciledAttributes.put(attributesToReconciliation.getId(), formula);
            } else {
                overallReconciliation = false;
                unreconciledAttributes.put(attributesToReconciliation.getId(), formula);
            }
        }

        return overallReconciliation;
    }

    private Map<String, String> getValuesForPendingMessage(Map<String, String> messageMap, Map<Integer, IndexConfigurationAttributeToAdd> expectedAttributes) {
        Map<String, String> valuesMap = new HashMap<>();

        for (String pendingMessageId : messageMap.keySet()) {
            System.out.println("Fetching attributes for PendingMessage ID: " + pendingMessageId);

            List<ValueOfAttribute> attributes = indexConfigurationInterface.findByPendingMessageId(pendingMessageId);
            System.out.println("Retrieved attributes: " + attributes);

            if (attributes != null && !attributes.isEmpty()) {
                for (ValueOfAttribute attribute : attributes) {
                    String expectedKey = attribute.getAttributeKey();
                    // Direct comparison of expected keys with retrieved keys
                    for (IndexConfigurationAttributeToAdd expectedAttribute : expectedAttributes.values()) {
                        if (expectedAttribute.getAttributeToAddKey().equals(expectedKey)) {
                            valuesMap.put(expectedKey, attribute.getValueOfAttribute());
                            System.out.println("Matched and fetched value for attribute key " + expectedKey + ": " + attribute.getValueOfAttribute());
                        } else {
                            System.out.println("No match for attribute key: " + expectedKey);
                        }
                    }
                }
            } else {
                System.err.println("Error: Attributes list is empty or null for PendingMessage ID: " + pendingMessageId);
            }
        }


        return valuesMap;
    }

    private void updateReconciliationStatus(MatchingResult matchingResult, boolean isReconciled) {
        // Determine the status based on whether reconciliation was successful
        String status = isReconciled ? "Reconciled" : "Not Reconciled";

        // Create the update request
        UpdateStatusRequest updateStatusRequest = new UpdateStatusRequest();
        updateStatusRequest.setStatus(status);

        // Update status for each source message that was reconciled
        for (String sourceMessageId : matchingResult.getSourceMessages().keySet()) {
            try {
                ResponseEntity<String> response = indexConfigurationInterface.updateStatusById(sourceMessageId, updateStatusRequest);
                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Source message status updated successfully for message ID: " + sourceMessageId);
                } else {
                    System.err.println("Failed to update source message status for message ID: " + sourceMessageId + " - Response: " + response.getStatusCode());
                }
            } catch (Exception e) {
                System.err.println("Exception occurred while updating source message status for message ID: " + sourceMessageId);
                e.printStackTrace();
            }
        }

// Update status for each target message that was reconciled
        for (String targetMessageId : matchingResult.getTargetMessages().keySet()) {
            try {
                ResponseEntity<String> response = indexConfigurationInterface.updateStatusById(targetMessageId, updateStatusRequest);
                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Target message status updated successfully for message ID: " + targetMessageId);
                } else {
                    System.err.println("Failed to update target message status for message ID: " + targetMessageId + " - Response: " + response.getStatusCode());
                }
            } catch (Exception e) {
                System.err.println("Exception occurred while updating target message status for message ID: " + targetMessageId);
                e.printStackTrace();
            }
        }

    }

    private List<AttributesToReconciliation> getAttributesToReconciliationByReconciliationConfigId(String reconciliationConfigId) {
        return attributesToReconciliationRepository.findByReconciliationConfigurationId(reconciliationConfigId);
    }
}
