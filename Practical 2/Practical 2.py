def main():
    # Input: number of input symbols
    num_symbols = int(input("Enter number of input symbols: "))

    # Input: input symbols
    symbols = []
    for i in range(num_symbols):
        symbol = input(f"Enter symbol {i + 1}: ")
        symbols.append(symbol)

    # Input: number of states
    num_states = int(input("Enter number of states: "))

    # Input: initial state
    initial_state = int(input(f"Enter initial state (1 to {num_states}): "))

    # Input: number of final states
    num_final_states = int(input("Enter number of final states: "))

    # Input: final states
    final_states = set()
    for i in range(num_final_states):
        final_state = int(input(f"Enter final state {i + 1} (1 to {num_states}): "))
        final_states.add(final_state)

    # Input: transition table
    transition_table = []
    print("Define the transition table:")
    for i in range(num_states):
        row = []
        for j in range(num_symbols):
            state = int(input(f"From state {i + 1} with input '{symbols[j]}', go to state (1 to {num_states}): "))
            row.append(state)
        transition_table.append(row)

    # Input: string to validate
    input_string = input("Enter the input string: ")

    # Process input string
    current_state = initial_state
    is_valid = True

    for ch in input_string:
        current_symbol = str(ch)
        if current_symbol not in symbols:
            print(f"Invalid input symbol encountered: {current_symbol}")
            is_valid = False
            break

        symbol_index = symbols.index(current_symbol)
        current_state = transition_table[current_state - 1][symbol_index]

    # Check if the string halts in a final state
    if is_valid and current_state in final_states:
        print("Valid String")
    else:
        print("Invalid String")


if __name__ == "__main__":
    main()
