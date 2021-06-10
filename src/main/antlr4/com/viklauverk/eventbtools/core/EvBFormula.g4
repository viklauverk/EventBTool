// Copyright (C) 2021 Viklauverk AB
// Author Fredrik Öhrström
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

grammar EvBFormula;

@lexer::header{
import com.viklauverk.eventbtools.core.SymbolTable;
}

@parser::header{
import com.viklauverk.eventbtools.core.SymbolTable;

import java.util.List;
import java.util.LinkedList;
}

@parser::members {
public SymbolTable symbol_table = null;
public boolean all_symbols_are_non_free_vars = false;
public List<String> non_free_vars = new LinkedList<>();

void pushFrame(EvBFormulaParser.ListOfNonFreeSymbolsContext sc)
{
    EvBFormulaParser.ListOfNonFreeVariablesContext vars = (EvBFormulaParser.ListOfNonFreeVariablesContext)sc;
    List<String> syms = new LinkedList<>();
    List<TerminalNode> symbols = vars.SYMBOL();
    for (TerminalNode tn : symbols)
    {
        syms.add(tn.getText());
    }
    symbol_table.pushFrame(syms);
}

void enableAllSymbolsAreNonFreeVars()
{
    all_symbols_are_non_free_vars = true;
    non_free_vars = new LinkedList<>();
}

void disableAllSymbolsAreNonFreeVars()
{
    all_symbols_are_non_free_vars = false;
}

void addNonFreeVar(String nfv)
{
    non_free_vars.add(nfv);
}

boolean allSymbolsAreNonFreeVars()
{
    return all_symbols_are_non_free_vars;
}

void pushFrameNonFreeVars()
{
    symbol_table.pushFrame(non_free_vars);
}

void popFrame()
{
    symbol_table.popFrame();
}

}
@lexer::members {
public SymbolTable symbol_table = null;
}


/* Accept all unicode whitespace:
   \u0020 	SPACE 	foo bar 	Depends on font, typically 1/4 em, often adjusted
   \u00A0 	NO-BREAK SPACE 	foo bar 	As a space, but often not adjusted
   \u1680 	OGHAM SPACE MARK 	foo bar 	Unspecified; usually not really a space but a dash
   \u180E 	MONGOLIAN VOWEL SEPARATOR 	foo᠎bar 	0
   \u2000 	EN QUAD 	foo bar 	1 en (= 1/2 em)
   \u2001 	EM QUAD 	foo bar 	1 em (nominally, the height of the font)
   \u2002 	EN SPACE (nut) 	foo bar 	1 en (= 1/2 em)
   \u2003 	EM SPACE (mutton) 	foo bar 	1 em
   \u2004 	THREE-PER-EM SPACE (thick space) 	foo bar 	1/3 em
   \u2005 	FOUR-PER-EM SPACE (mid space) 	foo bar 	1/4 em
   \u2006 	SIX-PER-EM SPACE 	foo bar 	1/6 em
   \u2007 	FIGURE SPACE 	foo bar 	“Tabular width”, the width of digits
   \u2008 	PUNCTUATION SPACE 	foo bar 	The width of a period “.”
   \u2009 	THIN SPACE 	foo bar 	1/5 em (or sometimes 1/6 em)
   \u200A 	HAIR SPACE 	foo bar 	Narrower than THIN SPACE
   \u200B 	ZERO WIDTH SPACE 	foo​bar 	0
   \u202F 	NARROW NO-BREAK SPACE 	foo bar 	Narrower than NO-BREAK SPACE (or SPACE), “typically the width of a thin space or a mid space”
   \u205F 	MEDIUM MATHEMATICAL SPACE 	foo bar 	4/18 em
   \u3000 	IDEOGRAPHIC SPACE 	foo　bar 	The width of ideographic (CJK) characters.
   \uFEFF 	ZERO WIDTH NO-BREAK SPACE 	foo﻿bar 	0
*/

WHITESPACE: [ \r\n\t\u00A0\u1680\u180E\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u200B\u202F\u205F\u3000\uFEFF]+ -> channel(HIDDEN);

NAT:   'NAT' | 'ℕ';
NAT1:  'NAT1'| 'ℕ1';
INT:   'INT' | 'ℤ';
BOOL:  'BOOL';
POW:   'POW' | 'ℙ';
POW1:  'POW1'| 'ℙ1';

