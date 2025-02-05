/*
 Copyright (C) 2021 Viklauverk AB (agpl-3.0-or-later)

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

package com.viklauverk.evbt.core.docgen;

import com.viklauverk.evbt.core.CommonSettings;
import com.viklauverk.evbt.core.console.Canvas;
import com.viklauverk.evbt.core.sys.Sys;

public class DocGenUnicode extends BaseDocGen
{
    public DocGenUnicode(CommonSettings common_settings, DocGenSettings docgen_settings, Sys sys)
    {
        super(common_settings, docgen_settings, sys, "plain");
    }

    @Override
    public String suffix()
    {
        return ".txt";
    }

    @Override
    public String generateDocument() throws Exception
    {
        Canvas cnvs = new Canvas();
        cnvs.setRenderTarget(RenderTarget.PLAIN);

        for (String ctx : sys().contextNames())
        {
            cnvs.append("EVBT(eb.show.part \""+ctx+"\")\n");
        }

        for (String mch : sys().machineNames())
        {
            cnvs.append("EVBT(eb.show.part \""+mch+"\")\n");
        }

        for (String thr : sys().deployedTheoryNames())
        {
            cnvs.append("EVBT(eb.show.part \""+thr+"\")\n");
        }

        return cnvs.render();
    }

}
