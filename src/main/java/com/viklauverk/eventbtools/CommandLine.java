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

package com.viklauverk.eventbtools;

import com.viklauverk.eventbtools.core.Log;
import com.viklauverk.eventbtools.core.LogLevel;
import com.viklauverk.eventbtools.core.LogModule;
import com.viklauverk.eventbtools.core.ShowSettings;
import com.viklauverk.eventbtools.core.ModelGenSettings;
import com.viklauverk.eventbtools.core.ModelTarget;
import com.viklauverk.eventbtools.core.RenderTarget;
import com.viklauverk.eventbtools.core.Settings;
import com.viklauverk.eventbtools.core.Util;

import java.io.File;

public class CommandLine
{
    static Log log = LogModule.lookup("cmdline");

    public static CmdArgs parse(Settings s, String[] args)
    {
        if (args.length < 1)
        {
            return new CmdArgs(Cmd.HELP, args);
        }
        String cs = args[0];
        Cmd cmd = Cmd.ERROR;
        try
        {
            cmd = Cmd.valueOf(cs.toUpperCase());
        }
        catch (IllegalArgumentException e)
        {
            log.usageError("Unknown command \"%s\"", cs);
            return new CmdArgs(Cmd.ERROR, args);
        }

        args = Util.shiftLeft(args);

        switch (cmd)
        {
        case CODEGEN: args = parseCodeGen(s, args); break;
        case CONSOLE:  args = parseConsole(s, args); break;
        case DOCGEN: args = parseDocGen(s, args); break;
        case DOCMOD: args = parseDocMod(s, args); break;
        case MODELGEN: args = parseModelGen(s, args); break;
        case EDK: args = parseEDK(s, args); break;
        case SHOW:  args = parseShow(s, args); break;
        case HELP: args = parseHelp(s, args); break;
        case LICENSE: args = parseLicense(s, args); break;
        case VERSION: break;
        }
        return new CmdArgs(cmd, args);
    }

    private static String[] handleGlobalOption(Settings s, String[] args)
    {
        for (;;)
        {
            if (args.length == 0) return args;
            String arg = args[0];
            if (arg.equals("-q"))
            {
                LogModule.setLogLevelFor("all", LogLevel.WARN);
                s.commonSettings().quietEnabled(true);
                args = Util.shiftLeft(args);
                continue;
            }
            if (arg.equals("-v"))
            {
                LogModule.setLogLevelFor("all", LogLevel.VERBOSE);
                s.commonSettings().setVerboseEnabled(true);
                args = Util.shiftLeft(args);
                continue;
            }
            if (arg.equals("-bw"))
            {
                s.commonSettings().setBlackWhiteEnabled(true);
                args = Util.shiftLeft(args);
                continue;
            }
            if (arg.equals("-ll") || arg.equals("--listlog"))
            {
                LogModule.printLogModules();
                System.exit(1);
                continue;
            }
            if (arg.startsWith("--debug="))
            {
                String modules = arg.substring(8);
                LogModule.setLogLevelFor(modules, LogLevel.DEBUG);
                s.commonSettings().setDebugEnabled(true);
                args = Util.shiftLeft(args);
                continue;
            }
            if (arg.startsWith("--debug-canvas"))
            {
                LogModule.setDebugCanvas(true);
                args = Util.shiftLeft(args);
                continue;
            }
            if (arg.startsWith("--trace="))
            {
                String modules = arg.substring(8);
                LogModule.setLogLevelFor(modules, LogLevel.TRACE);
                s.commonSettings().setTraceEnabled(true);
                args = Util.shiftLeft(args);
                continue;
            }
            if (arg.startsWith("--theory-root-dir="))
            {
                String trd = arg.substring(18);
                s.commonSettings().setTheoryRootDir(trd);
                args = Util.shiftLeft(args);
                continue;
            }
            if (arg.startsWith("--outputdir="))
            {
                String outputdir = arg.substring(12);
                s.commonSettings().setOutputDir(outputdir);
                args = Util.shiftLeft(args);
                continue;
            }
            if (arg.startsWith("-"))
            {
                log.usageError("Unknown option "+arg);
            }
            break;
        }
        return args;
    }

