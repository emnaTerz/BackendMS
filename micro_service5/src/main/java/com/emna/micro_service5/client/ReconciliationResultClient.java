package com.emna.micro_service5.client;

import com.emna.micro_service5.dto.ReconciliationConfigurationDTO;
import com.emna.micro_service5.model.MatchingConfiguration;
import com.emna.micro_service5.model.MatchingResult;
import com.emna.micro_service5.model.ReconciliationResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "micro-service4", url = "http://localhost:8084")

public interface ReconciliationResultClient {


    @GetMapping("/reconciliation/{id}")
    ResponseEntity<ReconciliationConfigurationDTO> getReconciliationConfiguration(@PathVariable("id") String id);



    @GetMapping("/reconciliation/all")
    ResponseEntity<List<ReconciliationConfigurationDTO>> getAllReconciliationConfigurations();

    // ReconciliationResultController APIs

    @PostMapping("/reconciliation/start/{reconciliationConfigId}")
    ResponseEntity<String> startReconciliation(@PathVariable("reconciliationConfigId") String reconciliationConfigId);

    @GetMapping("/reconciliation/config/{reconciliationConfigId}")
    List<ReconciliationResult> getReconciliationResultsByConfigId(@PathVariable("reconciliationConfigId") String reconciliationConfigId);


    @GetMapping("/reconciliation/matching-configuration/{id}")
    ResponseEntity<MatchingConfiguration> getMatchingConfigurationById(@PathVariable("id") String id);

    @GetMapping("/reconciliation/matching-results/{id}")
    ResponseEntity<List<MatchingResult>> getMatchingResultsByConfigId(@PathVariable("id") String id);

    @GetMapping("/reconciliation/message-id/{messageId}")
    List<ReconciliationResult> getReconciliationResultsByMessageId(@PathVariable("messageId") String messageId);
}


