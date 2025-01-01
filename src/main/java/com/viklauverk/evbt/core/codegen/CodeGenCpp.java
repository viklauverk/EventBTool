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

package com.viklauverk.evbt.core.codegen;

import com.viklauverk.evbt.core.CommonSettings;
import com.viklauverk.evbt.core.Unicode;
import com.viklauverk.evbt.core.console.Canvas;
import com.viklauverk.evbt.core.implementation.ImplType;
import com.viklauverk.evbt.core.implementation.PlanImplementation;
import com.viklauverk.evbt.core.log.Log;
import com.viklauverk.evbt.core.log.LogModule;
import com.viklauverk.evbt.core.sys.Action;
import com.viklauverk.evbt.core.sys.CarrierSet;
import com.viklauverk.evbt.core.sys.Constant;
import com.viklauverk.evbt.core.sys.Context;
import com.viklauverk.evbt.core.sys.Event;
import com.viklauverk.evbt.core.sys.Formula;
import com.viklauverk.evbt.core.sys.Guard;
import com.viklauverk.evbt.core.sys.Machine;
import com.viklauverk.evbt.core.sys.SymbolTable;
import com.viklauverk.evbt.core.sys.Sys;
import com.viklauverk.evbt.core.sys.Variable;
import com.viklauverk.evbt.core.visitors.VisitFormula;

/**
  NAT        int
  POW(NAT)   std::set<int>
  NAT+->NAT  std::map<int,int>
  String     std::string
  FloatingPoint double
  (1..32)-->NAT std::vector<int>
  size:NAT1
  (1..size)-->NAT std::vector<int>
*/
public class CodeGenCpp extends BaseCodeGen
{
    public static Log log = LogModule.lookup("codegen", CodeGenCpp.class);

    public CodeGenCpp(CommonSettings coms, CodeGenSettings cdgs, Sys sys, Machine mch)
    {
        super(coms, cdgs, sys, mch);
    }

    public void run() throws Exception
    {
        writeHeaderFile();
        writeCCFile();
    }

    public void writeContext(Context ctx)
    {
        if (ctx.isEDKContext())
        {
            ctx.edkContext().generateDeclarations(cnvs());
            return;
        }
        if (ctx.hasExtend())
        {
            for (Context c : ctx.extendsContexts())
            {
                writeContext(c);
            }
        }
        log.debug("Writing context "+ctx);
        for (String csn : ctx.setNames())
        {
            CarrierSet cs = ctx.getSet(csn);
            pl(Unicode.commentToCpp(cs.comment()));
            pl("enum "+cs.name());
            pl("{");
            StringBuilder sb = new StringBuilder();
            for (String mem : cs.memberNames())
            {
                if (sb.length() > 0) sb.append(",");
                sb.append(mem);
            }
            pl(sb.toString());
            pl("};");
        }

        for (String con : ctx.constantNames())
        {
            Constant co = ctx.getConstant(con);
            if (co.hasDefinition())
            {
                pl("#define "+co.name()+" "+co.definition());
            }
        }
    }

    public void writeContexts(Machine m) throws Exception
    {
        for (String el : m.contextNames())
        {
            Context ctx = m.getContext(el);
            writeContext(ctx);
        }
    }

    void writeEventDeclaration(Event e, boolean add_virtual, boolean has_parameters)
    {
        if (e.hasParameters() != has_parameters) return;

        log.debug("writing event declaration for: "+e);
        if (e.hasComment())
        {
            pl("    "+Unicode.commentToCpp(e.comment()));
        }
        StringBuffer sb = new StringBuffer();
        sb.append("    ");
        if (add_virtual) sb.append("virtual ");
        sb.append("bool "+e.name()+"(");
        int i = 0;
        for (Variable p : e.parameterOrdering())
        {
            if (!p.isOutParameter() && p.hasDefinition())
            {
                // This variable has a local definition within the guards,
                // thus it is not required as an argument to the function.
                continue;
            }
            if (i > 0) sb.append(",");
            assert (p.implType() != null) : "internal error: unknown type for "+p+" within "+e.symbolTable().tree();
            if (p.isOutParameter())
            {
                String translated_type = translateImplType(p.implType(), e.symbolTable());
                log.debug("translated type for *"+p.name()+" is "+translated_type);
                sb.append(translated_type+" *"+p.name()); // Notice the pointer asterisk!
            }
            else
            {
                String translated_type = translateImplType(p.implType(), e.symbolTable());
                log.debug("translated type for "+p.name()+" is "+translated_type);
                sb.append(translated_type+" "+p.name());
            }
            i++;
        }
        sb.append(")");
        if (add_virtual) sb.append(" = 0");
        sb.append(";");
        pl(sb.toString());
    }

