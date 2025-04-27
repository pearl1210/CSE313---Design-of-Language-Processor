%{
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
%}

%option noyywrap

ARTICLE        ("the"|"a"|"an")
PRONOUN        ("he"|"she"|"it"|"they"|"we"|"you"|"I"|"him"|"her"|"us"|"them"|"my"|"mine")
VERB           ([a-zA-Z]+ed|[a-zA-Z]+ing|"walk"|"run"|"cut"|"make"|("is"|"are"|"was"|"were"|"am"|"be"|"has"|"have"|"had"|"do"|"does"|"did"|"will"|"shall"))
ADVERB         [a-zA-Z]+ly
ADJECTIVE      ("beautiful"|"happy"|"big"|"small"|"red"|"light"|"blue")
PREPOSITION    ("in"|"on"|"at"|"with"|"about"|"to"|"from"|"by")
CONJUNCTION    ("and"|"but"|"or"|"nor"|"for"|"yet"|"so")
QUESTION_WORD  ("what"|"where"|"when"|"why"|"who"|"which"|"how"|"whose"|"whom")
PROPER_NOUN    [A-Z][a-z]+
PUNCTUATIONS   ("."|"!"|","|":"|"?")

AMBIGUOUS_WORD ("light"|"run"|"watch"|"play"|"like"|"fast"|"close"|"right"|"left")
COMMON_NOUN    [a-z]+

%%

{ARTICLE}        { printf("ARTICLE: %s\n", yytext); }
{PRONOUN}        { printf("PRONOUN: %s\n", yytext); }
{VERB}           { printf("VERB: %s\n", yytext); }
{ADVERB}         { printf("ADVERB: %s\n", yytext); }
{ADJECTIVE}      { printf("ADJECTIVE: %s\n", yytext); }
{PREPOSITION}    { printf("PREPOSITION: %s\n", yytext); }
{CONJUNCTION}    { printf("CONJUNCTION: %s\n", yytext); }
{QUESTION_WORD}  { printf("QUESTION WORD: %s\n", yytext); }
{PROPER_NOUN}    { printf("PROPER NOUN: %s\n", yytext); }
{PUNCTUATIONS}   { printf("PUNCTUATIONS: %s\n", yytext); }
{AMBIGUOUS_WORD} { printf("AMBIGUOUS WORD: %s\n", yytext); }
{COMMON_NOUN}    { printf("COMMON NOUN: %s\n", yytext); }
[ \t\n]          ; 
.                { printf("PUNCTUATIONS: %s\n", yytext); } 

%%

int main() {
    
    char input_text[] = "My name is Madhuri, and I am a student at Charusat University. I go to college every day to learn new things and meet my friends. My teachers are very kind, and they help me understand my subjects. I like reading books, drawing pictures, and listening to music in my free time. My college has a big library where I love to sit and read interesting stories. Every morning, I wake up early and get ready for my classes. After college, I sometimes walk in the garden and think about my dreams. I want to study hard and do something good for people. My parents always tell me to work hard and be kind to everyone. I am happy to be a student and excited about my future.";

    yy_scan_string(input_text);
    yylex();

    return 0;
}
