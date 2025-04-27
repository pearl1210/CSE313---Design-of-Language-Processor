import java.util.*;

public class BottomUpEvaluator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter arithmetic expression:");
        String input = scanner.nextLine();

        try {
            List<String> rpn = infixToRPN(input);
            double result = evaluateRPN(rpn);
            System.out.println("Result: " + result);
        } catch (Exception e) {
            System.out.println("Invalid expression");
        }
    }

    // Convert infix to postfix (RPN) using Shunting Yard Algorithm
    public static List<String> infixToRPN(String expression) throws Exception {
        List<String> output = new ArrayList<>();
        Stack<String> operatorStack = new Stack<>();
        StringTokenizer tokenizer = new StringTokenizer(expression, "+-*/^() ", true);

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token.isEmpty()) continue;

            if (isNumber(token)) {
                output.add(token);
            } else if (isOperator(token)) {
                while (!operatorStack.isEmpty() &&
                        isOperator(operatorStack.peek()) &&
                        ((isLeftAssociative(token) && precedence(token) <= precedence(operatorStack.peek())) ||
                         (!isLeftAssociative(token) && precedence(token) < precedence(operatorStack.peek())))) {
                    output.add(operatorStack.pop());
                }
                operatorStack.push(token);
            } else if (token.equals("(")) {
                operatorStack.push(token);
            } else if (token.equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    output.add(operatorStack.pop());
                }
                if (operatorStack.isEmpty() || !operatorStack.pop().equals("(")) {
                    throw new Exception("Mismatched parentheses");
                }
            } else {
                throw new Exception("Invalid token: " + token);
            }
        }

        while (!operatorStack.isEmpty()) {
            String op = operatorStack.pop();
            if (op.equals("(") || op.equals(")")) throw new Exception("Mismatched parentheses");
            output.add(op);
        }

        return output;
    }

    // Evaluate the RPN expression
    public static double evaluateRPN(List<String> tokens) throws Exception {
        Stack<Double> stack = new Stack<>();

        for (String token : tokens) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else if (isOperator(token)) {
                if (stack.size() < 2) throw new Exception("Not enough operands");
                double b = stack.pop();
                double a = stack.pop();
                switch (token) {
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/": stack.push(a / b); break;
                    case "^": stack.push(Math.pow(a, b)); break;
                    default: throw new Exception("Unknown operator");
                }
            } else {
                throw new Exception("Invalid token in RPN");
            }
        }

        if (stack.size() != 1) throw new Exception("Too many operands");
        return stack.pop();
    }

    // Utility functions
    private static boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isOperator(String token) {
        return "+-*/^".contains(token);
    }

    private static int precedence(String op) {
        return switch (op) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            case "^" -> 3;
            default -> 0;
        };
    }

    private static boolean isLeftAssociative(String op) {
        return !op.equals("^"); // ^ is right-associative
    }
}