    void  writeVirtualDeclarationsForConcretEventsWithParameters()
    {
        for (String el : mch().concreteEventNames())
        {
            Event e = mch().getConcreteEvent(el);
            writeEventDeclaration(e, true, true);
        }
    }

    void  writeNonVirtualDeclarationsForConcretEventsWithParameters()
    {
        for (String el : mch().concreteEventNames())
        {
            Event e = mch().getConcreteEvent(el);
            writeEventDeclaration(e, false, true);
        }
    }

    void  writeNonVirtualDeclarationsForConcretEventsWithoutParameters()
    {
        for (String el : mch().concreteEventNames())
        {
            Event e = mch().getConcreteEvent(el);
            if (e.isInit()) continue; // Do not list the INITIALISATION
            writeEventDeclaration(e, false, false);
        }
    }

    public void writeHeaderFile() throws Exception
    {
        String file = commonSettings().nickName()+".h";
        log.info("Writing "+file);
        open(file);
        pl("/*");
        pl("");
        pl(" Event B state machine generated from "+commonSettings().nickName());
        pl(" "+mch().comment());
        pl("");
        pl("*/");
        pl("#include<cmath>");
        pl("#include<functional>");
        pl("#include<map>");
        pl("#include<memory>");
        pl("#include<set>");
        pl("#include<string>");
        pl("#include<vector>");
        writeContexts(mch());
        pl("");
        pl("class "+commonSettings().nickName());
        pl("{");
        pl("    public:");
        pl("    virtual int run() = 0;");
        pl("    virtual void trace(std::function<void(const char *)> cb) = 0;");

        writeVirtualDeclarationsForConcretEventsWithParameters();

        pl("};");
        pl("");
        pl("std::unique_ptr<"+nickName()+"> create"+nickName()+"();");
        writeToFile(cnvs().render());
        cnvs().clear();
        close();
    }

    public void writeRunLoop()
    {
        pl("\n");
        pl("int "+nickName()+"Implementation::run()");
        pl("{");
        pl("    int c = 0;");
        pl("    while (true) {");

        for (String el : mch().concreteEventNames())
        {
            Event e = mch().getConcreteEvent(el);
            if (!e.isInit() && !e.hasParameters())
            {
                pl("        if ("+e.name()+"()) { c++; continue; }");
            }
        }

        pl("        // No event has triggered, the machine has stopped.");
        pl("        break;");
        pl("    }");
        pl("    return c;");
        pl("}");
    }

    public void writeGuard(Event e, String label)
    {
        PlanImplementation plan = new PlanImplementation(this);
        CodeGenFormulaCpp gen = new CodeGenFormulaCpp(this, plan);
        gen.setSymbolTable(e.symbolTable());
        Guard g = e.getGuard(label);
        if (g.hasComment())
        {
            pl("    "+Unicode.commentToCpp(g.comment()));
        }
        if (g.definesValue())
        {
            // A guard can serve as a definition and even output variable from the event.
            Variable v = e.symbolTable().getVariable(g.formula().left());
            if (v.isOutParameter())
            {
                p("*");
            }
            else
            {
                p("    "+translateImplType(v.implType(), e.symbolTable())+" ");
            }
            VisitFormula.walkk(plan, g.formula().left());
            VisitFormula.walk(gen, g.formula().left());
            p(" = ");
            VisitFormula.walkk(plan, g.formula().right());
            VisitFormula.walk(gen, g.formula().right());
            pl("; // "+g.formula());
        }
        else
        {
            // A guard is normally a guard that prevents the event from executing if
            // it does not evaluate to true.
            p("    bool "+label+" = ");
            gen.cnvs().setMark(); // For debugging formaula translation.
            VisitFormula.walkk(plan, g.formula());
            VisitFormula.walk(gen, g.formula());
            String debug = gen.cnvs().getSinceMark(); // When not debugging returns empty string.
            pl("; "+Unicode.commentToCpp(g.formula().toString()));
            pl("    if (!"+label+") return false;");
            log.debugp("guard", "translated %s into %s =%s", g.formula(), label, debug);
        }
    }

