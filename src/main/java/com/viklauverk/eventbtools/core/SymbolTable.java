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

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SymbolTable
{
    private static Log log = LogModule.lookup("symbols");

    private static Set<String> all_vars = new HashSet<>();

    public SymbolTable(String name)
    {
        name_ = name;
    }

    @Override
    public String toString()
    {
        return name_;
    }

    private String name_;
    private Set<SymbolTable> parents_ = new HashSet<>();
    private Set<String> predicate_symbols_ = new HashSet<>(); // PQR
    private Set<String> expression_symbols_ = new HashSet<>(); // EFG
    private Set<String> set_symbols_ = new HashSet<>(); // STU
    private Map<String,CarrierSet> sets_ = new HashMap<>();
    private Set<String> variable_symbols_ = new HashSet<>(); // xyzw
    private Map<String,Variable> variables_ = new HashMap<>();
    private Set<String> constant_symbols_ = new HashSet<>(); // cdf
    private Map<String,Constant> constants_ = new HashMap<>();
    private Set<String> number_symbols_ = new HashSet<>(); // NM
    private Set<String> any_symbols_ = new HashSet<>(); // ABC

    private LinkedList<Frame> frames_ = new LinkedList<>();

    public String name()
    {
        return name_;
    }

    private static class Frame
    {
        // This stores variables found during parsing of
        // universal (!x,y.) and existential (#x,y.) predicates.
        private Set<String> vars = new HashSet<>();
    }

    public void pushFrame(List<String> vars)
    {
        Frame f = new Frame();
        for (String s : vars)
        {
            f.vars.add(s);
        }
        frames_.addFirst(f);
    }

    public void popFrame()
    {
        frames_.removeFirst();
    }

    public void addParents(List<SymbolTable> parents)
    {
        for (SymbolTable p : parents)
        {
            parents_.add(p);
        }
    }

    public void addParent(SymbolTable p)
    {
        parents_.add(p);
    }

    public Set<String> anySymbols()
    {
        return any_symbols_;
    }

    public Set<String> constantSymbols()
    {
        return constant_symbols_;
    }

    public Set<String> expressionSymbols()
    {
        return expression_symbols_;
    }

    public Set<String> numberSymbols()
    {
        return number_symbols_;
    }

    public Set<String> setSymbols()
    {
        return set_symbols_;
    }

    public Set<String> predicateSymbols()
    {
        return predicate_symbols_;
    }

    public Set<String> variableSymbols()
    {
        return variable_symbols_;
    }

    public boolean isPredicateSymbol(String p)
    {
        boolean is = predicate_symbols_.contains(p);
        if (is) return true;
        for (SymbolTable parent : parents_)
        {
            is = parent.isPredicateSymbol(p);
            if (is) return true;
        }
        return false;
    }

    public void addPredicateSymbol(String s)
    {
        predicate_symbols_.add(s);
    }

    public void addPredicateSymbols(String... s)
    {
        predicate_symbols_.addAll(Arrays.asList(s));
    }

    public void addPredicateSymbols(List<String> s)
    {
        predicate_symbols_.addAll(s);
    }

    public boolean isExpressionSymbol(String e)
    {
        boolean is = expression_symbols_.contains(e);
        if (is) return true;
        for (SymbolTable parent : parents_)
        {
            is = parent.isExpressionSymbol(e);
            if (is) return true;
        }
        return false;
    }

    public void addExpressionSymbol(String s)
    {
        expression_symbols_.add(s);
    }

    public void addExpressionSymbols(String... s)
    {
        expression_symbols_.addAll(Arrays.asList(s));
    }

    public void addExpressionSymbols(List<String> s)
    {
        expression_symbols_.addAll(s);
    }

    public boolean isSetSymbol(String s)
    {
        boolean is = set_symbols_.contains(s);
        if (is) return true;
        for (SymbolTable parent : parents_)
        {
            is = parent.isSetSymbol(s);
            if (is) return true;
        }
        return false;
    }

    public CarrierSet getSet(Formula name)
    {
        return getSet(name.symbol());
    }

    public CarrierSet getSet(String name)
    {
        CarrierSet set = sets_.get(name);
        if (set != null) return set;
        for (SymbolTable parent : parents_)
        {
            set = parent.getSet(name);
            if (set != null) return set;
        }
        return null;
    }

    public void addSetSymbol(String s)
    {
        set_symbols_.add(s);
    }

    public void addSet(CarrierSet cs)
    {
        set_symbols_.add(cs.name());
        sets_.put(cs.name(), cs);
    }

    public void addSetSymbols(String... s)
    {
        set_symbols_.addAll(Arrays.asList(s));
    }

    public void addSetSymbols(List<String> s)
    {
        set_symbols_.addAll(s);
    }

    public boolean isVariableSymbol(String v)
    {
        boolean is = variable_symbols_.contains(v);
        if (is) return true;
        for (SymbolTable parent : parents_)
        {
            is = parent.isVariableSymbol(v);
            if (is) return true;
        }
        return false;
    }

    public boolean isNonFreeVariableSymbol(String v)
    {
        for (Frame f : frames_)
        {
            if (f.vars.contains(v)) {
                return true;
            }
        }
        return false;
    }

    public Variable getVariable(Formula name)
    {
        return getVariable(name.symbol());
    }

    public Variable getVariable(String name)
    {
        Variable var = variables_.get(name);
        if (var != null) return var;
        for (SymbolTable parent : parents_)
        {
            var = parent.getVariable(name);
            if (var != null) return var;
        }
        return null;
    }

    public SymbolTable whichSymbolTableForVariable(String name)
    {
        Variable var = variables_.get(name);
        if (var != null) return this;
        for (SymbolTable parent : parents_)
        {
            SymbolTable st = parent.whichSymbolTableForVariable(name);
            if (st != null) return st;
        }
        return null;
    }

    public void addVariableSymbol(String s)
    {
        log.debug("adding variable %s (symbol only) to symbol table %s", s, name_);
        assert (!variable_symbols_.contains(s)) : "internal error: already added "+s+" to "+name_;
        variable_symbols_.add(s);
    }

    public void addVariable(Variable v)
    {
        log.debug("adding variable %s to symbol table %s", v.name(), name_);
        assert (!variable_symbols_.contains(v.name())) : "internal error: already added "+v.name()+" to "+name_;
        variable_symbols_.add(v.name());
        variables_.put(v.name(), v);
    }

    public void addVariableSymbols(String... s)
    {
        variable_symbols_.addAll(Arrays.asList(s));
    }

    public void addVariableSymbols(List<String> s)
    {
        variable_symbols_.addAll(s);
    }

    public boolean isConstantSymbol(String c)
    {
        boolean is = constant_symbols_.contains(c);
        if (is) return true;
        for (SymbolTable parent : parents_)
        {
            is = parent.isConstantSymbol(c);
            if (is) return true;
        }
        return false;
    }

    public Constant getConstant(Formula name)
    {
        return getConstant(name.symbol());
    }

    public Constant getConstant(String name)
    {
        Constant var = constants_.get(name);
        if (var != null) return var;
        for (SymbolTable parent : parents_)
        {
            var = parent.getConstant(name);
            if (var != null) return var;
        }
        return null;
    }

    public void addConstantSymbol(String s)
    {
        constant_symbols_.add(s);
    }

    public void addConstant(Constant c)
    {
        constant_symbols_.add(c.name());
        constants_.put(c.name(), c);
    }

    public void addConstantSymbols(String... s)
    {
        constant_symbols_.addAll(Arrays.asList(s));
    }

    public void addConstantSymbols(List<String> s)
    {
        constant_symbols_.addAll(s);
    }

    public boolean isNumberSymbol(String p)
    {
        boolean is = number_symbols_.contains(p);
        if (is) return true;
        for (SymbolTable parent : parents_)
        {
            is = parent.isNumberSymbol(p);
            if (is) return true;
        }
        return false;
    }

    public void addNumberSymbol(String s)
    {
        number_symbols_.add(s);
    }

    public void addNumberSymbols(String... s)
    {
        number_symbols_.addAll(Arrays.asList(s));
    }

    public void addNumberSymbols(List<String> s)
    {
        number_symbols_.addAll(s);
    }

    public boolean isAnySymbol(String p)
    {
        boolean is = any_symbols_.contains(p);
        if (is) return true;
        for (SymbolTable parent : parents_)
        {
            is = parent.isAnySymbol(p);
            if (is) return true;
        }
        return false;
    }

    public void addAnySymbol(String s)
    {
        any_symbols_.add(s);
    }

    public void addAnySymbols(String... s)
    {
        any_symbols_.addAll(Arrays.asList(s));
    }

    public void addAnySymbols(List<String> s)
    {
        any_symbols_.addAll(s);
    }

    public boolean isUnknownSymbol(String p)
    {
        if (isPredicateSymbol(p)) return false;
        if (isExpressionSymbol(p)) return false;
        if (isSetSymbol(p)) return false;
        if (isVariableSymbol(p)) return false;
        if (isNonFreeVariableSymbol(p)) return false;
        if (isConstantSymbol(p)) return false;
        if (isNumberSymbol(p)) return false;
        if (isAnySymbol(p)) return false;
        return true;
    }

    private String printSyms(Set<String> syms)
    {
        StringBuilder o = new StringBuilder();
        List<String> ns = syms.stream().sorted().collect(Collectors.toList());
        int c = 0;
        for (String n : ns)
        {
            if (c > 0) o.append(" ");
            o.append(n);
            c++;
        }
        return o.toString();
    }

    public String tree()
    {
        StringBuilder o = new StringBuilder();
        o.append(name_);
        for (SymbolTable p : parents_)
        {
            o.append(" <- ("+p.tree()+")");
        }
        return o.toString();
    }

    public String print()
    {
        Canvas cnvs = new Canvas();
        cnvs.setRenderTarget(RenderTarget.PLAIN);
        StringBuilder o = new StringBuilder();

        for (SymbolTable p : parents_)
        {
            p.print();
        }
        String title = "SymbolTable "+name_;

        o.append("predicates: "+Canvas.flow(80, printSyms(predicate_symbols_)));
        o.append("-\n");
        o.append("expressions: "+Canvas.flow(80, printSyms(expression_symbols_)));
        o.append("-\n");
        o.append("sets: "+Canvas.flow(80, printSyms(set_symbols_)));
        o.append("-\n");
        o.append("variables: "+Canvas.flow(80, printSyms(variable_symbols_)));
        o.append("-\n");
        o.append("constants: "+Canvas.flow(80, printSyms(constant_symbols_)));

        if (any_symbols_.size() > 0)
        {
            o.append("-\n");
            o.append("anys: "+Canvas.flow(80, printSyms(any_symbols_)));
        }
        String f = cnvs.frame(title, o.toString(), Canvas.sline);
        return f;
    }

    public void addDefaults()
    {
        predicate_symbols_.add("P");
        predicate_symbols_.add("Q");
        predicate_symbols_.add("R");

        expression_symbols_.add("E");
        expression_symbols_.add("F");
        expression_symbols_.add("G");

        set_symbols_.add("S");
        set_symbols_.add("T");
        set_symbols_.add("U");

        variable_symbols_.add("x");
        variable_symbols_.add("y");
        variable_symbols_.add("z");

        constant_symbols_.add("c");
        constant_symbols_.add("d");
        constant_symbols_.add("f");

        number_symbols_.add("N");
        number_symbols_.add("M");

        any_symbols_.add("A");
        any_symbols_.add("B");
        any_symbols_.add("C");
    }

    public static SymbolTable PQR_EFG_STU_xyz_cdf_NM_ABC;

    static
    {
        SymbolTable st = new SymbolTable("plain");
        st.addDefaults();
        PQR_EFG_STU_xyz_cdf_NM_ABC = st;
    }
}
