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

package com.viklauverk.eventbtools.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Attribute;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class TheoryPath
{
    private static Log log = LogModule.lookup("theory");

    private boolean loaded_;
    private List<String> deployed_theories_;
    private Map<String,File> deployed_theory_;
    private Map<String,File> deployed_theory_source_;

    private File tcl_; // The theory path for deployed theories.
    private File theory_root_dir_; // Where to look for theories.

    public TheoryPath()
    {
        deployed_theories_ = new ArrayList<>();
        deployed_theory_ = new HashMap<>();
        deployed_theory_source_ = new HashMap<>();
    }

    File getDeployedTheoryDTF(String name)
    {
        return deployed_theory_.get(name);
    }

    File getDeployedTheorySourceTUF(String name)
    {
        return deployed_theory_source_.get(name);
    }

    List<String> deployedTheories()
    {
        return deployed_theories_;
    }

    public synchronized void load(File dir, File trd) throws Exception
    {
        if (loaded_) return;

        tcl_ = new File(dir, "TheoryPath.tcl");

        if (!tcl_.exists() || !tcl_.isFile())
        {
            // No theory path was found, which is ok.
            return;
        }

        theory_root_dir_ = trd;
        loaded_ = true;

        SAXReader reader = new SAXReader();

        log.debug("loading theory path "+tcl_);

        Document document = null;

        try
        {
            document = reader.read(tcl_);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.error("Failed loading theory tcl "+tcl_);
        }

        List<Node> ats = document.selectNodes("//org.eventb.theory.core.scAvailableTheory");
        for (Node at : ats)
        {
            String s = at.valueOf("@org.eventb.theory.core.availableTheory");
            int p = s.indexOf("|");
            int pp = s.indexOf("#");
            String dtf_file = s.substring(0, p); // Deployed Theory File
            String name = s.substring(pp+1);

            // Try to populate with this imported theory.
            File dtf = new File(theory_root_dir_, dtf_file);
            dtf = dtf.getAbsoluteFile();
            if (!dtf.exists() || !dtf.isFile())
            {
                LogModule.usageErrorStatic("Cannot find \"%s\" for theory \"%s\" which is used from theory path \"%s\"!\n"+
                                           "Use --theory-root-dir=... to point where theory projects are located.",
                                           dtf,
                                           name,
                                           tcl_);
            }
            deployed_theories_.add(name);
            deployed_theory_.put(name, dtf);

            log.debug("uses deployed theory: "+name+" loaded from: "+dtf);

            // Check if we have the source to the theory.
            s = at.valueOf("@org.eventb.core.source");
            p = s.indexOf("|");
            String tul_file = s.substring(0, p); // Source for theory
            File tul = new File(theory_root_dir_, tul_file);
            if (tul.exists() && tul.isFile())
            {
                deployed_theory_source_.put(name, tul);
            }
        }
    }
}