    public void writeEventDefinition(Event e)
    {
        if (!e.isInit())
        {
            p("bool "+nickName()+"Implementation::"+e.name()+"(");
        }
        else
        {
            p(nickName()+"Implementation::"+nickName()+"Implementation(");
        }
        int c = 0;
        for (Variable par : e.parameterOrdering())
        {
            if (c > 0) p(",");
            if (par.isOutParameter())
            {
                p(""+translateImplType(par.implType(), e.symbolTable())+" *"+par.name());
            }
            else
            {
                if (!par.hasDefinition())
                {
                    p(""+translateImplType(par.implType(), e.symbolTable())+" "+par.name());
                }
            }
            c++;
        }
        pl(")\n{");

        for (String label : e.guardNames())
        {
            writeGuard(e, label);
        }
        pl("");

        for (String label : e.actionNames())
        {
            Action a = e.getAction(label);
            if (a.hasComment())
            {
                pl("    "+Unicode.commentToCpp(a.comment()));
            }
            PlanImplementation plan = new PlanImplementation(this);
            CodeGenFormulaCpp gen = new CodeGenFormulaCpp(this, plan);
            gen.setSymbolTable(e.symbolTable());
            p("    ");
            VisitFormula.walkk(plan, a.formula());
            VisitFormula.walk(gen, a.formula());
            pl("; // "+a.formula());
        }

        pl("    traceEvent(\""+e.name()+"\");");
        if (!e.isInit()) pl("    return true;");
        pl("}\n\n");
    }

    public void writeCCFile() throws Exception
    {
        String file = commonSettings().nickName()+".cc";
        log.info("Writing "+file);
        open(file);
        pl("/*");
        pl("");
        pl(" Event B state machine generated from "+nickName());
        pl("");
        pl(" "+mch().comment());
        pl("*/");

        pl("#include\""+nickName()+".h\"");

        writeEDKContextsImplementationDeclarations(mch());

        pl("class "+nickName()+"Implementation : public "+nickName());
        pl("{");
        pl("    std::function<void(const char*)> trace_cb_;");
        for (Variable v : mch().variableOrdering())
        {
            if (v.implType() == null)
            {
                log.warn("variable %s has no known type within %s", v.name(), mch().symbolTable().tree());
            }
            pl("    "+translateImplType(v.implType(), mch().symbolTable())+" "+v.name()+";");
        }
        pl("    // Event functions");
        pl("    public:");
        pl("    "+nickName()+"Implementation();"); // The constructor aka INITIALISATION
        pl("    int run();");
        pl("    void trace(std::function<void(const char*)> cb) { trace_cb_ = cb; }");

        writeNonVirtualDeclarationsForConcretEventsWithParameters();

        pl("    private:");
        pl("    void traceEvent(const char *msg);");

        writeNonVirtualDeclarationsForConcretEventsWithoutParameters();

        pl("};\n\n");

        for (String el : mch().concreteEventNames())
        {
            Event e = mch().getConcreteEvent(el);
            writeEventDefinition(e);
        }

        pl("\n\nvoid "+nickName()+"Implementation::traceEvent(const char *msg)");
        pl("{");
        pl("    if (trace_cb_) trace_cb_(msg);");
        pl("}");

        writeRunLoop();

        pl("\nstd::unique_ptr<"+nickName()+"> create"+nickName()+"()");
        pl("{");
        pl("    return std::unique_ptr<"+nickName()+"Implementation>(new "+nickName()+"Implementation());");
        pl("}");

        writeEDKContextsImplementationDefinitions(mch());

        writeToFile(cnvs().render());
        cnvs().clear();
        close();
    }

