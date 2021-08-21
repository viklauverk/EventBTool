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

    Sys sys()
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

    SymbolTable currentSymbolTable()
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

    String renderTemplate(String name)
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

    String renderPart(String name, RenderTarget rt, RenderAttributes ra)
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

    String[] short_cuts = {
        // Add commands...
        "aa ", "add anys", "",
        "ac ", "add consts", "",
        "ad ", "add defaults", "",
        "ae ", "add exprs", "",
        "an ", "add nums", "",
        "ap ", "add preds", "",
        "as ", "add sets", "",
        "av ", "add vars", "",
        // Canvas commands
        "ca ", "canvas attributes", "",
        "ct ", "canvas target", "",
        // List commands...
        "lt ", "list tables", "",
        "la ", "list anys", "",
        "lc ", "list consts", "",
        "le ", "list exprs", "",
        "lp ", "list preds", "",
        "ls ", "list sets", "",
        "lh ", "list hyps", "",
        "lv ", "list vars", "",
        // Show commmands....
        "sf ", "show formula", ".",
        "sft ", "show formula tree", ".",
        "sftf ", "show formula tree framed", ".",
        "sff ", "show formula framed", ".",
        "st ", "show table", ".",
        "sp ", "show part", ".",
        "spf ", "show part framed", ".",
        "m ", "match", "..",
    };

    public String tryExpandCommands(String line)
    {
        assert (short_cuts.length % 3 == 0);
        for (int i=0; i<short_cuts.length; i+=3)
        {
            String prefix = short_cuts[i];
            if (line.startsWith(prefix))
            {
                String replace = short_cuts[i+1];
                String string_args = short_cuts[i+2];
                String r = line.substring(prefix.length()).trim();
                if (string_args.length() == 0)
                {
                    return replace + " " + r;
                }
                if (string_args.length() == 1)
                {
                    return replace + " \""+r+"\"";
                }
                if (string_args.length() == 2)
                {
                    int p = r.indexOf(" ");
                    if (p == -1)
                    {
                        return replace + " \""+r+"\"";
                    }
                    String left = r.substring(0, p);
                    String right = r.substring(p+1);
                    return replace + " \""+left+"\" \""+right+"\"";
                }
                assert (false) : "Unexpected string_args length "+string_args.length();
            }
        }
        return line;
    }

    public String go(String line_in)
    {
        line_in = line_in + " ";
        String line = tryExpandCommands(line_in);
        if (!line.equals(line_in))
        {
            log.debug("expanded command: '%s' to '%s'", line_in, line);
        }
        CharStream lineStream = CharStreams.fromString(line);

        ConsoleLexer lexer = new ConsoleLexer(lineStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ConsoleParser parser = new ConsoleParser(tokens);
        //parser.setTrace(true);
        lexer.removeErrorListeners();
        parser.removeErrorListeners();
        parser.addErrorListener(ThrowingErrorListener.INSTANCE);

        log.debug("go: %s", line);
        ParseTree tree = null;
        try
        {
            tree = parser.start();
        }
        catch (Exception e)
        {
            String info = "Could not parse command: \""+line+"\"\n"+e.getMessage();
            log.info("%s", info);
            /*
              System.out.println("========================");
              System.out.println(ReflectionToStringBuilder.toString(parser));
              System.out.println("========================");*/
            return info;
        }
        if (parser.getNumberOfSyntaxErrors() > 0)
        {
            String info = "Could not parse: \""+line+"\" since it has "+
                parser.getNumberOfSyntaxErrors()+
                " syntax errors.\n";

            log.debug("%s", info);
            return info;
        }

        try
        {
            ConsoleExecutor ce = new ConsoleExecutor(this, tokens);
            return ce.visit(tree);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.info("Could not execute command \"%s\"", line);
            return "FAILED COMMAND \""+line+"\"";
        }
    }

    String renderFormula(String line, boolean with_type, boolean with_frame, RenderTarget rt, RenderAttributes ra)
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

            if (with_type)
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
