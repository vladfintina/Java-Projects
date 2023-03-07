import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ExpressionEvaluator {
    private static Map<String, Double> variables = new HashMap<String, Double>();
    public static Double last = (double) 0;

    public static double useOperand(char operator, double y, double x) {
        switch (operator) {
            case '+':
                return x + y;
            case '-':
                return x - y;
            case '*':
                return x * y;
            case '/':
                if (y == 0)
                    throw new
                            UnsupportedOperationException(
                            "Cannot divide by zero");
                return x / y;
        }
        return 0;
    }

    public static boolean precedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')')
            return false;
        return (op1 != '*' && op1 != '/') ||
                (op2 != '+' && op2 != '-');
    }
    public static double evaluateExpression(String expression) throws Exception {
        char[] myTokens = expression.toCharArray();

        Stack<Double> values = new Stack<>();

        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < myTokens.length; i++) {

            if (myTokens[i] == ' ')
                continue;

            if (myTokens[i] >= '0' && myTokens[i] <= '9') {
                StringBuilder stringBuffer = new StringBuilder();

                while (i < myTokens.length && ((myTokens[i] >= '0' && myTokens[i] <= '9') || myTokens[i] == 'e' || myTokens[i] == '.'))
                    stringBuffer.append(myTokens[i++]);
                values.push(Double.parseDouble(stringBuffer.toString()));

                i--;
            } else if (Character.isLetter(expression.charAt(i))) {
                StringBuilder variable = new StringBuilder();
                while (i < expression.length() && Character.isLetter(expression.charAt(i))) {
                    variable.append(expression.charAt(i));
                    i++;
                }

                if (i < expression.length() && expression.charAt(i) != ' ' && expression.charAt(i) != '+'
                        && expression.charAt(i) != '-' && expression.charAt(i) != '*' && expression.charAt(i) != '/') {
                    throw new Exception();
                }
                String variable2 = variable.toString();
                if (variable2.equals("last")) {
                    values.push(last);
                } else if (variables.containsKey(variable2))
                    values.push(variables.get(variable2));
                else values.push((double) 0);
                i--;
            } else if (myTokens[i] == '(')
                operators.push(myTokens[i]);

            else if (myTokens[i] == ')') {
                while (operators.peek() != '(')
                    values.push(useOperand(operators.pop(), values.pop(), values.pop()));
                operators.pop();
            } else if (myTokens[i] == '+' || myTokens[i] == '-' || myTokens[i] == '*' || myTokens[i] == '/') {
                while (!operators.empty() && precedence(myTokens[i], operators.peek())) {
                    char op = operators.pop();
                    if (op == ')' || op == '(') {
                        throw new Exception();
                    }
                    values.push(useOperand(op, values.pop(), values.pop()));
                }

                if ((values.empty() || expression.charAt(i - 1) == '(') && expression.charAt(i) == '-') {
                    values.push((double) 0);
                }

                operators.push(myTokens[i]);
            }
        }

        while (!operators.empty()) {
            char op = operators.pop();
            if (op == ')' || op == '(') {
                throw new Exception();
            }
            double val1, val2;
            val2 = values.pop();
            //if (op == '-' && values.empty())
            // val1 = 0;
            //else
            val1 = values.pop();
            values.push(useOperand(op, val2, val1));
        }

        if (values.size() != 1) {
            throw new Exception();
        }

        return values.pop();
    }

    public static double calculateExpression(String expression) throws Exception {
        StringBuilder variable = new StringBuilder();
        while(expression.charAt(0) == ' '){
            expression = expression.substring(1);
        }
        if (expression.indexOf('=') != -1) {
            int i = 0;
            while (i < expression.length() && Character.isLetter(expression.charAt(i))) {
                variable.append(expression.charAt(i));
                i++;
            }
            if (variable.isEmpty()) {
                throw new Exception();
            }
            while (expression.charAt(i) == ' ')
                i++;
            if (expression.charAt(i) != '=') {
                throw new Exception();
            }
            expression = expression.substring(i + 1);
        }
        double nr = evaluateExpression(expression);
        if (!variable.isEmpty()) {
            variables.put(variable.toString(), nr);
        }
        last = nr;
        return nr;
    }

}


