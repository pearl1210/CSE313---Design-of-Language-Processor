%{
#include <stdio.h>
#include <stdlib.h>

int yylex(void);
int yyerror(const char *s);
%}

%token I T E_TOKEN A B

%%
S  : I E T S S1
   | A
   ;

S1 : E_TOKEN S
   | /* empty */
   ;

E  : B ;
%%

int yyerror(const char *s) {
    return 1;
}

int main() {
    printf("Enter the string:\n");
    if (yyparse() == 0)
        printf("Valid string\n");
    else
        printf("Invalid string\n");
    return 0;
}
