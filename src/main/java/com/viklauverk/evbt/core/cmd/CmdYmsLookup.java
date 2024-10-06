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

package com.viklauverk.evbt.core.cmd;

import com.viklauverk.evbt.core.Console;
import com.viklauverk.evbt.core.Operator;
import com.viklauverk.evbt.core.OperatorNotationType;
import com.viklauverk.evbt.core.OperatorType;
import com.viklauverk.evbt.core.SymbolTable;

import java.util.List;

public class CmdYmsLookup extends CmdCommon
{
    public CmdYmsLookup(Console console, String line)
    {
        super(console, line);
    }

    @Override
    public String go()
    {
        StringBuilder sb = new StringBuilder();
        String[] names = line_.split("\\s+");

        for (String n : names)
        {
            sb.append(lookup(n));
        }

        return sb.toString();
    }

    String lookup(String name)
    {
        SymbolTable st = console_.currentSymbolTable();

        if (st.isPredicateSymbol(name))
        {
            return "Predicate: "+name;
        }
        if (st.isExpressionSymbol(name))
        {
            return "Expression: "+name;
        }
        if (st.isSetSymbol(name))
        {
            return "Set: "+name;
        }
        if (st.isVariableSymbol(name))
        {
            return "Variable: "+name;
        }
        if (st.isConstantSymbol(name))
        {
            return "Constant: "+name;
        }
        if (st.isNumberSymbol(name))
        {
            return "Number: "+name;
        }
        if (st.isAnySymbol(name))
        {
            return "Any: "+name;
        }
        if (st.isPolymorphicDataTypeSymbol(name))
        {
            return "DataType: "+name;
        }
        if (st.isConstructorSymbol(name))
        {
            return "Constructor: "+name;
        }
        if (st.isDestructorSymbol(name))
        {
            return "Destructo: "+name;
        }
        Operator o = st.getOperator(name);
        if (o != null)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(o.name());
            sb.append("+");
            if (o.notationType() == OperatorNotationType.INFIX) sb.append("I");
            else sb.append("P");
            if (o.operatorType() == OperatorType.PREDICATE) sb.append("P");
            else sb.append("E");

            return "Operator: "+sb.toString();
        }
        return "Unknown: "+name;
    }
}
