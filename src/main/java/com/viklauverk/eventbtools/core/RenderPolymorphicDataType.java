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

public class RenderPolymorphicDataType extends CommonRenderFunctions
{
    public void visit_PolymorphicDataTypeStart(PolymorphicDataType pdt) { }

    public void visit_HeadingComplete(PolymorphicDataType pdt) { }

    public void visit_ConstructorsStart(PolymorphicDataType pdt) { }
    public void visit_Constructor(PolymorphicDataType pdt, Constructor cnstr) { }
    public void visit_ConstructorsEnd(PolymorphicDataType pdt) { }

    public void visit_PolymorphicDataTypeEnd(PolymorphicDataType pdt) { }

    protected String buildPolymorphicDataTypePartName(PolymorphicDataType pdt)
    {
        return pdt.theory().name()+"/"+pdt.baseName();
    }

    protected String buildConstructorsPartName(PolymorphicDataType pdt)
    {
        return pdt.theory().name()+"/"+pdt.baseName()+"/constructors";
    }

    protected String buildConstructorPartName(PolymorphicDataType pdt, Constructor cnstr)
    {
        return pdt.theory().name()+"/"+pdt.baseName()+"/constructor/"+cnstr.name();
    }
}
