package com.emna.micro_service2.controller;

import com.emna.micro_service2.model.IndexConfiguration;
import com.emna.micro_service2.model.PendingMessage;
import com.emna.micro_service2.repository.IndexConfigurationRepository;
import com.emna.micro_service2.repository.PendingMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ElasticsearchController {

    @Autowired
    private IndexConfigurationRepository indexConfigurationRepository;

    @Autowired
    private PendingMessageRepository pendingMessageRepository;


    @GetMapping("/indexConfigurations")
    public List<IndexConfiguration> getAllIndexConfigurations() {
        return (List<IndexConfiguration>) indexConfigurationRepository.findAll();
    }

    @GetMapping("/pendingMessages")
    public List<PendingMessage> getAllPendingMessages() {
        return (List<PendingMessage>) pendingMessageRepository.findAll();
    }

}
