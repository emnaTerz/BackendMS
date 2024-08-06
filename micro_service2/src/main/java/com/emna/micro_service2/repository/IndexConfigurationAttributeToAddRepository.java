package com.emna.micro_service2.repository;

import com.emna.micro_service2.model.IndexConfigurationAttributeToAdd;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndexConfigurationAttributeToAddRepository extends ElasticsearchRepository<IndexConfigurationAttributeToAdd, String> {

    List<IndexConfigurationAttributeToAdd> findByIndexConfigurationId(String indexConfigurationId);

    void deleteByAttributeToAddKey(String attributeToAddKey);
    boolean existsByIndexConfigurationId(String indexConfigurationId);

}
