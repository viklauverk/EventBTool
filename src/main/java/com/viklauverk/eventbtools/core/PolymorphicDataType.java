/*
 Copyright (C) 2024 Viklauverk AB

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
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

public class PolymorphicDataType
{
    private String base_name_; // List Seq
    private String long_name_; // List(T) Seq(T)
    private String comment_;

    private List<String> type_parameters_;

    private Map<String,Constructor> constructors_;
    private List<Constructor> constructor_ordering_;
    private List<String> constructor_names_;

    private Map<String,Operator> operators_;
    private List<Operator> operator_ordering_;
    private List<String> operator_names_;
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

        operators_ = new HashMap<>();
        operator_names_ = new ArrayList<>();
        operator_ordering_ = new ArrayList<>();
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
        StringBuilder sb = new StringBuilder();
        sb.append(base_name_);
        sb.append("(");
        boolean add_comma = false;
        for (String s : type_parameters_)
        {
            if (add_comma) sb.append(",");
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

    public void addOperator(Operator o)
    {
        operators_.put(o.name(), o);
        operator_ordering_.add(o);
        operator_names_ = operators_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public Operator getOperator(String name)
    {
        return operators_.get(name);
    }

    public List<Operator> operatorOrdering()
    {
        return operator_ordering_;
    }

    public List<String> operatorNames()
    {
        return operator_names_;
    }

}
