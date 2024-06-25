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

import com.viklauverk.eventbtools.core.Log;
import com.viklauverk.eventbtools.core.LogModule;
import com.viklauverk.eventbtools.core.Helpers.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class AllRenders
{
    private static Log log = LogModule.lookup("doc");

    private RenderMachine rm_;
    private RenderContext rc_;
    private RenderEvent re_;
    private RenderFormula rf_;

    Canvas current_, root_canvas_;
    LinkedList<Frame> stack_ = new LinkedList<>();

    private SystemSearch system_search_;

    @SuppressWarnings("this-escape")
    public AllRenders(RenderContext rc, RenderMachine rm,
                      RenderEvent re, RenderFormula rf,
                      Canvas c)
    {
        rc_ = rc;
        rm_ = rm;
        re_ = re;
        rf_ = rf;
        system_search_ = new SystemSearch();

        root_canvas_ = c;
        current_ = c;
        rc_.setRenders(this);
        rm_.setRenders(this);
        re_.setRenders(this);
    }

    public Canvas currentCanvas()
    {
        return current_;
    }

    private static class Frame
    {
        String id;
        Canvas canvas;

        Frame(String i, Canvas parent)
        {
            id = i;
            canvas = new Canvas(parent);
        }
    }

    public void pushCanvass(String id)
    {
        log.debug("push canvas "+id);
        Frame f = new Frame(id, current_);
        stack_.addFirst(f);
        current_ = f.canvas;
    }

    public void popStoreCanvasAndAppendd(String id)
    {
        log.debug("pop canvas "+id);
        Frame front = stack_.get(0);

        if (!front.id.equals(id))
        {
            log.error("internal error popping with id "+id+" but pushed with "+front.id);
        }

        Helpers.popFront(stack_);

        if (stack_.size() > 0)
        {
            current_ = stack_.get(0).canvas;
        }
        else
        {
            current_ = root_canvas_;
        }
        current_.appendCanvas(front.canvas);
    }

    public void walkContext(Context c, String pattern)
    {
        VisitContext.walk(rc_, c, pattern);
    }

    public void walkMachine(Machine m, String pattern)
    {
        VisitMachine.walk(rm_, m, pattern);
    }

    public void walkEvent(Event e, String pattern)
    {
        VisitEvent.walk(re_, e, pattern);
    }

    public void walkFormula(Formula i)
    {
        VisitFormula.walk(rf_, i);
    }

    public SystemSearch search()
    {
        return system_search_;
    }
}
