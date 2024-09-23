package com.emna.micro_service3.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.emna.micro_service3.Repository.MatchingResultsRepository;
import com.emna.micro_service3.model.MatchingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchingService {



    @Autowired
    private MatchingResultsRepository matchingResultsRepository;
    private final ElasticsearchClient elasticsearchClient;

    @Autowired
    public MatchingService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public List<MatchingResult> getMatchingResults(String matchingConfigurationId) {

        return matchingResultsRepository.findByMatchingConfigurationId(matchingConfigurationId);
    }


    public boolean doesSourceWithTargetsExist(String matchingConfigurationId, String sourceMessageId, List<String> targetMessageIds) {
        // Log input parameters
        System.out.println("Checking existence of source with multiple targets:");
        System.out.println("matchingConfigurationId: " + matchingConfigurationId);
        System.out.println("sourceMessageId: " + sourceMessageId);
        System.out.println("targetMessageIds: " + targetMessageIds);

        // Step 1: Fetch all matching results for the given matchingConfigurationId
        List<MatchingResult> results = matchingResultsRepository.findByMatchingConfigurationId(matchingConfigurationId);
        System.out.println("Number of matching results found: " + results.size());

        // Step 2: Filter the results in Java
        for (MatchingResult result : results) {
            // Extract the source ID from the keys
            Set<String> sourceIds = result.getSourceMessages().keySet();
            System.out.println("SourceMessageIds in result: " + sourceIds);

            if (sourceIds.contains(sourceMessageId)) {
                System.out.println("Source ID matched. Checking target IDs.");
                // Extract the target IDs from the keys
                Set<String> targetIds = result.getTargetMessages().keySet();

                // Check if any of the target IDs exist in the result
                for (String targetId : targetMessageIds) {
                    if (targetIds.contains(targetId)) {
                        System.out.println("Target ID " + targetId + " matched. Pair exists.");
                        return true; // Pair exists
                    }
                }
            }
        }

        System.out.println("Pair does not exist.");
        return false; // Pair does not exist
    }

    public boolean doesSourceWithTargetExist(String matchingConfigurationId, String sourceMessageId, String targetMessageId) {
        // Log input parameters
        System.out.println("Checking existence in service:");
        System.out.println("matchingConfigurationId: " + matchingConfigurationId);
        System.out.println("sourceMessageId: " + sourceMessageId);
        System.out.println("targetMessageId: " + targetMessageId);

        // Step 1: Fetch all matching results for the given matchingConfigurationId
        List<MatchingResult> results = matchingResultsRepository.findByMatchingConfigurationId(matchingConfigurationId);
        System.out.println("Number of matching results found: " + results.size());

        // Step 2: Filter the results in Java
        for (MatchingResult result : results) {
            // Extracting the source ID from the keys
            Set<String> sourceIds = result.getSourceMessages().keySet();
            System.out.println("SourceMessageIds in result: " + sourceIds);

            if (sourceIds.contains(sourceMessageId)) {
                System.out.println("Source ID matched. Checking target IDs.");
                // Extracting the target IDs from the keys
                Set<String> targetIds = result.getTargetMessages().keySet();
                if (targetIds.contains(targetMessageId)) {
                    System.out.println("Target ID matched. Pair exists.");
                    return true; // Pair exists
                }
            }
        }

        System.out.println("Pair does not exist.");
        return false; // Pair does not exist
    }



    public boolean doesTargetWithSourcesExist(String matchingConfigurationId, String targetMessageId, List<String> sourceMessageIds) {
        // Log input parameters
        System.out.println("Checking existence of target with multiple sources:");
        System.out.println("matchingConfigurationId: " + matchingConfigurationId);
        System.out.println("targetMessageId: " + targetMessageId);
        System.out.println("sourceMessageIds: " + sourceMessageIds);

        // Step 1: Fetch all matching results for the given matchingConfigurationId
        List<MatchingResult> results = matchingResultsRepository.findByMatchingConfigurationId(matchingConfigurationId);
        System.out.println("Number of matching results found: " + results.size());

        // Step 2: Filter the results in Java
        for (MatchingResult result : results) {
            // Extract the target IDs from the keys
            Set<String> targetIds = result.getTargetMessages().keySet();
            System.out.println("TargetMessageIds in result: " + targetIds);

            if (targetIds.contains(targetMessageId)) {
                System.out.println("Target ID matched. Checking source IDs.");
                // Extract the source IDs from the keys
                Set<String> sourceIds = result.getSourceMessages().keySet();

                // Check if any of the source IDs exist in the result
                for (String sourceId : sourceMessageIds) {
                    if (sourceIds.contains(sourceId)) {
                        System.out.println("Source ID " + sourceId + " matched. Pair exists.");
                        return true; // Pair exists
                    }
                }
            }
        }

        System.out.println("Pair does not exist.");
        return false; // Pair does not exist
    }


    public List<MatchingResult> getMatchingResultsBySourceMessageId(String sourceMessageId) {
        System.out.println("Fetching matching results for sourceMessageId: " + sourceMessageId);

        // Retrieve all matching results
        List<MatchingResult> allResults = matchingResultsRepository.findAll();
        System.out.println("Total matching results retrieved: " + allResults.size());

        // Filter results where the sourceMessageId is found in the sourceMessages map
        List<MatchingResult> filteredResults = new ArrayList<>();
        for (MatchingResult result : allResults) {
            // Iterate over sourceMessages map to check for the presence of sourceMessageId
            for (Map.Entry<String, String> entry : result.getSourceMessages().entrySet()) {
                String actualSourceId = entry.getKey();
                String value = entry.getValue();
                System.out.println("Checking sourceMessageId in result: " + actualSourceId + " with value: " + value);

                if (sourceMessageId.equals(actualSourceId) || value.contains(sourceMessageId)) {
                    System.out.println("Match found for sourceMessageId: " + sourceMessageId);
                    filteredResults.add(result);
                    break; // No need to check other entries in this result
                }
            }
        }

        System.out.println("Number of matching results after filtering: " + filteredResults.size());
        return filteredResults;
    }

    public List<MatchingResult> getMatchingResultsByTargetMessageId(String targetMessageId) {
        System.out.println("Fetching matching results for targetMessageId: " + targetMessageId);

        // Retrieve all matching results
        List<MatchingResult> allResults = matchingResultsRepository.findAll();
        System.out.println("Total matching results retrieved: " + allResults.size());

        // Filter results where the targetMessageId is found in the targetMessages map
        List<MatchingResult> filteredResults = new ArrayList<>();
        for (MatchingResult result : allResults) {
            Map<String, String> targetMessages = result.getTargetMessages();
            if (targetMessages != null) {
                // Iterate over targetMessages map to check for the presence of targetMessageId
                for (Map.Entry<String, String> entry : targetMessages.entrySet()) {
                    String actualTargetId = entry.getKey();
                    String value = entry.getValue();
                    System.out.println("Checking targetMessageId in result: " + actualTargetId + " with value: " + value);

                    if (targetMessageId.equals(actualTargetId) || value.contains(targetMessageId)) {
                        System.out.println("Match found for targetMessageId: " + targetMessageId);
                        filteredResults.add(result);
                        break; // No need to check other entries in this result
                    }
                }
            } else {
                System.out.println("Target messages map is null for MatchingResult ID: " + result.getId());
            }
        }

        System.out.println("Number of matching results after filtering: " + filteredResults.size());
        return filteredResults;
    }


    public boolean doesTargetWithSourcesExist(String matchingConfigurationId, List<String> targetMessageIds, List<String> sourceMessageIds) {
        // Retrieve all results for the given matchingConfigurationId
        List<MatchingResult> results = matchingResultsRepository.findByMatchingConfigurationId(matchingConfigurationId);

        // Iterate through each result and check if the source and target IDs match
        for (MatchingResult result : results) {
            Set<String> resultTargetIds = new HashSet<>(Arrays.asList(result.getTargetMessages().getOrDefault("matchedTargetMessages", "").split(",")));
            resultTargetIds.addAll(Arrays.asList(result.getTargetMessages().getOrDefault("unmatchedTargetMessages", "").split(",")));

            Set<String> resultSourceIds = new HashSet<>(Arrays.asList(result.getSourceMessages().getOrDefault("sourceMessageId", "").split(",")));

            // Trim the IDs to avoid issues with spaces
            resultTargetIds = resultTargetIds.stream().map(String::trim).collect(Collectors.toSet());
            resultSourceIds = resultSourceIds.stream().map(String::trim).collect(Collectors.toSet());

            System.out.println("Result Target IDs: " + resultTargetIds);
            System.out.println("Result Source IDs: " + resultSourceIds);
            System.out.println("Provided Target IDs: " + targetMessageIds);
            System.out.println("Provided Source IDs: " + sourceMessageIds);

            // Check if the current result contains all the provided target and source IDs
            if (resultTargetIds.containsAll(targetMessageIds) && resultSourceIds.containsAll(sourceMessageIds)) {
                System.out.println("Match found: Returning true.");
                return true; // Combination already exists
            }
        }
        System.out.println("No match found: Returning false.");
        return false; // Combination does not exist
    }



    public String deleteMatchingResult(String id) {
        if (matchingResultsRepository.existsById(id)) {
            matchingResultsRepository.deleteById(id);
            return "Matching result deleted successfully.";
        } else {
            return "Matching result not found.";
        }
    }
    public List<MatchingResult> getMatchingResultsByConfigurationIdAndStatus(String matchingConfigurationId, String matchStatus) {
        return matchingResultsRepository.findByMatchingConfigurationIdAndMatchStatus(matchingConfigurationId, matchStatus);
    }

    public List<MatchingResult> findMatchingResultsByMessageId(String messageId) {
        List<MatchingResult> allResults = matchingResultsRepository.findAll();

        // Log the size of all results and the incoming messageId
        System.out.println("Total Matching Results Fetched: " + allResults.size());
        System.out.println("Filtering results by messageId: " + messageId);

        return allResults.stream()
                .filter(result ->
                        (result.getSourceMessages() != null && result.getSourceMessages().containsKey(messageId)) ||
                                (result.getTargetMessages() != null && result.getTargetMessages().containsKey(messageId))
                )
                .collect(Collectors.toList());
    }


}