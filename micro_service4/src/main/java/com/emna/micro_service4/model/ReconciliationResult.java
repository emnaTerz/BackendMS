package com.emna.micro_service4.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Document(indexName = "reconciliation_results")
public class ReconciliationResult {

    @Id
    private String id;
    private String reconciliationConfigurationId;
    private Map<String, String> sourceMessages;
    private Map<String, String> targetMessages;
    private Date timestamp;
    private String reconciliationStatus;
    private String reconciliationDetails;
    private Map<String, String> reconciledAttributes;
    private Map<String, String> unreconciledAttributes;

    public ReconciliationResult() {
        this.id = UUID.randomUUID().toString();
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReconciliationConfigurationId() {
        return reconciliationConfigurationId;
    }

    public void setReconciliationConfigurationId(String reconciliationConfigurationId) {
        this.reconciliationConfigurationId = reconciliationConfigurationId;
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getReconciliationStatus() {
        return reconciliationStatus;
    }

    public void setReconciliationStatus(String reconciliationStatus) {
        this.reconciliationStatus = reconciliationStatus;
    }

    public String getReconciliationDetails() {
        return reconciliationDetails;
    }

    public void setReconciliationDetails(String reconciliationDetails) {
        this.reconciliationDetails = reconciliationDetails;
    }

    public Map<String, String> getReconciledAttributes() {
        return reconciledAttributes;
    }

    public void setReconciledAttributes(Map<String, String> reconciledAttributes) {
        this.reconciledAttributes = reconciledAttributes;
    }

    public Map<String, String> getUnreconciledAttributes() {
        return unreconciledAttributes;
    }

    public void setUnreconciledAttributes(Map<String, String> unreconciledAttributes) {
        this.unreconciledAttributes = unreconciledAttributes;
    }

    @Override
    public String toString() {
        return "ReconciliationResult{" +
                "id='" + id + '\'' +
                ", reconciliationConfigurationId='" + reconciliationConfigurationId + '\'' +
                ", sourceMessages=" + sourceMessages +
                ", targetMessages=" + targetMessages +
                ", timestamp=" + timestamp +
                ", reconciliationStatus='" + reconciliationStatus + '\'' +
                ", reconciliationDetails='" + reconciliationDetails + '\'' +
                ", reconciledAttributes=" + reconciledAttributes +
                ", unreconciledAttributes=" + unreconciledAttributes +
                '}';
    }
}
