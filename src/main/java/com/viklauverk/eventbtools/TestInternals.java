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

import com.viklauverk.eventbtools.core.Bounds;
import com.viklauverk.eventbtools.core.Canvas;
import com.viklauverk.eventbtools.core.LogModule;
import com.viklauverk.eventbtools.core.LogLevel;
import com.viklauverk.eventbtools.core.FormulaBuilder;
import com.viklauverk.eventbtools.core.Formula;
import com.viklauverk.eventbtools.core.ContainingCardinality;
import com.viklauverk.eventbtools.core.Typing;
import com.viklauverk.eventbtools.core.Pattern;
import com.viklauverk.eventbtools.core.SymbolTable;
import com.viklauverk.eventbtools.core.RenderTarget;
import com.viklauverk.eventbtools.core.Settings;
import com.viklauverk.eventbtools.core.Unicode;


public class TestInternals
{

    public static void main(String[] args)
    {
        LogModule.setLogLevelFor("all", LogLevel.INFO);

        boolean ok = true;

        ok &= testCommandLine();
        ok &= testParserRenderer();
        ok &= testMatching();
        ok &= testCommentWrapping();

        /*
        ok &= testCanvas0();
        ok &= testCanvas1();
        ok &= testCanvas2();
        ok &= testCanvas3();
        ok &= testCanvas4();*/

        if (!ok)
        {
            System.out.println("ERROR(s) detected!");
            System.exit(1);
        }
        System.exit(0);
    }

    public static boolean testCommandLine()
    {
        boolean ok = true;
        Settings s = new Settings();
        String[] a1 = { "help" };
        Cmd c = CommandLine.parse(s, a1);

        ok &= c == Cmd.HELP;
        return ok;
    }

    public static boolean testMatching()
    {
        boolean ok = true;

        // Additional variables height speeds
        //            constants addition

        // Basic matching
        ok &= testMatch("height > 5", "x > E", "E=5 x=height");
        ok &= testMatch("speeds ≔ speeds ∪ {72}", "x ≔ x ∪ {E}", "E=72 x=speeds");
        ok &= testMatch("addition(7)", "c(E)", "E=7 c=addition");
        ok &= testMatch("addition(7)", "c(N)", "c=addition N=7");
        ok &= testMatch("{1|->2}(1) = 2", "E(F) = N", "E={1↦2} F=1 N=2");

        // Test Meta data matching
        ok &= testMatch("height«5»", "x«N»", "x=height N=5");
//        ok &= testMatch("speeds«set|->»", "x«N»", "x=height N=5");

        ok &= testFailedMatch("height ≔ speeds ∪ {72}", "x ≔ x ∪ {E}", "FAILURE"); // Should be same variable in both x positions.

        return ok;
    }

    public static boolean testFailedMatch(String f, String p, String r)
    {
        boolean ok = testMatch(f, p, r, true);
        return !ok;
    }

    public static boolean testMatch(String f, String p, String r)
    {
        return testMatch(f, p, r, false);
    }

    public static boolean testMatch(String f, String p, String r, boolean expect_fail)
    {
        SymbolTable symbols = new SymbolTable("root");
        symbols.addVariableSymbols("height", "speeds");
        symbols.addConstantSymbols("addition");

        Formula formula = Formula.fromString(f, symbols);
        Pattern pattern = new Pattern();

        boolean or = pattern.match(formula, "", p);

        if (!or) {
            if (!expect_fail)
            {
                System.out.println("ERROR could not match \""+f+"\" to \""+p+"\"");
            }
            return false;
        }

        String m = pattern.allMatches();

        if (!r.equals(m))
        {
            System.out.println("ERROR expected \""+r+"\" but got \""+m+"\"");
            return false;
        }
        return true;
    }

