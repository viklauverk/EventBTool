
╭ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╮<br>
║ Alla mathematical statements are stored in Formulas.                                  <br>
╰ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╯<br>

The Formula class encodes statements like: actions `loans ≔ {book}⩤loans` as well as invariants,
axioms, guards and theorems `books∈ℙ(ℕ)`

[Formula.java](Formula.java)

A formula references child formulas, thus a statements like 'age∈ℕ' consists of a top-level
formula MEMBERSHIP with two children where the first child is a VARIABLE_SYMBOL and the second is NAT_SET.

Applying `toString()` on this formula renders `age∈ℕ` and applying `toStringWithTypes()` renders
`<MEMBERSHIP <VARIABLE_SYMBOL age>∈<NAT_SET ℕ>>`

The enum Node lists all the different possible formula nodes (MEMBERSHIP,SYMBOL_VARIABLE,NAT_SET etc).

[Node.java](Node.java)

New formulas are created using the formula factory or by parsing.

[FormulaFactory.java](FormulaFactory.java)

╭ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╮<br>
║ The mathematical expressions are parsed as follows.                                   <br>
╰ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╯<br>

The parser is defined in antlr4.

[EvBFormula.g4](../../../../../antlr4/com/viklauverk/eventbtools/core/EvBFormula.g4)

The parser creates a tree that is visited the FormulbaBuilder to create the actual formula.

[FormulaBuilder.java](FormulaBuilder.java)

Symbol tables are used to store the symbols needed for the parser to recognize references to sets, variables and constants.

[SymbolTable.java](SymbolTable.java)

Invoke `Formula f = fromString(input, symbols);` to parse the string `input`
(which can be be either unicode like `books∈ℙ(ℕ)` or the ascii version `books:POW(NAT)`)
using the supplied symbol table `symbols`.

(The formula stores integer indexes into a table of known symbol strings.
These are stored in [Symbols.java](Symbols.java) )

╭ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╮<br>
║ Typing Event-B mathematical statements                                                <br>
╰ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╯<br>

Event-B (and Classical B) avoids un-implementable set-paradoxes by enforcing typing of
mathematical statements.

A Type for a formula is stored in the Type class:

[Type.java](Type.java)

The maintenance of known types and the actual typing is done in:

[Typing.java](Typing.java)

A type is just a formula. The type of `x` in `x∈ℕ` is `ℕ`. The type of `books`
in `books⊆ℕ` is `ℙ(ℕ)` because the subset expression is first rewritten as `books∈ℙ(ℕ)`

For convenience, objects like variables, constants and sets can extend from
[Typed.java](Typed.java) because they can all be typed.

╭ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╮<br>
║ The pattern matches is a useful tool for typing and other work on formulas.           <br>
╰ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╯<br>

Try to match a formula `loans∈books⇸ borrowers` against a pattern `x∈y⇸ z`.
If the match is a success, then x can get queried and in this example will return `loans`.
Likewise for `y=books` and `z=borrowers`.

If the pattern was instead `x∈S` then the dictionary would contain `x` as before and `S` would
be the formula `<PARTIAL_FUNCTION <VARIABLE_SYMBOL y>⇸ <VARIABLE_SYMBOL z>>`.

The pattern is parsed using a default symbol table where:
```
P,Q,R are predicates
E,F,G are expressions
S,T,U are sets
x,y,z are variables
c,d,f are constants
m,n   are numbers
A,B,C matches any formula node.
```

[Pattern.java](Pattern.java)

╭ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╮<br>
║ Loading an Event-B system from disk.                                                  <br>
╰ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╯<br>

The source is fetched from the source dir specified in:

[CommonSettings.java](CommonSettings.java)

The loaded system (can include multiple machines and multiple contexts) is stored in:

[Sys.java](Sys.java)

A Rodin directory with bum and buc files
is loaded using `sys.loadMachinesAndContexts(s.commonSettings().sourceDir());`
The loading happens in this order:

```
// First create empty object instances for each context and machine.
populateContexts(dir);
populateMachines(dir);

// Now load the content, it is now possible to refer to other
// contexts and machines since we have prepopulated those maps.
// These will also add the known symbols for sets,constants and variables
// to the SymbolTables that are needed for parsing the formulas.
loadContexts();
loadMachines();

// Now we can actually parse the formulas and figure out the types.
parseContextFormulas();
parseMachineFormulas();
```

╭ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╮<br>
║ Several Event-B constructs are just formulas inside a wrapper class.                  <br>
╰ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╯<br>

For example, the Action, Axiom, Guard and the Invariant, therefore they share a common base class:

[IsAFormula.java](IsAFormula.java)

╭ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╮<br>
║ The Event-B Context.                                                                  <br>
╰ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╯<br>

[Context.java](Context.java)

[CarrierSet.java](CarrierSet.java)

[Constant.java](Constant.java)

[Axiom.java](Axiom.java)

╭ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╮<br>
║ The Event-B Machine.<br>
╰ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╯<br>

[Machine.java](Machine.java)

[Variable.java](Variable.java)

[Invariant.java](Invariant.java)

╭ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╮<br>
║ The Event-B Event.<br>
╰ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╯<br>

[Event.java](Event.java)

[Guard.java](Guard.java)

[Action.java](Action.java)

╭ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╮<br>
║ Printing formulas and generating code.<br>
╰ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╯<br>

