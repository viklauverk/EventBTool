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

public class RenderOperatorUnicode extends RenderOperator
{
    @Override
    public void visit_OperatorStart(Operator oprt)
    {
        String id = buildOperatorPartName(oprt);

        cnvs().marker(id);

        cnvs().startLine();
        if (oprt.notation() == OperatorNotationType.PREFIX)
        {
            cnvs().keywordLeft("prefix ");
        }
        if (oprt.notation() == OperatorNotationType.INFIX)
        {
            cnvs().keywordLeft("infix ");
        }
        cnvs().keywordLeft("operator ");
        cnvs().id(oprt.name());
        cnvs().endLine();

        if (oprt.hasComment())
        {
            cnvs().acomment(oprt.comment());
        }
    }

    @Override
    public void visit_HeadingComplete(Operator oprt)
    {
    }

    @Override
    public void visit_OperatorEnd(Operator oprt)
    {
        cnvs().startLine();
        cnvs().keyword("end");
        cnvs().endLine();
    }
}
