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

import java.io.File;

public class EDK
{
    private static Log log = LogModule.lookup("edk");

    Sys sys_;

    public EDK(Sys s)
    {
        sys_ = s;
    }

    public EDKContext lookupContext(String name, File buc)
    {
        if (name.equals("EDK_FloatingPoint_v1"))
        {
            return new EDK_FloatingPoint_v1_Cpp(this);
        }
        if (name.equals("EDK_String_v1"))
        {
            return new EDK_String_v1_Cpp(this);
        }
        return null;
    }

    public EDKTheory lookupTheory(String name, File tuf)
    {
        return null;
    }

}
