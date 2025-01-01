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

import com.viklauverk.evbt.core.console.Canvas;
import com.viklauverk.evbt.core.helpers.Util;
import com.viklauverk.evbt.core.sys.IsAFormula;

public class CommonRenderFunctions
{
    protected AllRenders renders_;
    protected boolean build_template_;

    protected AllRenders renders()
    {
        return renders_;
    }

    protected Canvas cnvs()
    {
        return renders_.currentCanvas();
    }

    public void setRenders(AllRenders ar)
    {
        renders_ = ar;
    }

    protected void pushCanvass(String id)
    {
        renders().pushCanvass(id);
    }

    protected void popStoreCanvasAndAppendd(String id)
    {
        renders().popStoreCanvasAndAppendd(id);
    }

    protected void stopAlignedLineAndHandlePotentialComment(String comment, Canvas cnvs, IsAFormula f)
    {
        boolean has_comment = comment.length() > 0;
        boolean use_next_line = false;

        // If comment contains explicit line breaks, then always place
        // the comment on its own line below the commented material.
        // An explicit line break is an empty line, ie two consecutive \n,
        // like a \par in TeX.
        if (Util.hasNewLine(comment))
        {
            use_next_line = true;
        }
        int clen = comment.length();
        int flen = 0;
        if (f != null && f.formula() != null)
        {
            flen =  f.formula().toString().length();
        }
        // Check if the sum of the formula length and the comment length is greater than 50,
        // then place the comment on its own line.
        if (clen + flen > 60)
        {
            use_next_line = true;
        }
        if (has_comment && !use_next_line)
        {
            // Placing the comment on the same line.
            cnvs.align();
            cnvs.comment(comment);
        }

        cnvs.stopAlignedLine();

        if (has_comment && use_next_line)
        {
            // Place the comment on a new line.
            cnvs.startAlignedLine();
            cnvs.commentWithExtraVSpace(comment);
            cnvs.stopAlignedLine();
        }
    }
}
