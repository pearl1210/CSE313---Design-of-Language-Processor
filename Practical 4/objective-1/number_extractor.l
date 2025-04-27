%{
#include <stdio.h>
%}

%%

[0-9]+    { printf("%s\n", yytext); }  // Match numbers and print them
.|\n      { /* Ignore other characters */ }

%%

int main() {
    yylex();  // Start lexical analysis
    return 0;
}

int yywrap() {
    return 1;
}