EQUIV: '<=>' | '⇔';
IMP:   '=>'  | '⇒';
AND:   '&'   | '∧';
OR:    'or'  | '∨';
NOT:   'not' | '¬';
TRUE:  'true'  | '⊤';
FALSE: 'false' | '⊥';
ETRUE: 'TRUE';
EFALSE: 'FALSE';
ALL:   '!'  | '∀';
EXI:   '#'  | '∃';
QDOT:  '.'  | '·';

EQ:    '=';
NEQ:   '/=' | '≠';
LT:    '<';
GT:    '>';
LEQ:   '<=' | '≤';
GEQ:   '>=' | '≥';
IN:    ':'  | '∈';
NOTIN: '/:' | '∉';
STRICT_SUBSET:     '<<:'  | '⊂';
NOT_STRICT_SUBSET: '/<<:' | '⊄';
SUBSET:      '<:' | '⊆';
NOT_SUBSET: '/<:' | '⊈';

REL:   '<->'  | '↔';
TREL:  '<<->' | '';
SREL:  '<->>' | '';
STREL: '<<->>'| '';
PFUN:  '+->'  | '⇸';
TFUN:  '-->'  | '→';
PINJ:  '>+>'  | '⤔';
TINJ:  '>->'  | '↣';
PSUR:  '+->>'  | '⤀';
TSUR:  '-->>'  | '↠';
TBIJ:  '>->>' | '⤖';

MAPSTO: '|->' | '↦';
EMPTY:  '{}'  | '∅';
INTER:  '/\\' | '∩';
UNION:  '\\/' | '∪';
SETMINUS: '\\'| '∖';
CPROD:   '**' | '×';

ID:    'id';
OVR:   '<+'   | '';
CIRC:  'circ' | '∘';
SEMI: ';';
DPROD: '><'   | '⊗';
PPROD: '||'   | '∥';
PRJ1: 'prj1';
PRJ2: 'prj2';
INV:  '~'    | '∼';
DOMRES: '<|'  | '◁';
DOMSUB: '<<|' | '⩤';
RANRES: '|>'  | '▷';
RANSUB: '|>>' | '⩥';
DOM: 'dom';
RAN: 'ran';

LAMBDA: '%'   | 'λ';
QUNION: 'UNION' | '⋃';
QINTER: 'INTER' | '⋂';
GUNION: 'union' ;
GINTER: 'inter' ;
MIN: 'min' ;
MAX: 'max' ;
MOD: 'mod' ;
MID:    '|' | '∣'; // Different chars!!!!!
UPTO:   '..' | '‥';
MINUS: '-' | '−';
ADD: '+';
MUL: '*' | '∗';
DIV: '/' | '÷';
EXP: '^';

CARD: 'card';
FINITE: 'finite';
PARTITION: 'partition';

OFTYPE: 'oftype' | '⦂';
TESTBOOL: 'bool';

BCMEQ: ':=' | '≔';
BCMIN: '::' | ':∈';
BCMSUCH: ':|' | ':∣';

PRIM: '\'';

NUMBER: [0-9]+;

SYMBOL: [a-zA-Z][a-zA-Z0-9_]*;

start : ( substitution | predicate | expression ) EOF # Done;

substitution
   : left=listOfSymbols BCMEQ right=listOfExpressions  # BecomeEQ
   | variable=SYMBOL '(' left=expression ')' BCMEQ right=expression # BecomeEQFuncApp
   | variable=SYMBOL BCMIN inner=expression  # BecomeIN
   | variable=SYMBOL BCMSUCH inner=predicate # BecomeSUCH
   ;

listOfNonFreeSymbols
   : SYMBOL (',' SYMBOL)*                          # ListOfNonFreeVariables
   ;

listOfSymbols
   : SYMBOL (',' SYMBOL)*                          # ListOfVariables
   ;

