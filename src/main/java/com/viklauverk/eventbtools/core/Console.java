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

import java.net.URL;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Comparator;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.tree.ParseTree;

public class Console
{
    private static Log log = LogModule.lookup("console");
    private static Log log_docgen = LogModule.lookup("docgen");

    private Sys sys_;

    private Canvas current_canvas_;
    private Deque<Canvas> canvas_stack_ = new ArrayDeque<>();

    private SymbolTable current_symbol_table_;
    private Deque<SymbolTable> table_stack_ = new ArrayDeque<>();

    private RenderTarget render_target_ = RenderTarget.TERMINAL;
    private RenderAttributes render_attributes_;

    private boolean running_ = true;

    public Console(Sys s, Settings set, Canvas canvas)
    {
        sys_ = s;

        current_canvas_ = canvas;
        canvas_stack_.addFirst(current_canvas_);

        current_symbol_table_ = sys_.rootSymbolTable();

        table_stack_.addFirst(current_symbol_table_);

        render_target_ = RenderTarget.TERMINAL;
        render_attributes_ = set.docGenSettings().renderAttributes();
    }

    public Sys sys()
    {
        return sys_;
    }

    public RenderTarget renderTarget()
    {
        return render_target_;
    }

    public RenderAttributes renderAttributes()
    {
        return render_attributes_;
    }

    void quit()
    {
        running_ = false;
    }

    public boolean running()
    {
        return running_;
    }

    public SymbolTable currentSymbolTable()
    {
        return current_symbol_table_;
    }

    public Canvas currentCanvas()
    {
        return current_canvas_;
    }

    public void setRenderTarget(RenderTarget t)
    {
        render_target_ = t;
    }

    public String renderTemplate(String name)
    {
        for (int i=0; i<Templates.templates.length; i+=2)
        {
            if (Templates.templates[i].equals(name))
            {
                return Templates.templates[i+1];
            }
        }
        return "Template "+name+" not found!";
    }

    public String renderPart(String name, RenderTarget rt, RenderAttributes ra)
    {
        CommonSettings cs = new CommonSettings();
        DocGenSettings ds = new DocGenSettings(rt, ra);

        BaseDocGen bdg = DocGen.lookup(cs, ds, sys_);

        String result = "";

        Canvas cnvs = sys().console().currentCanvas();
        cnvs.setRenderTarget(rt);
        cnvs.setRenderAttributes(ra);
        cnvs.clear();

        log_docgen.debug("console render part attributes "+cnvs.renderAttributes());
        result = bdg.renderParts(cnvs, name);
        if (ra.frame())
        {
            result = cnvs.frame("", result, Canvas.sline);
        }
        return result;
    }

    public void forceCurrentSymbolTable(SymbolTable st)
    {
        current_symbol_table_ = st;
    }

    void pushSymbolTable(String name)
    {
        SymbolTable st = sys_.newSymbolTable(name);
        st.addParent(current_symbol_table_);
        table_stack_.addFirst(st);
        current_symbol_table_ = st;
    }

    String popSymbolTable()
    {
        if (table_stack_.size() <= 1)
        {
            return "Cannot pop root symbol table";
        }
        table_stack_.removeFirst();
        current_symbol_table_ = table_stack_.peek();
        return "OK";
    }

    private void setSymbolTable(SymbolTable st)
    {
        current_symbol_table_ = st;
    }

    private String deduceType(String line)
    {
        Formula result = Formula.fromString(line, current_symbol_table_);
        ImplType type = null;
        if (result != null)
        {
            type = sys_.typing().deducePossibleImplTypesFromExpression(result, current_symbol_table_);
        }

        if (result != null && type != null)
        {
            return type.formula().toString(current_canvas_);
        }
        else
        {
            return "Could not deduce any type!";
        }
    }

    boolean startsWithEndsWith(char c, String line)
    {
        int first;
        for (first=0; first<line.length(); ++first)
        {
            if (line.charAt(first) == c) break;
            if (line.charAt(first) != ' ') return false;
        }
        int last;
        for (last=line.length()-1; last>first; last--)
        {
            if (line.charAt(last) == c) break;
            if (line.charAt(first) != ' ') return false;
        }
        if (first == last) return false;
        return true;
    }

    public String go(String line_in)
    {
        if (line_in.trim().length() == 0) return "";
        String line = line_in;
        log.debug("go: %s", line);
        try
        {
            ConsoleExecutor ce = new ConsoleExecutor(this, line);
            return ce.go();
        }
        catch (Exception e)
        {
            log.info("Could not execute command \"%s\"", line);
            return "FAILED COMMAND \""+line+"\"";
        }
    }

    public String renderFormula(String line, boolean with_meta, boolean with_type, boolean with_frame, RenderTarget rt, RenderAttributes ra)
    {
        Canvas cnvs = new Canvas();
        cnvs.setRenderTarget(rt);
        cnvs.setRenderAttributes(ra);
        cnvs.clear();

        try
        {
            // Parse the Event-B formula.
            Formula result = Formula.fromStringMightFail(line, current_symbol_table_);
            // If succ
            if (result == null)
            {
                return "Failed to parse!";
            }

            if (!with_type && with_meta)
            {
                cnvs.startMath();
                result.toStringWithMetas(cnvs);
                cnvs.stopMath();
                String o = cnvs.render();
                if (with_frame)
                {
                    o = cnvs.frame("", o, Canvas.sline);
                }
                return o;
            }

            if (with_type && !with_meta)
            {
                cnvs.startMath();
                result.toStringWithTypes(cnvs);
                cnvs.stopMath();
                String o = cnvs.render();
                if (with_frame)
                {
                    o = cnvs.frame("", o, Canvas.sline);
                }
                return o;
            }

            if (with_type && with_meta)
            {
                cnvs.startMath();
                result.toStringWithMetasAndTypes(cnvs);
                cnvs.stopMath();
                String o = cnvs.render();
                if (with_frame)
                {
                    o = cnvs.frame("", o, Canvas.sline);
                }
                return o;
            }

            cnvs.startMath();
            result.toString(cnvs);
            cnvs.stopMath();
            String o = cnvs.render();
            if (with_frame)
            {
                o = cnvs.frame("", o, Canvas.sline);
            }
            return o;
        }
        catch (Error e)
        {
            return e.getMessage();
        }
    }
}
