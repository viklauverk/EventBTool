/*
 Copyright (C) 2021-2024 Viklauverk AB

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

package com.viklauverk.eventbtools.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Attribute;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class Theory
{
    private static Log log = LogModule.lookup("theory");

    private String name_;
    private String comment_;
    private List<Theory> imports_theories_;

    private Map<String,PolymorphicDataType> polymorphic_data_types_;
    private List<PolymorphicDataType> polymorphic_data_type_ordering_;
    private List<String> polymorphic_data_type_names_;

    private Map<String,Operator> operators_;
    private List<Operator> operator_ordering_;
    private List<String> operator_names_;

    private Map<String,Axiom> axioms_;
    private List<Axiom> axiom_ordering_;
    private List<String> axiom_names_;

    private Map<String,ProofObligation> proof_obligations_ = new HashMap<>();
    private List<ProofObligation> proof_obligation_ordering_ = new ArrayList<>();
    private List<String> proof_obligation_names_ = new ArrayList<>();

    private boolean loaded_;
    // If this theory was loaded from a dtf file, then it is a deployed theory that can be
    // loaded into a symbol table and used in contexts and machines.
    private boolean deployed_;
    private File tuf_; // Theory source
    private File dtf_; // Deployed theory
    private File bps_; // Proof statistics
    private File bpo_; // Proof obligations
    private File theory_root_dir_; // Where to look for imported theories.
    private Sys sys_;

    private SymbolTable symbol_table_;

    private EDKTheory edk_theory_; // Points to the proper EVBT supported EDK information.

    public Theory(String n, Sys s, File f, File trd)
    {
        name_ = n;
        comment_ = "";
        imports_theories_ = new ArrayList<>();

        polymorphic_data_types_ = new HashMap<>();
        polymorphic_data_type_ordering_ = new ArrayList<>();
        polymorphic_data_type_names_ = new ArrayList<>();

        operators_ = new HashMap<>();
        operator_ordering_ = new ArrayList<>();
        operator_names_ = new ArrayList<>();

        axioms_ = new HashMap<>();
        axiom_ordering_ = new ArrayList<>();
        axiom_names_ = new ArrayList<>();

        loaded_ = false;
        tuf_ = f;
        assert (tuf_ != null) : "Source file must not be null!";

        if (tuf_.getPath().endsWith(".dtf"))
        {
            tuf_ = new File(tuf_.getPath().replace(".dtf",".tuf"));
            deployed_ = true;
        }

        theory_root_dir_ = trd;
        //assert (theory_root_dir_ != null) : "Theory root dir must not be null!";

        bps_ = new File(tuf_.getPath().replace(".tuf", ".bps"));
        bpo_ = new File(tuf_.getPath().replace(".tuf", ".bpo"));
        dtf_ = new File(tuf_.getPath().replace(".tuf", ".dtf"));

        sys_ = s;

        symbol_table_ = null;
    }

    public Sys sys()
    {
        return sys_;
    }

    public String name()
    {
        return name_;
    }

    public String comment()
    {
        return comment_;
    }

    public boolean hasComment()
    {
        return !comment_.equals("");
    }

    public boolean hasImports()
    {
        return imports_theories_.size() > 0;
    }

    List<Theory> importsTheories()
    {
        return imports_theories_;
    }

    SymbolTable symbolTable()
    {
        return symbol_table_;
    }

    public boolean hasPolymorphicDataTypes()
    {
        return polymorphic_data_type_ordering_.size() > 0;
    }

    public boolean hasOperators()
    {
        return operator_ordering_.size() > 0;
    }

    public boolean hasAxioms()
    {
        return axiom_ordering_.size() > 0;
    }

    public boolean hasProofObligations()
    {
        return proof_obligations_.size() > 0;
    }

    public int numProvedAuto()
    {
        int n = 0;
        for (ProofObligation po : proof_obligation_ordering_)
        {
            if (po.isProvedAuto()) n++;
        }
        return n;
    }

    public int numProvedManualNotReviewed()
    {
        int n = 0;
        for (ProofObligation po : proof_obligation_ordering_)
        {
            if (po.isProvedManualNotReviewed()) n++;
        }
        return n;
    }

    public int numProvedManualReviewed()
    {
        int n = 0;
        for (ProofObligation po : proof_obligation_ordering_)
        {
            if (po.isProvedManualReviewed()) n++;
        }
        return n;
    }

    public int numUnproven()
    {
        int n = 0;
        for (ProofObligation po : proof_obligation_ordering_)
        {
            if (!po.hasProof()) n++;
        }
        return n;
    }

    public ProofObligation getProofObligation(String name)
    {
        return proof_obligations_.get(name);
    }

    List<ProofObligation> proofObligationOrdering()
    {
        return proof_obligation_ordering_;
    }

    public List<String> proofObligationNames()
    {
        return proof_obligation_names_;
    }

    public void addPolymorphicDataType(PolymorphicDataType pdt)
    {
        polymorphic_data_types_.put(pdt.baseName(), pdt);
        polymorphic_data_type_ordering_.add(pdt);
        polymorphic_data_type_names_ = polymorphic_data_types_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public PolymorphicDataType getPolymorphicDataType(String name)
    {
        return polymorphic_data_types_.get(name);
    }

    public PolymorphicDataType getPolymorphicDataTypeRecursive(String name)
    {
        PolymorphicDataType dt = polymorphic_data_types_.get(name);
        if (dt != null) return dt;
        for (Theory t : imports_theories_)
        {
            dt = t.getPolymorphicDataTypeRecursive(name);
            if (dt != null) return dt;
        }
        return null;
    }

    public List<PolymorphicDataType> polymorphicDataTypeOrdering()
    {
        return polymorphic_data_type_ordering_;
    }

    public List<String> polymorphicDataTypeNames()
    {
        return polymorphic_data_type_names_;
    }

    public void addOperator(Operator o)
    {
        operators_.put(o.name(), o);
        operator_ordering_.add(o);
        operator_names_ = operators_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public Operator getOperator(String name)
    {
        return operators_.get(name);
    }

    public Operator getOperatorRecursive(String name)
    {
        Operator o = operators_.get(name);
        if (o != null) return o;
        for (Theory t : imports_theories_)
        {
            o = t.getOperatorRecursive(name);
            if (o != null) return o;
        }
        return null;
    }

    public List<Operator> operatorOrdering()
    {
        return operator_ordering_;
    }

    public List<String> operatorNames()
    {
        return operator_names_;
    }

    public void addAxiom(Axiom a)
    {
        axioms_.put(a.name(), a);
        axiom_ordering_.add(a);
        axiom_names_ = axioms_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public Axiom getAxiom(String name)
    {
        return axioms_.get(name);
    }

    public List<Axiom> axiomOrdering()
    {
        return axiom_ordering_;
    }

    public List<String> axiomNames()
    {
        return axiom_names_;
    }

    public void addProofObligation(ProofObligation po)
    {
        proof_obligations_.put(po.name(), po);
        proof_obligation_ordering_.add(po);
        proof_obligation_names_ = proof_obligations_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public boolean isEDKTheory()
    {
        return edk_theory_ != null;
    }

    public EDKTheory edkTheory()
    {
        return edk_theory_;
    }

    public synchronized void loadDeployedDTF() throws Exception
    {
        if (loaded_) return;
        loaded_ = true;
        deployed_ = true;
        SAXReader reader = new SAXReader();

        log.debug("loading deployed theory "+dtf_);

        Document document = null;

        try
        {
            document = reader.read(dtf_);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.error("Failed loading deployed theory "+dtf_);
        }

        edk_theory_ = sys().edk().lookupTheory(name(), dtf_);

        if (edk_theory_ != null)
        {
            log.debug("detected EDK Deployed Theory "+name());
        }

        List<Node> theory_comment = document.selectNodes("//org.eventb.theory.core.theoryRoot");

        for (Node m : theory_comment)
        {
            comment_ = m.valueOf("@org.eventb.core.comment");
        }

        // Does this theory import other theories?
        /*
        List<Node> itps = document.selectNodes("//org.eventb.theory.core.importTheoryProject");
        for (Node itp : itps)
        {
            List<Node> its = itp.selectNodes("//org.eventb.theory.core.importTheory");
            for (Node it : its)
            {
                String s = it.valueOf("@org.eventb.theory.core.importTheory");
                int p = s.indexOf("|");
                int pp = s.indexOf("#");
                String dtf_file = s.substring(0, p); // Deployed Theory File
                String name = s.substring(pp+1);

                Theory t = sys_.getDeployedTheory(name);
                if (t == null)
                {
                    // Try to populate with this imported theory.
                    File source = new File(theory_root_dir_, dtf_file);
                    if (!source.exists() || !source.isFile())
                    {
                        LogModule.usageErrorStatic("Cannot find \"%s\" for theory \"%s\" which is imported from theory \"%s\"!\n"+
                                                   "Use --theory-root-dir=... to point where theory projects are located.",
                                                   source,
                                                   name,
                                                   name());
                    }
                    //sys().populateDeployedTheory(name, source, theory_root_dir_);
                    t = sys_.getDeployedTheory(name);
                }
                assert (t != null) : "Error in loaded theory xml file. Cannot find imported theory "+name;
                imports_theories_.add(t);
            }
        }
        */
        // Load the data types.
        List<Node> list = document.selectNodes("//org.eventb.theory.core.scDatatypeDefinition");
        for (Node n : list)
        {
            String name = n.valueOf("@name");
            String comment = n.valueOf("@org.eventb.core.comment");
            PolymorphicDataType pdt = new PolymorphicDataType(name, this);
            pdt.addComment(comment);
            addPolymorphicDataType(pdt);

            List<Node> constructors = n.selectNodes("//org.eventb.theory.core.scDatatypeConstructor");
            for (Node c : constructors)
            {
                name = c.valueOf("@name");
                pdt.addConstructor(new Constructor(name, pdt));
            }
        }

        // Load the operators.
        list = document.selectNodes("//org.eventb.core.operator");
        for (Node n : list)
        {
            String name = n.valueOf("@org.eventb.core.identifier");
            String comment = n.valueOf("@org.eventb.core.comment");
            Operator o = new Operator(name, this);
            o.addComment(comment);
            addOperator(o);
        }

        // Load the axioms and theorems.
        list = document.selectNodes("//org.eventb.core.axiom");
        for (Node n : list)
        {
            String name = n.valueOf("@org.eventb.core.label");
            String pred = n.valueOf("@org.eventb.core.predicate");
            String comment = n.valueOf("@org.eventb.core.comment");
            String is_theorem = n.valueOf("@org.eventb.core.theorem");

            boolean it = is_theorem.equals("true");
            Axiom a = new Axiom(name, pred, comment, it);
            addAxiom(a);
        }

        buildSymbolTable();
    }

    public synchronized void loadSourceTUF() throws Exception
    {
        if (loaded_) return;
        loaded_ = true;
        SAXReader reader = new SAXReader();

        log.debug("loading theory "+tuf_);

        Document document = null;

        try
        {
            document = reader.read(tuf_);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.error("Failed loading theory "+tuf_);
        }

        edk_theory_ = sys().edk().lookupTheory(name(), tuf_);

        if (edk_theory_ != null)
        {
            log.debug("detected EDK Theory "+name());
        }

        List<Node> theory_comment = document.selectNodes("//org.eventb.theory.core.theoryRoot");

        for (Node m : theory_comment)
        {
            comment_ = m.valueOf("@org.eventb.core.comment");
        }

        // Does this theory import other theories?
        List<Node> itps = document.selectNodes("//org.eventb.theory.core.importTheoryProject");
        for (Node itp : itps)
        {
            List<Node> its = itp.selectNodes("//org.eventb.theory.core.importTheory");
            for (Node it : its)
            {
                String s = it.valueOf("@org.eventb.theory.core.importTheory");
                int p = s.indexOf("|");
                int pp = s.indexOf("#");
                String dtf_file = s.substring(0, p); // Deployed Theory File
                String name = s.substring(pp+1);

                Theory t = sys_.getSourceTheory(name);
                if (t == null)
                {
                    // Try to populate with this imported theory.
                    File source = new File(theory_root_dir_, dtf_file);
                    if (!source.exists() || !source.isFile())
                    {
                        LogModule.usageErrorStatic("Cannot find \"%s\" for theory \"%s\" which is imported from theory \"%s\"!\n"+
                                                   "Use --theory-root-dir=... to point where theory projects are located.",
                                                   source,
                                                   name,
                                                   name());
                    }
                    //sys().populateDeployedTheory(name, source, theory_root_dir_);
                    t = sys_.getDeployedTheory(name);
                }
                assert (t != null) : "Error in loaded theory xml file. Cannot find imported theory "+name;
                imports_theories_.add(t);
            }
        }

        // Load the data types.
        List<Node> list = document.selectNodes("//org.eventb.theory.core.datatypeDefinition");
        for (Node n : list)
        {
            String name = n.valueOf("@org.eventb.core.identifier");
            String comment = n.valueOf("@org.eventb.core.comment");
            PolymorphicDataType dt = new PolymorphicDataType(name, this);
            dt.addComment(comment);
            addPolymorphicDataType(dt);
        }

        // Load the operators.
        list = document.selectNodes("//org.eventb.core.operator");
        for (Node n : list)
        {
            String name = n.valueOf("@org.eventb.core.identifier");
            String comment = n.valueOf("@org.eventb.core.comment");
            Operator o = new Operator(name, this);
            o.addComment(comment);
            addOperator(o);
        }

        // Load the axioms and theorems.
        list = document.selectNodes("//org.eventb.core.axiom");
        for (Node n : list)
        {
            String name = n.valueOf("@org.eventb.core.label");
            String pred = n.valueOf("@org.eventb.core.predicate");
            String comment = n.valueOf("@org.eventb.core.comment");
            String is_theorem = n.valueOf("@org.eventb.core.theorem");

            boolean it = is_theorem.equals("true");
            Axiom a = new Axiom(name, pred, comment, it);
            addAxiom(a);
        }
    }

    public void loadProofStatus() throws Exception
    {
        SAXReader reader = new SAXReader();
        if (!bps_.exists())
        {
            log.info("No proof status file: "+bps_);
            return;
        }

        Document document = reader.read(bps_);
        log.debug("loading theory proof status file "+bps_);

        // /aa/bb/machine.bps
        String filename = bps_.toString();
        String[] tokens = filename.split(".+?/(?=[^/]+$)");
        filename = tokens[1];
        filename = filename.replace(".bps", "");

        List<Node> pos = document.selectNodes("//org.eventb.core.psStatus");
        for (Node r : pos)
        {
            String name = r.valueOf("@name").trim();
            String conf = r.valueOf("@org.eventb.core.confidence").trim();
            String man  = r.valueOf("@org.eventb.core.psManual").trim();
            ProofObligation po = new ProofObligation(name, Integer.parseInt(conf), man.equals("true"));
            log.debug("PO %s %s proved_auto=%s proved_manual_not_reviewed=%s proved_manual_reviewed=%s unproven=%s",
                      filename, name, po.isProvedAuto(), po.isProvedManualNotReviewed(), po.isProvedManualReviewed(), !po.hasProof());

            addProofObligation(po);
        }
    }

    public void loadCheckedTypes() throws Exception
    {
        if (!bpo_.exists()) return;

        SAXReader reader = new SAXReader();
        Document document = reader.read(bpo_);
        log.debug("loading checked types from theory proof obligation file "+bpo_);

        Element pofile = (Element)document.selectSingleNode("/org.eventb.core.poFile");
        if (pofile == null)
        {
            log.warn("broken file %s, no root org.eventb.core.poFile  found.", bpo_);
            return;
        }
        if (pofile.content().size() == 0)
        {
            // This might be ok if there are no proof obligations?
            log.debug("empty file %s, no content inside org.eventb.core.poFile.", bpo_);
            return;
        }
    }

    void buildSymbolTable()
    {
        if (symbol_table_ != null) return;

        log.debug("building symbol table for theory: %s", name_);

        /*
        List<SymbolTable> parents = new ArrayList<>();
        for (Theory t : imports_theories_)
        {
            t.buildSymbolTable();
            parents.add(t.symbolTable());
        }
        */

        symbol_table_ = sys_.newTheorySymbolTable(name_);
        //symbol_table_.addParents(parents);

        for (PolymorphicDataType pdt : polymorphicDataTypeOrdering())
        {
            log.debug("added data type %s to symbol table %s", pdt.longName(), symbol_table_.name());
            symbol_table_.addPolymorphicDataType(pdt);
        }
        for (Operator c : operatorOrdering())
        {
            log.debug("added operator set %s to symbol table %s", c, symbol_table_.name());
            symbol_table_.addPolymorphicOperator(c);
        }
    }

    public void parse()
    {
        log.debug("parsing %s", name());
        for (PolymorphicDataType pdt : polymorphicDataTypeOrdering())
        {
            Formula f = FormulaFactory.newPolymorphicDataTypeSymbol(
                pdt.baseName(),
                new Formula(com.viklauverk.eventbtools.core.Node.LIST_OF_EXPRESSIONS, Formula.NO_META),
                Formula.NO_META);
            ImplType type = sys().typing().lookupImplType(f);
            log.debug("adding polymorphic data type: "+type.name());
        }
        for (Operator o : operatorOrdering())
        {
            o.parseCheckedType(symbol_table_);
            log.debug("parsed checked type %s for operator %s", o.checkedType(), o.name());
        }
        for (Axiom a : axiomOrdering())
        {
            a.parse(symbol_table_);
            sys().typing().extractInfoFromAxiom(a.formula(), symbol_table_);
        }
    }
}
