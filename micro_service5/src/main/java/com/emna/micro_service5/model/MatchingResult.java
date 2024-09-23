package com.emna.micro_service5.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;
import java.util.Map;

@Document(indexName = "matching_results")
public class MatchingResult {

    @Id
    private String id;
    private String matchingConfigurationId;
    private Map<String, String> sourceMessages;
    private Map<String, String> targetMessages;
    private String matchDetails;
    private String matchStatus;
    private Date timestamp;
    private Map<String, String> matchedAttributes;
    private Map<String, String> unmatchedAttributes;

    public MatchingResult() {}

    public MatchingResult(String matchingConfigurationId, Map<String, String> sourceMessages, Map<String, String> targetMessages, String matchDetails, String matchStatus, Date timestamp, Map<String, String> matchedAttributes, Map<String, String> unmatchedAttributes) {
        this.matchingConfigurationId = matchingConfigurationId;
        this.sourceMessages = sourceMessages;
        this.targetMessages = targetMessages;
        this.matchDetails = matchDetails;
        this.matchStatus = matchStatus;
        this.timestamp = timestamp;
        this.matchedAttributes = matchedAttributes;
        this.unmatchedAttributes = unmatchedAttributes;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMatchingConfigurationId() {
        return matchingConfigurationId;
    }

    public void setMatchingConfigurationId(String matchingConfigurationId) {
        this.matchingConfigurationId = matchingConfigurationId;
    }

    public Map<String, String> getSourceMessages() {
        return sourceMessages;
    }

    public void setSourceMessages(Map<String, String> sourceMessages) {
        this.sourceMessages = sourceMessages;
    }

    public Map<String, String> getTargetMessages() {
        return targetMessages;
    }

    public void setTargetMessages(Map<String, String> targetMessages) {
        this.targetMessages = targetMessages;
    }

    public String getMatchDetails() {
        return matchDetails;
    }

    public void setMatchDetails(String matchDetails) {
        this.matchDetails = matchDetails;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getMatchedAttributes() {
        return matchedAttributes;
    }

    public void setMatchedAttributes(Map<String, String> matchedAttributes) {
        this.matchedAttributes = matchedAttributes;
    }

    public Map<String, String> getUnmatchedAttributes() {
        return unmatchedAttributes;
    }

    public void setUnmatchedAttributes(Map<String, String> unmatchedAttributes) {
        this.unmatchedAttributes = unmatchedAttributes;
    }

    @Override
    public String toString() {
        return "MatchingResult{" +
                "id='" + id + '\'' +
                ", matchingConfigurationId='" + matchingConfigurationId + '\'' +
                ", sourceMessages=" + sourceMessages +
                ", targetMessages=" + targetMessages +
                ", matchDetails='" + matchDetails + '\'' +
                ", matchStatus='" + matchStatus + '\'' +
                ", timestamp=" + timestamp +
                ", matchedAttributes=" + matchedAttributes +
                ", unmatchedAttributes=" + unmatchedAttributes +
                '}';
    }
}
