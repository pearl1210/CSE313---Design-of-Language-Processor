import java.util.*;

public class FirstFollow {
    static Map<String, List<String>> grammar = new HashMap<>();
    static Map<String, Set<String>> firstSets = new HashMap<>();
    static Map<String, Set<String>> followSets = new HashMap<>();

    public static void main(String[] args) {
        // Define the grammar
        // S  -> A B C | D  
        // A  -> a | ε  
        // B  -> b | ε  
        // C  -> ( S ) | c  
        // D  -> A C  

        grammar.put("S", Arrays.asList("A B C", "D"));
        grammar.put("A", Arrays.asList("a", "ε"));
        grammar.put("B", Arrays.asList("b", "ε"));
        grammar.put("C", Arrays.asList("( S )", "c"));
        grammar.put("D", Arrays.asList("A C"));

        // Compute First sets
        for (String nonTerminal : grammar.keySet()) {
            firstSets.put(nonTerminal, computeFirst(nonTerminal));
        }

        // Compute Follow sets
        for (String nonTerminal : grammar.keySet()) {
            followSets.put(nonTerminal, computeFollow(nonTerminal));
        }

        // Print First sets
        System.out.println("First Sets:");
        for (String nonTerminal : firstSets.keySet()) {
            System.out.println("First(" + nonTerminal + ") = " + firstSets.get(nonTerminal));
        }

        // Print Follow sets
        System.out.println("\nFollow Sets:");
        for (String nonTerminal : followSets.keySet()) {
            System.out.println("Follow(" + nonTerminal + ") = " + followSets.get(nonTerminal));
        }
    }

    // Compute First set for a given symbol
    private static Set<String> computeFirst(String symbol) {
        if (firstSets.containsKey(symbol)) {
            return firstSets.get(symbol);
        }

        Set<String> first = new HashSet<>();

        // If the symbol is a terminal, return itself
        if (!grammar.containsKey(symbol)) {
            first.add(symbol);
            return first;
        }

        // Process each production rule
        for (String production : grammar.get(symbol)) {
            String[] tokens = production.split(" ");
            for (String token : tokens) {
                Set<String> tokenFirst = computeFirst(token);
                first.addAll(tokenFirst);
                first.remove("ε"); // Remove epsilon initially

                if (!tokenFirst.contains("ε")) {
                    break;
                }
            }

            // If all symbols in production have ε, then add ε
            if (Arrays.stream(tokens).allMatch(t -> computeFirst(t).contains("ε"))) {
                first.add("ε");
            }
        }

        firstSets.put(symbol, first);
        return first;
    }

    // Compute Follow set for a given symbol
    private static Set<String> computeFollow(String symbol) {
        if (followSets.containsKey(symbol)) {
            return followSets.get(symbol);
        }

        Set<String> follow = new HashSet<>();

        // Start symbol rule
        if (symbol.equals("S")) {
            follow.add("$");
        }

        // Iterate over the grammar
        for (String lhs : grammar.keySet()) {
            for (String production : grammar.get(lhs)) {
                String[] tokens = production.split(" ");
                for (int i = 0; i < tokens.length; i++) {
                    if (tokens[i].equals(symbol)) {
                        int nextIndex = i + 1;

                        // Case 1: Symbol is followed by another symbol
                        while (nextIndex < tokens.length) {
                            Set<String> nextFirst = computeFirst(tokens[nextIndex]);
                            follow.addAll(nextFirst);
                            follow.remove("ε");

                            if (!nextFirst.contains("ε")) {
                                break;
                            }
                            nextIndex++;
                        }

                        // Case 2: Symbol is at the end or followed by ε
                        if (nextIndex == tokens.length) {
                            if (!lhs.equals(symbol)) {
                                follow.addAll(computeFollow(lhs));
                            }
                        }
                    }
                }
            }
        }

        followSets.put(symbol, follow);
        return follow;
    }
}