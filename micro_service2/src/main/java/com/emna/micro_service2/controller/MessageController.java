package com.emna.micro_service2.controller;

import com.emna.micro_service2.dto.PendingMessageDto;
import com.emna.micro_service2.dto.UpdateStatusRequest;
import com.emna.micro_service2.kafka.KafkaProducer;
import com.emna.micro_service2.model.PendingMessage;
import com.emna.micro_service2.model.ValueOfAttribute;
import com.emna.micro_service2.repository.ValueOfAttributeRepository;
import com.emna.micro_service2.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/config")
public class MessageController {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private MessageService service;
    @Autowired
    private ValueOfAttributeRepository repository;

    @PostMapping("/publish")
    public ResponseEntity<String> publishMessage(@RequestParam("file") MultipartFile file) {
        try {
            String xmlContent = new String(file.getBytes());
            System.out.println("Publishing message: " + xmlContent);
            kafkaProducer.sendMessage(xmlContent);
            return ResponseEntity.ok("Message published successfully!");
        } catch (IOException e) {
            System.out.println("Failed to read the file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to publish message: " + e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<List<PendingMessage>> getAllMessages() {
        List<PendingMessage> messages = service.getAllMessages();
        if (messages.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(messages);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<PendingMessageDto> getMessageById(@PathVariable String id) {
        System.out.println("ID: " + id);

        PendingMessageDto pendingMessageDto = service.getMessageById(id);
        if (pendingMessageDto != null) {
            return new ResponseEntity<>(pendingMessageDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/attributes/{pendingMessageId}")
    public ResponseEntity<List<ValueOfAttribute>> getAttributesByPendingMessageId(@PathVariable String pendingMessageId) {
        List<ValueOfAttribute> attributes = service.getAttributesByPendingMessageId(pendingMessageId);
        if (attributes.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(attributes);
        }
    }
    @GetMapping("/ById/{id}")
    public ResponseEntity<ValueOfAttribute> getValueOfAttributeById(@PathVariable String id) {
        Optional<ValueOfAttribute> valueOfAttribute = repository.findById(id);
        return valueOfAttribute.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/{configurationId}/messages")
    public ResponseEntity<List<PendingMessage>> getMessagesByConfigId(@PathVariable("configurationId") String configurationId) {
        List<PendingMessage> messages = service.getMessagesByConfigId(configurationId);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @PutMapping("/update-status/{id}")
    public ResponseEntity<Void> updateStatusById(
            @PathVariable String id,
            @RequestBody UpdateStatusRequest request) {
        service.updateStatusById(id, request.getStatus());
        return ResponseEntity.ok().build();
    }
    @GetMapping("/messages")
    public ResponseEntity<List<PendingMessage>> findAllPendingMessages() {
        List<PendingMessage> messages = service.getAllMessages();
        if (messages.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(messages);
        }
    }

}
