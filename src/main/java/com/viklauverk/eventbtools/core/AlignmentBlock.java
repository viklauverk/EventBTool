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

class AlignmentBlock
{
    static Log log = LogModule.lookup("canvas");

    private RenderTarget render_target_;

    private List<Integer> cols_ = new ArrayList<>();  // A distinct column.
    // Read: alfa § beta § gamma
    // Set:  0=5,1=5,2=6
    private List<Integer> spans_ = new ArrayList<>(); // Max span found from col to end.
    // Read: alfa § beta § gamma
    // Set:  0=0,1=0,2=6
    // Read: alfa § betagamma
    // Set:  0=0,1=10,2=6
    private List<String[]> lines_ = new ArrayList<>(); // The lines split into columns.

    // l,X,X = first column fit content, second and third column share what is left.
    private List<String> aligns_ = new ArrayList<>();

    private boolean found_alignments_ = false;

    AlignmentBlock(RenderTarget rt)
    {
        render_target_ = rt;
    }

    public boolean hasLines()
    {
        return lines_.size() > 0;
    }

    void setAligns(String a)
    {
        String[] cols = a.split(",", -1);
        for (String c : cols)
        {
            aligns_.add(c);
        }
        guaranteeNumCols(cols.length);
    }

    String render()
    {
        if (lines_.size() == 0) return "";
        if (found_alignments_ == false)
        {
            StringBuilder o = new StringBuilder();
            for (String[] cols : lines_)
            {
                dbgCnvs(o, "[ab_raw]");
                o.append(cols[0]);
                dbgCnvs(o, "[/ab_raw]");
                o.append("\n");
            }
            return o.toString();
        }

        // To work around a bug in xltabular we restart the table after 20 lines.
        StringBuilder result = new StringBuilder();
        int count = 0;
        StringBuilder o = new StringBuilder();
        for (int i = 0; i < lines_.size(); ++i)
        {
            String[] cols = lines_.get(i);
            o.append(wrapRow(applyAlignment(cols)));
            count++;
            if (count >= 20)
            {
                count = 0;
                result.append(wrapBlock(o.toString(), i+1 < lines_.size()));
                o = new StringBuilder();
            }
        }
        if (count > 0)
        {
            result.append(wrapBlock(o.toString(), false));
        }
        return result.toString();
    }

    void addLine(String l)
    {
        if (l.length() == 0) return;
        String[] cols = l.split("§", -1);
        if (cols.length > 1)
        {
            found_alignments_ = true;
        }
        guaranteeNumCols(cols.length);
        lines_.add(cols);

        for (int col = 0; col<cols.length; col++)
        {
            String s = cols[col];
            // Any ansi sequencies for color should not affect the alignment.
            s = Canvas.removeAnsi(s);
            boolean is_last_col = (col == cols.length-1);
            if (!is_last_col && s.length() > colWidth(col))
            {
                // This is a proper column §.....§ set the col width.
                setColWidth(col, s.length());
            }
            if (is_last_col && s.length() > colSpan(col))
            {
                // This is the last column in this line, set the span
                // to end width in addition to the col width.
                setColSpan(col, s.length());
            }
        }
    }

    void finalizeAlignments()
    {
        if (lines_.size() == 0) return;

        // Check if last line is empty, then remove it.
        String[] cols = lines_.get(lines_.size() - 1);
        if (cols.length == 1 && cols[0].length() == 0)
        {
            lines_.remove(lines_.size()-1);
        }
    }

    protected void guaranteeNumCols(int num_cols)
    {
        while (num_cols > cols_.size()) cols_.add(0);
        while (num_cols > spans_.size()) spans_.add(0);
    }

    protected int size()
    {
        return cols_.size();
    }

    protected int colWidth(int i)
    {
        return cols_.get(i);
    }

    protected int colSpan(int i)
    {
        return spans_.get(i);
    }

    protected void setColWidth(int i, int w)
    {
        cols_.set(i, w);
    }

