/*package com.emna.micro_service3.controller;

import com.emna.micro_service3.client.IndexConfigurationInterface;
import com.emna.micro_service3.model.IndexConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class IndexConfigurationController {

    @Autowired
    private IndexConfigurationInterface indexConfigurationInterface;

    @GetMapping("/config")
    public List<IndexConfiguration> getAllIndexConfigurations() {
        return indexConfigurationInterface.findAllIndexConfigurations();
    }
}*/
package com.emna.micro_service3.controller;


import com.emna.micro_service3.client.IndexConfigurationInterface;
import com.emna.micro_service3.dto.UpdateStatusRequest;
import com.emna.micro_service3.model.IndexConfiguration;
import com.emna.micro_service3.model.IndexConfigurationAttributeToAdd;
import com.emna.micro_service3.model.PendingMessage;
import com.emna.micro_service3.model.ValueOfAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class IndexConfigurationController {

    @Autowired
    private IndexConfigurationInterface indexConfigurationInterface;

    @GetMapping("/config")
    public List<IndexConfiguration> getAllIndexConfigurations() {
        return indexConfigurationInterface.findAllIndexConfigurations();
    }

    @GetMapping("/config/{configurationId}/attributes")
    public List<IndexConfigurationAttributeToAdd> getAttributesByConfigurationId(@PathVariable("configurationId") String configurationId) {
        return indexConfigurationInterface.getAttributesByConfigurationId(configurationId);
    }

    @GetMapping("/messages/{pendingMessageId}")
    public PendingMessage getPendingMessageById(@PathVariable("pendingMessageId") String pendingMessageId) {
        return indexConfigurationInterface.getPendingMessageById(pendingMessageId);
    }

    @GetMapping("/messages/{pendingMessageId}/attributes")
    public List<ValueOfAttribute> findByPendingMessageId(@PathVariable("pendingMessageId") String pendingMessageId) {
        return indexConfigurationInterface.findByPendingMessageId(pendingMessageId);
    }
    @GetMapping("/config/{configurationId}/messages")
    public ResponseEntity<List<PendingMessage>> getMessagesByConfigurationId(@PathVariable("configurationId") String configurationId) {
        List<PendingMessage> messages = indexConfigurationInterface.getMessagesByConfigId(configurationId);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @PutMapping("/messages/update-status/{id}")
    public ResponseEntity<String> updateStatusById(
            @PathVariable String id,
            @RequestBody UpdateStatusRequest request) {
        try {
            ResponseEntity<String> response = indexConfigurationInterface.updateStatusById(id, request);
            if (response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok("Status updated successfully");
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body("Failed to update status: " + response.getBody());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating status: " + e.getMessage());
        }
    }
}

