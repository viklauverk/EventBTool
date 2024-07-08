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

package com.viklauverk.eventbtools.core.cmd;

import java.util.List;

import com.viklauverk.eventbtools.core.Console;
import com.viklauverk.eventbtools.core.Formula;
import com.viklauverk.eventbtools.core.Pattern;

public class CmdUtilMatch extends CmdCommon
{
    public CmdUtilMatch(Console console, String line)
    {
        super(console, line);
    }

    @Override
    public String go()
    {
        String l = line_.trim();
        int mmm = l.indexOf("---");
        if (mmm == -1) return "You have to separate the formula from the pattern with ---.";
        String f = l.substring(0, mmm).trim();
        String pattern = l.substring(mmm+3).trim();

        Formula formula = Formula.fromString(f, console_.currentSymbolTable());
        Pattern p = new Pattern();
        boolean ok = p.match(formula, "match", pattern);

        if (!ok)
        {
            return "No match!";
        }

        StringBuilder out = new StringBuilder();
        for (String key : p.predicateNames())
        {
            out.append(""+key+"="+p.getPred(key)+"\n");
        }

        for (String key : p.expressionNames())
        {
            out.append(""+key+"="+p.getExpr(key)+"\n");
        }

        for (String key : p.setNames())
        {
            out.append(""+key+"="+p.getSet(key)+"\n");
        }

        for (String key : p.variableNames())
        {
            out.append(""+key+"="+p.getVar(key)+"\n");
        }

        for (String key : p.constantNames())
        {
            out.append(""+key+"="+p.getConst(key)+"\n");
        }

        for (String key : p.numberNames())
        {
            out.append(""+key+"="+p.getNumber(key)+"\n");
        }

        for (String key : p.anyNames())
        {
            out.append(""+key+"="+p.getAny(key)+"\n");
        }

        for (String key : p.polymorphicDataTypeNames())
        {
            out.append(""+key+"="+p.getPolymorphicDataType(key)+"\n");
        }

        return out.toString();
    }
}
