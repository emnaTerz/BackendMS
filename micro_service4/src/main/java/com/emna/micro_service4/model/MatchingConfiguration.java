package com.emna.micro_service4.model;
import com.emna.micro_service3.model.enums.MatchingType;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;
import java.util.List;

@Document(indexName = "matching_configuration")
public class MatchingConfiguration {

    @Id
    private String id;
    private String sourceId;
    private String targetId;
    private String name;
    private MatchingType matchingType;
    private Date creationDate;
    private Date updateDate;
    private List<Date> scheduleList;

    // No-argument constructor
    public MatchingConfiguration() {
    }

    // Full-argument constructor
    public MatchingConfiguration(String id, String sourceId, String targetId, String name, MatchingType matchingType, Date creationDate, Date updateDate, List<Date> scheduleList) {
        this.id = id;
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.name = name;
        this.matchingType = matchingType;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.scheduleList = scheduleList;
    }

    // Getters and Setters
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
