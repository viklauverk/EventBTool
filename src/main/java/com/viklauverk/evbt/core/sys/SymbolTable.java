/*
 Copyright (C) 2021-2024 Viklauverk AB (agpl-3.0-or-later)

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

package com.viklauverk.evbt.core.sys;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.viklauverk.evbt.common.log.Log;
import com.viklauverk.evbt.common.log.LogModule;
import com.viklauverk.evbt.core.EvBFormulaParser;
import com.viklauverk.evbt.core.console.Canvas;
import com.viklauverk.evbt.core.docgen.RenderTarget;

public class SymbolTable
{
    private static Log log = LogModule.lookup("evbt.symbols", SymbolTable.class);

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

    private Set<String> polymorphic_data_type_symbols_ = new HashSet<>(); // H List Seq
    private Map<String,PolymorphicDataType> polymorphic_data_types_ = new HashMap<>();

    private Set<String> constructor_symbols_ = new HashSet<>(); // nil epoch cons
    private Map<String,Constructor> constructors_ = new HashMap<>();

    private Set<String> destructor_symbols_ = new HashSet<>(); // head tail
    private Map<String,Destructor> destructors_ = new HashMap<>();

    private Set<String> operator_symbols_ = new HashSet<>(); // seq seqSize listSize append
    private Map<String,Operator> operators_ = new HashMap<>(); // Theory added operators.

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

    public boolean hasParents()
    {
        return parents_.size() > 0;
    }

    public void addParents(List<SymbolTable> parents)
    {
        for (SymbolTable p : parents)
        {
            assert(p != null);
            parents_.add(p);
        }
    }

    public void addParent(SymbolTable p)
    {
        assert(p != null);
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

    public Set<String> polymorphicDataTypeSymbols()
    {
        return polymorphic_data_type_symbols_;
    }

    public boolean hasAnySymbols()
    {
        return any_symbols_.size() > 0;
    }

    public boolean hasConstantSymbols()
    {
        return constant_symbols_.size() > 0;
    }

    public boolean hasExpressionSymbols()
    {
        return expression_symbols_.size() > 0;
    }

    public boolean hasNumberSymbols()
    {
        return number_symbols_.size() > 0;
    }

    public boolean hasSetSymbols()
    {
        return set_symbols_.size() > 0;
    }

    public boolean hasPredicateSymbols()
    {
        return predicate_symbols_.size() > 0;
    }

    public boolean hasVariableSymbols()
    {
        return variable_symbols_.size() > 0;
    }

    public boolean hasPolymorphicDataTypeSymbols()
    {
        return polymorphic_data_type_symbols_.size() > 0;
    }

    public boolean hasConstructorSymbols()
    {
        return constructor_symbols_.size() > 0;
    }

    public boolean hasDestructorSymbols()
    {
        return destructor_symbols_.size() > 0;
    }

    public boolean hasOperatorSymbols()
    {
        return operators_.size() > 0;
    }

    public PolymorphicDataType getPolymorphicDataType(Formula name)
    {
        return getPolymorphicDataType(name.symbol());
    }

    public PolymorphicDataType getPolymorphicDataType(String name)
    {
        PolymorphicDataType pdt = polymorphic_data_types_.get(name);
        if (pdt != null) return pdt;
        for (SymbolTable parent : parents_)
        {
            pdt = parent.getPolymorphicDataType(name);
            if (pdt != null) return pdt;
        }
        return null;
    }

    public void addPolymorphicDataTypeSymbols(String... s)
    {
        polymorphic_data_type_symbols_.addAll(Arrays.asList(s));
    }

    public Set<String> polymorphicOperatorSymbols()
    {
        return operator_symbols_;
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
            if (f.vars.contains(v)) return true;
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

    // Constructor /////////////////////////////////////////////////////////////

    public Constructor getConstructor(Formula name)
    {
        return getConstructor(name.symbol());
    }

    public Constructor getConstructor(String name)
    {
        Constructor var = constructors_.get(name);
        if (var != null) return var;
        for (SymbolTable parent : parents_)
        {
            var = parent.getConstructor(name);
            if (var != null) return var;
        }
        return null;
    }

    public boolean isConstructorSymbol(String c)
    {
        boolean is = constructor_symbols_.contains(c);
        if (is) return true;
        for (SymbolTable parent : parents_)
        {
            is = parent.isConstructorSymbol(c);
            if (is) return true;
        }
        return false;
    }

    public void addConstructorSymbol(String s)
    {
        constructor_symbols_.add(s);
    }

    public void addConstructor(Constructor c)
    {
        constructor_symbols_.add(c.name());
        constructors_.put(c.name(), c);
    }

    public void addConstructorSymbols(String... s)
    {
        constructor_symbols_.addAll(Arrays.asList(s));
    }

    public void addConstructorSymbols(List<String> s)
    {
        constructor_symbols_.addAll(s);
    }

    // Destructor /////////////////////////////////////////////////////////////

    public Destructor getDestructor(Formula name)
    {
        return getDestructor(name.symbol());
    }

    public Destructor getDestructor(String name)
    {
        Destructor var = destructors_.get(name);
        if (var != null) return var;
        for (SymbolTable parent : parents_)
        {
            var = parent.getDestructor(name);
            if (var != null) return var;
        }
        return null;
    }

    public boolean isDestructorSymbol(String c)
    {
        boolean is = destructor_symbols_.contains(c);
        if (is) return true;
        for (SymbolTable parent : parents_)
        {
            is = parent.isDestructorSymbol(c);
            if (is) return true;
        }
        return false;
    }

    public void addDestructorSymbol(String s)
    {
        destructor_symbols_.add(s);
    }

    public void addDestructor(Destructor c)
    {
        destructor_symbols_.add(c.name());
        destructors_.put(c.name(), c);
    }

    public void addDestructorSymbols(String... s)
    {
        destructor_symbols_.addAll(Arrays.asList(s));
    }

    public void addDestructorSymbols(List<String> s)
    {
        destructor_symbols_.addAll(s);
    }

    // Number ////////////////////////////////////////////////

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

    public void addPolymorphicDataType(PolymorphicDataType pdt)
    {
        polymorphic_data_type_symbols_.add(pdt.baseName());
        polymorphic_data_types_.put(pdt.baseName(), pdt);
    }

    public boolean isPolymorphicDataTypeSymbol(String base_name)
    {
        boolean is = polymorphic_data_type_symbols_.contains(base_name);
        if (is) return true;
        for (SymbolTable parent : parents_)
        {
            is = parent.isPolymorphicDataTypeSymbol(base_name);
            if (is) return true;
        }
        return false;
    }


    public void addOperator(Operator o)
    {
        operator_symbols_.add(o.name());
        operators_.put(o.name(), o);
    }

    public Operator getOperator(Formula name)
    {
        return getOperator(name.symbol());
    }

    public Operator getOperator(String name)
    {
        Operator op = operators_.get(name);
        if (op != null) return op;
        for (SymbolTable parent : parents_)
        {
            op = parent.getOperator(name);
            if (op != null) return op;
        }
        return null;
    }

    public boolean isOperatorSymbol(OperatorNotationType nt, OperatorType ot, String p)
    {
        Operator op = operators_.get(p);
        if (op != null) {
            boolean ok = op.isOp(nt, ot);
            return ok;
        }

        for (SymbolTable parent : parents_)
        {
            boolean is = parent.isOperatorSymbol(nt, ot, p);
            if (is) {
                return true;
            }
        }
        return false;
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
        if (isPolymorphicDataTypeSymbol(p)) return false;
        if (isConstructorSymbol(p)) return false;
        if (isDestructorSymbol(p)) return false;
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

    private String printOperatorSyms(Set<String> syms)
    {
        StringBuilder o = new StringBuilder();
        List<String> ns = syms.stream().sorted().collect(Collectors.toList());
        int c = 0;
        for (String n : ns)
        {
            if (c > 0) o.append(" ");
            o.append(n);
            o.append("+");
            Operator op = getOperator(n);
            if (op != null)
            {
                if (op.notationType() == OperatorNotationType.INFIX) o.append("I");
                else o.append("P");
                if (op.operatorType() == OperatorType.PREDICATE) o.append("P");
                else o.append("E");
            }
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

        String title = "SymbolTable "+name_;
        boolean need_line = false;

        if (hasConstantSymbols())
        {
            if (need_line) o.append("-\n");
            o.append("constants: "+Canvas.flow(80, printSyms(constant_symbols_)));
            need_line = true;
        }
        if (hasExpressionSymbols())
        {
            if (need_line) o.append("-\n");
            o.append("expressions: "+Canvas.flow(80, printSyms(expression_symbols_)));
            need_line = true;
        }
        if (hasNumberSymbols())
        {
            if (need_line) o.append("-\n");
            o.append("numbers: "+Canvas.flow(80, printSyms(number_symbols_)));
            need_line = true;
        }
        if (hasPredicateSymbols())
        {
            if (need_line) o.append("-\n");
            o.append("predicates: "+Canvas.flow(80, printSyms(predicate_symbols_)));
            need_line = true;
        }
        if (hasSetSymbols())
        {
            if (need_line) o.append("-\n");
            o.append("sets: "+Canvas.flow(80, printSyms(set_symbols_)));
            need_line = true;
        }
        if (hasVariableSymbols())
        {
            if (need_line) o.append("-\n");
            o.append("variables: "+Canvas.flow(80, printSyms(variable_symbols_)));
            need_line = true;
        }
        if (hasPolymorphicDataTypeSymbols())
        {
            if (need_line) o.append("-\n");
            o.append("datatypes: "+Canvas.flow(80, printSyms(polymorphic_data_type_symbols_)));
            need_line = true;
        }
        if (hasConstructorSymbols())
        {
            if (need_line) o.append("-\n");
            o.append("constructors: "+Canvas.flow(80, printSyms(constructor_symbols_)));
            need_line = true;
        }
        if (hasDestructorSymbols())
        {
            if (need_line) o.append("-\n");
            o.append("destructors: "+Canvas.flow(80, printSyms(destructor_symbols_)));
            need_line = true;
        }
        if (hasOperatorSymbols())
        {
            if (need_line) o.append("-\n");
            o.append("operators: "+Canvas.flow(80, printOperatorSyms(operator_symbols_)));
            need_line = true;
        }
        if (hasAnySymbols())
        {
            if (need_line) o.append("-\n");
            o.append("anys: "+Canvas.flow(80, printSyms(any_symbols_)));
            need_line = true;
        }
        for (SymbolTable p : parents_)
        {
            o.append(p.print());
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

        polymorphic_data_type_symbols_.add("H");
        constructor_symbols_.add("cx");
        destructor_symbols_.add("dx");

        addOperator(Sys.dummyTheory().generatePhantomOperator("pp+PP"));
        addOperator(Sys.dummyTheory().generatePhantomOperator("pe+PE"));
        addOperator(Sys.dummyTheory().generatePhantomOperator("ip+IP"));
        addOperator(Sys.dummyTheory().generatePhantomOperator("ie+IE"));
    }

    public void collectInfixOperators(HashMap<String,Integer> map)
    {
        for (SymbolTable st : parents_)
        {
            st.collectInfixOperators(map);
        }
        for (Map.Entry<String, Operator> entry : operators_.entrySet())
        {
            Operator op = entry.getValue();
            if (op.notationType() == OperatorNotationType.INFIX)
            {
                if (op.operatorType() == OperatorType.PREDICATE)
                {
                    map.put(op.name(), EvBFormulaParser.OP_IP);
                }
                else
                {
                    map.put(op.name(), EvBFormulaParser.OP_IE);
                }
            }
            else
            {
                if (op.operatorType() == OperatorType.PREDICATE)
                {
                    map.put(op.name(), EvBFormulaParser.OP_PP);
                }
                else
                {
                    map.put(op.name(), EvBFormulaParser.OP_PE);
                }
            }
        }
    }

    public static SymbolTable PQR_EFG_STU_xyz_cdf_NM_ABC_H_cx_dx_op;

    static
    {
        SymbolTable st = new SymbolTable("plain");
        st.addDefaults();
        PQR_EFG_STU_xyz_cdf_NM_ABC_H_cx_dx_op = st;
    }
}
