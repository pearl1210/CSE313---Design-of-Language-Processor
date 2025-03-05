class LexicalAnalyzer:
    def __init__(self):
        # Define C language keywords
        self.keywords = {
            'auto', 'break', 'case', 'char', 'const', 'continue', 'default', 'do',
            'double', 'else', 'enum', 'extern', 'float', 'for', 'goto', 'if',
            'inline', 'int', 'long', 'register', 'restrict', 'return', 'short',
            'signed', 'sizeof', 'static', 'struct', 'switch', 'typedef', 'union',
            'unsigned', 'void', 'volatile', 'while', '_Bool', '_Complex', '_Imaginary'
        }
        self.type_keywords = {
            'int', 'char', 'float', 'double', 'void', 'long', 'short',
            'unsigned', 'signed', '_Bool', '_Complex', '_Imaginary'
        }
        self.operators = {'+', '-', '*', '/', '=', '==', '!=', '<', '>', '<=', '>=',
                         '&&', '||', '!', '&', '|', '^', '~', '>>', '<<', '++', '--',
                         '%', '+=', '-=', '*=', '/=', '%=', '&=', '|=', '^=', '>>=', '<<='}
        self.punctuation = {',', ';', '(', ')', '{', '}', '[', ']', '.', '->', '...'}
        self.symbol_table = []
        self.errors = []
        self.tokens_list = []  # Store all tokens for context checking
        
    def is_valid_identifier(self, lexeme):
        if not lexeme[0].isalpha() and lexeme[0] != '_':
            return False
        return all(c.isalnum() or c == '_' for c in lexeme)
    
    def remove_comments(self, code):
        lines = code.split('\n')
        cleaned_lines = []
        in_multiline_comment = False
        
        for line in lines:
            if not in_multiline_comment:
                if '//' in line:
                    line = line.split('//')[0]
                
                if '/*' in line:
                    parts = line.split('/*')
                    if '*/' in line[line.find('/*'):]:
                        end_part = line[line.find('*/') + 2:]
                        line = parts[0] + end_part
                    else:
                        line = parts[0]
                        in_multiline_comment = True
            else:
                if '*/' in line:
                    line = line[line.find('*/') + 2:]
                    in_multiline_comment = False
                else:
                    continue
            
            cleaned_lines.append(line)
        
        return '\n'.join(cleaned_lines)
    
    def tokenize(self, code):
        code = self.remove_comments(code)
        self.tokens_list = []  # Reset tokens list
        current_lexeme = ''
        i = 0
        
        while i < len(code):
            char = code[i]
            
            if char.isspace():
                if current_lexeme:
                    self.process_lexeme(current_lexeme)
                    current_lexeme = ''
                i += 1
                continue
            
            if char in '"\'':
                if current_lexeme:
                    self.process_lexeme(current_lexeme)
                    current_lexeme = ''
                
                quote = char
                string_content = quote
                i += 1
                while i < len(code) and code[i] != quote:
                    string_content += code[i]
                    i += 1
                if i < len(code):
                    string_content += code[i]
                    self.tokens_list.append(('String', string_content))
                else:
                    self.errors.append(f"Unterminated string: {string_content}")
                i += 1
                continue
            
            if char in '+-*/<>=!&|^~%':
                if current_lexeme:
                    self.process_lexeme(current_lexeme)
                    current_lexeme = ''
                
                op = char
                if i + 1 < len(code):
                    double_op = char + code[i + 1]
                    if double_op in self.operators:
                        op = double_op
                        i += 1
                self.tokens_list.append(('Operator', op))
                i += 1
                continue
            
            if char in ',.;(){}[]':
                if current_lexeme:
                    self.process_lexeme(current_lexeme)
                    current_lexeme = ''
                self.tokens_list.append(('Punctuation', char))
                i += 1
                continue
            
            current_lexeme += char
            i += 1
        
        if current_lexeme:
            self.process_lexeme(current_lexeme)
        
        return self.tokens_list
    
    def process_lexeme(self, lexeme):
        if lexeme in self.keywords:
            self.tokens_list.append(('Keyword', lexeme))
        elif lexeme.isdigit() or (lexeme[0].isdigit() and all(c.isalnum() or c == '.' for c in lexeme)):
            if self.is_valid_number(lexeme):
                self.tokens_list.append(('Constant', lexeme))
            else:
                self.errors.append(f"{lexeme} invalid number")
        elif self.is_valid_identifier(lexeme):
            self.tokens_list.append(('Identifier', lexeme))
            # Symbol table population is handled separately after tokenization
        else:
            self.errors.append(f"{lexeme} invalid lexeme")
    
    def is_valid_number(self, lexeme):
        try:
            if '.' in lexeme:
                float(lexeme)
            else:
                int(lexeme)
            return True
        except ValueError:
            return False
    
    def populate_symbol_table(self):
        self.symbol_table = []  # Reset symbol table
        i = 0
        while i < len(self.tokens_list):
            token_type, token_value = self.tokens_list[i]
            
            # Check if we have a type keyword followed by an identifier
            if (token_type == 'Keyword' and token_value in self.type_keywords and
                i + 1 < len(self.tokens_list)):
                
                # Look at next token
                next_token = self.tokens_list[i + 1]
                if next_token[0] == 'Identifier':
                    identifier = next_token[1]
                    # Check if it's not a function (no opening parenthesis follows)
                    if (i + 2 >= len(self.tokens_list) or 
                        self.tokens_list[i + 2][1] != '('):
                        if identifier not in self.symbol_table:
                            self.symbol_table.append(identifier)
            i += 1
    
    def analyze(self, code):
        self.tokens_list = self.tokenize(code)
        self.populate_symbol_table()
        
        # Print tokens
        print("TOKENS")
        for token_type, token_value in self.tokens_list:
            print(f"{token_type}: {token_value}")
        
        # Print lexical errors
        if self.errors:
            print("\nLEXICAL ERRORS")
            for error in self.errors:
                print(error)
        
        # Print symbol table
        print("\nSYMBOL TABLE ENTRIES")
        for i, identifier in enumerate(self.symbol_table, 1):
            print(f"{i}) {identifier}")
        
        return self.tokens_list
    
    
    def analyze_file(self, filename):
        try:
            with open(filename, 'r') as file:
                source_code = file.read()
                return self.analyze(source_code)
        except FileNotFoundError:
            print(f"Error: File '{filename}' not found")
            return None
        except Exception as e:
            print(f"Error reading file: {str(e)}")
            return None

# Example usage
if __name__ == "__main__":
    import sys
    
    if len(sys.argv) != 2:
        print("Usage: python lexical_analyzer.py <input_file.c>")
        sys.exit(1)
    
    input_file = sys.argv[1]
    analyzer = LexicalAnalyzer()
    analyzer.analyze_file(input_file)