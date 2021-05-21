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
import com.viklauverk.eventbtools.core.Sys;
import com.viklauverk.eventbtools.core.Type;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;

import java.util.Scanner;
import java.util.List;

public class RunConsole
{
    public static void run(Settings s) throws Exception
    {
        Sys sys = new Sys();

        sys.loadMachinesAndContexts(s.commonSettings().sourceDir());

        Canvas canvas = new Canvas();
        canvas.setRenderTarget(RenderTarget.TERMINAL);
        RenderAttributes ra = new RenderAttributes();
        ra.setColor(true);
        canvas.setRenderAttributes(ra);

        System.out.println("EVBT console 1.0.0");

        Completer completer = new ArgumentCompleter(
            new StringsCompleter("show", "show", "list", "read"),
            new StringsCompleter("formula", "framed"),
            new Completer() {
                @Override
                public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
                    candidates.add(new Candidate("", "", null, "frequency in MHz", null, null, false));
                }
            });
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
                String out = sys.console().go(line);
                System.out.print(out);
                if (!out.endsWith("\n")) System.out.println();
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
