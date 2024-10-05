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

package com.viklauverk.evbt.core.cmd;

import java.util.List;

import com.viklauverk.evbt.core.Console;
import com.viklauverk.evbt.core.RenderTarget;
import com.viklauverk.evbt.core.RenderAttributes;

public class CmdEbShowFormula extends CmdCommon
{
    public CmdEbShowFormula(Console console, String line)
    {
        super(console, line);
    }

    @Override
    public String go()
    {
        extractOptions();

        boolean show_meta = checkOption("--meta");
        boolean show_tree = checkOption("--tree");
        boolean show_frame = checkOption("--frame");
        boolean render_plain = checkOption("--plain");
        boolean render_terminal = checkOption("--terminal");
        boolean render_tex = checkOption("--tex");
        boolean render_html = checkOption("--html");

        RenderTarget rt = toRenderTarget(render_plain, render_terminal, render_tex, render_html, console_.renderTarget());
        RenderAttributes ra = console_.renderAttributes();

        return console_.renderFormula(line_, show_meta, show_tree, show_frame, rt, ra);
    }
}
