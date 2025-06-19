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

import java.io.File;
import java.io.FileWriter;

import com.viklauverk.evbt.common.log.Log;
import com.viklauverk.evbt.common.log.LogModule;
import com.viklauverk.evbt.core.CommonSettings;
import com.viklauverk.evbt.core.Templates;
import com.viklauverk.evbt.core.console.Canvas;
import com.viklauverk.evbt.core.sys.Sys;

public class DocGenHtmq extends BaseDocGen
{
    private static Log log = LogModule.lookup("evbt.htmq", DocGenHtmq.class);

    public DocGenHtmq(CommonSettings common_settings, DocGenSettings docgen_settings, Sys sys)
    {
        super(common_settings, docgen_settings, sys, "htmq");
    }

    @Override
    public String suffix()
    {
        return ".htmq";
    }

    @Override
    public String generateDocument() throws Exception
    {
        Canvas cnvs = new Canvas();
        cnvs.setRenderTarget(RenderTarget.HTML);

        String css = commonSettings().nickName()+".css";
        String tmp = Templates.HtmqHeader.replace("$TITLE$", "'"+commonSettings().nickName()+"'");
        tmp = tmp.replace("$STYLE$", css);
        cnvs.append(tmp);

        try
        {
            File f = new File(commonSettings().outputDir(), css);
            FileWriter writer = new FileWriter(f);
            writer.write(Templates.HtmlCss);
            writer.close();
            log.info("Writing "+css);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        cnvs.append("        h2='An Event-B Specification of'\n");
        cnvs.append("        h2='"+commonSettings().nickName()+"'\n");
        cnvs.append("        hr\n");

        for (String ctx : sys().contextNames())
        {
            cnvs.append("EVBT(show part htmq \""+ctx+"\")\n");
        }

        for (String mch : sys().machineNames())
        {
            cnvs.append("EVBT(show part htmq \""+mch+"\")\n");
        }

        cnvs.append(Templates.HtmqFooter);
        return cnvs.render();
    }

}
