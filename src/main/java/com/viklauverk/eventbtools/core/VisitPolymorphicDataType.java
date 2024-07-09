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

import com.viklauverk.eventbtools.core.Formula;

public class VisitPolymorphicDataType
{
    public static void walk(RenderPolymorphicDataType re, PolymorphicDataType pdt, String pattern)
    {
        String path = pdt.theory().name()+"/datatypes/"+pdt.baseName()+"/";
        boolean m = Util.match(path, pattern);

        if (m) re.visit_PolymorphicDataTypeStart(pdt);

        if (m) re.visit_HeadingComplete(pdt);

        if (pdt.hasConstructors())
        {
            if (m) re.visit_ConstructorsStart(pdt);
            for (Constructor cnstr : pdt.constructorOrdering())
            {
                boolean pp =  Util.match(path+"constructors/"+cnstr.name()+"/", pattern);
                if (pp) re.visit_Constructor(pdt, cnstr);
            }
            if (m) re.visit_ConstructorsEnd(pdt);
        }

        if (m) re.visit_PolymorphicDataTypeEnd(pdt);
    }
}
