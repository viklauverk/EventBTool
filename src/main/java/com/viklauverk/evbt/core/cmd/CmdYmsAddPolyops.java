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
import com.viklauverk.evbt.core.Sys;

import java.util.List;

public class CmdYmsAddPolyops extends CmdCommon
{
    public CmdYmsAddPolyops(Console console, String line)
    {
        super(console, line);
    }

    @Override
    public String go()
    {
        String[] ops = line_.split("\\s+");

        for (String op : ops)
        {
            Operator o = Sys.dummyTheory().generatePhantomOperator(op);
            if (o != null)
            {
                console_.currentSymbolTable().addOperator(o);
            }
            else
            {
                return "Cannot add operator \""+op+"\" since it does not end with +PP +PE +IP +IE.";
            }
        }
        return "OK";
    }

}
