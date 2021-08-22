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

package com.viklauverk.eventbtools;

import java.util.Set;

import com.viklauverk.eventbtools.core.BaseCodeGen;
import com.viklauverk.eventbtools.core.CodeGen;
import com.viklauverk.eventbtools.core.Log;
import com.viklauverk.eventbtools.core.LogModule;
import com.viklauverk.eventbtools.core.Machine;
import com.viklauverk.eventbtools.core.ProgrammingLanguage;
import com.viklauverk.eventbtools.core.Settings;
import com.viklauverk.eventbtools.core.Sys;
import com.viklauverk.eventbtools.core.Typing;

public class RunCodeGen
{
    public static Log log = LogModule.lookup("codegen");

    public static void run(Settings s)
        throws Exception
    {
        Sys sys = new Sys(s);
        sys.loadMachinesAndContexts(s.commonSettings().sourceDir());

        for (String m : s.commonSettings().machinesAndContexts())
        {
            Machine mch = sys.getMachine(m);
            if (mch == null)
            {
                LogModule.usageErrorStatic("No such machine \"%s\"", m);
            }
            mch.buildImplementation();

            BaseCodeGen gen = CodeGen.lookup(s.commonSettings(), s.codeGenSettings(), sys, mch);
            gen.printAssumptions();
            gen.run();
        }

        if (log.verboseEnabled())
        {
            Set<String> types = sys.typing().checkedTypeNames();

            log.verbose("Found distinct checked types:");
            for (String t : types)
            {
                log.verbose(t);
            }
            log.verbose("");

            types = sys.typing().implTypeNames();

            log.verbose("Found distinct implementation types:");
            for (String t : types)
            {
                log.verbose(t);
            }
            log.verbose("");
        }
    }
}