    public static boolean testParserRenderer()
    {
        boolean ok = true;

        //////////////////////////////////////
        // Test symbols
        ok &= check("P", "P", "<PREDICATE_SYMBOL P>");
        ok &= check("E", "E", "<EXPRESSION_SYMBOL E>");
        ok &= check("S", "S", "<SET_SYMBOL S>");

        ok &= check("x", "x", "<VARIABLE_SYMBOL x>");
        ok &= check("c", "c", "<CONSTANT_SYMBOL c>");
        ok &= check("N", "N", "<NUMBER_SYMBOL N>");

        //////////////////////////////////////
        // Test predicates

        ok &= check("false", "⊥", "<FALSE ⊥>");
        ok &= check("true", "⊤", "<TRUE ⊤>");

        //     conjunction
        ok &= check("P&Q", "P∧Q", "<CONJUNCTION <PREDICATE_SYMBOL P>∧<PREDICATE_SYMBOL Q>>");
        // Check that & is left associative.
        ok &= check("P&Q&R", "P∧Q∧R", "<CONJUNCTION <CONJUNCTION <PREDICATE_SYMBOL P>∧<PREDICATE_SYMBOL Q>>∧<PREDICATE_SYMBOL R>>");
        // Check that parenthesis can force a different association.
        //ok &= check("P&(Q&R)", "P∧(Q∧R)", "<CONJUNCTION <PREDICATE_SYMBOL P>∧(<CONJUNCTION <PREDICATE_SYMBOL Q>∧<PREDICATE_SYMBOL R>>)>");

        //     disjunction
        ok &= check("P or Q", "P∨Q", "<DISJUNCTION <PREDICATE_SYMBOL P>∨<PREDICATE_SYMBOL Q>>");
        ok &= check("P or Q or R", "P∨Q∨R", "<DISJUNCTION <DISJUNCTION <PREDICATE_SYMBOL P>∨<PREDICATE_SYMBOL Q>>∨<PREDICATE_SYMBOL R>>");

        //     implication
        ok &= check("P=>Q", "P⇒ Q", "<IMPLICATION <PREDICATE_SYMBOL P>⇒ <PREDICATE_SYMBOL Q>>");
        //ok &= check("(P=>Q)=>R", "(P⇒ Q)⇒ R", "<IMPLICATION (<IMPLICATION <PREDICATE_SYMBOL P>⇒ <PREDICATE_SYMBOL Q>>)⇒ <PREDICATE_SYMBOL R>>");
        //ok &= checkFail("P=>Q=>R");

        //     equivalence
        ok &= check("P<=>Q", "P⇔ Q", "<EQUIVALENCE <PREDICATE_SYMBOL P>⇔ <PREDICATE_SYMBOL Q>>");

        //     negation
        ok &= check("not P", "¬P", "<NEGATION ¬<PREDICATE_SYMBOL P>>");

        //     universal quantifier
        ok &= check("!a.a:NAT1=>a>0", "∀a·a∈ℕ1⇒ a>0", "<UNIVERSALQ ∀<LIST_OF_NONFREE_VARIABLES <VARIABLE_NONFREE_SYMBOL a>>·<IMPLICATION <MEMBERSHIP <VARIABLE_NONFREE_SYMBOL a>∈<NAT1_SET ℕ1>>⇒ <GREATER_THAN <VARIABLE_NONFREE_SYMBOL a>><NUMBER 0>>>>");

        //     existential quantifier
        ok &= check("#a.a:NAT", "∃a·a∈ℕ", "<EXISTENTIALQ ∃<LIST_OF_NONFREE_VARIABLES <VARIABLE_NONFREE_SYMBOL a>>·<MEMBERSHIP <VARIABLE_NONFREE_SYMBOL a>∈<NAT_SET ℕ>>>");
        //     equality
        ok &= check("x=17", "x=17", "<EQUALS <VARIABLE_SYMBOL x>=<NUMBER 17>>");

        //     inequality
        ok &= check("16/=17", "16≠17", "<NOT_EQUALS <NUMBER 16>≠<NUMBER 17>>");

        //////////////////////////////////////
        // Test sets

        //     singleton set
        ok &= check("{1}", "{1}", "<ENUMERATED_SET {<NUMBER 1>}>");

        //     enumeration
        ok &= check("{1, 2, 3}", "{1,2,3}", "<ENUMERATED_SET {<NUMBER 1>,<NUMBER 2>,<NUMBER 3>}>");

        //     empty set
        ok &= check("{}", "∅", "<EMPTY_SET ∅>");

        //     set comprehension
        ok &= check("{ a . true | 5 }", "{a·⊤|5}", "<SET_COMPREHENSION {<LIST_OF_NONFREE_VARIABLES <VARIABLE_NONFREE_SYMBOL a>>·<TRUE ⊤>|<NUMBER 5>}>");

        //     set comprehension special form, an expression to the left
        ok &= check("{ x+y | x:NAT & y:NAT }", "{x+y|x∈ℕ∧y∈ℕ}", "<SET_COMPREHENSION_SPECIAL {<ADDITION <VARIABLE_NONFREE_SYMBOL x>+<VARIABLE_NONFREE_SYMBOL y>>|<CONJUNCTION <MEMBERSHIP <VARIABLE_NONFREE_SYMBOL x>∈<NAT_SET ℕ>>∧<MEMBERSHIP <VARIABLE_NONFREE_SYMBOL y>∈<NAT_SET ℕ>>>}>");
        // dest := { x |-> y | x : S ∧ y=12 }

        //     set comprehension list of variables to the left
        ok &= check("{ x | x:NAT }", "{x|x∈ℕ}", "<SET_COMPREHENSION_SPECIAL {<VARIABLE_NONFREE_SYMBOL x>|<MEMBERSHIP <VARIABLE_NONFREE_SYMBOL x>∈<NAT_SET ℕ>>}>");

        //     union
        ok &= check("S \\/ T", "S∪T", "<SET_UNION <SET_SYMBOL S>∪<SET_SYMBOL T>>");

        //     disjunction
        ok &= check("S /\\ T", "S∩T", "<SET_INTERSECTION <SET_SYMBOL S>∩<SET_SYMBOL T>>");

        //     difference (set minus)
        ok &= check("S \\ T", "S\\T", "<SET_MINUS <SET_SYMBOL S>\\<SET_SYMBOL T>>");

        //     ordered pair
        ok &= check("1|->2", "1↦2", "<MAPSTO <NUMBER 1>↦<NUMBER 2>>");

        //     complicated pair structure
        ok &= check("x = 1 |-> (2|-> (3|->4))", "x=1↦(2↦(3↦4))", "<EQUALS <VARIABLE_SYMBOL x>=<MAPSTO <NUMBER 1>↦<PARENTHESISED_EXPRESSION (<MAPSTO <NUMBER 2>↦<PARENTHESISED_EXPRESSION (<MAPSTO <NUMBER 3>↦<NUMBER 4>>)>>)>>>");

        //     projection1
        ok &= check("{1|->2} <| prj1", "{1↦2}◁ prj1 ", "<DOMAIN_RESTRICTION <ENUMERATED_SET {<MAPSTO <NUMBER 1>↦<NUMBER 2>>}>◁<PRJ1  prj1 >>");

        //     projection2
        ok &= check("{1|->2} <| prj2", "{1↦2}◁ prj2 ", "<DOMAIN_RESTRICTION <ENUMERATED_SET {<MAPSTO <NUMBER 1>↦<NUMBER 2>>}>◁<PRJ2  prj2 >>");

        //     cartesian product
        ok &= check("S**T", "S×T", "<CARTESIAN_PRODUCT <SET_SYMBOL S>×<SET_SYMBOL T>>");

        //     power set
        ok &= check("POW(S)", "ℙ(S)", "<POWER_SET ℙ(<SET_SYMBOL S>)>");

        //     power set (without the empty subset)
        ok &= check("POW1(S)", "ℙ1(S)", "<POWER1_SET ℙ1(<SET_SYMBOL S>)>");

        //     cardinality
        ok &= check("card(S)", "card(S)", "<CARDINALITY card(<SET_SYMBOL S>)>");

        //     generalized union
        ok &= check("union(U)", "union(U)", "<G_UNION union(<SET_SYMBOL U>)>");

        //     generalized intersection
        ok &= check("inter(U)", "inter(U)", "<G_INTER inter(<SET_SYMBOL U>)>");

        //     quantified union
        ok &= check("UNION a,b.a:NAT&b:NAT|S", "⋃a,b.a∈ℕ∧b∈ℕ|S", "<Q_UNION ⋃<LIST_OF_NONFREE_VARIABLES <VARIABLE_NONFREE_SYMBOL a>,<VARIABLE_NONFREE_SYMBOL b>>.<CONJUNCTION <MEMBERSHIP <VARIABLE_NONFREE_SYMBOL a>∈<NAT_SET ℕ>>∧<MEMBERSHIP <VARIABLE_NONFREE_SYMBOL b>∈<NAT_SET ℕ>>>|<SET_SYMBOL S>>");

        //     quantified intersection
        ok &= check("INTER a,b,c . a:NAT & b:NAT & c:NAT | S ", "⋂a,b,c.a∈ℕ∧b∈ℕ∧c∈ℕ|S", "<Q_INTER ⋂<LIST_OF_NONFREE_VARIABLES <VARIABLE_NONFREE_SYMBOL a>,<VARIABLE_NONFREE_SYMBOL b>,<VARIABLE_NONFREE_SYMBOL c>>.<CONJUNCTION <CONJUNCTION <MEMBERSHIP <VARIABLE_NONFREE_SYMBOL a>∈<NAT_SET ℕ>>∧<MEMBERSHIP <VARIABLE_NONFREE_SYMBOL b>∈<NAT_SET ℕ>>>∧<MEMBERSHIP <VARIABLE_NONFREE_SYMBOL c>∈<NAT_SET ℕ>>>|<SET_SYMBOL S>>");


        // Set predicates

        //     membership
        ok &= check("E:S", "E∈S", "<MEMBERSHIP <EXPRESSION_SYMBOL E>∈<SET_SYMBOL S>>");

        //     non-membership
        ok &= check("E/:S", "E∉S", "<NOT_MEMBERSHIP <EXPRESSION_SYMBOL E>∉<SET_SYMBOL S>>");

        //     subset
        ok &= check("S<:T", "S⊆T", "<SUBSET <SET_SYMBOL S>⊆<SET_SYMBOL T>>");

        //     not a subset
        ok &= check("S/<:T", "S⊈T", "<NOT_SUBSET <SET_SYMBOL S>⊈<SET_SYMBOL T>>");

        //     strict (proper) subset
        ok &= check("S<<:T", "S⊂T", "<STRICT_SUBSET <SET_SYMBOL S>⊂<SET_SYMBOL T>>");

        //     not a strict (proper) subset
        ok &= check("S/<<:T", "S⊄T", "<NOT_STRICT_SUBSET <SET_SYMBOL S>⊄<SET_SYMBOL T>>");

        //     finite
        ok &= check("finite(S)", "finite(S)", "<FINITE finite(<SET_SYMBOL S>)>");

        //     partition
        ok &= check("partition(S,{1},{2})", "partition(S,{1},{2})", "<PARTITION partition(<SET_SYMBOL S>,<LIST_OF_EXPRESSIONS <ENUMERATED_SET {<NUMBER 1>}>,<ENUMERATED_SET {<NUMBER 2>}>>)>");

        //     BOOL
        ok &= check("bool(P):BOOL", "bool(P)∈BOOL", "<MEMBERSHIP <TEST_BOOL bool(<PREDICATE_SYMBOL P>)>∈<BOOL_SET BOOL>>");

        // Numbers

        //    integers
        ok &= check("INT", "ℤ", "<INT_SET ℤ>");

        //    natural numbers
        ok &= check("NAT", "ℕ", "<NAT_SET ℕ>");

        //    positive natural numbers > 0
        ok &= check("NAT1", "ℕ1", "<NAT1_SET ℕ1>");

        //    minimum
        ok &= check("min(S)", "min(S)", "<MINIMUM min(<SET_SYMBOL S>)>");

        //    maximum
        ok &= check("max(S)", "max(S)", "<MAXIMUM max(<SET_SYMBOL S>)>");

        //    addition
        ok &= check("E+F", "E+F", "<ADDITION <EXPRESSION_SYMBOL E>+<EXPRESSION_SYMBOL F>>");

        //    subtraction
        ok &= check("E-F", "E-F", "<SUBTRACTION <EXPRESSION_SYMBOL E>-<EXPRESSION_SYMBOL F>>");

        //    multiplication
        ok &= check("E*F", "E∗F", "<MULTIPLICATION <EXPRESSION_SYMBOL E>∗<EXPRESSION_SYMBOL F>>");

        //    division
        ok &= check("E/F", "E÷F", "<DIVISION <EXPRESSION_SYMBOL E>÷<EXPRESSION_SYMBOL F>>");

        //    remainder
        ok &= check("23 mod 7", "23 mod 7", "<MODULO <NUMBER 23> mod <NUMBER 7>>");

        //    interval
        ok &= check("7 : 1..32", "7∈1‥32", "<MEMBERSHIP <NUMBER 7>∈<UP_TO <NUMBER 1>‥<NUMBER 32>>>");

        //    vector
        ok &= check("(1..32) --> NAT", "(1‥32)→ ℕ", "<TOTAL_FUNCTION <PARENTHESISED_EXPRESSION (<UP_TO <NUMBER 1>‥<NUMBER 32>>)>→ <NAT_SET ℕ>>");

        // Number predicates

        //    greater
        ok &= check("4711 > 17", "4711>17", "<GREATER_THAN <NUMBER 4711>><NUMBER 17>>");

        //    less
        ok &= check("17 < 4711", "17<4711", "<LESS_THAN <NUMBER 17><<NUMBER 4711>>");

        //    greater or equal
        ok &= check("4711 >= 17", "4711≥17", "<GREATER_THAN_OR_EQUAL <NUMBER 4711>≥<NUMBER 17>>");

        //    less or equal
        ok &= check("17 <= 4711", "17≤4711", "<LESS_THAN_OR_EQUAL <NUMBER 17>≤<NUMBER 4711>>");

        // Relations

        //     relation
        ok &= check("S <-> T", "S↔ T", "<RELATION <SET_SYMBOL S>↔ <SET_SYMBOL T>>");

        //     domain of relation
        ok &= check("dom(S)", "dom(S)", "<DOMAIN dom(<SET_SYMBOL S>)>");

        //     range of relation
        ok &= check("ran(S)", "ran(S)", "<RANGE ran(<SET_SYMBOL S>)>");

        //     total relation
        ok &= check("S <<-> T", "S T", "<TOTAL_RELATION <SET_SYMBOL S> <SET_SYMBOL T>>");

        //     surjective relation
        ok &= check("S <->> T", "S T", "<SURJECTIVE_RELATION <SET_SYMBOL S> <SET_SYMBOL T>>");

        //     total surjective relation
        ok &= check("S <<->> T", "S T", "<SURJECTIVE_TOTAL_RELATION <SET_SYMBOL S> <SET_SYMBOL T>>");

        //     forward composition
        ok &= check("S ; T", "S;T", "<FORWARD_COMPOSITION <SET_SYMBOL S>;<SET_SYMBOL T>>");

        //     backward composition
        ok &= check("S circ T", "S∘T", "<BACKWARD_COMPOSITION <SET_SYMBOL S>∘<SET_SYMBOL T>>");

        //     identity set
        ok &= check("id", " id ", "<ID_SET  id >");

        //     domain restriction S◁r keep all x|->y pairs where x:S
        ok &= check("S <| T", "S◁T", "<DOMAIN_RESTRICTION <SET_SYMBOL S>◁<SET_SYMBOL T>>");

        //     domain subtraction S⩤r remove all x|->y pairs where x:S
        ok &= check("S <<| T", "S⩤T", "<DOMAIN_SUBTRACTION <SET_SYMBOL S>⩤<SET_SYMBOL T>>");

        //     domain restriction r▷S keep all x|->y pairs where x:S
        ok &= check("T |> S", "T▷S", "<RANGE_RESTRICTION <SET_SYMBOL T>▷<SET_SYMBOL S>>");

        //     domain subtraction r⩥S remove all x|->y pairs where x:S
        ok &= check("T |>> S", "T⩥S", "<RANGE_SUBTRACTION <SET_SYMBOL T>⩥<SET_SYMBOL S>>");

        //     invert relation
        ok &= check("S~", "S~", "<INVERT <SET_SYMBOL S>~>");

        //     relational image
        ok &= check("S[T]", "S[T]", "<RELATION_IMAGE <SET_SYMBOL S>[<SET_SYMBOL T>]>");

        //     overriding
        ok &= check("S<+T", "ST", "<OVERRIDE <SET_SYMBOL S><SET_SYMBOL T>>");

        //     direct product
        ok &= check("S><T", "S⊗T", "<DIRECT_PRODUCT <SET_SYMBOL S>⊗<SET_SYMBOL T>>");

        //     parallel product
        ok &= check("S||T", "S∥T", "<PARALLEL_PRODUCT <SET_SYMBOL S>∥<SET_SYMBOL T>>");

        /*
        //     projection1
        ok &= check("(S**T)<|prj1", "S×T◁ prj1 ", "<DOMAIN_RESTRICTION <CARTESIAN_PRODUCT <SET_SYMBOL S>×<SET_SYMBOL T>>◁<PRJ1_SET  prj1 >>");

        //     projection2
        ok &= check("(S**T)<|prj2", "S×T◁ prj2 ", "<DOMAIN_RESTRICTION <CARTESIAN_PRODUCT <SET_SYMBOL S>×<SET_SYMBOL T>>◁<PRJ2_SET  prj2 >>");
        */
        // Functions

        //     partial function
        ok &= check("S+->T", "S⇸ T", "<PARTIAL_FUNCTION <SET_SYMBOL S>⇸ <SET_SYMBOL T>>");

        //     total function
        ok &= check("S-->T", "S→ T", "<TOTAL_FUNCTION <SET_SYMBOL S>→ <SET_SYMBOL T>>");

        //     partial injection
        ok &= check("S>+>T", "S⤔ T", "<PARTIAL_INJECTION <SET_SYMBOL S>⤔ <SET_SYMBOL T>>");

        //     total injection
        ok &= check("S>->T", "S↣ T", "<TOTAL_INJECTION <SET_SYMBOL S>↣ <SET_SYMBOL T>>");

        //     partial surjection
        ok &= check("S+->>T", "S⤀ T", "<PARTIAL_SURJECTION <SET_SYMBOL S>⤀ <SET_SYMBOL T>>");

        //     total surjection
        ok &= check("S-->>T", "S↠ T", "<TOTAL_SURJECTION <SET_SYMBOL S>↠ <SET_SYMBOL T>>");

        //     bijection
        ok &= check("S>->>T", "S⤖ T", "<TOTAL_BIJECTION <SET_SYMBOL S>⤖ <SET_SYMBOL T>>");

        //     lambda abstraction
        ok &= check("(%a.a:NAT|a+4711)", "(λa·a∈ℕ|a+4711)", "<PARENTHESISED_EXPRESSION (<LAMBDA_ABSTRACTION λ<LIST_OF_NONFREE_VARIABLES <VARIABLE_NONFREE_SYMBOL a>>·<MEMBERSHIP <VARIABLE_NONFREE_SYMBOL a>∈<NAT_SET ℕ>>|<ADDITION <VARIABLE_NONFREE_SYMBOL a>+<NUMBER 4711>>>)>");

        //     function application
        ok &= check("x=y(4711)", "x=y(4711)", "<EQUALS <VARIABLE_SYMBOL x>=<FUNC_APP <VARIABLE_SYMBOL y>(<NUMBER 4711>)>>");

        //     applied function returns function that is applied
        ok &= check("x(y)(4711)", "x(y)(4711)", "<FUNC_APP <FUNC_APP <VARIABLE_SYMBOL x>(<VARIABLE_SYMBOL y>)>(<NUMBER 4711>)>");

        //     function which is a enumerated set applied variable
        ok &= check("{1|->2, 2|->3}(2)", "{1↦2,2↦3}(2)", "<FUNC_APP <ENUMERATED_SET {<MAPSTO <NUMBER 1>↦<NUMBER 2>>,<MAPSTO <NUMBER 2>↦<NUMBER 3>>}>(<NUMBER 2>)>");

        return ok;
    }

