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

import com.viklauverk.evbt.core.docgen.RenderTarget;

public class CmdHelp extends CmdCommon
{
    public CmdHelp(Console console, String line)
    {
        super(console, line);
    }

    @Override
    public String go()
    {
        String cmd = line_.trim();
        if (cmd.equals(""))
        {
            return globalHelp();
        }

        return cmdHelp(cmd);
    }

    String globalHelp()
    {
        Canvas c = new Canvas();
        c.setRenderTarget(RenderTarget.PLAIN);

        c.append("Commands:\n\n");
        for (EvbtCmd cmd : EvbtCmd.values())
        {
            c.append(cmd.name+"§"+cmd.explanation+"\n");
        }
        return c.render();
    }

    String cmdHelp(String cmds)
    {
        EvbtCmd cmd = EvbtCmd.parse(cmds);
        Canvas c = new Canvas();
        c.setRenderTarget(RenderTarget.PLAIN);

        c.append(cmd.manual);
        return c.render();
    }
}
