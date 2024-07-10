/*
 Copyright (C) 2021-2024 Viklauverk AB

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
import java.util.Map;
import java.util.HashMap;

import com.viklauverk.eventbtools.core.Formula;

public class RenderTheory extends CommonRenderFunctions
{
    public void visit_TheoryStart(Theory thr) { }

    public void visit_ImportsStart(Theory thr) { }
    public void visit_Import(Theory thr, Theory ext) { }
    public void visit_ImportsEnd(Theory thr) { }

    public void visit_HeadingComplete(Theory thr) { }

    public void visit_PolymorphicDataTypesStart(Theory thr) { }
    public void visit_PolymorphicDataType(Theory thr, PolymorphicDataType pdt) { }
    public void visit_PolymorphicDataTypesEnd(Theory thr) { }

    public void visit_OperatorsStart(Theory thr) { }
    public void visit_Operator(Theory thr, Operator oprt) { }
    public void visit_OperatorsEnd(Theory thr) { }

    public void visit_AxiomsStart(Theory thr) { }
    public void visit_Axiom(Theory thr, Axiom axiom) { }
    public void visit_AxiomsEnd(Theory thr) { }

    public void visit_TheoryEnd(Theory thr) { }

    protected static String buildTheoryPartName(Theory thr)
    {
        return "thr/"+thr.name();
    }

    protected static String buildDataTypePartName(Theory thr, PolymorphicDataType pdt)
    {
        return "thr/"+thr.name()+"/datatype/"+(pdt!=null?pdt.longName():"");
    }

    protected static String buildOperatorPartName(Theory thr, Operator oprt)
    {
        return "thr/"+thr.name()+"/operator/"+(oprt!=null?oprt.name():"");
    }

    protected static String buildAxiomPartName(Theory thr, Axiom axm)
    {
        return "thr/"+thr.name()+"/axiom/"+(axm!=null?axm.name():"");
    }

}
