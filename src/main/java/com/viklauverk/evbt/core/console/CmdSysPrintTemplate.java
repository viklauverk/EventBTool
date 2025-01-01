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

package com.viklauverk.evbt.core.console;

public class CmdSysPrintTemplate extends CmdCommon
{
    public CmdSysPrintTemplate(Console console, String line)
    {
        super(console, line);
    }

    @Override
    public String go()
    {
        String name = line_.trim();
        try
        {
            String s = console_.renderTemplate(name);
            if (s != null) return s;
            return "Unknown template: "+s;
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
    }
}
