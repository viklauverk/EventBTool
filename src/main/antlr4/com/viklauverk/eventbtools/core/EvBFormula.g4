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
import com.viklauverk.eventbtools.core.SymbolTable;
}

@parser::header{
import com.viklauverk.eventbtools.core.SymbolTable;
import com.viklauverk.eventbtools.core.OperatorNotationType;
import com.viklauverk.eventbtools.core.OperatorType;

import java.util.List;
import java.util.LinkedList;
}

@parser::members {
public SymbolTable symbol_table = null;
}
@lexer::members {
public SymbolTable symbol_table = null;
}

WHITESPACE: [ \r\n\t\u00A0]+ -> channel(HIDDEN);

BOOL:  'BOOL';
AND:   '&'   | '∧';
TRUE:  'true'  | '⊤';
FALSE: 'false' | '⊥';
ETRUE: 'TRUE';
EFALSE: 'FALSE';

ADD: '+';

BCMEQ: ':=' | '≔';

SYMBOL: [a-zA-Z][a-zA-Z0-9_]*;

start : ( substitution | predicate | expression ) EOF # Done;

substitution
   : BCMEQ
   ;

operator_ip: { symbol_table.isOperatorSymbol(OperatorNotationType.INFIX, OperatorType.PREDICATE, _input.LT(1).getText()) }? SYMBOL;
operator_ie: { symbol_table.isOperatorSymbol(OperatorNotationType.INFIX, OperatorType.EXPRESSION, _input.LT(1).getText()) }? SYMBOL;

predicate
   : TRUE                                           # AlwaysTrue
   | FALSE                                          # AlwaysFalse
   | left=predicate operator=AND  right=predicate   # Conjunction
   | '(' inner=predicate ')'                             # PredicateParentheses
   | left=expression operator=operator_ip  { System.out.println("PASSED IP operator"); } right=expression # OperatorInfixPredicate
;

expression
   : ETRUE                                     # ExpressionTRUE
   | EFALSE                                    # ExpressionFALSE
   | BOOL                                      # BOOLSet
   | { symbol_table.isVariableSymbol(_input.LT(1).getText()) }?   variable=SYMBOL  PRIM?  # ExpressionVariable
   | left=expression operator=ADD  right=expression  # Addition
   | '(' inner=expression ')'                       # ExpressionParentheses
   | left=expression operator=operator_ie  { System.out.println("PASSED IE operator"); } right=expression # OperatorInfixExpression
    ;
