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
public class ManyToOneMatchingProcess implements MatchingProcess {

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
        List<PendingMessage> sourceMessages = getMessagesByConfigId(matchingConfiguration.getSourceId());
        List<PendingMessage> targetMessages = getMessagesByConfigId(matchingConfiguration.getTargetId());

        Map<String, Boolean> sourceMessageMatchStatus = new HashMap<>();
        Map<String, Boolean> targetMessageMatchStatus = new HashMap<>();

        for (PendingMessage targetMessage : targetMessages) {
            targetMessageMatchStatus.put(targetMessage.getId(), false);

            // Fetch existing matching results for the target message
            List<MatchingResult> existingTargetResults = matchingService.getMatchingResultsByTargetMessageId(targetMessage.getId());

            MatchingResult existingMatchedResult = null;
            MatchingResult existingUnmatchedResult = null;

// Check if there's an existing result for this target message with the same status
            for (MatchingResult existingResult : existingTargetResults) {
                if ("Matched".equals(existingResult.getMatchStatus())) {
                    existingMatchedResult = existingResult;
                } else if ("Reconciled".equals(existingResult.getMatchStatus())) {
                    existingMatchedResult = existingResult;
                } else if ("Unmatched".equals(existingResult.getMatchStatus())) {
                    existingUnmatchedResult = existingResult;
                } else if ("Not Reconciled".equals(existingResult.getMatchStatus())) {
                    existingUnmatchedResult = existingResult;
                } else if ("Partially Reconciled".equals(existingResult.getMatchStatus())) {
                    existingMatchedResult = existingResult;
                }
            }

            List<String> matchedSourceIds = new ArrayList<>();
            List<String> unmatchedSourceIds = new ArrayList<>();
            Map<String, String> overallMatchedAttributes = new HashMap<>();
            Map<String, String> overallUnmatchedAttributes = new HashMap<>();
            StringBuilder matchDetails = new StringBuilder();

            for (PendingMessage sourceMessage : sourceMessages) {
                sourceMessageMatchStatus.put(sourceMessage.getId(), false);

                boolean exists = matchingService.doesTargetWithSourcesExist(matchingConfiguration.getId(), targetMessage.getId(), Collections.singletonList(sourceMessage.getId()));

                if (exists) {
                    System.out.println("Target ID " + targetMessage.getId() + " has already been processed with source ID " + sourceMessage.getId());
                    continue;
                }

                Map<String, String> matchedAttributes = new HashMap<>();
                Map<String, String> unmatchedAttributes = new HashMap<>();

                boolean match = isMatch(sourceMessage, targetMessage, matchingConfiguration.getId(), matchedAttributes, unmatchedAttributes);

                if (match) {
                    matchedSourceIds.add(sourceMessage.getId());
                    overallMatchedAttributes.putAll(matchedAttributes);
                    matchDetails.append("Source message ID: ").append(sourceMessage.getId())
                            .append(", Target message ID: ").append(targetMessage.getId())
                            .append(" did match.\n");
                    sourceMessageMatchStatus.put(sourceMessage.getId(), true);
                    targetMessageMatchStatus.put(targetMessage.getId(), true);
                } else {
                    unmatchedSourceIds.add(sourceMessage.getId());
                    overallUnmatchedAttributes.putAll(unmatchedAttributes);
                    matchDetails.append("Source message ID: ").append(sourceMessage.getId())
                            .append(", Target message ID: ").append(targetMessage.getId())
                            .append(" did not match.\n");
                }
            }

            if (!matchedSourceIds.isEmpty()) {
                if (existingMatchedResult != null) {
                    // Update existing matched result
                    existingMatchedResult.getSourceMessages().putAll(
                            matchedSourceIds.stream().collect(Collectors.toMap(id -> id, id -> "Source Message " + id))
                    );
                    existingMatchedResult.getMatchedAttributes().putAll(overallMatchedAttributes);
                    existingMatchedResult.setMatchDetails(existingMatchedResult.getMatchDetails() + matchDetails.toString());
                    matchingResultsRepository.save(existingMatchedResult);
                } else {
                    // Create a new matched result
                    MatchingResult matchedResult = new MatchingResult();
                    matchedResult.setMatchingConfigurationId(matchingConfiguration.getId());
                    matchedResult.setTargetMessages(Map.of( targetMessage.getId(),"targetMessageId"+ targetMessage.getId()));
                    matchedResult.setSourceMessages(matchedSourceIds.stream().collect(Collectors.toMap(id -> id, id -> "Source Message " + id)));
                    matchedResult.setTimestamp(new Date());
                    matchedResult.setMatchedAttributes(new HashMap<>(overallMatchedAttributes));
                    matchedResult.setUnmatchedAttributes(new HashMap<>());
                    matchedResult.setMatchStatus("Matched");
                    matchedResult.setMatchDetails(matchDetails.toString());

                    matchingResultsRepository.save(matchedResult);
                }
                updateMessageStatuses(sourceMessageMatchStatus);
                updateMessageStatuses(targetMessageMatchStatus);
            }

            if (!unmatchedSourceIds.isEmpty()) {
                if (existingUnmatchedResult != null) {
                    // Update existing unmatched result
                    existingUnmatchedResult.getSourceMessages().putAll(
                            unmatchedSourceIds.stream().collect(Collectors.toMap(id -> id, id -> "Source Message " + id))
                    );
                    existingUnmatchedResult.getUnmatchedAttributes().putAll(overallUnmatchedAttributes);
                    existingUnmatchedResult.setMatchDetails(existingUnmatchedResult.getMatchDetails() + matchDetails.toString());
                    matchingResultsRepository.save(existingUnmatchedResult);
                } else {
                    // Create a new unmatched result
                    MatchingResult unmatchedResult = new MatchingResult();
                    unmatchedResult.setMatchingConfigurationId(matchingConfiguration.getId());
                    unmatchedResult.setTargetMessages(Map.of(targetMessage.getId(),"targetMessageId"+ targetMessage.getId()));
                    unmatchedResult.setSourceMessages(unmatchedSourceIds.stream().collect(Collectors.toMap(id -> id, id -> "Source Message " + id)));
                    unmatchedResult.setTimestamp(new Date());
                    unmatchedResult.setMatchedAttributes(new HashMap<>());
                    unmatchedResult.setUnmatchedAttributes(new HashMap<>(overallUnmatchedAttributes));
                    unmatchedResult.setMatchStatus("Unmatched");
                    unmatchedResult.setMatchDetails(matchDetails.toString());

                    matchingResultsRepository.save(unmatchedResult);
                }
                updateMessageStatuses(sourceMessageMatchStatus);
                updateMessageStatuses(targetMessageMatchStatus);
            }
        }
    }

    private void updateMessageStatuses(Map<String, Boolean> messageMatchStatus) {
        for (Map.Entry<String, Boolean> entry : messageMatchStatus.entrySet()) {
            String messageId = entry.getKey();
            boolean isMatched = entry.getValue();
            UpdateStatusRequest updateStatusRequest = new UpdateStatusRequest();
            updateStatusRequest.setStatus(isMatched ? "Matched" : "Unmatched");

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

    private boolean isMatch(PendingMessage sourceMessage, PendingMessage targetMessage, String matchingConfigId, Map<String, String> matchedAttributes, Map<String, String> unmatchedAttributes) throws ScriptException {
        List<AttributesToMatch> attributesToMatchList = getAttributesToMatchByMatchingConfigId(matchingConfigId);

        if (attributesToMatchList == null || attributesToMatchList.isEmpty()) {
            System.err.println("Error: No attributes to match found for matching configuration ID: " + matchingConfigId);
            return false;
        }

        boolean overallMatch = true;

        for (AttributesToMatch attributesToMatch : attributesToMatchList) {
            Map<String, String> sourceValues = new HashMap<>();
            Map<String, String> targetValues = new HashMap<>();

            for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : attributesToMatch.getSourceAttributes().entrySet()) {
                String sourceValue = getAttributeValue(sourceMessage.getId(), entry.getValue().getAttributeToAddKey());
                sourceValues.put(entry.getValue().getAttributeToAddKey(), sourceValue);
            }

            for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : attributesToMatch.getTargetAttributes().entrySet()) {
                String targetValue = getAttributeValue(targetMessage.getId(), entry.getValue().getAttributeToAddKey());
                targetValues.put(entry.getValue().getAttributeToAddKey(), targetValue);
            }

            String formula = attributesToMatch.buildFormula(sourceValues, targetValues);
            boolean match = formulaEvaluator.evaluate(formula);

            if (match) {
                matchedAttributes.put(attributesToMatch.getId(), formula);
            } else {
                overallMatch = false;
                unmatchedAttributes.put(attributesToMatch.getId(), formula);
            }
        }

        return overallMatch;
    }

    private List<AttributesToMatch> getAttributesToMatchByMatchingConfigId(String matchingConfigId) {
        return attributesToMatchRepository.findByMatchingConfigurationId(matchingConfigId);
    }

    private String getAttributeValue(String pendingMessageId, String attributeKey) {
        try {
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
                    .query(q -> q.match(m -> m.field("indexConfigurationId").query(configId)))
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
