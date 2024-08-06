package com.emna.micro_service3.dto;


import com.emna.micro_service3.model.enums.MatchingType;

import java.util.Date;
import java.util.List;

public class MatchingConfigurationResponse {

    private String id;
    private String name;
    private String sourceId;
    private String targetId;
    private MatchingType matchingType;
    private Date creationDate;
    private Date updateDate;
    private List<Date> scheduleList;

    public MatchingConfigurationResponse() {
    }

    public MatchingConfigurationResponse(String id, String sourceId, String targetId, String name, MatchingType matchingType, Date creationDate, Date updateDate, List<Date> scheduleList) {
        this.id = id;
        this.name = name;
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.matchingType = matchingType;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.scheduleList = scheduleList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public MatchingType getMatchingType() {
        return matchingType;
    }

    public void setMatchingType(MatchingType matchingType) {
        this.matchingType = matchingType;
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

    public List<Date> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(List<Date> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
