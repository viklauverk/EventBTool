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

import com.viklauverk.common.console.Canvas;

public class ConstructorArgument
{
    private String name_;
    private String type_s_;
    private Formula type_;
    private Constructor constructor_;

    public ConstructorArgument(String n, String t, Constructor c)
    {
        name_ = n;
        type_s_ = t;
        constructor_ = c;
    }

    @Override
    public String toString()
    {
        return name_;
    }

    public void toString(Canvas ca)
    {
        ca.constructor(name_);
        ca.append("::::");
        type_.toString(ca);
    }

    public String name()
    {
        return name_;
    }

    public Formula type()
    {
        return type_;
    }

    public Constructor constructor()
    {
        return constructor_;
    }

    public void reparse()
    {
        type_ = Formula.fromString(type_s_, constructor_.constructorSymbolTable());
    }
}
