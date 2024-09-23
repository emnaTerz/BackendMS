
package com.emna.micro_service5.service;

import com.emna.micro_service5.client.IndexConfigurationInterface;
import com.emna.micro_service5.client.MatchingResultsClient;
import com.emna.micro_service5.client.ReconciliationResultClient;
import com.emna.micro_service5.dto.IndexConfigurationResponse;
import com.emna.micro_service5.model.MatchingResult;
import com.emna.micro_service5.model.PendingMessage;
import com.emna.micro_service5.model.ReconciliationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private IndexConfigurationInterface indexConfigurationInterface;
    @Autowired
    private MatchingResultsClient matchingResultsClient;

    @Autowired
    private ReconciliationResultClient reconciliationResultsClient;
    public long getTotalPendingMessages() {
        List<PendingMessage> messages = indexConfigurationInterface.findAllPendingMessages();
        return messages.size();
    }

    public Map<String, Long> getCountOfMessagesByStatus() {
        List<PendingMessage> messages = indexConfigurationInterface.findAllPendingMessages();
        return messages.stream()
                .collect(Collectors.groupingBy(PendingMessage::getStatus, Collectors.counting()));
    }

    public Map<String, Double> getPercentageDistributionOfMessagesByStatus() {
        long totalMessages = getTotalPendingMessages();
        Map<String, Long> countByStatus = getCountOfMessagesByStatus();
        return countByStatus.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (entry.getValue() * 100.0) / totalMessages
                ));
    }

    public Map<String, Double> getAverageTimeToStatusChange() {
        List<PendingMessage> messages = indexConfigurationInterface.findAllPendingMessages();
        Map<String, List<Long>> timeDurations = new HashMap<>();

        for (PendingMessage message : messages) {
            List<MatchingResult> matchingResults = matchingResultsClient.getMatchingResultsByMessageId(message.getId());
            List<ReconciliationResult> reconciliationResults = reconciliationResultsClient.getReconciliationResultsByMessageId(message.getId());

            // Example: Calculate time from 'unused' to 'matched'
            Long timeToMatch = calculateTimeTakenForStatusChange(message, matchingResults, reconciliationResults, "unused", "matched");
            if (timeToMatch != null) {
                timeDurations.computeIfAbsent("unused_to_matched", k -> new ArrayList<>()).add(timeToMatch);
            }

            // Add other status transitions similarly, e.g., from 'matched' to 'reconciled', etc.
        }

        Map<String, Double> averageTimeByStatus = new HashMap<>();
        for (Map.Entry<String, List<Long>> entry : timeDurations.entrySet()) {
            List<Long> durations = entry.getValue();
            averageTimeByStatus.put(entry.getKey(), durations.stream().mapToLong(val -> val).average().orElse(0.0));
        }

        return averageTimeByStatus;
    }


    public Map<String, Integer> getRoundedProcessingTimeForAllIndexConfigs() {
        Map<String, Integer> roundedProcessingTimes = new HashMap<>();

        try {
            List<IndexConfigurationResponse> configs = indexConfigurationInterface.getAllConfigDetails();
            for (IndexConfigurationResponse config : configs) {
                String configName = config.getName(); // Assuming `getName()` returns the config name
                List<PendingMessage> messages = indexConfigurationInterface.getMessagesByConfigId(config.getId());
                long totalProcessingTime = 0;
                int count = 0;

                for (PendingMessage message : messages) {
                    List<MatchingResult> matchingResults = matchingResultsClient.getMatchingResultsByMessageId(message.getId());
                    List<ReconciliationResult> reconciliationResults = reconciliationResultsClient.getReconciliationResultsByMessageId(message.getId());

                    if (!matchingResults.isEmpty()) {
                        Date matchTime = matchingResults.get(0).getTimestamp();
                        long processingTime = matchTime.getTime() - message.getCreationDate().getTime();
                        totalProcessingTime += processingTime;
                        count++;
                    } else if (!reconciliationResults.isEmpty()) {
                        Date reconciliationTime = reconciliationResults.get(0).getTimestamp();
                        long processingTime = reconciliationTime.getTime() - message.getCreationDate().getTime();
                        totalProcessingTime += processingTime;
                        count++;
                    }
                }

                // Calculate the average processing time in days and round it
                if (count > 0) {
                    double averageProcessingTimeInDays = (double) totalProcessingTime / (count * 86400000.0);
                    int roundedProcessingTimeInDays = (int) Math.round(averageProcessingTimeInDays);
                    roundedProcessingTimes.put(configName, roundedProcessingTimeInDays);
                } else {
                    roundedProcessingTimes.put(configName, 0);
                }
            }
        } catch (Exception e) {
            System.err.println("Error calculating processing times: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return roundedProcessingTimes;
    }

    public Long calculateTimeTakenForStatusChange(PendingMessage message, List<MatchingResult> matchingResults, List<ReconciliationResult> reconciliationResults, String fromStatus, String toStatus) {
        Date startTime = null;
        Date endTime = null;

        // Determine the start time based on the 'fromStatus'
        if ("unused".equalsIgnoreCase(fromStatus)) {
            startTime = message.getCreationDate();
        }

        // Find the first occurrence of the 'toStatus' in MatchingResults
        for (MatchingResult result : matchingResults) {
            if (toStatus.equalsIgnoreCase(result.getMatchStatus())) {
                endTime = result.getTimestamp();
                break; // Assuming we only care about the first match
            }
        }

        // If not found in MatchingResults, check ReconciliationResults
        if (endTime == null) {
            for (ReconciliationResult result : reconciliationResults) {
                if (toStatus.equalsIgnoreCase(result.getReconciliationStatus())) {
                    endTime = result.getTimestamp();
                    break; // Assuming we only care about the first match
                }
            }
        }

        // Calculate the difference between end time and start time
        if (startTime != null && endTime != null) {
            return (endTime.getTime() - startTime.getTime()) / (1000 * 60 * 60 * 24); // return difference in days
        }

        // If no matching timestamp found, return null or a default value
        return null;
    }


}
