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

import com.viklauverk.eventbtools.core.Formula;

public class GenerateFormulaBaseCodeGen extends RenderFormulaUnicode
{
    private BaseCodeGen codegen_;

    GenerateFormulaBaseCodeGen(BaseCodeGen codegen)
    {
        super(codegen.cnvs());
        codegen_ = codegen;
        limitToAscii();
    }

    protected BaseCodeGen codegen()
    {
        return codegen_;
    }

    @Override public Formula visit_BECOME_EQ(Formula f)
    {
        if (codegen().tryIfSetClear(f, symbols(), cnvs())) return f;
        if (codegen().tryIfVectorAssign(f, symbols(), cnvs())) return f;
        if (codegen().tryIfAddToSet(f, symbols(), cnvs())) return f;
        if (codegen().tryIfSubtractFromSet(f, symbols(), cnvs())) return f;
        if (codegen().tryIfMapDomainSubtraction(f, symbols(), cnvs())) return f;

        return null;
    }

    @Override public Formula visit_BECOME_EQ_FUNC_APP(Formula f)
    {
        if (codegen().tryIfMapOverride(f, symbols(), cnvs())) return f;

        return super.visit_BECOME_EQ_FUNC_APP(f);
    }

    @Override public Formula visit_MEMBERSHIP(Formula f)
    {
        if (codegen().tryIfMembershipInSimpleSet(f, symbols(), cnvs())) return f;
        if (codegen().tryIfMembershipInNumberRange(f, symbols(), cnvs())) return f;
        if (codegen().tryIfMembershipInVariable(f, symbols(), cnvs())) return f;
        if (codegen().tryIfPairMembershipInFunctionSet(f, symbols(), cnvs())) return f;
        if (codegen().tryIfMembershipInDomainOfFunction(f, symbols(), cnvs())) return f;
        if (codegen().tryIfMembershipInVector(f, symbols(), cnvs())) return f;

        return super.visit_MEMBERSHIP(f);
    }

    @Override public Formula visit_NOT_MEMBERSHIP(Formula f)
    {
        if (codegen().tryIfMembershipInSimpleSet(f, symbols(), cnvs())) return f;
        if (codegen().tryIfMembershipInVariable(f, symbols(), cnvs())) return f;
        if (codegen().tryIfPairMembershipInFunctionSet(f, symbols(), cnvs())) return f;
        if (codegen().tryIfMembershipInDomainOfFunction(f, symbols(), cnvs())) return f;

        return super.visit_NOT_MEMBERSHIP(f);
    }

    @Override public Formula visit_FUNC_APP(Formula f)
    {
        if (codegen().tryIfFunctionalApplication(f, symbols(), cnvs())) return f;

        return null;
    }

    @Override public Formula visit_UNIVERSALQ(Formula f)
    {
        if (codegen().tryIfUniversalQ(f, symbols(), cnvs())) return f;

        return null;
    }

    @Override public Formula visit_EXISTENTIALQ(Formula f)
    {
        if (codegen().tryIfExistentialQ(f, symbols(), cnvs())) return f;

        return null;
    }

}
