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

package com.viklauverk.evbt.ext;

import com.viklauverk.common.log.Log;
import com.viklauverk.common.log.LogModule;

import java.io.IOException;
import java.util.ArrayList;

public class LineEditor
{
    private void debug(String s)
    {

    }

    private static String MoveOneRight="\033[1C";
    private static String MoveOneLeft="\033[1D";
    private static String ClearLineMarkerToEnd="\033[0K";
    private static String GotoColumn1="\033[1G";

    private static String gotoX(int x) { return "\033["+x+"G"; }

    // Arrow up:    27 91 65
    // Arrow down:  27 91 66
    // Arrow right: 27 91 67
    // Arrow left:  27 91 68

    String visible = "";
    String next = "";
    int x = 0; // Cursor position 0 is the leftmost column.
    int min_x = 0; // Leftmost allowed cursor position.
    boolean inserting = true;
    ArrayList<String> history = new ArrayList<>();
    int hp = 0;

    void typeAtX(char c)
    {
        debug("typeAtX x="+x+" c="+c+" visible="+visible+" next="+next+"\n");
        if (inserting)
        {
            if (x == 0)
            {
                next = c+next;
            }
            else if (x == visible.length())
            {
                next = next+c;
            }
            else
            {
                next = next.substring(0, x) + c + next.substring(x);
            }
        }
        else
        {

        }
        x++;
    }

    void backspace()
    {
        debug("\nbackspace x="+x+" visible="+visible+" next="+next+"\n");
        if (x > min_x)
        {
            next = next.substring(0, x-1) + next.substring(x);
            x--;
        }
    }

    void tabComplete()
    {
        String[] words = visible.split(" ");
        System.out.println("");
        for (String w  : words)
        {
            System.out.println("W="+w);
        }
    }

    void redraw()
    {
        update();
        visible = next;
    }

    void update()
    {
        if (visible.equals(next))
        {
            // What is displayed is already equal the next line. Do nothing.
            return;
        }
        if (next.length() == 0)
        {
            // The new line is empty, clear it.
            System.out.print(GotoColumn1);
            System.out.print(ClearLineMarkerToEnd);
            return;
        }
        System.out.print(GotoColumn1);
        System.out.print(ClearLineMarkerToEnd);
        System.out.print(next);
        System.out.print(gotoX(x+1)); // Ansi starts counting from 1.
    }

    public String nextLine(String prefix)
    {
        int c = 0;
        visible = next = prefix;
        System.out.print(prefix);
        x = prefix.length();
        min_x = prefix.length();
        for (;;)
        {
            try
            {
                c = RawConsoleInput.read(true);
                if (c == 3 || c == 10 || c == 0) break;
                if (c == 27)
                {
                    c = RawConsoleInput.read(false);
                    if (c == 91)
                    {
                        c = RawConsoleInput.read(false);
                        if (c == 68)
                        {
                            // Arrow left
                            if (x > min_x)
                            {
                                System.out.print(MoveOneLeft);
                                x--;
                            }
                            continue;
                        }
                        if (c == 67)
                        {
                            // Arrow right
                            if (x < visible.length())
                            {
                                System.out.print(MoveOneRight);
                                x++;
                            }
                            continue;
                        }
                        if (c == 65)
                        {
                            // Arrow up
                            continue;
                        }
                    }
                }
                if (c == 9)
                {
                    tabComplete();
                }
                if (c == 127 || c == 8)
                {
                    backspace();
                }
                else
                {
                    if (c >= 32)
                    {
                        typeAtX((char)c);
                    }
                }
                redraw();
            }
            catch (IOException e)
            {
            }
        }
        if (c == 0 || c == 3)
        {
            return "";
        }
        if (c == 10 || c == 3 || c == 0) System.out.println();

        return next.substring(prefix.length());
    }
}
