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

package com.viklauverk.evbt.core;

import com.viklauverk.common.log.Log;
import com.viklauverk.common.log.LogModule;

public class RenderAttributes implements Cloneable
{
    private static LogModule log = LogModule.lookup("renderattributes");

    private boolean color_ = true;    // Colorize the output.
    private boolean labels_ = true;   // Display the labels, like axm0_1: or @inv2 etc.
    private boolean comments_ = true;  // Display the comments.
    private boolean at_label_ = false;// If at_=true then display labels as @inv2 otherwise labels look like inv2:
    private boolean frame_ = false;   // Frame the output.
    private boolean horizontal_layout_ = false; // Make the output a horizontal box, like display:inline in css.
    private boolean indent_ = true;   // Indent the sub-parts.
    private boolean anchors_ = false; // Add anchors <a ...> or \label{} to definitions.
    private boolean indexes_ = false; // Add index notices.
    private boolean debug_ = false;   // Inject debug information into the generated rendered output.

    public RenderAttributes()
    {
    }

    public RenderAttributes clone() throws CloneNotSupportedException
    {
        return (RenderAttributes)super.clone();
    }

    public RenderAttributes copy()
    {
        try
        {
            return this.clone();
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public void setColor(boolean co)
    {
        color_ = co;
    }

    boolean color()
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

    public void setComments(boolean l)
    {
        comments_ = l;
    }

    public boolean comments()
    {
        return comments_;
    }

    public void setAtLabel(boolean v)
    {
        at_label_ = v;
    }

    public boolean atLabel()
    {
        return at_label_;
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

    public void setDefault()
    {
        set("color", "true");
        set("labels", "true");
        set("comments", "true");
        set("at_label", "false");
        set("anchors", "true");
        set("indexes", "true");
    }

    public void setCompact()
    {
        set("color", "true");
        set("labels", "false");
        set("comments", "false");
        set("at_label", "false");
        set("anchors", "true");
        set("indexes", "true");
    }

    boolean set(String key, String val)
    {
        log.debug("setting %s to %s", key, val);
        switch (key)
        {
        case "color":
        {
            if (!val.equals("true") && !val.equals("false")) return false;
            setColor(val.equals("true"));
            return true;
        }
        case "labels":
        {
            if (!val.equals("true") && !val.equals("false")) return false;
            setLabels(val.equals("true"));
            return true;
        }
        case "comments":
        {
            if (!val.equals("true") && !val.equals("false")) return false;
            setComments(val.equals("true"));
            return true;
        }
        case "at_label":
        {
            if (!val.equals("true") && !val.equals("false")) return false;
            setAtLabel(val.equals("true"));
            return true;
        }
        }
        return false;
    }

    public boolean setStyle(String s)
    {
        log.debug("decoding style: %s", s);

        Style style = new Style(s);

        if (!style.valid())
        {
            log.debug("not valid: %s", s);
            return false;
        }

        log.debug("set doc style: %s", style.main());

        switch (style.main())
        {
        case "default": setDefault(); break;
        case "compact": setCompact(); break;
        default:
            return false;
        }

        for (String key : style.keys())
        {
            String val = style.get(key);
            boolean ok = set(key, val);
            log.debug("set %s = %s", key, val);
            if (!ok) return false;
        }

        return true;
    }

    public String toString()
    {
        return
            "color="+color_+" "+
            "labels="+labels_+" "+
            "comments="+comments_+" "+
            "at_label="+at_label_+" "+
            "frame="+frame_+" "+
            "horizontal_layout="+horizontal_layout_+" "+
            "indent="+indent_+" "+
            "anchors="+anchors_+" "+
            "indexes="+indexes_+" "+
            "debug="+debug_;
    }

}
