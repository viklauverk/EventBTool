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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.Charset;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.function.Function;

public class Util
{
    public static Log log = LogModule.lookup("util");
    public static Log log_match = LogModule.lookup("match");

    public static String[] shiftLeft(String[] args)
    {
        if (args.length <= 1) return new String[0];

        String[] tmp = new String[args.length-1];
        for (int i=0; i<args.length-1; ++i)
        {
            tmp[i] = args[i+1];
        }
        return tmp;
    }

    public static String padRightToLen(String s, char c, int len)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(s);
        len -= s.length();
        while (len > 0) { sb.append(c); len--; }
        return sb.toString();
    }

    public static String padRight(String s, char c, int len)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(s);
        while (len > 0) { sb.append(c); len--; }
        return sb.toString();
    }

    public static String padLeft(String s, char c, int len)
    {
        StringBuilder sb = new StringBuilder();
        while (len > 0) { sb.append(c); len--; }
        sb.append(s);
        return sb.toString();
    }

    public static
    String replaceChars(String s)
    {
        StringBuilder o = new StringBuilder();
        for (int i=0; i<s.length(); ++i)
        {
            char c = s.charAt(i);
            if (c == '∈') o.append(':');
            else if (c == '∉') o.append("/=");
            else if (c == '≔') o.append(":=");
            else if (c == 'ℕ') o.append("NAT");
            else if (c == '∀') o.append('!');
            else if (c == '⇒') o.append("=>");
            else if (c == '∅') o.append("{}");
            else if (c == '∧') o.append("&");
            else if (c == '≠') o.append("/=");
            else if (c == '⊆') o.append("<:");
            else if (c == '≤') o.append("<=");
            else if (c == '≥') o.append(">=");
            else if (c == '∪') o.append("\\/");
            else if (c == '∃') o.append("#");
            else if (c == '·') o.append(".");
            else if (c == '→') o.append("-->");
            else if (c == 'ℙ') o.append("pow");
            else if (c == '×') o.append("**");
            else
            o.append(c);
        }
        return o.toString();
    }

    public static
    boolean hasNewLine(String s)
    {
        for (int i=0; i<s.length(); ++i)
        {
            if (s.charAt(i) == '\n') return true;
        }
        return false;
    }

    public static
    String trimLines(String s)
    {
        StringBuilder o = new StringBuilder();
        String[] lines = s.split("\n", -1);
        for (String l : lines)
        {
            o.append(l.trim());
            o.append("\n");
        }
        return o.toString();
    }

    public static String readFile(String path)
        throws IOException
    {
        Charset encoding = Charset.defaultCharset();
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static void writefile(String path, String content)
        throws IOException
    {
        Files.write(Paths.get(path), content.getBytes());
    }

    static int capturePar(String in, int pos, StringBuilder out)
    {
        assert(in.charAt(pos) == '(');
        out.append('(');
        pos++;
        for (;;)
        {
            if (pos >= in.length()) return -1; // Oups! Reached EOF without closing par.
            char c = in.charAt(pos);

            if (c == ')')
            {
                out.append(c);
                return pos+1;
            }
            else
            if (c == '(')
            {
                pos = capturePar(in, pos, out);
                if (pos == -1) return -1;
            }
            else
            {
                out.append(c);
                pos++;
            }
        }
    }

    static int findNextEVBT(String in, int pos)
    {
        for (;;)
        {
            if (pos+5 >= in.length()) return -1;

            if (in.charAt(pos) == 'E' &&
                in.charAt(pos+1) == 'V' &&
                in.charAt(pos+2) == 'B' &&
                in.charAt(pos+3) == 'T' &&
                in.charAt(pos+4) == '(')
            {
                return pos;
            }
            pos++;
        }
    }

    public static String replaceEVBT(String template,
                                     Function<String,String> handler)
    {
        StringBuffer out = new StringBuffer();

        int prev = 0;
        for (;;)
        {
            int pos = findNextEVBT(template, prev);
            if (pos == -1) break;
            out.append(template.substring(prev, pos));

            StringBuilder buf = new StringBuilder();
            prev = capturePar(template, pos+4, buf);
            if (prev == -1)
            {
                String part = buf.toString();
                pos = part.indexOf("\n");
                if (pos > 5)
                {
                    part = part.substring(0, pos);
                }
                if (part.length() > 32)
                {
                    part = part.substring(0,32);
                }
                log.usageError("Probably unmatched parentheses in EVBT(...) command\n"+
                               "that starts with: EVBT"+part);
                System.exit(1);
            }

            String key = buf.toString().substring(1, buf.length()-1); // Remove outer parentheses.
            String value = handler.apply(key);
            if (value == null)
            {
                value = "ERROR MISSING "+key;
            }
            if (value.endsWith("\n")) value = value.substring(0,value.length()-1);
            log.debug("replacing >>%s<< with >>%s<<", key, value);
            out.append(value);
        }
        out.append(template.substring(prev));
        return out.toString();
    }

    // Pattern Elevator -- render whole machine
    //         Elevator/invariants -- render all invariants
    //         Elevator/invariants/inv_space -- render only this invariant

    public static boolean match(String path, String pattern)
    {
        log_match.trace("match? \"%s\" \"%s\"", path, pattern);
        if (pattern == null || pattern.length() == 0)
        {
            log_match.trace("yes!");
            return true;
        }

        if (path.startsWith(pattern))
        {
            log_match.trace("yes!");
            return true;
        }

        // Very rudimentary wildcard. To be improved.
        if (pattern.startsWith("*"))
        {
            if (path.indexOf(pattern.substring(1)) != -1)
            {
                log_match.trace("yes!");
                return true;
            }
        }
        log_match.trace("no!");
        return false;
    }

    /*
    private static boolean isChar(char c)
    {
        return (c >='a' && c <='<') ||
               (c >= 'A' && c <= 'Z');
    }

    public static int widthFromString(String s)
    {
        int w = 0;
        for (int i=0; i<s.length(); ++i)
        {
            // Find tex commands and collapse them into a single char.
            if (s.charAt(i) == '\\')
            {
                w++;
                i++;
                while (i < s.length() && isChar(s.charAt(i))) i++;
                continue;
            }
            if (s.charAt(i) == ' ') continue;
            w++;
        }
        }*/


    public static
    String texSafe(String s)
    {
        assert s.indexOf("\n") == -1 : "Internal error: string should not contain newline \""+s+"\"";

        StringBuilder sb = new StringBuilder();
        for (int i=0; i<s.length(); ++i)
        {
            char c = s.charAt(i);
            if (c == '_')
            {
                sb.append("\\protect\\UL ");
            }
            else
            if (c == '&')
            {
                sb.append("\\&");
            }
            else
            if (c == '^')
            {
                sb.append("\\^");
            }
            else
            if (c == '#')
            {
                sb.append("\\#");
            }
            else
            if (c == '~')
            {
                sb.append("\\~");
            }
            else
            {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static
    String htmqSafe(String s)
    {
        assert s.indexOf("\n") == -1 : "Internal error: string should not contain newline \""+s+"\"";
        return "'"+s+"'";
    }

    public static String numQuotes(int n)
    {
        StringBuilder o = new StringBuilder();

        while (n > 0)
        {
            o.append("'");
            n--;
        }

        return o.toString();
    }

    public static String quoteXMQ(String s)
    {
        boolean must_quote = true;
        int num_quotes = 0;
        int max_quotes = 0;

        for (int i=0; i<s.length(); ++i)
        {
            char c = s.charAt(i);
            if (c == ' ' || c == '\'' || c == '(' || c == ')' || c == '{' || c == '}' || c == '\n' || c == '\t' || c == '=')
            {
                must_quote = true;
                if (c == '\'')
                {
                    num_quotes++;
                    if (num_quotes > max_quotes) max_quotes = num_quotes;
                }
                else
                {
                    num_quotes = 0;
                }
            }
        }

        if (must_quote)
        {
            String qs = numQuotes(max_quotes+1);
            return qs+s+qs;
        }
        return s;
    }

    public static String[] everySecond(String[] a)
    {
        String[] r = new String[a.length / 2];

        for (int i = 0; i < r.length; ++i)
        {
            r[i] = a[i*2];
        }
        return r;
    }
}
