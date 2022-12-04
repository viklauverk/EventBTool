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

public enum RenderTarget
{
    PLAIN,    // Render using plain unicode (can even be limited to ASCII)
    TERMINAL, // Use PLAIN but allow ansi-sequences for colors and positioning.
    TEX,      // Use PLAIN but use tex for symbols, colors and positioning.
    HTMQ;     // Use PLAIN but use htmq that can later be converted into html.

    public static RenderTarget lookup(String s)
    {
        switch (s)
        {
        case "plain": return PLAIN;
        case "terminal": return TERMINAL;
        case "tex": return TEX;
        case "htmq": return HTMQ;
        }

        return null;
    }
}
