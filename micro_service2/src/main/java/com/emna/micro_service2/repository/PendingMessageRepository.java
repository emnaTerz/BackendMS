package com.emna.micro_service2.repository;

import com.emna.micro_service2.model.PendingMessage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PendingMessageRepository extends ElasticsearchRepository<PendingMessage, String> {
    List<PendingMessage> findAll();
    List<PendingMessage> findByNameAndSenderAndMessageCategory(String name, String sender, String messageCategory);
    List<PendingMessage> findByIndexConfigurationId(String indexConfigurationId);

}
