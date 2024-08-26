package com.emna.micro_service4.dto;

import java.util.Date;
import java.util.List;

public class ReconciliationConfigurationDTO {
    private String id;
    private String name;
    private String matchingConfigurationId;
    private Date creationDate;
    private Date updateDate;
    private String tolerance;
    private List<Date> scheduleList;

    // Constructors, getters and setters

    public ReconciliationConfigurationDTO() {}

    public ReconciliationConfigurationDTO(String id, String name, String matchingConfigurationId, Date creationDate, Date updateDate, String tolerance, List<Date> scheduleList) {
        this.id = id;
        this.name = name;
        this.matchingConfigurationId = matchingConfigurationId;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.tolerance = tolerance;
        this.scheduleList = scheduleList;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMatchingConfigurationId() { return matchingConfigurationId; }
    public void setMatchingConfigurationId(String matchingConfigurationId) { this.matchingConfigurationId = matchingConfigurationId; }
    public Date getCreationDate() { return creationDate; }
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }
    public Date getUpdateDate() { return updateDate; }
    public void setUpdateDate(Date updateDate) { this.updateDate = updateDate; }
    public String getTolerance() { return tolerance; }
    public void setTolerance(String tolerance) { this.tolerance = tolerance; }
    public List<Date> getScheduleList() { return scheduleList; }
    public void setScheduleList(List<Date> scheduleList) { this.scheduleList = scheduleList; }

    @Override
    public String toString() {
        return "ReconciliationConfigurationDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", matchingConfigurationId='" + matchingConfigurationId + '\'' +
                ", creationDate=" + creationDate +
                ", updateDate=" + updateDate +
                ", tolerance='" + tolerance + '\'' +
                ", scheduleList=" + scheduleList +
                '}';
    }
}
