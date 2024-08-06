package com.emna.micro_service2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IndexConfigurationRequest {

    private String name;
    private String messageCategory;
    private String sender;

    public IndexConfigurationRequest() { }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMessageCategory() { return messageCategory; }
    public void setMessageCategory(String messageCategory) { this.messageCategory = messageCategory; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public IndexConfigurationRequest(String name, String messageCategory, String sender) {
        this.name = name;
        this.messageCategory = messageCategory;
        this.sender = sender;
    }
}
