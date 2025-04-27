import java.util.*;
import java.util.regex.*;

public class ConstantFoldingOptimizer {

    // ========== Token Types ==========
    enum TokenType { NUMBER, VARIABLE, PLUS, MINUS, MULT, DIV, LPAREN, RPAREN, EOF }

    static class Token {
        TokenType type;
        String value;
        Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    // ========== Lexer ==========
    static class Lexer {
        private final String input;
        private int pos = 0;
        private final int length;

        Lexer(String input) {
            this.input = input.replaceAll("\\s+", "");
            this.length = this.input.length();
        }

        boolean isAtEnd() {
            return pos >= length;
        }

        char peek() {
            return input.charAt(pos);
        }

        Token nextToken() {
            if (isAtEnd()) return new Token(TokenType.EOF, "");

            char c = peek();

            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                while (!isAtEnd() && (Character.isDigit(peek()) || peek() == '.')) {
                    sb.append(peek());
                    pos++;
                }
                return new Token(TokenType.NUMBER, sb.toString());
            }

            if (Character.isLetter(c)) {
                StringBuilder sb = new StringBuilder();
                while (!isAtEnd() && Character.isLetterOrDigit(peek())) {
                    sb.append(peek());
                    pos++;
                }
                return new Token(TokenType.VARIABLE, sb.toString());
            }

            switch (c) {
                case '+': pos++; return new Token(TokenType.PLUS, "+");
                case '-': pos++; return new Token(TokenType.MINUS, "-");
                case '*': pos++; return new Token(TokenType.MULT, "*");
                case '/': pos++; return new Token(TokenType.DIV, "/");
                case '(': pos++; return new Token(TokenType.LPAREN, "(");
                case ')': pos++; return new Token(TokenType.RPAREN, ")");
                default: throw new RuntimeException("Unexpected character: " + c);
            }
        }
    }

    // ========== AST Node ==========
    abstract static class ASTNode {
        abstract boolean isConstant();
        abstract double evaluate(); // Only for constant nodes
        abstract String toExpression(); // Reconstruct the expression
    }

    static class NumberNode extends ASTNode {
        double value;
        NumberNode(double value) { this.value = value; }
        boolean isConstant() { return true; }
        double evaluate() { return value; }
        String toExpression() { return String.valueOf(value); }
    }

    static class VariableNode extends ASTNode {
        String name;
        VariableNode(String name) { this.name = name; }
        boolean isConstant() { return false; }
        double evaluate() { throw new UnsupportedOperationException(); }
        String toExpression() { return name; }
    }

    static class BinaryOpNode extends ASTNode {
        String op;
        ASTNode left, right;

        BinaryOpNode(String op, ASTNode left, ASTNode right) {
            this.op = op;
            this.left = left;
            this.right = right;
        }

        boolean isConstant() {
            return left.isConstant() && right.isConstant();
        }

        double evaluate() {
            double l = left.evaluate();
            double r = right.evaluate();
            return switch (op) {
                case "+" -> l + r;
                case "-" -> l - r;
                case "*" -> l * r;
                case "/" -> l / r;
                default -> throw new RuntimeException("Unknown operator: " + op);
            };
        }

        String toExpression() {
            return "(" + left.toExpression() + " " + op + " " + right.toExpression() + ")";
        }
    }

    // ========== Parser ==========
    static class Parser {
        private final Lexer lexer;
        private Token currentToken;

        Parser(String input) {
            this.lexer = new Lexer(input);
            advance();
        }

        void advance() {
            currentToken = lexer.nextToken();
        }

        ASTNode parse() {
            return expr();
        }

        ASTNode expr() {
            ASTNode node = term();
            while (currentToken.type == TokenType.PLUS || currentToken.type == TokenType.MINUS) {
                String op = currentToken.value;
                advance();
                ASTNode right = term();
                node = new BinaryOpNode(op, node, right);
            }
            return node;
        }

        ASTNode term() {
            ASTNode node = factor();
            while (currentToken.type == TokenType.MULT || currentToken.type == TokenType.DIV) {
                String op = currentToken.value;
                advance();
                ASTNode right = factor();
                node = new BinaryOpNode(op, node, right);
            }
            return node;
        }

        ASTNode factor() {
            if (currentToken.type == TokenType.NUMBER) {
                double val = Double.parseDouble(currentToken.value);
                advance();
                return new NumberNode(val);
            } else if (currentToken.type == TokenType.VARIABLE) {
                String name = currentToken.value;
                advance();
                return new VariableNode(name);
            } else if (currentToken.type == TokenType.LPAREN) {
                advance();
                ASTNode node = expr();
                if (currentToken.type != TokenType.RPAREN) {
                    throw new RuntimeException("Missing closing parenthesis");
                }
                advance();
                return node;
            } else {
                throw new RuntimeException("Unexpected token: " + currentToken.value);
            }
        }
    }

    // ========== Constant Folding ==========
    static ASTNode foldConstants(ASTNode node) {
        if (node instanceof BinaryOpNode opNode) {
            opNode.left = foldConstants(opNode.left);
            opNode.right = foldConstants(opNode.right);
            if (opNode.isConstant()) {
                return new NumberNode(opNode.evaluate());
            }
        }
        return node;
    }

    // ========== Main ==========
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter an arithmetic expression:");
        String input = scanner.nextLine();

        try {
            Parser parser = new Parser(input);
            ASTNode ast = parser.parse();
            ASTNode optimized = foldConstants(ast);
            String optimizedExpression = optimized.toExpression();

            // Optional: remove extra parentheses
            optimizedExpression = removeRedundantParentheses(optimizedExpression);
            System.out.println("Optimized expression: " + optimizedExpression);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Helper: Optional cleaner
    static String removeRedundantParentheses(String expr) {
        // Simplified, not full-proof: Remove outermost parentheses if safe
        while (expr.startsWith("(") && expr.endsWith(")")) {
            expr = expr.substring(1, expr.length() - 1);
        }
        return expr.replaceAll("\\(([^()]+)\\)", "$1");
    }
}
