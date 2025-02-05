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

package com.viklauverk.evbt.core.console;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.viklauverk.evbt.core.docgen.RenderTarget;

public class CmdCommon
{
    protected Console console_;

    // The remaining command line to parse for command.
    protected String line_;
    protected int index_;

    // Options are extracted and stored here.
    // For example --tex
    protected Set<String> options_;
    // For example --indent=123
    protected Map<String,String> option_values_;

    public CmdCommon(Console console, String line)
    {
        console_ = console;
        line_ = line;
        index_ = 0;
    }

    public Console console()
    {
        return console_;
    }

    public String go()
    {
        return "?";
    }

    public void extractOptions()
    {
        options_ = new HashSet<>();
        option_values_ = new HashMap<>();

        for (;;)
        {
            String o = getNextOption();
            if (o == null) break;
        }

        skipWhitespace();

        line_ = line_.substring(index_);
        index_ = 0;
    }

    void skipWhitespace()
    {
        while (index_ < line_.length() && line_.charAt(index_) == ' ') index_++;
    }

    String getNextOption()
    {
        String name;
        String value;
        int eq = 0;

        skipWhitespace();

        if (index_ >= line_.length()) return null;

        char c = line_.charAt(index_);

        if (c != '-') return null;

        int from = index_;

        while (index_ < line_.length())
        {
            c = line_.charAt(index_);
            if (c == ' ') break;
            if (c == '=' && eq == 0) eq = index_;
            index_++;
        }

        if (eq == 0)
        {
            name = line_.substring(from, index_);
            options_.add(name);
        }
        else
        {
            name = line_.substring(from, eq);
            value = line_.substring(eq+1, index_);
            option_values_.put(name, value);
        }
        return name;
    }

    public boolean checkOption(String name)
    {
        if (!options_.contains(name)) return false;
        options_.remove(name);
        return true;
    }

    public String checkOptionValue(String name)
    {
        if (option_values_.get(name) == null) return null;

        String v = option_values_.get(name);
        option_values_.remove(name);
        return v;
    }

    public boolean nomoreOptions()
    {
        return options_.size() == 0 && option_values_.size() == 0;
    }

    public String unknownOptions()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Unknown options: ");
        for (String s : options_)
        {
            sb.append(s);
            sb.append(" ");
        }
        for (String s : option_values_.keySet())
        {
            sb.append(s);
            sb.append("=");
            sb.append(option_values_.get(s));
        }
        return sb.toString();
    }

    public RenderTarget toRenderTarget(boolean render_plain, boolean render_terminal, boolean render_tex, boolean render_html, RenderTarget dt)
    {
        if (render_plain) return RenderTarget.PLAIN;
        if (render_terminal) return RenderTarget.TERMINAL;
        if (render_tex) return RenderTarget.TEX;
        if (render_html) return RenderTarget.HTML;
        return dt;
    }

}
