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
import com.viklauverk.evbt.core.Templates;
import com.viklauverk.evbt.core.Unicode;
import com.viklauverk.evbt.core.console.Canvas;
import com.viklauverk.evbt.core.helpers.Util;
import com.viklauverk.evbt.core.sys.Context;
import com.viklauverk.evbt.core.sys.Machine;
import com.viklauverk.evbt.core.sys.Sys;
import com.viklauverk.evbt.core.sys.Theory;

public class DocGenTeX extends BaseDocGen
{
    public DocGenTeX(CommonSettings common_settings, DocGenSettings docgen_settings, Sys sys)
    {
        super(common_settings, docgen_settings, sys, "tex");
    }

    @Override
    public String suffix()
    {
        return ".tex";
    }

    @Override
    public String generateDocument() throws Exception
    {
        Canvas cnvs = new Canvas();
        cnvs.setRenderTarget(RenderTarget.TEX);

        cnvs.append(Templates.TeXHeader.replace("$PATH_TO_IMAGES$", commonSettings().sourceDir()+"/"));

        cnvs.append("EVBT(sys.print.template TeXDefinitions)\n");

        cnvs.append("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
        cnvs.append("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
        cnvs.append("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");

        cnvs.append("\\makeindex\n");
        cnvs.append("\\begin{document}\n");

        cnvs.append("\\raggedright\n");

        cnvs.append("{\\Large An Event-B Specification of} \\\\\n");
        cnvs.append("\\vspace{2mm}\n");
        cnvs.append("{\\Huge "+Util.texSafe(commonSettings().nickName())+"} \\\\\n");
        cnvs.append("\\HRULE\n\n");

        if (sys().projectInfo().length() > 0)
        {
            cnvs.append("\n\n\\par ");
            cnvs.append(Unicode.commentToTeX(sys().projectInfo()));
            cnvs.append("\n\n\\HRULE\n\n");
        }

        cnvs.append("\\tableofcontents\n");

        for (String ctx : sys().contextNames())
        {
            // Skip EDK contexts for the moment.
            if (ctx.startsWith("EDK_")) continue;

            Context c = sys().getContext(ctx);
            String pos = "";
            if (c.numUnproven() > 0)
            {
                pos += "\\Unproved ";
            }
            if (c.numProvedManualReviewed() > 0)
            {
                pos += "\\Reviewed ";
            }

            cnvs.append("\\pagebreak\n\n");
            cnvs.append("\\section{\\KEYWL{CONTEXT}\\small\\ "+Util.texSafe(ctx)+" "+pos+"}\n\n");
            cnvs.append("EVBT(eb.show.part --tex ctx/"+ctx+")\n");
        }

        for (String mch : sys().machineNames())
        {
            cnvs.append("\\pagebreak\n\n");
            Machine m = sys().getMachine(mch);
            String pos = "";
            if (m.numUnproven() > 0)
            {
                pos += "\\Unproved ";
            }
            if (m.numProvedManualReviewed() > 0)
            {
                pos += "\\Reviewed ";
            }

            cnvs.append("\\section{\\KEYWL{"+m.machineOrRefinement().toUpperCase()+"}\\small\\ "+Util.texSafe(mch)+" "+pos+"}\n\n");
            cnvs.append("EVBT(eb.show.part --tex mch/"+mch+")\n");
        }

        for (String thr : sys().deployedTheoryNames())
        {
            cnvs.append("\\pagebreak\n\n");
            Theory t = sys().getDeployedTheory(thr);
            String pos = "";
            if (t.numUnproven() > 0)
            {
                pos += "\\Unproved ";
            }
            if (t.numProvedManualReviewed() > 0)
            {
                pos += "\\Reviewed ";
            }

            cnvs.append("\\section{\\KEYWL{THEORY}\\small\\ "+Util.texSafe(thr)+" "+pos+"}\n\n");
            cnvs.append("EVBT(eb.show.part --tex thr/"+thr+")\n");
        }

        cnvs.append("\\printindex\n");

        cnvs.append("\\end{document}\n");
        return cnvs.render();
    }
}