    private static String[] parseCodeGen(Settings s, String[] args)
    {
        for (;;)
        {
            args = handleGlobalOption(s, args);
            if (args.length == 0) break;

            String arg = args[0];

            if (!arg.startsWith("--")) break;
        }

        if (args.length < 2)
        {
            log.usageError("Usage: evbt codegen [options] <format> <dir/machine.bum>");
            System.exit(1);
        }

        s.codeGenSettings().parseLanguage(args[0]);
        args = Util.shiftLeft(args);

        File file = new File(args[0]);
        if (!file.exists() || file.isDirectory() || !args[0].endsWith(".bum"))
        {
            log.usageError("Not a proper Event-B machine file: "+args[0]);
            System.exit(1);
        }

        File dir = file.getParentFile();
        s.commonSettings().setSourceDir(dir.getPath());
        args = Util.shiftLeft(args);

        String file_name = file.getName();
        String machine_name = file_name.substring(0, file_name.length() - 4);
        s.commonSettings().addMachineOrContext(machine_name);
        args = Util.shiftLeft(args);

        return args;
    }

    private static String[] parseConsole(Settings s, String[] args)
    {
        for (;;)
        {
            args = handleGlobalOption(s, args);
            if (args.length == 0) break;
            String arg = args[0];

            if (!arg.startsWith("--")) break;

            LogModule.usageErrorStatic("Unknown option \"%s\"", arg);

            break;
        }

        if (args.length == 0) return args;

        File dir = new File(args[0]);
        if (dir.isDirectory())
        {
            s.commonSettings().setSourceDir(args[0]);
            args = Util.shiftLeft(args);
        }

        return args;
    }

    private static boolean parseDocStyle(Settings s, String arg)
    {
        if (arg.startsWith("--docstyle="))
        {
            String style = arg.substring(11);
            log.debug("doc style \"%s\"", style);
            log.debug("style render attributes before set "+s.docGenSettings().renderAttributes());
            boolean ok = s.docGenSettings().renderAttributes().setStyle(style);
            log.debug("style render attributes "+s.docGenSettings().renderAttributes());
            if (!ok)
            {
                log.error("Could not parse document style: "+style);
            }
            return true;
        }
        return false;
    }

    private static String[] parseDocGen(Settings s, String[] args)
    {
        for (;;)
        {
            args = handleGlobalOption(s, args);
            if (args.length == 0) break;

            String arg = args[0];

            if (!arg.startsWith("--")) break;

            if (parseDocStyle(s, arg))
            {
                args = Util.shiftLeft(args);
                continue;
            }

            LogModule.usageErrorStatic("Unknown option \"%s\"", arg);
        }

        if (args.length < 2)
        {
            log.usageError("Usage: evbt docgen [options] <plain|tex|htmq> <dir> <machine|context>*");
            System.exit(1);
        }

        RenderTarget rt = RenderTarget.lookup(args[0]);
        if (rt == null)
        {
            log.usageError("Not a supported render target \""+args[0]+"\", available targets are: plain, terminal, tex and htmq.");
        }

        s.docGenSettings().setRenderTarget(rt);
        args = Util.shiftLeft(args);

        s.commonSettings().setSourceDir(args[0]);
        args = Util.shiftLeft(args);

        while(args.length > 0)
        {
            s.commonSettings().addMachineOrContext(args[0]);
            args = Util.shiftLeft(args);
        }

        return args;
    }

    private static String[] parseDocMod(Settings s, String[] args)
    {
        for (;;)
        {
            args = handleGlobalOption(s, args);
            if (args.length == 0) break;

            String arg = args[0];

            if (!arg.startsWith("--")) break;

            if (parseDocStyle(s, arg))
            {
                args = Util.shiftLeft(args);
                continue;
            }

            LogModule.usageErrorStatic("Unknown option \"%s\"", arg);
        }

        if (args.length < 3)
        {
            log.usageError("Usage: evbt docmod [options] <tex|htmq> <source_file> <dest_file> { <dir> } ");
            System.exit(1);
        }

        RenderTarget rt = RenderTarget.lookup(args[0]);
        if (rt == null)
        {
            log.usageError("Not a supported render target \""+args[0]+"\", available targets are: plain, terminal, tex and htmq.");
        }

        s.docGenSettings().setRenderTarget(rt);
        args = Util.shiftLeft(args);

        s.commonSettings().setSourceFile(args[0]);
        args = Util.shiftLeft(args);

        s.commonSettings().setDestFile(args[0]);
        args = Util.shiftLeft(args);

        if (args.length == 1)
        {
            s.commonSettings().setSourceDir(args[0]);
            args = Util.shiftLeft(args);
        }
        else if (args.length != 0)
        {
            log.usageError("Usage: evbt docmod [options] <plain|tex|htmq> <source_file> <dest_file> { <dir> } ");
            System.exit(1);
        }

        return args;
    }

