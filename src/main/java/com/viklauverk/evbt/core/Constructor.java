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

package com.viklauverk.evbt.core;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Constructor extends Typed
{
    // The base name, such as nil or cons
    private String name_;

    // The full api with all types, such as cons(head:T, tail:List<T>)
    private Formula api_;

    // The short api for info, such as cons(head,tail)
    private Formula short_api_;

    // Any comment regarding this constructo.
    private String comment_;

    // The constructor belongs to this datatype.
    private PolymorphicDataType polymorphic_datatype_;

    // Any arguments to this constructor.
    private List<ConstructorArgument> arguments_ = new LinkedList<>();

    private Map<String,Destructor> destructors_ = new HashMap<>();
    private List<Destructor> destructor_ordering_ = new ArrayList<>();
    private List<String> destructor_names_ = new ArrayList<>();

    // This symbol tables contains the name of the constructor and constructor argument variables.
    private SymbolTable constructor_symbol_table_;

    public Constructor(String n, PolymorphicDataType pdt)
    {
        name_ = n;
        polymorphic_datatype_ = pdt;
        constructor_symbol_table_ = new SymbolTable("CNSTR_"+name_);
    }

    @Override
    public String toString()
    {
        return name_;
    }

    public String name()
    {
        return name_;
    }

    public void writeApi(Canvas cnvs)
    {
        api_.toString(cnvs);
    }

    public void writeApiShort(Canvas cnvs)
    {
        short_api_.toString(cnvs);
    }

    public String comment()
    {
        return comment_;
    }

    public PolymorphicDataType polymorphicDataType()
    {
        return polymorphic_datatype_;
    }

    public void addComment(String c)
    {
        comment_ = c;
    }

    public void addDestructor(Destructor o)
    {
        destructors_.put(o.name(), o);
        destructor_ordering_.add(o);
        destructor_names_ = destructors_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public Destructor getDestructor(String name)
    {
        return destructors_.get(name);
    }

    public List<Destructor> destructorOrdering()
    {
        return destructor_ordering_;
    }

    public List<String> destructorNames()
    {
        return destructor_names_;
    }

    public void addArgument(String name, String type)
    {
        constructor_symbol_table_.addVariableSymbol(name);
        arguments_.add(new ConstructorArgument(name, type, this));
    }

    public SymbolTable constructorSymbolTable()
    {
        return constructor_symbol_table_;
    }

    void buildAPI(SymbolTable st)
    {
        if (arguments_.size() == 0)
        {
            api_ = FormulaFactory.newConstructorSymbol (name_, null, null);
            short_api_ = api_;
            return;
        }

        List<Formula> types = new LinkedList<>();
        for (ConstructorArgument ca : arguments_)
        {
            String info = ca.name()+":"+ca.type();
            Formula var = Formula.fromString(info, st);
            types.add(var);
        }
        Formula type_preds = FormulaFactory.newListOfPredicates(types);
        api_ = FormulaFactory.newConstructorSymbol (name_, type_preds, null);

        List<Formula> args_only = new LinkedList<>();
        for (ConstructorArgument ca : arguments_)
        {
            args_only.add(FormulaFactory.newVariableSymbol(ca.name(), null));
        }
        Formula args = FormulaFactory.newListOfExpressions(args_only);
        short_api_ = FormulaFactory.newConstructorSymbol(name_, args, null);
    }

    public void reparse()
    {
        if (!constructor_symbol_table_.hasParents())
        {
            constructor_symbol_table_.addParent(polymorphic_datatype_.pdtSymbolTable());
        }
        for (ConstructorArgument ca : arguments_)
        {
            ca.reparse();
        }
        buildAPI(constructor_symbol_table_);
    }

}
