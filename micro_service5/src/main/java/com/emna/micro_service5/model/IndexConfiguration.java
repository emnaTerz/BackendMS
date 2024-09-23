package com.emna.micro_service5.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Document(indexName = "index_configuration")
public class IndexConfiguration {

    @Id
    private String id;
    private String sender;
    private String name;
    private Date creationDate;
    private Date updateDate;
    private String messageCategory;

    public IndexConfiguration(String id, String sender, String name, Date creationDate, Date updateDate, String messageCategory) {
        this.id = id;
        this.sender = sender;
        this.name = name;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.messageCategory = messageCategory;
    }

    public IndexConfiguration(String sender, String name, String messageCategory) {
        this.sender = sender;
        this.name = name;
        this.messageCategory = messageCategory;
    }

    public IndexConfiguration() { }

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
