/*
 Copyright (C) 2021-2024 Viklauverk AB

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
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Constructor extends Typed
{
    private String name_;
    private String comment_;
    private PolymorphicDataType polymorphic_datatype_;

    private List<OperatorArgument> arguments_ = new LinkedList<>();

    private Map<String,Destructor> destructors_ = new HashMap<>();
    private List<Destructor> destructor_ordering_ = new ArrayList<>();
    private List<String> destructor_names_ = new ArrayList<>();

    private SymbolTable constructor_symbol_table_;

    public Constructor(String n, PolymorphicDataType pdt)
    {
        name_ = n;
        polymorphic_datatype_ = pdt;
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

    public void toString(Canvas cnvs)
    {
        cnvs.constructor(name_);
        if (destructor_ordering_.size() > 0)
        {
            cnvs.append("(");
            cnvs.append(")");
        }
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
        arguments_.add(new OperatorArgument(name, type, null));
    }

}
