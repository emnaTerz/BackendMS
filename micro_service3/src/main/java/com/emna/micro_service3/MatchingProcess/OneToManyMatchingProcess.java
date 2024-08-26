
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
public class OneToManyMatchingProcess implements MatchingProcess {

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



  /*  @Override
    public void process(MatchingConfiguration matchingConfiguration) throws ScriptException, IOException {
        List<PendingMessage> sourceMessages = getMessagesByConfigId(matchingConfiguration.getSourceId());
        List<PendingMessage> targetMessages = getMessagesByConfigId(matchingConfiguration.getTargetId());

        Map<String, Boolean> sourceMessageMatchStatus = new HashMap<>();
        Map<String, Boolean> targetMessageMatchStatus = new HashMap<>();

        for (PendingMessage sourceMessage : sourceMessages) {
            sourceMessageMatchStatus.put(sourceMessage.getId(), false);

            // Fetch existing matching results for the source message
            List<MatchingResult> existingResults = matchingResultsRepository.findBySourceMessageId(sourceMessage.getId());

            MatchingResult existingMatchedResult = null;
            MatchingResult existingUnmatchedResult = null;

            // Check if there's an existing result for this source message with the same status
            for (MatchingResult existingResult : existingResults) {
                if ("Matched".equals(existingResult.getMatchStatus())) {
                    existingMatchedResult = existingResult;
                } else if ("Unmatched".equals(existingResult.getMatchStatus())) {
                    existingUnmatchedResult = existingResult;
                }
             else if ("Reconciled".equals(existingResult.getMatchStatus())) {
                existingUnmatchedResult = existingResult;
            }
                else if ("Not Reconciled".equals(existingResult.getMatchStatus())) {
                    existingUnmatchedResult = existingResult;
                }
                else if ("Partially Reconciled".equals(existingResult.getMatchStatus())) {
                    existingUnmatchedResult = existingResult;
                }
            }

            List<String> matchedTargetIds = new ArrayList<>();
            List<String> unmatchedTargetIds = new ArrayList<>();
            Map<String, String> overallMatchedAttributes = new HashMap<>();
            Map<String, String> overallUnmatchedAttributes = new HashMap<>();
            StringBuilder matchDetails = new StringBuilder();

            for (PendingMessage targetMessage : targetMessages) {
                List<String> singleTargetIdList = Collections.singletonList(targetMessage.getId());

                boolean exists = matchingService.doesSourceWithTargetsExist(matchingConfiguration.getId(), sourceMessage.getId(), singleTargetIdList);

                if (exists) {
                    System.out.println("Source ID " + sourceMessage.getId() + " has already been processed with target ID " + targetMessage.getId());
                    continue;
                }

                Map<String, String> matchedAttributes = new HashMap<>();
                Map<String, String> unmatchedAttributes = new HashMap<>();

                boolean match = isMatch(sourceMessage, targetMessage, matchingConfiguration.getId(), matchedAttributes, unmatchedAttributes);

                if (match) {
                    matchedTargetIds.add(targetMessage.getId());
                    overallMatchedAttributes.putAll(matchedAttributes);
                    matchDetails.append("Source message ID: ").append(sourceMessage.getId())
                            .append(", Target message ID: ").append(targetMessage.getId())
                            .append(" did match.\n");
                    sourceMessageMatchStatus.put(sourceMessage.getId(), true);
                    targetMessageMatchStatus.put(targetMessage.getId(), true);
                } else {
                    unmatchedTargetIds.add(targetMessage.getId());
                    overallUnmatchedAttributes.putAll(unmatchedAttributes);
                    matchDetails.append("Source message ID: ").append(sourceMessage.getId())
                            .append(", Target message ID: ").append(targetMessage.getId())
                            .append(" did not match.\n");

                    unmatchedAttributes.putAll(matchedAttributes);
                }
            }

            if (!matchedTargetIds.isEmpty()) {
                if (existingMatchedResult != null) {
                    // Update existing matched result
                    existingMatchedResult.getTargetMessages().putAll(matchedTargetIds.stream().collect(Collectors.toMap(id -> id, id -> "Target Message " + id)));
                    existingMatchedResult.getMatchedAttributes().putAll(overallMatchedAttributes);
                    existingMatchedResult.setMatchDetails(existingMatchedResult.getMatchDetails() + matchDetails.toString());
                    matchingResultsRepository.save(existingMatchedResult);
                } else {
                    // Create a new matched result
                    MatchingResult matchedResult = new MatchingResult();
                    matchedResult.setMatchingConfigurationId(matchingConfiguration.getId());
                    matchedResult.setSourceMessages(Map.of(sourceMessage.getId(),"sourceMessageId"+ sourceMessage.getId()));
                    matchedResult.setTargetMessages(matchedTargetIds.stream().collect(Collectors.toMap(id -> id, id -> "Target Message " + id)));
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

            if (!unmatchedTargetIds.isEmpty()) {
                if (existingUnmatchedResult != null) {
                    // Update existing unmatched result
                    existingUnmatchedResult.getTargetMessages().putAll(unmatchedTargetIds.stream().collect(Collectors.toMap(id -> id, id -> "Target Message " + id)));
                    existingUnmatchedResult.getUnmatchedAttributes().putAll(overallUnmatchedAttributes);
                    existingUnmatchedResult.setMatchDetails(existingUnmatchedResult.getMatchDetails() + matchDetails.toString());
                    matchingResultsRepository.save(existingUnmatchedResult);
                } else {
                    // Create a new unmatched result
                    MatchingResult unmatchedResult = new MatchingResult();
                    unmatchedResult.setMatchingConfigurationId(matchingConfiguration.getId());
                    unmatchedResult.setSourceMessages(Map.of(sourceMessage.getId(),"sourceMessageId"+ sourceMessage.getId()));
                    unmatchedResult.setTargetMessages(unmatchedTargetIds.stream().collect(Collectors.toMap(id -> id, id -> "Target Message " + id)));
                    unmatchedResult.setTimestamp(new Date());
                    unmatchedResult.setMatchedAttributes(new HashMap<>()); // Include only the matched attributes for those targets
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
*/
  @Override
  public void process(MatchingConfiguration matchingConfiguration) throws ScriptException, IOException {
      List<PendingMessage> sourceMessages = getMessagesByConfigId(matchingConfiguration.getSourceId());
      List<PendingMessage> targetMessages = getMessagesByConfigId(matchingConfiguration.getTargetId());

      Map<String, Boolean> sourceMessageMatchStatus = new HashMap<>();
      Map<String, Boolean> targetMessageMatchStatus = new HashMap<>();

      for (PendingMessage sourceMessage : sourceMessages) {
          sourceMessageMatchStatus.put(sourceMessage.getId(), false);

          // Fetch existing matching results for the source message
          List<MatchingResult> existingResults = matchingService.getMatchingResultsBySourceMessageId(sourceMessage.getId());

          MatchingResult existingMatchedResult = null;
          MatchingResult existingUnmatchedResult = null;

          // Check if there's an existing result for this source message with the same status
          for (MatchingResult existingResult : existingResults) {
              if ("Matched".equals(existingResult.getMatchStatus())) {
                  existingMatchedResult = existingResult;
              } else if ("Reconciled".equals(existingResult.getMatchStatus())) {
                  existingMatchedResult = existingResult;

              } else if ("Unmatched".equals(existingResult.getMatchStatus())) {
                  existingUnmatchedResult = existingResult;
              }
              else if ("Not Reconciled".equals(existingResult.getMatchStatus())) {
                  existingMatchedResult = existingResult;
              }
              else if ("Partially Reconciled".equals(existingResult.getMatchStatus())) {
                  existingMatchedResult = existingResult;
              }
          }

          List<String> matchedTargetIds = new ArrayList<>();
          List<String> unmatchedTargetIds = new ArrayList<>();
          Map<String, String> overallMatchedAttributes = new HashMap<>();
          Map<String, String> overallUnmatchedAttributes = new HashMap<>();
          StringBuilder matchDetails = new StringBuilder();

          for (PendingMessage targetMessage : targetMessages) {
              List<String> singleTargetIdList = Collections.singletonList(targetMessage.getId());

              boolean exists = matchingService.doesSourceWithTargetsExist(matchingConfiguration.getId(), sourceMessage.getId(), singleTargetIdList);

              if (exists) {
                  System.out.println("Source ID " + sourceMessage.getId() + " has already been processed with target ID " + targetMessage.getId());
                  continue;
              }

              Map<String, String> matchedAttributes = new HashMap<>();
              Map<String, String> unmatchedAttributes = new HashMap<>();

              boolean match = isMatch(sourceMessage, targetMessage, matchingConfiguration.getId(), matchedAttributes, unmatchedAttributes);

              if (match) {
                  matchedTargetIds.add(targetMessage.getId());
                  overallMatchedAttributes.putAll(matchedAttributes);
                  matchDetails.append("Source message ID: ").append(sourceMessage.getId())
                          .append(", Target message ID: ").append(targetMessage.getId())
                          .append(" did match.\n");
                  sourceMessageMatchStatus.put(sourceMessage.getId(), true);
                  targetMessageMatchStatus.put(targetMessage.getId(), true);
              } else {
                  unmatchedTargetIds.add(targetMessage.getId());
                  overallUnmatchedAttributes.putAll(unmatchedAttributes);
                  matchDetails.append("Source message ID: ").append(sourceMessage.getId())
                          .append(", Target message ID: ").append(targetMessage.getId())
                          .append(" did not match.\n");

                  unmatchedAttributes.putAll(matchedAttributes);
              }
          }

          if (!matchedTargetIds.isEmpty()) {
              if (existingMatchedResult != null) {
                  // Update existing matched result by merging with new target messages
                  Map<String, String> updatedTargetMessages = existingMatchedResult.getTargetMessages();
                  matchedTargetIds.forEach(id -> updatedTargetMessages.put(id, "Target Message " + id));
                  existingMatchedResult.setTargetMessages(updatedTargetMessages);

                  // Merge matched attributes and update details
                  existingMatchedResult.getMatchedAttributes().putAll(overallMatchedAttributes);
                  existingMatchedResult.setMatchDetails(existingMatchedResult.getMatchDetails() + matchDetails.toString());

                  matchingResultsRepository.save(existingMatchedResult);
              } else {
                  // Create a new matched result
                  MatchingResult matchedResult = new MatchingResult();
                  matchedResult.setMatchingConfigurationId(matchingConfiguration.getId());
                  matchedResult.setSourceMessages(Map.of(sourceMessage.getId(),"sourceMessageId"+ sourceMessage.getId()));
                  matchedResult.setTargetMessages(matchedTargetIds.stream().collect(Collectors.toMap(id -> id, id -> "Target Message " + id)));
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

          if (!unmatchedTargetIds.isEmpty()) {
              if (existingUnmatchedResult != null) {
                  // Update existing unmatched result by merging with new target messages
                  Map<String, String> updatedTargetMessages = existingUnmatchedResult.getTargetMessages();
                  unmatchedTargetIds.forEach(id -> updatedTargetMessages.put(id, "Target Message " + id));
                  existingUnmatchedResult.setTargetMessages(updatedTargetMessages);

                  // Merge unmatched attributes and update details
                  existingUnmatchedResult.getUnmatchedAttributes().putAll(overallUnmatchedAttributes);
                  existingUnmatchedResult.setMatchDetails(existingUnmatchedResult.getMatchDetails() + matchDetails.toString());

                  matchingResultsRepository.save(existingUnmatchedResult);
              } else {
                  // Create a new unmatched result
                  MatchingResult unmatchedResult = new MatchingResult();
                  unmatchedResult.setMatchingConfigurationId(matchingConfiguration.getId());
                  unmatchedResult.setSourceMessages(Map.of(sourceMessage.getId(),"sourceMessageId"+ sourceMessage.getId()));
                  unmatchedResult.setTargetMessages(unmatchedTargetIds.stream().collect(Collectors.toMap(id -> id, id -> "Target Message " + id)));
                  unmatchedResult.setTimestamp(new Date());
                  unmatchedResult.setMatchedAttributes(new HashMap<>()); // Include only the matched attributes for those targets
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
            // Highlighted error message
            System.err.println("Error: No attributes to match found for matching configuration ID: " + matchingConfigId);
            return false; // or handle the error as per your requirement
        }

        boolean overallMatch = true;

        for (AttributesToMatch attributesToMatch : attributesToMatchList) {
            Map<String, String> sourceValues = new HashMap<>();
            Map<String, String> targetValues = new HashMap<>();

            // Get source values
            for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : attributesToMatch.getSourceAttributes().entrySet()) {
                String sourceValue = getAttributeValue(sourceMessage.getId(), entry.getValue().getAttributeToAddKey());
                sourceValues.put(entry.getValue().getAttributeToAddKey(), sourceValue);
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

        return overallMatch;
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

