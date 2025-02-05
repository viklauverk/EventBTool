/*
 Copyright (C) 2021-2024 Viklauverk AB (agpl-3.0-or-later)

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

package com.viklauverk.evbt.core.docgen;

import com.viklauverk.evbt.core.sys.Axiom;
import com.viklauverk.evbt.core.sys.Constructor;
import com.viklauverk.evbt.core.sys.Operator;
import com.viklauverk.evbt.core.sys.PolymorphicDataType;
import com.viklauverk.evbt.core.sys.Theory;

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

    public static String buildTheoryPartName(Theory thr)
    {
        return "thr/"+thr.name();
    }

    public static String buildDataTypePartName(Theory thr, PolymorphicDataType pdt)
    {
        return "thr/"+thr.name()+"/datatype/"+(pdt!=null?pdt.longName():"");
    }

    public static String buildDataTypePartName(PolymorphicDataType pdt)
    {
        return "thr/"+pdt.theory().name()+"/datatype/"+pdt.baseName();
    }

    public static String buildConstructorPartName(PolymorphicDataType pdt, Constructor cnstr)
    {
        return "thr/"+pdt.theory().name()+"/datatype/"+pdt.baseName()+"/constructor/"+(cnstr!=null?cnstr.name():"");
    }

    public static String buildOperatorPartName(Theory thr, Operator oprt)
    {
        return "thr/"+thr.name()+"/operator/"+(oprt!=null?oprt.name():"");
    }

    public static String buildAxiomPartName(Theory thr, Axiom axm)
    {
        return "thr/"+thr.name()+"/axiom/"+(axm!=null?axm.name():"");
    }

}
