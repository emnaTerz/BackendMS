package com.emna.micro_service4.model;
/*
import com.emna.micro_service3.model.IndexConfigurationAttributeToAdd;
import com.emna.micro_service4.dto.AttributesToReconciliationDTO;
import com.emna.micro_service4.model.enums.Operation;
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


    // Constructor, getters, and setters

    public AttributesToReconciliation() {
        this.id = UUID.randomUUID().toString();  // Automatically generate key when an instance is created
    }

    public AttributesToReconciliation(String id, String reconciliationConfigurationId, Map<Integer, IndexConfigurationAttributeToAdd> sourceAttributes, Map<Integer, IndexConfigurationAttributeToAdd> targetAttributes, Map<Integer, Operation> sourceOperations, Map<Integer, Operation> targetOperations, Map<Integer, String> sourceValues, Map<Integer, String> targetValues) {
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

    public String getFormula() {
        StringBuilder formula = new StringBuilder();

        // Build the source part of the formula
        for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : sourceAttributes.entrySet()) {
            Integer index = entry.getKey();
            if (sourceOperations.containsKey(index)) {
                String operation = getOperationSymbol(sourceOperations.get(index));
                if (operation.equals("sqrt")) {
                    formula.append(operation).append("(").append(sourceAttributes.get(index).getAttributeToAdd()).append(") ");
                } else {
                    formula.append(sourceAttributes.get(index).getAttributeToAdd()).append(" ").append(operation).append(" ");
                }
            } else {
                formula.append(sourceAttributes.get(index).getAttributeToAdd()).append(" ");
            }
        }

        formula.append(" = ");

        // Build the target part of the formula
        for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : targetAttributes.entrySet()) {
            Integer index = entry.getKey();
            if (targetOperations.containsKey(index)) {
                String operation = getOperationSymbol(targetOperations.get(index));
                if (operation.equals("sqrt")) {
                    formula.append(operation).append("(").append(targetAttributes.get(index).getAttributeToAdd()).append(") ");
                } else if (operation.equals("^")) {
                    formula.append(targetAttributes.get(index).getAttributeToAdd()).append(operation).append(targetValues.get(index)).append(" ");
                } else {
                    formula.append(targetAttributes.get(index).getAttributeToAdd()).append(" ").append(operation).append(" ");
                }
            } else {
                formula.append(targetAttributes.get(index).getAttributeToAdd()).append(" ");
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
            if (sourceOperations.containsKey(index)) {
                String operation = getOperationSymbol(sourceOperations.get(index));
                if (operation.equals("sqrt")) {
                    formula.append(operation).append("(\"").append(value).append("\") ");
                } else if (operation.equals("^")) {
                    formula.append("\"").append(value).append("\" ").append(operation).append(" ").append(sourceValues.get(index)).append(" ");
                } else {
                    formula.append("\"").append(value).append("\" ").append(operation).append(" ");
                }
            } else {
                formula.append("\"").append(value).append("\" ");
            }
        }

        formula.append(" == ");

        // Build the target part of the formula
        for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : targetAttributes.entrySet()) {
            Integer index = entry.getKey();
            String value = targetValues.get(entry.getValue().getAttributeToAddKey());
            if (targetOperations.containsKey(index)) {
                String operation = getOperationSymbol(targetOperations.get(index));
                if (operation.equals("sqrt")) {
                    formula.append(operation).append("(\"").append(value).append("\") ");
                } else if (operation.equals("^")) {
                    formula.append("\"").append(value).append("\" ").append(operation).append(" ").append(targetValues.get(index)).append(" ");
                } else {
                    formula.append("\"").append(value).append("\" ").append(operation).append(" ");
                }
            } else {
                formula.append("\"").append(value).append("\" ");
            }
        }

        return formula.toString().trim();
    }

    public boolean equalsAttributesAndOperations(AttributesToReconciliationDTO dto) {
        return this.reconciliationConfigurationId.equals(dto.getReconciliationConfigurationId()) &&
                this.sourceAttributes.equals(dto.getSourceAttributes()) &&
                this.targetAttributes.equals(dto.getTargetAttributes()) &&
                this.sourceOperations.equals(dto.getSourceOperations()) &&
                this.targetOperations.equals(dto.getTargetOperations()) &&
                this.sourceValues.equals(dto.getSourceValues()) &&
                this.targetValues.equals(dto.getTargetValues());
    }
    public double evaluateSource(Map<String, String> sourceValues) {
        StringBuilder sourceFormula = new StringBuilder();

        for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : sourceAttributes.entrySet()) {
            Integer index = entry.getKey();
            String value = sourceValues.get(entry.getValue().getAttributeToAddKey());
            if (sourceOperations.containsKey(index)) {
                String operation = getOperationSymbol(sourceOperations.get(index));
                if (operation.equals("sqrt")) {
                    sourceFormula.append("Math.sqrt(").append(value).append(") ");
                } else if (operation.equals("^")) {
                    sourceFormula.append(value).append(" ").append(operation).append(" 2 ");
                } else {
                    sourceFormula.append(value).append(" ").append(operation).append(" ");
                }
            } else {
                sourceFormula.append(value).append(" ");
            }
        }

        // Evaluate the source formula
        return evaluateFormula(sourceFormula.toString().trim());
    }

    // Method to build and evaluate the target part of the formula for each target
    public double evaluateTarget(Map<String, String> targetValues) {
        StringBuilder targetFormula = new StringBuilder();

        for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : targetAttributes.entrySet()) {
            Integer index = entry.getKey();
            String value = targetValues.get(entry.getValue().getAttributeToAddKey());
            if (targetOperations.containsKey(index)) {
                String operation = getOperationSymbol(targetOperations.get(index));
                if (operation.equals("sqrt")) {
                    targetFormula.append("Math.sqrt(").append(value).append(") ");
                } else if (operation.equals("^")) {
                    targetFormula.append(value).append(" ").append(operation).append(" 2 ");
                } else {
                    targetFormula.append(value).append(" ").append(operation).append(" ");
                }
            } else {
                targetFormula.append(value).append(" ");
            }
        }

        // Evaluate the target formula
        return evaluateFormula(targetFormula.toString().trim());
    }

    // Method to evaluate a formula string using JavaScript
    private double evaluateFormula(String formula) {
        try {
            // Log the formula before evaluation
            System.out.println("Evaluating formula: " + formula);

            // Replace Java-style Math.sqrt with JavaScript-compatible Math.sqrt
            formula = formula.replaceAll("Math.sqrt\\(([^)]+)\\)", "Math.sqrt($1)");

            // Evaluate the formula using the JavaScript engine
            Object result = new javax.script.ScriptEngineManager()
                    .getEngineByName("JavaScript")
                    .eval(formula);

            // Handle cases where the result is not directly castable to double
            if (result instanceof Number) {
                return ((Number) result).doubleValue();
            } else {
                throw new RuntimeException("Unexpected result type: " + result.getClass().getName());
            }
        } catch (Exception e) {
            System.err.println("Error evaluating formula: " + formula);
            throw new RuntimeException("Error evaluating formula: " + formula, e);
        }
    }


    public String buildFormule(Map<String, String> sourceValues, Map<String, String> targetValues) {
        StringBuilder formula = new StringBuilder();

        // Build the source part of the formula
        for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : sourceAttributes.entrySet()) {
            Integer index = entry.getKey();
            String value = sourceValues.get(entry.getValue().getAttributeToAddKey());
            if (sourceOperations.containsKey(index)) {
                String operation = getOperationSymbol(sourceOperations.get(index));
                if (operation.equals("sqrt")) {
                    formula.append(operation).append("(").append(value).append(") ");
                } else {
                    formula.append(value).append(" ").append(operation).append(" ");
                }
            } else {
                formula.append(value).append(" ");
            }
        }

        formula.append("= ");

        // Build the target part of the formula
        for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : targetAttributes.entrySet()) {
            Integer index = entry.getKey();
            String value = targetValues.get(entry.getValue().getAttributeToAddKey());
            if (targetOperations.containsKey(index)) {
                String operation = getOperationSymbol(targetOperations.get(index));
                if (operation.equals("sqrt")) {
                    formula.append(operation).append("(").append(value).append(") ");
                } else {
                    formula.append(value).append(" ").append(operation).append(" ");
                }
            } else {
                formula.append(value).append(" ");
            }
        }

        return formula.toString().trim();
    }
}
*/

