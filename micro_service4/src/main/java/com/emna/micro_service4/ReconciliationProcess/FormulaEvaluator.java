package com.emna.micro_service4.ReconciliationProcess;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.springframework.stereotype.Component;

import javax.script.ScriptException;

@Component
public class FormulaEvaluator {

    public boolean evaluateWithTolerance(String formula, String toleranceStr) throws ScriptException {
        double tolerance = Double.parseDouble(toleranceStr);

        // Prepare the final JavaScript expression
        String jsExpression = buildJsExpression(formula);
        System.out.println("Evaluating JavaScript expression: " + jsExpression);

        try (Context context = Context.create()) {
            Value result = context.eval("js", jsExpression);

            // Handle different result types (number, boolean)
            if (result.isNumber()) {
                double evaluationResult = result.asDouble();
                return Math.abs(evaluationResult) <= tolerance;
            } else if (result.isBoolean()) {
                // If result is a boolean, handle accordingly
                return result.asBoolean();
            } else {
                throw new RuntimeException("Unexpected result type: " + result);
            }
        } catch (Exception e) {
            System.err.println("Error evaluating formula with tolerance: " + e.getMessage());
            throw new RuntimeException("Error evaluating formula with tolerance: " + e.getMessage(), e);
        }
    }

    private String buildJsExpression(String formula) {
        // Replace power operation with JavaScript's Math.pow function
        formula = formula.replaceAll("\\^", ",");
        formula = formula.replaceAll("power\\((.*),(.*)\\)", "Math.pow($1,$2)");

        // Replace square root operation with JavaScript's Math.sqrt function
        formula = formula.replaceAll("sqrt\\((.*)\\)", "Math.sqrt($1)");

        // Convert strings to numbers if needed
        formula = formula.replaceAll("\"([0-9\\.]+)\"", "$1");

        return "var result = (" + formula + "); result;";
    }



}

