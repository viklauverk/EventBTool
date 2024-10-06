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

public class EDK_FloatingPoint_v1_Cpp extends EDKContext
{
    private static Log log = LogModule.lookup("edk");

    public EDK_FloatingPoint_v1_Cpp(EDK e)
    {
        super(e);
    }

    @Override
    public void generateDeclarations(Canvas cnvs)
    {
    }

    @Override
    public void generateImplementationDeclarations(Canvas cnvs)
    {
        cnvs.append("\n");
        cnvs.append("float makeFloat(int s, int e);\n");
        cnvs.append("#define EMPTYS \"\"\n");
        cnvs.append("#define ZEROF ((float)0)\n");

        cnvs.append("\n");
    }

    @Override
    public void generateImplementationDefinitions(Canvas cnvs)
    {
        cnvs.append("\n");
        cnvs.append("\n");
        cnvs.append("float makeFloat(int s, int e)\n");
        cnvs.append("{\n");
        cnvs.append("   return ((float)s) * pow((float)10.0, (float)e);\n");
        cnvs.append("}\n");
        cnvs.append("\n");
    }

    public String translateImplType(ImplType type, SymbolTable symbols, BaseCodeGen bcg)
    {
        Formula t = type.formula();

        boolean ok = bcg.pattern().match(t,
                                     "set_symbol", "S");

        if (!ok)
        {
            return null;
        }

        String rule = bcg.pattern().matchedRule();

        switch (rule)
        {
        case "set_symbol":
        {
            if (t.isSymbol() && t.symbol().equals("Float")) return "float";
            return null;
        }
        }
        return null;
    }

    public boolean handleSetClear(Formula setvar, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handleAddToSet(Formula setvar, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handleMapOverride(Formula setvar, Formula index, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handleMapDomainSubtraction(Formula setvar, Formula key, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handleMembershipInVariable(Formula setvar, Formula member, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handleMembershipNotInVariable(Formula setvar, Formula member, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handleAlwaysTrue(SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handleAlwaysFalse(SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handlePairMembershipInFunctionSetvar(Formula setvar, Formula key, Formula value,
                                                 SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handlePairMembershipNotInFunctionSetvar(Formula setvar, Formula key, Formula value,
                                                    SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handleMembershipInFixedVector(Formula set, Formula key, Formula size,
                                                 SymbolTable symbols, Canvas cnvs, Formula origin, boolean yesno)
    {
        return false;
    }

    public boolean handleFunctionalApplication(Formula setvar, Formula index, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        if (!setvar.isSymbol()) return false;
        String name = setvar.symbol();

        boolean ok = pattern().match(index,
                                     "a", "x|->y");

        if (!ok) return false;

        switch (name)
        {
        case "makeFloat":
        {
            Formula x = pattern().getVar("x");
            Formula y = pattern().getVar("y");
            cnvs.append("makeFloat("+x+","+y+")");
            return true;
        }
        case "addFloat":
        {
            Formula x = pattern().getVar("x");
            Formula y = pattern().getVar("y");
            cnvs.append("("+x+"+"+y+")");
            return true;
        }
        case "subFloat":
        {
            Formula x = pattern().getVar("x");
            Formula y = pattern().getVar("y");
            cnvs.append("("+x+"-"+y+")");
            return true;
        }
        case "divFloat":
        {
            Formula x = pattern().getVar("x");
            Formula y = pattern().getVar("y");
            cnvs.append("("+x+"/"+y+")");
            return true;
        }
        }

        return false;
    }

    public boolean handleFunctionalInvApplication(Formula setvar, Formula index, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

}
