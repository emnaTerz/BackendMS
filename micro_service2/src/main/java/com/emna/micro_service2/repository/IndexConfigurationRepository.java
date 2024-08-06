package com.emna.micro_service2.repository;

import com.emna.micro_service2.model.IndexConfiguration;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndexConfigurationRepository extends ElasticsearchRepository<IndexConfiguration, String> {

    Optional<IndexConfiguration> findByNameAndSenderAndMessageCategory(String name, String sender, String messageCategory);

    Optional<IndexConfiguration> findBySenderAndMessageCategory(String sender, String messageCategory);

    List<IndexConfiguration> findAll();
}
