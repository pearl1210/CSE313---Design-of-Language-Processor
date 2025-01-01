def validate_dfa(input_string, num_states, initial_state, accepting_states, transition_table, symbols):
    current_state = initial_state

    for char in input_string:
        if char not in symbols:
            return "Invalid String: Contains undefined symbols."
        symbol_index = symbols.index(char)
        current_state = transition_table[current_state][symbol_index]

    return "Valid String" if current_state in accepting_states else "Invalid String"

def main():
    # Input: Number of input symbols
    num_symbols = int(input("Enter the number of input symbols: "))
    symbols = []
    for i in range(num_symbols):
        symbols.append(input(f"Enter symbol {i + 1}: "))

    # Input: Number of states
    num_states = int(input("Enter the number of states: "))
    
    # Input: Initial state
    initial_state = int(input("Enter the initial state (0 to {}): ".format(num_states - 1)))

    # Input: Number of accepting states
    num_accepting_states = int(input("Enter the number of accepting states: "))
    accepting_states = set()
    for i in range(num_accepting_states):
        state = int(input(f"Enter accepting state {i + 1} (0 to {num_states - 1}): "))
        accepting_states.add(state)

    # Input: Transition table
    transition_table = []
    print("\nDefine the transition table:")
    for i in range(num_states):
        row = []
        for j in range(num_symbols):
            next_state = int(input(f"From state {i} with input '{symbols[j]}', go to state (0 to {num_states - 1}): "))
            row.append(next_state)
        transition_table.append(row)

    # Input: String to validate
    input_string = input("\nEnter the input string: ")

    # Validate the string
    result = validate_dfa(input_string, num_states, initial_state, accepting_states, transition_table, symbols)
    print(result)

if __name__ == "__main__":
    main()
