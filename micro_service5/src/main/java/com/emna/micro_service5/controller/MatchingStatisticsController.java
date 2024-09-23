package com.emna.micro_service5.controller;

import com.emna.micro_service5.model.enums.MatchingType;
import com.emna.micro_service5.service.MatchingStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/statistics/matching")
public class MatchingStatisticsController {

    @Autowired
    private MatchingStatisticsService matchingStatisticsService;

    @GetMapping("/total-configurations")
    public ResponseEntity<Map<String, Long>> getTotalConfigurations() {
        Map<String, Long> totalConfigurations = matchingStatisticsService.getTotalConfigurations();
        return ResponseEntity.ok(totalConfigurations);
    }

    @GetMapping("/configurations-by-type")
    public ResponseEntity<Map<MatchingType, Long>> getMatchingConfigurationsByType() {
        return ResponseEntity.ok(matchingStatisticsService.getMatchingConfigurationsByType());
    }

    @GetMapping("/average-matching-time")
    public ResponseEntity<Double> getAverageMatchingTime() {
        return ResponseEntity.ok(matchingStatisticsService.getAverageMatchingTime());
    }

    @GetMapping("/scheduled-processes")
    public ResponseEntity<Map<String, Long>> getScheduledMatchingProcesses() {
        return ResponseEntity.ok(matchingStatisticsService.getScheduledMatchingProcesses());
    }


}
