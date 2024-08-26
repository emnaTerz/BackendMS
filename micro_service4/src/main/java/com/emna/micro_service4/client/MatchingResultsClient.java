package com.emna.micro_service4.client;

import com.emna.micro_service4.model.MatchingConfiguration;
import com.emna.micro_service4.model.MatchingResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "micro-service3", url = "http://localhost:8083")
public interface MatchingResultsClient {

    @GetMapping("/api/results/{id}")
    List<MatchingResult> getMatchingResults(@PathVariable("id") String id);

    @GetMapping("/api/{id}")
    Optional<MatchingConfiguration> getMatchingConfigurationById(@PathVariable("id") String id);


    @GetMapping("/api/matched-results/{matchingConfigurationId}/{matchStatus}")
    List<MatchingResult> getMatchingResultsByConfigurationIdAndStatus(
            @PathVariable("matchingConfigurationId") String matchingConfigurationId,
            @PathVariable("matchStatus") String matchStatus);
}
