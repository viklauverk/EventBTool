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

public class CmdSysRead extends CmdCommon
{
    public CmdSysRead(Console console, String line)
    {
        super(console, line);
    }

    @Override
    public String go()
    {
        String dir = line_.trim();
        try
        {
            String info = console_.sys().loadMachinesAndContexts(dir);
            return "Read "+dir+" ("+info+")";
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
    }
}
