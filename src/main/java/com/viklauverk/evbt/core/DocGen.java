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

package com.viklauverk.evbt.core;

public class DocGen
{
    static Log log = LogModule.lookup("docgen");

    public static BaseDocGen lookup(CommonSettings cs, DocGenSettings ds, Sys sys)
    {
        switch (ds.renderTarget())
        {
        case PLAIN:
            return new DocGenUnicode(cs, ds, sys);
        case TERMINAL:
            return new DocGenUnicode(cs, ds, sys);
        case TEX:
            return new DocGenTeX(cs, ds, sys);
        case HTML:
            return new DocGenHtmq(cs, ds, sys);
        }
        assert (false) : "Missing case for render target: "+ds.renderTarget();
        return null;
    }
}
