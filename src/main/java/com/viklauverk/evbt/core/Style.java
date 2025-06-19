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

package com.viklauverk.evbt.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.viklauverk.evbt.common.log.LogModule;

public class Style
{
    private static LogModule log = LogModule.lookup("evbt.style", Style.class);

    private boolean valid_;
    private String main_;
    private Map<String,String> settings_;

    public Style(String s)
    {
        settings_ = new HashMap<String,String>();
        parse(s);
    }

    public boolean valid()
    {
        return valid_;
    }

    public String main()
    {
        return main_;
    }

    public String get(String key)
    {
        return settings_.get(key);
    }

    public Set<String> keys()
    {
        return settings_.keySet();
    }

    public void parse(String s)
    {
        // A style starts with a name:
        // default
        // compact
        // terse
        //
        // This name sets a bunch of settings, then these settings can
        // be overridden:
        // default,-color
        // default,-labels,-comments
        // terse,+labels
        //
        // +x,-y  are the same as  x=true,y=false
        // Other values can also be set.
        // default,color=false,spacing=7

        valid_ = true;
        s = s.trim();
        log.debug("setting style \""+s+"\"");
        String[] parts = s.split(",");
        if (parts.length == 0 ||
            parts[0].length() == 0)
        {
            valid_ = false;
            return;
        }

        main_ = parts[0];
        for (int i=1; i<parts.length; ++i)
        {
            handleSetting(parts[i]);
        }
    }

    public void handleSetting(String s)
    {
        if (s.length() == 0)
        {
            valid_ = false;
            return;
        }
        if (s.charAt(0) == '+' ||
            s.charAt(0) == '-')
        {
            if (s.indexOf("=") != -1)
            {
                valid_ = false;
                return;
            }
            if (s.charAt(0) == '+')
            {
                settings_.put(s.substring(1), "true");
                return;
            }
            settings_.put(s.substring(1), "false");
            return;
        }
        int p = s.indexOf("=");
        if (p == -1)
        {
            valid_ = false;
            return;
        }
        String left = s.substring(0, p);
        String right = s.substring(p+1);
        if (left.length() == 0 || right.length() == 0)
        {
            valid_ = false;
            return;
        }
        settings_.put(left, right);
    }
}
