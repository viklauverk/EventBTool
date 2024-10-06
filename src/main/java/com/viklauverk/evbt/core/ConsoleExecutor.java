/*
 Copyright (C) 2021-2023 Viklauverk AB (agpl-3.0-or-later)

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
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import com.viklauverk.evbt.core.cmd.Cmd;
import com.viklauverk.evbt.core.cmd.CmdCommon;

public class ConsoleExecutor
{
    private static Log log = LogModule.lookup("console", ConsoleExecutor.class);

    private Console console_;
    private String line_;
    private int index_;

    ConsoleExecutor(Console console, String line)
    {
        console_ = console;
        line_ = line;
    }

    String go()
    {
        index_ = 0;
        String tok = getNextToken();

        if (tok.equals("q") || tok.equals("quit"))
        {
            console_.quit();
            return "";
        }

        Cmd cmd = Cmd.parse(tok);

        if (cmd == null || cmd == Cmd.ERROR)
        {
            return LogModule.safeFormat("Unknown command: %s", tok);
        }

        skipWhitespace();

        String args = line_.substring(index_);
        CmdCommon cco = cmd.constructor.create(console_, args);
        log.debug("Executing cmd %s on line >%s<\n", cmd, args);
        String response = "";
        try
        {
            response = cco.go();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
            response = "CAUGHT: "+e.toString();
        }
        return response;
    }

    void skipWhitespace()
    {
        while (index_ < line_.length() && line_.charAt(index_) == ' ') index_++;
    }

    boolean isTokenChar(char c)
    {
        if (c >= 'a' && c <= 'z') return true;
        if (c >= 'A' && c <= 'Z') return true;
        if (c == '.') return true;
        if (c == '-') return true;
        return false;
    }

    String getNextToken()
    {
        skipWhitespace();

        StringBuilder sb = new StringBuilder();
        while (index_ < line_.length())
        {
            char c = line_.charAt(index_);
            if (!isTokenChar(c)) break;
            sb.append(c);
            index_++;
        }

        return sb.toString();
    }

    /*
        ConsoleParser.HelperContext helper = ctx.helper();
        ConsoleParser.QuitContext quit = ctx.quit();
        ConsoleParser.HistoryContext history = ctx.history();
        ConsoleParser.Ca_canvasContext ca_canvas = ctx.ca_canvas();
        ConsoleParser.Env_print_templateContext env_print_template = ctx.env_print_template();
        ConsoleParser.Env_readContext env_read = ctx.env_read();
        ConsoleParser.Env_setContext env_set = ctx.env_set();
        ConsoleParser.Env_listContext env_list = ctx.env_list();
        ConsoleParser.Yms_addContext yms_add = ctx.yms_add();
        ConsoleParser.Yms_popContext yms_pop = ctx.yms_pop();
        ConsoleParser.Yms_pushContext yms_push = ctx.yms_push();
        ConsoleParser.Yms_showContext yms_show = ctx.yms_show();
        ConsoleParser.Eb_showContext eb_show = ctx.eb_show();
        ConsoleParser.Util_matchContext util_match = ctx.util_match();

    @Override
    private String printSyms(Set<String> syms)
    {
        StringBuilder o = new StringBuilder();
        List<String> ns = syms.stream().sorted().collect(Collectors.toList());
        for (String n : ns)
        {
            o.append(n);
            o.append("\n");
        }
        return o.toString();
    }


    @Override
    public String visitShowPartOfSymbolTable(ConsoleParser.ShowPartOfSymbolTableContext ctx)
    {
        String type = ctx.typeOfSymbol().getText();

        StringBuilder sb = new StringBuilder();

        switch (type)
        {
        case "anys":
            return printSyms(console_.currentSymbolTable().anySymbols());

        case "consts":
            return printSyms(console_.currentSymbolTable().constantSymbols());

        case "exprs":
            return printSyms(console_.currentSymbolTable().expressionSymbols());

        case "nums":
            return printSyms(console_.currentSymbolTable().numberSymbols());

        case "preds":
            return printSyms(console_.currentSymbolTable().predicateSymbols());

        case "sets":
            return printSyms(console_.currentSymbolTable().setSymbols());

        case "vars":
            return printSyms(console_.currentSymbolTable().variableSymbols());

        default:
            assert (false) : "internal error: missing case for "+type;
        }
        return "?";
    }


    @Override
    public String visitMatchPattern(ConsoleParser.MatchPatternContext ctx)
    {
        String f = removeQuotes(ctx.formula.getText());
        String pattern = removeQuotes(ctx.pattern.getText());

        Formula formula = Formula.fromString(f, console_.currentSymbolTable());
        Pattern p = new Pattern();
        boolean ok = p.match(formula, "match", pattern);

        if (!ok)
        {
            return "No match!";
        }

        StringBuilder out = new StringBuilder();
        for (String key : p.predicateNames())
        {
            out.append(""+key+"="+p.getPred(key)+"\n");
        }

        for (String key : p.expressionNames())
        {
            out.append(""+key+"="+p.getExpr(key)+"\n");
        }

        for (String key : p.setNames())
        {
            out.append(""+key+"="+p.getSet(key)+"\n");
        }

        for (String key : p.variableNames())
        {
            out.append(""+key+"="+p.getVar(key)+"\n");
        }

        for (String key : p.constantNames())
        {
            out.append(""+key+"="+p.getConst(key)+"\n");
        }

        for (String key : p.numberNames())
        {
            out.append(""+key+"="+p.getNumber(key)+"\n");
        }

        for (String key : p.anyNames())
        {
            out.append(""+key+"="+p.getAny(key)+"\n");
        }

        return out.toString();
    }

    @Override
    public String visitPushTable(ConsoleParser.PushTableContext ctx)
    {
        String name = removeQuotes(ctx.name.getText());

        console_.pushSymbolTable(name);
        return "OK";
    }

    @Override
    public String visitPopTable(ConsoleParser.PopTableContext ctx)
    {
        return console_.popSymbolTable();
    }

    @Override
    public String visitRenderWithFormat(ConsoleParser.RenderWithFormatContext ctx)
    {
        CommonSettings cs = new CommonSettings();
        DocGenSettings ds = new DocGenSettings();
        ds.parseFormat(ctx.format().getText());

        BaseDocGen bdg = DocGen.lookup(cs, ds, console_.sys());
        String result = "";
        try
        {
            result = bdg.renderParts("");
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
        return result;
    }

    @Override
    public String visitSetDefaultRenderTarget(ConsoleParser.SetDefaultRenderTargetContext ctx)
    {
        String target = ctx.renderTarget().getText();
        console_.setRenderTarget(RenderTarget.lookup(target));
        return "OK";
    }

    @Override
    public String visitShowTable(ConsoleParser.ShowTableContext ctx)
    {
        String name = removeQuotes(ctx.name.getText());
        SymbolTable st = console_.sys().getSymbolTable(name);
        return st.print();
    }


    @Override
    public String visitEnvPrintTemplate(ConsoleParser.EnvPrintTemplateContext ctx)
    {
        String name = removeQuotes(ctx.name.getText());
        String s = console_.renderTemplate(name);

        if (s == null)
        {
            return "EVBT_ERROR: Template "+name+" not found!";
        }

        return s;
    }

    @Override
    public String visitShowFormula(ConsoleParser.ShowFormulaContext ctx)
    {
        String f = removeQuotes(ctx.formula.getText());
        System.out.println("FORMAL >"+f+"<");
        String metaaa = ctx.metaaa() != null ? ctx.metaaa().getText() : "";
        String treee = ctx.treee() != null ? ctx.treee().getText() : "";
        RenderTarget rt = useOrDefault(ctx.renderTarget(), console_.renderTarget());
        RenderAttributes ra = console_.renderAttributes();
        String framed = ctx.framed() != null ? ctx.framed().getText() : "";

        boolean show_meta = metaaa.equals("meta");
        boolean show_tree = treee.equals("tree");
        boolean show_frame = framed.equals("framed");

        return console_.renderFormula(f, show_meta, show_tree, show_frame, rt, ra);
    }

    @Override
    public String visitCanvasSetRenderAttributes(ConsoleParser.CanvasSetRenderAttributesContext ctx)
    {
        for (ConsoleParser.RenderAttributeContext c : ctx.renderAttribute())
        {
            String key = c.attrKey().getText();
            String val = removeQuotes(c.attrVal.getText());
            System.out.println("KEY="+key+"  VAL="+val);
            console_.currentCanvas().renderAttributes().set(key, val);
        }
        return "OK";
    }


    @Override
    public String visitShowCurrentTable(ConsoleParser.ShowCurrentTableContext ctx)
    {
        return console_.currentSymbolTable().name();
    }

    */
}
