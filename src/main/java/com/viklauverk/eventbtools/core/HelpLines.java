// Copyright Viklauverk AB 2021-2023
package com.viklauverk.eventbtools.core;
public class HelpLines
{
   public static String help =
"Commands:\n\n"+
" quit                             § Quit evbt\n"+
" help {<command>}                            § Give general help or help on a command.\n"+
" history                                   § List the history of previous commands.\n"+
" ca.push \"<name>\"        § Push a new canvas on the canvas stack.\n"+
" ca.pop                  § Pop the top canvas.\n"+
" ca.set.width            § Set canvas render width for the current canvas.\n"+
" ca.target <render_target>  § plain terminal tex htmq\n"+
" ca.attributes <render_attribute> §  Set rendering attributes, like color, labels, at, frame etc.\n"+
" co.show.formula {types} {<renderTarget>} \"<formula>\" § Render  the code for the formula given the current environment.\n"+
" co.show.part {framed} {<renderTarget>} \"<part>\"      § Render the code for the Event-B part, eg Elevator/invariants/inv1\n"+
" ir.show.formula {types} {<renderTarget>} \"<formula>\" § Render the ir for the formula given the current environment.\n"+
" ir.show.part {framed} {<renderTarget>} \"<part>\"      § Render the ir for the Event-B part, eg Elevator/invariants/inv1\n"+
" mo.show.formula {types} {<renderTarget>} \"<formula>\" § Render a model for the formula given the current environment.\n"+
" mo.show.part {framed} {<renderTarget>} \"<part>\"      § Render a model for the Event-B part, eg Elevator/invariants/inv1\n"+
" eb.show.formula {types} {<renderTarget>} \"<formula>\" § Parse the formula using the current symbol table and render it.\n"+
" eb.show.part {framed} {<renderTarget>} \"<part>\"      § Show the Event-B part, eg Elevator/invariants/inv1\n"+
" eb.show.current.table                          § Show the name of the current symbol table.\n"+
" eb.show.table \"<name\">                         § Show the name of the current symbol table.\n"+
" yms.add <symbol_type> symbol+             § Add symbols to symbol table.\n"+
" yms.add.defaults                          § Add symbols to symbol table.\n"+
" yms.show           § Show the symbol table\n"+
" yms.push \"<name>\"               § Create a new symbol table and push it on the stack.\n"+
" yms.pop                                  § Pop and remove the current symbol table.\n"+
" env.ls                                 § List objects of a certain type.\n"+
" env.ls.tables                          § List objects of a certain type.\n"+
" env.ls.hyps                            § List objects of a certain type.\n"+
" env.ls.contexts                        § List objects of a certain type.\n"+
" env.ls.pars                            § List objects of a certain type.\n"+
" env.read \"<dir>\"                  § Read the machines (bum files) and contexts (buc files) stored inside dir.\n"+
" env.set.default.renderTarget {plain|terminal|tex|htmq} § Set the default renderTarget for rendering.\n"+
" env.set.default.hiding {nol|noc}                 § Set what parts to hide by default.\n"+
" util.match \"<formula>\" \"<pattern>\"               § Match formula against pattern and print matching sub-formulas.\n"+
"";


    public static String help_ca_attributes =
"";


    public static String help_ca_pop =
"";


    public static String help_ca_push =
"";


    public static String help_ca_set_width =
"";


    public static String help_ca_target =
"";


    public static String help_co_show_formula =
"";


    public static String help_co_show_part =
"";


    public static String help_eb_show_current_table =
"";


    public static String help_eb_show_formula =
"";


    public static String help_eb_show_part =
"";


    public static String help_eb_show_table =
"";


    public static String help_env_ls =
"";


    public static String help_env_ls_contexts =
"";


    public static String help_env_ls_hyps =
"";


    public static String help_env_ls_pars =
"";


    public static String help_env_ls_tables =
"";


    public static String help_env_read =
"";


    public static String help_env_set_default_hiding =
"";


    public static String help_env_set_default_renderTarget =
"";


    public static String help_help =
"Usage quit\n"+
"\n"+
"Quit evbt console.\n"+
"Usage help\n"+
"\n"+
"Give general help.\n"+
"";


    public static String help_history =
"Usage history\n"+
"\n"+
"List the history of previous commands.\n"+
"";


    public static String help_ir_show_formula =
"";


    public static String help_ir_show_part =
"";


    public static String help_mo_show_formula =
"";


    public static String help_mo_show_part =
"";


    public static String help_quit =
"";


    public static String help_util_match =
"";


    public static String help_yms_add =
"";


    public static String help_yms_add_defaults =
"";


    public static String help_yms_pop =
"";


    public static String help_yms_push =
"";


    public static String help_yms_show =
"";


public static java.util.HashMap<String,String> helps;
static { 
    helps = new java.util.HashMap<>();
    helps.put("ca_attributes", help_ca_attributes);
    helps.put("ca_pop", help_ca_pop);
    helps.put("ca_push", help_ca_push);
    helps.put("ca_set_width", help_ca_set_width);
    helps.put("ca_target", help_ca_target);
    helps.put("co_show_formula", help_co_show_formula);
    helps.put("co_show_part", help_co_show_part);
    helps.put("eb_show_current_table", help_eb_show_current_table);
    helps.put("eb_show_formula", help_eb_show_formula);
    helps.put("eb_show_part", help_eb_show_part);
    helps.put("eb_show_table", help_eb_show_table);
    helps.put("env_ls", help_env_ls);
    helps.put("env_ls_contexts", help_env_ls_contexts);
    helps.put("env_ls_hyps", help_env_ls_hyps);
    helps.put("env_ls_pars", help_env_ls_pars);
    helps.put("env_ls_tables", help_env_ls_tables);
    helps.put("env_read", help_env_read);
    helps.put("env_set_default_hiding", help_env_set_default_hiding);
    helps.put("env_set_default_renderTarget", help_env_set_default_renderTarget);
    helps.put("help", help_help);
    helps.put("history", help_history);
    helps.put("ir_show_formula", help_ir_show_formula);
    helps.put("ir_show_part", help_ir_show_part);
    helps.put("mo_show_formula", help_mo_show_formula);
    helps.put("mo_show_part", help_mo_show_part);
    helps.put("quit", help_quit);
    helps.put("util_match", help_util_match);
    helps.put("yms_add", help_yms_add);
    helps.put("yms_add_defaults", help_yms_add_defaults);
    helps.put("yms_pop", help_yms_pop);
    helps.put("yms_push", help_yms_push);
    helps.put("yms_show", help_yms_show);
}


}
