package com.emna.micro_service3.Repository;

import com.emna.micro_service3.model.AttributesToMatch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AttributesToMatchRepository extends ElasticsearchRepository<AttributesToMatch, String> {
    List<AttributesToMatch> findByMatchingConfigurationId(String matchingConfigurationId);


}

