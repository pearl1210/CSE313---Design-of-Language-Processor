def validate_dfa():
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

    # Input: number of accepting states
    num_accepting_states = int(input("Enter number of accepting states: "))

    # Input: accepting states
    accepting_states = set()
    for i in range(num_accepting_states):
        state = int(input(f"Enter accepting state {i + 1} (1 to {num_states}): "))
        accepting_states.add(state)

    # Input: transition table
    transition_table = {}
    print("Define the transition table:")
    for state in range(1, num_states + 1):
        transition_table[state] = {}
        for symbol in symbols:
            next_state = int(input(f"From state {state} with input '{symbol}', go to state (1 to {num_states}): "))
            transition_table[state][symbol] = next_state

    # Input: string to validate
    input_string = input("Enter the input string: ")

    # Process the input string using the DFA
    current_state = initial_state
    if input_string and input_string[0].isalpha():  # Check if the string starts with an alphabet
        for char in input_string:
            if char not in symbols:
                print(f"Invalid input symbol encountered: {char}")
                return "Invalid String"
            current_state = transition_table[current_state][char]
        # Check if the current state is an accepting state
        if current_state in accepting_states:
            return "Valid String"
    else:
        return "Invalid String: Must start with an alphabet"

    return "Invalid String"


# Execute the DFA validation
result = validate_dfa()
print(result)
