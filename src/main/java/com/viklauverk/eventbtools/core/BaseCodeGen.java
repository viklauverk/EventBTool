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

import java.io.File;
import java.io.FileWriter;
import java.util.function.Function;

import static com.viklauverk.eventbtools.core.Node.*;

public abstract class BaseCodeGen implements CommonCodeGenFunctions
{
    private static Log log = LogModule.lookup("codegen");

    private CommonSettings common_settings_;
    private CodeGenSettings codegen_settings_;
    private Sys sys_;
    private Canvas canvas_;
    private Machine mch_;
    private FileWriter writer_;
    private Pattern pattern_;

    public BaseCodeGen(CommonSettings common_settings,
                       CodeGenSettings codegen_settings, Sys sys, Machine mch)
    {
        common_settings_ = common_settings;
        codegen_settings_ = codegen_settings;
        sys_ = sys;
        canvas_ = new Canvas();
        canvas_.setRenderTarget(RenderTarget.PLAIN);
        mch_ = mch;
        pattern_ = new Pattern();
    }

    public abstract void run() throws Exception;

    public void printAssumptions()
    {
        log.info("Assumptions:");
        log.info("ℕ   int64_t and >= 0 guard");
        log.info("ℕ1  int64_t and > 0 guard");
        log.info("ℤ   int64_t");
    }

    protected Canvas cnvs()
    {
        return canvas_;
    }

    protected void pl(String s)
    {
        cnvs().append(s);
        cnvs().nl();
    }

    protected void p(String s)
    {
        cnvs().append(s);
    }

    protected CommonSettings commonSettings()
    {
        return common_settings_;
    }

    protected String nickName()
    {
        return common_settings_.nickName();
    }

    protected CodeGenSettings codeGenSettings()
    {
        return codegen_settings_;
    }

    protected Sys sys()
    {
        return sys_;
    }

    protected Machine mch()
    {
        return mch_;
    }

