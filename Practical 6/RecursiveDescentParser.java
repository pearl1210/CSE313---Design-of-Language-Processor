import java.util.Arrays;

public class RecursiveDescentParser {
    private static int i = 0; // Index for parsing the input string
    private static String input; // Input string to be validated

    // Recursive Descent Parser Functions

    // S → ( L ) | a
    private static boolean S() {
        if (i < input.length() && input.charAt(i) == 'a') {
            i++; // Consume 'a'
            return true;
        } else if (i < input.length() && input.charAt(i) == '(') {
            i++; // Consume '('
            if (L()) {
                if (i < input.length() && input.charAt(i) == ')') {
                    i++; // Consume ')'
                    return true;
                }
            }
        }
        return false;
    }

    // L → S L'
    private static boolean L() {
        if (S()) {
            return LPrime();
        }
        return false;
    }

    // L' → , S L' | ε
    private static boolean LPrime() {
        if (i < input.length() && input.charAt(i) == ',') {
            i++; // Consume ','
            if (S()) {
                return LPrime();
            } else {
                return false;
            }
        }
        return true; // Epsilon (ε) transition
    }

    // Function to validate the input string
    private static void validate(String str) {
        input = str;
        i = 0; // Reset index

        if (S() && i == input.length()) {
            System.out.println("Valid string");
        } else {
            System.out.println("Invalid string");
        }
    }

    // Main function to test the parser
    public static void main(String[] args) {
        String[] testCases = {
            "a", "(a)", "(a,a)", "(a,(a,a),a)", "(a,a),(a,a)", "a)", "(a", "a,a", "a,", "(a,a),a"
        };

        for (String test : testCases) {
            System.out.print("ip: " + test + " -> ");
            validate(test);
        }
    }
}
