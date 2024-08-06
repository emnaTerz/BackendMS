package com.emna.micro_service2.kafka;

import com.emna.micro_service2.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class KafkaConsumer {

    @Autowired
    private MessageService messageService;

    @KafkaListener(topics = "your_topic_name", groupId = "your_group_id", containerFactory = "kafkaListenerContainerFactory")
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
        try {
            messageService.storeFile(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
