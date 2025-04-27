import java.util.*;

public class PP {
    static Map<String, List<String>> grammar = new HashMap<>();
    static Map<String, Set<String>> firstSets = new HashMap<>();
    static Map<String, Set<String>> followSets = new HashMap<>();
    static Map<String, Map<String, Set<String>>> parsingTable = new HashMap<>();

    public static void main(String[] args) {
        // Step 1: Define the Grammar
        grammar.put("S", Arrays.asList("A B C", "D"));
        grammar.put("A", Arrays.asList("a", "ε"));
        grammar.put("B", Arrays.asList("b", "ε"));
        grammar.put("C", Arrays.asList("( S )", "c"));
        grammar.put("D", Arrays.asList("A C"));

        // Step 2: Compute First and Follow Sets
        for (String nonTerminal : grammar.keySet()) {
            firstSets.put(nonTerminal, computeFirst(nonTerminal));
        }
        for (String nonTerminal : grammar.keySet()) {
            followSets.put(nonTerminal, computeFollow(nonTerminal));
        }

        // Step 3: Construct Predictive Parsing Table
        constructParsingTable();

        // Step 4: Check if the Grammar is LL(1)
        boolean isLL1 = checkLL1Grammar();
        System.out.println("\nGrammar is " + (isLL1 ? "LL(1)" : "Not LL(1)"));

        // Step 5: Validate an Input String
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nEnter a string to validate: ");
        String input = scanner.nextLine();
        boolean isValid = validateString(input);
        System.out.println(isValid ? "Valid string" : "Invalid string");
    }

    // Compute First Set
    private static Set<String> computeFirst(String symbol) {
        if (firstSets.containsKey(symbol)) return firstSets.get(symbol);
        Set<String> first = new HashSet<>();

        if (!grammar.containsKey(symbol)) {
            first.add(symbol);
            return first;
        }

        for (String production : grammar.get(symbol)) {
            String[] tokens = production.split(" ");
            for (String token : tokens) {
                Set<String> tokenFirst = computeFirst(token);
                first.addAll(tokenFirst);
                first.remove("ε");

                if (!tokenFirst.contains("ε")) break;
            }
            if (Arrays.stream(tokens).allMatch(t -> computeFirst(t).contains("ε"))) {
                first.add("ε");
            }
        }

        firstSets.put(symbol, first);
        return first;
    }

    // Compute Follow Set
    private static Set<String> computeFollow(String symbol) {
        if (followSets.containsKey(symbol)) return followSets.get(symbol);
        Set<String> follow = new HashSet<>();

        if (symbol.equals("S")) follow.add("$");

        for (String lhs : grammar.keySet()) {
            for (String production : grammar.get(lhs)) {
                String[] tokens = production.split(" ");
                for (int i = 0; i < tokens.length; i++) {
                    if (tokens[i].equals(symbol)) {
                        int nextIndex = i + 1;
                        while (nextIndex < tokens.length) {
                            Set<String> nextFirst = computeFirst(tokens[nextIndex]);
                            follow.addAll(nextFirst);
                            follow.remove("ε");
                            if (!nextFirst.contains("ε")) break;
                            nextIndex++;
                        }
                        if (nextIndex == tokens.length) {
                            if (!lhs.equals(symbol)) follow.addAll(computeFollow(lhs));
                        }
                    }
                }
            }
        }

        followSets.put(symbol, follow);
        return follow;
    }

    // Construct Predictive Parsing Table
    private static void constructParsingTable() {
        for (String nonTerminal : grammar.keySet()) {
            parsingTable.put(nonTerminal, new HashMap<>());

            for (String production : grammar.get(nonTerminal)) {
                Set<String> firstOfProduction = getFirstOfProduction(production);

                for (String terminal : firstOfProduction) {
                    if (!terminal.equals("ε")) {
                        parsingTable.get(nonTerminal).computeIfAbsent(terminal, k -> new HashSet<>()).add(production);
                    }
                }

                if (firstOfProduction.contains("ε")) {
                    for (String terminal : followSets.get(nonTerminal)) {
                        parsingTable.get(nonTerminal).computeIfAbsent(terminal, k -> new HashSet<>()).add(production);
                    }
                }
            }
        }

        // Print the parsing table
        System.out.println("Predictive Parsing Table:");
        for (String nonTerminal : parsingTable.keySet()) {
            for (String terminal : parsingTable.get(nonTerminal).keySet()) {
                Set<String> rules = parsingTable.get(nonTerminal).get(terminal);
                System.out.println(nonTerminal + ", " + terminal + " : " + String.join(" , ", rules));
            }
        }
    }

    // Get First Set of a production rule
    private static Set<String> getFirstOfProduction(String production) {
        Set<String> firstSet = new HashSet<>();
        String[] tokens = production.split(" ");
        for (String token : tokens) {
            Set<String> tokenFirst = computeFirst(token);
            firstSet.addAll(tokenFirst);
            if (!tokenFirst.contains("ε")) break;
        }
        return firstSet;
    }

    // Check if Grammar is LL(1)
    private static boolean checkLL1Grammar() {
        boolean isLL1 = true;
        for (String nonTerminal : parsingTable.keySet()) {
            for (String terminal : parsingTable.get(nonTerminal).keySet()) {
                Set<String> rules = parsingTable.get(nonTerminal).get(terminal);
                if (rules.size() > 1) {
                    System.out.println("Conflict detected for " + nonTerminal + ", " + terminal + " : " + String.join(" , ", rules));
                    isLL1 = false;
                }
            }
        }
        return isLL1;
    }

    // **Validate Input String using Predictive Parsing**
    private static boolean validateString(String input) {
        Stack<String> stack = new Stack<>();
        stack.push("$");
        stack.push("S");

        String[] tokens = (input + " $").split(" ");
        int index = 0;

        while (!stack.isEmpty()) {
            String top = stack.pop();
            String currentToken = tokens[index];

            if (top.equals(currentToken)) {
                index++;
            } else if (parsingTable.containsKey(top) && parsingTable.get(top).containsKey(currentToken)) {
                Set<String> rules = parsingTable.get(top).get(currentToken);
                if (rules.size() > 1) {
                    System.out.println("Parsing error: Ambiguous grammar detected!");
                    return false;
                }
                String[] rule = rules.iterator().next().split(" ");
                for (int i = rule.length - 1; i >= 0; i--) {
                    if (!rule[i].equals("ε")) stack.push(rule[i]);
                }
            } else {
                return false; // Parsing error
            }
        }

        return index == tokens.length;
    }
}
