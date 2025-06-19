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

import com.viklauverk.evbt.common.log.Log;
import com.viklauverk.evbt.common.log.LogModule;
import com.viklauverk.evbt.core.Settings;
import com.viklauverk.evbt.core.docgen.BaseDocGen;
import com.viklauverk.evbt.core.docgen.DocGen;
import com.viklauverk.evbt.core.docgen.RenderTarget;
import com.viklauverk.evbt.core.sys.Sys;

public class RunDocGen
{
    private static Log log = LogModule.lookup("evbt.docgen", RunDocGen.class);

    public static void run(CmdArgs ca, Settings s)
        throws Exception
    {
        Sys sys = new Sys(s);
        log.info("Loading machines, contexts and theories from: %s with additional theories from: %s\n",
                 s.commonSettings().sourceDir(),
                 s.commonSettings().theoryRootDir());
        sys.loadTheoriesAndContextsAndMachines(s.commonSettings().sourceDir(), s.commonSettings().theoryRootDir());

        BaseDocGen bdg = DocGen.lookup(s.commonSettings(), s.docGenSettings(), sys);

        String template = s.commonSettings().outputDir()+"/"+s.commonSettings().nickName()+"_template"+bdg.suffix();

        // Write the template that will invoke show parts on all contexts and machines.
        bdg.genTemplateFile(template);

        String out = s.commonSettings().outputDir()+"/"+s.commonSettings().nickName()+bdg.suffix();

        // Run docmod on the template to fill in the actual contexts and machines.
        bdg.modFile(template, out);

        if (s.docGenSettings().renderTarget() == RenderTarget.TEX)
        {
            String idx = s.commonSettings().outputDir()+"/"+s.commonSettings().nickName()+".idx";

            log.info("Now run: xelatex "+out);
            log.info("         makeindex "+idx);
            log.info("         xelatex "+out);
        }
        if (s.docGenSettings().renderTarget() == RenderTarget.HTML)
        {
            String html = s.commonSettings().outputDir()+"/"+s.commonSettings().nickName()+".html";
            log.info("Now run: xmq "+out+" > "+html);
        }

    }

}
