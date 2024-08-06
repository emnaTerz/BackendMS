package com.emna.micro_service3.model;
/*
import com.emna.micro_service3.model.enums.Operation;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Map;

@Document(indexName = "attribute_to_match")
public class AttributesToMatch {
    @Id
    private String id;
    @Field(name = "MatchingConfigurationId")
    private String matchingConfigurationId; // Changed to lowercase initial letter
    @Field(type = FieldType.Nested) // Ensure complex types are correctly annotated
    private Map<Integer, IndexConfigurationAttributeToAdd> sourceAttributes;

    @Field(type = FieldType.Nested)
    private Map<Integer, IndexConfigurationAttributeToAdd> targetAttributes;

    @Field(type = FieldType.Object)
    private Map<Integer, Operation> sourceOperations;

    @Field(type = FieldType.Object)
    private Map<Integer, Operation> targetOperations;

    // Constructor and getters/setters should also reflect this change
    public String getMatchingConfigurationId() {
        return matchingConfigurationId;
    }

    public void setMatchingConfigurationId(String matchingConfigurationId) {
        this.matchingConfigurationId = matchingConfigurationId;
    }

    public AttributesToMatch(String id, String matchingConfigurationId, Map<Integer, IndexConfigurationAttributeToAdd> sourceAttributes, Map<Integer, IndexConfigurationAttributeToAdd> targetAttributes, Map<Integer, Operation> targetOperations, Map<Integer, Operation> sourceOperations) {
        this.id = id;
        this.matchingConfigurationId = matchingConfigurationId;
        this.sourceAttributes = sourceAttributes;
        this.targetAttributes = targetAttributes;
        this.targetOperations = targetOperations;
        this.sourceOperations = sourceOperations;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSourceAttributes(Map<Integer, IndexConfigurationAttributeToAdd> sourceAttributes) {
        this.sourceAttributes = sourceAttributes;
    }

    public void setTargetAttributes(Map<Integer, IndexConfigurationAttributeToAdd> targetAttributes) {
        this.targetAttributes = targetAttributes;
    }

    public void setTargetOperations(Map<Integer, Operation> targetOperations) {
        this.targetOperations = targetOperations;
    }

    public void setSourceOperations(Map<Integer, Operation> sourceOperations) {
        this.sourceOperations = sourceOperations;
    }

    public String getId() {
        return id;
    }

    public Map<Integer, IndexConfigurationAttributeToAdd> getSourceAttributes() {
        return sourceAttributes;
    }

    public Map<Integer, IndexConfigurationAttributeToAdd> getTargetAttributes() {
        return targetAttributes;
    }

    public Map<Integer, Operation> getTargetOperations() {
        return targetOperations;
    }

    public Map<Integer, Operation> getSourceOperations() {
        return sourceOperations;
    }

    public String getFormule() {
        StringBuilder formula = new StringBuilder();

        // Build the source part of the formula
        for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : sourceAttributes.entrySet()) {
            Integer index = entry.getKey();
            formula.append(sourceAttributes.get(index).getAttributeToAdd());
            if (sourceOperations.containsKey(index)) {
                formula.append(" ").append(getOperationSymbol(sourceOperations.get(index))).append(" ");
            }
        }

        formula.append(" = ");

        // Build the target part of the formula
        for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : targetAttributes.entrySet()) {
            Integer index = entry.getKey();
            formula.append(targetAttributes.get(index).getAttributeToAdd());
            if (targetOperations.containsKey(index)) {
                formula.append(" ").append(getOperationSymbol(targetOperations.get(index))).append(" ");
            }
        }

        return formula.toString().trim();
    }

    // Helper method to convert Operation enum to symbol
    private String getOperationSymbol(Operation operation) {
        switch (operation) {
            case somme:
                return "+";
            case soustraction:
                return "-";
            case multiplication:
                return "*";
            case division:
                return "/";
            default:
                return "";
        }
    }
}
*/

import com.emna.micro_service3.model.enums.Operation;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Map;

@Document(indexName = "attribute_to_match")
public class AttributesToMatch {
    @Id
    private String id;

    @Field(name = "MatchingConfigurationId")
    private String matchingConfigurationId;

    @Field(type = FieldType.Nested)
    private Map<Integer, IndexConfigurationAttributeToAdd> sourceAttributes;

    @Field(type = FieldType.Nested)
    private Map<Integer, IndexConfigurationAttributeToAdd> targetAttributes;

    @Field(type = FieldType.Object)
    private Map<Integer, Operation> sourceOperations;

    @Field(type = FieldType.Object)
    private Map<Integer, Operation> targetOperations;

    // Constructor, getters, and setters

    public AttributesToMatch() {
    }

    public AttributesToMatch(String id, String matchingConfigurationId, Map<Integer, IndexConfigurationAttributeToAdd> sourceAttributes, Map<Integer, IndexConfigurationAttributeToAdd> targetAttributes, Map<Integer, Operation> sourceOperations, Map<Integer, Operation> targetOperations) {
        this.id = id;
        this.matchingConfigurationId = matchingConfigurationId;
        this.sourceAttributes = sourceAttributes;
        this.targetAttributes = targetAttributes;
        this.sourceOperations = sourceOperations;
        this.targetOperations = targetOperations;
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

    // Other methods
    public String getFormule() {
        StringBuilder formula = new StringBuilder();

        // Build the source part of the formula
        for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : sourceAttributes.entrySet()) {
            Integer index = entry.getKey();
            formula.append(sourceAttributes.get(index).getAttributeToAdd());
            if (sourceOperations.containsKey(index)) {
                formula.append(" ").append(getOperationSymbol(sourceOperations.get(index))).append(" ");
            }
        }

        formula.append(" = ");

        // Build the target part of the formula
        for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : targetAttributes.entrySet()) {
            Integer index = entry.getKey();
            formula.append(targetAttributes.get(index).getAttributeToAdd());
            if (targetOperations.containsKey(index)) {
                formula.append(" ").append(getOperationSymbol(targetOperations.get(index))).append(" ");
            }
        }

        return formula.toString().trim();
    }
    public String buildFormula(Map<String, String> sourceValues, Map<String, String> targetValues) {
        StringBuilder formula = new StringBuilder();

        // Build the source part of the formula
        for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : sourceAttributes.entrySet()) {
            Integer index = entry.getKey();
            String value = sourceValues.get(entry.getValue().getAttributeToAddKey());
            formula.append("\"").append(value).append("\"");
            if (sourceOperations.containsKey(index)) {
                formula.append(" ").append(getOperationSymbol(sourceOperations.get(index))).append(" ");
            }
        }

        formula.append(" == ");

        // Build the target part of the formula
        for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : targetAttributes.entrySet()) {
            Integer index = entry.getKey();
            String value = targetValues.get(entry.getValue().getAttributeToAddKey());
            formula.append("\"").append(value).append("\"");
            if (targetOperations.containsKey(index)) {
                formula.append(" ").append(getOperationSymbol(targetOperations.get(index))).append(" ");
            }
        }

        return formula.toString().trim();
    }

    // Helper method to convert Operation enum to symbol
    private String getOperationSymbol(Operation operation) {
        switch (operation) {
            case somme:
                return "+";
            case soustraction:
                return "-";
            case multiplication:
                return "*";
            case division:
                return "/";
            default:
                return "";
        }
    }
}

