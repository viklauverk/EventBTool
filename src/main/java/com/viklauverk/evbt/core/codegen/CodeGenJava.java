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

package com.viklauverk.evbt.core.codegen;

import com.viklauverk.evbt.core.CommonSettings;
import com.viklauverk.evbt.core.sys.Machine;
import com.viklauverk.evbt.core.sys.Sys;

public class CodeGenJava extends BaseCodeGen
{
    public CodeGenJava(CommonSettings s1, CodeGenSettings s2, Sys sys, Machine mch)
    {
        super(s1, s2, sys, mch);
    }

    public void run() throws Exception
    {
    }


}
