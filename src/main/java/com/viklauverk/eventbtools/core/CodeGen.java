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

public class CodeGen
{
    static Log log = LogModule.lookup("codegen");

    public static BaseCodeGen lookup(CommonSettings cs, CodeGenSettings cgs, Sys sys, Machine mch)
    {
        switch (cgs.language())
        {
        case CPP:
            return new CodeGenCpp(cs, cgs, sys, mch);
        case JAVA:
            return new CodeGenJava(cs, cgs, sys, mch);
        case JAVASCRIPT:
            return new CodeGenJavascript(cs, cgs, sys, mch);
        case SQL:
            return new CodeGenSQL(cs, cgs, sys, mch);
        }
        return null;
    }
}
