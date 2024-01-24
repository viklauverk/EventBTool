// Copyright (C) 2021-2023 Viklauverk AB
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

grammar Console;

WHITESPACE: [ \r\n\t]+ -> channel(HIDDEN);

NUMBER: [0-9]+;

SYMBOL: [a-zA-Z][a-zA-Z0-9_]*;

STRING: '"' (~'"')+ '"' | (~' ')+;

start : (
        quit
        | helper
        | history
        | ca_canvas
        | co_show
        | env_list
        | env_read
        | env_set
        | ir_show
        | mo_show
        | eb_show
        | yms_add
        | yms_push
        | yms_pop
        | yms_show
        | util_match
        ) EOF # Done;

quit
//G quit                             § Quit evbt
//help:Usage quit
//help:
//help:Quit evbt console.
   : ('q' | 'quit') # QuitQuit
   ;

helper
//G help {<command>}                            § Give general help or help on a command.
//help:Usage help
//help:
//help:Give general help.
   : ('h' | 'help') # HelpHelp
   ;

history
//G history                                   § List the history of previous commands.
//history:Usage history
//history:
//history:List the history of previous commands.
   : 'history'                          #ListHistory
   ;

ca_canvas
//G ca.push "<name>"        § Push a new canvas on the canvas stack.
//G ca.pop                  § Pop the top canvas.
//G ca.set.width            § Set canvas render width for the current canvas.
//G ca.target <render_target>  § plain terminal tex htmq
//G ca.attributes <render_attribute> §  Set rendering attributes, like color, labels, at, frame etc.
//canvas:Usage: ca.push "<name>"
//canvas:       ca.set width 120
//canvas:
//canvas:Create a canvas for rendering formulas.
//canvas:
//canvas:Examples:
//canvas:    ca.push "test"
//canvas:    ca.set width 80
   : 'ca.push' name=STRING               # CanvasPush
   | 'ca.target' renderTarget            # CanvasSetRenderTarget
   | 'ca.attributes' renderAttribute*    # CanvasSetRenderAttributes
   | 'ca.pop'                            # CanvasPop
   ;

renderTarget
   : 'plain'
   | 'terminal'
   | 'tex'
   | 'htmq'
   ;

renderAttribute
   :
   attrKey '=' attrVal=STRING
   ;

attrKey
    : 'color'
    | 'labels'
    | 'at'
    | 'frame'
    | 'hlayout'
    | 'indent'
    | 'anchors'
    | 'indexes'
    ;

co_show
//G co.show.formula {types} {<renderTarget>} "<formula>" § Render  the code for the formula given the current environment.
//G co.show.part {framed} {<renderTarget>} "<part>"      § Render the code for the Event-B part, eg Elevator/invariants/inv1
//show:Usage: co.show.formula {types} {<renderTarget>} "<formula>"
//show:       co.show.part {framed} {<renderTarget>} "<formula>"
//show:
//show:Show the assignment/part in the select model renderTarget.
//show:
//show:Example:
//show:    co.show "x:=7"
//show:    co.show "Elevator/events/up"
   : 'co.show.formula' formula=STRING # ShowCode
   | 'co.show.part' framed? renderTarget? name=STRING   # ShowCodePart
   ;

//  framed? hiding? renderTarget?

ir_show
//G ir.show.formula {types} {<renderTarget>} "<formula>" § Render the ir for the formula given the current environment.
//G ir.show.part {framed} {<renderTarget>} "<part>"      § Render the ir for the Event-B part, eg Elevator/invariants/inv1
//show:Usage: ir.show.formula {types} {<renderTarget>} "<formula>"
//show:       ir.show.part {framed} {<renderTarget>} "<formula>"
//show:
//show:Render the intermediate representation for code generation.
//show:
//show:Example:
//show:    ir.show "x:=7"
//show:    ir.show "Elevator/events/up"
   : 'ir.show.formula' framed? hiding? renderTarget? formula=STRING # ShowIR
   | 'ir.show.part' framed? renderTarget? name=STRING   # ShowIRPart
   ;

mo_show
//G mo.show.formula {types} {<renderTarget>} "<formula>" § Render a model for the formula given the current environment.
//G mo.show.part {framed} {<renderTarget>} "<part>"      § Render a model for the Event-B part, eg Elevator/invariants/inv1
//show:Usage: mo.show.formula {types} {<renderTarget>} "<formula>"
//show:       mo.show.part {framed} {<renderTarget>} "<formula>"
//show:
//show:Render the intermediate representation for code generation.
//show:
//show:Example:
//show:    mo.show "x:=7"
//show:    mo.show "Elevator/events/up"
   : 'mo.show.formula' framed? hiding? renderTarget? formula=STRING # ShowModel
   | 'mo.show.part' framed? renderTarget? name=STRING   # ShowModelPart
   ;

eb_show
//G eb.show.formula {types} {<renderTarget>} "<formula>" § Parse the formula using the current symbol table and render it.
//G eb.show.part {framed} {<renderTarget>} "<part>"      § Show the Event-B part, eg Elevator/invariants/inv1
//G eb.show.current.table                          § Show the name of the current symbol table.
//G eb.show.table "<name">                         § Show the name of the current symbol table.
//show:Usage: eb.show.formula {types} {<renderTarget>} "<formula>"
//show:       eb.show.part {framed} {<renderTarget>} "<formula>"
//show:       eb.show.current table
//show:
//show:Parse the given formula using the current symbol table and then
//show:render the formula using the given renderTarget.
//show:
//show:Example:
//show:    eb.show "x:NAT"
//show:    eb.show tree "x+->y"
//show:    eb.show tex "s := s \/ { 2 }"
//show:    eb.show tree terminal "s := s \/ { 2 }"
//show:    eb.show part terminal "Elevator/invariants/inv1"
   : 'eb.show.formula' metaaa? treee? framed? hiding? renderTarget? formula=STRING # ShowFormula
   | 'eb.show.current.table'        # ShowCurrentTable
   | 'eb.show.table' name=STRING      # ShowTable
   | 'eb.show.part' framed? renderTarget? name=STRING   # ShowPart
