package com.emna.micro_service2.repository;

import com.emna.micro_service2.model.ValueOfAttribute;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValueOfAttributeRepository extends ElasticsearchRepository<ValueOfAttribute, String> {
    List<ValueOfAttribute> findAll();
    List<ValueOfAttribute> findByPendingMessageId(String id);

    void deleteByAttributeKey(String attributeKey);
    List<ValueOfAttribute> findByPendingMessageIdIn(List<String> pendingMessageIds);


}
