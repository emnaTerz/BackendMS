package com.emna.micro_service3.Repository;


import com.emna.micro_service3.model.MatchingResult;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchingResultsRepository extends ElasticsearchRepository<MatchingResult, String> {
    List<MatchingResult> findByMatchingConfigurationId(String matchingConfigurationId);

    void deleteByMatchingConfigurationId(String matchingConfigurationId);
    List<MatchingResult> findByMatchingConfigurationIdAndMatchStatus(String matchingConfigurationId, String matchStatus);
    @Query("{\"bool\": {\"must\": [ {\"term\": {\"sourceMessages.sourceMessageId.keyword\": \"?0\"}} ]}}")
    List<MatchingResult> findBySourceMessageId(String sourceMessageId);
    List<MatchingResult> findAll();

    List<MatchingResult> findBySourceMessagesAndMatchStatus(String sourceMessages, String matchStatus);


    @Query(value = "{\"bool\": {\"must\": [" +
            "{\"term\": {\"matchingConfigurationId\": \"?0\"}}," +
            "{\"nested\": {\"path\": \"sourceMessages\", \"query\": {\"term\": {\"sourceMessages.key\": \"?1\"}}}}," +
            "{\"nested\": {\"path\": \"targetMessages\", \"query\": {\"term\": {\"targetMessages.key\": \"?2\"}}}}" +
            "]}}")
    Long countByMatchingConfigurationIdAndSourceMessageIdAndTargetMessageId(
            String matchingConfigurationId,
            String sourceMessageId,
            String targetMessageId
    );









}
