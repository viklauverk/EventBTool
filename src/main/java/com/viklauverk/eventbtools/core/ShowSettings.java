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

import java.util.ArrayList;
import java.util.List;

public class ShowSettings
{
    boolean horizontal_layout_ = false;
    int horizontal_width_ = 120;

    boolean show_comments_ = false;

    boolean show_contexts_ = false;
    boolean show_axioms_ = false;

    boolean show_machines_ = false;
    boolean show_invariants_ = false;

    boolean show_events_ = false;

    String framing_ = "unicode";

    private List<String> parts_ = new ArrayList<>();

    public ShowSettings()
    {
        showAll();
    }

    public void showAll()
    {
        show_comments_ = true;

        show_contexts_ = true;
        show_axioms_ = true;

        show_machines_ = true;
        show_invariants_ = true;

        show_events_ = true;
    }

    public void showNothing()
    {
        show_comments_ = false;

        show_contexts_ = false;
        show_axioms_ = false;

        show_machines_ = false;
        show_invariants_ = false;

        show_events_ = false;
    }

    public void showComments(boolean s) { show_comments_ = s; }
    public void showContexts(boolean s) { show_contexts_ = s; }
    public void showAxioms(boolean s) { show_axioms_ = s; if (s) show_contexts_ = true;}
    public void showMachines(boolean s) { show_machines_ = s; }
    public void showInvariants(boolean s) { show_invariants_ = s; }
    public void showEvents(boolean s) { show_events_ = s; }

    public boolean showingComments() { return show_comments_; }
    public boolean showingContexts() { return show_contexts_; }
    public boolean showingAxioms() { return show_axioms_; }
    public boolean showingMachines() { return show_machines_; }
    public boolean showingInvariants() { return show_invariants_; }
    public boolean showingEvents() { return show_events_; }

    public void parseParts(String s)
    {
        String[] ps = s.split(",", -1);
        for (String p : ps)
        {
            if (p.equals("") || p.equals("-") || p.equals("-all"))
            {
                LogModule.usageErrorStatic("Bad show selection \""+p+"\"");
            }
            if (p.equals("all")) showAll();
            boolean enable = true;
            if (p.charAt(0) == '-')
            {
                p = p.substring(1);
                enable = false;
            }
            if (p.equals("com")) showComments(enable);
            if (p.equals("ctx")) showContexts(enable);
            if (p.equals("axm")) showAxioms(enable);
            if (p.equals("mch")) showMachines(enable);
            if (p.equals("inv")) showInvariants(enable);
            if (p.equals("eve")) showEvents(enable);
        }
    }

    public boolean horizontalLayout() { return horizontal_layout_; }
    public void setHorizontalLayout(boolean v) { horizontal_layout_ = v; }

    public int horizontalWidth() { return horizontal_width_; }
    public void setHorizontalWidth(int w) { horizontal_width_ = w; }

    public void addPartIdentifier(String part)
    {
        parts_.add(part);
    }

    public List<String> parts()
    {
        return parts_;
    }

}
