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

package com.viklauverk.evbt.common.cli;

public class Util
{
    public static String[] shiftArrayLeft(String[] args)
    {
        if (args.length <= 1) return new String[0];

        String[] tmp = new String[args.length-1];
        for (int i=0; i<args.length-1; ++i)
        {
            tmp[i] = args[i+1];
        }
        return tmp;
    }

    public static String padRight(String s, char c, int len)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(s);
        while (len > 0) { sb.append(c); len--; }
        return sb.toString();
    }

}
