/*
 Copyright (C) 2021-2024 Viklauverk AB

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

package com.viklauverk.evbt;

import com.viklauverk.evbt.core.Log;
import com.viklauverk.evbt.core.LogLevel;
import com.viklauverk.evbt.core.LogModule;
import com.viklauverk.evbt.core.Settings;
import com.viklauverk.evbt.core.Version;

public class Main
{
    private static Log log = LogModule.lookup("main");

    public static void main(String[] args)
    {
        try
        {
            Settings s = new Settings();
            CmdArgs ca = CommandLine.parse(s, args);
            log.debug("debug output enabled for: %s", LogModule.debugEnabledModules());
            run(ca, s);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void run(CmdArgs ca, Settings s) throws Exception
    {
        switch (ca.cmd)
        {
        case CODEGEN:
            RunCodeGen.run(ca, s);
            break;
        case MODELGEN:
            RunModelGen.run(ca, s);
            break;
        case CONSOLE:
            RunConsole.run(ca, s);
            break;
        case DOCGEN:
            RunDocGen.run(ca, s);
            break;
        case DOCMOD:
            RunDocMod.run(ca, s);
            break;
        case EDK:
            RunEDK.run(ca, s);
            break;
        case SHOW:
            RunShow.run(ca, s);
            break;
        case HELP:
            printHelp(s);
            return;
        case LICENSE:
            printLicense();
            break;
        case VERSION:
            printVersion();
            break;
        }
    }

    public static void printVersion()
        throws Exception
    {
        System.out.println("evbt version: "+Version.version);
        System.out.println(Version.commit);
    }

    public static void printHelp(Settings s)
        throws Exception
    {
        System.out.println("evbt version: "+Version.version);
        System.out.println("usage: evbt <command> [options] <args>");
        System.out.println("");
        System.out.println("Available commands:");
        System.out.println("  codegen  [options] <language> <dir>/<machine>");
        System.out.println("  modelgen [options] <model> <dir>/<machine>");
        System.out.println("  docgen   [options] <format> <dir>");
        System.out.println("  docmod   [options] <format> <source_file> <dest_file> { <dir> }");
        System.out.println("  edk      [options] <dir>");
        System.out.println("  show     [options] <dir> <part_identifier>*");
        System.out.println("  console  [options] { <dir> }");
        System.out.println("  help");
        System.out.println("  license");
        System.out.println("  version");
        System.out.println("  nowarranty");
        System.out.println("");
        System.out.println("A <part_identifier> is for example just a machine name, like Elevator,");
        System.out.println("or more specific like Elevator/moveUp/grd_02 or FloorContext/axm_03");
        System.out.println("");
        System.out.println("Common options for all commands:");
        System.out.println("  -q  No output except errors.");
        System.out.println("  -v  Verbose output.");
        System.out.println("  --outputdir=<dir>");
        if (s.commonSettings().verboseEnabled())
        {
            System.out.println("  --debug=all Debug log for all modules.");
            System.out.println("  --trace=all Trace log for all modules.");
        }
        System.out.println("");
        System.out.println("Copyright (C) 2021-2023 Viklauverk AB");
        System.out.println("Licensed to you under the GNU Affero General Public License 3.");
        System.out.println("");
    }

    static void printLicense()
    {
        String l =
            "Event-B Tools (evbt)\n"+
            "Copyright (C) 2021-2023 Viklauverk AB\n"+
            "\n"+
            "This program is free software: you can redistribute it and/or modify\n"+
            "it under the terms of the GNU Affero General Public License as published by\n"+
            "the Free Software Foundation, either version 3 of the License, or\n"+
            "(at your option) any later version.\n"+
            "\n"+
            "This program is distributed in the hope that it will be useful,\n"+
            "but WITHOUT ANY WARRANTY; without even the implied warranty of\n"+
            "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"+
            "GNU Affero General Public License for more details.\n"+
            "\n"+
            "You should have received a copy of the GNU Affero General Public License\n"+
            "along with this program.  If not, see <http://www.gnu.org/licenses/>.\n"+
            "\n"+
            "Note, that since this tool can be used to generate code from Event-B sources,\n"+
            "it can insert code that is licensed under this license or other licenses.\n"+
            "Check the generated code for statements on the additional licenses.\n";

        System.out.print(l);
    }

}
