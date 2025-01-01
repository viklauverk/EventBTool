/*
 Copyright (C) 2024 Viklauverk AB (agpl-3.0-or-later)

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
import com.viklauverk.evbt.core.sys.Operator;
import com.viklauverk.evbt.core.sys.PolymorphicDataType;
import com.viklauverk.evbt.core.sys.Theory;

public class RenderTheorySearch extends RenderTheory
{
    @Override
    public void visit_TheoryStart(Theory thr)
    {
        renders().search().addPart(buildTheoryPartName(thr));
    }

    @Override
    public void visit_Import(Theory thr, Theory ext) { }

    @Override
    public void visit_PolymorphicDataType(Theory thr, PolymorphicDataType pdt)
    {
        renders().search().addPart(RenderTheory.buildDataTypePartName(thr, pdt));
    }

    @Override
    public void visit_Operator(Theory thr, Operator oprt)
    {
        renders().search().addPart(RenderTheory.buildOperatorPartName(thr, oprt));
    }

    @Override
    public void visit_Axiom(Theory thr, Axiom axiom)
    {
        renders().search().addPart(RenderTheory.buildAxiomPartName(thr, axiom));
    }
}
