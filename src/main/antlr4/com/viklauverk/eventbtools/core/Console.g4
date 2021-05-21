// Copyright (C) 2021 Viklauverk AB
// Author Fredrik Öhrström
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

STRING: '"' (~'"')+ '"';

start : ( add
        | canvas
        | history
        | list
        | match
        | pop
        | print
        | push
        | read
        | set
        | show
        | helper
        | quit
        ) EOF # Done;

add
//G add <symbol_type> symbol+             § Add symbols to symbol table.
//G add defaults                          § Add symbols to symbol table.
//add:Usage: add <symbol_type> <symbol>+
//add:       add defaults
//add:
//add:Add the supplied symbols to <table_name> or the current active symbol table.
//add:Symbol types are: vars consts sets preds expres nums anys
//add:Adding defaults means adding PQR xy ST EF
//add:
//add:Examples:
//add:    add consts c d e
//add:    add vars x y z to WorkSyms
//add:    add preds P Q R
//add:    add defaults
   : 'add' typeOfSymbol symbols                # AddSymbols
   | 'add' 'defaults'                          # AddDefaultSymbols
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

canvas
//G canvas push "<name>"        § Push a new canvas on the canvas stack.
//G canvas pop                  § Pop the top canvas.
//G canvas set width            § Set canvas render width for the current canvas.
//canvas:Usage: canvas push "<name>"
//canvas:       canvas set width 120
//canvas:
//canvas:Create a canvas for rendering formulas.
//canvas:
//canvas:Examples:
//canvas:    canvas push "test"
//canvas:    canvas set width 80
   : 'canvas' 'push' name=STRING               # CanvasPush
   | 'canvas' 'target' renderTarget            # CanvasSetRenderTarget
   | 'canvas' 'attributes' renderAttribute*    # CanvasSetRenderAttributes
   | 'canvas' 'pop'                            # CanvasPop
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

history
//G history                                   § List the history of previous commands.
//history:Usage history
//history:
//history:List the history of previous commands.
   : 'history'                          #ListHistory
   ;

list
//G list <object_type>                          § List objects of a certain type.
//list:Usage list <object_type>
//list:
//list:List all objects of a given type.
//list:The available types are: tables hyps machines contexts
//list:anys consts exprs nums preds sets vars
    : 'list' 'tables'                  # ListTables
    | 'list' 'hyps'                    # ListHypothesis
    | 'list' 'machines'                # ListMachines
    | 'list' 'contexts'                # ListContexts
    | 'list' typeOfSymbol              # ListSymbols
    | 'list' 'parts'                   # ListParts
    ;

read
//G read "<dir>"                  § Read the machines (bum files) and contexts (buc files) stored inside dir.
//read:Usage read "<dir>"
//read:
//read:Read the machines (bum) and context (buc) files stored in <dir>.
//read:Give the system an overall nick name as well for the renderings.
    : 'read' dir=STRING    # ReadDir
    ;

match
//G match "<formula>" "<pattern>"               § Match formula against pattern and print matching sub-formulas.
//match:Usage: match "<formula>" "<pattern>"
//match:
//match:Match the formula agains the pattern, then print the matching sub-formulas.
//match:
//match:Examples:
//match:    match "1+5=7" "E=F"
//match:    match "floor:NAT" "x:S"
    : 'match' formula=STRING pattern=STRING # MatchPattern
    ;

print
//G print all                                 § Print variables
//match:Usage: print all
//match:
//match:Print all variables.
//match:
//match:Examples:
//match:    print all
    : 'print' 'all'     # printAll
    ;

pop
//G pop table                                 § Pop and remove the current symbol table.
//match:Usage: pop table
//match:
//match:Pops and removes the current active symbol table.
//match:
//match:Examples:
//match:    pop table
    : 'pop' 'table'     # popTable
    ;

push
//G new table "<name>"               § Create a new symbol table and push it on the stack.
//match:Usage: new table "<name>"
//match:
//match:Create a new symbol table which inherits from the current symbol table.
//match:
//match:Examples:
//match:    new table "work"
    : 'push' 'table' name=STRING  # pushTable
    ;

format
    : 'plain' | 'terminal' | 'tex' | 'htmq';

treee
    : 'tree' ;

framed
    : 'framed' ;

// noc = No comments
// nol = No labels @grd0_1 etc
// non = No event names
hiding
: 'noc' | 'nol' | 'non';

set
//G set default format {plain|terminal|tex|htmq} § Set the default format for rendering.
//G set default hiding {nol|noc}                 § Set what parts to hide by default.
//show:Usage: set default format <format>
//show:       set default hiding <hiding>
//show:
//show:Set default values.
//show:
//show:Example:
//show:    set default format tex
//show:    set default hiding nol
   : 'set' 'default' 'format' format # SetDefaultFormat
   | 'set' 'default' 'hiding' hiding # SetDefaultHiding
   ;

show
//G show formula {types} {<format>} "<formula>" § Parse the formula using the current symbol table and render it.
//G show part {framed} {<format>} "<part>"      § Show the Event-B part, eg Elevator/invariants/inv1
//G show current table                          § Show the name of the current symbol table.
//G show table "<name">                         § Show the name of the current symbol table.
//show:Usage: show formula {types} {<format>} "<formula>"
//show:       show part {framed} {<format>} "<formula>"
//show:       show current table
//show:
//show:Parse the given formula using the current symbol table and then
//show:render the formula using the given format.
//show:
//show:Example:
//show:    show "x:NAT"
//show:    show tree "x+->y"
//show:    show tex "s := s \/ { 2 }"
//show:    show tree terminal "s := s \/ { 2 }"
//show:    show part terminal "Elevator/invariants/inv1"
   : 'show' 'formula' treee? framed? hiding? format? formula=STRING # ShowFormula
   | 'show' 'current' 'table'        # ShowCurrentTable
   | 'show' 'table' name=STRING      # ShowTable
   | 'show' 'part' framed? format? name=STRING   # ShowPart
   | 'show' 'template' name=STRING       # ShowTemplate
   ;


helper
//G help {<command>}                            § Give general help or help on a command.
//help:Usage help {<command>}
//help:
//help:Give general help or help on a specific command.
   : 'help' # HelpHelp
   | 'help' topic #Help
   ;

topic
   : 'add'
   | 'help'
   | 'list'
   | 'match'
   | 'read'
   | 'render'
   | 'show'
   | 'shortcuts'
   ;

quit
//G quit                             § Quit evbt
//help:Usage quit
//help:
//help:Quit evbt console.
   : 'quit' # QuitQuit
   ;
