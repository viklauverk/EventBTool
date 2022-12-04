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

import java.util.HashMap;

public class Meta
{
    final Bounds bounds;
    final ContainingCardinality storage;
    final Implementation imp;

    private Meta(Bounds bo, ContainingCardinality it, Implementation im)
    {
        bounds = bo;
        storage = it;
        imp = im;
    }

    public Meta parse(String s)
    {
        // Scalars
        //                     Bounds    | ContainingCardinality | ChoosenImplementation
        // x:1..22              1..22    | INT8                  | var    (normal c++ class member variable)
        // x:1..22              1..22    | INT8                  | ext   (Getter/setters must be specified for this variable)
        // x:1..22              1..22    | INT8                  | mem    (a memory location must be specified for this variable)
        // x:1..22              1..22    | INT8                  | sql    (the variable is stored in an sql table)
        // x:NAT                0..z     | Z                     | var    (a bigint type is stored)

        // Sets
        // x:1..1024-->0..255   100..1024 | INT16                | dom    (domain)
        //                      0..255    | INT8                 | ran    (range)
        //                      0..262143 | INT32                | vec/map/ext/mem/sql
        // y<:0..65535          0..ui16   | UINT16               | set func mem

        // num: 0..100
        // card(EXT) = 22
        // exts: 0..num <-> EXT    <0..2200|F16

        String[] parts = s.split("|");
        Bounds b = Bounds.parse(parts[0]);
        return new Meta(null,null,null);
    }

    public String toString()
    {
        if (imp == null)
        {
            return bounds+"|"+storage;
        }
        return bounds+"|"+storage+"|"+imp;
    }
}
