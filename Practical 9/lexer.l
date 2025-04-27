%{
#include "parser.tab.h"
%}

%%
i      { return I; }
b      { return B; }
t      { return T; }
a      { return A; }
e      { return E_TOKEN; }
[ \t\n] ;              // Ignore whitespace
.      { return -1; }  // Invalid character
%%

int yywrap() {
    return 1;
}
