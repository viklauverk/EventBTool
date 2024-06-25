/*
 Copyright (C) 2021-2023 Viklauverk AB

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

import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import com.viklauverk.eventbtools.core.cmd.Cmd;
import com.viklauverk.eventbtools.core.cmd.CmdCommon;

public class ConsoleExecutor
{
    private static Log log = LogModule.lookup("console");

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
        return cco.go();
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
    RenderTarget useOrDefault(String t, RenderTarget dt)
    {
        if (t == null || t.equals("")) return dt;
        return RenderTarget.lookup(t);
    }

    boolean isSet(String t, String d)
    {
        if (t == null || !t.equals(d)) return false;
        return true;
    }

    // This code is obnoxious, is there a better way to do this? And keep
    // The detection of incomplete command lines...
    @Override
    public String visitDone(ConsoleParser.DoneContext ctx)
    {
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
		TerminalNode eof = ctx.EOF();

        if (eof == null) {
            return "Could not parse whole line.\n";
        }

        String r = "";
        if (quit != null)
        {
            r = this.visit(quit);
        }
        if (helper != null)
        {
            r = this.visit(helper);
        }
        if (history != null)
        {
            r = this.visit(history);
        }
        if (ca_canvas != null)
        {
            r = this.visit(ca_canvas);
        }
        if (env_list != null)
        {
            r = this.visit(env_list);
        }
        if (env_print_template != null)
        {
            r = this.visit(env_print_template);
        }
        if (env_read != null)
        {
            r = this.visit(env_read);
        }
        if (util_match != null)
        {
            r = this.visit(util_match);
        }
        if (yms_add != null)
        {
            r = this.visit(yms_add);
        }
        if (yms_push != null)
        {
            r = this.visit(yms_push);
        }
        if (yms_pop != null)
        {
            r = this.visit(yms_pop);
        }
        if (yms_show != null)
        {
            r = this.visit(yms_show);
        }
        if (env_set != null)
        {
            r = this.visit(env_set);
        }
        if (eb_show != null)
        {
            r = this.visit(eb_show);
        }

        if (r == null)
        {
            return "Internal error, exception inside ConsoleExecutor visitor?\n";
        }
        return r;
    }

    @Override
    public String visitAddDefaultSymbols(ConsoleParser.AddDefaultSymbolsContext ctx)
    {
        console_.currentSymbolTable().addDefaults();
        return "OK";
    }

    @Override
    public String visitAddSymbols(ConsoleParser.AddSymbolsContext ctx)
    {
        String type = ctx.getStart().getText();

        StringBuilder sb = new StringBuilder();
        List<String> elements = new LinkedList<>();
        ConsoleParser.ListOfSymbolsContext list = (ConsoleParser.ListOfSymbolsContext)ctx.symbols();
        for (org.antlr.v4.runtime.tree.TerminalNode sec : list.SYMBOL())
        {
            elements.add(sec.getText());
        }
        //log.debug("visit ");

        switch (type)
        {
        case "yms.add.anys":
            console_.currentSymbolTable().addAnySymbols(elements);
            break;
        case "yms.add.constants":
            console_.currentSymbolTable().addConstantSymbols(elements);
            break;
        case "yms.add.expressions":
            console_.currentSymbolTable().addExpressionSymbols(elements);
            break;
        case "yms.add.numbers":
            console_.currentSymbolTable().addNumberSymbols(elements);
            break;
        case "yms.add.predicates":
            console_.currentSymbolTable().addPredicateSymbols(elements);
            break;
        case "yms.add.sets":
            console_.currentSymbolTable().addSetSymbols(elements);
            break;
        case "yms.add.variables":
            console_.currentSymbolTable().addVariableSymbols(elements);
            break;
        }

        return "OK";
    }

    @Override
    public String visitHelpHelp(ConsoleParser.HelpHelpContext ctx)
    {
        Canvas c = new Canvas();
        c.setRenderTarget(RenderTarget.PLAIN);
        c.append(HelpLines.help);
        return c.render();
    }

    @Override
    public String visitHelp(ConsoleParser.HelpContext ctx)
    {
        String arg = ctx.name.getText();
        if (arg == null) arg = "";

        String help = HelpLines.helps.get(arg);
        if (help != null)
        {
            Canvas c = new Canvas();
            c.setRenderTarget(RenderTarget.PLAIN);
            c.append(help);
            return c.render();
        }

        return "Unknown command "+arg;
    }

    @Override
    public String visitQuitQuit(ConsoleParser.QuitQuitContext ctx)
    {
        console_.quit();
        return "quit";
    }

    @Override
    public String visitListTables(ConsoleParser.ListTablesContext ctx)
    {
        StringBuilder sb = new StringBuilder();
        for (String t : console_.sys().allSymbolTables().keySet())
        {
            sb.append(t);
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String visitListMachines(ConsoleParser.ListMachinesContext ctx)
    {
        StringBuilder sb = new StringBuilder();
        for (String t : console_.sys().machineNames())
        {
            sb.append(t);
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String visitListContexts(ConsoleParser.ListContextsContext ctx)
    {
        StringBuilder sb = new StringBuilder();
        for (String t : console_.sys().contextNames())
        {
            sb.append(t);
            sb.append("\n");
        }
        return sb.toString();
    }

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
    public String visitListParts(ConsoleParser.ListPartsContext ctx)
    {
        List<String> parts = console_.sys().listParts();

        StringBuilder sb = new StringBuilder();
        for (String p : parts)
        {
            sb.append(p);
            sb.append("\n");
        }
        return sb.toString();
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
    public String visitReadDir(ConsoleParser.ReadDirContext ctx)
    {
        String dir = removeQuotes(ctx.dir.getText());

        try
        {
            return console_.sys().loadMachinesAndContexts(dir);
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
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
    public String visitListHistory(ConsoleParser.ListHistoryContext ctx)
    {
        return "history...";
    }

    @Override
    public String visitShowCurrentTable(ConsoleParser.ShowCurrentTableContext ctx)
    {
        return console_.currentSymbolTable().name();
    }

    String removeQuotes(String f)
    {
        if (f.startsWith("\"") &&
            f.endsWith("\""))
        {
            return f.substring(1, f.length()-1).trim();
        }
        return f.trim();
    }
    */
}
