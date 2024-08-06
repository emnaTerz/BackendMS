package com.emna.micro_service3.controller;

import com.emna.micro_service3.MatchingProcess.MatchingProcessFactory;
import com.emna.micro_service3.Repository.MatchingConfigurationRepository;
import com.emna.micro_service3.Repository.MatchingResultsRepository;
import com.emna.micro_service3.model.MatchingConfiguration;
import com.emna.micro_service3.model.MatchingResult;
import com.emna.micro_service3.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/matching")
public class MatchingController {

    @Autowired
    private MatchingProcessFactory matchingProcessFactory;
    @Autowired
    private  MatchingService matchingService;
    @Autowired
    private MatchingConfigurationRepository matchingConfigurationRepository;
    @Autowired
    private MatchingResultsRepository matchingResultsRepository;
    @PostMapping("/start/{id}")
    public String startMatchingProcess(@PathVariable String id) {
        System.out.println("Fetching matching configuration for ID: " + id);
        try {
            MatchingConfiguration matchingConfiguration = matchingConfigurationRepository.findById(id).orElse(null);
            if (matchingConfiguration == null) {
                System.out.println("Matching configuration not found for ID: " + id);
                return "Matching configuration not found.";
            }
            System.out.println("Found matching configuration: " + matchingConfiguration);
            matchingProcessFactory.getMatchingProcess(matchingConfiguration.getMatchingType()).process(matchingConfiguration);
            return "Matching process started successfully.";
        } catch (ScriptException | IOException e) {
            e.printStackTrace();
            return "Error occurred while starting the matching process: " + e.getMessage();
        }
    }
    @GetMapping("/results/{id}")
    public List<MatchingResult> getMatchingResults(@PathVariable String id) {

        return matchingService.getMatchingResults(id);
    }
    @DeleteMapping("/results/{id}")
    public String deleteMatchingResult(@PathVariable String id) {
        return matchingService.deleteMatchingResult(id);
    }
    @DeleteMapping("/delete/{id}")
    public String deleteMatchingResults(@PathVariable String id) {
        try {

            matchingResultsRepository.deleteByMatchingConfigurationId(id);
            return "Matching results deleted successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while deleting matching results: " + e.getMessage();
        }
    }
}
