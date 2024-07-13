/*
 Copyright (C) 2021 Viklauverk AB

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
CanvasLineFormatting understands a special meta-language that instructs
this formatter to perform the necessary layout changes of the text.
The § sign is an alignment marker.
xx§y§zzzzzzz
xxxxx§yyy§z
¤flush¤

will render as:
xx    y   zzzzzzz
xxxxx yyy z

It also understands ¤hfil¤ which will expand to fill the available space.

*/

class CanvasLineFormatting
{
    static Log log = LogModule.lookup("canvaslineformatting");

    private RenderTarget render_target_;
    private AlignmentBlock current_ = null;
    private List<AlignmentBlock> blocks_ = new ArrayList<>();

    CanvasLineFormatting(RenderTarget e)
    {
        render_target_ = e;
    }

    String render(List<CanvasLine> lines)
    {
        // Create alignment block and calculate the columns widths and spans.
        createBlocks(lines);

        // Render the blocks and concatenate them.
        StringBuilder o = new StringBuilder();
        for (AlignmentBlock block : blocks_)
        {
            o.append(block.render());
        }
        return o.toString();
    }

    protected void createBlocks(List<CanvasLine> lines)
    {
        int bn = 0;
        current_ = new AlignmentBlock(render_target_);
        blocks_.add(current_);

        for (CanvasLine cl : lines)
        {
            if (cl.line.length() > 0 && cl.line.indexOf("¤flush¤") != -1)
            {
                if (current_.hasLines())
                {
                    current_.finalizeAlignments();
                    current_ = new AlignmentBlock(render_target_);
                    blocks_.add(current_);
                }
            }
            else
            if (cl.line.length() > 0 && cl.line.indexOf("¤align ") != -1)
            {
                if (current_.hasLines())
                {
                    current_.finalizeAlignments();
                    current_ = new AlignmentBlock(render_target_);
                    blocks_.add(current_);
                }
                String aligns = cl.line.substring(7, cl.line.length()-1);
                current_.setAligns(aligns);
            }
            else
            {
                current_.addLine(cl.line.toString());
            }
        }
        current_.finalizeAlignments();
    }
}
