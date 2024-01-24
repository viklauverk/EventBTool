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

import com.viklauverk.eventbtools.core.Machine;
import com.viklauverk.eventbtools.core.Sys;

import com.viklauverk.eventbtools.core.Canvas;
import com.viklauverk.eventbtools.core.Console;
import com.viklauverk.eventbtools.core.ConsoleException;
import com.viklauverk.eventbtools.core.Log;
import com.viklauverk.eventbtools.core.LogLevel;
import com.viklauverk.eventbtools.core.LogModule;
import com.viklauverk.eventbtools.core.Machine;
import com.viklauverk.eventbtools.core.FormulaBuilder;
import com.viklauverk.eventbtools.core.Formula;
import com.viklauverk.eventbtools.core.Typing;
import com.viklauverk.eventbtools.core.SymbolTable;
import com.viklauverk.eventbtools.core.Prover;
import com.viklauverk.eventbtools.core.RenderAttributes;
import com.viklauverk.eventbtools.core.RenderTarget;
import com.viklauverk.eventbtools.core.Settings;
import com.viklauverk.eventbtools.core.Sys;
import com.viklauverk.eventbtools.core.ImplType;

import com.viklauverk.eventbtools.core.ConsoleCompleter;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.builtins.Completers.TreeCompleter;
import org.jline.builtins.Completers.DirectoriesCompleter;

import java.util.Scanner;
import java.util.List;
import java.nio.file.Paths;

public class RunConsole
{
    public static void run(Settings s) throws Exception
    {
        Sys sys = new Sys(s);

        sys.loadMachinesAndContexts(s.commonSettings().sourceDir());

        Canvas canvas = new Canvas();
        canvas.setRenderTarget(RenderTarget.TERMINAL);
        RenderAttributes ra = sys.console().renderAttributes();
        ra.setColor(true);
        canvas.setRenderAttributes(ra);

        System.out.println("EVBT console 2.0.0");

        Completer completer = ConsoleCompleter.addCompleters(sys);
        /*
            new AggregateCompleter(
                new ArgumentCompleter(
                    new StringsCompleter("env.read"),
                    new DirectoriesCompleter(Paths.get("."))),

                new ArgumentCompleter(
                    new StringsCompleter("env.ls"),
                    new DirectoriesCompleter(Paths.get("."))),

                new ArgumentCompleter(
                    new StringsCompleter("ta.add.defaults")),

                new ArgumentCompleter(
                    new StringsCompleter("ta.push")));
*/

        //new DirectoriesCompleter(Paths.get("."));
        /*new TreeCompleter(
                node("sys",
                node("read", */
        //new StringsCompleter("quit", "help", "history", "sys", "util", "ca", "st", "sp", "mo", "ir", "co");

        /*
            new Completer() {
                @Override
                public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
                    candidates.add(new Candidate("", "", null, "frequency in MHz", null, null, false));
                }
            });
            */
        LineReader reader = LineReaderBuilder
            .builder()
            .completer(completer)
            .build();
        String prompt = "evbt>";

        while (sys.console().running())
        {
            String line = null;
            try
            {
                line = reader.readLine(prompt);
                if (line.trim().length() > 0)
                {
                    String out = sys.console().go(line);
                    System.out.print(out);
                    if (!out.endsWith("\n")) System.out.println();
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
