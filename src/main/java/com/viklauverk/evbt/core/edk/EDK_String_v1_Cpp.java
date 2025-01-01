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

package com.viklauverk.evbt.core.edk;

import com.viklauverk.evbt.core.codegen.BaseCodeGen;
import com.viklauverk.evbt.core.console.Canvas;
import com.viklauverk.evbt.core.implementation.ImplType;
import com.viklauverk.evbt.core.sys.Formula;
import com.viklauverk.evbt.core.sys.SymbolTable;

public class EDK_String_v1_Cpp extends EDKContext
{
    public EDK_String_v1_Cpp(EDK e)
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
    }

    @Override
    public void generateImplementationDefinitions(Canvas cnvs)
    {
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
            if (t.isSymbol() && t.symbol().equals("String")) return "std::string";
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
        case "concatString":
        {
            Formula x = pattern().getVar("x");
            Formula y = pattern().getVar("y");
            cnvs.append("("+x+"+"+y+")");
            return true;
        }
        case "likeString":
        {
            Formula x = pattern().getVar("x");
            Formula y = pattern().getVar("y");
            cnvs.append("likeString("+x+"+"+y+")");
            return true;
        }
        }

        return false;
    }

    public boolean handleFunctionalInvApplication(Formula setvar, Formula index, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
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