predicate
   : TRUE                                           # AlwaysTrue
   | FALSE                                          # AlwaysFalse
   | { symbol_table.isAnySymbol(_input.LT(1).getText()) }?  sym=SYMBOL  # AnyPredicateSymbol
   | { symbol_table.isPredicateSymbol(_input.LT(1).getText()) }? sym=SYMBOL  # PredicateSymbol
   | '(' inner=predicate ')'                        # PredicateParentheses
   | left=expression IN right=expression            # SetMembership
   | left=expression NOTIN right=expression         # SetNotMembership
   | left=predicate operator=AND right=predicate    # Conjunction
   | left=predicate operator=IMP right=predicate    # Implication
   | left=predicate operator=EQUIV right=predicate  # Equivalence
   | left=predicate operator=OR right=predicate     # Disjunction
   | operator=NOT right=predicate                   # Negation
   | ALL left=listOfNonFreeSymbols
     { pushFrame(((UniversalContext)_localctx).left); }
     QDOT right=predicate
     { popFrame(); }
     # Universal
   | EXI left=listOfNonFreeSymbols
     { pushFrame(((ExistentialContext)_localctx).left); }
     QDOT right=predicate
     { popFrame(); }
     # Existential
   | left=expression operator=EQ right=expression   # Equals
   | left=expression operator=NEQ right=expression  # NotEquals
   | left=expression operator=LT right=expression   # LessThan
   | left=expression operator=GT right=expression   # GreaterThan
   | left=expression operator=LEQ right=expression  # LessThanOrEqual
   | left=expression operator=GEQ right=expression  # GreaterThanOrEqual
   | left=expression SUBSET right=expression        # SubSet
   | left=expression NOT_SUBSET right=expression    # NotSubSet
   | left=expression STRICT_SUBSET right=expression # StrictSubSet
   | left=expression NOT_STRICT_SUBSET right=expression # NotStrictSubSet
   | FINITE '(' inner=expression ')'                       # FiniteSet
   | PARTITION '(' left=expression ',' right=listOfExpressions ')' # PartitionSet
;

