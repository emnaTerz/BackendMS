package com.emna.micro_service3.dto;

import com.emna.micro_service3.model.IndexConfigurationAttributeToAdd;
import com.emna.micro_service3.model.enums.Operation;

import java.util.Map;

public class AttributesToMatchRequest {
    private String matchingConfigurationId;
    private Map<Integer, IndexConfigurationAttributeToAdd> sourceAttributes;
    private Map<Integer, IndexConfigurationAttributeToAdd> targetAttributes;
    private Map<Integer, Operation> sourceOperations;
    private Map<Integer, Operation> targetOperations;

    // Getters and Setters
    public String getMatchingConfigurationId() {
        return matchingConfigurationId;
    }

    public void setMatchingConfigurationId(String matchingConfigurationId) {
        this.matchingConfigurationId = matchingConfigurationId;
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

    public Map<Integer, Operation> getSourceOperations() {
        return sourceOperations;
    }

    public void setSourceOperations(Map<Integer, Operation> sourceOperations) {
        this.sourceOperations = sourceOperations;
    }

    public Map<Integer, Operation> getTargetOperations() {
        return targetOperations;
    }

    public void setTargetOperations(Map<Integer, Operation> targetOperations) {
        this.targetOperations = targetOperations;
    }
}