import com.emna.micro_service3.model.IndexConfigurationAttributeToAdd;
import com.emna.micro_service4.dto.AttributesToReconciliationDTO;
import com.emna.micro_service4.dto.FormulaDTO;
import com.emna.micro_service4.model.enums.Operation;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;


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

    // Build and evaluate source formula
    public double evaluateSource(Map<String, String> sourceValues) {
        StringBuilder sourceFormula = new StringBuilder();
        buildFormulaPart(sourceFormula, sourceAttributes, sourceOperations, sourceValues);
        return evaluateFormula(sourceFormula.toString().trim());
    }

    // Build and evaluate target formula
    public double evaluateTarget(Map<String, String> targetValues) {
        StringBuilder targetFormula = new StringBuilder();
        buildFormulaPart(targetFormula, targetAttributes, targetOperations, targetValues);
        return evaluateFormula(targetFormula.toString().trim());
    }

    // Build formula part (shared between source and target)
    private void buildFormulaPart(StringBuilder formula, Map<Integer, IndexConfigurationAttributeToAdd> attributes,
                                  Map<Integer, Operation> operations, Map<String, String> values) {
        for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : attributes.entrySet()) {
            Integer index = entry.getKey();
            String value = values.get(entry.getValue().getAttributeToAddKey());
            if (operations.containsKey(index)) {
                String operation = getOperationSymbol(operations.get(index));
                if (operation.equals("sqrt")) {
                    formula.append("Math.sqrt(").append(value).append(") ");
                } else if (operation.equals("^")) {
                    formula.append(value).append(" ").append(operation).append(" 2 ");
                } else {
                    formula.append(value).append(" ").append(operation).append(" ");
                }
            } else {
                formula.append(value).append(" ");
            }
        }
    }

    // Evaluate a formula string using JavaScript
