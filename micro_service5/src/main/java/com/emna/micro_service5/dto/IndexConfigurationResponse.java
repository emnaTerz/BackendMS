package com.emna.micro_service5.dto;

import java.util.Date;

public class IndexConfigurationResponse {

    private String id;
    private String sender;
    private String name;
    private Date creationDate;
    private Date updateDate;
    private String messageCategory;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Date getCreationDate() { return creationDate; }
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }
    public Date getUpdateDate() { return updateDate; }
    public void setUpdateDate(Date updateDate) { this.updateDate = updateDate; }
    public String getMessageCategory() { return messageCategory; }
    public void setMessageCategory(String messageCategory) { this.messageCategory = messageCategory; }
}
