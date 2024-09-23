package com.emna.micro_service5.controller;

import com.emna.micro_service5.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/total-pending-messages")
    public ResponseEntity<Long> getTotalPendingMessages() {
        long totalMessages = statisticsService.getTotalPendingMessages();
        return ResponseEntity.ok(totalMessages);
    }

    @GetMapping("/count-by-status")
    public ResponseEntity<Map<String, Long>> getCountOfMessagesByStatus() {
        Map<String, Long> countByStatus = statisticsService.getCountOfMessagesByStatus();
        return ResponseEntity.ok(countByStatus);
    }

    @GetMapping("/percentage-by-status")
    public ResponseEntity<Map<String, Double>> getPercentageDistributionOfMessagesByStatus() {
        Map<String, Double> percentageByStatus = statisticsService.getPercentageDistributionOfMessagesByStatus();
        return ResponseEntity.ok(percentageByStatus);
    }

    @GetMapping("/average-time-to-status-change")
    public ResponseEntity<Map<String, Double>> getAverageTimeToStatusChange() {
        Map<String, Double> averageTimeByStatus = statisticsService.getAverageTimeToStatusChange();
        return ResponseEntity.ok(averageTimeByStatus);
    }

    @GetMapping("/rounded-processing-times")
    public Map<String, Integer> getRoundedProcessingTimeForAllIndexConfigs() {
        return statisticsService.getRoundedProcessingTimeForAllIndexConfigs();
    }


}
