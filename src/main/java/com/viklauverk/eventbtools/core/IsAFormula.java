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

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class IsAFormula
{
    private String name_;
    private Formula formula_;
    private String  formula_s_;
    private String comment_;

    public boolean equals(IsAFormula f)
    {
        return formula_.equals(f.formula_);
    }

    public IsAFormula(String n, String fs, String c)
    {
        name_ = n;
        formula_ = null;
        formula_s_ = fs;
        comment_ = c;
    }

    public IsAFormula(Formula f)
    {
        formula_ = f;
        name_ = formula_s_ = f.toString();
        comment_ = "";
    }

    public String name()
    {
        return name_;
    }

    public String writeFormulaStringToCanvas(Canvas c)
    {
        return formula_.toString(c);
    }

    public Formula formula()
    {
        return formula_;
    }

    public String comment()
    {
        return comment_;
    }

    public boolean hasComment()
    {
        return comment_.length() > 0;
    }

    public void parse(SymbolTable st)
    {
        formula_ = Formula.fromString(formula_s_, st);
        formula_s_ = formula_.toString();
    }

}
