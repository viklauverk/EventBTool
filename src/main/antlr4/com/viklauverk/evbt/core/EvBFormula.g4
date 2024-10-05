// Copyright (C) 2021-2024 Viklauverk AB
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
import com.viklauverk.evbt.core.SymbolTable;

import java.util.Map;
import java.util.HashMap;
}

@parser::header{
import com.viklauverk.evbt.core.SymbolTable;
import com.viklauverk.evbt.core.OperatorNotationType;
import com.viklauverk.evbt.core.OperatorType;

import java.util.List;
import java.util.LinkedList;
}

@parser::members {
public SymbolTable symbol_table = null;
public SymbolTable meta_table = null;
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
public HashMap<String,Integer> symbol_overrides = new HashMap<>();

int potentialTypeOverride(String t)
{
    Integer i = symbol_overrides.get(t);
    if (i == null) return EvBFormulaParser.SYMBOL;
    return i;
}

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

SYMBOL: [a-zA-Z][a-zA-Z0-9_]*
   {
       // There is an interesting limitation/bug? in antlr4 where semantic predicates
       // does not work properly for infix operator selection. This is a workaround for
       // those operators....
       int pto = potentialTypeOverride(getText());
       if (pto != EvBFormulaParser.SYMBOL)
       {
           setType(pto);
       };
    }
    ;

// New operators loaded from theories.
OP_IP: ; // Infix predicate operator
OP_IE: ; // Infix expression operator
OP_PP: ; // Prefix predicate operator
OP_PE: ; // Prefix expression operator

start : ( substitution | predicate | expression ) EOF # Done;

substitution
   : left=listOfSymbols BCMEQ meta? right=listOfExpressions  # BecomeEQ
   | variable=SYMBOL '(' left=expression ')' BCMEQ meta? right=expression # BecomeEQFuncApp
   | variable=SYMBOL BCMIN meta? inner=expression  # BecomeIN
   | left=listOfSymbols BCMSUCH meta? right=predicate # BecomeSUCH
   ;

listOfNonFreeSymbols
   : SYMBOL ofTyping? (',' SYMBOL ofTyping? )*                          # ListOfNonFreeVariables
   ;

ofTyping
    : OFTYPE meta? type=expression                 # OfTypeVar
    ;

listOfSymbols
   : SYMBOL (',' SYMBOL)*                          # ListOfVariables
   ;

predicate
   : TRUE meta?                                          # AlwaysTrue
   | FALSE meta?                                         # AlwaysFalse
   | '(' inner=predicate ')'                             # PredicateParentheses
   | { symbol_table.isAnySymbol(_input.LT(1).getText()) }?  sym=SYMBOL meta? # AnyPredicateSymbol
   | { symbol_table.isPredicateSymbol(_input.LT(1).getText()) }? sym=SYMBOL meta? # PredicateSymbol

   | left=expression operator=OP_IP meta? right=expression # OperatorInfixPredicate
   | left=expression IN meta? right=expression            # SetMembership
   | left=expression NOTIN meta? right=expression         # SetNotMembership
   | left=predicate operator=AND meta? right=predicate    # Conjunction
   | left=predicate operator=IMP meta? right=predicate    # Implication
   | left=predicate operator=EQUIV meta? right=predicate  # Equivalence
   | left=predicate operator=OR meta? right=predicate     # Disjunction
   | left=expression operator=EQ meta? right=expression   # Equals
   | left=expression operator=NEQ meta? right=expression  # NotEquals
   | left=expression operator=LT meta? right=expression   # LessThan
   | left=expression operator=GT meta? right=expression   # GreaterThan
   | left=expression operator=LEQ meta? right=expression  # LessThanOrEqual
   | left=expression operator=GEQ meta? right=expression  # GreaterThanOrEqual
   | left=expression SUBSET meta? right=expression        # SubSet
   | left=expression NOT_SUBSET meta? right=expression    # NotSubSet
   | left=expression STRICT_SUBSET meta? right=expression # StrictSubSet
   | left=expression NOT_STRICT_SUBSET meta? right=expression # NotStrictSubSet
   | NOT meta? right=predicate                             # Negation
   | FINITE '(' inner=expression ')'                       # FiniteSet
   | PARTITION '(' left=expression ',' right=listOfExpressions ')' # PartitionSet
   | operator=OP_PP meta? ( '(' parameters=listOfExpressions ')' )?  # OperatorPrefixPredicate

   | ALL meta? left=listOfNonFreeSymbols
     { pushFrame(((UniversalContext)_localctx).left); }
     QDOT right=predicate
     { popFrame(); }
     # Universal
   | EXI meta? left=listOfNonFreeSymbols
     { pushFrame(((ExistentialContext)_localctx).left); }
     QDOT right=predicate
     { popFrame(); }
     # Existential
;

expression
   : ETRUE meta?                                    # ExpressionTRUE
   | EFALSE meta?                                   # ExpressionFALSE
   | NUMBER meta?                                   # Number
   | '(' inner=expression ')'                       # ExpressionParentheses

   | { allSymbolsAreNonFreeVars() }? variable=SYMBOL meta? { addNonFreeVar(((SetComprehensionNonFreeExpressionVariableContext)_localctx).variable.getText()); } # SetComprehensionNonFreeExpressionVariable

   | { symbol_table.isExpressionSymbol(_input.LT(1).getText()) }? sym=SYMBOL meta? # ExpressionSymbol
   | { symbol_table.isAnySymbol(_input.LT(1).getText()) }?        sym=SYMBOL meta? # AnyExpressionSymbol
   | { symbol_table.isNumberSymbol(_input.LT(1).getText()) }?     sym=SYMBOL meta? # NumberSymbol
   | { symbol_table.isNonFreeVariableSymbol(_input.LT(1).getText()) }?   variable=SYMBOL meta? # NonFreeExpressionVariable
   | { symbol_table.isVariableSymbol(_input.LT(1).getText()) }?   variable=SYMBOL  PRIM? meta? # ExpressionVariable

   | { symbol_table.isConstructorSymbol(_input.LT(1).getText()) }? constructor=SYMBOL
        meta? ( '(' ')' | '(' parameters=listOfExpressions ')' )? # Constructor

   | { symbol_table.isDestructorSymbol(_input.LT(1).getText()) }? destructor=SYMBOL
        meta? ( '(' ')' | '(' parameters=listOfExpressions ')' )? # Destructor

   | { symbol_table.isConstantSymbol(_input.LT(1).getText()) }?   constant=SYMBOL meta?        # ExpressionConstant

   // Should we be able to talk about all functions such that their applications give such and such result? For the moment, we can't.
   | { symbol_table.isVariableSymbol(_input.LT(1).getText()) }?   variable=SYMBOL PRIM? INV? meta? '(' inner=expression ')' # VariableFunctionApplication
   | { symbol_table.isConstantSymbol(_input.LT(1).getText()) }?   constant=SYMBOL meta? '(' inner=expression ')' # ConstantFunctionApplication
   | function=expression meta? '(' inner=expression ')'  # GenericFunctionApplication

   | left=expression operator=OP_IE meta? right=expression # OperatorInfixExpression
   | left=expression operator=MAPSTO right=expression # MapsToExpression
   | left=expression operator=MAPSTO right=expression # MapsToSet
   | left=expression operator=MUL meta? right=expression  # Multiplication
   | left=expression operator=DIV meta? right=expression  # Division
   | left=expression operator=ADD meta? right=expression  # Addition
   | left=expression operator=MINUS meta? right=expression  # Subtraction
   | left=expression operator=MOD meta? right=expression  # Modulo
   | left=expression operator=EXP meta? right=expression  # Exponentiation
   | left=expression '[' right=expression ']' meta? # RelationImage
   | left=expression OVR meta? right=expression  # Override
   | left=expression DPROD meta? right=expression  # DirectProduct
   | left=expression PPROD meta? right=expression  # ParallelProduct
   | left=expression OFTYPE meta? right=expression  # OfType

   | left=expression UPTO meta? right=expression     # UpTo
   | left=expression UNION meta? right=expression # SetUnion
   | left=expression INTER meta? right=expression # SetIntersection
   | left=expression SETMINUS meta? right=expression # SetMinus
   | left=expression CPROD meta? right=expression # CartesianProduct
   | left=expression REL meta? right=expression   # Relation
   | left=expression TREL meta? right=expression  # TotalRelation
   | left=expression SREL meta? right=expression  # SurjectiveRelation
   | left=expression STREL meta? right=expression # SurjectiveTotalRelation
   | left=expression PFUN meta? right=expression  # PartialFunction
   | left=expression TFUN meta? right=expression  # TotalFunction
   | left=expression PINJ meta? right=expression  # PartialInjection
   | left=expression TINJ meta? right=expression  # TotalInjection
   | left=expression PSUR meta? right=expression  # PartialSurjection
   | left=expression TSUR meta? right=expression  # TotalSurjection
   | left=expression TBIJ meta? right=expression  # TotalBijection

   | left=expression SEMI meta? right=expression  # ForwardComposition
   | left=expression CIRC meta? right=expression  # BackwardComposition

   | left=expression DOMRES meta? right=expression  # DomainRestriction
   | left=expression DOMSUB meta? right=expression  # DomainSubtraction
   | left=expression RANRES meta? right=expression  # RangeRestriction
   | left=expression RANSUB meta? right=expression  # RangeSubtraction

   | operator=OP_PE meta? ( '(' ')' | '(' parameters=listOfExpressions ')' )? # OperatorPrefixExpression

   | MINUS right=expression                               # UnaryMinus
   | MIN '(' inner=expression ')' meta?                   # Minimum
   | MAX '(' inner=expression ')' meta?                   # Maximum
   | TESTBOOL '(' inner=predicate ')' meta?               # TestBool
   | CARD '(' inner=expression ')' meta?          # Cardinality
   | EMPTY meta?                                    # EmptySet
   | ID   meta?                                    # IdSet
   | PRJ1 meta?                                    # Prj1
   | PRJ2 meta?                                    # Prj2
   | { symbol_table.isSetSymbol(_input.LT(1).getText()) }? sym=SYMBOL meta?  # SetSymbol
   | { symbol_table.isPolymorphicDataTypeSymbol(_input.LT(1).getText()) }? datatype=SYMBOL
     ( ('(' | '<' | '‹') parameters=listOfExpressions (')' | '>' | '›') )? meta?  # PolymorphicDataTypeSymbol
   | NAT meta?               # NATSet
   | NAT1 meta?              # NAT1Set
   | INT meta?               # INTSet
   | BOOL meta?              # BOOLSet
   | POW meta? '(' inner=expression ')'  # PowerSet
   | POW1 meta? '(' inner=expression ')' # Power1Set
   | DOM meta? '(' inner=expression ')'  # Domain
   | RAN meta? '(' inner=expression ')'  # Range
   | GUNION meta? '(' inner=expression ')' # GeneralizedUnion
   | GINTER meta? '(' inner=expression ')' # GeneralizedIntersection

   | inner=expression INV meta?          # InvertSet
   | '{' expression (',' expression)* '}' meta? # EnumeratedSet

   | LAMBDA meta? vars=listOfNonFreeSymbols
         { pushFrame(((LambdaAbstractionExpressionContext)_localctx).vars); }
      QDOT pred=predicate MID formula=expression
         { popFrame(); }
     # LambdaAbstractionExpression

   | '{' meta? vars=listOfNonFreeSymbols
         { pushFrame(((SetComprehensionContext)_localctx).vars); }
     QDOT pred=predicate MID formula=expression
         { popFrame(); }
     '}'
     # SetComprehension

   | '{' meta?
       { enableAllSymbolsAreNonFreeVars(); } // The expression definition implicitly declares the non-free vars!
       formula=expression
       { disableAllSymbolsAreNonFreeVars(); pushFrameNonFreeVars(); }
       MID pred=predicate
       { popFrame(); }
     '}'
     # SetComprehensionSpecial

    | QUNION meta? vars=listOfNonFreeSymbols
          { pushFrame(((QuantifiedUnionContext)_localctx).vars); }
       QDOT pred=predicate MID inner=expression
          { popFrame(); }
      # QuantifiedUnion

    | QINTER meta? vars=listOfNonFreeSymbols
          { pushFrame(((QuantifiedIntersectionContext)_localctx).vars); }
       QDOT pred=predicate MID inner=expression
          { popFrame(); }
      # QuantifiedIntersection

    ;

listOfExpressions
   : expression (',' expression)*
   ;

meta
   : '«' ( substitution | predicate | expression ) '»' #MetaFormula
   ;