// Evaluate a formula string using JavaScript


    private double evaluateFormula(String formula) {
        try {
            // Log the formula before evaluation
            System.out.println("Evaluating formula: " + formula);

            // Replace Java-style Math.sqrt with JavaScript-compatible Math.sqrt
            formula = formula.replaceAll("Math.sqrt\\(([^)]+)\\)", "Math.sqrt($1)");

            // Use GraalVM to evaluate the JavaScript formula
            try (Context context = Context.create()) {
                Value result = context.eval("js", formula);

                // Handle cases where the result is not directly castable to double
                if (result.isNumber()) {
                    return result.asDouble();
                } else {
                    throw new RuntimeException("Unexpected result type: " + result);
                }
            }
        } catch (Exception e) {
            System.err.println("Error evaluating formula: " + formula);
            e.printStackTrace();
            throw new RuntimeException("Error evaluating formula: " + formula, e);
        }
    }


    // Method to check equality of attributes and operations
    public boolean equalsAttributesAndOperations(AttributesToReconciliationDTO dto) {
        return this.reconciliationConfigurationId.equals(dto.getReconciliationConfigurationId()) &&
                this.sourceAttributes.equals(dto.getSourceAttributes()) &&
                this.targetAttributes.equals(dto.getTargetAttributes()) &&
                this.sourceOperations.equals(dto.getSourceOperations()) &&
                this.targetOperations.equals(dto.getTargetOperations()) &&
                this.sourceValues.equals(dto.getSourceValues()) &&
                this.targetValues.equals(dto.getTargetValues());
    }
    public String buildFormula(Map<String, String> sourceValues, Map<String, String> targetValues) {
        StringBuilder formula = new StringBuilder();

        // Build the source part of the formula
        for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : sourceAttributes.entrySet()) {
            Integer index = entry.getKey();
            String value = sourceValues.get(entry.getValue().getAttributeToAddKey());
            if (sourceOperations.containsKey(index)) {
                String operation = getOperationSymbol(sourceOperations.get(index));
                if (operation.equals("sqrt")) {
                    formula.append(operation).append("(\"").append(value).append("\") ");
                } else if (operation.equals("^")) {
                    formula.append("\"").append(value).append("\" ").append(operation).append(" ").append(sourceValues.get(index)).append(" ");
                } else {
                    formula.append("\"").append(value).append("\" ").append(operation).append(" ");
                }
            } else {
                formula.append("\"").append(value).append("\" ");
            }
        }

        formula.append(" == ");

        // Build the target part of the formula
        for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : targetAttributes.entrySet()) {
            Integer index = entry.getKey();
            String value = targetValues.get(entry.getValue().getAttributeToAddKey());
            if (targetOperations.containsKey(index)) {
                String operation = getOperationSymbol(targetOperations.get(index));
                if (operation.equals("sqrt")) {
                    formula.append(operation).append("(\"").append(value).append("\") ");
                } else if (operation.equals("^")) {
                    formula.append("\"").append(value).append("\" ").append(operation).append(" ").append(targetValues.get(index)).append(" ");
                } else {
                    formula.append("\"").append(value).append("\" ").append(operation).append(" ");
                }
            } else {
                formula.append("\"").append(value).append("\" ");
            }
        }

        return formula.toString().trim();
    }
    public FormulaDTO getFormulaWithId() {
        StringBuilder formula = new StringBuilder();

        // Helper function to extract the last part of the attribute path
        Function<String, String> extractAttributeName = attributePath -> {
            if (attributePath != null && attributePath.contains(".")) {
                return attributePath.substring(attributePath.lastIndexOf('.') + 1);
            }
            return attributePath;
        };

        // Build the source part of the formula
        for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : sourceAttributes.entrySet()) {
            Integer index = entry.getKey();
            String attributeName = extractAttributeName.apply(sourceAttributes.get(index).getAttributeToAdd());
            if (sourceOperations.containsKey(index)) {
                String operation = getOperationSymbol(sourceOperations.get(index));
                if (operation.equals("sqrt")) {
                    formula.append(operation).append("(").append(attributeName).append(") ");
                } else if (operation.equals("^")) {
                    formula.append(attributeName).append(" ").append(operation).append(" ").append(sourceValues.get(index)).append(" ");
                } else {
                    formula.append(attributeName).append(" ").append(operation).append(" ");
                }
            } else {
                formula.append(attributeName).append(" ");
            }
        }

        formula.append(" = ");

        // Build the target part of the formula
        for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : targetAttributes.entrySet()) {
            Integer index = entry.getKey();
            String attributeName = extractAttributeName.apply(targetAttributes.get(index).getAttributeToAdd());
            if (targetOperations.containsKey(index)) {
                String operation = getOperationSymbol(targetOperations.get(index));
                if (operation.equals("sqrt")) {
                    formula.append(operation).append("(").append(attributeName).append(") ");
                } else if (operation.equals("^")) {
                    formula.append(attributeName).append(" ").append(operation).append(" ").append(targetValues.get(index)).append(" ");
                } else {
                    formula.append(attributeName).append(" ").append(operation).append(" ");
                }
            } else {
                formula.append(attributeName).append(" ");
            }
        }

        return new FormulaDTO(this.id, formula.toString().trim());
    }




}
