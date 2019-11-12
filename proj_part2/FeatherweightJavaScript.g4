grammar FeatherweightJavaScript;


@header { package edu.sjsu.fwjs.parser; }

// Reserved words
IF        : 'if' ;
ELSE      : 'else' ;
WHILE     : 'while' ;
FUNCTION  : 'function' ;
VAR       : 'var' ;
PRINT     : 'print' ;

// Literals
INT       : [1-9][0-9]* | '0' ;
BOOL      : ('true' | 'false') ;
NULL      : 'null' ;

// Symbols
MUL       : '*' ;
DIV       : '/' ;
ADD       : '+' ;
SUB       : '-' ;
MOD       : '%' ;

GT        : '>' ;
LT        : '<' ;
GE        : '>=' ;
LE        : '<=' ;
EQ        : '==' ;

SEPARATOR : ';' ;

// Identifier
ID        : [_a-zA-Z][_a-zA-Z0-9]* ;



// Whitespace and comments
NEWLINE   : '\r'? '\n' -> skip ;
LINE_COMMENT  : '//' ~[\n\r]* -> skip ;
WS            : [ \t]+ -> skip ; // ignore whitespace

BLOCK_COMMENT : '/*'(.*[\n\r]*)*'*/' -> skip ; 
//     '/*' .*? '*/'



// ***Paring rules ***

/** The start rule */
prog: stat+ ;

stat: expr SEPARATOR                                    # bareExpr
    | IF '(' expr ')' block ELSE block                  # ifThenElse
    | IF '(' expr ')' block                             # ifThen
    | WHILE '(' expr ')' block                          # whileStat   
    | PRINT '(' expr ')' SEPARATOR                      # printStat    
    | SEPARATOR                                         # emptyStat     
    ;

expr: expr op=( '*' | '/' | '%' ) expr                  # MulDivMod
    | expr op=( '+' | '-' ) expr                        # AddSub
    | expr op=( '<' | '<=' | '>' | '>=' | '==' ) expr   # Cmp
    | FUNCTION '(' (ID (',' ID)*)? ')' block            # FuncDeclare 
    | ID '(' (expr (',' expr)*)? ')'              # FuncApply   
    | VAR ID '=' expr                                   # VarDeclare  
    | ID                                                # VarReference  
    | ID '=' expr                                       # Assign
    | INT                                               # int
    | BOOL                                              # bool
    | NULL                                              # null
    | '(' expr ')'                                      # parens
    ;

block: '{' stat* '}'                                    # fullBlock
     | stat                                             # simpBlock
     ;

