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

import java.util.Map;
import java.util.HashMap;

public class Symbols
{
    private static Map<String,Integer> sym_to_int_ = new HashMap<>();
    private static Map<Integer,String> int_to_sym_ = new HashMap<>();
    private static int num_sym_ = 0;

    public static String name(int s)
    {
        return int_to_sym_.get(s);
    }

    public static synchronized int intern(String sym)
    {
        Integer i = sym_to_int_.get(sym);
        if (i == null)
        {
            int n = num_sym_;
            num_sym_++;
            sym_to_int_.put(sym, n);
            int_to_sym_.put(n, sym);
            return n;
        }
        return i;
    }

}
