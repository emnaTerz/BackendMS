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
public class ManyToOneReconciliationProcess implements ReconciliationProcess {

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
        System.out.println("Starting Many-to-One reconciliation process for Configuration ID: " + reconciliationConfiguration.getId());

        // Retrieve matching results using the reconciliation configuration ID
        List<MatchingResult> matchingResults = matchingResultsClient.getMatchingResultsByConfigurationIdAndStatus(reconciliationConfiguration.getMatchingConfigurationId(), "MATCHED");
        System.out.println("Retrieved " + matchingResults.size() + " matching results.");

        for (MatchingResult matchingResult : matchingResults) {
            // Extract the clean target message ID
            String targetMessageId = matchingResult.getTargetMessages().keySet().stream()
                    .findFirst().orElse(null);

            if (targetMessageId != null) {
                targetMessageId = targetMessageId.replace("targetMessageId", "").trim();
            }
            // Extract the clean source message IDs
            Set<String> sourceMessageIds = matchingResult.getSourceMessages().values().stream().collect(Collectors.toSet());

            if (reconciliationConfigurationService.doesGroupExist(reconciliationConfiguration.getId(), sourceMessageIds, targetMessageId)) {
                System.out.println("Group with sourceMessageIds: " + sourceMessageIds + " and targetMessageId: " + targetMessageId + " already exists. Skipping reconciliation.");
                continue; // Skip to the next matching result
            }
            System.out.println("Processing targetMessageId: " + targetMessageId + " with sourceMessageIds: " + sourceMessageIds);

            List<ReconciliationResult> existingResults = reconciliationConfigurationService.getReconciliationResultsByTargetMessageId(targetMessageId);

            ReconciliationResult existingReconciledResult = null;
            ReconciliationResult existingUnreconciledResult = null;

            // Check if there's an existing result for this target message with the same status
            for (ReconciliationResult existingResult : existingResults) {
                if ("Reconciled".equals(existingResult.getReconciliationStatus())) {
                    existingReconciledResult = existingResult;
                } else if ("Not Reconciled".equals(existingResult.getReconciliationStatus()) ||
                        "Partially Reconciled".equals(existingResult.getReconciliationStatus())) {
                    existingUnreconciledResult = existingResult;
                }
            }

            List<String> reconciledSourceIds = new ArrayList<>();
            List<String> unreconciledSourceIds = new ArrayList<>();
            Map<String, String> overallReconciledAttributes = new HashMap<>();
            Map<String, String> overallUnreconciledAttributes = new HashMap<>();
            StringBuilder reconciliationDetails = new StringBuilder();

            // Fetch attributes to reconciliation by reconciliation configuration ID
            List<AttributesToReconciliation> attributesToReconciliationList = attributesToReconciliationService.findAllByReconciliationConfigurationId(reconciliationConfiguration.getId())
                    .stream()
                    .map(AttributesToReconciliationMapper::mapToEntity)
                    .collect(Collectors.toList());

            if (attributesToReconciliationList == null || attributesToReconciliationList.isEmpty()) {
                System.err.println("No attributes to reconcile found for reconciliation configuration ID: " + reconciliationConfiguration.getId());
                continue;
            }

            double targetValue = 0.0;
            double sourceSum = 0.0;

            // Evaluate the target value once using the target attributes
            for (AttributesToReconciliation attributesToReconciliation : attributesToReconciliationList) {
                Map<String, String> targetValues = getValuesForPendingMessage(Collections.singletonMap(targetMessageId, targetMessageId), attributesToReconciliation.getTargetAttributes());
                targetValue = attributesToReconciliation.evaluateTarget(targetValues);
                System.out.println("Evaluated target value: " + targetValue);

                // Evaluate and sum source values using the source attributes
                for (String sourceId : sourceMessageIds) {
                    boolean exists = reconciliationConfigurationService.doesGroupExist(reconciliationConfiguration.getId(), sourceId, Collections.singleton(targetMessageId));

                    if (exists) {
                        System.out.println("Target ID " + targetMessageId + " has already been processed with source ID " + sourceId);
                        continue;
                    }

                    // Proceed with processing the source message
                    Map<String, String> sourceValues = getValuesForPendingMessage(Collections.singletonMap(sourceId, sourceId), attributesToReconciliation.getSourceAttributes());
                    double sourceValue = attributesToReconciliation.evaluateSource(sourceValues);
                    System.out.println("Evaluated source value for sourceId " + sourceId + ": " + sourceValue);

                    sourceSum += sourceValue;

                    // Track reconciled/unreconciled attributes with Source sum and Target value information
                    if (Math.abs(targetValue - sourceSum) <= Double.parseDouble(reconciliationConfiguration.getTolerance())) {
                        reconciledSourceIds.add(sourceId);
                        overallReconciledAttributes.put("Source sum: " + sourceSum + ", Target value: " + targetValue, "Reconciled");
                    } else {
                        unreconciledSourceIds.add(sourceId);
                        overallUnreconciledAttributes.put("Source sum: " + sourceSum + ", Target value: " + targetValue, "Unreconciled");
                    }
                }
            }

            System.out.println("Final Sum of source values: " + sourceSum);

            // Step 3: Compare source and target values and determine status
            String status;
            if (Math.abs(targetValue - sourceSum) <= Double.parseDouble(reconciliationConfiguration.getTolerance())) {
                status = "Reconciled";
            } else if (sourceSum < targetValue && sourceSum > 0) {
                status = "Partially Reconciled";
            } else {
                status = "Not Reconciled";
            }
            System.out.println("Final Reconciliation result: " + status);

            reconciliationDetails.append("Final reconciliation: Source sum: ").append(sourceSum)
                    .append(", Target value: ").append(targetValue)
                    .append(", Status: ").append(status)
                    .append("\n");

            // Update existing result or create a new one
            ReconciliationResult reconciliationResult;
            if (existingReconciledResult != null) {
                reconciliationResult = existingReconciledResult;
                // Add only new source IDs
                sourceMessageIds.stream()
                        .filter(id -> !reconciliationResult.getSourceMessages().containsKey(id))
                        .forEach(id -> reconciliationResult.getSourceMessages().put(cleanMessageId(id, "Source Message "), "Source Message " + cleanMessageId(id, "Source Message ")));
                reconciliationResult.getReconciledAttributes().putAll(overallReconciledAttributes);
            } else if (existingUnreconciledResult != null) {
                reconciliationResult = existingUnreconciledResult;
                // Add only new source IDs
                sourceMessageIds.stream()
                        .filter(id -> !reconciliationResult.getSourceMessages().containsKey(id))
                        .forEach(id -> reconciliationResult.getSourceMessages().put(cleanMessageId(id, "Source Message "), "Source Message " + cleanMessageId(id, "Source Message ")));
            } else {
                reconciliationResult = new ReconciliationResult();
                reconciliationResult.setReconciliationConfigurationId(reconciliationConfiguration.getId());
                reconciliationResult.setTargetMessages(Map.of(targetMessageId, "Target Message " + targetMessageId));
                reconciliationResult.setSourceMessages(sourceMessageIds.stream()
                        .collect(Collectors.toMap(id -> cleanMessageId(id, "Source Message "), id -> "Source Message " + cleanMessageId(id, "Source Message "))));
                reconciliationResult.setTimestamp(new Date());
                reconciliationResult.setReconciledAttributes(new HashMap<>(overallReconciledAttributes));
            }

            reconciliationResult.setReconciliationStatus(status);
            reconciliationResult.setReconciliationDetails(reconciliationDetails.toString());

            // Conditionally include unreconciledAttributes only if not empty
            if (!overallUnreconciledAttributes.isEmpty()) {
                reconciliationResult.setUnreconciledAttributes(new HashMap<>(overallUnreconciledAttributes));
            } else {
                reconciliationResult.setUnreconciledAttributes(null);
            }

            reconciliationResultsRepository.save(reconciliationResult);
            updateReconciliationStatus(matchingResult, "Reconciled".equals(status));
        }
    }

    // Helper method to clean the message ID
    private String cleanMessageId(String messageId, String prefix) {
        if (messageId.startsWith(prefix)) {
            return messageId.substring(prefix.length());
        }
        return messageId;
    }

            private Map<String, String> getValuesForPendingMessage(Map<String, String> messageMap, Map<Integer, IndexConfigurationAttributeToAdd> expectedAttributes) {
        Map<String, String> valuesMap = new HashMap<>();

        for (Map.Entry<String, String> entry : messageMap.entrySet()) {
            String pendingMessageId = entry.getValue();

            // Remove any prefixes like "Target Message " or "Source Message "
            if (pendingMessageId.startsWith("Target Message ")) {
                pendingMessageId = pendingMessageId.substring("Target Message ".length());
            } else if (pendingMessageId.startsWith("Source Message ")) {
                pendingMessageId = pendingMessageId.substring("Source Message ".length());
            }

            System.out.println("Fetching attributes for PendingMessage ID: " + pendingMessageId);

            List<ValueOfAttribute> attributes = indexConfigurationInterface.findByPendingMessageId(pendingMessageId);

            System.out.println("API Request: GET /api/config/attributes/" + pendingMessageId);
            System.out.println("Full Retrieved attributes object: " + attributes);

            if (attributes != null && !attributes.isEmpty()) {
                for (ValueOfAttribute attribute : attributes) {
                    // Normalize and trim the keys for comparison
                    String fetchedKey = attribute.getAttributeKey().trim().toLowerCase();
                    System.out.println("Fetched attribute key: " + fetchedKey);

                    for (IndexConfigurationAttributeToAdd expectedAttribute : expectedAttributes.values()) {
                        String expectedKey = expectedAttribute.getAttributeToAddKey().trim().toLowerCase();
                        System.out.println("Expected key in use: " + expectedKey);

                        if (expectedKey.equals(fetchedKey)) {
                            valuesMap.put(expectedAttribute.getAttributeToAddKey(), attribute.getValueOfAttribute());
                            System.out.println("Matched and fetched value for attribute key " + expectedKey + ": " + attribute.getValueOfAttribute());
                            break; // Key found, break inner loop
                        } else {
                            System.out.println("No match for attribute key: " + fetchedKey + " (expected: " + expectedKey + ")");
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
            if (sourceMessageId.startsWith("Target Message ")) {
                sourceMessageId = sourceMessageId.substring("Target Message ".length());
            } else if (sourceMessageId.startsWith("Source Message ")) {
                sourceMessageId = sourceMessageId.substring("Source Message ".length());
            }

            ResponseEntity<String> response = indexConfigurationInterface.updateStatusById(sourceMessageId, updateStatusRequest);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Source message status updated successfully for message ID: " + sourceMessageId);
            } else {
                System.err.println("Failed to update source message status for message ID: " + sourceMessageId);
            }
        });

        matchingResult.getTargetMessages().values().forEach(targetMessageId -> {
            if (targetMessageId.startsWith("targetMessageId")) {
                targetMessageId = targetMessageId.substring("targetMessageId".length());
            } else if (targetMessageId.startsWith("Source Message ")) {
                targetMessageId = targetMessageId.substring("Source Message ".length());
            }

            ResponseEntity<String> response = indexConfigurationInterface.updateStatusById(targetMessageId, updateStatusRequest);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Target message status updated successfully for message ID: " + targetMessageId);
            } else {
                System.err.println("Failed to update target message status for message ID: " + targetMessageId);
            }
        });
    }
}

