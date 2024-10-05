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
// All rights reserved! Do not share!

package com.viklauverk.evbt.core;

import com.viklauverk.evbt.core.Log;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class LogModule implements Log
{
    private static boolean initialized_ = false;
    private static Map<String,LogModule> modules_ = new HashMap<>();
    private static LogLevel all_ = LogLevel.INFO;
    private static Map<LogModuleNames,LogLevel> module_levels_ = new HashMap<>();
    private static Set<String> expected_modules_ = new HashSet<>();
    private static boolean debug_enabled_ = true;
    private static boolean debug_canvas_enabled_ = false;

    private LogModuleNames module_;
    private LogLevel log_level_;

    static
    {
        for (LogModuleNames lm : LogModuleNames.values())
        {
            String name = lm.toString();
            expected_modules_.add(name);
            modules_.put(name, new LogModule(name));
        }
    }

    private LogModule(String name)
    {
        log_level_ = LogLevel.INFO;

        try
        {
            module_  = LogModuleNames.valueOf(name);
        }
        catch (IllegalArgumentException e)
        {
            LogModule.usageErrorStatic("Unknown log module \"%s\"", name);
        }
    }


    private void evalLogLevel()
    {
        log_level_ = module_levels_.get(module_);
        if (log_level_ == null)
        {
            log_level_ = all_;
        }
        if (log_level_ == LogLevel.DEBUG ||
            log_level_ == LogLevel.TRACE)
        {
            debug_enabled_ = true;
        }
    }

    public static boolean debugEnabledSomewhere()
    {
        return debug_enabled_;
    }

    /**
       Fetch the log module for a given name, like prover, machine, codegen, etc.
     */
    public static synchronized LogModule lookup(String name)
    {
        LogModule lm = modules_.get(name);
        if (lm == null)
        {
            internalErrorStatic("Log module %s is not expected! Rerun \"make logmodulenames\" to build again.", name);
        }
        return lm;
    }

    public static void setLogLevelFor(String modules, LogLevel ll)
    {
        String[] ms = modules.split(",", -1);
        for (String m : ms)
        {
            LogLevel tmp = ll;
            if (m.equals("") || m.equals("-") || m.equals("-all"))
            {
                usageErrorStatic("Bad log module \""+m+"\"");
            }
            if (m.charAt(0) == '-')
            {
                tmp = LogLevel.INFO;
            }
            if (m.equals("all"))
            {
                all_ = tmp;
            }
            else
            {
                LogModule lm = lookup(m);
                if (!expected_modules_.contains(m))
                {
                    internalErrorStatic("Unknown log module \"%s\"", m);
                }
                LogModuleNames module = LogModuleNames.valueOf(m);
                module_levels_.put(module, ll);
            }
        }
        for (LogModule lm : modules_.values())
        {
            lm.evalLogLevel();
        }
    }

    static String safeFormat(String msg, Object... args)
    {
        try
        {
            return String.format(msg, args);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        return "ERROR_FORMATTING "+msg;
    }

    public void error(String msg, Object... args)
    {
        String m = "("+module_+") "+safeFormat(msg, args);
        System.err.println(m);
        System.exit(1);
    }

    public void usageError(String msg, Object... args)
    {
        String out = safeFormat(msg, args);
        System.out.println(out);
        System.exit(1);
    }

    public static void usageErrorStatic(String msg, Object... args)
    {
        String out = safeFormat(msg, args);
        System.out.println(out);
        System.exit(1);
    }

    public void internalError(String msg, Object... args)
    {
        String out = "("+module_+") internal error "+safeFormat(msg, args);
        System.out.println(out);
        System.exit(1);
    }

    private static void internalErrorStatic(String msg, Object... args)
    {
        String out = "(log) internal error "+safeFormat(msg, args);
        System.out.println(out);
        System.exit(1);
    }

    public void failure(String msg, Object... args)
    {
        String out = "("+module_+") failure "+safeFormat(msg, args);
        System.out.println(out);
    }

    public void warn(String msg, Object... args)
    {
        if (log_level_.value() >= LogLevel.WARN.value())
        {
            String out = "("+module_+") warning "+safeFormat(msg, args);
            System.out.println(out);
        }
    }

    public void info(String msg, Object... args)
    {
        if (log_level_.value() >= LogLevel.INFO.value())
        {
            String out = safeFormat(msg, args);
            System.out.println(out);
        }
    }

    public void verbose(String msg, Object... args)
    {
        if (log_level_.value() >= LogLevel.VERBOSE.value())
        {
            String out = safeFormat(msg, args);
            System.out.println(out);
        }
    }

    public void debug(String msg, Object... args)
    {
        if (log_level_.value() >= LogLevel.DEBUG.value())
        {
            String out = "("+module_+") "+safeFormat(msg, args);
            System.out.println(out);
        }
    }

    public void debugp(String part, String msg, Object... args)
    {
        if (log_level_.value() >= LogLevel.DEBUG.value())
        {
            String out = "("+module_+"-"+part+") "+safeFormat(msg, args);
            System.out.println(out);
        }
    }

    public void trace(String msg, Object... args)
    {
        if (log_level_.value() >= LogLevel.TRACE.value())
        {
            String out = "("+module_+") "+safeFormat(msg, args);
            System.out.println(out);
        }
    }

    public boolean verboseEnabled()
    {
        return log_level_.value() >= LogLevel.VERBOSE.value();
    }

    public boolean debugEnabled()
    {
        return log_level_.value() >= LogLevel.DEBUG.value();
    }

    public boolean traceEnabled()
    {
        return log_level_.value() >= LogLevel.TRACE.value();
    }

    public boolean debugCanvasEnabled()
    {
        return debug_canvas_enabled_;
    }

    public static void setDebugCanvas(boolean e)
    {
        debug_canvas_enabled_ = e;
    }

    public static void printLogModules()
    {
        for (LogModuleNames lm : LogModuleNames.values())
        {
            System.out.println(lm);
        }
    }

    public static String debugEnabledModules()
    {
        StringBuilder sb = new StringBuilder();
        for (String name : modules_.keySet())
        {
            LogModule lm = modules_.get(name);
            if (lm.log_level_.value() <= LogLevel.DEBUG.value())
            {
                sb.append(name+" ");
            }
        }
        return sb.toString().trim();
    }

}
