%{
#include <stdio.h>
#include <string.h>
#include <ctype.h>

#define MIN_LENGTH 9
#define MAX_LENGTH 15

int has_upper = 0, has_lower = 0, has_digit = 0, has_symbol = 0, length = 0;
%}

%%

[A-Z]      { has_upper = 1; length++; }     // At least one uppercase letter
[a-z]      { has_lower = 1; length++; }     // At least one lowercase letter
[0-9]      { has_digit = 1; length++; }     // At least one digit
[*;#$@]    { has_symbol = 1; length++; }    // At least one special symbol
.          { length++; }                    // Count other valid characters
\n         { return 1; }                    // End of input

%%

int main() {
    printf("Enter a password: ");
    yylex();  // Start lexical analysis

    if (length >= MIN_LENGTH && length <= MAX_LENGTH &&
        has_upper && has_lower && has_digit && has_symbol) {
        printf("Valid password\n");
    } else {
        printf("Invalid password\n");
    }

    return 0;
}

int yywrap() {
    return 1;
}
