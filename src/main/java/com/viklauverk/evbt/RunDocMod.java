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

import com.viklauverk.common.log.Log;
import com.viklauverk.common.log.LogModule;
import com.viklauverk.evbt.core.BaseDocGen;
import com.viklauverk.evbt.core.DocGen;
import com.viklauverk.evbt.core.RenderTarget;
import com.viklauverk.evbt.core.Settings;
import com.viklauverk.evbt.core.Sys;

public class RunDocMod
{
    private static Log log = LogModule.lookup("docmod", RunDocMod.class);

    public static void run(CmdArgs ca, Settings s)
        throws Exception
    {
        Sys sys = new Sys(s);
        if (s.commonSettings().sourceDir() != null)
        {
            log.info("Loading machines, contexts and theories from: %s with additional theories from: %s\n",
                     s.commonSettings().sourceDir(),
                     s.commonSettings().theoryRootDir());
            sys.loadTheoriesAndContextsAndMachines(s.commonSettings().sourceDir(), s.commonSettings().theoryRootDir());
        }

        BaseDocGen bdg = DocGen.lookup(s.commonSettings(), s.docGenSettings(), sys);
        sys.console().setRenderTarget(RenderTarget.TEX);
        bdg.modFile(s.commonSettings().sourceFile(), s.commonSettings().destFile());
    }
}