    public static boolean check(String in, String out, String out_with_types)
    {
        SymbolTable fc = SymbolTable.PQR_EFG_STU_xyz_cdf_NM_ABC;

        Formula fo = Formula.fromString(in, fc);
        String ot = fo.toStringWithTypes();
        String o = fo.toString();
        boolean o1 = o.equals(out);
        boolean o2 = ot.equals(out_with_types);
        boolean ok = o1 && o2;

        if (!ok)
        {
            System.out.println("Error parsing "+in);
        }
        if (!o1)
        {
            System.out.println("Expected \""+out+"\"");
            System.out.println("But got  \""+o+"\"");
        }
        if (!o2)
        {
            System.out.println("Expected \""+out_with_types+"\"");
            System.out.println("But got  \""+ot+"\"");
        }
        return ok;
    }

    public static boolean checkFail(String in)
    {
        SymbolTable fc = SymbolTable.PQR_EFG_STU_xyz_cdf_NM_ABC;
        try
        {
            Formula f = Formula.fromString(in, fc);
        }
        catch (Exception e)
        {
            return true;
        }
        System.out.println("Expected parse fail for: "+in);
        return false;
    }

    public static boolean testCanvas0()
    {
        Canvas c = new Canvas();
        c.setRenderTarget(RenderTarget.PLAIN);

        c.startAlignments("0,0,0");
        c.append("alfa§beta§gamma§d\n");
        c.append("aaaaaaaaaaaa§bbbbbbbbbbbbbbbbbbbbbbbbbb§g§delta\n");
        c.append("a long comment xx yy xx yy xx yy xx yy\n");
        c.stopAlignments();

        String s = c.render();

        String check =
            "alfa         beta                       gamma d    \n"+
            "aaaaaaaaaaaa bbbbbbbbbbbbbbbbbbbbbbbbbb g     delta\n"+
            "a long comment xx yy xx yy xx yy xx yy\n";
        if (!check.equals(s))
        {
            System.out.println("Plain test, expected\n>>>\n"+check+"<<< but got\n>>>\n"+s+"<<<");
            return false;
        }

        c.setRenderTarget(RenderTarget.TEX);
        s = c.render();

        check =
            "\\begin{tabular}{lllll}\n"+
            "\\makecell[tl]{alfa}&\\makecell[tl]{beta}&\\makecell[tl]{gamma}&\\makecell[tl]{d}\\\\\n"+
            "\\makecell[tl]{aaaaaaaaaaaa}&\\makecell[tl]{bbbbbbbbbbbbbbbbbbbbbbbbbb}&\\makecell[tl]{g}&\\makecell[tl]{delta}\\\\\n"+
            "\\multicolumn{4}{l}{\\makecell[tl]{a long comment xx yy xx yy xx yy xx yy}}\\\\\n"+
            "\\end{tabular}\n";
        if (!check.equals(s))
        {
            System.out.println("Tex test, expected\n>>>\n"+check+"<<< but got\n>>>\n"+s+"<<<");
            return false;
        }

        c.setRenderTarget(RenderTarget.HTML);
        s = c.render();

        check =
            " table {\n"+
            " tr {  td { 'alfa' }  td { 'beta' }  td { 'gamma' }  td { 'd' }  }\n"+
            " tr {  td { 'aaaaaaaaaaaa' }  td { 'bbbbbbbbbbbbbbbbbbbbbbbbbb' }  td { 'g' }  td { 'delta' }  }\n"+
            " tr {  td(colspan=4) { 'a long comment xx yy xx yy xx yy xx yy' }  }\n"+
            " }\n";
        if (!check.equals(s))
        {
            System.out.println("Htmq test, expected\n>>>\n"+check+"<<< but got\n>>>\n"+s+"<<<");
            return false;
        }

        return true;
    }

