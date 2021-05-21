/*
 Copyright (C) 2021 Viklauverk AB
 Author Fredrik Öhrström

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

public class RenderAttributes
{
    private boolean color_ = true;    // Colorize the output.
    private boolean labels_ = true;   // Display the labels, like axm0_1: or @inv2 etc.
    private boolean at_ = false;      // If at_ then display labels as @inv2 instead of inv2:
    private boolean frame_ = false;   // Frame the output.
    private boolean horizontal_layout_ = false; // Make the output a horizontal box, like display:inline in css.
    private boolean indent_ = true;   // Indent the sub-parts.
    private boolean anchors_ = false; // Add anchors <a ...> or \label{} to definitions.
    private boolean indexes_ = false; // Add index notices.
    private boolean debug_ = false;   // Inject debug information into the generated rendered output.

    public void setColor(boolean co)
    {
        color_ = co;
    }

    public boolean color()
    {
        return color_;
    }

    public void setLabels(boolean l)
    {
        labels_ = l;
    }

    public boolean labels()
    {
        return labels_;
    }

    public void setAt(boolean v)
    {
        at_ = v;
    }

    public boolean at()
    {
        return at_;
    }

    public void setFrame(boolean wf)
    {
        frame_ = wf;
    }

    public boolean frame()
    {
        return frame_;
    }

    public void setHorizontalLayout(boolean hl)
    {
        horizontal_layout_ = hl;
    }

    public boolean horizontalLayout()
    {
        return horizontal_layout_;
    }

    public void setIndent(boolean t)
    {
        indent_ = t;
    }

    public boolean indent()
    {
        return indent_;
    }

    public void setAnchors(boolean t)
    {
        anchors_ = t;
    }

    public boolean anchors()
    {
        return anchors_;
    }

    public void setIndexes(boolean t)
    {
        indexes_ = t;
    }

    public boolean index()
    {
        return indexes_;
    }

    public void set(String key, String val)
    {
        switch (key)
        {
        case "color":
        {
            assert(val.equals("true") || val.equals("false"));
            setColor(val.equals(true));
            return;
        }
        case "labels":
        {
            assert(val.equals("true") || val.equals("false"));
            setLabels(val.equals(true));
            return;
        }
        }
    }

}
