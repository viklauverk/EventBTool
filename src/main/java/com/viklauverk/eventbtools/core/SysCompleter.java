// Copyright Viklauverk AB 2024
package com.viklauverk.eventbtools.core;

import java.util.List;
import org.jline.reader.Completer;
import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

public class SysCompleter implements Completer
{
    Sys sys_;
    boolean machines_;
    boolean contexts_;
    boolean tables_;
    boolean tree_;

    SysCompleter(Sys sys, boolean machines, boolean contexts, boolean tables, boolean tree)
    {
        sys_ = sys;
        machines_ = machines;
        contexts_ = contexts;
        tables_ = tables;
        tree_ = tree;
    }

    public void complete(LineReader reader,
                         ParsedLine line,
                         List<Candidate> candidates)
    {
        if (tree_)
        {
            int space= line.line().indexOf(" ");
            if (space == -1) return;

            String path = line.line().substring(space+1).trim();

            String[] parts = path.split("/");

            if (sys_.getMachine(parts[0]) == null)
            {
                String mname = parts[0].trim();
                for (String m : sys_.machineNames())
                {
                    if (m.startsWith(mname)) candidates.add(new Candidate(m+"/"));
                }
            }

            return;
        }

        if (machines_)
        {
            for (String m : sys_.machineNames())
            {
                candidates.add(new Candidate(m));
            }
        }

        if (contexts_)
        {
            for (String m : sys_.contextNames())
            {
                candidates.add(new Candidate(m));
            }
        }

        if (tables_)
        {
            for (String t : sys_.allSymbolTables().keySet())
            {
                candidates.add(new Candidate(t));
            }
        }

    }
}