    public static boolean testCanvas1()
    {
        Canvas c = new Canvas();
        c.setRenderTarget(RenderTarget.PLAIN);

        c.startAlignments("0,0,0");
        c.append("alfa:§"+Canvas.Red+"123"+Canvas.NoColor+"§34\n");
        c.append("beta:§123456§3445\n");
        c.append("gamma:§12§5\n");
        c.stopAlignments();
        c.startAlignments("0,0,0");
        c.append("....\n");
        c.append("xx§yy\n");
        c.append("x§y\n");
        c.append("§z\n");
        c.stopAlignments();

        String check =
            "alfa:  "+Canvas.Red+"123"+Canvas.NoColor+"    34  \n"+
            "beta:  123456 3445\n"+
            "gamma: 12     5   \n"+
            "....\n"+
            "xx yy\n"+
            "x  y \n"+
            "   z \n";

        String s = c.render();

        if (!check.equals(s))
        {
            System.out.println("Expected\n>>>\n"+check+"<<< but got\n>>>\n"+s+"<<<");
            return false;
        }
        return true;
    }


    public static boolean testCanvas2()
    {
        Canvas c = new Canvas();
        c.setRenderTarget(RenderTarget.PLAIN);

        c.startAlignments("0,50%,40%");
        c.append(
            "    grd1:§borr∈borrowers§// Valid borrower.\n"+
            "    grd2:§book∈books§// Valid book.\n"+
            "    grd3:§book↦borr∉loans§// Not a necessary test, but used for this example anyway.\n"+
            "    grd4:§book∉dom(loans)§// The book is not loaned out already.\n");
        c.stopAlignments();

        String check =
            "    grd1: borr∈borrowers  // Valid borrower.                                        \n"+
            "    grd2: book∈books      // Valid book.                                            \n"+
            "    grd3: book↦borr∉loans // Not a necessary test, but used for this example anyway.\n"+
            "    grd4: book∉dom(loans) // The book is not loaned out already.                    \n";
        String s = c.render();
        if (!check.equals(s))
        {
            System.out.println("Expected\n>>>\n"+check+"<<< but got\n>>>\n"+s+"<<<");
            return false;
        }
        return true;
    }

