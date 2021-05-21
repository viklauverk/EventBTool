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

import static com.viklauverk.eventbtools.core.Helpers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class Canvas
{
    static Log log = LogModule.lookup("canvas");

    private RenderTarget render_target_ = RenderTarget.PLAIN;
    private RenderAttributes render_attributes_ = new RenderAttributes();

    private boolean use_layout_ = true;
    private boolean use_at_signs_ = false;
    private boolean force_raw_ = false; // Set to true when generating formulas for caching.

    private int layout_width_ = 120;
    private int used_width_ = 0;

    private boolean use_borders_ = false;

    private int curr_line_ = 0;
    private int curr_left_ = 0;
    private ArrayList<CanvasLine> lines_ = new ArrayList<>();

    private int last_debug_readout_line_ = 0;
    private int last_debug_readout_col_ = 0;

    private boolean line_active_ = false;
    private boolean alignments_active_ = false;
    private boolean aligned_line_active_ = false;
    private boolean math_active_ = false;
    private int num_aligns_ = 0;

    private Set<String> hiding_ = new HashSet<>();

    public Canvas() {}

    public static String align_2col = "l,X";
    public static String align_3col = "l,V{8cm},X";

    public Canvas(Canvas parent)
    {
        force_raw_ = parent.force_raw_;
        use_layout_ = parent.use_layout_;
        use_at_signs_ = parent.use_at_signs_;
        layout_width_ = parent.layout_width_;
        used_width_ = 0;
        use_borders_ = parent.use_borders_;
        render_target_ = parent.render_target_;
        render_attributes_ = parent.render_attributes_;
        hiding_ = parent.hiding_;

        line_active_ = parent.line_active_;
        alignments_active_ = parent.alignments_active_;
        math_active_ = parent.math_active_;
    }

    public Canvas copy()
    {
        return new Canvas(this);
    }

    public void hide(String h)
    {
        if (h == null) return;
        String[] parts = h.split(",", -1);
        for (String p : parts)
        {
            hiding_.add(p);
        }
        log.debug("hiding %s", h);
    }

    public void clear()
    {
        lines_ = new ArrayList<>();
    }

    public boolean hiding(String m)
    {
        return hiding_.contains(m);
    }

    public RenderTarget renderTarget()
    {
        return render_target_;
    }

    public void setRenderTarget(RenderTarget t)
    {
        render_target_ = t;
    }

    public RenderAttributes renderAttributes()
    {
        return render_attributes_;
    }

    public void setRenderAttributes(RenderAttributes ra)
    {
        render_attributes_ = ra;
    }

    public void appendCanvas(Canvas c)
    {
        List<CanvasLine> tobeadded = c.lines_;
        for (CanvasLine l : tobeadded)
        {
            pushBack(lines_, l);
        }
    }

    public void appendCanvasMerge(Canvas c)
    {
        ArrayList<CanvasLine> tobeadded = c.lines_;
        boolean first = true;
        for (CanvasLine l : tobeadded)
        {
            if (first)
            {
                getLastLine().line.append(l.line);
                first = false;
            }
            else
            {
                pushBack(lines_, l);
            }
        }
    }

    public int layoutWidth()
    {
        return layout_width_;
    }

    public void setLayoutWidth(int w)
    {
        layout_width_ = w;
    }

    String renderRaw()
    {
        // No formatting at all. This output is used for
        // rendering formulas as comparable/cachable text strings.
        // Thus no format settings must ever modify this output.
        StringBuilder o = new StringBuilder();
        for (int i=0; i<lines_.size(); ++i)
        {
            CanvasLine l = lines_.get(i);
            if (i > 0) o.append("\n");
            o.append(l.line);
        }
        return o.toString();
    }

    public String render()
    {
        if (force_raw_) return renderRaw();

        CanvasLineFormatting clf = new CanvasLineFormatting(render_target_);
        return clf.render(lines_);
    }

    public void setMark()
    {
        if (lines_.size() == 0)
        {
            last_debug_readout_line_ = 0;
            last_debug_readout_col_ = 0;
            return;
        }
        CanvasLine l = lines_.get(lines_.size()-1);
        last_debug_readout_line_ = lines_.size()-1;
        last_debug_readout_col_ = l.line.length();
    }

    public String getSinceMark()
    {
        if (!LogModule.debugEnabledSomewhere()) return "";

        if (lines_.size() == 0) return "";

        StringBuilder o = new StringBuilder();

        CanvasLine l = lines_.get(last_debug_readout_line_);
        if (last_debug_readout_col_ < l.line.length())
        {
            o.append(l.line.substring(last_debug_readout_col_));
        }
        o.append("\n");

        for (int i=last_debug_readout_line_+1; i<lines_.size(); ++i)
        {
            o.append(lines_.get(i));
            o.append("\n");
        }

        while (o.length() > 1 &&  o.charAt(o.length()-1) == '\n') o.setLength(o.length()-1);
        return o.toString();
    }

    public void useLayout(boolean t)
    {
        use_layout_ = t;
    }

    public void useBorders(boolean t)
    {
        use_borders_ = t;
    }

    public void useRaw(boolean t)
    {
        force_raw_ = t;
    }

    public void append(String b)
    {
        String[] lines = b.split("\n", -1);
        boolean first = true;
        for (String l : lines)
        {
            if (first)
            {
                CanvasLine ll = getLastLine();
                ll.line.append(l);
                first = false;
            }
            else
            {
                lines_.add(new CanvasLine(l));
            }
        }
        if (b.length() >= 1 && b.charAt(b.length() - 1) == '\n')
        {
            lines_.add(new CanvasLine(""));
        }
        flush();
    }

    public void appendBox(String b)
    {
        if (use_layout_)
        {
            appendBoxWithLayout(b);
        }
        else
        {
            appendBoxPlain(b);
        }
    }

    CanvasLine getLastLine()
    {
        if (lines_.size() == 0)
        {
            lines_.add(new CanvasLine(""));
        }
        return getLine(lines_.size()-1);
    }

    CanvasLine getLine(int i)
    {
        while (lines_.size() <= i)
        {
            lines_.add(new CanvasLine(""));
        }
        return lines_.get(i);
    }

    public static int width(String b)
    {
        if (b == null) return 0;
        int w = 0;
        String[] lines = b.split("\n", -1);
        for (String l : lines) w = Math.max(w, l.length());
        return w;
    }

    public void appendBoxWithLayout(String b)
    {
        int width = 0;
        String[] lines = b.split("\n", -1);
        for (String l : lines) width = Math.max(width, l.length());
        boolean fit = true;
        for (int i = 0; i<lines.length; ++i)
        {
            CanvasLine l = getLine(curr_line_+i);
            curr_left_ = Math.max(curr_left_, l.line.length());
            if (l.line.length() + width + 2 > layout_width_)
            {
                fit = false;
            }
        }

        if (fit == false)
        {
            lines_.add(new CanvasLine(""));
            flush();
        }
        else
        {
            if (curr_left_ > 0)
            {
                curr_left_ += 2;
            }
        }

        for (int i = 0; i<lines.length; ++i)
        {
            CanvasLine l = getLine(curr_line_+i);
            while (l.line.length() < curr_left_) l.line.append(" ");
            l.line.append(lines[i]);
        }
    }

    public void appendBoxPlain(String b)
    {
        String[] lines = b.split("\n", -1);
        for (String l : lines)
        {
            lines_.add(new CanvasLine(l));
        }
        flush();
    }

    public void flush()
    {
        curr_line_ = lines_.size();
        curr_left_ = 0;
    }

    public static String flow(int width, String s)
    {
        if (s == null) return "";
        int curr_len = 0;
        StringBuilder o = new StringBuilder();
        String[] words = s.split(" ");
        for (String w : words)
        {
            if (curr_len + w.length() > width)
            {
                o.append("\n");
                curr_len = 0;
            }
            if (curr_len > 0) o.append(" ");
            o.append(w);
            curr_len += w.length();
        }
        o.append("\n");
        return o.toString();
    }

    static class Box
    {
        // topleft topright bottomleft bottomright borderhoriz bordervert
        public final String tl, tr, bl, br, bh, bv;
        // middleleft middlehoriz middleright
        public final String ml, mh, mr;
        // middletop middlevert middlebottom
        public final String mt, mv, mb;
        // titleleft titleright
        public final String til, tir;

        Box(String a, String b, String c, String d, String e, String f,
            String g, String h, String i, String j, String k, String l,
            String m, String n)
        {
            tl = a; tr = b; bl = c; br = d; bh = e; bv = f;
            ml = g; mh = h; mr = i;
            mt = j; mv = k; mb = l;
            til = m; tir = n;
        }
    }

    static Box sline = new Box("┌", "┐", "└", "┘", "─", "│", "├", "─", "┤", "┬", "│", "┴", " ", " ");
    static Box dline = new Box("╔", "╗", "╚", "╝", "═", "║", "╟", "─", "╢", "╤", "│", "╧", "╡", "╞");

    public
    String frame(String title, String s, Box b)
    {
        if (s.endsWith("\n"))
        {
            s = s.substring(0,s.length()-1);
        }
        switch (render_target_)
        {
        case PLAIN:
        case TERMINAL:
            return frameUnicode(title, s, b);
        case TEX:
            return frameTeX(title, s, b);
        case HTMQ:
            return frameHTMQ(title, s, b);
        }
        return s;
    }

    String frameUnicode(String title, String s, Box b)
    {
        StringBuilder o = new StringBuilder();
        String[] lines = s.split("\n", -1);
        int width = 0;
        int title_width = lengthIgnoringAnsi(title);
        for (String l : lines)
        {
            width = Math.max(width, lengthIgnoringAnsi(l));
        }
        width = Math.max(width, 2+title_width);

        o.append(b.tl);
        if (title_width == 0)
        {
            for (int i=0; i<width; ++i) o.append(b.bh);
        }
        else
        {
            o.append(b.til);
            o.append(title);
            o.append(b.tir);
            for (int i=0; i<width-title_width-2; ++i) o.append(b.bh);
        }
        o.append(b.tr);
        o.append("\n");

        for (String l : lines)
        {
            if (l.equals("-"))
            {
                o.append(b.ml);
                for (int i=0; i<width; ++i) o.append(b.mh);
                o.append(b.mr);
                o.append("\n");
            }
            else
            {
                o.append(b.bv);
                o.append(l);
                int delta = width-lengthIgnoringAnsi(l);
                for (int i=0; i<delta; ++i) o.append(" ");
                o.append(b.bv);
                o.append("\n");
            }
        }

        o.append(b.bl);
        for (int i=0; i<width; ++i) o.append(b.bh);
        o.append(b.br);
        o.append("\n");

        return o.toString();
    }

    String frameTeX(String title, String s, Box b)
    {
        StringBuilder o = new StringBuilder();
        String[] lines = s.split("\n", -1);
        int width = 0;
        int title_width = title.length();
        for (String l : lines) width = Math.max(width, l.length());
        width = Math.max(width, 2+title_width);

        o.append("\\bgroup\n");
        o.append("\\def\\arraystretch{1.5}%\n");
        o.append("\\begin{tabular}{|l|}%frameTeX\n");
        o.append("\\hline\n");

        if (title_width > 0)
        {
            o.append(title);
            o.append("\\\\\n");
            o.append("\\hline\n");
        }

        for (String l : lines)
        {
            if (l.equals("-"))
            {
                o.append("\\hline\n");
            }
            else
            {
                o.append(l);
                o.append("\\\\\n");
            }
        }

        o.append("\\hline\n");
        o.append("\\end{tabular}%frameTeX\n");
        o.append("\\egroup\n");
        return o.toString();
    }

    String frameHTMQ(String title, String s, Box b)
    {
        return s;
    }

    public static int isAnsiHere(int i, String l)
    {
        int start = i;
        if (l.charAt(i) == '\033' && l.charAt(i+1) == '[')
        {
            i += 2;
            while (l.charAt(i) != 'm') i++;
            return i-start+1;
        }
        return 0;
    }

    public static int lengthIgnoringAnsi(String l)
    {
        int sum = 0;
        for (int i=0; i<l.length(); ++i)
        {
            int len = Canvas.isAnsiHere(i, l);
            if (len == 0)
            {
                sum++;
            }
            else
            {
                // Skip ansi sequence.
                i += len-1; // Minus one because of loop j++;
            }
        }
        return sum;
    }

    public static String removeAnsi(String l)
    {
        StringBuilder o = new StringBuilder();

        for (int i=0; i<l.length(); ++i)
        {
            if (l.charAt(i) == '\033' && l.charAt(i+1) == '[')
            {
                i += 2;
                while (l.charAt(i) != 'm') i++;
            }
            else
            {
                o.append(l.charAt(i));
            }
        }
        return o.toString();
    }

    public static String NoColor="\033[0m";

    // Regular Colors
    private static String Black="\033[0;30m";
    public static String Red="\033[0;31m";
    private static String Green="\033[0;32m";
    private static String Yellow="\033[0;33m";
    private static String Blue="\033[0;34m";
    private static String Purple="\033[0;35m";
    private static String Cyan="\033[0;36m";
    private static String White="\033[0;37m";

    // Bold
    private static String BBlack="\033[1;30m";
    private static String BRed="\033[1;31m";
    private static String BGreen="\033[1;32m";
    private static String BYellow="\033[1;33m";
    private static String BBlue="\033[1;34m";
    private static String BPurple="\033[1;35m";
    private static String BCyan="\033[1;36m";
    private static String BWhite="\033[1;37m";

    // Underline
    private static String UBlack="\033[4;30m";
    private static String URed="\033[4;31m";
    private static String UGreen="\033[4;32m";
    private static String UYellow="\033[4;33m";
    private static String UBlue="\033[4;34m";
    private static String UPurple="\033[4;35m";
    private static String UCyan="\033[4;36m";
    private static String UWhite="\033[4;37m";

    // Background
    private static String On_Black="\033[40m";
    private static String On_Red="\033[41m";
    private static String On_Green="\033[42m";
    private static String On_Yellow="\033[43m";
    private static String On_Blue="\033[44m";
    private static String On_Purple="\033[45m";
    private static String On_Cyan="\033[46m";
    private static String On_White="\033[47m";

    // High Intensity
    private static String IBlack="\033[0;90m";
    private static String IRed="\033[0;91m";
    private static String IGreen="\033[0;92m";
    private static String IYellow="\033[0;93m";
    private static String IBlue="\033[0;94m";
    private static String IPurple="\033[0;95m";
    private static String ICyan="\033[0;96m";
    private static String IWhite="\033[0;97m";

    // Bold High Intensity
    private static String BIBlack="\033[1;90m";
    private static String BIRed="\033[1;91m";
    private static String BIGreen="\033[1;92m";
    private static String BIYellow="\033[1;93m";
    private static String BIBlue="\033[1;94m";
    private static String BIPurple="\033[1;95m";
    private static String BICyan="\033[1;96m";
    private static String BIWhite="\033[1;97m";

    // High Intensity backgrounds
    private static String On_IBlack="\033[0;100m";
    private static String On_IRed="\033[0;101m";
    private static String On_IGreen="\033[0;102m";
    private static String On_IYellow="\033[0;103m";
    private static String On_IBlue="\033[0;104m";
    private static String On_IPurple="\033[0;105m";
    private static String On_ICyan="\033[0;106m";
    private static String On_IWhite="\033[0;107m";

    /*
    public int lengthIgnoringAnsi(String s)
    {
        int c = 0;
        for (int i=0; i<s.length();)
        {
            if (s.charAt(i) == '\033')
            {
                // Skip ansi control sequence.
                while (i<s.length() && s.charAt(i) != 'm')
                {
                    i++;
                }
                i++; // Skip the m.
            }
            else
            {
                c++;
                i++;
            }
        }
        return c;
    }
    */

    /**
       Previously added canvas lines will be scanned for § characters
       and align them into vertical columns. Padding or making tables.

       If no § characters are found, then no formatting will be applied.
     */
    public void stopAlignments()
    {
        assert(alignments_active_ == true) : "Internal error: Expected alignments to be active when starting alignments, but it was already active!";
        alignments_active_ = false;

        append("¤flush¤\n");
    }

    public void startAlignments(String a)
    {
        assert(alignments_active_ == false) : "Internal error: Expected alignments to be inactive when starting alignments, but it was already active!";
        alignments_active_ = true;
        append("¤flush¤\n");
        // Example alignment: 0,50%,40%
        // 0 = shrink to content width.
        // 0.5 = 0.5 of line text width
        // 0.4 = 0.4
        append("¤align "+a+"¤\n");
    }

    public void startAlignedLine()
    {
       assert(aligned_line_active_ == false) : "Internal error: Expected aligned line to be inactive when starting aligned line, but it was already active!";
        aligned_line_active_ = true;
    }

    public void stopAlignedLine()
    {
       assert(aligned_line_active_ == true) : "Internal error: Expected aligned line to be active when stopping aligned line, but it was already active!";
        aligned_line_active_ = false;
        append("\n");
    }

    public void startMath()
    {
        assert(math_active_ == false) : "Internal error: Expected math to be inactive when starting math mode, and it was already active!";
        math_active_ = true;
        switch (render_target_)
        {
        case PLAIN:
            return;
        case TERMINAL:
            return;
        case TEX:
            append("$");
            return;
        case HTMQ:
            //append(" '\\(' ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void stopMath()
    {
        assert(math_active_ == true) : "Internal error: Expected math to be active when stopping math mode, and it was already inactive!";
        math_active_ = false;
        switch (render_target_)
        {
        case PLAIN:
            return;
        case TERMINAL:
            return;
        case TEX:
            append("$");
            return;
        case HTMQ:
            //append(" '\\)' ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void startGuard()
    {
        switch (render_target_)
        {
        case PLAIN:
            return;
        case TERMINAL:
            return;
        case TEX:
            append("\\GRD{");
            return;
        case HTMQ:
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void stopGuard()
    {
        switch (render_target_)
        {
        case PLAIN:
            return;
        case TERMINAL:
            return;
        case TEX:
            append("}");
            return;
        case HTMQ:
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void startAction()
    {
        switch (render_target_)
        {
        case PLAIN:
            return;
        case TERMINAL:
            return;
        case TEX:
            append("\\ACT{");
            return;
        case HTMQ:
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void stopAction()
    {
        switch (render_target_)
        {
        case PLAIN:
            return;
        case TERMINAL:
            return;
        case TEX:
            append("}");
            return;
        case HTMQ:
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void hrule()
    {
        switch (render_target_)
        {
        case PLAIN:
            append("\n");
            return;
        case TERMINAL:
            append("\n");
            return;
        case TEX:
            append("\\hrule");
            return;
        case HTMQ:
            append(" hr ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void nl()
    {
        append("\n");
    }

    public void newlineInFormula()
    {
        switch (render_target_)
        {
        case PLAIN:
            append("\n");
            return;
        case TERMINAL:
            append("\n");
            return;
        case TEX:
            append(" \\newline ");
            return;
        case HTMQ:
            append(" br ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void startLine()
    {
        assert (line_active_ == false) : "Internal error: startLine expected no active line, but it was!";
        line_active_ = true;
        dbgCnvs("[line]");
        switch (render_target_)
        {
        case PLAIN:
        case TERMINAL:
            return;
        case TEX:
            append("\\LINE{");
            return;
        case HTMQ:
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void endLine()
    {
        assert (line_active_ == true) : "Internal error: endLine expected line to be active!, but it was not!";
        line_active_ = false;
        math_active_ = false;

        dbgCnvs("[/line]");

        switch (render_target_)
        {
        case PLAIN:
            append("\n");
            return;
        case TERMINAL:
            append("\n");
            return;
        case TEX:
            append("}\n");
            return;
        case HTMQ:
            append(" br \n");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void line(String l)
    {
        startLine();
        append(l);
        endLine();
    }

    public void lufft()
    {
        switch (render_target_)
        {
        case PLAIN:
            append("\n");
            return;
        case TERMINAL:
            append("\n");
            return;
        case TEX:
            append("\\LUFT\n");
            return;
        case HTMQ:
            append(" br ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void align()
    {
       assert(aligned_line_active_ == true) : "Internal error: Expected aligned line active when aligning!";

        append("§"); // Canvas.render will use § on multiple lines to force horisontal alignement.
        num_aligns_++;
    }

    public void space()
    {
        append(" ");
    }

    public void id(String s)
    {
        switch (render_target_)
        {
        case PLAIN:
            append(s);
            return;
        case TERMINAL:
            append(s);
            return;
        case TEX:
            append("\\ID{"+Util.texSafe(s)+"}");
            return;
        case HTMQ:
            append(" span(class=ID)="+Util.quoteXMQ(s)+" ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void keyword(String s)
    {
        switch (render_target_)
        {
        case PLAIN:
            append(s);
            return;
        case TERMINAL:
            append(colorize(BRed, s));
            return;
        case TEX:
            append("\\KEYW{"+Util.texSafe(s).toUpperCase()+"}");
            return;
        case HTMQ:
            append(" span(class=KEYW)="+Util.quoteXMQ(s)+" ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void keywordLeft(String s)
    {
        switch (render_target_)
        {
        case PLAIN:
            append(s);
            return;
        case TERMINAL:
            append(colorize(BRed,s));
            return;
        case TEX:
            append("\\KEYWL{"+Util.texSafe(s).toUpperCase()+"}");
            return;
        case HTMQ:
            append(" span(class=KEYWL)="+Util.quoteXMQ(s)+" ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void variableDef(String s)
    {
        switch (render_target_)
        {
        case PLAIN:
            append(s);
            return;
        case TERMINAL:
            append(colorize(BBlue, s));
            return;
        case TEX:
            append("\\VARDEF{"+Util.texSafe(s)+"}");
            return;
        case HTMQ:
            append(" span(class=VARDEF)="+Util.quoteXMQ(s)+" ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void variable(String s)
    {
        switch (render_target_)
        {
        case PLAIN:
            append(s);
            return;
        case TERMINAL:
            append(colorize(BBlue, s));
            return;
        case TEX:
            append("\\VAR{"+Util.texSafe(s)+"}");
            return;
        case HTMQ:
            append(" span(class=VAR)="+Util.quoteXMQ(s)+" ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void nonFreeVariable(String s)
    {
        switch (render_target_)
        {
        case PLAIN:
            append(s);
            return;
        case TERMINAL:
            append(s);
            return;
        case TEX:
            append("\\NONF{"+Util.texSafe(s)+"}");
            return;
        case HTMQ:
            append(" span(class=NONF)="+Util.quoteXMQ(s)+" ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void number(String s)
    {
        switch (render_target_)
        {
        case PLAIN:
            append(s);
            return;
        case TERMINAL:
            append(s);
            return;
        case TEX:
            append("\\NUM{"+Util.texSafe(s)+"}");
            return;
        case HTMQ:
            append(" span(class=NUM)="+Util.quoteXMQ(s)+" ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void any(String s)
    {
        switch (render_target_)
        {
        case PLAIN:
            append(s);
            return;
        case TERMINAL:
            append(s);
            return;
        case TEX:
            append("\\ANY{"+Util.texSafe(s)+"}");
            return;
        case HTMQ:
            append(" span(class=ANY)="+Util.quoteXMQ(s)+" ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void constant(String s)
    {
        switch (render_target_)
        {
        case PLAIN:
            append(s);
            return;
        case TERMINAL:
            append(colorize(BGreen, s));
            return;
        case TEX:
            append("\\CON{"+Util.texSafe(s)+"}");
            return;
        case HTMQ:
            append(" span(class=CON)="+Util.quoteXMQ(s)+" ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void primitiveSet(String s)
    {
        switch (render_target_)
        {
        case PLAIN:
            append(s);
            return;
        case TERMINAL:
            append(colorize(Red, s));
            return;
        case TEX:
            append("\\PSET{"+Util.texSafe(s)+"}");
            return;
        case HTMQ:
            append(" span(class=PSET)="+Util.quoteXMQ(s)+" ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void set(String s)
    {
        switch (render_target_)
        {
        case PLAIN:
            append(s);
            return;
        case TERMINAL:
            append(colorize(Green, s));
            return;
        case TEX:
            append("\\CSET{"+Util.texSafe(s)+"}");
            return;
        case HTMQ:
            append(" span(class=CSET)="+Util.quoteXMQ(s)+" ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void predicate(String s)
    {
        switch (render_target_)
        {
        case PLAIN:
            append(s);
            return;
        case TERMINAL:
            append(s);
            return;
        case TEX:
            append("\\PRED{"+Util.texSafe(s)+"}");
            return;
        case HTMQ:
            append(" span(class=PRED)="+Util.quoteXMQ(s)+" ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void expression(String s)
    {
        switch (render_target_)
        {
        case PLAIN:
            append(s);
            return;
        case TERMINAL:
            append(s);
            return;
        case TEX:
            append("\\EXPR{"+Util.texSafe(s)+"}");
            return;
        case HTMQ:
            append(" span(class=EXPR)="+Util.quoteXMQ(s)+" ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void symbol(String s)
    {
        switch (render_target_)
        {
        case PLAIN:
            append(s);
            return;
        case TERMINAL:
            append(s);
            return;
        case TEX:
            append(s);
            return;
        case HTMQ:
            append(" "+Util.quoteXMQ(s)+" ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void marker(String s)
    {
        if (!renderAttributes().anchors()) return;

        switch (render_target_)
        {
        case PLAIN:
            return;
        case TERMINAL:
            return;
        case TEX:
            append("\\label{"+s+"}");
            return;
        case HTMQ:
            append(" a(id=\""+s+"\") ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void comment(String s)
    {
        if (hiding("comment")) return;

        if (s.equals("")) return;

        switch (render_target_)
        {
        case PLAIN:
            append("// "+s);
            return;
        case TERMINAL:
            append(colorize(Green, "// "+s));
            return;
        case TEX:
            append("\\COM{"+Unicode.commentToTeX(s)+"}");
            return;
        case HTMQ:
            append(" span(class=COM)="+Util.quoteXMQ(s)+" ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void acomment(String s)
    {
        if (hiding("comment")) return;

        if (s.equals("")) return;

        switch (render_target_)
        {
        case PLAIN:
            append("// "+s);
            return;
        case TERMINAL:
            append(colorize(Green, s));
            return;
        case TEX:
            append("\\ACOM{"+Unicode.commentToTeX(s)+"}");
            return;
        case HTMQ:
            append(" span(class=ACOM)="+Util.quoteXMQ(s)+" ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void label(String s)
    {
        if (!renderAttributes().labels()) return;

        String pre = "";
        String post = ":";
        if (renderAttributes().at())
        {
            pre = "@";
            post = "";
        }
        switch (render_target_)
        {
        case PLAIN:
            append(pre+s+post);
            return;
        case TERMINAL:
            append(colorize(Blue, pre+s+post));
            return;
        case TEX:
            append("\\LAB{"+Util.texSafe(pre+s+post)+"}");
            return;
        case HTMQ:
            append(" span(class=LAB)="+Util.quoteXMQ(pre+s+post)+" ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public void invariant(String s)
    {
        if (s.equals("")) return;

        switch (render_target_)
        {
        case PLAIN:
            append("// "+s);
            return;
        case TERMINAL:
            append(colorize(Green, "// "+s));
            return;
        case TEX:
            append("\\COM{"+Unicode.commentToTeX(s)+"}");
            return;
        case HTMQ:
            append(" span(class=COM)="+Util.quoteXMQ(s)+" ");
            return;
        }
        assert (false) : "Unknown encoding "+render_target_;
    }

    public String colorize(String color, String s)
    {
        if (renderAttributes().color())
        {
            return color+s+NoColor;
        }
        return s;
    }

    protected void dbgCnvs(String info)
    {
        if (log.debugCanvasEnabled()) append(info);
    }

    protected String dbgCnvsMsg(String info)
    {
        if (log.debugCanvasEnabled()) return info;
        return "";
    }

}
