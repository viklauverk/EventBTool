/*
 Copyright (C) 2024 Viklauverk AB (agpl-3.0-or-later)

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PolymorphicDataType
{
    private String base_name_; // List Seq
    private String long_name_; // List(T) Seq(T)
    private Formula formula_; // List<T> Seq<T>
    private String comment_;

    private List<String> type_parameters_;

    private Map<String,Constructor> constructors_;
    private List<Constructor> constructor_ordering_;
    private List<String> constructor_names_;

    private SymbolTable pdt_symbol_table_;
    private Theory theory_;

    protected Implementation implementation_;

    public PolymorphicDataType(String bn, Theory t)
    {
        base_name_ = bn;
        long_name_ = bn;

        type_parameters_ = new ArrayList<>();

        constructors_ = new HashMap<>();
        constructor_names_ = new ArrayList<>();
        constructor_ordering_ = new ArrayList<>();

        pdt_symbol_table_ = new SymbolTable("PDT_"+bn);
        pdt_symbol_table_.addPolymorphicDataTypeSymbols(bn);

        theory_ = t;
    }

    public String baseName()
    {
        return base_name_;
    }

    public String longName()
    {
        return long_name_;
    }

    public Formula formula()
    {
        return formula_;
    }

    public void reparse()
    {
        if (!pdt_symbol_table_.hasParents())
        {
            pdt_symbol_table_.addParent(theory_.localSymbolTable());
        }
        formula_ = Formula.fromString(long_name_, pdt_symbol_table_);
        for (Constructor c : constructor_ordering_)
        {
            c.reparse();
        }
    }

    public String comment()
    {
        return comment_;
    }

    public boolean hasComment()
    {
        return comment_ != null && comment_.length() > 0;
    }

    public boolean hasConstructors()
    {
        return constructor_ordering_.size() > 0;
    }

    public Theory theory()
    {
        return theory_;
    }

    public void addComment(String c)
    {
        comment_ = c;
    }

    public boolean isPolymorphic()
    {
        return type_parameters_.size() != 0;
    }

    public void addTypeParameter(String p)
    {
        type_parameters_.add(p);
        pdt_symbol_table_.addSetSymbol(p);
        StringBuilder sb = new StringBuilder();
        sb.append(base_name_);
        sb.append("(");
        boolean add_comma = false;
        for (String s : type_parameters_)
        {
            if (add_comma)
            {
                sb.append(",");
            }
            sb.append(s);
            add_comma = true;
        }
        sb.append(")");
        long_name_ = sb.toString();
    }

    public void addConstructor(Constructor o)
    {
        constructors_.put(o.name(), o);
        constructor_ordering_.add(o);
        constructor_names_ = constructors_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public Constructor getConstructor(String name)
    {
        return constructors_.get(name);
    }

    public List<Constructor> constructorOrdering()
    {
        return constructor_ordering_;
    }

    public List<String> constructorNames()
    {
        return constructor_names_;
    }

    public SymbolTable pdtSymbolTable()
    {
        return pdt_symbol_table_;
    }
}
