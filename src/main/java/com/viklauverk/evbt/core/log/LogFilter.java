/*
 Copyright (C) 2021-2024 Viklauverk AB (spdx: agpl-3.0-or-later)

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

package com.viklauverk.evbt.core.log;

import java.util.LinkedList;

import com.viklauverk.evbt.core.InternalError;

public class LogFilter
{
    private String[] grep_;
    private String[] ungrep_;

    LogFilter(String expression)
    {
        String[] parts = expression.split(":");
        LinkedList<String> g = new LinkedList<>();
        LinkedList<String> ug = new LinkedList<>();

        for (String s : parts)
        {
            if (s.startsWith("-")) ug.add(s.substring(1));
            else if (s.startsWith("+")) g.add(s.substring(1));
            else {
                throw new InternalError("Invalid log filter expression");
            }
        }

        if (g.size() > 0) grep_ = g.toArray(new String[0]);
        if (ug.size() > 0) ungrep_ = ug.toArray(new String[0]);
    }

    public boolean check(String line)
    {
        if (grep_ != null)
        {
            for (String s : grep_)
            {
                if (line.indexOf(s) == -1) return false;
            }
        }
        if (ungrep_ != null)
        {
            for (String s : ungrep_)
            {
                if (line.indexOf(s) != -1) return false;
            }
        }
        return true;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("/");
        if (grep_ != null) for (String s : grep_) sb.append("+"+s+":");
        if (ungrep_ != null) for (String s : ungrep_) sb.append("-"+s+":");
        if (sb.length() > 1)
        {
            int last = sb.length() - 1;
            sb.replace(last, last + 1, "");
        }
        sb.append("/");
        return sb.toString();
    }
}
