package com.emna.micro_service3.service;

import com.emna.micro_service3.Repository.MatchingResultsRepository;
import com.emna.micro_service3.model.MatchingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchingService {



    @Autowired
    private MatchingResultsRepository matchingResultsRepository;

    public List<MatchingResult> getMatchingResults(String matchingConfigurationId) {

        return matchingResultsRepository.findByMatchingConfigurationId(matchingConfigurationId);
    }
    public String deleteMatchingResult(String id) {
        if (matchingResultsRepository.existsById(id)) {
            matchingResultsRepository.deleteById(id);
            return "Matching result deleted successfully.";
        } else {
            return "Matching result not found.";
        }
    }
}