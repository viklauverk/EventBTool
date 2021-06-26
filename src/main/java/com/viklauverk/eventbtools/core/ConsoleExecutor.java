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

import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

public class ConsoleExecutor extends ConsoleBaseVisitor<String>
{
    private Console console_;
    private CommonTokenStream tokens_;

    ConsoleExecutor(Console console, CommonTokenStream tokens)
    {
        console_ = console;
        tokens_ = tokens;
    }

    RenderTarget useOrDefault(ParserRuleContext t, RenderTarget dt)
    {
        if (t == null || t.getText() == null || t.getText().equals("")) return dt;
        return RenderTarget.lookup(t.getText());
    }

    boolean isSet(ParserRuleContext t, String d)
    {
        if (t == null || t.getText() == null || !t.getText().equals(d)) return false;
        return true;
    }

    @Override
    public String visitDone(ConsoleParser.DoneContext ctx)
    {
        ConsoleParser.AddContext add = ctx.add();
        ConsoleParser.CanvasContext canvas = ctx.canvas();
        ConsoleParser.HelperContext helper = ctx.helper();
        ConsoleParser.HistoryContext history = ctx.history();
        ConsoleParser.ListContext list = ctx.list();
        ConsoleParser.MatchContext match = ctx.match();
        ConsoleParser.PopContext pop = ctx.pop();
        ConsoleParser.PushContext push = ctx.push();
        ConsoleParser.ReadContext read = ctx.read();
        ConsoleParser.SetContext set = ctx.set();
        ConsoleParser.ShowContext show = ctx.show();
		TerminalNode eof = ctx.EOF();

        if (eof == null) {
            return "Could not parse whole line.\n";
        }

        String r = "";
        if (helper != null)
        {
            r = this.visit(helper);
        }
        if (history != null)
        {
            r = this.visit(history);
        }
        if (add != null)
        {
            r = this.visit(add);
        }
        if (canvas != null)
        {
            r = this.visit(canvas);
        }
        if (list != null)
        {
            r = this.visit(list);
        }
        if (read != null)
        {
            r = this.visit(read);
        }
        if (match != null)
        {
            r = this.visit(match);
        }
        if (push != null)
        {
            r = this.visit(push);
        }
        if (pop != null)
        {
            r = this.visit(pop);
        }
        if (set != null)
        {
            r = this.visit(set);
        }
        if (show != null)
        {
            r = this.visit(show);
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
        String type = ctx.typeOfSymbol().getText();

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
        case "vars":
            console_.currentSymbolTable().addVariableSymbols(elements);
            break;
        case "consts":
            console_.currentSymbolTable().addConstantSymbols(elements);
            break;
        case "sets":
            console_.currentSymbolTable().addSetSymbols(elements);
            break;
        case "preds":
            console_.currentSymbolTable().addPredicateSymbols(elements);
            break;
        case "exprs":
            console_.currentSymbolTable().addExpressionSymbols(elements);
            break;
        case "nums":
            console_.currentSymbolTable().addNumberSymbols(elements);
            break;
        case "anys":
            console_.currentSymbolTable().addAnySymbols(elements);
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
        String arg = ctx.topic().getText();
        if (arg == null) arg = "";

        if (arg.equals("shortcuts"))
        {
            String[] sc = console_.short_cuts;
            StringBuilder out = new StringBuilder();
            out.append("Available short cuts:\n");
            for (int i=0; i<sc.length; i+=3)
            {
                out.append(sc[i]);
                out.append(" ");
                out.append(sc[i+1]);
                out.append(" ");
                out.append(sc[i+2]);
                out.append("\n");
            }
            return out.toString();
        }
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
        System.out.println("GURKA");
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
    public String visitListSymbols(ConsoleParser.ListSymbolsContext ctx)
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

    /*
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
    */

    @Override
    public String visitSetDefaultFormat(ConsoleParser.SetDefaultFormatContext ctx)
    {
        String target = ctx.format().getText();
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
    public String visitShowPart(ConsoleParser.ShowPartContext ctx)
    {
        String pattern = removeQuotes(ctx.name.getText());
        if (!pattern.startsWith("*"))
        {
            // Force the pattern to match all to the end of the path.
            pattern += "/";
        }
        RenderTarget rt = useOrDefault(ctx.format(), console_.renderTarget());
        RenderAttributes ra = console_.renderAttributes().copy();
        ra.setFrame(isSet(ctx.framed(), "framed"));

        String content = console_.renderPart(pattern, rt, ra);
        if (content == null)
        {
            return "EVBT_ERROR: Part \""+pattern+"\" not found!";
        }
        return content;
    }

    @Override
    public String visitShowTemplate(ConsoleParser.ShowTemplateContext ctx)
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
        String treee = ctx.treee() != null ? ctx.treee().getText() : "";
        RenderTarget rt = useOrDefault(ctx.format(), console_.renderTarget());
        RenderAttributes ra = console_.renderAttributes();
        String framed = ctx.framed() != null ? ctx.framed().getText() : "";

        boolean show_tree = treee.equals("tree");
        boolean show_frame = framed.equals("framed");

        return console_.renderFormula(f, show_tree, show_frame, rt, ra);
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
}