    @Override
    public boolean handleSetClear(Formula setvar, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append(setvar+".clear()");
        return true;
    }

    @Override
    public boolean handleVectorAssign(Formula setvar, Formula size, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append(setvar+".assign("+size+","+value+")");
        return true;
    }

    @Override
    public boolean handleAddToSet(Formula setvar, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append(setvar+".insert("+value+")");
        return true;
    }

    @Override
    public boolean handleSubtractFromSet(Formula setvar, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append(setvar+".erase("+value+")");
        return true;
    }

    @Override
    public boolean handleMapOverride(Formula setvar, Formula index, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append(setvar+"["+index+"] = "+value);
        return true;
    }

    @Override
    public boolean handleMapDomainSubtraction(Formula setvar, Formula key, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append(setvar+".erase("+key+")");
        return true;
    }

    @Override
    public boolean handleMembershipInVariable(Formula setvar, Formula member, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append(""+setvar+".count("+member+")");
        return true;
    }

    @Override
    public boolean handleMembershipNotInVariable(Formula setvar, Formula member, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append(""+setvar+".count("+member+")==0");
        return true;
    }

    @Override
    public boolean handleNumberRangeTest(Formula var, Formula from, Formula to, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("("+var+" >= "+from+" && "+var+" <= "+to+")");
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
        cnvs.append(setvar+".count("+key+")==1 && "+setvar+"["+key+"] == "+value);
        return true;
    }

    @Override
    public boolean handlePairMembershipNotInFunctionSetvar(Formula setvar, Formula key, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append(setvar+".count("+key+")==0 || "+setvar+"["+key+"] != "+value);
        return true;
    }

    @Override
    public boolean handleMembershipInDomainOfFunctionSetvar(Formula setvar, Formula key, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append(""+setvar+".count("+key+")");
        return true;
    }

    @Override
    public boolean handleMembershipNotInDomainOfFunctionSetvar(Formula setvar, Formula key, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append(""+setvar+".count("+key+")==0");
        return true;
    }

    @Override
    public boolean handleMembershipInFixedVector(Formula set, Formula key, Formula size, SymbolTable symbols,
                                                 Canvas cnvs, Formula origin, boolean yesno)
    {
        if (yesno)
        {
            cnvs.append(""+key+".size() == "+size);
        }
        else
        {
            cnvs.append(""+key+".size() != "+size);
        }
        return true;
    }

    @Override
    public boolean handleFunctionalApplication(Formula setvar, Formula index, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append(""+setvar+"["+index+"]");
        return true;
    }

    @Override
    public boolean handleFunctionalInvApplication(Formula setvar, Formula index, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append(""+setvar+"["+index+"]");
        return true;
    }

    @Override
    public String handleTranslateInt(ImplType type, SymbolTable symbols)
    {
        return "uint64_t";
    }

    @Override
    public String handleTranslateIntRange(Formula from, Formula to, SymbolTable symbols)
    {
        return "uint64_t";
    }

    @Override
    public String handleTranslateBool(ImplType type, SymbolTable symbols)
    {
        return "bool";
    }

    @Override
    public String handleTranslateSet(ImplType inner_type, SymbolTable symbols)
    {
        String it = translateImplType(inner_type, symbols);
        return "std::set<"+it+">";
    }

    @Override
    public String handleTranslateVector(ImplType inner_type, SymbolTable symbols)
    {
        String it = translateImplType(inner_type, symbols);
        return "std::vector<"+it+">";
    }

    @Override
    public String handleTranslateFunction(ImplType from, ImplType to, SymbolTable symbols)
    {
        String inner_type_left = translateImplType(from, symbols);
        String inner_type_right = translateImplType(to, symbols);
        return "std::map<"+inner_type_left+","+inner_type_right+">";
    }

    @Override
    public String handleTranslateRelation(ImplType from, ImplType to, SymbolTable symbols)
    {
        String inner_type_left = translateImplType(from, symbols);
        String inner_type_right = translateImplType(to, symbols);
        return "std::multimap<"+inner_type_left+","+inner_type_right+">";
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
