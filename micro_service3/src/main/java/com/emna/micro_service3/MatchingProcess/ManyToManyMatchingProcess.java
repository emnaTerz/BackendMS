package com.emna.micro_service3.MatchingProcess;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.emna.micro_service3.Repository.AttributesToMatchRepository;
import com.emna.micro_service3.Repository.MatchingResultsRepository;
import com.emna.micro_service3.client.IndexConfigurationInterface;
import com.emna.micro_service3.dto.UpdateStatusRequest;
import com.emna.micro_service3.model.*;
import com.emna.micro_service3.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManyToManyMatchingProcess implements MatchingProcess {

    @Autowired
    private ElasticsearchClient client;

    @Autowired
    private MatchingResultsRepository matchingResultsRepository;

    @Autowired
    private AttributesToMatchRepository attributesToMatchRepository;

    @Autowired
    private FormulaEvaluator formulaEvaluator;

    @Autowired
    private IndexConfigurationInterface indexConfigurationInterface;
    @Autowired
    private MatchingService matchingService;



    @Override
    public void process(MatchingConfiguration matchingConfiguration) throws ScriptException, IOException {
        System.out.println("Starting process for matching configuration ID: " + matchingConfiguration.getId());

        // Step 1: Retrieve source and target messages by configuration ID
        List<PendingMessage> sourceMessages = getMessagesByConfigId(matchingConfiguration.getSourceId());
        List<PendingMessage> targetMessages = getMessagesByConfigId(matchingConfiguration.getTargetId());

        System.out.println("Source messages: " + sourceMessages);
        System.out.println("Target messages: " + targetMessages);

        Set<String> processedSourceTargetPairs = new HashSet<>();
        Map<String, String> sourceMessageMatchStatus = new HashMap<>();
        Map<String, String> targetMessageMatchStatus = new HashMap<>();

        // Step 2: Group sources based on their attributes
        Map<String, List<PendingMessage>> sourceGroups = groupByAttributes(sourceMessages, matchingConfiguration.getId());

        // Step 3: Process each group of sources against all targets
        for (Map.Entry<String, List<PendingMessage>> entry : sourceGroups.entrySet()) {
            String groupKey = entry.getKey();
            List<PendingMessage> groupedSources = entry.getValue();

            Map<String, String> overallMatchedAttributes = new HashMap<>();
            Map<String, String> overallUnmatchedAttributes = new HashMap<>();
            List<String> matchedTargetIds = new ArrayList<>();
            List<String> unmatchedTargetIds = new ArrayList<>();
            StringBuilder matchDetails = new StringBuilder();

            for (PendingMessage targetMessage : targetMessages) {
                String pairId = groupKey + "-" + targetMessage.getId();
                if (processedSourceTargetPairs.contains(pairId)) {
                    continue; // Skip already processed pairs
                }

                Map<String, String> matchedAttributes = new HashMap<>();
                Map<String, String> unmatchedAttributes = new HashMap<>();

                boolean match = isMatch(groupedSources, targetMessage, matchingConfiguration.getId(), matchedAttributes, unmatchedAttributes);

                if (match) {
                    matchedTargetIds.add(targetMessage.getId());
                    overallMatchedAttributes.putAll(matchedAttributes);
                    matchDetails.append("Source group key: ").append(groupKey)
                            .append(", Target message ID: ").append(targetMessage.getId())
                            .append(" did match.\n");
                    targetMessageMatchStatus.put(targetMessage.getId(), "MATCHED");
                    groupedSources.forEach(sourceMessage -> sourceMessageMatchStatus.put(sourceMessage.getId(), "MATCHED"));
                } else {
                    unmatchedTargetIds.add(targetMessage.getId());
                    overallUnmatchedAttributes.putAll(unmatchedAttributes);
                    matchDetails.append("Source group key: ").append(groupKey)
                            .append(", Target message ID: ").append(targetMessage.getId())
                            .append(" did not match.\n");
                }

                processedSourceTargetPairs.add(pairId); // Mark this source-target pair as processed

                // Log matched and unmatched attributes for each comparison
                System.out.println("Matched attributes for source group key " + groupKey + " and target message ID " + targetMessage.getId() + ": " + matchedAttributes);
                System.out.println("Unmatched attributes for source group key " + groupKey + " and target message ID " + targetMessage.getId() + ": " + unmatchedAttributes);
            }

            // Create and save the matched results
            if (!matchedTargetIds.isEmpty()) {
                MatchingResult matchedResult = new MatchingResult();
                matchedResult.setMatchingConfigurationId(matchingConfiguration.getId());
                matchedResult.setSourceMessages(groupedSources.stream().map(PendingMessage::getId).collect(Collectors.toMap(id -> id, id ->  id)));
                matchedResult.setTargetMessages(matchedTargetIds.stream().collect(Collectors.toMap(id -> id, id -> id)));
                matchedResult.setTimestamp(new Date());
                matchedResult.setMatchedAttributes(new HashMap<>(overallMatchedAttributes));
                matchedResult.setMatchStatus("Matched");
                matchedResult.setMatchDetails(matchDetails.toString());

                System.out.println("Saving matched result for source group key " + groupKey);
                matchingResultsRepository.save(matchedResult);
            }

            // Create and save the unmatched results
            if (!unmatchedTargetIds.isEmpty()) {
                MatchingResult unmatchedResult = new MatchingResult();
                unmatchedResult.setMatchingConfigurationId(matchingConfiguration.getId());
                unmatchedResult.setSourceMessages(groupedSources.stream().map(PendingMessage::getId).collect(Collectors.toMap(id -> id, id -> "Source Message " + id)));
                unmatchedResult.setTargetMessages(unmatchedTargetIds.stream().collect(Collectors.toMap(id -> id, id -> "Target Message " + id)));
                unmatchedResult.setTimestamp(new Date());
                unmatchedResult.setUnmatchedAttributes(new HashMap<>(overallUnmatchedAttributes));
                unmatchedResult.setMatchStatus("Unmatched");
                unmatchedResult.setMatchDetails(matchDetails.toString());

                System.out.println("Saving unmatched result for source group key " + groupKey);
                matchingResultsRepository.save(unmatchedResult);
            }
        }

        // Step 5: Update the status of each source and target message
        System.out.println("Updating message statuses...");
        updateMessageStatuses(sourceMessageMatchStatus);
        updateMessageStatuses(targetMessageMatchStatus);
    }



    private Map<String, List<PendingMessage>> groupByAttributes(List<PendingMessage> sourceMessages, String matchingConfigId) throws ScriptException {
        List<AttributesToMatch> attributesToMatchList = getAttributesToMatchByMatchingConfigId(matchingConfigId);
        Map<String, List<PendingMessage>> groupedSources = new HashMap<>();

        for (PendingMessage sourceMessage : sourceMessages) {
            StringBuilder groupKey = new StringBuilder();
            for (AttributesToMatch attributesToMatch : attributesToMatchList) {
                for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : attributesToMatch.getSourceAttributes().entrySet()) {
                    String sourceValue = getAttributeValue(sourceMessage.getId(), entry.getValue().getAttributeToAddKey());
                    groupKey.append(sourceValue).append("-");
                }
            }
            String finalGroupKey = groupKey.toString();
            groupedSources.computeIfAbsent(finalGroupKey, k -> new ArrayList<>()).add(sourceMessage);
        }
        return groupedSources;
    }

    private boolean isMatch(List<PendingMessage> sourceMessages, PendingMessage targetMessage, String matchingConfigId, Map<String, String> matchedAttributes, Map<String, String> unmatchedAttributes) throws ScriptException {
        List<AttributesToMatch> attributesToMatchList = getAttributesToMatchByMatchingConfigId(matchingConfigId);

        if (attributesToMatchList == null || attributesToMatchList.isEmpty()) {
            System.err.println("**Error: No attributes to match found for matching configuration ID: " + matchingConfigId + "**");
            return false;
        }

        System.out.println("Attributes to match list: " + attributesToMatchList);

        boolean overallMatch = true;

        for (AttributesToMatch attributesToMatch : attributesToMatchList) {
            Map<String, String> sourceValues = new HashMap<>();
            Map<String, String> targetValues = new HashMap<>();

            // Get source values
            for (PendingMessage sourceMessage : sourceMessages) {
                for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : attributesToMatch.getSourceAttributes().entrySet()) {
                    String sourceValue = getAttributeValue(sourceMessage.getId(), entry.getValue().getAttributeToAddKey());
                    sourceValues.put(entry.getValue().getAttributeToAddKey(), sourceValue);
                }
            }

            // Get target values
            for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : attributesToMatch.getTargetAttributes().entrySet()) {
                String targetValue = getAttributeValue(targetMessage.getId(), entry.getValue().getAttributeToAddKey());
                targetValues.put(entry.getValue().getAttributeToAddKey(), targetValue);
            }

            // Log source and target values
            System.out.println("Source values for attributes to match ID " + attributesToMatch.getId() + ": " + sourceValues);
            System.out.println("Target values for attributes to match ID " + attributesToMatch.getId() + ": " + targetValues);

            // Evaluate the formula
            String formula = attributesToMatch.buildFormula(sourceValues, targetValues);
            System.out.println("Final formula: " + formula);

            boolean match = formulaEvaluator.evaluate(formula);
            System.out.println("Match result: " + match);

            if (match) {
                matchedAttributes.put(attributesToMatch.getId(), formula);
                System.out.println("Added to matched attributes: " + attributesToMatch.getId() + " -> " + formula); // Log matched attribute addition
            } else {
                overallMatch = false;
                unmatchedAttributes.put(attributesToMatch.getId(), formula);
                System.out.println("Added to unmatched attributes: " + attributesToMatch.getId() + " -> " + formula); // Log unmatched attribute addition
            }
        }

        System.out.println("Matched attributes after evaluation: " + matchedAttributes);
        System.out.println("Unmatched attributes after evaluation: " + unmatchedAttributes);

        return overallMatch;
    }





        private void updateMessageStatuses(Map<String, String> messageMatchStatus) {
        for (Map.Entry<String, String> entry : messageMatchStatus.entrySet()) {
            String messageId = entry.getKey();
            String isMatched = entry.getValue();
            UpdateStatusRequest updateStatusRequest = new UpdateStatusRequest();
            updateStatusRequest.setStatus(isMatched);

            try {
                ResponseEntity<String> response = indexConfigurationInterface.updateStatusById(messageId, updateStatusRequest);
                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Status updated successfully for message ID: " + messageId);
                } else {
                    System.err.println("Failed to update status for message ID: " + messageId + " - Response: " + response.getStatusCode());
                }
            } catch (Exception e) {
                System.err.println("Exception occurred while updating status for message ID: " + messageId);
                e.printStackTrace();
            }
        }
    }

    private List<AttributesToMatch> getAttributesToMatchByMatchingConfigId(String matchingConfigId) {
        List<AttributesToMatch> attributesToMatchList = attributesToMatchRepository.findByMatchingConfigurationId(matchingConfigId);
        System.out.println("Fetched attributes to match for matching config ID " + matchingConfigId + ": " + attributesToMatchList); // Log fetched attributes to match
        return attributesToMatchList;
    }

    private String getAttributeValue(String pendingMessageId, String attributeKey) {
        try {
            System.out.println("Fetching attribute value for pendingMessageId: " + pendingMessageId + ", attributeKey: " + attributeKey);

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("valueofaatributetoadd")
                    .query(q -> q
                            .bool(b -> b
                                    .must(m -> m.match(t -> t.field("pendingMessageId").query(pendingMessageId)))
                                    .must(m -> m.match(t -> t.field("attributeKey").query(attributeKey)))
                            )
                    )
            );

            SearchResponse<ValueOfAttribute> searchResponse = client.search(searchRequest, ValueOfAttribute.class);

            System.out.println("Search response: " + searchResponse.toString());

            return searchResponse.hits().hits().stream()
                    .map(Hit::source)
                    .findFirst()
                    .map(ValueOfAttribute::getValueOfAttribute)
                    .orElse(null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<PendingMessage> getMessagesByConfigId(String configId) {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("pending_messages")
                    .query(q -> q
                            .match(m -> m.field("indexConfigurationId").query(configId))
                    )
            );

            SearchResponse<PendingMessage> searchResponse = client.search(searchRequest, PendingMessage.class);

            return searchResponse.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
