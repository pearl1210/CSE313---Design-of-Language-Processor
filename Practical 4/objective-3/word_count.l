%{
#include <stdio.h>
#include <ctype.h>

int char_count = 0, word_count = 0, line_count = 1;
int in_word = 0; // To track if we are inside a word
%}

%%

\n          { line_count++; char_count++; in_word = 0; }  // Count new lines
[ \t\r]+    { char_count += yyleng; in_word = 0; }  // Count spaces/tabs but reset word tracking
[A-Za-z0-9]+ { word_count++; char_count += yyleng; in_word = 1; }  // Count words (alphanumeric sequences)
.           { char_count++; }  // Count all other characters (punctuation, symbols)

%%

int main(int argc, char *argv[]) {
    if (argc < 2) {
        printf("Usage: %s <filename>\n", argv[0]);
        return 1;
    }

    FILE *file = fopen(argv[1], "r");
    if (!file) {
        printf("Error: Could not open file %s\n", argv[1]);
        return 1;
    }

    yyin = file;  // Set Lex input to read from the file
    yylex();  // Start lexical analysis

    printf("Total Characters: %d\n", char_count);
    printf("Total Words: %d\n", word_count);
    printf("Total Lines: %d\n", line_count);

    fclose(file);
    return 0;
}

int yywrap() {
    return 1;
}
