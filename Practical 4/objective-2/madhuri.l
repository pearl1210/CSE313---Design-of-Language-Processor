%{
#include <stdio.h>
#include <string.h>
#include <ctype.h>
%}

%%

[Cc]harusat   { printf("university"); }  // Match "charusat", "Charusat"
.|\n          { printf("%s", yytext); }  // Print other characters unchanged

%%

int main() {
    yylex();  // Start lexical analysis
    return 0;
}

int yywrap() {
    return 1;
}
