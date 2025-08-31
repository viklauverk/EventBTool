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

package com.viklauverk.evbt.core;

import java.util.HashMap;

public class Unicode
{
    // List of non-ascii Event-B unicode characters that needs
    // special care if they occur in comments when generating TeX.
    enum NonAsciiChars
    {
        BECOME_EQ         ('≔', "\\bcmeq "),
        FALSE             ('⊥', "\\bfalse "),
        TRUE              ('⊤', "\\btrue "),
        CONJUNCTION       ('∧', "\\land "),
        IMPLICATION       ('⇒', "\\limp "),
        NEGATION          ('¬', "\\lnot "),
        DISJUNCTION       ('∨', "\\lor "),
        EQUIVALENCE       ('⇔', "\\leqv "),
        UNIVERSALQ        ('∀', "\\forall "),
        EXISTENTIALQ      ('∃', "\\exists "),
        MAPSTO            ('↦', "\\mapsto "),
        NOT_EQUALS        ('≠', "\\neq "),
        LESS_THAN_OR_EQUAL('≤', "\\le "),
        GREATER_THAN_OR_EQUAL('≥',"\\ge "),
        MEMBERSHIP        ('∈', "\\in "),
        NOT_MEMBERSHIP    ('∉', "\\notin "),
        SUBSET            ('⊆', "\\subseteq "),
        STRICT_SUBSET     ('⊂', "\\subset "),
        NOT_SUBSET        ('⊈', "\\not\\subseteq "),
        NOT_STRICT_SUBSET ('⊄', "\\not\\subset "),
        OF_TYPE           ('⦂', "\\oftype "),
        CARTESIAN_PRODUCT ('×', "\\cprod "),
        RELATION          ('↔', "\\rel "),
        TOTAL_RELATION    ('', "\\trel "),
        SURJECTIVE_RELATION('',"\\srel "),
        SURJECTIVE_TOTAL_RELATION('', "\\strel "),
        PARTIAL_FUNCTION  ('⇸', "\\pfun "),
        TOTAL_FUNCTION    ('→', "\\tfun "),
        PARTIAL_INJECTION ('⤔', "\\pinj "),
        TOTAL_INJECTION   ('↣', "\\tinj "),
        PARTIAL_SURJECTION('⤀', "\\psur "),
        TOTAL_SURJECTION  ('↠', "\\tsur "),
        TOTAL_BIJECTION   ('⤖', "\\tbij "),
        BACKWARD_COMPOSITION('∘',"\\bcomp "),
        DOMAIN_RESTRICTION('◁', "\\domres "),
        DOMAIN_SUBTRACTION('⩤', "\\domsub "),
        RANGE_RESTRICTION ('▷', "\\ranres "),
        RANGE_SUBTRACTION ('⩥', "\\ransub "),
        OVERRIDE          ('', "\\ovl "),
        DIRECT_PRODUCT    ('⊗', "\\dprod "),
        PARALLEL_PRODUCT  ('∥', "\\pprod "),
        POWER             ('ℙ', "\\mathbb{P} "),
        Q_UNION           ('⋃', "\\Union "),
        Q_INTER           ('⋂', "\\Inter "),
        SET_UNION         ('∪', "\\bunion "),
        SET_INTERSECTION  ('∩', "\\binter "),
        SET_MINUS         ('∖', "\\setminus "), // This is not backslash! This is unicode U+2216
        LAMBDA            ('λ', "\\lambda "),
        DOT               ('·', "\\qdot "),
        MULTIPLICATION    ('∗', "*"), // Woot?
        DIVISION          ('÷', "\\div "),
        EXPONENTIATION    ('^', "\\expn "),
        UP_TO             ('‥', ".."), // Woot?
        EMPTY_SET         ('∅', "\\emptyset "),
        NAT_SET           ('ℕ', "\\mathbb{N} "),
        INT_SET           ('ℤ', "\\mathbb{Z} ");

        char   uni; // Unicode char
        String tex; // Safe tex (bsymb loaded), both for unicode and non-unicode aware tex.

        NonAsciiChars(char u, String t)
        {
            uni = u;
            tex = t;
        }
    }

    // List of chars that must be protected from TeX.
    enum TeXUnsafeChars
    {
        BACKSLASH         ('\\',"\\textbackslash "),
        HASH              ('#', "\\#"),
        PERCENT           ('%', "\\%"),
        AMPERSAND         ('&', "\\&"),
        LBRACE            ('{', "\\{"),
        RBRACE            ('}', "\\}"),
        UNDERLINE         ('_', "\\_"),
        TILDE             ('~', "~{}"),
        DOLLAR            ('$', "\\$"),
        NEWLINE           ('\n', "\\newline ");