//   | 'eb.show.template' name=STRING       # ShowTemplate
   ;

yms_add
//G yms.add <symbol_type> symbol+             § Add symbols to symbol table.
//G yms.add.defaults                          § Add symbols to symbol table.
//add:Usage: yms.add <symbol_type> <symbol>+
//add:       yms.add defaults
//add:
//add:Add the supplied symbols to the current active symbol table.
//add:Symbol types are: vars consts sets preds expres nums anys
//add:Adding defaults means adding PQR xy ST EF
//add:
//add:Examples:
//add:    yms.add.anys A B C
//add:    yms.add.constants c d e
//add:    yms.add.expressions E F G
//add:    yms.add.numbers M N
//add:    yms.add.predicates P Q R
//add:    yms.add.sets P Q R
//add:    yms.add.variables x y z
//add:    yms.add.defaults     Add some useful default symbols
   : 'yms.add.anys' symbols                    # AddSymbols
   | 'yms.add.constants' symbols                    # AddSymbols
   | 'yms.add.expressions' symbols                  # AddSymbols
   | 'yms.add.numbers' symbols                      # AddSymbols
   | 'yms.add.predicates' symbols                   # AddSymbols
   | 'yms.add.sets' symbols                         # AddSymbols
   | 'yms.add.variables' symbols                    # AddSymbols
   | 'yms.add.defaults'                             # AddDefaultSymbols
   ;

typeOfSymbol
   : 'anys'
   | 'consts'
   | 'exprs'
   | 'nums'
   | 'preds'
   | 'sets'
   | 'vars'
     ;

symbols
   : SYMBOL+                           # ListOfSymbols
   ;

yms_show
//G yms.show           § Show the symbol table
//add:Usage: yms.show
//add:
//add:Show the current symbol table.
//add:
//add:Examples:
//add:    yms.show
   : 'yms.show'                     # ShowAllOfSymbolTable
     | 'yms.show' typeOfSymbol        # ShowPartOfSymbolTable
   ;

yms_push
//G yms.push "<name>"               § Create a new symbol table and push it on the stack.
//match:Usage: yms.push "<name>"
//match:
//match:Create a new symbol table which inherits from the current symbol table.
//match:
//match:Examples:
//match:    new table "work"
    : 'yms.push' name=STRING  # pushTable
    ;

yms_pop
//G yms.pop                                  § Pop and remove the current symbol table.
//match:Usage: yms.pop
//match:
//match:Pops and removes the current active symbol table.
//match:
//match:Examples:
//match:    yms.pop
    : 'yms.pop'  # popTable
    ;

env_list
//G env.ls.machines                        § List objects of a certain type.
//G env.ls.tables                          § List objects of a certain type.
//G env.ls.hyps                            § List objects of a certain type.
//G env.ls.contexts                        § List objects of a certain type.
//G env.ls.pars                            § List objects of a certain type.
//list:Usage env.ls <object_type>
//list:
//list:List all objects of a given type.
//list:The available types are: tables hyps machines contexts
//list:anys consts exprs nums preds sets vars
    : 'env.ls.machines'                # ListMachines
    | 'env.ls.tables'                  # ListTables
    | 'env.ls.hyps'                    # ListHypothesis
    | 'env.ls.contexts'                    # ListContexts
    | 'env.ls.parts'                   # ListParts
    ;

env_read
//G env.read "<dir>"                  § Read the machines (bum files) and contexts (buc files) stored inside dir.
//read:Usage envread "<dir>"
//read:
//read:Read the machines (bum) and context (buc) files stored in <dir>.
//read:Give the system an overall nick name as well for the renderings.
    : 'env.read' dir=STRING    # ReadDir
//C   SC"env.read" DC
    ;

metaaa
    : 'meta' ;

treee
    : 'tree' ;

framed
    : 'framed' ;

// noc = No comments
// nol = No labels @grd0_1 etc
// non = No event names
hiding
: 'noc' | 'nol' | 'non';

env_set
//G env.set.default.renderTarget {plain|terminal|tex|htmq} § Set the default renderTarget for rendering.
//G env.set.default.hiding {nol|noc}                 § Set what parts to hide by default.
//show:Usage: env.set.default.renderTarget <renderTarget>
//show:       env.set.default.hiding <hiding>
//show:
//show:Set default values.
//show:
//show:Example:
//show:    set.default.renderTarget tex
//show:    set.default.hiding nol
   : 'env.set.default.render_target' renderTarget # SetDefaultRenderTarget
   | 'env.set.default.hiding' hiding # SetDefaultHiding
   ;

util_match
//G util.match "<formula>" "<pattern>"               § Match formula against pattern and print matching sub-formulas.
//match:Usage: util.match "<formula>" "<pattern>"
//match:
//match:Match the formula agains the pattern, then print the matching sub-formulas.
//match:
//match:Examples:
//match:    util.match "1+5=7" "E=F"
//match:    util.match "floor:NAT" "x:S"
    : 'util.match' formula=STRING pattern=STRING # MatchPattern
    ;
