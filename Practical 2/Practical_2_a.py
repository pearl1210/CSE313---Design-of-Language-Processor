def validate_string(input_string):
    # DFA states
    state = "q0"
    
    for char in input_string:
        if state == "q0":
            if char == "0":
                state = "q1"
            elif char == "1":
                state = "q0"
            else:
                return "Invalid String"
        elif state == "q1":
            if char == "1":
                state = "q2"
            else:
                return "Invalid String"
        elif state == "q2":
            if char == "1":
                state = "q3"
            else:
                return "Invalid String"
        elif state == "q3":
            if char == "0":
                state = "q1"
            elif char == "1":
                state = "q0"
            else:
                return "Invalid String"
        else:
            return "Invalid String"
    
    # Acceptable final states
    if state in {"q0", "q3"}:
        return "Valid String"
    else:
        return "Invalid String"

# Input string
input_string = input("Enter a string over {0, 1}: ")
result = validate_string(input_string)
print(result)