    public static boolean testCanvas3()
    {
        Canvas c = new Canvas();
        c.setRenderTarget(RenderTarget.TEX);

        c.startAlignments("0,50%,40%");
        c.append(
            "alfa:§123§Kommentarer\n"+
            "alfabeta:§1§No comments\n"+
            "gam:§1232424§Yes\n");
        c.stopAlignments();

        String check =
            "\\begin{tabular}{llll}\n"+
            "\\makecell[tl]{alfa:}&\\makecell[tl]{123}&\\makecell[tl]{Kommentarer}\\\\\n"+
            "\\makecell[tl]{alfabeta:}&\\makecell[tl]{1}&\\makecell[tl]{No comments}\\\\\n"+
            "\\makecell[tl]{gam:}&\\makecell[tl]{1232424}&\\makecell[tl]{Yes}\\\\\n"+
            "\\end{tabular}\n";
        String s = c.render();
        if (!check.equals(s))
        {
            System.out.println("Expected\n>>>\n"+check+"<<< but got\n>>>\n"+s+"<<<");
            return false;
        }
        return true;
    }

    public static boolean testCanvas4()
    {
        Canvas c = new Canvas();
        c.setRenderTarget(RenderTarget.HTML);

        c.startAlignments("0,0,0");
        c.append(
            "alfa:§123§Kommentarer\n"+
            "alfabeta:§1§No comments\n"+
            "gam:§1232424§Yes\n");
        c.stopAlignments();

        String check =
            " table {\n"+
            " tr {  td { 'alfa:' }  td { '123' }  td { 'Kommentarer' }  }\n"+
            " tr {  td { 'alfabeta:' }  td { '1' }  td { 'No comments' }  }\n"+
            " tr {  td { 'gam:' }  td { '1232424' }  td { 'Yes' }  }\n"+
            " }\n";

        String s = c.render();
        if (!check.equals(s))
        {
            System.out.println("Expected\n>>>\n"+check+"<<< but got\n>>>\n"+s+"<<<");
            return false;
        }
        return true;
    }