    private static String[] parseModelGen(Settings s, String[] args)
    {
        for (;;)
        {
            args = handleGlobalOption(s, args);
            if (args.length == 0) break;

            String arg = args[0];

            if (!arg.startsWith("--")) break;

            LogModule.usageErrorStatic("Unknown option \"%s\"", arg);
        }

        if (args.length < 2)
        {
            log.usageError("Usage: evbt modelgen [options] <why3> <dir>");
            System.exit(1);
        }

        ModelTarget mt = ModelTarget.lookup(args[0]);
        if (mt == null)
        {
            log.usageError("Not a supported model target \""+args[0]+"\", available targets are: why3");
        }

        s.modelGenSettings().setModelTarget(mt);
        args = Util.shiftLeft(args);

        s.commonSettings().setSourceDir(args[0]);
        args = Util.shiftLeft(args);

        while(args.length > 0)
        {
            s.commonSettings().addMachineOrContext(args[0]);
            args = Util.shiftLeft(args);
        }

        return args;
    }

    private static String[] parseEDK(Settings s, String[] args)
    {
        for (;;)
        {
            args = handleGlobalOption(s, args);
            if (args.length == 0) break;

            String arg = args[0];

            if (!arg.startsWith("--")) break;

            LogModule.usageErrorStatic("Unknown option \"%s\"", arg);
        }

        if (args.length < 1)
        {
            log.usageError("Usage: evbt edk <dir>");
            System.exit(1);
        }

        s.commonSettings().setOutputDir(args[0]);
        args = Util.shiftLeft(args);

        if (args.length != 0)
        {
            log.usageError("Usage: evbt edk [options] <dir>");
            System.exit(1);
        }

        return args;
    }

    private static String[] parseHelp(Settings s, String[] args)
    {
        for (;;)
        {
            args = handleGlobalOption(s, args);
            if (args.length == 0) break;
            String arg = args[0];

            if (!arg.startsWith("--")) break;

            LogModule.usageErrorStatic("Unknown option \"%s\"", arg);

            break;
        }

        return args;
    }

    private static String[] parseLicense(Settings s, String[] args)
    {
        for (;;)
        {
            args = handleGlobalOption(s, args);
            if (args.length == 0) break;
            String arg = args[0];

            if (!arg.startsWith("--")) break;

            LogModule.usageErrorStatic("Unknown option \"%s\"", arg);

            break;
        }

        return args;
    }

    private static String[] parseShow(Settings s, String[] args)
    {
        for (;;)
        {
            args = handleGlobalOption(s, args);
            if (args.length == 0) break;
            String arg = args[0];

            if (parseDocStyle(s, arg))
            {
                args = Util.shiftLeft(args);
                continue;
            }
            if (arg.startsWith("--parts="))
            {
                String parts = arg.substring(8);
                log.debug("showing parts \"%s\"", parts);
                s.showSettings().parseParts(parts);
                args = Util.shiftLeft(args);
                continue;
            }
            if (arg.startsWith("--layout=h,120"))
            {
                s.showSettings().setHorizontalLayout(true);
                s.showSettings().setHorizontalWidth(120);
                args = Util.shiftLeft(args);
                continue;
            }
            break;
        }

        if (args.length < 1)
        {
            log.usageError("Usage: evbt show <source_dir> <part_identifier>*");
            System.exit(1);
        }

        s.commonSettings().setSourceDir(args[0]);
        args = Util.shiftLeft(args);

        while(args.length > 0)
        {
            s.showSettings().addPartIdentifier(args[0]);
            args = Util.shiftLeft(args);
        }

        return args;
    }


}
