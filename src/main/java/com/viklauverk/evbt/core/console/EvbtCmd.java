/*
 Copyright (C) 2024 Viklauverk AB (agpl-3.0-or-later)

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

package com.viklauverk.evbt.core.console;

public enum EvbtCmd
{
    EB_SHOW_FORMULA(
        "eb.show.formula",
        "Parse and render formula.",
        "",
        CmdEbShowFormula::new),
    EB_SHOW_PART(
        "eb.show.part",
        "Print Event-B model part.",
        "",
        CmdEbShowPart::new),
    ERROR("error",
          "Error",
          "",
          null),
    HELP(
        "help",
        "Print help on a command.",
        """
           Usage: help
                  help <command>
        """,
        CmdHelp::new),
    SYS_PRINT_TEMPLATE(
        "sys.print.template",
        "Print a builtin template for code/documentation.",
        "",
        CmdSysPrintTemplate::new),
    SYS_LS_CONTEXTS(
        "sys.ls.contexts",
        "List all contexts in the system, or only those who match text",
        """
        Usage: sys.ls.contexts
               sys.ls.contexts abc
        """,
        CmdSysLsContexts::new),
    SYS_LS_DATA_TYPES(
        "sys.ls.data-types",
        "List all data types loaded from theories.",
        """
        Usage: sys.ls.data-types
               sys.ls.data-types abc
        """,
        CmdSysLsDataTypes::new),
    SYS_LS_MACHINES(
        "sys.ls.machines",
        "List all machines in the system, or only those who match text",
        """
        Usage: sys.ls.machines
               sys.ls.machines abc
        """,
        CmdSysLsContexts::new),
    SYS_LS_PARTS(
        "sys.ls.parts",
        "List all machine parts in the system, or only those who match text",
        """
        Usage: sys.ls.parts
               sys.ls.parts abc
        """,
        CmdSysLsParts::new),
    SYS_LS_SEEN_THEORIES(
        "sys.ls.seen-theories",
        "List all theories listed in the theory path.",
        """
        Usage: sys.ls.seen-theories
               sys.ls.seen-theories abc
        """,
        CmdSysLsSeenTheories::new),
    SYS_LS_SPECIALISED_DATA_TYPES(
        "sys.ls.specialised-data-types",
        "List all specialised data types.",
        """
        Usage: sys.ls.specialised-data-types
               sys.ls.specialised-data-types abc
        """,
        CmdSysLsSpecialisedDataTypes::new),
    SYS_LS_TABLES(
        "sys.ls.tables",
        "List all tables in the system, or only those who match text",
        """
        Usage: sys.ls.tables
               sys.ls.tables abc
        """,
        CmdSysLsTables::new),
    SYS_READ(
        "sys.read",
        "Read an event-b directory and load all files.",
        "Usage: sys.read <dir>",
        CmdSysRead::new),
    QUIT(
        "quit",
        "Exit console.",
        "Exit console.",
        null),
    UTIL_MATCH(
        "util.match",
        "Match two formulas.",
        "Usage: util.match 1+2 --- M+N",
        CmdUtilMatch::new),
    YMS_ADD_ANYS(
        "yms.add.anys",
        "Add any (matching anything) symbols to current symbol table.",
        "Usage: yms.add.anys A B",
        CmdYmsAddAnys::new),
    YMS_ADD_DEFAULTS(
        "yms.add.defaults",
        "Add default symbols to current symbol table.",
        "",
        CmdYmsAddDefaults::new),
    YMS_ADD_CONSTANTS(
        "yms.add.constants",
        "Add constant symbols to current symbol table.",
        "Usage: yms.add.constants c d f",
        CmdYmsAddConstants::new),
    YMS_ADD_EXPRESSIONS(
        "yms.add.expressions",
        "Add expression symbols to current symbol table.",
        "Usage: yms.add.expression E F",
        CmdYmsAddExpressions::new),
    YMS_ADD_NUMBERS(
        "yms.add.numbers",
        "Add number symbols to current symbol table.",
        "Usage: yms.add.expression N M",
        CmdYmsAddNumbers::new),
    YMS_ADD_POLYTYPES(
        "yms.add.datatypes",
        "Add polymorphic data types to current symbol table.",
        "Usage: yms.add.datatype H List Pair",
        CmdYmsAddPolytypes::new),
    YMS_ADD_POLYOPS(
        "yms.add.operators",
        "Add polymorphic operators to current symbol table.\n"+
        "The name must be suffixed with +PE for notation Prefix/Expression\n"+
        "+IP for Infix/Predicate, etc for +PP and +IE",
        "Usage: yms.add.operators h+IP listSize+PE",
        CmdYmsAddPolyops::new),
    YMS_ADD_PREDICATES(
        "yms.add.predicates",
        "Add predicate symbols to current symbol table.",
        "Usage: yms.add.predicates P Q R",
        CmdYmsAddPredicates::new),
    YMS_ADD_SETS(
        "yms.add.sets",
        "Add set symbols to current symbol table.",
        "Usage: yms.add.sets S T",
        CmdYmsAddSets::new),
    YMS_ADD_VARIABLES(
        "yms.add.variables",
        "Add variable symbols to current symbol table.",
        "Usage: yms.add.variables x y",
        CmdYmsAddVariables::new),
    YMS_LOOKUP(
        "yms.lookup",
        "Lookup the string and find the object registered for this symbol.",
        "Usage: yms.lookup lessThan",
        CmdYmsLookup::new),
    YMS_SHOW(
        "yms.show",
        "Show active symbol table.",
        "",
        CmdYmsShow::new)
        ;

    public String name;
    public String explanation;
    public String manual;
    public CreateCmd constructor;

    EvbtCmd(String n, String e, String m, CreateCmd co)
    {
        name = n;
        explanation = e;
        manual = m;
        constructor = co;
    }

    public static EvbtCmd parse(String s)
    {
        for (EvbtCmd c : EvbtCmd.values())
        {
            if (s.equals(c.name)) return c;
        }
        return ERROR;
    }

    public static String[] getNames()
    {
        String[] r = new String[EvbtCmd.values().length];

        int i = 0;
        for (EvbtCmd c : EvbtCmd.values())
        {
            r[i] = c.name;
            i++;
        }

        return r;
    }

}
