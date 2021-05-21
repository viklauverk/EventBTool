/*
 Copyright (C) 2021 Viklauverk AB
 Author Fredrik Öhrström

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

    protected void stopAlignedLineAndHandlePotentialComment(String comment, Canvas cnvs)
    {
        boolean mlc = Util.hasNewLine(comment);
        boolean has = comment.length() > 0;
        if (comment.length() > 100)
        {
            // Any comment with a length longer
            // than 100 characters is put on its own line below.
            mlc = true;
        }
        if (!mlc && has)
        {
            // We have a single line comment.
            cnvs.align();
            cnvs.comment(comment);
        }

        cnvs.stopAlignedLine();

        if (mlc)
        {
            cnvs.startAlignedLine();
            cnvs.align();
            cnvs.comment(comment);
            cnvs.stopAlignedLine();
        }
    }
}
