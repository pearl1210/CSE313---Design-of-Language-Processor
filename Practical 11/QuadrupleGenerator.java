import java.util.*;

public class QuadrupleGenerator {
    enum TokenType { NUMBER, PLUS, MINUS, MULT, DIV, LPAREN, RPAREN, EOF }

    static class Token {
        TokenType type;
        String value;

        Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    static class Lexer {
        private final String input;
        private int pos = 0;
        private final int length;

        Lexer(String input) {
            this.input = input.replaceAll("\\s+", ""); // remove whitespace
            this.length = this.input.length();
        }

        boolean isAtEnd() {
            return pos >= length;
        }

        char currentChar() {
            return input.charAt(pos);
        }

        Token nextToken() {
            if (isAtEnd()) return new Token(TokenType.EOF, "");

            char c = currentChar();

            if (Character.isDigit(c)) {
                StringBuilder num = new StringBuilder();
                while (!isAtEnd() && (Character.isDigit(currentChar()) || currentChar() == '.')) {
                    num.append(currentChar());
                    pos++;
                }
                return new Token(TokenType.NUMBER, num.toString());
            } else if (c == '+') {
                pos++; return new Token(TokenType.PLUS, "+");
            } else if (c == '-') {
                pos++; return new Token(TokenType.MINUS, "-");
            } else if (c == '*') {
                pos++; return new Token(TokenType.MULT, "*");
            } else if (c == '/') {
                pos++; return new Token(TokenType.DIV, "/");
            } else if (c == '(') {
                pos++; return new Token(TokenType.LPAREN, "(");
            } else if (c == ')') {
                pos++; return new Token(TokenType.RPAREN, ")");
            }

            throw new RuntimeException("Invalid character: " + c);
        }
    }

    static class Quadruple {
        String operator, operand1, operand2, result;

        Quadruple(String operator, String operand1, String operand2, String result) {
            this.operator = operator;
            this.operand1 = operand1;
            this.operand2 = operand2;
            this.result = result;
        }

        public String toString() {
            return String.format("%-8s %-8s %-8s %-8s", operator, operand1, operand2, result);
        }
    }

    static class Parser {
        private final Lexer lexer;
        private Token currentToken;
        private final List<Quadruple> quadruples = new ArrayList<>();
        private int tempCount = 1;

        Parser(String input) {
            lexer = new Lexer(input);
            advance();
        }

        void advance() {
            currentToken = lexer.nextToken();
        }

        String newTemp() {
            return "t" + tempCount++;
        }

        void parse() {
            E(); // Start parsing from E
        }

        String E() {
            String left = T();
            while (currentToken.type == TokenType.PLUS || currentToken.type == TokenType.MINUS) {
                String op = currentToken.value;
                advance();
                String right = T();
                String temp = newTemp();
                quadruples.add(new Quadruple(op, left, right, temp));
                left = temp;
            }
            return left;
        }

        String T() {
            String left = F();
            while (currentToken.type == TokenType.MULT || currentToken.type == TokenType.DIV) {
                String op = currentToken.value;
                advance();
                String right = F();
                String temp = newTemp();
                quadruples.add(new Quadruple(op, left, right, temp));
                left = temp;
            }
            return left;
        }

        String F() {
            if (currentToken.type == TokenType.NUMBER) {
                String val = currentToken.value;
                advance();
                return val;
            } else if (currentToken.type == TokenType.LPAREN) {
                advance(); // consume '('
                String val = E();
                if (currentToken.type != TokenType.RPAREN)
                    throw new RuntimeException("Missing closing parenthesis");
                advance(); // consume ')'
                return val;
            } else {
                throw new RuntimeException("Unexpected token: " + currentToken.value);
            }
        }

        void printQuadruples() {
            System.out.printf("%-8s %-8s %-8s %-8s\n", "Operator", "Operand1", "Operand2", "Result");
            for (Quadruple q : quadruples) {
                System.out.println(q);
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter an arithmetic expression:");
        String input = scanner.nextLine();

        try {
            Parser parser = new Parser(input);
            parser.parse();
            parser.printQuadruples();
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