    public static boolean testCanvas5()
    {
        Canvas c = new Canvas();
        c.setRenderTarget(RenderTarget.TEX);

        c.startAlignments("0,50%,40%");
        c.append(
            "\\documentclass[10pt,a4paper]{article}\n"+
            "\\begin{document}\n"+
            "¤flush_raw¤\n"+
            "inv1:§x=123§\n"+
            "Mera kommenterar, riktigt många kommentarer.\n"+
            "inv2:§y<6 AND y!=123§Kommentar\n"+
            "¤flush_align¤\n");

            String check = "";

        String s = c.render();
        if (!check.equals(s))
        {
            System.out.println("Expected\n>>>\n"+check+"<<< but got\n>>>\n"+s+"<<<");
            return false;
        }
        return true;
    }

    public static boolean testCommentWrapping()
    {

        String s = Unicode.commentToTeX("HEjsan!\nHoppsan!\n\nNyrad\nSammarad\n");
        String check = "HEjsan! Hoppsan!  \\linebreak\\rule{0mm}{5mm}\\hspace{-1mm}  Nyrad Sammarad";
        if (!s.equals(check))
        {
            System.out.println("Expected\n>>>\n"+check+"<<< but got\n>>>\n"+s+"<<<");
            return false;
        }
        return true;
    }

}
