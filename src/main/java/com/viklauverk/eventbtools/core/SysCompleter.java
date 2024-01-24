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
    SysCompleter(Sys sys, boolean machines, boolean contexts, boolean tables)
    {
        sys_ = sys;
        machines_ = machines;
        contexts_ = contexts;
        tables_ = tables;
    }

    public void complete(LineReader reader,
                         ParsedLine line,
                         List<Candidate> candidates)
    {
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

        System.err.println("PRUTT >"+line.line()+"<");
        if (tables_)
        {
            System.err.println("PRUTTT >"+line.line()+"<");
            for (String t : sys_.allSymbolTables().keySet())
            {
                System.err.println("PRUTTTT >"+t+"<");
                candidates.add(new Candidate(t));
            }
        }

    }
}
