package com.emna.micro_service4.Repository;

import com.emna.micro_service4.model.AttributesToReconciliation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttributesToReconciliationRepository extends ElasticsearchRepository<AttributesToReconciliation, String> {
    List<AttributesToReconciliation> findByReconciliationConfigurationId(String reconciliationConfigurationId);


}
