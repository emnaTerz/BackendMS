package com.emna.micro_service4.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Document(indexName = "reconciliation_configuration")
public class ReconciliationConfiguration {

    @Id
    private String id;
    private String name;
    private String matchingConfigurationId;
    private Date creationDate;
    private Date updateDate;
    private String tolerance;
    private List<Date> scheduleList;

    // No-argument constructor
    public ReconciliationConfiguration() {
        this.id = UUID.randomUUID().toString();  // Automatically generate key when an instance is created
    }

    // Full-argument constructor
    public ReconciliationConfiguration(String id, String name, String matchingConfigurationId, Date creationDate, Date updateDate, String tolerance, List<Date> scheduleList) {
        this();
        this.name = name;
        this.matchingConfigurationId = matchingConfigurationId;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.tolerance = tolerance;
        this.scheduleList = scheduleList;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMatchingConfigurationId() {
        return matchingConfigurationId;
    }

    public void setMatchingConfigurationId(String matchingConfigurationId) {
        this.matchingConfigurationId = matchingConfigurationId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getTolerance() {
        return tolerance;
    }

    public void setTolerance(String tolerance) {
        this.tolerance = tolerance;
    }

    public List<Date> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(List<Date> scheduleList) {
        this.scheduleList = scheduleList;
    }
}
