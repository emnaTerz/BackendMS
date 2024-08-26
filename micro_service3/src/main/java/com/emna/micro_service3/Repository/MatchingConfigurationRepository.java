package com.emna.micro_service3.Repository;

import com.emna.micro_service3.model.MatchingConfiguration;
import com.emna.micro_service3.model.enums.MatchingType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchingConfigurationRepository extends ElasticsearchRepository<MatchingConfiguration, String> {
    List<MatchingConfiguration> findBySourceIdAndTargetIdAndMatchingType(String sourceId, String targetId, MatchingType matchingType);


}