/*
package com.emna.micro_service3.client;

import com.emna.micro_service2.dto.UpdateStatusRequest;
import com.emna.micro_service3.model.IndexConfiguration;
import com.emna.micro_service3.model.IndexConfigurationAttributeToAdd;
import com.emna.micro_service3.model.PendingMessage;
import com.emna.micro_service3.model.ValueOfAttribute;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "micro-service2" , url = "http://localhost:8082")
public interface IndexConfigurationInterface {

    @GetMapping("/api/config/all-config-details")
    List<IndexConfiguration> findAllIndexConfigurations();

    @GetMapping("/api/config/{configurationId}/attributes")
    List<IndexConfigurationAttributeToAdd> getAttributesByConfigurationId(@PathVariable("configurationId") String configurationId);

    @GetMapping("/messages/{id}")
    PendingMessage getPendingMessageById(@PathVariable("id") String id);

    @GetMapping("/messages/attributes/{pendingMessageId}")
    List<ValueOfAttribute> findByPendingMessageId(@PathVariable("pendingMessageId") String pendingMessageId);

    @GetMapping("/messages")
    List<PendingMessage> findAllPendingMessage();
    @GetMapping("/messages/{configurationId}/messages")
    List<PendingMessage> getMessagesByConfigId(@PathVariable("configurationId") String configurationId);
    @PutMapping("/messages/update-status/{id}")
    ResponseEntity<String> updateStatusById(@PathVariable("id") String id, @RequestBody UpdateStatusRequest request);
}*/

package com.emna.micro_service3.client;

import com.emna.micro_service3.dto.UpdateStatusRequest;
import com.emna.micro_service3.model.IndexConfiguration;
import com.emna.micro_service3.model.IndexConfigurationAttributeToAdd;
import com.emna.micro_service3.model.PendingMessage;
import com.emna.micro_service3.model.ValueOfAttribute;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "micro-service2", url = "http://localhost:8082")
public interface IndexConfigurationInterface {

    @GetMapping("/api/config/all-config-details")
    List<IndexConfiguration> findAllIndexConfigurations();

    @GetMapping("/api/config/{configurationId}/attributes")
    List<IndexConfigurationAttributeToAdd> getAttributesByConfigurationId(@PathVariable("configurationId") String configurationId);

    @GetMapping("/api/config/messages/{id}")
    PendingMessage getPendingMessageById(@PathVariable("id") String id);

    @GetMapping("/api/config/attributes/{pendingMessageId}")
    List<ValueOfAttribute> findByPendingMessageId(@PathVariable("pendingMessageId") String pendingMessageId);

    @GetMapping("/api/config/messages")
    List<PendingMessage> findAllPendingMessages();

    @GetMapping("/api/config/{configurationId}/messages")
    List<PendingMessage> getMessagesByConfigId(@PathVariable("configurationId") String configurationId);

    @PutMapping("/api/config/update-status/{id}")
    ResponseEntity<String> updateStatusById(@PathVariable("id") String id, @RequestBody UpdateStatusRequest request);
}