expression
   : ETRUE                                          # ExpressionTRUE
   | EFALSE                                         # ExpressionFALSE
   | NUMBER                                         # Number
   | { allSymbolsAreNonFreeVars() }? variable=SYMBOL { addNonFreeVar(((SetComprehensionNonFreeExpressionVariableContext)_localctx).variable.getText()); } # SetComprehensionNonFreeExpressionVariable
   | { symbol_table.isExpressionSymbol(_input.LT(1).getText()) }? sym=SYMBOL # ExpressionSymbol
   | { symbol_table.isAnySymbol(_input.LT(1).getText()) }?        sym=SYMBOL # AnyExpressionSymbol
   | { symbol_table.isNumberSymbol(_input.LT(1).getText()) }?     sym=SYMBOL # NumberSymbol
   | { symbol_table.isNonFreeVariableSymbol(_input.LT(1).getText()) }?   variable=SYMBOL # NonFreeExpressionVariable
   | { symbol_table.isVariableSymbol(_input.LT(1).getText()) }?   variable=SYMBOL  PRIM? # ExpressionVariable
   | { symbol_table.isConstantSymbol(_input.LT(1).getText()) }?   constant=SYMBOL                       # ExpressionConstant
   // Should we be able to talk about all functions such that their applications give such and such result? For the moment, we can't.
   | { symbol_table.isVariableSymbol(_input.LT(1).getText()) }?   variable=SYMBOL PRIM? INV? '(' inner=expression ')' # VariableFunctionApplication
   | { symbol_table.isConstantSymbol(_input.LT(1).getText()) }?   constant=SYMBOL '(' inner=expression ')' # ConstantFunctionApplication
   | function=expression '(' inner=expression ')'   # GenericFunctionApplication
   | '(' inner=expression ')'                       # ExpressionParentheses
   | left=expression operator=MAPSTO right=expression # MapsToExpression
   | left=expression operator=MAPSTO right=expression # MapsToSet
   | PRJ1 '(' inner=expression ')'                  # Prj1
   | PRJ2 '(' inner=expression ')'                  # Prj2
   | left=expression operator=MUL right=expression  # Multiplication
   | left=expression operator=DIV right=expression  # Division
   | left=expression operator=ADD right=expression  # Addition
   | left=expression operator=MINUS right=expression  # Subtraction
   | operator=MINUS right=expression                  # UnaryMinus
   | left=expression operator=MOD right=expression  # Modulo
   | left=expression operator=EXP right=expression  # Exponentiation
   | MIN '(' inner=expression ')'                   # Minimum
   | MAX '(' inner=expression ')'                   # Maximum
   | TESTBOOL '(' inner=predicate ')'               # TestBool
   | CARD '(' inner=expression ')'                  # Cardinality
   | EMPTY                                          # EmptySet
    | ID                                            # IdSet
    | { symbol_table.isSetSymbol(_input.LT(1).getText()) }? sym=SYMBOL  # SetSymbol
    | { symbol_table.isAnySymbol(_input.LT(1).getText()) }? sym=SYMBOL  # AnySetSymbol
    | { symbol_table.isNonFreeVariableSymbol(_input.LT(1).getText()) }?   variable=SYMBOL # NonFreeSetVariable
    | { symbol_table.isVariableSymbol(_input.LT(1).getText()) }? variable=SYMBOL PRIM? # SetVariable
    | { symbol_table.isConstantSymbol(_input.LT(1).getText()) }? constant=SYMBOL # SetConstant
    | NAT                     # NATSet
    | NAT1                    # NAT1Set
    | INT                     # INTSet
    | BOOL                    # BOOLSet
    | POW '(' inner=expression ')'   # PowerSet
    | POW1 '(' inner=expression ')'  # Power1Set
    | DOM '(' inner=expression ')'   # Domain
    | RAN '(' inner=expression ')'   # Range
    | inner=expression INV           # InvertSet
    | left=expression '[' right=expression ']' # RelationImage
    | left=expression OVR right=expression  # Override
    | left=expression DPROD right=expression  # DirectProduct
    | left=expression PPROD right=expression  # ParallelProduct
    | left=expression OFTYPE right=expression  # OfType
    | '{' expression (',' expression)* '}' # EnumeratedSet

    | '(' LAMBDA vars=listOfNonFreeSymbols
          { pushFrame(((LambdaAbstractionExpressionContext)_localctx).vars); }
       QDOT pred=predicate MID formula=expression
          { popFrame(); } ')'
      # LambdaAbstractionExpression

    | '(' LAMBDA vars=listOfNonFreeSymbols
          { pushFrame(((LambdaAbstractionSetContext)_localctx).vars); }
       QDOT pred=predicate MID formula=expression
          { popFrame(); } ')'
      # LambdaAbstractionSet

    | '{' vars=listOfNonFreeSymbols
          { pushFrame(((SetComprehensionContext)_localctx).vars); }
      QDOT pred=predicate MID formula=expression
          { popFrame(); }
      '}'
      # SetComprehension

    | '{'
        { enableAllSymbolsAreNonFreeVars(); } // The expression definition implicitly declares the non-free vars!
        formula=expression
        { disableAllSymbolsAreNonFreeVars(); pushFrameNonFreeVars(); }
        MID pred=predicate
        { popFrame(); }
      '}'
      # SetComprehensionSpecial

    | from=expression UPTO to=expression     # RangeSet
    | left=expression UNION right=expression # SetUnion
    | left=expression INTER right=expression # SetIntersection
    | left=expression SETMINUS right=expression # SetMinus
    | left=expression CPROD right=expression # CartesianProduct
    | left=expression REL right=expression   # Relation
    | left=expression TREL right=expression  # TotalRelation
    | left=expression SREL right=expression  # SurjectiveRelation
    | left=expression STREL right=expression # SurjectiveTotalRelation
    | left=expression PFUN right=expression  # PartialFunction
    | left=expression TFUN right=expression  # TotalFunction
    | left=expression PINJ right=expression  # PartialInjection
    | left=expression TINJ right=expression  # TotalInjection
    | left=expression PSUR right=expression  # PartialSurjection
    | left=expression TSUR right=expression  # TotalSurjection
    | left=expression TBIJ right=expression  # TotalBijection

    | left=expression SEMI right=expression  # ForwardComposition
    | left=expression CIRC right=expression  # BackwardComposition

    | left=expression DOMRES right=expression  # DomainRestriction
    | left=expression DOMSUB right=expression  # DomainSubtraction
    | left=expression RANRES right=expression  # RangeRestriction
    | left=expression RANSUB right=expression  # RangeSubtraction

    | GUNION '(' inner=expression ')' # GeneralizedUnion
    | GINTER '(' inner=expression ')' # GeneralizedIntersection

    | QUNION vars=listOfNonFreeSymbols
          { pushFrame(((QuantifiedUnionContext)_localctx).vars); }
       QDOT pred=predicate MID inner=expression
          { popFrame(); }
      # QuantifiedUnion

    | QINTER vars=listOfNonFreeSymbols
          { pushFrame(((QuantifiedIntersectionContext)_localctx).vars); }
       QDOT pred=predicate MID inner=expression
          { popFrame(); }
      # QuantifiedIntersection

    ;

listOfExpressions
   : expression (',' expression)*
   ;
