package com.emna.micro_service5.client;

import com.emna.micro_service5.dto.IndexConfigurationResponse;
import com.emna.micro_service5.model.IndexConfigurationAttributeToAdd;
import com.emna.micro_service5.model.PendingMessage;
import com.emna.micro_service5.model.ValueOfAttribute;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "micro-service2", url = "http://localhost:8082")
public interface IndexConfigurationInterface {


    @GetMapping("/api/config/all-config-details")
    List<IndexConfigurationResponse> getAllConfigDetails();
    @GetMapping("/api/config")
    List<PendingMessage> findAllPendingMessages(); // Adjusted to match the existing endpoint

    @GetMapping("/api/config/{id}")
    PendingMessage getPendingMessageById(@PathVariable("id") String id);

    @GetMapping("/api/config/attributes/{pendingMessageId}")
    List<ValueOfAttribute> findByPendingMessageId(@PathVariable("pendingMessageId") String pendingMessageId);

    @GetMapping("/api/config/{configurationId}/messages")
    List<PendingMessage> getMessagesByConfigId(@PathVariable("configurationId") String configurationId);

    @GetMapping("/api/config/{configurationId}/attributes")
    List<IndexConfigurationAttributeToAdd> getAttributesByConfigurationId(@PathVariable("configurationId") String configurationId);

}
