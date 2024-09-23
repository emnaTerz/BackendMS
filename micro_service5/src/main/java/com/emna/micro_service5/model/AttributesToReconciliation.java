package com.emna.micro_service5.model;


import com.emna.micro_service5.model.enums.Operation;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Map;
import java.util.UUID;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(indexName = "attribute_to_reconciliation")
public class AttributesToReconciliation {
    @Id
    private String id;

    @Field(name = "ReconciliationConfigurationId")
    private String reconciliationConfigurationId;

    @Field(type = FieldType.Nested)
    private Map<Integer, IndexConfigurationAttributeToAdd> sourceAttributes;

    @Field(type = FieldType.Nested)
    private Map<Integer, IndexConfigurationAttributeToAdd> targetAttributes;

    @Field(type = FieldType.Object)
    private Map<Integer, Operation> sourceOperations;

    @Field(type = FieldType.Object)
    private Map<Integer, Operation> targetOperations;

    @Field(type = FieldType.Text)
    private Map<Integer, String> sourceValues;

    @Field(type = FieldType.Text)
    private Map<Integer, String> targetValues;

    // Constructor
    public AttributesToReconciliation() {
        this.id = UUID.randomUUID().toString();
    }

    public AttributesToReconciliation(String id, String reconciliationConfigurationId,
                                      Map<Integer, IndexConfigurationAttributeToAdd> sourceAttributes,
                                      Map<Integer, IndexConfigurationAttributeToAdd> targetAttributes,
                                      Map<Integer, Operation> sourceOperations,
                                      Map<Integer, Operation> targetOperations,
                                      Map<Integer, String> sourceValues,
                                      Map<Integer, String> targetValues) {
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

    // Get the operation symbol
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
            case power:
                return "^";
            case sqrt:
                return "sqrt";
            default:
                return "";
        }
    }

}
