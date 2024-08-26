package com.emna.micro_service4.Repository;

import com.emna.micro_service4.model.ReconciliationConfiguration;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReconciliationConfigurationRepository extends ElasticsearchRepository<ReconciliationConfiguration, String> {
    Optional<ReconciliationConfiguration> findByMatchingConfigurationId(String matchingConfigurationId);
}
