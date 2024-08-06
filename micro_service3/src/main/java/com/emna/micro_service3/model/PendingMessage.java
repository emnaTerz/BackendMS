package com.emna.micro_service3.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "pending_messages")
public class PendingMessage {

    @Id
    private String id;
    private String indexConfigurationId;
    private String sender;
    private String name;
    private Date creationDate;
    private String messageCategory;

    private String status;



    /* Constructors */

    public PendingMessage(String sender, String name, Date creationDate, String messageCategory, String status) {
        this.sender = sender;
        this.name = name;
        this.creationDate = creationDate;
        this.messageCategory = messageCategory;
        this.status = status;

    }

    public PendingMessage(String id, String sender, String name, Date creationDate, String messageCategory, String status) {
        this.id = id;
        this.sender = sender;
        this.name = name;
        this.creationDate = creationDate;
        this.messageCategory = messageCategory;
        this.status = status;
    }

    public PendingMessage() {
    }

    /* Getters and setters... */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    /* ToString */

    @Override
    public String toString() {
        return "PendingMessage{" +
                "id='" + id + '\'' +
                ", sender='" + sender + '\'' +
                ", name='" + name + '\'' +
                ", creationDate=" + creationDate +
                ", messageCategory='" + messageCategory + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

