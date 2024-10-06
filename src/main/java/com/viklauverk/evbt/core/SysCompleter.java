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
            String l = line.line().trim();
            int space = l.indexOf(" ");
            if (space == -1) return;

            if (space >= l.length()-1) return;
            while (l.charAt(space+1) == '-')
            {
                space = l.indexOf(" ", space+1);
                if (space == -1) return;
                if (space+1 >= l.length()) return;
            }
            String path = line.line().substring(space+1).trim();

            List<String> parts = sys_.listParts("");

            for (String s : parts)
            {
                if (s.startsWith(path)) candidates.add(new Candidate(s));
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