Visitor interfaces are used to iterate contexts, machines, events for documentation.

The FormulaVisitor is used both for documentation and code generation.

[FormulaVisitor.java](FormulaVisitor.java)

[VisitContext.java](VisitContext.java)

[VisitMachine.java](VisitMachine.java)

[VisitEvent.java](VisitEvent.java)

[VisitFormula.java](VisitFormula.java)

To visit something you do: `VisitFormula.walk(visitor, formula)` or `VisitContext.walk(visitor, context)` etc.

╭ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╮<br>
║ Console
╰ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╯<br>

The console is an interactive tool to examine Event-B models and to experiment with formulas
and (in the future) proofs.

The commands that can be executed inside the console, can also be executed inside a document
when using docmod. Such an invocation looks like: `EVBT(command)`

Type `help` for help. Press tab for tab completion of commands and arguments.
```
yms.add.defaults
eb.show.formula framed "TRUE:BOOL"
```

[Console.java](Console.java)

[ConsoleException.java](ConsoleException.java)

[ConsoleExecutor.java](ConsoleExecutor.java)

[ConsoleSettings.java](ConsoleSettings.java)

╭ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╮<br>
║ Documentation (terminal, tex and html) generators.<br>
╰ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╯<br>

The settings for generation of documentation is stored in:

[DocGenSettings.java](DocGenSettings.java)
[DocGen.java](DocGen.java)

[Templates.java](Templates.java)
[ThrowingErrorListener.java](ThrowingErrorListener.java)

[GeneratedParts.java](GeneratedParts.java)

[HelpLines.java](HelpLines.java)

[LogModuleNames.java](LogModuleNames.java)


When showing on terminal the settings are stored in:

[ShowSettings.java](ShowSettings.java)

The two main classes for generating documentation are:

[DocGenHtmq.java](DocGenHtmq.java)

[DocGenTeX.java](DocGenTeX.java)

[DocGenUnicode.java](DocGenUnicode.java)

(These classes inherit common code from [BaseDocGen.java](BaseDocGen.java) )

[AllRenders.java](AllRenders.java)

Each generator extends from the CommonRenderFunctions class. This enables
each generator to find all the other relevant generators.

[CommonRenderFunctions.java](CommonRenderFunctions.java)

[RenderContext.java](RenderContext.java)
[RenderContextHtmq.java](RenderContextHtmq.java)
[RenderContextTeX.java](RenderContextTeX.java)
[RenderContextUnicode.java](RenderContextUnicode.java)

[RenderEvent.java](RenderEvent.java)
[RenderEventHtmq.java](RenderEventHtmq.java)
[RenderEventTeX.java](RenderEventTeX.java)
[RenderEventUnicode.java](RenderEventUnicode.java)

[RenderFormula.java](RenderFormula.java)
[RenderFormulaHtmq.java](RenderFormulaHtmq.java)
[RenderFormulaTeX.java](RenderFormulaTeX.java)
[RenderFormulaUnicode.java](RenderFormulaUnicode.java)

[RenderMachine.java](RenderMachine.java)
[RenderMachineHtml.java](RenderMachineHtml.java)
[RenderMachineTeX.java](RenderMachineTeX.java)
[RenderMachineUnicode.java](RenderMachineUnicode.java)


╭ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╮<br>
║ Code generation high level.<br>
╰ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╯<br>

The settings for generation of documentation is stored in:

[CodeGenSettings.java](CodeGenSettings.java)

CodeGen.lookup(...) returns the appropriate class extending BaseCodeGen.

[CodeGen.java](CodeGen.java)

[BaseCodeGen.java](BaseCodeGen.java)

[ProgrammingLanguage.java](ProgrammingLanguage.java)

Extenders of BaseCodeGen are:

[CodeGenCpp.java](CodeGenCpp.java)

[CodeGenJava.java](CodeGenJava.java)

[CodeGenJavascript.java](CodeGenJavascript.java)

[CodeGenSQL.java](CodeGenSQL.java)

These classes also implement the interface [CommonCodeGenFunctions.java](CommonCodeGenFunctions.java)
to make it easier to implement a new language.

╭ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╮<br>
║ Code generation formulas.<br>
╰ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╯<br>

The clases implementing translation for formulas to respective language are the generators:

[GenerateFormulaCpp.java](GenerateFormulaCpp.java)

[GenerateFormulaJava.java](GenerateFormulaJava.java)

[GenerateFormulaJavascript.java](GenerateFormulaJavascript.java)

(These generators inherit common code from [GenerateFormulaBaseCodeGen.java](GenerateFormulaBaseCodeGen.java) )


╭ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╮<br>
║ Prover does not do anything right now.<br>
╰ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╯<br>

[Prover.java](Prover.java)

╭ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╮<br>
║ Rendering of documentation and code uses a canvas.<br>
╰ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╯<br>

The canvas is an output device that can place boxes horizontally,
frame them or simply output lines as is. This is useful for outputting
on the terminal, but also generating formatted source code.

[Canvas.java](Canvas.java)
[CanvasEncoding.java](CanvasEncoding.java)


╭ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╮<br>
║ Utility functions.<br>
╰ ╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╯<br>

[Log.java](Log.java)

[LogLevel.java](LogLevel.java)

[LogModule.java](LogModule.java)

[Pair.java](Pair.java)

[Util.java](Util.java)
