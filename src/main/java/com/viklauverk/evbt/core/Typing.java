/*
 Copyright (C) 2021 Viklauverk AB (agpl-3.0-or-later)

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

package com.viklauverk.evbt.core;

import com.viklauverk.common.log.Log;
import com.viklauverk.common.log.LogModule;

import static com.viklauverk.evbt.core.Node.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class Typing
{
    private static Log log = LogModule.lookup("typing", Typing.class);

    private Map<String,CheckedType> checked_types_ = new HashMap<>();
    private Map<String,ImplType> impl_types_ = new HashMap<>();
    private Sys sys_;
    Pattern pattern_;

    public Typing(Sys sys)
    {
        sys_ = sys;
        pattern_ = new Pattern();
    }

    public CheckedType lookupCheckedType(String t, SymbolTable st)
    {
        CheckedType type = checked_types_.get(t);
        if (type != null) return type;

        Formula f = Formula.fromString(t, st);
        return lookupCheckedType(f);
    }

    public CheckedType lookupCheckedType(Formula i)
    {
        String t = i.toString();

        CheckedType type = checked_types_.get(t);
        if (type != null) return type;

        type = new CheckedType(i);

        checked_types_.put(t, type);
        return type;
    }

    public ImplType lookupImplType(String t)
    {
        ImplType type = impl_types_.get(t);
        if (type != null) return type;

        Formula f = Formula.fromString(t, new SymbolTable(""));
        return lookupImplType(f);
    }

    public ImplType lookupImplType(Formula i)
    {
        String t = i.toString();

        ImplType type = impl_types_.get(t);
        if (type != null) return type;

        type = new ImplType(i);

        impl_types_.put(t, type);
        return type;
    }

    public Set<String> implTypeNames()
    {
        return impl_types_.keySet();
    }

    public Set<String> checkedTypeNames()
    {
        return checked_types_.keySet();
    }

    Pattern pattern()
    {
        return pattern_;
    }

    public void extractInfoFromAxiom(Formula f, SymbolTable symbols)
    {
        SpecialisedDataType.findSpecialisations(f, sys_);
        boolean ok = extractPossibleDefinitionFromEquals(f, symbols);
        if (ok) return;
        extractPossiblePartition(f, symbols);
        extractPossibleImplTypesFromPredicate(f, symbols);
    }

    public void extractInfoFromTheorem(Formula f, SymbolTable symbols)
    {
        SpecialisedDataType.findSpecialisations(f, sys_);
        extractPossibleImplTypesFromPredicate(f, symbols);
    }

    public void extractInfoFromInvariant(Formula f, SymbolTable symbols)
    {
        SpecialisedDataType.findSpecialisations(f, sys_);
        extractPossibleImplTypesFromPredicate(f, symbols);
    }

    public void extractInfoFromVariant(Formula f, SymbolTable symbols)
    {
        SpecialisedDataType.findSpecialisations(f, sys_);
        extractPossibleImplTypesFromPredicate(f, symbols);
    }

    public void extractInfoFromGuard(Guard g, SymbolTable symbols)
    {
        SpecialisedDataType.findSpecialisations(g.formula(), sys_);
        boolean b = extractPossibleDefinitionFromEquals(g.formula(), symbols);
        if (b) {
            log.debug("guard %s defines a value.", g.name());
            g.setAsDefiningValue();
            return;
        }
        extractPossibleImplTypesFromPredicate(g.formula(), symbols);
    }

    public void extractInfoFromWitness(Witness w, SymbolTable symbols)
    {
        // Can/should any typing be extracted here?
    }

    public void extractInfoFromAction(Action a, SymbolTable symbols)
    {
        SpecialisedDataType.findSpecialisations(a.formula(), sys_);
        extractPossibleImplTypesFromBecome(a.formula(), symbols);
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
                        c.setImplType(lookupImplType(sym));
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
    private void extractPossibleImplTypesFromPredicate(Formula predicate, SymbolTable symbols)
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
                                     "var_in_set",       "x:S",
                                     "const_in_set",     "c:S",
                                     "var_in_var",      "x:y",
                                     "var_is_subset",   "x<:S",
                                     "var_is_subset_of_var", "x<:y",
                                     "var_in_pdt",      "x:H",
                                     "var_in_pdt",      "x:H(A)");

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
            extractPossibleImplTypesFromPredicate(predicate.left(), symbols);
            extractPossibleImplTypesFromPredicate(predicate.right(), symbols);
            return;
        }
        case "expr_equals": // E = F
        {
            // Since this is an equals comparison, the types should be equals as well.
            ImplType left = deducePossibleImplTypesFromExpression(predicate.left(), symbols);
            ImplType right = deducePossibleImplTypesFromExpression(predicate.right(), symbols);
            // As a side effect the deduce possible types from expression, it will
            // find any types used inside the expression as well.
            ImplType empty_set = lookupImplType("{}");

            if (left != null && right != null && !left.equals(empty_set) && !right.equals(empty_set))
            {
                // Both are the non-empty set, lets compare.
                if (!left.equals(right))
                {
                    log.debug("Oups! Implementation types do not match for equals %s left %s right %s", predicate, left, right);
                }
            }
            return;
        }
        case "var_primitive": // x:NAT
        {
            Variable variable = symbols.getVariable(pattern().getVar("x"));
            ImplType type = variable.updateImplType(lookupImplType(predicate.right()));
            log.debug("typing variable %s to %s", variable, type);
            return;
        }
        case "const_primitive": // c:NAT
        {
            Constant constant = symbols.getConstant(pattern().getConst("c"));
            ImplType type = constant.updateImplType(lookupImplType(predicate.right()));
            log.debug("typing constant %s to %s", constant, type);
            return;
        }
        case "var_greater_than": // x>N
        {
            Variable variable = symbols.getVariable(pattern().getVar("x"));
            Formula n = pattern().getNumber("N");
            ImplType type = lookupImplType("NAT");
            if (n.intData() > 0)
            {
                type = lookupImplType("NAT1");
            }
            variable.setImplType(type);
            log.debug("typing variable %s to %s", variable, type);
            return;
        }
        case "const_greater_than": // c>N
        {
            Constant constant = symbols.getConstant(pattern().getConst("c"));
            Formula n = pattern().getNumber("N");
            ImplType type = lookupImplType("NAT");
            if (n.intData() > 0)
            {
                type = lookupImplType("NAT1");
            }
            constant.setImplType(type);
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
                ImplType type = member.updateImplType(lookupImplType(set.name())); // A carrier set is its own type!
                log.debug("typing variable %s to carrier set %s in %s", member, type, symbols.tree());
            }
            else
            {
                // It is not a symbol, could be a set like POW(NAT).
                ImplType type = member.updateImplType(lookupImplType(S));
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
                ImplType type = member.updateImplType(lookupImplType(set.name())); // A carrier set is its own type!
                log.debug("typing constant %s to carrier set %s in %s", member, type, symbols.tree());
            }
            else
            {
                // It is not a symbol, coult be a set like POW(NAT).
                ImplType type = member.updateImplType(lookupImplType(S));
                log.debug("typing constant %s to set %s", member, type);
            }
            return;
        }
        case "var_in_var": // x:y used for book:books
        {
            Variable member = symbols.getVariable(pattern().getVar("x"));
            Variable set    = symbols.getVariable(pattern().getVar("y"));
            ImplType type = member.updateImplType(deduceInnerImplType(set.implType()));
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
                ImplType type = lookupImplType(set.name());
                type = lookupImplType(FormulaFactory.newPowerSet(type.formula(), Formula.NO_META));
                type = member.updateImplType(type);
                log.debug("typing (subset) variable %s to %s in %s", member, type, symbols.tree());
            }
            else
            {
                // It is not a symbol, could be a set like POW(NAT).
                ImplType type = lookupImplType(S);
                type = lookupImplType(FormulaFactory.newPowerSet(type.formula(), Formula.NO_META));
                type = member.updateImplType(type);
                log.debug("typing (subset) variable %s to %s", member, type);
            }
            return;
        }
        case "var_is_subset_of_var": // x<:y used for ids_to_move<:ids
        {
            Variable x = symbols.getVariable(pattern().getVar("x"));
            Variable y = symbols.getVariable(pattern().getVar("y"));
            if (y.implType() == null)
            {
                log.debug("No implementation type known for %s in predicate: %s", y, predicate);
                return;
            }
            log.debug("%s has type %s", y, y.implType());
            ImplType type = deduceInnerImplType(y.implType());
            if (type == null)
            {
                log.debug("Could not deduce inner type of %s", y.implType());
                return;
            }
            type = lookupImplType(FormulaFactory.newPowerSet(type.formula(), Formula.NO_META));
            type = x.updateImplType(type);
            log.debug("typing (subset of var) variable %s to %s through %s", x, type, symbols.tree());
            return;
        }
        case "var_in_pdt": // x:H(A) used for element:List(INT) and weight:Real
        {
            Variable member = symbols.getVariable(pattern().getVar("x"));

            Formula H = pattern().getPolymorphicDataType("H");
            // H=List(ℤ)
            assert(H.isSymbol()); // Yes List is a symbol (and there is more types)

            // We have List(INT) now lookup just List.
            PolymorphicDataType pdt  = symbols.getPolymorphicDataType(H.symbol());
            assert (pdt != null) : "internal error: polymorphic data type not found: "+pattern().getPolymorphicDataType("H")+" in "+symbols.tree();
            // FIXME String specialisation = H.toString(); // This will generate again, for example: List(ℤ)
            ImplType type = member.updateImplType(lookupImplType(H)); // Lookup the specialisation of this pdt.
            log.debug("typing variable %s to data type %s in %s", member, type, symbols.tree());
            return;
        }

        default:
            assert (false) : "internal error: unknown rule "+rule;
        }
    }


    /**
     * This function has a type and forces it onto an expression.
     */
    public void forceImplTypeOnExpression(Formula expression, ImplType type, SymbolTable symbols)
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
            if (var.implType() != null)
            {
                if (var.implType().equals(type))
                {
                    // The same correct type has been deduced!
                    log.debug("variable %s alread has type %s", var, type);
                    return;
                }
                else
                {
                    log.debug("Oups! Not possible to force implementation type %s onto variable %s with implementation type %s",
                             type, var, var.implType());
                    return;
                }
            }
            log.debug("forcing type %s onto variable %s", type, var);
            var.setImplType(type);
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
    public ImplType deducePossibleImplTypesFromExpression(Formula expression, SymbolTable symbols)
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
            ImplType type = lookupImplType("NAT");
            log.debug("deduced type %s from nat expression %s", type, expression);
            return type;
        }
        case "ran":
        {
            Formula expr = pattern().getExpr("E");
            ImplType type = deducePossibleImplTypesFromExpression(expr, symbols);
            if (!type.formula().node().isRelation())
            {
                log.info("Oups! Applying ran to type %s is not possible.", expr);
                return null;
            }
            ImplType target_type = lookupImplType(type.formula().range());
            // But the actual range type is the power set of this range.
            Formula range = FormulaFactory.newPowerSet(target_type.formula(), Formula.NO_META);
            ImplType range_type = lookupImplType(range);
            log.debug("deduced type %s from ran expression %s", range_type, expression);
            return range_type;
        }
        case "dom":
        {
            ImplType type = lookupImplType(pattern().getSet("S"));
            log.debug("deduced type %s from rel expression %s", type, expression);
            return type;
        }
        case "fapp":
        {
            Formula funcvar = pattern().getVar("y");
            Variable var = symbols.getVariable(funcvar.toString());
            if (var.implType() == null)
            {
                log.info("Oups! No implementation type found for variable: %s in predicate: %s in: %s %s",
                         var, expression, var.machine(), var.event());
                return null;
            }
            ImplType type = lookupImplType(var.implType().formula().range());
            log.debug("deduced type %s from fapp expression %s", type, expression);
            return type;
        }
        case "fcapp":
        {
            Formula funcc = pattern().getConst("c");
            Constant cons = symbols.getConstant(funcc);
            if (cons.implType() == null)
            {
                log.info("Oups! No implementation type found for constant: %s in predicate: %s in: %s",
                         cons, expression, cons.context());
                return null;
            }
            ImplType type = lookupImplType(cons.implType().formula().range());
            log.debug("deduced type %s from fcapp expression %s", type, expression);
            return type;
        }
        case "constant":
        {
            Formula c = pattern().getConst("c");
            Constant cons = symbols.getConstant(c);
            if (cons.implType() == null)
            {
                log.debug("Hmhm, no type found for const %s in predicate: %s", cons, expression);
                return null;
            }
            ImplType type = lookupImplType(cons.implType().formula());
            log.debug("deduced type %s from const %s", type, expression);
            return type;
        }
        case "variable":
        {
            Formula x = pattern().getVar("x");
            Variable var = symbols.getVariable(x);
            if (var.implType() == null)
            {
                // Its ok if a variable not yet has a type.
                log.debug("Hmhm, no type found for var %s in predicate: %s", var, expression);
                return null;
            }
            ImplType type = lookupImplType(var.implType().formula());
            log.debug("deduced type %s from var %s", type, expression);
            return type;
        }
        case "same_set_type":
        {
            Formula left = pattern().getExpr("E");
            Formula right = pattern().getExpr("F");
            ImplType left_type = deducePossibleImplTypesFromExpression(left, symbols);
            ImplType right_type = deducePossibleImplTypesFromExpression(right, symbols);

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
                forceImplTypeOnExpression(left, right_type, symbols);
                return right_type;
            }
            if (left_type != null && right_type == null)
            {
                forceImplTypeOnExpression(right, left_type, symbols);
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
            ImplType type = var.updateImplType(lookupImplType("BOOL"));
            log.debug("defining variable %s to be bool(%s) of type %s", var.name(), rvalue, type);
            return true;
        }
        case "var_def": // x = E
        {
            Formula lvalue = pattern().getVar("x");
            Formula rvalue = pattern().getExpr("E");
            Variable var = symbols.getVariable(lvalue.symbol());
            ImplType type = var.updateImplType(deducePossibleImplTypesFromExpression(rvalue, symbols));
            var.setDefinition(rvalue);
            log.debug("defining variable %s to be var_def %s of type %s", var.name(), var.definition(), type);
            return true;
        }
        case "const_def": // c = E
        {
            Formula lvalue = pattern().getConst("c");
            Formula rvalue = pattern().getExpr("E");

            Constant co = symbols.getConstant(lvalue.symbol());
            ImplType type = co.updateImplType(deducePossibleImplTypesFromExpression(rvalue, symbols));
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
            // FIXME Formula func = pattern().getVar("y");
            // FIXME Formula index = pattern().getExpr("E");
            Variable variable = symbols.getVariable(var.symbol());
            ImplType type = variable.updateImplType(deducePossibleImplTypesFromExpression(fapp, symbols));
            variable.setDefinition(f.right());
            log.debug("defining variable %s to be var_def_func %s of type %s", variable.name(), variable.definition(), type);
            return true;
        }
        case "var_def_const_func": // x = c(E)
        {
            Formula fapp = f.right();
            Formula var = pattern().getVar("x");
            // FIXME Formula func = pattern().getConst("c");
            // FIXME Formula index = pattern().getExpr("E");
            Variable variable = symbols.getVariable(var.symbol());
            ImplType type = variable.updateImplType(deducePossibleImplTypesFromExpression(fapp, symbols));
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
    public ImplType memberMustBeOfThisImplType()
    {
        return null;
    }

    public ImplType deduceInnerImplType(ImplType type)
    {
        if (type == null) return null;
        Formula i = type.formula();
        log.debug("deducing inner type %s", i);

        if (i.is(POWER_SET))
        {
            return lookupImplType(i.child());
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

    private void extractPossibleImplTypesFromBecome(Formula become, SymbolTable symbols)
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

            ImplType type = var.updateImplType(deducePossibleImplTypesFromExpression(rvalue, symbols));
            log.debug("typing (become) variable %s to %s", lvalue, type);
            return;
        }
        case "const": // x := c
        {
            Formula lvalue = pattern().getVar("x");
            Formula rvalue = pattern().getConst("c");
            Variable var = symbols.getVariable(lvalue.symbol());

            ImplType type = var.updateImplType(deducePossibleImplTypesFromExpression(rvalue, symbols));
            log.debug("typing (become) variable %s to %s", lvalue, type);
            return;
        }
        }
    }

    static ImplType mostSpecificImplType(ImplType a, ImplType b)
    {
        return a;
    }
}
