
package com.emna.micro_service4.ReconciliationProcess;

import com.emna.micro_service3.dto.UpdateStatusRequest;
import com.emna.micro_service3.model.IndexConfigurationAttributeToAdd;
import com.emna.micro_service4.Repository.ReconciliationResultsRepository;
import com.emna.micro_service4.client.IndexConfigurationInterface;
import com.emna.micro_service4.client.MatchingResultsClient;
import com.emna.micro_service4.mapper.AttributesToReconciliationMapper;
import com.emna.micro_service4.model.*;
import com.emna.micro_service4.service.AttributesToReconciliationService;
import com.emna.micro_service4.service.ReconciliationConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManyToManyReconciliationProcess implements ReconciliationProcess {

    @Autowired
    private ReconciliationResultsRepository reconciliationResultsRepository;

    @Autowired
    private MatchingResultsClient matchingResultsClient;

    @Autowired
    private IndexConfigurationInterface indexConfigurationInterface;

    @Autowired
    private ReconciliationConfigurationService reconciliationConfigurationService;

    @Autowired
    private AttributesToReconciliationService attributesToReconciliationService;

    @Override
    public void process(ReconciliationConfiguration reconciliationConfiguration) throws ScriptException, IOException {
        System.out.println("Starting Many-to-Many reconciliation process for Configuration ID: " + reconciliationConfiguration.getId());

        List<MatchingResult> matchingResults = matchingResultsClient.getMatchingResultsByConfigurationIdAndStatus(reconciliationConfiguration.getMatchingConfigurationId(), "MATCHED");
        System.out.println("Retrieved " + matchingResults.size() + " matching results.");

        for (MatchingResult matchingResult : matchingResults) {
            // Extracting the keys directly without any modification
            Set<String> sourceMessageIds = matchingResult.getSourceMessages().keySet();
            Set<String> targetMessageIds = matchingResult.getTargetMessages().keySet();

            System.out.println("Processing sourceMessageIds: " + sourceMessageIds + " with targetMessageIds: " + targetMessageIds);

            boolean allExist = true;
            List<String> missingSourceIds = new ArrayList<>();
            for (String sourceMessageId : sourceMessageIds) {
                List<ReconciliationResult> existingResults = reconciliationResultsRepository.findBySourceMessagesSourceMessageId(sourceMessageId);
                if (existingResults.isEmpty()) {
                    allExist = false;
                    missingSourceIds.add(sourceMessageId); // Track missing IDs
                }
            }

            if (!allExist) {
                System.out.println("Some sourceMessageIds do not exist: " + missingSourceIds);
                // You can decide to mark this reconciliation as "Partially Reconciled" or continue with existing data
            }

            // Continue with the reconciliation process even if not all source IDs exist
            ReconciliationResult existingReconciledResult = null;
            ReconciliationResult existingUnreconciledResult = null;

            for (String sourceMessageId : sourceMessageIds) {
                List<ReconciliationResult> existingResults = reconciliationResultsRepository.findBySourceMessagesSourceMessageId(sourceMessageId);
                for (ReconciliationResult existingResult : existingResults) {
                    if ("Reconciled".equals(existingResult.getReconciliationStatus())) {
                        existingReconciledResult = existingResult;
                    } else if ("Not Reconciled".equals(existingResult.getReconciliationStatus()) ||
                            "Partially Reconciled".equals(existingResult.getReconciliationStatus())) {
                        existingUnreconciledResult = existingResult;
                    }
                }
            }

            Map<String, String> targetMessagesMap = new HashMap<>();
            Map<String, String> overallReconciledAttributes = new HashMap<>();
            Map<String, String> overallUnreconciledAttributes = new HashMap<>();
            StringBuilder reconciliationDetails = new StringBuilder();

            List<AttributesToReconciliation> attributesToReconciliationList = attributesToReconciliationService.findAllByReconciliationConfigurationId(reconciliationConfiguration.getId())
                    .stream()
                    .map(AttributesToReconciliationMapper::mapToEntity)
                    .collect(Collectors.toList());

            if (attributesToReconciliationList.isEmpty()) {
                System.err.println("No attributes to reconcile found for reconciliation configuration ID: " + reconciliationConfiguration.getId());
                continue;
            }

            double sourceSum = 0.0;
            double targetSum = 0.0;

            for (AttributesToReconciliation attributesToReconciliation : attributesToReconciliationList) {
                // Evaluate source values for all source messages
                for (String sourceId : sourceMessageIds) {
                    Map<String, String> sourceValues = getValuesForPendingMessage(
                            Collections.singletonMap(sourceId, sourceId), // Directly using the sourceId as the key
                            attributesToReconciliation.getSourceAttributes()
                    );
                    double sourceValue = attributesToReconciliation.evaluateSource(sourceValues);
                    sourceSum += sourceValue;
                    System.out.println("Accumulated source value for sourceId " + sourceId + ": " + sourceSum);
                }

                // Evaluate target values for all target messages
                for (String targetId : targetMessageIds) {
                    Map<String, String> targetValues = getValuesForPendingMessage(
                            Collections.singletonMap(targetId, targetId), // Directly using the targetId as the key
                            attributesToReconciliation.getTargetAttributes()
                    );
                    double targetValue = attributesToReconciliation.evaluateTarget(targetValues);
                    targetSum += targetValue;
                    System.out.println("Accumulated target value for targetId " + targetId + ": " + targetSum);

                    // Determine reconciliation status for this pair
                    if (Math.abs(sourceSum - targetSum) <= Double.parseDouble(reconciliationConfiguration.getTolerance())) {
                        targetMessagesMap.put(targetId, "Target Message " + targetId);
                        overallReconciledAttributes.put("Source sum: " + sourceSum + ", Target value: " + targetValue, "Reconciled");
                    } else {
                        targetMessagesMap.put(targetId, "Target Message " + targetId);
                        overallUnreconciledAttributes.put("Source sum: " + sourceSum + ", Target value: " + targetValue, "Unreconciled");
                    }
                }
            }

            System.out.println("Final Sum of target Sum: " + targetSum);

            String status;
            if (Math.abs(sourceSum - targetSum) <= Double.parseDouble(reconciliationConfiguration.getTolerance())) {
                status = "Reconciled";
                overallUnreconciledAttributes.clear();  // Clear unreconciled attributes if status is Reconciled
            } else if (targetSum < sourceSum && targetSum > 0) {
                status = "Partially Reconciled";
            } else {
                status = "Not Reconciled";
            }

            reconciliationDetails.append("Final reconciliation: Source Sum: ").append(sourceSum)
                    .append(", Target Sum: ").append(targetSum)
                    .append(", Status: ").append(status)
                    .append("\n");

            ReconciliationResult reconciliationResult;
            if (existingReconciledResult != null) {
                reconciliationResult = existingReconciledResult;
                reconciliationResult.getTargetMessages().putAll(targetMessagesMap);
                reconciliationResult.getReconciledAttributes().putAll(overallReconciledAttributes);
            } else if (existingUnreconciledResult != null) {
                reconciliationResult = existingUnreconciledResult;
                reconciliationResult.getTargetMessages().putAll(targetMessagesMap);
            } else {
                reconciliationResult = new ReconciliationResult();
                reconciliationResult.setReconciliationConfigurationId(reconciliationConfiguration.getId());
                reconciliationResult.setSourceMessages(matchingResult.getSourceMessages());
                reconciliationResult.setTargetMessages(targetMessagesMap);
                reconciliationResult.setTimestamp(new Date());
                reconciliationResult.setReconciledAttributes(new HashMap<>(overallReconciledAttributes));
            }

            reconciliationResult.setReconciliationStatus(status);
            reconciliationResult.setReconciliationDetails(reconciliationDetails.toString());

            if (!overallUnreconciledAttributes.isEmpty()) {
                reconciliationResult.setUnreconciledAttributes(new HashMap<>(overallUnreconciledAttributes));
            } else {
                reconciliationResult.setUnreconciledAttributes(null);
            }

            reconciliationResultsRepository.save(reconciliationResult);
            updateReconciliationStatus(matchingResult, "Reconciled".equals(status));
        }
    }

    private Map<String, String> getValuesForPendingMessage(Map<String, String> messageMap, Map<Integer, IndexConfigurationAttributeToAdd> expectedAttributes) {
        Map<String, String> valuesMap = new HashMap<>();

        for (String pendingMessageId : messageMap.keySet()) {
            System.out.println("Fetching attributes for PendingMessage ID: " + pendingMessageId);

            // Now we correctly fetch attributes using just the key
            List<ValueOfAttribute> attributes = indexConfigurationInterface.findByPendingMessageId(pendingMessageId);

            System.out.println("API Request: GET /api/config/attributes/" + pendingMessageId);
            System.out.println("Full Retrieved attributes object: " + attributes);

            if (attributes != null && !attributes.isEmpty()) {
                for (ValueOfAttribute attribute : attributes) {
                    String expectedKey = attribute.getAttributeKey();
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
        String status = isReconciled ? "Reconciled" : "Not Reconciled";
        UpdateStatusRequest updateStatusRequest = new UpdateStatusRequest();
        updateStatusRequest.setStatus(status);

        matchingResult.getSourceMessages().values().forEach(sourceMessageId -> {
            ResponseEntity<String> response = indexConfigurationInterface.updateStatusById(sourceMessageId, updateStatusRequest);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Source message status updated successfully for message ID: " + sourceMessageId);
            } else {
                System.err.println("Failed to update source message status for message ID: " + sourceMessageId);
            }
        });

        matchingResult.getTargetMessages().values().forEach(targetMessageId -> {
            ResponseEntity<String> response = indexConfigurationInterface.updateStatusById(targetMessageId, updateStatusRequest);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Target message status updated successfully for message ID: " + targetMessageId);
            } else {
                System.err.println("Failed to update target message status for message ID: " + targetMessageId);
            }
        });
    }
}
