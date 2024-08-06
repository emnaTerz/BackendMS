package com.emna.micro_service3.MatchingProcess;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.springframework.stereotype.Component;

import javax.script.ScriptException;

@Component
public class FormulaEvaluator {

    public boolean evaluate(String formula) throws ScriptException {
        System.out.println("Evaluating formula: " + formula);

        // Prepare the final JavaScript expression
        String jsExpression = "var result = (" + formula + "); result;";
        System.out.println("Final formula: " + jsExpression);

        try (Context context = Context.create()) {
            Value result = context.eval("js", jsExpression);
            boolean evaluationResult = result.asBoolean();
            System.out.println("Evaluation result: " + evaluationResult);
            return evaluationResult;
        } catch (Exception e) {
            System.err.println("Error evaluating formula: " + e.getMessage());
            throw new RuntimeException("Error evaluating formula: " + e.getMessage(), e);
        }
    }
}
