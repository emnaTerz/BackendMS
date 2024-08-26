package com.emna.micro_service4.controller;

import com.emna.micro_service4.ReconciliationProcess.ReconciliationProcess;
import com.emna.micro_service4.ReconciliationProcess.ReconciliationProcessFactory;
import com.emna.micro_service4.Repository.ReconciliationConfigurationRepository;
import com.emna.micro_service4.Repository.ReconciliationResultsRepository;
import com.emna.micro_service4.client.MatchingResultsClient;
import com.emna.micro_service4.model.MatchingConfiguration;
import com.emna.micro_service4.model.MatchingResult;
import com.emna.micro_service4.model.ReconciliationConfiguration;
import com.emna.micro_service4.model.ReconciliationResult;
import com.emna.micro_service4.service.ReconciliationConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reconciliation")
public class ReconciliationResultController {

    @Autowired
    private ReconciliationResultsRepository reconciliationResultsRepository;

    @Autowired
    private ReconciliationProcessFactory reconciliationProcessFactory;

    @Autowired
    private ReconciliationConfigurationRepository reconciliationConfigurationRepository;
    @Autowired
    private MatchingResultsClient matchingResultsClient;

    @Autowired
    private ReconciliationConfigurationService reconciliationConfigurationService;
    @PostMapping("/start/{reconciliationConfigId}")
    public ResponseEntity<String> startReconciliation(@PathVariable("reconciliationConfigId") String reconciliationConfigId) {
        // Fetch the ReconciliationConfiguration using the ID
        ReconciliationConfiguration reconciliationConfiguration = reconciliationConfigurationRepository.findById(reconciliationConfigId)
                .orElseThrow(() -> new IllegalArgumentException("Reconciliation Configuration not found for ID: " + reconciliationConfigId));

        // Get the appropriate reconciliation process using the factory
        ReconciliationProcess reconciliationProcess = reconciliationProcessFactory.getReconciliationProcess(reconciliationConfiguration.getMatchingConfigurationId());

        // Execute the reconciliation process
        try {
            reconciliationProcess.process(reconciliationConfiguration);
            return ResponseEntity.ok("Reconciliation process started successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during reconciliation process: " + e.getMessage());
        }
    }
    @GetMapping("/config/{reconciliationConfigId}")
    public List<ReconciliationResult> getReconciliationResultsByConfigId(@PathVariable("reconciliationConfigId") String reconciliationConfigId) {
        return reconciliationResultsRepository.findByReconciliationConfigurationId(reconciliationConfigId);
    }

    @DeleteMapping("/config/{reconciliationConfigId}")
    public ResponseEntity<String> deleteReconciliationResultsByConfigId(@PathVariable("reconciliationConfigId") String reconciliationConfigId) {
        reconciliationResultsRepository.deleteByReconciliationConfigurationId(reconciliationConfigId);
        return ResponseEntity.ok("Reconciliation results deleted successfully.");
    }
    @GetMapping("/matching-configuration/{id}")
    public ResponseEntity<MatchingConfiguration> getMatchingConfigurationById(@PathVariable("id") String id) {
        Optional<MatchingConfiguration> matchingConfiguration = matchingResultsClient.getMatchingConfigurationById(id);
        if (matchingConfiguration.isPresent()) {
            return ResponseEntity.ok(matchingConfiguration.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/matching-results/{id}")
    public ResponseEntity<List<MatchingResult>> getMatchingResultsByConfigId(@PathVariable("id") String id) {
        List<MatchingResult> matchingResults = matchingResultsClient.getMatchingResults(id);
        if (!matchingResults.isEmpty()) {
            return ResponseEntity.ok(matchingResults);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exists/{reconciliationConfigurationId}/{sourceMessageId}/{targetMessageId}")
    public ResponseEntity<Boolean> doesPairExist(
            @PathVariable String reconciliationConfigurationId,
            @PathVariable String sourceMessageId,
            @PathVariable String targetMessageId) {

        boolean exists = reconciliationConfigurationService.doesPairExist(reconciliationConfigurationId, sourceMessageId, targetMessageId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/results/source/{sourceMessageId}")
    public ResponseEntity<List<ReconciliationResult>> getReconciliationResultsBySourceMessageId(
            @PathVariable String sourceMessageId) {

        List<ReconciliationResult> results = reconciliationConfigurationService.getReconciliationResultsBySourceMessageId(sourceMessageId);

        if (results.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if no results found
        } else {
            return ResponseEntity.ok(results); // Return 200 OK with the results
        }
    }
    @GetMapping("/results/target/{targetMessageId}")
    public ResponseEntity<List<ReconciliationResult>> getReconciliationResultsByTargetMessageId(@PathVariable String targetMessageId) {
        List<ReconciliationResult> results = reconciliationConfigurationService.getReconciliationResultsByTargetMessageId(targetMessageId);
        return ResponseEntity.ok(results);
    }
}