        char   uni; // Unicode char
        String tex; // Safe tex (bsymb loaded), both for unicode and non-unicode aware tex.

        TeXUnsafeChars(char u, String t)
        {
            uni = u;
            tex = t;
        }
    }

    static HashMap<Character,NonAsciiChars> non_ascii_chars_;
    static HashMap<Character,TeXUnsafeChars> tex_unsafe_chars_;

    static
    {
        non_ascii_chars_ = new HashMap<>();
        for (NonAsciiChars c : NonAsciiChars.values())
        {
            non_ascii_chars_.put(c.uni, c);
        }
        tex_unsafe_chars_ = new HashMap<>();
        for (TeXUnsafeChars c : TeXUnsafeChars.values())
        {
            tex_unsafe_chars_.put(c.uni, c);
        }
    }

    public static
    String trimLinesAndJoin(String s)
    {
        s = s.trim();
        String[] rows = s.split("\n", -1);
        StringBuilder o = new StringBuilder();
        boolean space = false;
        boolean break_added = false;
        for (String r : rows)
        {
            String l = r.trim();
            if (l.length() == 0)
            {
                if (!break_added)
                {
                    o.append(" ¤BR¤ ");
                    break_added = true;
                }
            }
            else
            {
                break_added = false;
                if (space)
                {
                    o.append(" ");
                }
                else
                {
                    space = true;
                }
            }
            if (l.length() > 0)
            {
                o.append(l);
            }
        }
        return o.toString();
    }

    public static
    String commentToTeX(String s)
    {
        s = trimLinesAndJoin(s);
        StringBuilder o = new StringBuilder();
        boolean in_math_mode = false;
        int word_len = 0; // Max word len is 10, then a \- is inserted.
        for (int i=0; i<s.length(); ++i)
        {
            char c = s.charAt(i);
            if (c == ' ' || c == '\n') word_len = 0;
            if (c == '\\' && i+1 < s.length())
            {
                word_len = 0; // Reset word len count.
                char cc = s.charAt(i+1);
                if (cc == '(' && in_math_mode == false)
                {
                    in_math_mode = true;
                    i++; // skip (
                    o.append("$");
                    continue;
                }
                if (cc == ')' && in_math_mode == true)
                {
                    // A \) exits tex math mode.
                    in_math_mode = false;
                    i++; // skip )
                    o.append("$");
                    continue;
                }
                if (cc == '\n')
                {
                    // A \ followed by newline means join next line
                    // but do not add the newline.
                    i++; // skip nl
                    continue;
                }
            }

            // Math mode means that you can give tex commads using \...
            if (!in_math_mode)
            {
                // But we are in normal comment mode. Any tex control chars must be escaped.
                TeXUnsafeChars tuc = tex_unsafe_chars_.get(c);
                if (tuc != null)
                {
                    // Oups, unsafe like \ _ ^ # etc, replace it with some tex-friendly.
                    o.append(tuc.tex);
                    word_len = 0; // Reset word len count.
                    continue;
                }
            }
            NonAsciiChars nac = non_ascii_chars_.get(c);
            if (nac == null)
            {
                // Not a special unicode character, just append it.
                o.append(c);
                word_len++;
                if (word_len > 15)
                {
                    // Make long words hyphenable.
                    o.append("\\-");
                    word_len = 0;
                    }
            }
            else
            {
                // Need to replace the unicode with a tex command usually from bsymb.sty.
                if (!in_math_mode) o.append("$");
                o.append(nac.tex);
                if (!in_math_mode) o.append("$");
            }
        }
        return o.toString().replace("¤BR¤", " \\linebreak\\rule{0mm}{5mm}\\hspace{-1mm}");
    }

    public static
    String commentToCpp(String s)
    {
        s = s.trim();
        String[] rows = s.split("\n", -1);
        if (rows.length == 1) return "// "+rows[0];
        StringBuilder o = new StringBuilder();
        o.append("/* ");
        boolean nl = false;
        for (String r : rows)
        {
            String l = r.trim();
            if (nl)
            {
                o.append("\n");
            }
            else
            {
                nl = true;
            }
            o.append(l);
        }
        o.append(" */\n");
        return o.toString();
    }

}
