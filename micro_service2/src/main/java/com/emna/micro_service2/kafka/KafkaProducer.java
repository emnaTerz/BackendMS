package com.emna.micro_service2.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "your_topic_name";

    public String sendMessage(String message) {
        System.out.println("Publishing message to topic: " + TOPIC);
        kafkaTemplate.send(TOPIC, message);
        return "Message published successfully!";
    }
}

