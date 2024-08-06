/*package com.emna.micro_service3.dto;

import com.emna.micro_service3.model.enums.MatchingType;

import java.util.Date;
import java.util.List;
public  class MatchingConfigurationRequest {
    private String sourceId;
    private String targetId;
    private String name;
    private MatchingType matchingType;
    private List<Date> scheduleList;

    // Getters and Setters
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
}*/

package com.emna.micro_service3.dto;

import com.emna.micro_service3.model.enums.MatchingType;

import java.util.Date;
import java.util.List;

public class MatchingConfigurationRequest {
    private String sourceId;
    private String targetId;
    private String name;
    private MatchingType matchingType;
    private List<Date> scheduleList;

    // Getters and Setters
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

