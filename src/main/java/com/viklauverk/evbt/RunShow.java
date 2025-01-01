/*
 Copyright (C) 2021-2024 Viklauverk AB (agpl-3.0-or-later)

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

package com.viklauverk.evbt;

import com.viklauverk.evbt.core.docgen.RenderAttributes;
import com.viklauverk.evbt.core.docgen.RenderTarget;
import com.viklauverk.evbt.core.sys.Machine;
import com.viklauverk.evbt.core.sys.Sys;
import com.viklauverk.evbt.core.Settings;
import com.viklauverk.evbt.core.console.Canvas;

public class RunShow
{
    public static void run(CmdArgs ca, Settings s)
        throws Exception
    {
        Sys sys = new Sys(s);

        sys.loadTheoriesAndContextsAndMachines(s.commonSettings().sourceDir(), s.commonSettings().theoryRootDir());
        for (Machine mch : sys.machineOrdering())
        {
            mch.buildImplementation();
        }

        Canvas canvas = new Canvas();
        RenderTarget rt = RenderTarget.TERMINAL;
        RenderAttributes ra = sys.console().renderAttributes();
        ra.setColor(true);
        ra.setAtLabel(true);
        canvas.setRenderTarget(rt);
        canvas.setRenderAttributes(ra);

        canvas.useLayout(true); // s.showSettings().horizontalLayout());
        canvas.setLayoutWidth(120); // s.showSettings().horizontalWidth());

        String all = sys.show(s.showSettings(), canvas, "");

        if (s.showSettings().parts().size() > 0)
        {
         /*   for (String p : s.showSettings().parts())
            {
                // System.out.println(parts.getPart(p));
            }*/
        }
        else
        {
            System.out.println(all);
        }
    }
}
