/*
 Copyright (C) 2021 Viklauverk AB

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

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public
class Pattern
{
    private static Log log = LogModule.lookup("pattern");

    private static Map<String,Formula> match_rules_ = new HashMap<>();

    private final MatchResult match_results_;

    public Pattern()
    {
        match_results_ = new MatchResult();
    }

    public static class MatchResult
    {
        public String rule = "";
        public Map<String,Formula> predicates = new HashMap<>();
        public Map<String,Formula> expressions = new HashMap<>();
        public Map<String,Formula> sets = new HashMap<>();
        public Map<String,Formula> variables = new HashMap<>();
        public Map<String,Formula> constants = new HashMap<>();
        public Map<String,Formula> anys = new HashMap<>();
        public Map<String,Formula> numbers = new HashMap<>();
        public Map<String,Formula> polymorphic_data_types = new HashMap<>();

        public void clear()
        {
            rule = "";
            predicates.clear();
            expressions.clear();
            sets.clear();
            variables.clear();
            constants.clear();
            anys.clear();
            numbers.clear();
            polymorphic_data_types.clear();
        }
    }

    /**
     * Try to match the formula against the given patterns.
     * The patterns are given as pairs (pattern_name,pattern)
     * like this:
     *
     * bool b = pat.match(f, "p1" "x:NAT", "p2", "S-->T")
     *
     * If b is true and pat.matchedRule() is "p1" then
     * pat.getVar("x") will return the part of f that matched x.
     *
     * The patterns are always parsed using a symbol table containing:
     *
     * predicate symbols: PQR
     * expression symbols: EFG
     * set symbols: STU
     * variable symbols: xyz
     * constant_symbols:cdf
     * any symbols: ABC
     * number symbols: NM
     * polymorphic_data_types: H
     */
    public boolean match(Formula formula, String... pattern_strings)
    {
        int n = pattern_strings.length;
        assert (n % 2 == 0) : "internal error: match invoked with wrong number of arguments";
        for (int i=0; i<n; i+=2)
        {
            String rule = pattern_strings[i];
            Formula pattern = lookupMatchRule(pattern_strings[i+1]);
            match_results_.clear();
            boolean ok = tryMatch(formula, pattern, match_results_);
            log.trace("try %s MATCH(%s) %s result %s", formula, rule, pattern, ok);
            if (ok) {
                match_results_.rule = rule;
                return true;
            }
        }

        return false;
    }

    private static Formula lookupMatchRule(String s)
    {
        Formula f = match_rules_.get(s);
        if (f != null) return f;

        f = Formula.fromString(s, SymbolTable.PQR_EFG_STU_xyz_cdf_NM_ABC);
        match_rules_.put(s, f);
        return f;
    }


    private static boolean okToAdd(Formula f, Formula p, Map<String,Formula> prevs, String table_name)
    {
        Node ft = f.node();
        Node pt = p.node();

        Formula prev = prevs.get(p.symbol());
        if (prev == null)
        {
            // First time we find this (P,E,S,v,c) symbol.
            // No checking needed, just store the matching formula.
            prevs.put(p.symbol(), f);
            log.trace("match found %s(%s) == %s(%s) stored %s = %s into table %s", ft, f, pt, p, p.symbol(), f, table_name);
            return true;
        }

        // Aha, we have already matched this (P,E,S,v,c) symbol.
        // Now check that the newly found formula is equivalent
        // to the previously found formula. Otherwise, there is no match.
        boolean ok = prev.equals(f);
        if (ok)
        {
            log.trace(" Already stored match %s === %s in table %s", p.symbol(), f, table_name);
        }
        else
        {
            log.trace(" New match is different! OUCH %s !== %s in table %s", p.symbol(), f, table_name);
        }
        return ok;
    }

    private static boolean tryMetaMatch(Formula f, Formula p, MatchResult mr)
    {
        // Either both has to have a meta, or none must have a meta.
        if (f.hasMeta() != p.hasMeta()) return false;

        if (f.hasMeta())
        {
            log.trace("Matching meta \"%s\" VS \"%s\"", f.meta(), p.meta());
            boolean ok = tryMatch(f.meta(), p.meta(), mr);
            if (ok)
            {
                log.trace("Meta matched ok!");
            }
            else
            {
                log.trace("Meta match failed!");
            }
            if (!ok) return false;
        }
        return true;
    }

    private static boolean tryMatch(Formula f, Formula p, MatchResult mr)
    {
        Node ft = f.node();
        Node pt = p.node();

        log.trace("test %s(%s) == %s(%s)", ft, f, pt, p);

        if (pt == Node.PREDICATE_SYMBOL)
        {
            if (!f.isPredicate()) return false;
            if (!tryMetaMatch(f, p, mr)) return false;
            return okToAdd(f, p, mr.predicates, "predicates");
        }
        if (pt == Node.EXPRESSION_SYMBOL)
        {
            if (!f.isExpression()) return false;
            if (!tryMetaMatch(f, p, mr)) return false;
            return okToAdd(f, p, mr.expressions, "expressions");
        }
        if (pt == Node.SET_SYMBOL)
        {
            if (!f.isSet()) return false;
            if (!tryMetaMatch(f, p, mr)) return false;
            return okToAdd(f, p, mr.sets, "sets");
        }
        if (pt == Node.VARIABLE_SYMBOL)
        {
            if (!f.isVariable()) return false;
            if (!tryMetaMatch(f, p, mr)) return false;
            return okToAdd(f, p, mr.variables, "variables");
        }
        if (pt == Node.CONSTANT_SYMBOL)
        {
            if (!f.isConstant()) return false;
            if (!tryMetaMatch(f, p, mr)) return false;
            return okToAdd(f, p, mr.constants, "constants");
        }
        if (pt == Node.NUMBER_SYMBOL)
        {
            if (!f.isNumber()) return false;
            if (!tryMetaMatch(f, p, mr)) return false;
            return okToAdd(f, p, mr.numbers, "numbers");
        }
        if (pt == Node.ANY_SYMBOL)
        {
            if (!tryMetaMatch(f, p, mr)) return false;
            return okToAdd(f, p, mr.anys, "anys");
        }

        if (pt == Node.POLYMORPHIC_DATA_TYPE_SYMBOL)
        {
            if (!tryMetaMatch(f, p, mr)) return false;
            if (!okToAdd(f, p, mr.polymorphic_data_types, "polymorphic_data_types")) return false;
            // Continue with children types.
        }

        // The node types do not match!
        if (ft != pt) return false;

        // Special case, the pattern has a single any symbol,
        // this can match any number of child symbols.
        if (p.numChildren() == 1 && p.child(0).is(Node.ANY_SYMBOL))
        {
            // BUT! The ANY matches the parent f.
            // Because there is no obvious way to store a list of children, so lets
            // just reuse the parent as the list holder.
            // This means that matching { c, d } with the pattern { A } will match
            // and getAny("A") will return <ENUMERATED_SET {<CONSTANT_SYMBOL c>,<CONSTANT_SYMBOL d>}>
            if (!tryMetaMatch(f, p, mr)) return false;
            return okToAdd(f, p.child(0), mr.anys, "anys");
        }

        // We have identical types, now check recursively if it matches.
        if (f.numChildren() != p.numChildren()) return false;

        int n = f.numChildren();
        for (int i=0; i<n; ++i)
        {
            boolean ok = tryMatch(f.child(i), p.child(i), mr);
            if (!ok) return false;
        }

        // All children matched!
        return true;
    }

    public Formula getPred(String p)
    {
        Formula f = match_results_.predicates.get(p);
        assert (f != null) : "Internal error, could not find predicate "+p+" in matched results.";
        return f;
    }

    public Formula tryGetPred(String p)
    {
        return match_results_.predicates.get(p);
    }

    public Set<String> predicateNames()
    {
        return match_results_.predicates.keySet();
    }

    public Formula getExpr(String e)
    {
        Formula f = match_results_.expressions.get(e);
        assert (f != null) : "Internal error, could not find expression "+e+" in matched results.";
        return f;
    }

    public Formula tryGetExpr(String e)
    {
        return match_results_.expressions.get(e);
    }

    public Set<String> expressionNames()
    {
        return match_results_.expressions.keySet();
    }

    public Formula getSet(String s)
    {
        Formula f = match_results_.sets.get(s);
        assert (f != null) : "Internal error, could not find set "+s+" in matched results.";
        return f;
    }

    public Formula tryGetSet(String s)
    {
        return match_results_.sets.get(s);
    }

    public Set<String> setNames()
    {
        return match_results_.sets.keySet();
    }

    public Formula getVar(String v)
    {
        Formula f = match_results_.variables.get(v);
        assert (f != null) : "Internal error, could not find variable "+v+" in matched results.";
        return f;
    }

    public Formula tryGetVar(String v)
    {
        return match_results_.variables.get(v);
    }

    public Set<String> variableNames()
    {
        return match_results_.variables.keySet();
    }

    public Formula getConst(String c)
    {
        Formula f = match_results_.constants.get(c);
        assert (f != null) : "Internal error, could not find constant "+c+" in matched results.";
        return f;
    }

    public Formula tryGetConst(String c)
    {
        return match_results_.constants.get(c);
    }

    public Set<String> constantNames()
    {
        return match_results_.constants.keySet();
    }

    public Formula getNumber(String v)
    {
        Formula f = match_results_.numbers.get(v);
        assert (f != null) : "Internal error, could not find number "+v+" in matched results.";
        return f;
    }

    public Set<String> numberNames()
    {
        return match_results_.numbers.keySet();
    }

    public Formula getPolymorphicDataType(String v)
    {
        Formula f = match_results_.polymorphic_data_types.get(v);
        assert (f != null) : "Internal error, could not find polymorphic data type "+v+" in matched results.";
        return f;
    }

    public Set<String> polymorphicDataTypeNames()
    {
        return match_results_.polymorphic_data_types.keySet();
    }

    public Formula getAny(String a)
    {
        Formula f = match_results_.anys.get(a);
        assert (f != null) : "Internal error, could not find any-symbol "+a+" in matched results.";
        return f;
    }

    public Formula tryGetAny(String a)
    {
        return match_results_.anys.get(a);
    }

    public Set<String> anyNames()
    {
        return match_results_.anys.keySet();
    }

    public String matchedRule()
    {
        return match_results_.rule;
    }

    public String allMatches()
    {
        StringBuilder sb = new StringBuilder();

        for (String pred : predicateNames())
        {
            sb.append(pred+"="+getPred(pred)+" ");
        }
        for (String e : expressionNames())
        {
            sb.append(e+"="+getExpr(e)+" ");
        }
        for (String s : setNames())
        {
            sb.append(s+"="+getSet(s)+" ");
        }
        for (String v : variableNames())
        {
            sb.append(v+"="+getVar(v)+" ");
        }
        for (String c : constantNames())
        {
            sb.append(c+"="+getConst(c)+" ");
        }
        for (String n : numberNames())
        {
            sb.append(n+"="+getNumber(n)+" ");
        }
        for (String n : polymorphicDataTypeNames())
        {
            sb.append(n+"="+getPolymorphicDataType(n)+" ");
        }
        for (String a : anyNames())
        {
            sb.append(a+"="+getAny(a)+" ");
        }
        return sb.toString().trim();
    }

}
