package com.mlt.mad_lab_project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText display;
    private String currentInput = "";
    private boolean lastInputWasOperator = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        display = findViewById(R.id.display);

        // Set click listeners for all buttons
        setButtonClickListener(R.id.btn_0);
        setButtonClickListener(R.id.btn_1);
        setButtonClickListener(R.id.btn_2);
        setButtonClickListener(R.id.btn_3);
        setButtonClickListener(R.id.btn_4);
        setButtonClickListener(R.id.btn_5);
        setButtonClickListener(R.id.btn_6);
        setButtonClickListener(R.id.btn_7);
        setButtonClickListener(R.id.btn_8);
        setButtonClickListener(R.id.btn_9);

        setButtonClickListener(R.id.btn_add);
        setButtonClickListener(R.id.btn_subtract);
        setButtonClickListener(R.id.btn_multiply);
        setButtonClickListener(R.id.btn_divide);
        setButtonClickListener(R.id.btn_pow);

        setButtonClickListener(R.id.btn_sin);
        setButtonClickListener(R.id.btn_cos);
        setButtonClickListener(R.id.btn_tan);
        setButtonClickListener(R.id.btn_log);
        setButtonClickListener(R.id.btn_ln);
        setButtonClickListener(R.id.btn_sqrt);
        setButtonClickListener(R.id.btn_fact);
        setButtonClickListener(R.id.btn_pi);
        setButtonClickListener(R.id.btn_e);

        setButtonClickListener(R.id.btn_clear);
        setButtonClickListener(R.id.btn_bracket_open);
        setButtonClickListener(R.id.btn_bracket_close);
        setButtonClickListener(R.id.btn_dot);
        setButtonClickListener(R.id.btn_delete);
        setButtonClickListener(R.id.btn_equals);
    }

    private void setButtonClickListener(int id) {
        findViewById(id).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Button button = (Button) v;
        String buttonText = button.getText().toString();

        if (id == R.id.btn_clear) {
            currentInput = "";
            display.setText("0");
            lastInputWasOperator = false;
        } else if (id == R.id.btn_delete) {
            if (currentInput.length() > 0) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
                display.setText(currentInput.isEmpty() ? "0" : currentInput);
            }
        } else if (id == R.id.btn_equals) {
            calculateResult();
        } else if (id == R.id.btn_add || id == R.id.btn_subtract ||
                id == R.id.btn_multiply || id == R.id.btn_divide ||
                id == R.id.btn_pow) {
            handleOperator(buttonText);
        } else if (id == R.id.btn_sin || id == R.id.btn_cos || id == R.id.btn_tan ||
                id == R.id.btn_log || id == R.id.btn_ln || id == R.id.btn_sqrt ||
                id == R.id.btn_fact) {
            handleFunction(buttonText);
        } else if (id == R.id.btn_pi) {
            currentInput += Math.PI;
            display.setText(currentInput);
        } else if (id == R.id.btn_e) {
            currentInput += Math.E;
            display.setText(currentInput);
        } else {
            // Handle numbers, decimal point, and brackets
            if (currentInput.equals("0") && !buttonText.equals(".")) {
                currentInput = buttonText;
            } else {
                currentInput += buttonText;
            }
            display.setText(currentInput);
            lastInputWasOperator = false;
        }
    }

    private void handleOperator(String operator) {
        if (!currentInput.isEmpty()) {
            char lastChar = currentInput.charAt(currentInput.length() - 1);

            // Replace the last operator if the last input was an operator
            if (lastInputWasOperator && isOperator(lastChar)) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
            }

            currentInput += operator;
            display.setText(currentInput);
            lastInputWasOperator = true;
        }
    }

    private void handleFunction(String function) {
        if (currentInput.isEmpty() || lastInputWasOperator) {
            // Apply function to the last number or start a new function
            currentInput += function + "(";
        } else {
            // Extract the last number and apply function to it
            try {
                String[] parts = currentInput.split("[+\\-*/^]");
                String lastNumber = parts[parts.length - 1];

                // Remove the last number from currentInput
                currentInput = currentInput.substring(0, currentInput.length() - lastNumber.length());

                // Apply the function
                currentInput += function + "(" + lastNumber + ")";
            } catch (Exception e) {
                currentInput = function + "(" + currentInput + ")";
            }
        }
        display.setText(currentInput);
        lastInputWasOperator = false;
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    private void calculateResult() {
        try {
            // Replace × with * for evaluation
            String expression = currentInput.replace("×", "*");

            // Evaluate the expression
            double result = evaluateExpression(expression);

            // Display the result
            if (result == (long) result) {
                display.setText(String.format("%d", (long) result));
            } else {
                display.setText(String.format("%s", result));
            }

            currentInput = display.getText().toString();
            lastInputWasOperator = false;
        } catch (Exception e) {
            display.setText("Error");
            currentInput = "";
        }
    }

    private double evaluateExpression(String expression) {
        // Handle parentheses first
        while (expression.contains("(")) {
            int openIndex = expression.lastIndexOf("(");
            int closeIndex = expression.indexOf(")", openIndex);

            if (closeIndex == -1) {
                throw new RuntimeException("Mismatched parentheses");
            }

            String subExpr = expression.substring(openIndex + 1, closeIndex);
            double subResult = evaluateSimpleExpression(subExpr);

            // Check if this is a function call
            if (openIndex > 0 && Character.isLetter(expression.charAt(openIndex - 1))) {
                int funcStart = openIndex - 1;
                while (funcStart >= 0 && Character.isLetter(expression.charAt(funcStart))) {
                    funcStart--;
                }
                funcStart++;

                String funcName = expression.substring(funcStart, openIndex);
                subResult = applyFunction(funcName, subResult);
                expression = expression.substring(0, funcStart) + subResult + expression.substring(closeIndex + 1);
            } else {
                expression = expression.substring(0, openIndex) + subResult + expression.substring(closeIndex + 1);
            }
        }

        return evaluateSimpleExpression(expression);
    }

    private double evaluateSimpleExpression(String expression) {
        // Handle exponents first
        String[] terms = expression.split("\\^");
        if (terms.length > 1) {
            double result = Double.parseDouble(terms[terms.length - 1]);
            for (int i = terms.length - 2; i >= 0; i--) {
                result = Math.pow(Double.parseDouble(terms[i]), result);
            }
            return result;
        }

        // Handle multiplication and division
        terms = expression.split("[*/]");
        if (terms.length > 1) {
            double result = Double.parseDouble(terms[0]);
            int termIndex = 1;

            for (char c : expression.toCharArray()) {
                if (c == '*' || c == '/') {
                    double nextTerm = Double.parseDouble(terms[termIndex++]);
                    if (c == '*') {
                        result *= nextTerm;
                    } else {
                        if (nextTerm == 0) {
                            throw new RuntimeException("Division by zero");
                        }
                        result /= nextTerm;
                    }
                }
            }
            return result;
        }

        // Handle addition and subtraction
        terms = expression.split("[+-]");
        if (terms.length > 1) {
            double result = Double.parseDouble(terms[0]);
            int termIndex = 1;

            for (char c : expression.toCharArray()) {
                if (c == '+' || c == '-') {
                    double nextTerm = Double.parseDouble(terms[termIndex++]);
                    if (c == '+') {
                        result += nextTerm;
                    } else {
                        result -= nextTerm;
                    }
                }
            }
            return result;
        }

        // If no operators, just return the number
        return Double.parseDouble(expression);
    }

    private double applyFunction(String functionName, double value) {
        switch (functionName) {
            case "sin":
                return Math.sin(Math.toRadians(value));
            case "cos":
                return Math.cos(Math.toRadians(value));
            case "tan":
                return Math.tan(Math.toRadians(value));
            case "log":
                return Math.log10(value);
            case "ln":
                return Math.log(value);
            case "sqrt":
                return Math.sqrt(value);
            case "fact":
                return factorial((int) value);
            default:
                throw new RuntimeException("Unknown function: " + functionName);
        }
    }

    private double factorial(int n) {
        if (n < 0) {
            throw new RuntimeException("Factorial of negative number");
        }
        double result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}