package com.emna.micro_service4.Repository;

import com.emna.micro_service4.model.ReconciliationResult;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReconciliationResultsRepository extends ElasticsearchRepository<ReconciliationResult, String> {

    List<ReconciliationResult> findByReconciliationConfigurationId(String reconciliationConfigurationId);

    List<ReconciliationResult> findAll();

    void deleteByReconciliationConfigurationId(String reconciliationConfigId);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"sourceMessages.sourceMessageId\": \"?0\"}}]}}")
    List<ReconciliationResult> findBySourceMessagesSourceMessageId(String sourceMessageId);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"targetMessages.targetMessageId\": \"?0\"}}]}}")
    List<ReconciliationResult> findByTargetMessagesTargetMessageId(String targetMessageId);

}
