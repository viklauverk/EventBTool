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

import static com.viklauverk.evbt.core.Node.*;

import com.viklauverk.common.log.Log;
import com.viklauverk.common.log.LogModule;
import java.net.URL;

import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;


public class CodeGenJavascript extends BaseCodeGen
{
    private static Log log = LogModule.lookup("codegen", CodeGenJavascript.class);

    public CodeGenJavascript(CommonSettings coms, CodeGenSettings cdgs, Sys sys, Machine mch)
    {
        super(coms, cdgs, sys, mch);
    }

    public void run()
    {
        try
        {
            String file = nickName()+".js";
            log.info("Writing "+file);
            open(file);
            generateJS();
            writeToFile(cnvs().render());
            cnvs().clear();
            close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    void generateJS() throws Exception
    {
        pl("module.exports = {");
        pl("    create: function() { return new "+nickName()+"(); }");
        pl("};");
        pl("");
        pl("class "+nickName());
        pl("{");
        pl("    constructor()");
        pl("    {");
        pl("        // Constants");

        writeConstants();

        pl("        // Variables");
        pl("        this.trace_cb = null;");

        writeInitialisation();

        pl("    }");
        pl("");

        writeEvents();

        pl("");
        pl("    trace(cb) { this.trace_cb = cb; }");

        pl("    traceEvent(msg) { if (this.trace_cb) this.trace_cb(msg); }");

        pl("    run()");
        pl("    {");

        writeEventLoop();

        pl("    }");
        pl("}");
    }

    void writeConstants() throws Exception
    {
        for (String ctxs : mch().contextNames())
        {
            Context ctx = mch().getContext(ctxs);

            for (String csn : ctx.setNames())
            {
                CarrierSet cs = ctx.getSet(csn);
                pl("        // Set "+cs.name());
                int i = 0;
                for (String member : cs.memberNames())
                {
                    pl("        this."+cs.name()+"_"+member+" = "+i+";");
                    i++;
                }
            }

            for (String con : ctx.constantNames())
            {
                Constant co = ctx.getConstant(con);
                if (co.hasDefinition())
                {
                    pl("        this."+co.name()+" = "+co.definition()+";");
                }
            }
        }
    }

    void writeVariablesForConstruction() throws Exception
    {
        for (String vars : mch().variableNames())
        {
            Variable var = mch().getVariable(vars);

            String construct = translateImplTypeForConstruction(var.implType());
            if (construct != null)
            {
                pl("        this."+var.name()+construct+";");
            }
        }
    }

    void writeInitialisation() throws Exception
    {
        Event e = mch().getConcreteEvent("INITIALISATION");
        // First write necessary constructors for complex types like sets and maps.
        // Skip initialisations that implicitly define the type, like NAT, BOOL and CarrierSets.
        writeVariablesForConstruction();
        // Now write the initialisations, some of which implicitly define the types.
        writeEventActions(e);
    }

    void writeEventActions(Event e) throws Exception
    {
        for (Action a : e.actionOrdering())
        {
            p("        ");
            renderFormulaOntoCanvas(a.formula(), e.symbolTable());
            pl(";");
        }
    }

    String calcParameters(Event e) throws Exception
    {
        StringBuilder params = new StringBuilder();
        int c = 0;
        for (Variable p : e.parameterOrdering())
        {
            if (c > 0) params.append(",");
            params.append(p.name());
            c++;
        }
        return params.toString();
    }

    void writeEvents() throws Exception
    {
        for (String en : mch().concreteEventNames())
        {
            Event e = mch().getConcreteEvent(en);
            if (e.isInit()) continue;
            pl("    "+e.name()+"("+calcParameters(e)+")");
            pl("    {");
            writeGuards(e);
            pl("");
            writeActions(e);
            pl("        this.traceEvent('"+e.name()+"');");
            pl("        return true;");
            pl("    }");
            pl("");
        }
    }

    void writeGuards(Event e) throws Exception
    {
        for (String gn : e.guardNames())
        {
            Guard g = e.getGuard(gn);
            p("        const "+g.name()+" = Boolean(");
            renderFormulaOntoCanvas(g.formula(), e.symbolTable());
            pl(");");
            pl("        if (!"+g.name()+") return false;");
        }
    }

    void writeActions(Event e) throws Exception
    {
        for (String an : e.actionNames())
        {
            Action a = e.getAction(an);
            pl("        // "+a.name()+" "+a.comment());
            p("        ");
            renderFormulaOntoCanvas(a.formula(), e.symbolTable());
            pl(";");
        }
    }

    void writeEventLoop() throws Exception
    {
        pl("        let c = 0;");
        pl("        for (;;)");
        pl("        {");
        for (String en : mch().eventNames())
        {
            Event e = mch().getEvent(en);
            if (e.isInit()) continue;
            if (e.hasParameters()) continue;
            pl("            if (this."+e.name()+"()) { c++; continue; }");
        }
        pl("            break;");
        pl("        }");
        pl("        return c;");
    }

    String translateImplTypeForConstruction(ImplType t)
    {
        Formula f = t.formula();
        if (f.is(POWER_SET))
        {
            return " = new Set()";
        }
        if (f.is(PARTIAL_FUNCTION))
        {
            return " = new Map()";
        }
        return null;
    }

    void renderFormulaOntoCanvas(Formula f, SymbolTable st)
    {
        PlanImplementation plan = new PlanImplementation(this);
        CodeGenFormulaJavascript gen = new CodeGenFormulaJavascript(this, plan);
        gen.setSymbolTable(st);
        VisitFormula.walkk(plan, f);
        VisitFormula.walk(gen, f);
    }

    @Override
    public boolean handleSetClear(Formula setvar, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("this."+setvar+".clear()");
        return true;
    }

    @Override
    public boolean handleAddToSet(Formula setvar, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("this."+setvar+".add("+value+")");
        return true;
    }

    @Override
    public boolean handleSubtractFromSet(Formula setvar, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("this."+setvar+".delete("+value+")");
        return true;
    }

    @Override
    public boolean handleMapOverride(Formula setvar, Formula index, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("this."+setvar+".set("+index+","+value+")");
        return true;
    }

    @Override
    public boolean handleMapDomainSubtraction(Formula setvar, Formula key, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("this."+setvar+".delete("+key+")");
        return true;
    }

    @Override
    public boolean handleMembershipInVariable(Formula setvar, Formula member, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("this."+setvar+".has("+member+")");
        return true;
    }

    @Override
    public boolean handleMembershipNotInVariable(Formula setvar, Formula member, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("!this."+setvar+".has("+member+")");
        return true;
    }

    @Override
    public boolean handleAlwaysTrue(SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("true");
        return true;
    }

    @Override
    public boolean handleAlwaysFalse(SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("false");
        return true;
    }

    @Override
    public boolean handlePairMembershipInFunctionSetvar(Formula setvar, Formula key, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("this."+setvar+".has("+key+") && "+setvar+".get("+key+") === "+value);
        return true;
    }

    @Override
    public boolean handlePairMembershipNotInFunctionSetvar(Formula setvar, Formula key, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("!this."+setvar+".has("+key+") || "+setvar+".get("+key+") !== "+value);
        return true;
    }

    @Override
    public boolean handleMembershipInDomainOfFunctionSetvar(Formula setvar, Formula key, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("this."+setvar+".has("+key+")");
        return true;
    }

    @Override
    public boolean handleMembershipNotInDomainOfFunctionSetvar(Formula setvar, Formula key, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("!this."+setvar+".has("+key+")");
        return true;
    }

    @Override
    public boolean handleFindExistentialQOverSet(Formula setvar, Formula iterator, Formula out, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return true;
    }

    @Override
    public boolean handleFindUniversalQOverSet(Formula setvar, Formula iterator, Formula out, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return true;
    }

}
