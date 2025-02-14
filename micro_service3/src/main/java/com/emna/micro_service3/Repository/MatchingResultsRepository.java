package com.emna.micro_service3.Repository;


import com.emna.micro_service3.model.MatchingResult;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchingResultsRepository extends ElasticsearchRepository<MatchingResult, String> {
    List<MatchingResult> findByMatchingConfigurationId(String matchingConfigurationId);

    void deleteByMatchingConfigurationId(String matchingConfigurationId);
    List<MatchingResult> findByMatchingConfigurationIdAndMatchStatus(String matchingConfigurationId, String matchStatus);

    List<MatchingResult> findAll();













}
