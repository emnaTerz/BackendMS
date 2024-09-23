package com.emna.micro_service5.service;

import com.emna.micro_service5.client.IndexConfigurationInterface;
import com.emna.micro_service5.client.MatchingResultsClient;
import com.emna.micro_service5.client.ReconciliationResultClient;
import com.emna.micro_service5.dto.IndexConfigurationResponse;
import com.emna.micro_service5.dto.ReconciliationConfigurationDTO;
import com.emna.micro_service5.model.MatchingConfiguration;
import com.emna.micro_service5.model.enums.MatchingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MatchingStatisticsService {

    @Autowired
    private MatchingResultsClient matchingResultsClient;
    @Autowired
    private ReconciliationResultClient reconciliationResultClient;

    @Autowired
    private IndexConfigurationInterface indexConfigurationInterface;

    // Total Matching Configurations
    public Map<String, Long> getTotalConfigurations() {
        Map<String, Long> configurationsCount = new HashMap<>();

        // Get the total number of reconciliation configurations
        List<ReconciliationConfigurationDTO> reconciliationConfigurations = reconciliationResultClient.getAllReconciliationConfigurations().getBody();
        long reconciliationConfigCount = reconciliationConfigurations != null ? reconciliationConfigurations.size() : 0;

        // Get the total number of index configurations
        List<IndexConfigurationResponse> indexConfigurations = indexConfigurationInterface.getAllConfigDetails();
        long indexConfigCount = indexConfigurations != null ? indexConfigurations.size() : 0;

        // Get the total number of matching configurations
        List<MatchingConfiguration> matchingConfigurations = matchingResultsClient.getAllMatchingConfigurations();
        long matchingConfigCount = matchingConfigurations != null ? matchingConfigurations.size() : 0;

        // Populate the map with the different configuration counts
        configurationsCount.put("Reconciliation Configurations", reconciliationConfigCount);
        configurationsCount.put("Index Configurations", indexConfigCount);
        configurationsCount.put("Matching Configurations", matchingConfigCount);

        return configurationsCount;
    }

    // Matching Configurations by Type
    public Map<MatchingType, Long> getMatchingConfigurationsByType() {
        List<MatchingConfiguration> configurations = matchingResultsClient.getAllMatchingConfigurations();
        return configurations.stream()
                .collect(Collectors.groupingBy(MatchingConfiguration::getMatchingType, Collectors.counting()));
    }

    // Average Matching Time
    public double getAverageMatchingTime() {
        List<MatchingConfiguration> configurations = matchingResultsClient.getAllMatchingConfigurations();
        return configurations.stream()
                .mapToLong(config -> config.getUpdateDate().getTime() - config.getCreationDate().getTime())
                .average()
                .orElse(0.0);
    }

    // Scheduled Matching Processes (count frequency of schedule entries)
    public Map<String, Long> getScheduledMatchingProcesses() {
        List<MatchingConfiguration> configurations = matchingResultsClient.getAllMatchingConfigurations();
        return configurations.stream()
                .flatMap(config -> config.getScheduleList().stream())
                .collect(Collectors.groupingBy(schedule -> schedule.toString(), Collectors.counting()));
    }






}
