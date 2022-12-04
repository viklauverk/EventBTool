/*
 Copyright (C) 2021 Viklauverk AB
 
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

package com.viklauverk.eventbtools;

import com.viklauverk.eventbtools.core.BaseDocGen;
import com.viklauverk.eventbtools.core.DocGen;
import com.viklauverk.eventbtools.core.Log;
import com.viklauverk.eventbtools.core.LogModule;
import com.viklauverk.eventbtools.core.RenderTarget;
import com.viklauverk.eventbtools.core.Settings;
import com.viklauverk.eventbtools.core.Sys;

public class RunDocMod
{
    private static Log log = LogModule.lookup("docmod");

    public static void run(Settings s)
        throws Exception
    {
        Sys sys = new Sys(s);
        if (s.commonSettings().sourceDir() != null)
        {
            log.info("Loading machines and contexts from: %s", s.commonSettings().sourceDir());
            sys.loadMachinesAndContexts(s.commonSettings().sourceDir());
        }

        BaseDocGen bdg = DocGen.lookup(s.commonSettings(), s.docGenSettings(), sys);
        sys.console().setRenderTarget(RenderTarget.TEX);
        bdg.modFile(s.commonSettings().sourceFile(), s.commonSettings().destFile());
    }
}
