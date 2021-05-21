// Copyright Viklauverk AB 2021
package com.viklauverk.eventbtools.core;
public class HelpLines
{
   public static String help =
"Commands:\n\n"+
" add <symbol_type> symbol+             § Add symbols to symbol table.\n"+
" add defaults                          § Add symbols to symbol table.\n"+
" canvas push \"<name>\"        § Push a new canvas on the canvas stack.\n"+
" canvas pop                  § Pop the top canvas.\n"+
" canvas set width            § Set canvas render width for the current canvas.\n"+
" history                                   § List the history of previous commands.\n"+
" list <object_type>                          § List objects of a certain type.\n"+
" read \"<dir>\"                  § Read the machines (bum files) and contexts (buc files) stored inside dir.\n"+
" match \"<formula>\" \"<pattern>\"               § Match formula against pattern and print matching sub-formulas.\n"+
" print all                                 § Print variables\n"+
" pop table                                 § Pop and remove the current symbol table.\n"+
" new table \"<name>\"               § Create a new symbol table and push it on the stack.\n"+
" set default format {plain|terminal|tex|htmq} § Set the default format for rendering.\n"+
" set default hiding {nol|noc}                 § Set what parts to hide by default.\n"+
" show formula {types} {<format>} \"<formula>\" § Parse the formula using the current symbol table and render it.\n"+
" show part {framed} {<format>} \"<part>\"      § Show the Event-B part, eg Elevator/invariants/inv1\n"+
" show current table                          § Show the name of the current symbol table.\n"+
" show table \"<name\">                         § Show the name of the current symbol table.\n"+
" help {<command>}                            § Give general help or help on a command.\n"+
" quit                             § Quit evbt\n"+
"";


    public static String help_add =
"Usage: add <symbol_type> <symbol>+\n"+
"       add defaults\n"+
"\n"+
"Add the supplied symbols to <table_name> or the current active symbol table.\n"+
"Symbol types are: vars consts sets preds expres nums anys\n"+
"Adding defaults means adding PQR xy ST EF\n"+
"\n"+
"Examples:\n"+
"    add consts c d e\n"+
"    add vars x y z to WorkSyms\n"+
"    add preds P Q R\n"+
"    add defaults\n"+
"";


    public static String help_canvas =
"Usage: canvas push \"<name>\"\n"+
"       canvas set width 120\n"+
"\n"+
"Create a canvas for rendering formulas.\n"+
"\n"+
"Examples:\n"+
"    canvas push \"test\"\n"+
"    canvas set width 80\n"+
"";


    public static String help_help =
"Usage help {<command>}\n"+
"\n"+
"Give general help or help on a specific command.\n"+
"Usage quit\n"+
"\n"+
"Quit evbt console.\n"+
"";


    public static String help_history =
"Usage history\n"+
"\n"+
"List the history of previous commands.\n"+
"";


    public static String help_list =
"Usage list <object_type>\n"+
"\n"+
"List all objects of a given type.\n"+
"The available types are: tables hyps machines contexts\n"+
"anys consts exprs nums preds sets vars\n"+
"";


    public static String help_match =
"Usage: match \"<formula>\" \"<pattern>\"\n"+
"\n"+
"Match the formula agains the pattern, then print the matching sub-formulas.\n"+
"\n"+
"Examples:\n"+
"    match \"1+5=7\" \"E=F\"\n"+
"    match \"floor:NAT\" \"x:S\"\n"+
"Usage: print all\n"+
"\n"+
"Print all variables.\n"+
"\n"+
"Examples:\n"+
"    print all\n"+
"Usage: pop table\n"+
"\n"+
"Pops and removes the current active symbol table.\n"+
"\n"+
"Examples:\n"+
"    pop table\n"+
"Usage: new table \"<name>\"\n"+
"\n"+
"Create a new symbol table which inherits from the current symbol table.\n"+
"\n"+
"Examples:\n"+
"    new table \"work\"\n"+
"";


    public static String help_new =
"";


    public static String help_pop =
"";


    public static String help_print =
"";


    public static String help_quit =
"";


    public static String help_read =
"Usage read \"<dir>\"\n"+
"\n"+
"Read the machines (bum) and context (buc) files stored in <dir>.\n"+
"Give the system an overall nick name as well for the renderings.\n"+
"";


    public static String help_set =
"";


    public static String help_show =
"Usage: set default format <format>\n"+
"       set default hiding <hiding>\n"+
"\n"+
"Set default values.\n"+
"\n"+
"Example:\n"+
"    set default format tex\n"+
"    set default hiding nol\n"+
"Usage: show formula {types} {<format>} \"<formula>\"\n"+
"       show part {framed} {<format>} \"<formula>\"\n"+
"       show current table\n"+
"\n"+
"Parse the given formula using the current symbol table and then\n"+
"render the formula using the given format.\n"+
"\n"+
"Example:\n"+
"    show \"x:NAT\"\n"+
"    show tree \"x+->y\"\n"+
"    show tex \"s := s \\/ { 2 }\"\n"+
"    show tree terminal \"s := s \\/ { 2 }\"\n"+
"    show part terminal \"Elevator/invariants/inv1\"\n"+
"";


public static java.util.HashMap<String,String> helps;
static { 
    helps = new java.util.HashMap<>();
    helps.put("add", help_add);
    helps.put("canvas", help_canvas);
    helps.put("help", help_help);
    helps.put("history", help_history);
    helps.put("list", help_list);
    helps.put("match", help_match);
    helps.put("new", help_new);
    helps.put("pop", help_pop);
    helps.put("print", help_print);
    helps.put("quit", help_quit);
    helps.put("read", help_read);
    helps.put("set", help_set);
    helps.put("show", help_show);
}


}
