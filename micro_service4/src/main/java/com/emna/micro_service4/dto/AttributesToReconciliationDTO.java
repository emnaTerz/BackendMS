package com.emna.micro_service4.dto;

import com.emna.micro_service3.model.IndexConfigurationAttributeToAdd;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributesToReconciliationDTO {
    private String id;
    private String reconciliationConfigurationId;
    private Map<Integer, IndexConfigurationAttributeToAdd> sourceAttributes;
    private Map<Integer, IndexConfigurationAttributeToAdd> targetAttributes;
    private Map<Integer, String> sourceOperations;
    private Map<Integer, String> targetOperations;
    private Map<Integer, String> sourceValues;
    private Map<Integer, String> targetValues;


    // Constructors, getters, and setters

    public AttributesToReconciliationDTO() {
        this.id = UUID.randomUUID().toString();  // Automatically generate key when an instance is created
    }

    public AttributesToReconciliationDTO(String id, String reconciliationConfigurationId, Map<Integer, IndexConfigurationAttributeToAdd> sourceAttributes, Map<Integer, IndexConfigurationAttributeToAdd> targetAttributes, Map<Integer, String> sourceOperations, Map<Integer, String> targetOperations, Map<Integer, String> sourceValues, Map<Integer, String> targetValues) {
        this.id = id;
        this.reconciliationConfigurationId = reconciliationConfigurationId;
        this.sourceAttributes = sourceAttributes;
        this.targetAttributes = targetAttributes;
        this.sourceOperations = sourceOperations;
        this.targetOperations = targetOperations;
        this.sourceValues = sourceValues;
        this.targetValues = targetValues;
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

    public Map<Integer, IndexConfigurationAttributeToAdd> getSourceAttributes() {
        return sourceAttributes;
    }

    public void setSourceAttributes(Map<Integer, IndexConfigurationAttributeToAdd> sourceAttributes) {
        this.sourceAttributes = sourceAttributes;
    }

    public Map<Integer, IndexConfigurationAttributeToAdd> getTargetAttributes() {
        return targetAttributes;
    }

    public void setTargetAttributes(Map<Integer, IndexConfigurationAttributeToAdd> targetAttributes) {
        this.targetAttributes = targetAttributes;
    }

    public Map<Integer, String> getSourceOperations() {
        return sourceOperations;
    }

    public void setSourceOperations(Map<Integer, String> sourceOperations) {
        this.sourceOperations = sourceOperations;
    }

    public Map<Integer, String> getTargetOperations() {
        return targetOperations;
    }

    public void setTargetOperations(Map<Integer, String> targetOperations) {
        this.targetOperations = targetOperations;
    }

    public Map<Integer, String> getSourceValues() {
        return sourceValues;
    }

    public void setSourceValues(Map<Integer, String> sourceValues) {
        this.sourceValues = sourceValues;
    }

    public Map<Integer, String> getTargetValues() {
        return targetValues;
    }

    public void setTargetValues(Map<Integer, String> targetValues) {
        this.targetValues = targetValues;
    }


}
