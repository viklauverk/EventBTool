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

public class RenderPolymorphicDataTypeUnicode extends RenderPolymorphicDataType
{
    @Override
    public void visit_PolymorphicDataTypeStart(PolymorphicDataType pdt)
    {
        String id = buildPolymorphicDataTypePartName(pdt);

        cnvs().marker(id);

        if (pdt.hasComment())
        {
            cnvs().acomment(pdt.comment());
        }
        cnvs().startLine();
        cnvs().polymorphicDataType(pdt.hrLongName());
        if (pdt.hasConstructors())
        {
            cnvs().keyword(" ≜");
        }
        cnvs().endLine();
    }

    @Override
    public void visit_ConstructorsStart(PolymorphicDataType pdt)
    {
    }

    @Override
    public void visit_Constructor(PolymorphicDataType pdt, Constructor cnstr)
    {
        cnvs().startAlignedLine();
        cnvs().keywordLeft("    ► ");
        cnvs().startMath();
        cnvs().constructor(cnstr.name());
        cnvs().stopMath();
        cnvs().align();
        cnvs().comment(cnstr.comment());
        cnvs().stopAlignedLine();
    }

    @Override
    public void visit_ConstructorsEnd(PolymorphicDataType pdt)
    {
    }

    @Override
    public void visit_HeadingComplete(PolymorphicDataType pdt)
    {
    }

    @Override
    public void visit_PolymorphicDataTypeEnd(PolymorphicDataType pdt)
    {
    }
}
