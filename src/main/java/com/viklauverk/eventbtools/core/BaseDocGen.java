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

package com.viklauverk.eventbtools.core;

import java.net.URL;

import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class BaseDocGen
{
    static Log log = LogModule.lookup("docgen");

    private CommonSettings common_settings_;
    private DocGenSettings docgen_settings_;
    private Sys sys_;
    private FileWriter writer_;
    private String default_format_;

    public BaseDocGen(CommonSettings cs, DocGenSettings ds, Sys sys, String df)
    {
        common_settings_ = cs;
        docgen_settings_ = ds;
        sys_ = sys;
        default_format_ = df; // tex, hxmq, terminal, plain
    }

    public CommonSettings commonSettings()
    {
        return common_settings_;
    }

    public DocGenSettings docGenSettings()
    {
        return docgen_settings_;
    }

    public Sys sys()
    {
        return sys_;
    }

    public void open(String file) throws Exception
    {
        if (writer_ != null) writer_.close();
        writer_ = new FileWriter(file);
    }

    public void p(String s) throws Exception
    {
        writer_.write(s);
    }

    public void pl(String s) throws Exception
    {
        writer_.write(s);
        writer_.write("\n");
    }

    public void close() throws Exception
    {
        writer_.close();
    }

    public String suffix()
    {
        return "?";
    }

    public abstract String generateDocument() throws Exception;

    public String renderParts(Canvas cnvs, String pattern)
    {
        AllRenders ar = sys().lookupRenders(cnvs.renderTarget(),
                                            cnvs);

        for (String ctxs : sys().contextNames())
        {
            Context ctx = sys().getContext(ctxs);
            ar.walkContext(ctx, pattern);
        }

        for (String mchs : sys().machineNames())
        {
            Machine mch = sys().getMachine(mchs);
            ar.walkMachine(mch, pattern);
        }

        return cnvs.render();
    }

    public void genTemplateFile(String in)
    {
        log.info("Writing template "+in);
        try
        {
            open(in);
            p(generateDocument());
            close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void modFile(String in, String out)
    {
        log.info("Writing "+out);
        log.debug("modfile render attributes %s", sys().settings().docGenSettings().renderAttributes());
        sys().console().go("set default format "+default_format_);
        try
        {
            String template = Util.readFile(in);
            String output = Util.replaceEVBT(template, this::dynamicHandler);
            Util.writefile(out, output);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public String dynamicHandler(String formula)
    {
        return sys().console().go(formula);
    }
}
