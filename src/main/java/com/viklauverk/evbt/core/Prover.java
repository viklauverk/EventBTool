/*
 Copyright (C) 2021 Viklauverk AB (agpl-3.0-or-later)

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

import java.util.Hashtable;
import java.util.LinkedList;

public
class Prover
{
    SymbolTable fc = SymbolTable.PQR_EFG_STU_xyz_cdf_NM_ABC_H_cx_dx_op;

    // _a stands for antecedent
    // _c stands for consequent

    // An inference rule, used backwards:
    //
    // H,P |- Q       antecedent Then we can instead prove H,P entails Q.
    // ---------
    // H |- P=>Q      consequent If we have H entails P=>Q

    Formula DR1_a= Formula.fromString ("P", fc);
    Formula DR1_c = Formula.fromString ("not(not(P))", fc);

    Formula DR2_a1= Formula.fromString ("P", fc);
    Formula DR2_a2= Formula.fromString ("not(Q)", fc);
    Formula DR2_c = Formula.fromString ("not(P=>Q)", fc);

    Formula DR3_a = Formula.fromString ("P=>not(Q)", fc);
    Formula DR3_c = Formula.fromString ("not(P&Q)", fc);

    Formula DR4_a = Formula.fromString ("P=>R", fc);
    Formula DR4_c = Formula.fromString ("not(not(P))=>R", fc);

    Formula DR5_a = Formula.fromString ("P=>(not(Q)=>R)", fc);
    Formula DR5_c = Formula.fromString ("not(P=>Q)=>R", fc);

    Formula DR6_a1 = Formula.fromString ("not(P)=>R", fc);
    Formula DR6_a2 = Formula.fromString ("not(Q)=>R", fc);
    Formula DR6_c = Formula.fromString ("not(P&Q)=>R", fc);

    Formula DR7_a1 = Formula.fromString ("not(P)=>R", fc);
    Formula DR7_a2 = Formula.fromString ("Q=>R", fc);
    Formula DR7_c = Formula.fromString ("(P=>Q)=>R", fc);

    Formula DR8_a = Formula.fromString ("P=>(Q=>R)", fc);
    Formula DR8_c = Formula.fromString ("(P&Q)=>R", fc);

    Formula DR14_a = Formula.fromString ("!x.not(P) => R", fc);
    Formula DR14_c = Formula.fromString ("not(#x.P) => R", fc);

    Formula DR15_a = Formula.fromString ("!x.not(P)", fc);
    Formula DR15_c = Formula.fromString ("not(#x.P)", fc);

    Formula DR16_a = Formula.fromString ("#x.not(P)", fc);
    Formula DR16_c = Formula.fromString ("not(!x.P)", fc);

    Formula CNJ_a1 = Formula.fromString ("P", fc);
    Formula CNJ_a2 = Formula.fromString ("Q", fc);
    Formula CNJ_c = Formula.fromString ("P&Q", fc);

    LinkedList<Formula> hyps = new LinkedList<>();
    Hashtable<Formula,Formula>  used_for_dr11s = new Hashtable<>();

    int last_variable_in_hyps;

    /*
    boolean tryToProve (Formula f)
    {
        f = f.normalize ();
        return tryToProveData (f, "");
    }

    boolean tryToProveData (Formula f, String indent)
    {
        while (true)
        {
            System.out.println (indent+"Trying to prove:");
            int j = 0;
            System.out.print (indent);
            Iterator<Formula> i = hyps.iterator();
            while (i.hasNext())
            {
                if (j>0) System.out.print (",");
                j++;
                Formula fo = i.next();
                FormulaPrinter.print(fo);
            }
            System.out.print (" |- ");

            FormulaPrinter.println (f);

            boolean b;

            // Is goal an implication and the implicated is in HYP?
            if (tryBS1 (f))
            {
                System.out.println (indent+"Discharged by BS1");
                return true;
            }
            // Is goal in HYP?
            if (tryBS2 (f))
            {
                System.out.println (indent+"Discharged by BS2");
                return true;
            }

            // Is goal an implication and the implicee is in HYP?
            if (tryDB1 (f))
            {
                System.out.println (indent+"Discharged by DB1");
                return true;
            }

            // Is goal an implication and the implicee is in HYP?
            if (tryDB2 (f))
            {
                System.out.println (indent+"Discharged by DB2");
                return true;
            }

            if (tryRule ("DR1", f, DR1_a, DR1_c,indent)) continue;
            if (tryRule2 ("DR2", f, DR2_a1, DR2_a2, DR2_c,indent)) return true;
            if (tryRule ("DR3", f, DR3_a, DR3_c,indent)) continue;
            if (tryRule ("DR4", f, DR4_a, DR4_c,indent)) continue;
            if (tryRule ("DR5", f, DR5_a, DR5_c,indent)) continue;
            if (tryRule2 ("DR6", f, DR6_a1, DR6_a2, DR6_c,indent)) return true;
            if (tryRule2 ("DR7", f, DR7_a1, DR7_a2, DR7_c,indent)) return true;
            if (tryRule ("DR8", f, DR8_a, DR8_c,indent)) continue;

            if (tryGEN (f, indent)) continue;
            if (tryDR9 (f, indent)) continue;
            if (tryDR10 (f, indent)) continue;

            if (tryDR12 (f, indent)) continue;
            if (tryDR13 (f, indent)) continue;

            if (tryRule ("DR14", f, DR14_a, DR14_c,indent)) continue;
            if (tryRule ("DR15", f, DR15_a, DR15_c,indent)) continue;
            if (tryRule ("DR16", f, DR16_a, DR16_c,indent)) continue;

            if (tryRule2 ("CNJ", f, CNJ_a1, CNJ_a2, CNJ_c,indent)) return true;

            // Ok, didn't work, lets try DED
            if (tryDED (f,indent)) continue;

            // Ok, try adding universal hyps when all else fails!
            if (tryDR11 (f, indent)) return true;

            break;
        }
        return false;
    }

    public
    void pushHyp (Formula f)
    {
        hyps.add (f);
    }

    public
    void popToDepth (int d)
    {
        while (hyps.size()>d) hyps.removeLast ();
    }

    public
    int hypDepth ()
    {
        return hyps.size();
    }

    public
    boolean hypsContains (Formula f)
    {
        Iterator<Formula> i = hyps.iterator();
        while (i.hasNext())
        {
            Formula ff = i.next();
            if (f.equals (ff))
            {
                return true;
            }
        }
        return false;
    }

    public
    boolean tryBS1 (Formula f)
    {
        if (!f.is(Part.IMPLICATION)) return false;
        return hypsContains (f.cdr());
    }

    public
    boolean tryBS2 (Formula f)
    {
        return hypsContains (f);
    }

    public
    boolean tryDB1 (Formula f)
    {
        if (!f.is(Part.IMPLICATION)) return false;
        if (!f.car().is(Part.NEGATION)) return false;
        return hypsContains (f.car().car());
    }

    public
    boolean tryDB2 (Formula f)
    {
        if (!f.is(Part.IMPLICATION)) return false;
        Formula check = FormulaFactory.newNegation(f.car());
        return hypsContains (check);
    }

    public
    boolean tryRule (String name,
                     Formula f,
                     Formula antecedent,
                     Formula consequent,
                     String indent)
    {
        TreeMap<String,Formula> preds = new TreeMap<>();
        TreeMap<String,Formula> vars = new TreeMap<>();
        boolean b = consequent.match (f, preds, vars);
        if (!b) return false;
        System.out.println (indent+"Applying "+name);
        f.set (antecedent.reinstate (preds, vars));
        return true;
    }

    public
    boolean tryRule2 (String name,
                      Formula f,
                      Formula antecedent1,
                      Formula antecedent2,
                      Formula consequent,
                      String indent)
    {
        TreeMap<String,Formula> preds = new TreeMap<>();
        TreeMap<String,Formula> vars = new TreeMap<>();
        boolean b = consequent.match (f, preds, vars);
        if (!b) return false;
        int depth = hypDepth ();
        System.out.println (indent+"Applying "+name+"1");
        b = tryToProveData (antecedent1.reinstate (preds, vars), indent+"    ");
        popToDepth (depth);
        System.out.println (indent+"Applying "+name+"2");
        if (b) b = tryToProveData (antecedent2.reinstate (preds, vars), indent+"    ");
        popToDepth (depth);
        return b;
    }

    public
    boolean tryGEN (Formula f, String indent)
    {
        // Check there is an universalq
        if (!f.is(Part.UNIVERSALQ)) return false;

        System.out.println (indent+"Applying GEN");
        f.set (f.removeQuantifier (hyps));
        return true;
    }

    public
    boolean tryDR9 (Formula f, String indent)
    {
        if (f.is(Part.IMPLICATION) &&
            f.car().is(Part.NEGATION) &&
            f.car().car().is(Part.UNIVERSALQ))
        {
            System.out.println (indent+"Applying DR9");

            Formula P = f.car().car();
            Formula R = f.cdr();

            P = P.removeQuantifier (hyps);

            f.set (FormulaFactory.newImplication (FormulaFactory.newNegation (P), R));
            return true;
        }
        return false;
    }

    public
    boolean tryDR10 (Formula f, String indent)
    {
        if (f.is(Part.NEGATION) &&
            f.car().is(Part.UNIVERSALQ))
        {
            System.out.println (indent+"Applying DR10");

            Formula P = f.car();

            P = P.removeQuantifier (hyps);

            f.set (FormulaFactory.newNegation (P));
            return true;
        }
        return false;
    }

    public
    boolean tryDR11 (Formula f, String indent)
    {
        Iterator<Formula> i = hyps.iterator();
        while (i.hasNext())
        {
            Formula h = i.next();
            if (h.is(Part.UNIVERSALQ))
            {
                // Check if it has been used already
                Formula hh = used_for_dr11s.get (h);
                if (hh!=null) continue;
                System.out.print (indent+"Trying DR11 with ");
                used_for_dr11s.put (h,h);
                FormulaPrinter.println(h);
                h = h.removeQuantifier (hyps);
                Formula p = FormulaFactory.newImplication (h, f);

                boolean b = tryToProveData (p, indent+"    ");
                used_for_dr11s.remove (h);
                if (b) return true;
            }
        }
        return false;
    }

    public
    boolean tryDR12 (Formula f, String indent)
    {
        if (f.is(Part.IMPLICATION) &&
            f.car().is(Part.EXISTENTIALQ))
        {
            System.out.println (indent+"Applying DR12");

            Formula P = f.car();
            Formula R = f.cdr();

            P = P.removeQuantifier (hyps);

            f.set (FormulaFactory.newImplication (P, R));
            return true;
        }
        return false;
    }

    public
    boolean tryDR13 (Formula f, String indent)
    {
        if (f.is(Part.EXISTENTIALQ))
        {
            System.out.println (indent+"Applying DR13");

            Formula P = f.removeQuantifier (hyps);

            f.set (P);
            return true;
        }
        return false;
    }


    public
    boolean existsInHyp (Formula f)
    {
        Iterator<Formula> i = hyps.iterator ();
        while (i.hasNext())
        {
            Formula h = i.next();
            if (h.equals (f)) return true;
        }
        return false;
    }

    public
    boolean tryDED (Formula f, String indent)
    {
        // Check there is an implication
        if (!f.is(Part.IMPLICATION)) return false;

        Formula p = f.car ();
        if (!existsInHyp (p))
        {
            System.out.println (indent+"Applying DED");
            pushHyp (f.car());
            f.set (f.cdr ());
            return true;
        }
        System.out.println (indent+"Ick, DED not applied due to loop!");
        return false;
    }

    public static
    void printFormulaMap (TreeMap<String,Formula> m)
    {
        Iterator<String> i = m.keySet().iterator();
        Iterator<Formula> j = m.values().iterator();
        while (i.hasNext())
        {
            String key = i.next();
            Formula ff = j.next();

            System.out.print (key+" === ");
            FormulaPrinter.println(ff);
        }
    }

    public
    void interactive ()
    {
        BufferedReader in = null;
        PrintStream    out = null;

        in = new BufferedReader (new InputStreamReader(System.in));
        out = System.out;

        String cmd = "";

        out.println ("Welcome to Beouf!");
        while (true)
        {
            try
            {
                out.print("boeuf>");
                out.flush();
                cmd = in.readLine();

                if (cmd == null) break;
                cmd = cmd.trim ();
                if (cmd.equals("")) continue;

                if (cmd.charAt (0)=='*')
                {
                    // Add formula
                    continue;
                }

                boolean b = tryToProve (Formula.fromString (cmd, fc));
                if (b)
                    System.out.println ("Proof succeeded!");
                else
                    System.out.println ("Failed!");
            }
            catch (java.io.IOException ex)
            {
                return;
            }
        }
    }

    public static
    void main (String[] args) throws IOException
    {
        Prover p = new Prover ();

        Formula a = Formula.fromString (
            "!(x).(x:S => x:T) => !(y).(!(x).(x:y => x:S)=>!(x).(x:y=>x:T))",
            SymbolTable.pqr_efg_stu);

        FormulaPrinter.println(a);
        a = a.normalize();
        a = a.fixSetStuff();
        FormulaPrinter.println(a);

        boolean b = p.tryToProve (a);
        if (b) System.out.println ("Succeeded!");
        else
            System.out.println ("Failed!");
    }

    */
    // GOAL  prove seat<:S & x:S => (seat \/ {x}) <: S

    // GOAL  prove seat:NAT => seat-1:NAT
}