    protected void setColSpan(int i, int w)
    {
        spans_.set(i, w);
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<cols_.size(); ++i)
        {
            sb.append(""+cols_.get(i)+"["+spans_.get(i)+"] ");
        }
        sb.setLength(sb.length()-1);
        return sb.toString();
    }

    protected String wrapBlock(String s, boolean add_vspace)
    {
        String prefix = "";
        String postfix = "";
        if (render_target_ == RenderTarget.TEX)
        {
            prefix = generateStartTabbingTeX(this);
            postfix = generateStopTabbingTeX();
            if (add_vspace)
            {
                postfix += generateRuleAndVSpace();
            }
        }
        if (render_target_ == RenderTarget.HTML)
        {
            prefix = generateStartTabbingHTMQ(this);
            postfix = generateStopTabbingHTMQ();
            if (add_vspace)
            {
                postfix += "";
            }
        }
        return dbgCnvsMsg("[block]")+prefix+s+postfix+dbgCnvsMsg("[/block]");
    }

    protected String wrapRow(String s)
    {
        switch (render_target_)
        {
        case PLAIN:
        case TERMINAL:
            return s+"\n";
        case TEX:
            return s+"\\\\\n";
        case HTML:
            return "<tr>"+s+"</tr>\n";
        }
        return s;
    }

    protected String padCell(String s, int w, int cw, int col, boolean is_last_cell)
    {
        int colspan = 1;
        if (is_last_cell)
        {
            // This is the last cell for this line.
            // If cols == cols.size()-1 then colspan stays at 1.
            // Otherwise it will increase to span the remaining cols.
            colspan  = cols_.size() - col;
        }

        switch (render_target_)
        {
        case PLAIN:
        case TERMINAL:
            return Util.padRight(s, ' ', cw-w)+(is_last_cell?"":" ");
        case TEX:
            if (colspan > 1)
            {
                if (colspan == 2)
                {
                    s = "\\spantwo{"+s+"}";
                }
                else if (colspan == 3)
                {
                    s = "\\spanthree{"+s+"}";
                }
                else
                {
                    assert false : "Oups! Not implemented wider spans!";
                }
            }
            return s+(is_last_cell?"":"&");
        case HTML:
            if (colspan > 1)
            {
                return "<td colspan="+colspan+">"+s+"</td>";
            }
            return "<td>"+s+"</td>";
        }
        return "ERROR "+s;
    }


    protected String applyAlignment(String[] cols)
    {
        StringBuilder o = new StringBuilder();
        for (int col = 0; col < cols.length; ++col)
        {
            String s = cols[col];
            int width = Canvas.lengthIgnoringAnsi(s);

            boolean is_last_col = (col == cols.length-1);

            int calculated_width;
            if (is_last_col)
            {
                // Last column, use the col to end span value for width.
                calculated_width = colSpan(col);
            }
            else
            {
                // Not last column, use the column width.
                calculated_width = colWidth(col);
            }
            o.append(padCell(s, width, calculated_width, col, is_last_col));
        }

        return o.toString();
    }

    protected String generateStartTabbingTeX(AlignmentBlock cols)
    {
        StringBuilder o = new StringBuilder();
        o.append("\\begin{xltabular}{\\linewidth}[l]{");
        if (false) o.append("|");
        for (int i=0; i<aligns_.size(); ++i)
        {
            if (aligns_.get(i) != null)
            {
                String s = aligns_.get(i);
                o.append(s);
                if (false) o.append("|");
            }
            else
            {
                o.append("l");
                if (false) o.append("|");
            }
        }
        o.append("}");
        if (false) o.append("\\hline");
        o.append("\n");

        return o.toString();
    }

    protected String generateStopTabbingTeX()
    {
        // \\hline
        return "\\end{xltabular}\n";
    }

    protected String generateRuleAndVSpace()
    {
        // \\hline
        return "\\vspace{4mm}\n";
    }

    protected String generateStartTabbingHTMQ(AlignmentBlock cols)
    {
        StringBuilder o = new StringBuilder();
        o.append(" table {\n");
        return o.toString();
    }

    protected String generateStopTabbingHTMQ()
    {
        return " }\n";
    }

    protected void dbgCnvs(StringBuilder o, String info)
    {
        if (log.debugCanvasEnabled()) o.append(info);
    }

    protected String dbgCnvsMsg(String info)
    {
        if (log.debugCanvasEnabled()) return info;
        return "";
    }

}
