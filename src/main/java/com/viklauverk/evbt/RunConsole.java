/*
 Copyright (C) 2021-2024 Viklauverk AB (agpl-3.0-or-later)

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

import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;

import com.viklauverk.evbt.core.AbortIntoConsole;
import com.viklauverk.evbt.core.EvbtConsoleCompleter;
import com.viklauverk.evbt.core.Settings;
import com.viklauverk.evbt.core.console.Canvas;
import com.viklauverk.evbt.core.docgen.RenderAttributes;
import com.viklauverk.evbt.core.docgen.RenderTarget;
import com.viklauverk.evbt.core.log.Log;
import com.viklauverk.evbt.core.log.LogModule;
import com.viklauverk.evbt.core.sys.Sys;

public class RunConsole
{
    private static Log log = LogModule.lookup("console", RunConsole.class);

    public static void run(CmdArgs ca, Settings s) throws Exception
    {
        Sys sys = new Sys(s);

        if (s.commonSettings().sourceDir() != null)
        {
            System.out.print(String.format("Loading machines, contexts and theories from: %s with additional theories from: %s\n",
                                           s.commonSettings().sourceDir(),
                                           s.commonSettings().theoryRootDir()));
        }
        try
        {
            sys.loadTheoriesAndContextsAndMachines(s.commonSettings().sourceDir(), s.commonSettings().theoryRootDir());
        }
        catch (AbortIntoConsole e)
        {
            sys.console().forceCurrentSymbolTable(e.symbolTable());
            System.out.println(e.formula());
        }
        Canvas canvas = new Canvas();
        canvas.setRenderTarget(RenderTarget.TERMINAL);
        RenderAttributes ra = sys.console().renderAttributes();
        ra.setColor(true);
        canvas.setRenderAttributes(ra);

        log.info("EVBT console 2.0.0");

        Completer completer = EvbtConsoleCompleter.addCompleters(sys);
        LineReader reader = LineReaderBuilder
            .builder()
            .completer(completer)
            .build();
        String prompt = "evbt>";

        int i = 0;
        while (sys.console().running())
        {
            String line = null;
            try
            {
                if (i >= ca.args.length)
                {
                    line = reader.readLine(prompt);
                }
                else
                {
                    line = ca.args[i];
                    i++;
                }
                if (line.trim().length() > 0)
                {
                    String out = sys.console().go(line);
                    if (s.commonSettings().quietEnabled() && out.equals("OK"))
                    {
                        // Be silent.
                    }
                    else
                    {
                        System.out.print(out);
                        if (out.length() > 0 && !out.endsWith("\n")) System.out.println();
                    }
                }
            }
            catch (UserInterruptException e)
            {
                break;
            }
            catch (EndOfFileException e)
            {
                break;
            }
        }
    }
}
