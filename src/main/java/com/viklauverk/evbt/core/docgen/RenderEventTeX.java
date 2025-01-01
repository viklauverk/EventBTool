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

package com.viklauverk.evbt.core.docgen;

import com.viklauverk.evbt.core.helpers.Util;
import com.viklauverk.evbt.core.sys.Event;

public class RenderEventTeX extends RenderEventUnicode
{
    @Override
    public void visit_EventStart(Event eve)
    {
        if (!eve.name().equals("INITIALISATION"))
        {
            // Do not list the init event, since it always exists.
            cnvs().append("\\subsection{\\footnotesize ");
            cnvs().id(eve.name());
            if (eve.hasParameters())
            {
                cnvs().append("(");
                boolean space = false;
                for (String p : eve.parameterNames())
                {
                    if (space) cnvs().append(" ");
                    else space = true;
                    cnvs().append("\\VARDEF{"+Util.texSafe(p)+"}");
                }
                cnvs().append(")");
            }
            if (eve.hasRefines())
            {
                if (eve.extended())
                {
                    cnvs().append("\\ \\KEYW{extends} ");
                }
                else
                {
                    cnvs().append("\\ \\KEYW{refines} ");
                }

                for (String r : eve.refinesEventNames())
                {
                    cnvs().append(Util.texSafe(r)+" ");
                }
            }
            cnvs().append("}\n");
        }
        else
        {
            cnvs().append("\\HRULE\\par\n");
        }
        super.visit_EventStart(eve);
    }

    @Override
    public void visit_EventEnd(Event eve)
    {
        super.visit_EventEnd(eve);
    }
}
