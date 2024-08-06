package com.emna.micro_service2.dto;

import com.emna.micro_service2.model.ValueOfAttribute;

import java.util.Date;
import java.util.List;

public class PendingMessageDto {
    private String sender;
    private String name;
    private Date creationDate;
    private String messageCategory;
private String IndexConfigurationId;
    private String status;
    private List<ValueOfAttribute> attributes;

    // Constructors, getters, and setters...



    public PendingMessageDto(String sender, String name, Date creationDate, String messageCategory, String status, List<ValueOfAttribute> attributes) {
        this.sender = sender;
        this.name = name;
        this.creationDate = creationDate;
        this.messageCategory = messageCategory;
        this.attributes = attributes;
        this.status = status;
    }

    public String getIndexConfigurationId() {
        return IndexConfigurationId;
    }

    public void setIndexConfigurationId(String indexConfigurationId) {
        IndexConfigurationId = indexConfigurationId;
    }

    public PendingMessageDto() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getMessageCategory() {
        return messageCategory;
    }

    public void setMessageCategory(String messageCategory) {
        this.messageCategory = messageCategory;
    }

    public List<ValueOfAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<ValueOfAttribute> attributes) {
        this.attributes = attributes;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    @Override
    public String toString() {
        return "PendingMessageDto{" +
                "sender='" + sender + '\'' +
                ", name='" + name + '\'' +
                ", creationDate=" + creationDate +
                ", messageCategory='" + messageCategory + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
