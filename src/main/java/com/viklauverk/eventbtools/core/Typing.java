/*
 Copyright (C) 2021 Viklauverk AB
 Author Fredrik Öhrström

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.viklauverk.eventbtools.core;

import static com.viklauverk.eventbtools.core.Node.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class Typing
{
    private static Log log = LogModule.lookup("typing");

    private Map<String,Type> types_ = new HashMap<>();

    public Type lookup(String t)
    {
        Type type = types_.get(t);
        if (type != null) return type;

        Formula f = Formula.fromString(t, new SymbolTable(""));
        return lookupType(f);
    }

    public Type lookupType(Formula i)
    {
        String t = i.toString();

        Type type = types_.get(t);
        if (type != null) return type;

        type = new Type(i);

        types_.put(t, type);
        return type;
    }

    public Set<String> typeNames()
    {
        return types_.keySet();
    }

    Pattern pattern_;

    public Typing()
    {
        pattern_ = new Pattern();
    }

    Pattern pattern()
    {
        return pattern_;
    }

    public void extractInfoFromAxiom(Formula f, SymbolTable symbols)
    {
        boolean ok = extractPossibleDefinitionFromEquals(f, symbols);
        if (ok) return;
        extractPossiblePartition(f, symbols);
        extractPossibleTypesFromPredicate(f, symbols);
    }

    public void extractInfoFromTheorem(Formula f, SymbolTable symbols)
    {
        extractPossibleTypesFromPredicate(f, symbols);
    }

    public void extractInfoFromInvariant(Formula f, SymbolTable symbols)
    {
        extractPossibleTypesFromPredicate(f, symbols);
    }

    public void extractInfoFromGuard(Guard g, SymbolTable symbols)
    {
        boolean b = extractPossibleDefinitionFromEquals(g.formula(), symbols);
        if (b) {
            log.debug("guard %s defines a value.", g.name());
            g.setAsDefiningValue();
            return;
        }
        extractPossibleTypesFromPredicate(g.formula(), symbols);
    }

    public void extractInfoFromWitness(Witness w, SymbolTable symbols)
    {
        // Can/should any typing be extracted here?
    }

    public void extractInfoFromAction(Action a, SymbolTable symbols)
    {
        extractPossibleTypesFromBecome(a.formula(), symbols);
    }

    private void extractPossiblePartition(Formula f, SymbolTable fc)
    {
        if (f.is(PARTITION) &&
            f.left().is(SET_SYMBOL) &&
            f.right().is(LIST_OF_EXPRESSIONS))
        {
            Formula sym = f.left();
            Formula list_of_sets = f.right();

            for (int i=0; i < list_of_sets.numChildren(); ++i)
            {
                Formula s = list_of_sets.child(i);
                addSetOfConstantsToSet(s, sym, fc);
            }
        }
    }

    private void addSetOfConstantsToSet(Formula s, Formula sym, SymbolTable fc)
    {
        if (!sym.is(SET_SYMBOL)) return;
        if (s.is(ENUMERATED_SET))
        {
            for (int j=0; j < s.numChildren(); ++j)
            {
                Formula m = s.child(j);
                if (m.is(CONSTANT_SYMBOL))
                {
                    Constant c = fc.getConstant(m.symbol());
                    CarrierSet cs = fc.getSet(sym.symbol());
                    if (cs == null)
                    {
                        log.debug("could not find set based on symbol %s", sym.symbol());
                    }
                    else if (c == null)
                    {
                        log.debug("could not find constant based on symbol %s", m.symbol());
                    }
                    else
                    {
                        cs.addMember(c);
                        c.setType(lookupType(sym));
                        log.debug("adding member %s to %s", m.symbol(), sym.symbol());
                    }
                }
            }
        }
    }

    /**
     * Examine the predicate in the context of the symbol table to extract
     * type information for the variables and constants, assuming that the
     * given predicate is of course true/holds/is valid.
     */
    private void extractPossibleTypesFromPredicate(Formula predicate, SymbolTable symbols)
    {
        boolean ok = pattern().match(predicate,
                                     "conjunction",     "P & Q",
                                     "expr_equals",     "E = F",
                                     "var_primitive",   "x:NAT",
                                     "var_primitive",   "x:NAT1",
                                     "var_primitive",   "x:INT",
                                     "var_primitive",   "x:BOOL",
                                     "const_primitive", "c:NAT",
                                     "const_primitive", "c:NAT1",
                                     "const_primitive", "c:INT",
                                     "const_primitive", "c:BOOL",
                                     // The next match must be after the primitive sets above.
                                     // Otherwise the S will match the primitive sets.
                                     "var_greater_than","x>N",
                                     "const_greater_than","c>N",
                                     "var_in_set",      "x:S",
                                     "const_in_set",    "c:S",
                                     "var_in_var",      "x:y",
                                     "var_is_subset",   "x<:S",
                                     "var_is_subset_of_var", "x<:y");

        if (!ok)
        {
            log.debug("could not find any type from the predicate: "+predicate);
            return;
        }

        String rule = pattern().matchedRule();

        log.debug("extracting possible types (%s) from predicate \"%s\"", rule, predicate);

        switch (rule)
        {
        case "conjunction": // P & Q
        {
            // Recurse into the sub-formulas and extract possible type information.
            extractPossibleTypesFromPredicate(predicate.left(), symbols);
            extractPossibleTypesFromPredicate(predicate.right(), symbols);
            return;
        }
        case "expr_equals": // E = F
        {
            // Since this is an equals comparison, the types should be equals as well.
            Type left = deducePossibleTypesFromExpression(predicate.left(), symbols);
            Type right = deducePossibleTypesFromExpression(predicate.right(), symbols);
            // As a side effect the deduce possible types from expression, it will
            // find any types used inside the expression as well.
            Type empty_set = lookup("{}");

            if (left != null && right != null && !left.equals(empty_set) && !right.equals(empty_set))
            {
                // Both are the non-empty set, lets compare.
                if (!left.equals(right))
                {
                    log.debug("Oups! Types do not match for equals %s left %s right %s", predicate, left, right);
                }
            }
            return;
        }
        case "var_primitive": // x:NAT
        {
            Variable variable = symbols.getVariable(pattern().getVar("x"));
            Type type = variable.updateType(lookupType(predicate.right()));
            log.debug("typing variable %s to %s", variable, type);
            return;
        }
        case "const_primitive": // c:NAT
        {
            Constant constant = symbols.getConstant(pattern().getConst("c"));
            Type type = constant.updateType(lookupType(predicate.right()));
            log.debug("typing constant %s to %s", constant, type);
            return;
        }
        case "var_greater_than": // x>N
        {
            Variable variable = symbols.getVariable(pattern().getVar("x"));
            Formula n = pattern().getNumber("N");
            Type type = lookup("NAT");
            if (n.intData() > 0)
            {
                type = lookup("NAT1");
            }
            variable.setType(type);
            log.debug("typing variable %s to %s", variable, type);
            return;
        }
        case "const_greater_than": // c>N
        {
            Constant constant = symbols.getConstant(pattern().getConst("c"));
            Formula n = pattern().getNumber("N");
            Type type = lookup("NAT");
            if (n.intData() > 0)
            {
                type = lookup("NAT1");
            }
            constant.setType(type);
            log.debug("typing constant %s to %s", constant, type);
            return;
        }
        case "var_in_set": // x:S used for dir:DIR ids:POW(NAT)
        {
            Variable member = symbols.getVariable(pattern().getVar("x"));

            Formula S = pattern().getSet("S");
            if (S.isSymbol())
            {
                // Its a set symbol, could be DIR for example.
                CarrierSet set  = symbols.getSet(S.symbol());
                assert (set != null) : "internal error: set not found: "+pattern().getSet("S")+" in "+symbols.tree();
                Type type = member.updateType(lookup(set.name())); // A carrier set is its own type!
                log.debug("typing variable %s to carrier set %s in %s", member, type, symbols.tree());
            }
            else
            {
                // It is not a symbol, could be a set like POW(NAT).
                Type type = member.updateType(lookupType(S));
                log.debug("typing variable %s to set %s", member, type);
            }
            return;
        }
        case "const_in_set": // c:S used for MakeFlot:NAT**NAT --> Float
        {
            Constant member = symbols.getConstant(pattern().getConst("c"));

            Formula S = pattern().getSet("S");
            if (S.isSymbol())
            {
                // Its a set symbol, could be DIR for example.
                CarrierSet set  = symbols.getSet(S.symbol());
                assert (set != null) : "internal error: set not found: "+pattern().getSet("S")+" in "+symbols.tree();
                Type type = member.updateType(lookup(set.name())); // A carrier set is its own type!
                log.debug("typing constant %s to carrier set %s in %s", member, type, symbols.tree());
            }
            else
            {
                // It is not a symbol, coult be a set like POW(NAT).
                Type type = member.updateType(lookupType(S));
                log.debug("typing constant %s to set %s", member, type);
            }
            return;
        }
        case "var_in_var": // x:y used for book:books
        {
            Variable member = symbols.getVariable(pattern().getVar("x"));
            Variable set    = symbols.getVariable(pattern().getVar("y"));
            Type type = member.updateType(deduceInnerType(set.type()));
            log.debug("typing variable %s to %s through %s", member, type, set);
            return;
        }
        case "var_is_subset": // x<:NAT used for id  ⊆ ℕ
        {
            Variable member = symbols.getVariable(pattern().getVar("x"));

            Formula S = pattern().getSet("S");
            if (S.isSymbol())
            {
                // Its a set symbol, could be DIR for example.
                CarrierSet set  = symbols.getSet(S.symbol());
                assert (set != null) : "internal error: set not found: "+pattern().getSet("S")+" in "+symbols.tree();
                Type type = lookup(set.name());
                type = lookupType(FormulaFactory.newPowerSet(type.formula()));
                type = member.updateType(type);
                log.debug("typing (subset) variable %s to %s in %s", member, type, symbols.tree());
            }
            else
            {
                // It is not a symbol, could be a set like POW(NAT).
                Type type = lookupType(S);
                type = lookupType(FormulaFactory.newPowerSet(type.formula()));
                type = member.updateType(type);
                log.debug("typing (subset) variable %s to %s", member, type);
            }
            return;
        }
        case "var_is_subset_of_var": // x<:y used for ids_to_move<:ids
        {
            Variable x = symbols.getVariable(pattern().getVar("x"));
            Variable y = symbols.getVariable(pattern().getVar("y"));
            if (y.type() == null)
            {
                log.debug("No type known for %s in predicate: %s", y, predicate);
                return;
            }
            log.debug("%s has type %s", y, y.type());
            Type type = deduceInnerType(y.type());
            if (type == null)
            {
                log.debug("Could not deduce inner type of %s", y.type());
                return;
            }
            type = lookupType(FormulaFactory.newPowerSet(type.formula()));
            type = x.updateType(type);
            log.debug("typing (subset of var) variable %s to %s through %s", x, type, symbols.tree());
            return;
        }

        default:
            assert (false) : "internal error: unknown rule "+rule;
        }
    }


    /**
     * This function has a type and forces it onto an expression.
     */
    public void forceTypeOnExpression(Formula expression, Type type, SymbolTable symbols)
    {
        assert type != null;

        boolean ok = pattern().match(expression,
                                     "variable", "x");

        if (!ok)
        {
            log.debug("could force type %s on expression %s", type, expression);
            return;
        }

        String rule = pattern().matchedRule();

        log.debug("forcing (%s) type %s on expression \"%s\"", rule, type, expression);

        switch (rule)
        {
        case "variable":
        {
            Formula v = pattern().getVar("x");
            Variable var = symbols.getVariable(v);
            if (var.type() != null)
            {
                if (var.type().equals(type))
                {
                    // The same correct type has been deduced!
                    log.debug("variable %s alread has type %s", var, type);
                    return;
                }
                else
                {
                    log.debug("Oups! Not possible to force type %s onto variable %s with type %s",
                             type, var, var.type());
                    return;
                }
            }
            log.debug("forcing type %s onto variable %s", type, var);
            var.setType(type);
            return;
        }
        default:
            assert (false) : "internal error: missing alternative "+rule;
        }
    }

    /**
     * This function tries to deduce the type of an expression.
     * It does so by walking into the the expression and find the
     * appropriate type
     */
    public Type deducePossibleTypesFromExpression(Formula expression, SymbolTable symbols)
    {
        boolean ok = pattern().match(expression,
                                     "nat", "N",
                                     "nat", "E + E",
                                     "nat", "E - E",
                                     "nat", "E * E",
                                     "nat", "E / E",
                                     "ran", "ran(E)",
                                     "dom", "dom(S+->T)",
                                     "dom", "dom(S-->T)",
                                     "fapp", "y(E)",
                                     "fcapp", "c(E)",
                                     "constant", "c",
                                     "variable", "x",
                                     "same_set_type", "E \\/ F",
                                     "same_set_type", "E /\\ F");

        if (!ok)
        {
            log.debug("could not find type from expression "+expression);
            return null;
        }

        String rule = pattern().matchedRule();

        log.debug("deducing possible type (%s) from expression \"%s\"", rule, expression);

        switch (rule)
        {
        case "nat":
        {
            Type type = lookup("NAT");
            log.debug("deduced type %s from nat expression %s", type, expression);
            return type;
        }
        case "ran":
        {
            Formula expr = pattern().getExpr("E");
            Type type = deducePossibleTypesFromExpression(expr, symbols);
            if (!type.formula().node().isRelation())
            {
                log.info("Oups! Applying ran to type %s is not possible.", expr);
                return null;
            }
            Type target_type = lookupType(type.formula().range());
            // But the actual range type is the power set of this range.
            Formula range = FormulaFactory.newPowerSet(target_type.formula());
            Type range_type = lookupType(range);
            log.debug("deduced type %s from ran expression %s", range_type, expression);
            return range_type;
        }
        case "dom":
        {
            Type type = lookupType(pattern().getSet("S"));
            log.debug("deduced type %s from rel expression %s", type, expression);
            return type;
        }
        case "fapp":
        {
            Formula funcvar = pattern().getVar("y");
            Variable var = symbols.getVariable(funcvar.toString());
            if (var.type() == null)
            {
                log.info("Oups! No type found for %s in predicate: %s", var, expression);
                return null;
            }
            Type type = lookupType(var.type().formula().range());
            log.debug("deduced type %s from fapp expression %s", type, expression);
            return type;
        }
        case "fcapp":
        {
            Formula funcc = pattern().getConst("c");
            Constant cons = symbols.getConstant(funcc);
            if (cons.type() == null)
            {
                log.info("Oups! No type found for %s in predicate: %s", cons, expression);
                return null;
            }
            Type type = lookupType(cons.type().formula().range());
            log.debug("deduced type %s from fcapp expression %s", type, expression);
            return type;
        }
        case "constant":
        {
            Formula c = pattern().getConst("c");
            Constant cons = symbols.getConstant(c);
            if (cons.type() == null)
            {
                log.debug("Hmhm, no type found for const %s in predicate: %s", cons, expression);
                return null;
            }
            Type type = lookupType(cons.type().formula());
            log.debug("deduced type %s from const %s", type, expression);
            return type;
        }
        case "variable":
        {
            Formula x = pattern().getVar("x");
            Variable var = symbols.getVariable(x);
            if (var.type() == null)
            {
                // Its ok if a variable not yet has a type.
                log.debug("Hmhm, no type found for var %s in predicate: %s", var, expression);
                return null;
            }
            Type type = lookupType(var.type().formula());
            log.debug("deduced type %s from var %s", type, expression);
            return type;
        }
        case "same_set_type":
        {
            Formula left = pattern().getExpr("E");
            Formula right = pattern().getExpr("F");
            Type left_type = deducePossibleTypesFromExpression(left, symbols);
            Type right_type = deducePossibleTypesFromExpression(right, symbols);

            if (left_type != null && right_type != null)
            {
                if (left_type.equals(right_type))
                {
                    // Both sides has the same type. Good, we are done here.
                    log.debug("deduced same type %s for both sides", left_type);
                    return left_type;
                }
                log.info("Error! Left and right types in conjunction/disjunction do not match! %s %s %s",
                         left_type, right_type, expression);
                return null;
            }
            // If you make conjunction or disjunction between
            // a variable and a set. Then the types must match.
            // We can use this to deduce the type of the other side.
            if (left_type == null && right_type != null)
            {
                forceTypeOnExpression(left, right_type, symbols);
                return right_type;
            }
            if (left_type != null && right_type == null)
            {
                forceTypeOnExpression(right, left_type, symbols);
                return left_type;
            }
            // Neither left nor right type found.
            return null;
        }
        default:
            assert (false) : "internal error: missing alternative "+rule;
        }
        return null;
    }

    /**
     * This function examines the formula (in the context of the given symbol table)
     * to deduce if the formula counts as a definition. I.e. the left hand side
     * variable must be equal to a right hand side expression.
    */
    private boolean extractPossibleDefinitionFromEquals(Formula f, SymbolTable symbols)
    {
        boolean ok = pattern().match(f,
                                     "bool_func",    "x = bool(P)",
                                     "var_def_func", "x = y(E)",
                                     "var_def_const_func", "x = c(E)",
                                     "var_def",      "x = E",
                                     "const_def",    "c = E",
                                     "set_def",      "S = { A }");

        if (!ok) return false;

        switch (pattern().matchedRule())
        {
        case "bool_func" : // x = bool(P)
        {
            Formula lvalue = pattern().getVar("x");
            Formula rvalue = pattern().getPred("P");
            Variable var = symbols.getVariable(lvalue.symbol());
            Type type = var.updateType(lookup("BOOL"));
            log.debug("defining variable %s to be bool(%s) of type %s", var.name(), rvalue, type);
            return true;
        }
        case "var_def": // x = E
        {
            Formula lvalue = pattern().getVar("x");
            Formula rvalue = pattern().getExpr("E");
            Variable var = symbols.getVariable(lvalue.symbol());
            Type type = var.updateType(deducePossibleTypesFromExpression(rvalue, symbols));
            var.setDefinition(rvalue);
            log.debug("defining variable %s to be var_def %s of type %s", var.name(), var.definition(), type);
            return true;
        }
        case "const_def": // c = E
        {
            Formula lvalue = pattern().getConst("c");
            Formula rvalue = pattern().getExpr("E");

            Constant co = symbols.getConstant(lvalue.symbol());
            Type type = co.updateType(deducePossibleTypesFromExpression(rvalue, symbols));
            co.setDefinition(rvalue);
            log.debug("defining constant %s to be const_def %s of type %s", co.name(), co.definition(), type);
            return true;
        }
        case "set_def": // S = { A }
        {
            Formula lvalue = pattern().getSet("S");
            Formula rvalue = pattern().getAny("A");
            addSetOfConstantsToSet(rvalue, lvalue, symbols);
            return true;
        }
        case "var_def_func": // x = y(E)
        {
            Formula fapp = f.right();
            Formula var = pattern().getVar("x");
            Formula func = pattern().getVar("y");
            Formula index = pattern().getExpr("E");
            Variable variable = symbols.getVariable(var.symbol());
            Type type = variable.updateType(deducePossibleTypesFromExpression(fapp, symbols));
            variable.setDefinition(f.right());
            log.debug("defining variable %s to be var_def_func %s of type %s", variable.name(), variable.definition(), type);
            return true;
        }
        case "var_def_const_func": // x = c(E)
        {
            Formula fapp = f.right();
            Formula var = pattern().getVar("x");
            Formula func = pattern().getConst("c");
            Formula index = pattern().getExpr("E");
            Variable variable = symbols.getVariable(var.symbol());
            Type type = variable.updateType(deducePossibleTypesFromExpression(fapp, symbols));
            variable.setDefinition(f.right());
            log.debug("defining variable %s to be var_def_const_func %s of type %s", variable.name(), variable.definition(), type);
            return true;
        }
        }
        return false;
    }

    /**
      mynumbers:POW(NAT) this is the same as mynumbers <: NAT
      ie, numbers is a subset of the natural numbers.

      So what type has x in x:nynumbers ?

      It is x : _ : POW(NAT)
      ie. x i a member of something that is a member of POW(NAT):

     */
    public Type memberMustBeOfThisType()
    {
        return null;
    }

    public Type deduceInnerType(Type type)
    {
        Formula i = type.formula();
        log.debug("deducing inner type %s", i);

        if (i.is(POWER_SET))
        {
            return lookupType(i.child());
        }
        if (i.node().isRelation() || i.node().isFunction())
        {
            // x <: y and y : FUNCTION
            // This kind of assumes that FUNCTION is not a total function? Right?
            // Because otherwise a subset of a total function, might not be total anymore?
            // Should I test for this here?
            // Or if FUNCTION is a total function, then the returned type should be a partial function?
            return type;
        }

        assert (false) : "Could not deduce inner type for: "+type;
        return null;
    }

    private void extractPossibleTypesFromBecome(Formula become, SymbolTable symbols)
    {
        boolean ok = pattern().match(become,
                                     "expression",     "x := E",
                                     "const",          "x := c");

        if (!ok)
        {
            log.debug("could not find any type from the become: "+become);
            return;
        }

        String rule = pattern().matchedRule();

        switch (rule)
        {
        case "expression": // x := E
        {
            Formula lvalue = pattern().getVar("x");
            Formula rvalue = pattern().getExpr("E");
            Variable var = symbols.getVariable(lvalue.symbol());

            Type type = var.updateType(deducePossibleTypesFromExpression(rvalue, symbols));
            log.debug("typing (become) variable %s to %s", lvalue, type);
            return;
        }
        case "const": // x := c
        {
            Formula lvalue = pattern().getVar("x");
            Formula rvalue = pattern().getConst("c");
            Variable var = symbols.getVariable(lvalue.symbol());

            Type type = var.updateType(deducePossibleTypesFromExpression(rvalue, symbols));
            log.debug("typing (become) variable %s to %s", lvalue, type);
            return;
        }
        }
    }

    static Type mostSpecificType(Type a, Type b)
    {
        return a;
    }
}
