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

package com.viklauverk.evbt.core;

import com.viklauverk.common.console.Canvas;

public class ModelGenWhy3 extends BaseModelGen
{
    public ModelGenWhy3(CommonSettings common_settings, ModelGenSettings modelgen_settings, Sys sys)
    {
        super(common_settings, modelgen_settings, sys, "why3");
    }

    @Override
    public String suffix()
    {
        return ".txt";
    }

    @Override
    public String generateModel() throws Exception
    {
        Canvas cnvs = new Canvas();
        cnvs.setRenderTarget(RenderTarget.PLAIN);

        for (String ctx : sys().contextNames())
        {
            cnvs.append("EVBT(mo.show.part \""+ctx+"\")\n");
        }

        for (String mch : sys().machineNames())
        {
            cnvs.append("EVBT(mo.show.part \""+mch+"\")\n");
        }
        return cnvs.render();
    }

}
