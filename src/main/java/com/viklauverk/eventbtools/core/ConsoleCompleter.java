// Copyright Viklauverk AB 2021-2023
package com.viklauverk.eventbtools.core;

import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.builtins.Completers.TreeCompleter;
import org.jline.builtins.Completers.DirectoriesCompleter;
import org.jline.reader.Completer;
import java.nio.file.Paths;

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
             new ArgumentCompleter(new StringsCompleter("env.ls.machines")),
             new ArgumentCompleter(new StringsCompleter("env.ls.contexts")),
             new ArgumentCompleter(new StringsCompleter("env.ls.hyps")),
             new ArgumentCompleter(new StringsCompleter("env.ls.parts")),
             new ArgumentCompleter(new StringsCompleter("env.ls.tables")),
             new ArgumentCompleter(
                 new StringsCompleter("env.read"),
                 new DirectoriesCompleter(Paths.get("."))
                 ),
             new ArgumentCompleter(new StringsCompleter("env.set.default.hiding")),
             new ArgumentCompleter(new StringsCompleter("env.set.default.renderTarget")),
             new ArgumentCompleter(new StringsCompleter("help")),
             new ArgumentCompleter(new StringsCompleter("history")),
             new ArgumentCompleter(new StringsCompleter("ir.show.formula")),
             new ArgumentCompleter(new StringsCompleter("ir.show.part")),
             new ArgumentCompleter(new StringsCompleter("mo.show.formula")),
             new ArgumentCompleter(new StringsCompleter("mo.show.part")),
             new ArgumentCompleter(new StringsCompleter("eb.show.current.table")),
             new ArgumentCompleter(new StringsCompleter("eb.show.formula")),
             new ArgumentCompleter(new StringsCompleter("eb.show.part")),
             new ArgumentCompleter(
                 new StringsCompleter("eb.show.table"),
                 new SysCompleter(sys, false, false, true)
                 ),
             new ArgumentCompleter(new StringsCompleter("util.match")),
             new ArgumentCompleter(new StringsCompleter("yms.add.anys")),
             new ArgumentCompleter(new StringsCompleter("yms.add.constants")),
             new ArgumentCompleter(new StringsCompleter("yms.add.expressions")),
             new ArgumentCompleter(new StringsCompleter("yms.add.numbers")),
             new ArgumentCompleter(new StringsCompleter("yms.add.predicates")),
             new ArgumentCompleter(new StringsCompleter("yms.add.sets")),
             new ArgumentCompleter(new StringsCompleter("yms.add.variables")),
             new ArgumentCompleter(new StringsCompleter("yms.add.defaults")),
             new ArgumentCompleter(new StringsCompleter("yms.pop")),
             new ArgumentCompleter(new StringsCompleter("yms.push")),
             new ArgumentCompleter(new StringsCompleter("yms.show")),
             new ArgumentCompleter(new StringsCompleter("quit"))
        );
    }
}
