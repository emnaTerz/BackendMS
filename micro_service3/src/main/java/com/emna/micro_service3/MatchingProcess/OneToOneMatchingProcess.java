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
public class OneToOneMatchingProcess implements MatchingProcess {

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

/*
  @Override
  public void process(MatchingConfiguration matchingConfiguration) throws ScriptException, IOException {
      List<PendingMessage> sourceMessages = getMessagesByConfigId(matchingConfiguration.getSourceId());
      List<PendingMessage> targetMessages = getMessagesByConfigId(matchingConfiguration.getTargetId());

      Set<String> processedSourceTargetPairs = new HashSet<>();
      Map<String, Boolean> sourceMessageMatchStatus = new HashMap<>();
      Map<String, Boolean> targetMessageMatchStatus = new HashMap<>();

      for (PendingMessage sourceMessage : sourceMessages) {
          sourceMessageMatchStatus.put(sourceMessage.getId(), false);
      }

      for (PendingMessage targetMessage : targetMessages) {
          targetMessageMatchStatus.put(targetMessage.getId(), false);
      }

      for (PendingMessage sourceMessage : sourceMessages) {
          for (PendingMessage targetMessage : targetMessages) {
              String pairId = sourceMessage.getId() + "-" + targetMessage.getId();

              // Check if the matching result already exists for this pair
              if (matchingService.existsMatchingResult(matchingConfiguration.getId(), sourceMessage.getId(), targetMessage.getId())) {
                  System.out.println("Pair already processed: " + pairId);
                  processedSourceTargetPairs.add(pairId); // Mark this source-target pair as processed
                  continue; // Skip the pair as it is already processed
              }

              // Proceed with matching if the pair has not been processed
              Map<String, String> matchedAttributes = new HashMap<>();
              Map<String, String> unmatchedAttributes = new HashMap<>();

              boolean match = isMatch(sourceMessage, targetMessage, matchingConfiguration.getId(), matchedAttributes, unmatchedAttributes);

              MatchingResult matchingResult = new MatchingResult();
              matchingResult.setMatchingConfigurationId(matchingConfiguration.getId());
              matchingResult.setSourceMessages(Map.of("sourceMessageId", sourceMessage.getId()));
              matchingResult.setTargetMessages(Map.of("targetMessageId", targetMessage.getId()));
              matchingResult.setTimestamp(new Date());

              if (match) {
                  matchingResult.setMatchStatus("Matched");
                  matchingResult.setMatchDetails("Source message ID: " + sourceMessage.getId() + ", Target message ID: " + targetMessage.getId() + " did match.");
                  matchingResult.setMatchedAttributes(new HashMap<>(matchedAttributes));
                  matchingResult.setUnmatchedAttributes(new HashMap<>());
                  sourceMessageMatchStatus.put(sourceMessage.getId(), true);
                  targetMessageMatchStatus.put(targetMessage.getId(), true);
              } else {
                  matchingResult.setMatchStatus("Unmatched");
                  matchingResult.setMatchDetails("Source message ID: " + sourceMessage.getId() + ", Target message ID: " + targetMessage.getId() + " did not match.");
                  matchingResult.setMatchedAttributes(new HashMap<>(matchedAttributes));
                  matchingResult.setUnmatchedAttributes(new HashMap<>(unmatchedAttributes));
              }

              System.out.println("Matched attributes: " + matchedAttributes);
              System.out.println("Unmatched attributes: " + unmatchedAttributes);

              // Save the matching result
              matchingResultsRepository.save(matchingResult);


              updateMessageStatuses(sourceMessageMatchStatus);
              updateMessageStatuses(targetMessageMatchStatus);
              processedSourceTargetPairs.add(pairId); // Mark this source-target pair as processed
          }
      }

      // Update the status of each source and target message

  }*/

    @Override
    public void process(MatchingConfiguration matchingConfiguration) throws ScriptException, IOException {
        List<PendingMessage> sourceMessages = getMessagesByConfigId(matchingConfiguration.getSourceId());
        List<PendingMessage> targetMessages = getMessagesByConfigId(matchingConfiguration.getTargetId());

        Map<String, Boolean> sourceMessageMatchStatus = new HashMap<>();
        Map<String, Boolean> targetMessageMatchStatus = new HashMap<>();

        for (PendingMessage sourceMessage : sourceMessages) {
            sourceMessageMatchStatus.put(sourceMessage.getId(), false);

            for (PendingMessage targetMessage : targetMessages) {
                targetMessageMatchStatus.put(targetMessage.getId(), false);

                // Check if this source-target pair has already been processed
                if (matchingService.doesSourceWithTargetExist(matchingConfiguration.getId(), sourceMessage.getId(), targetMessage.getId())) {
                    System.out.println("Source ID " + sourceMessage.getId() + " has already been processed with target ID " + targetMessage.getId());
                    continue; // Skip as this combination of source and target IDs is already processed
                }

                Map<String, String> matchedAttributes = new HashMap<>();
                Map<String, String> unmatchedAttributes = new HashMap<>();

                boolean match = isMatch(sourceMessage, targetMessage, matchingConfiguration.getId(), matchedAttributes, unmatchedAttributes);

                MatchingResult matchingResult = new MatchingResult();
                matchingResult.setMatchingConfigurationId(matchingConfiguration.getId());
                matchingResult.setSourceMessages(Map.of(sourceMessage.getId(),"sourceMessageId" + sourceMessage.getId()));
                matchingResult.setTargetMessages(Map.of(targetMessage.getId(), "Target Message " + targetMessage.getId()));
                matchingResult.setTimestamp(new Date());
                matchingResult.setMatchedAttributes(new HashMap<>(matchedAttributes));
                matchingResult.setUnmatchedAttributes(new HashMap<>(unmatchedAttributes));
                matchingResult.setMatchStatus(matchedAttributes.isEmpty() ? "Unmatched" : "Matched");

                StringBuilder matchDetails = new StringBuilder();
                if (match) {
                    matchDetails.append("Source message ID: ").append(sourceMessage.getId())
                            .append(", Target message ID: ").append(targetMessage.getId())
                            .append(" did match.\n");
                    sourceMessageMatchStatus.put(sourceMessage.getId(), true);
                    targetMessageMatchStatus.put(targetMessage.getId(), true);
                } else {
                    matchDetails.append("Source message ID: ").append(sourceMessage.getId())
                            .append(", Target message ID: ").append(targetMessage.getId())
                            .append(" did not match.\n");
                }
                matchingResult.setMatchDetails(matchDetails.toString());

                System.out.println("Overall matched attributes for source message ID " + sourceMessage.getId() + ": " + matchedAttributes);
                System.out.println("Overall unmatched attributes for source message ID " + sourceMessage.getId() + ": " + unmatchedAttributes);

                matchingResultsRepository.save(matchingResult);
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

        // Highlighted error message
        if (attributesToMatchList == null || attributesToMatchList.isEmpty()) {
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

            // Evaluate the formula
            String formula = attributesToMatch.buildFormula(sourceValues, targetValues);
            System.out.println("Final formula: " + formula);

            boolean match = formulaEvaluator.evaluate(formula);
            System.out.println("Match result: " + match);

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