    public void open(String file)
    {
        try
        {
            if (writer_ != null) writer_.close();
            File f = new File(commonSettings().outputDir(), file);
            writer_ = new FileWriter(f);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void writeToFile(String s)
    {
        try
        {
            writer_.write(s);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void close()
    {
        try
        {
            writer_.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public Pattern pattern()
    {
        return pattern_;
    }


    public boolean tryIfSetClear(Formula f, SymbolTable symbols, Canvas cnvs)
    {
        boolean ok = pattern().match(f, "", "x ≔ ∅");

        if (!ok) return false;

        Formula var = pattern().getVar("x");
        Variable v = symbols.getVariable(var.symbol());

        assert (v.implType() != null) : "Cannot generate code for set clear if type is unknown!";

        Formula type = v.implType().formula();

        if (type.is(POWER_SET) || type.is(PARTIAL_FUNCTION))
        {
            handleSetClear(var, symbols, cnvs, f);
            return true;
        }
        return false;
    }

    public boolean handleSetClear(Formula v, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("NOT IMPLEMENTED set clear: "+origin);
        return false;
    }

    public boolean tryIfAddToSet(Formula f, SymbolTable symbols, Canvas cnvs)
    {
        boolean ok = pattern().match(f,
                                     "lefte",  "x ≔ x ∪ {E}",
                                     "righte", "x ≔ {E} ∪ x",
                                     "leftc",  "x ≔ x ∪ {c}",
                                     "rightc", "x ≔ {c} ∪ x");

        if (!ok) return false;

        Formula setvar = pattern().getVar("x");
        Formula value = null;
        String rule = pattern().matchedRule();

        switch (rule)
        {
        case "lefte" :
        case "righte":
            value = pattern().getExpr("E");
            break;
        case "leftc":
        case "rightc":
            value = pattern().getConst("c");
            break;
        }
        handleAddToSet(setvar, value, symbols, cnvs, f);
        return true;
    }

    public boolean handleAddToSet(Formula setvar, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("NOT IMPLEMENTED add to set: "+origin);
        return false;
    }

    public boolean tryIfSubtractFromSet(Formula f, SymbolTable symbols, Canvas cnvs)
    {
        boolean ok = pattern().match(f,
                                     "lefte",  "x ≔ x \\ {E}");

        if (!ok) return false;

        Formula setvar = pattern().getVar("x");
        Formula value = null;
        String rule = pattern().matchedRule();

        switch (rule)
        {
        case "lefte" :
            value = pattern().getExpr("E");
            break;
        }

        handleSubtractFromSet(setvar, value, symbols, cnvs, f);
        return true;
    }

    public boolean handleSubtractFromSet(Formula setvar, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("NOT IMPLEMENTED subtract from set: "+origin);
        return false;
    }

    public boolean tryIfVectorAssign(Formula f, SymbolTable symbols, Canvas cnvs)
    {
        boolean ok = pattern().match(f,
                                     "number", "x ≔ 1..N ** {M}",
                                     "var",    "x ≔ 1..y ** {M}");

        if (!ok) return false;

        Formula var = pattern().getVar("x");
        Variable v = symbols.getVariable(var.symbol());

        Formula val = pattern().getNumber("M");

        assert (v.implType() != null) : "Cannot generate code for vector clear if type is unknown!";

        Formula type = v.implType().formula();

        if (type.is(TOTAL_FUNCTION))
        {
            switch (pattern().matchedRule())
            {
                case "number":
                {
                    Formula size = pattern().getNumber("N");
                    handleVectorAssign(var, size, val, symbols, cnvs, f);
                    break;
                }
                case "var":
                {
                    Formula size = pattern().getVar("y");
                    handleVectorAssign(var, size, val, symbols, cnvs, f);
                    break;
                }
            }
            return true;
        }
        return false;
    }

    public boolean handleVectorAssign(Formula v, Formula size, Formula val, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("NOT IMPLEMENTED vector assign: "+origin);
        return false;
    }

    public boolean tryIfMapOverride(Formula f, SymbolTable symbols, Canvas cnvs)
    {
        boolean ok = pattern().match(f,
                                     "", "x(E) ≔ F");

        if (!ok) return false;

        Formula set = pattern().getVar("x");
        Formula index = pattern().getExpr("E");
        Formula value = pattern().getExpr("F");

        handleMapOverride(set, index, value, symbols, cnvs, f);

        return true;
    }

    public boolean handleMapOverride(Formula set, Formula index, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("NOT IMPLEMENTED map overridet: "+origin);
        return false;
    }

    public boolean tryIfMapDomainSubtraction(Formula f, SymbolTable symbols, Canvas cnvs)
    {
        boolean ok = pattern().match(f,
                                     "", "x≔{y}⩤x");

        if (!ok) return false;

        Formula set = pattern().getVar("x");
        Formula key = pattern().getVar("y");

        handleMapDomainSubtraction(set, key, symbols, cnvs, f);

        return true;
    }

    public boolean handleMapDomainSubtraction(Formula set, Formula key, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("NOT IMPLEMENTED map domain subtraction: "+origin);
        return false;
    }

    public boolean tryIfMembershipInVariable(Formula f, SymbolTable symbols, Canvas cnvs)
    {
        boolean ok = pattern().match(f,
                                     "in",    "x : y",
                                     "notin", "x /: y");
        if (!ok) return false;

        Formula member = pattern().getVar("x");
        Formula setvar = pattern().getVar("y");

        Variable set = symbols.getVariable(setvar.symbol());

        if (pattern().matchedRule().equals("in"))
        {
            handleMembershipInVariable(setvar,  member, symbols, cnvs, f);
        }
        else
        {
            handleMembershipNotInVariable(setvar,  member, symbols, cnvs, f);
        }

        return true;
    }

    public boolean handleMembershipInVariable(Formula setvar, Formula member, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("NOT IMPLEMENTED membership in variable: "+origin);
        return false;
    }

    public boolean handleMembershipNotInVariable(Formula setvar, Formula member, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("NOT IMPLEMENTED membership not in variable: "+origin);
        return false;
    }

    public boolean tryIfMembershipInNumberRange(Formula f, SymbolTable symbols, Canvas cnvs)
    {
        boolean ok = pattern().match(f,
                                     "in", "x:N..M");

        if (ok)
        {
            Formula x = pattern().getVar("x");
            Formula N = pattern().getNumber("N");
            Formula M = pattern().getNumber("M");

            handleNumberRangeTest(x, N, M, symbols, cnvs, f);
            return true;
        }
        return false;
    }

    public boolean handleNumberRangeTest(Formula var, Formula from, Formula to, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("NOT IMPLEMENTED number range test: "+origin);
        return false;
    }

    public boolean tryIfMembershipInSimpleSet(Formula f, SymbolTable symbols, Canvas cnvs)
    {
        boolean ok = pattern().match(f,
                                     "in", "x:NAT",
                                     "in", "x:BOOL");

        if (ok)
        {
            // These type checks are handled in C++ by typing of the variable.
            // Therefore a membership test must automatically true.
            handleAlwaysTrue(symbols, cnvs, f);
            return true;
        }

        ok = pattern().match(f,
                             "", "x:(N..M)-->S",
                             "", "x:(N..y)-->S");

        if (ok) return false; // Oups not a simple set.

        ok = pattern().match(f,
                             "in", "x:S");

        if (ok)
        {
            Formula x = pattern().getVar("x");
            Formula S = pattern().getSet("S");
            Variable v = symbols.getVariable(x);
            if (v == null)
            {
                System.err.println("FORMULA "+f);
                System.err.println("No such variable "+x+" found in symbol table");
                System.err.println(symbols.print());
            }
            String vtype = v.implType().formula().toString();
            String stype = S.toString();
            if (vtype.equals(stype))
            {
                // User specified SETs are still  handled in C++ by typing of the variable.
                // So again, a membership test must automatically true.
                handleAlwaysTrue(symbols, cnvs, f);
                return true;
            }
        }
        return false;
    }

    public boolean handleAlwaysTrue(SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("NOT IMPLEMENTED always true: "+origin);
        return false;
    }

    public boolean handleAlwaysFalse(SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("NOT IMPLEMENTED always false: "+origin);
        return false;
    }

    public boolean tryIfPairMembershipInFunctionSet(Formula f, SymbolTable symbols, Canvas cnvs)
    {
        boolean ok = pattern().match(f,
                                     "in",    "x|->y : z",
                                     "notin", "x|->y /: z");
        if (!ok) return false;

        Formula key = pattern().getVar("x");
        Formula value = pattern().getVar("y");
        Formula setvar = pattern().getVar("z");

        if (pattern().matchedRule().equals("in"))
        {
            handlePairMembershipInFunctionSetvar(setvar, key, value, symbols, cnvs, f);
        }
        else
        {
            handlePairMembershipNotInFunctionSetvar(setvar, key, value, symbols, cnvs, f);
        }
        return true;
    }

    public boolean handlePairMembershipInFunctionSetvar(Formula setvar, Formula key, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("NOT IMPLEMENTED pair membership in function setvar: "+origin);
        return false;
    }

    public boolean handlePairMembershipNotInFunctionSetvar(Formula setvar, Formula key, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("NOT IMPLEMENTED pair membership NOT in function setvar: "+origin);
        return false;
    }

    public boolean tryIfMembershipInDomainOfFunction(Formula f, SymbolTable symbols, Canvas cnvs)
    {
        boolean ok = pattern().match(f,
                                     "in",    "x : dom(y)",
                                     "notin", "x /: dom(y)",
                                     "cin",   "c : dom(y)",
                                     "cnotin",   "c /: dom(y)",
                                     "Ein",   "E : dom(y)",
                                     "Enotin", "E /: dom(y)");

        if (!ok) return false;

        Formula setvar = pattern().getVar("y");
        Variable func = symbols.getVariable(setvar);
        if (func.implType().isVector())
        {
            System.err.println("GURURUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU "+setvar);
        }
        String rule = pattern().matchedRule();
        switch (rule)
        {
        case "in":
        {
            Formula key = pattern().getVar("x");
            handleMembershipInDomainOfFunctionSetvar(setvar, key, symbols, cnvs, f);
            break;
        }
        case "notin":
        {
            Formula key = pattern().getVar("x");
            handleMembershipNotInDomainOfFunctionSetvar(setvar, key, symbols, cnvs, f);
            break;
        }
        case "cin":
        {
            Formula cons = pattern().getConst("c");
            handleMembershipInDomainOfFunctionSetvar(setvar, cons, symbols, cnvs, f);
            break;
        }
        case "cnotin":
        {
            Formula cons = pattern().getConst("c");
            handleMembershipInDomainOfFunctionSetvar(setvar, cons, symbols, cnvs, f);
            break;
        }
        case "Ein":
        {
            Formula expr = pattern().getExpr("E");
            handleMembershipInDomainOfFunctionSetvar(setvar, expr, symbols, cnvs, f);
            break;
        }
        case "Enotin":
        {
            Formula expr = pattern().getExpr("E");
            handleMembershipNotInDomainOfFunctionSetvar(setvar, expr, symbols, cnvs, f);
            break;
        }
        }
        return true;
    }

    public boolean handleMembershipInDomainOfFunctionSetvar(Formula setvar, Formula key, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("NOT IMPLEMENTED membership in domain of function setvar: "+origin);
        return false;
    }

    public boolean handleMembershipNotInDomainOfFunctionSetvar(Formula setvar, Formula key, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("NOT IMPLEMENTED membership NOT in domain of function setvar: "+origin);
        return false;
    }

    public boolean tryIfMembershipInVector(Formula f, SymbolTable symbols, Canvas cnvs)
    {
        boolean ok = pattern().match(f,
                                     "inn",    "x : 1..N --> S",
                                     "notinn", "x /: 1..N --> S",
                                     "inc",    "x : 1..c --> S",
                                     "notinc", "x /: 1..c --> S",
                                     "inv",    "x : 1..y --> S",
                                     "notinv", "x /: 1..y --> S");
        if (!ok) return false;

        Formula key = pattern().getVar("x");
        Formula set = pattern().getSet("S");

        switch (pattern().matchedRule())
        {
        case "inn" :
        {
            Formula N = pattern().getNumber("N");
            handleMembershipInFixedVector(set, key, N, symbols, cnvs, f, true);
            break;
        }
        case "notinn" :
        {
            Formula N = pattern().getNumber("N");
            handleMembershipInFixedVector(set, key, N, symbols, cnvs, f, false);
            break;
        }
        }
        return true;
    }

    public boolean handleMembershipInFixedVector(Formula set, Formula key, Formula size, SymbolTable symbols,
                                                 Canvas cnvs, Formula origin, boolean yesno)
    {
        cnvs.append("NOT IMPLEMENTED "+yesno+" membership in function setvar: "+origin);
        return false;
    }

    public boolean tryIfFunctionalApplication(Formula f, SymbolTable symbols, Canvas cnvs)
    {
        boolean ok = pattern().match(f,
                                     "a", "x(E)",
                                     "b", "x~(E)",
                                     "c", "c(E)");


        if (!ok) return false;

        String rule = pattern().matchedRule();

        switch (rule)
        {
        case "a":
        {
            Formula set = pattern().getVar("x");
            Formula index = pattern().getExpr("E");
            boolean ook = doHandle( cg -> cg.handleFunctionalApplication(set, index, symbols, cnvs, f) );
            if (ook) return true;
            handleFunctionalApplication(set, index, symbols, cnvs, f);
            break;
        }
        case "b":
        {
            Formula set = pattern().getVar("x");
            Formula index = pattern().getExpr("E");
            handleFunctionalInvApplication(set, index, symbols, cnvs, f);
            break;
        }
        case "c":
        {
            Formula set = pattern().getConst("c");
            Formula index = pattern().getExpr("E");
            boolean ook = doHandle( cg -> cg.handleFunctionalApplication(set, index, symbols, cnvs, f) );
            if (ook) return true;
            handleFunctionalApplication(set, index, symbols, cnvs, f);
            break;
        }
        }
        return true;
    }

    public boolean handleFunctionalApplication(Formula set, Formula index, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("NOT IMPLEMENTED functional application: "+origin);
        return false;
    }

    public boolean handleFunctionalInvApplication(Formula set, Formula index, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        cnvs.append("NOT IMPLEMENTED functional inv application: "+origin);
        return false;
    }

    public boolean doHandle(Function<CommonCodeGenFunctions,Boolean> cb)
    {
        for (String el : mch().contextNames())
        {
            Context ctx = mch().getContext(el);
            if (ctx.isEDKContext())
            {
                boolean ok = cb.apply(ctx.edkContext());
                if (ok) return true;
            }
        }
        return false;
    }

    public void writeEDKContextsImplementationDeclarations(Machine m) throws Exception
    {
        for (String el : m.contextNames())
        {
            Context ctx = m.getContext(el);
            if (ctx.isEDKContext())
            {
                ctx.edkContext().generateImplementationDeclarations(cnvs());
            }
        }
    }

    public void writeEDKContextsImplementationDefinitions(Machine m) throws Exception
    {
        for (String el : m.contextNames())
        {
            Context ctx = m.getContext(el);
            if (ctx.isEDKContext())
            {
                ctx.edkContext().generateImplementationDefinitions(cnvs());
            }
        }
    }

    String translateEDKImplType(ImplType type, SymbolTable symbols)
    {
        for (String el : mch().contextNames())
        {
            Context ctx = mch().getContext(el);
            if (ctx.isEDKContext())
            {
                String t = ctx.edkContext().translateImplType(type, symbols, this);
                if (t != null) return t;
            }
        }
        return null;
    }

    public String translateImplType(ImplType type, SymbolTable symbols)
    {
        Formula t = type.formula();

        log.debug("translating type: %s", t);

        // Detect any EDK defined types and translate these.
        String edk_type = translateEDKImplType(type, symbols);
        if (edk_type != null) return edk_type;

        boolean ok = pattern().match(t,
                                     "int", "NAT",
                                     "int", "NAT1",
                                     "int", "INT",
                                     "int_range", "N..M",
                                     "int_range_const", "N..c",
                                     "bool", "BOOL",
                                     "var_symbol", "x",
                                     "domain_of_var", "dom(x)",
                                     "range_of_var", "ran(x)",
                                     "power_set", "POW(S)",
                                     "function", "S+->T",
                                     "function", "x+->y",
                                     "function", "x-->y",
                                     "fixed_vector",   "(1..N)-->T",
                                     "variable_vector",  "(1..x)-->T",
                                     "function", "x-->S",
                                     "function", "S-->T",
                                     "relation", "S<->T",
                                     "relation", "x<->S",
                                     "enumerated_set", "{ A }",
                                     "set_symbol", "S");

        if (!ok)
        {
            log.debug("no match for translating type %s into c++", type);
            return "Unknown";
        }

        String rule = pattern().matchedRule();

        switch (rule)
        {
        case "int":
        {
            String ts = handleTranslateInt(type, symbols);
            log.debug("int: translated type %s to %s", t, ts);
            return ts;
        }
        case "int_range":
        {
            Formula from = pattern().getNumber("N");
            Formula to = pattern().getNumber("M");
            String ts = handleTranslateIntRange(from, to, symbols);
            log.debug("int_range: translated type %s (%s .. %s) to %s", t, from, to, ts);
            return ts;
        }
        case "int_range_const":
        {
            Formula from = pattern().getNumber("N");
            Formula to = pattern().getConst("c");
            Formula def = symbols.getConstant(to).definition();
            String ts = handleTranslateIntRange(from, def, symbols); // Can handle when def is null.
            log.debug("int_range_const: translated type %s (%s .. %s) to %s", t, from, def, ts);
            return ts;
        }
        case "bool":
        {
            String ts = handleTranslateBool(type, symbols);
            log.debug("bool: translated type %s to %s", t, ts);
            return ts;
        }
        case "var_symbol":
        {
            // The type is a variable and such a variable is usually a subset of something else.
            // We are translating x:books and books:POW(NAT)
            // So x cannot be of type books, because books is a dynamic
            // subset, we have to translate the type into the underlying type NAT.
            Variable var = symbols.getVariable(t.symbol());
            log.debug("var_symbol: var type %s", var.implType());
            // Now find the inside type of the variables type.
            ImplType inner_type = sys().typing().deduceInnerImplType(var.implType());
            log.debug("var_symbol: inner type %s", inner_type);
            String translated = translateImplType(inner_type, symbols);
            log.debug("var_symbol: translated type %s to %s through %s", t, translated, var.implType());
            return translated;
        }
        case "domain_of_var":
        {
            // x is a member of the domain of a variable, therefore the variable
            // is a relation or function. Fetch the type of the variable and look left.
            Formula x = pattern().getVar("x");
            Variable var = symbols.getVariable(x);
            log.debug("domain_of_var: var type %s", var.implType());
            // Now var.implType() must be a relation/function.
            ImplType inner_type = sys().typing().lookupImplType(var.implType().formula().left());
            log.debug("domain_of_var: inner type %s", inner_type);
            String translated = translateImplType(inner_type, symbols);
            log.debug("var_symbol: translated type %s to %s through %s", t, translated, inner_type);
            return translated;
        }
        case "range_of_var":
        {
            // x is a member of the range of a variable, therefore the variable
            // is a relation or function. Fetch the type of the variable and look right.
            Formula x = pattern().getVar("x");
            Variable var = symbols.getVariable(x);
            log.debug("range_of_var: var type %s", var.implType());
            // Now var.implType() must be a relation/function.
            ImplType inner_type = sys().typing().lookupImplType(var.implType().formula().right());
            log.debug("range_of_var: inner type %s", inner_type);
            String translated = translateImplType(inner_type, symbols);
            log.debug("var_symbol: translated type %s to %s through %s", t, translated, inner_type);
            return translated;
        }
        case "power_set":
        {
            log.debug("power_set: translated power set type %s", t);
            ImplType it = sys().typing().lookupImplType(t.child());
            String inner_type = translateImplType(it, symbols);
            String ts = handleTranslateSet(it, symbols);
            log.debug("power_set: translated type %s through %s to %s", t, it, ts);
            return ts;
        }
        case "domain_of_relation":
        {
            ImplType it = sys().typing().lookupImplType(t.child());
            String inner_type = translateImplType(it, symbols);
            String ts = handleTranslateSet(it, symbols);
            log.debug("domain_of_relation: translated type %s through %s to %s", t, it, ts);
            return ts;
        }
        case "fixed_vector":
        {
            ImplType to = sys().typing().lookupImplType(t.right());
            log.debug("fixed vector: %s", to);
            String ts = handleTranslateVector(to, symbols);
            log.debug("vector: translated type %s to %s", t, ts);
            return ts;
        }
        case "variable_vector":
        {
            Formula size = t.left().right();
            Variable v = symbols.getVariable(size.symbol());
            ImplType to = sys().typing().lookupImplType(t.right());
            log.debug("variable vector: %s", to);
            String ts = handleTranslateVector(to, symbols);
            log.debug("vector: translated type %s to %s", t, ts);
            return ts;
        }
        case "function":
        {
            ImplType from = sys().typing().lookupImplType(t.left());
            ImplType to = sys().typing().lookupImplType(t.right());
            log.debug("function: from %s to %s", from, to);
            String inner_type_left = translateImplType(from, symbols);
            String inner_type_right = translateImplType(to, symbols);
            String ts = handleTranslateFunction(from, to, symbols);
            log.debug("function: translated type %s to %s", t, ts);
            return ts;
        }
        case "relation":
        {
            ImplType from = sys().typing().lookupImplType(t.left());
            ImplType to = sys().typing().lookupImplType(t.right());
            log.debug("relation: from %s to %s", from, to);
            String inner_type_left = translateImplType(from, symbols);
            String inner_type_right = translateImplType(to, symbols);
            String ts = handleTranslateRelation(from, to, symbols);
            log.debug("relation: translated type %s to %s", t, ts);
            return ts;
        }
        case "enumerated_set":
        {
            log.debug("enumerated_set: translated %s", t);
            assert (t.is(ENUMERATED_SET));

            ImplType found_type = null;
            for (int j=0; j < t.numChildren(); ++j)
            {
                Formula m = t.child(j);
                if (m.is(CONSTANT_SYMBOL))
                {
                    Constant c = symbols.getConstant(m.symbol());
                    if (found_type == null)
                    {
                        found_type = c.implType();
                    }
                    assert found_type == c.implType() : "Ouch, constants in enumerated set belongs to different carrier sets!";
                }
            }
            assert (found_type.formula().isSymbol()) : "missing type translation case handler for "+found_type;
            log.debug("enumerated_set: translated type %s to set %s", t, found_type);
            return found_type.formula().symbol();
        }
        case "set_symbol":
        {
            // This code is supposed to execute only for set symbols.
            // If it triggers for anything else, then we have a forgotten case.
            assert (t.isSymbol()) : "missing type translation case handler for "+t;
            log.debug("set_symbol: translated type %s to set %s", t, t);
            return t.symbol();
        }
        }
        assert (false): "internal error: could not translate type "+t;
        return "GURKA";
    }

    public String handleTranslateInt(ImplType t, SymbolTable symbols)
    {
        return "NOT_IMPLEMENTED_INT_TYPE";
    }

    public String handleTranslateIntRange(Formula from, Formula to, SymbolTable symbols)
    {
        return "NOT_IMPLEMENTED_INT_RANGE";
    }

    public String handleTranslateBool(ImplType t, SymbolTable symbols)
    {
        return "NOT_IMPLEMENTED_BOOL";
    }

    public String handleTranslateSet(ImplType inner_type, SymbolTable symbols)
    {
        String it = translateImplType(inner_type, symbols);
        return "NOT_IMPLEMENTED_SET<"+it+">";
    }

    public String handleTranslateVector(ImplType inner_type, SymbolTable symbols)
    {
        String it = translateImplType(inner_type, symbols);
        return "NOT_IMPLEMENTED_VECTOR<"+it+">";
    }

    public String handleTranslateFunction(ImplType from, ImplType to, SymbolTable symbols)
    {
        String inner_type_left = translateImplType(from, symbols);
        String inner_type_right = translateImplType(to, symbols);
        return "NOT_IMPLEMENTED_FUNCTION<"+inner_type_left+","+inner_type_right+">";
    }

    public String handleTranslateRelation(ImplType from, ImplType to, SymbolTable symbols)
    {
        String inner_type_left = translateImplType(from, symbols);
        String inner_type_right = translateImplType(to, symbols);
        return "NOT_IMPLEMENTED_RELATION<"+inner_type_left+","+inner_type_right+">";
    }

    public boolean tryIfUniversalQ(Formula f, SymbolTable symbols, Canvas cnvs)
    {
        return true;
    }

    public boolean tryIfExistentialQ(Formula f, SymbolTable symbols, Canvas cnvs)
    {
        /*
        boolean ok = pattern().match(f,
                                     "find",  "#x.x P & Q & y = x",
                                     "find",  "#x.x P & Q & x = y");
        if (!ok) return false;

        Formula iterator = pattern().getVar("x");
        Formula out = pattern().getVar("y");

        String rule = pattern().matchedRule();
        switch (rule)
        {
        case "find" :
            outvalue = pattern().getExpr("E");
            handlePairMembershipInFunctionSetvar(setvar, key, value, symbols, cnvs, f);

            break;
        }
        handleAddToSet(setvar, value, symbols, cnvs, f);


        if (pattern().matchedRule().equals("in"))
        {
        }
        else
        {
            handlePairMembershipNotInFunctionSetvar(setvar, key, value, symbols, cnvs, f);
            }*/
        return true;
    }

    public boolean handleFindExistentialQOverSet(Formula setvar, Formula iterator, Formula out, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return true;
    }

    public boolean handleFindUniversalQOverSet(Formula setvar, Formula iterator, Formula out, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return true;
    }
}
