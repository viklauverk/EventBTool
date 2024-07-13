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

public class OperatorArgument
{
    private String name_;
    private String type_s_;
    private Formula type_;
    private Operator operator_;

    public OperatorArgument(String n, String t, Operator o)
    {
        name_ = n;
        type_s_ = t;
        operator_ = o;
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

    public Formula type()
    {
        return type_;
    }

    public Operator operator()
    {
        return operator_;
    }

    public void reparse()
    {
        type_ = Formula.fromString(type_s_, operator_.theory().localSymbolTable());
    }
}
