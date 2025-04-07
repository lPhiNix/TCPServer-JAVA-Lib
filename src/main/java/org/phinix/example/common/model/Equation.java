package org.phinix.example.common.model;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.HashMap;
import java.util.Map;

public class Equation {
    private final String mathExpression;
    private final Expression expression;

    private static final String VARIABLE = "x";

    private static final Map<String, Double> validConstants = new HashMap<>();
    static {
        validConstants.put("π", Math.PI);
        validConstants.put("pi", Math.PI);
        validConstants.put("PI", Math.PI);
        validConstants.put("e", Math.E);
        validConstants.put("E", Math.E);
    }

    public Equation(String mathExpression) {
        this.mathExpression = mathExpression;
        this.expression = buildExpression(mathExpression);
    }

    public String getMathExpression() {
        return mathExpression;
    }

    public boolean isValid(Expression expression) {
        try {
            expression.setVariable(VARIABLE, 1.0);
            expression.evaluate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Expression buildExpression(String expr) {
        ExpressionBuilder builder = new ExpressionBuilder(expr)
                .variable(VARIABLE);

        for (String constant : validConstants.keySet()) {
            if (expr.contains(constant) && !constant.equalsIgnoreCase(VARIABLE)) {
                builder.variable(constant);
            }
        }

        Expression built = builder.build();

        for (Map.Entry<String, Double> entry : validConstants.entrySet()) {
            String name = entry.getKey();
            if (!name.equalsIgnoreCase(VARIABLE)) {
                built.setVariable(name, entry.getValue());
            }
        }

        return built;
    }

    private double evaluateIn(double x) {
        return expression.setVariable(VARIABLE, x).evaluate();
    }

    public boolean tryGuessRoot(String mathExpression) {
        Expression resultExpression = buildExpression(mathExpression);

        if (!isValid(expression)) {
            return false;
        }

        double result = resultExpression.evaluate();

        return evaluateIn(result) == 0;
    }
}
