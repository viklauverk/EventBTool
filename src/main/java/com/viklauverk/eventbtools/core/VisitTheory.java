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

public class VisitTheory
{
    /** Walk the Event-B theory thr and invoke the visitor functions in rc
        for each part of the thr. Also only visit parts that matches the pattern.
    */
    public static void walk(RenderTheory rt, Theory thr, String pattern)
    {
        boolean m = Util.match(RenderTheory.buildTheoryPartName(thr), pattern);
        if (m) rt.visit_TheoryStart(thr);

        if (m && thr.hasImports())
        {
            rt.visit_ImportsStart(thr);
            for (Theory c : thr.importsTheories())
            {
                rt.visit_Import(thr, c);
            }
            rt.visit_ImportsEnd(thr);
        }

        if (m) rt.visit_HeadingComplete(thr);

        if (thr.hasPolymorphicDataTypes())
        {
            boolean s = Util.match(RenderTheory.buildDataTypePartName(thr, null), pattern);

            if (s) rt.visit_PolymorphicDataTypesStart(thr);
            for (PolymorphicDataType pdt : thr.polymorphicDataTypeOrdering())
            {
                boolean ss = Util.match(RenderTheory.buildDataTypePartName(thr, pdt), pattern);
                if (ss)
                {
                    rt.visit_PolymorphicDataType(thr, pdt);
                }
            }
            if (s) rt.visit_PolymorphicDataTypesEnd(thr);
        }

        if (thr.hasOperators())
        {
            boolean s = Util.match(RenderTheory.buildOperatorPartName(thr, null), pattern);

            if (s) rt.visit_OperatorsStart(thr);
            for (Operator oprt : thr.operatorOrdering())
            {
                boolean ss = Util.match(RenderTheory.buildOperatorPartName(thr, oprt), pattern);
                if (ss)
                {
                    rt.visit_Operator(thr, oprt);
                }
            }
            if (s) rt.visit_OperatorsEnd(thr);
        }

        if (m) rt.visit_TheoryEnd(thr);
    }
}
