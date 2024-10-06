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

package com.viklauverk.evbt.core;

import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.builtins.Completers.TreeCompleter;
import org.jline.builtins.Completers.DirectoriesCompleter;
import org.jline.reader.Completer;
import java.nio.file.Paths;

import com.viklauverk.evbt.core.cmd.Cmd;

public class ConsoleCompleter
{
    public static Completer addCompleters(Sys sys) {
        return new AggregateCompleter(
             new ArgumentCompleter(new StringsCompleter("ca.attributes")),
             new ArgumentCompleter(new StringsCompleter("ca.pop")),
             new ArgumentCompleter(new StringsCompleter("ca.push")),
             new ArgumentCompleter(new StringsCompleter("ca.set.width")),
             new ArgumentCompleter(new StringsCompleter("ca.target")),
             new ArgumentCompleter(new StringsCompleter("co.show.formula")),
             new ArgumentCompleter(new StringsCompleter("co.show.part")),
             new ArgumentCompleter(new StringsCompleter("sys.ls.machines")),
             new ArgumentCompleter(new StringsCompleter("sys.ls.contexts")),
             new ArgumentCompleter(new StringsCompleter("sys.ls.data-types")),
             new ArgumentCompleter(new StringsCompleter("sys.ls.hyps")),
             new ArgumentCompleter(new StringsCompleter("sys.ls.parts")),
             new ArgumentCompleter(new StringsCompleter("sys.ls.tables")),
             new ArgumentCompleter(new StringsCompleter("sys.ls.seen-theories")),
             new ArgumentCompleter(new StringsCompleter("sys.ls.specialised-data-types")),
             new ArgumentCompleter(
                 new StringsCompleter("sys.read"),
                 new DirectoriesCompleter(Paths.get("."))
                 ),
             new ArgumentCompleter(
                 new StringsCompleter("sys.print.template"),
                 new StringsCompleter(Util.everySecond(Templates.templates))
                 ),
             new ArgumentCompleter(new StringsCompleter("sys.set.default.hiding")),
             new ArgumentCompleter(new StringsCompleter("sys.set.default.renderTarget")),
             new ArgumentCompleter(
                 new StringsCompleter("help"),
                 new StringsCompleter(Cmd.getNames())
                 ),
             new ArgumentCompleter(new StringsCompleter("history")),
             new ArgumentCompleter(new StringsCompleter("ir.show.formula")),
             new ArgumentCompleter(new StringsCompleter("ir.show.part")),
             new ArgumentCompleter(new StringsCompleter("mo.show.formula")),
             new ArgumentCompleter(new StringsCompleter("mo.show.part")),
             new ArgumentCompleter(new StringsCompleter("eb.show.current.table")),
             new ArgumentCompleter(new StringsCompleter("eb.show.formula")),
             new ArgumentCompleter(
                 new StringsCompleter("eb.show.part"),
                 new SysCompleter(sys, false, false, false, true)
                 ),
             new ArgumentCompleter(
                 new StringsCompleter("eb.show.table"),
                 new SysCompleter(sys, false, false, true, false)
                 ),
             new ArgumentCompleter(new StringsCompleter("util.match")),
             new ArgumentCompleter(new StringsCompleter("yms.add.anys")),
             new ArgumentCompleter(new StringsCompleter("yms.add.constants")),
             new ArgumentCompleter(new StringsCompleter("yms.add.expressions")),
             new ArgumentCompleter(new StringsCompleter("yms.add.numbers")),
             new ArgumentCompleter(new StringsCompleter("yms.add.datatypes")),
             new ArgumentCompleter(new StringsCompleter("yms.add.operators")),
             new ArgumentCompleter(new StringsCompleter("yms.add.predicates")),
             new ArgumentCompleter(new StringsCompleter("yms.add.sets")),
             new ArgumentCompleter(new StringsCompleter("yms.add.variables")),
             new ArgumentCompleter(new StringsCompleter("yms.add.defaults")),
             new ArgumentCompleter(new StringsCompleter("yms.lookup")),
             new ArgumentCompleter(new StringsCompleter("yms.pop")),
             new ArgumentCompleter(new StringsCompleter("yms.push")),
             new ArgumentCompleter(new StringsCompleter("yms.show")),
             new ArgumentCompleter(new StringsCompleter("quit"))
        );
    }
}
