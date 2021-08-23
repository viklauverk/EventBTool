/*
 Copyright (C) 2021 Viklauverk AB
 Author Fredrik Öhrström

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

public class Context
{
    private static Log log = LogModule.lookup("context");

    private String name_;
    private String comment_;
    private List<Context> extends_contexts_;

    private Map<String,CarrierSet> sets_;
    private List<CarrierSet> set_ordering_;
    private List<String> set_names_;

    private Map<String,Constant> constants_;
    private List<Constant> constant_ordering_;
    private List<String> constant_names_;

    private Map<String,Axiom> axioms_;
    private List<Axiom> axiom_ordering_;
    private List<String> axiom_names_;

    private Map<String,ProofObligation> proof_obligations_ = new HashMap<>();
    private List<ProofObligation> proof_obligation_ordering_ = new ArrayList<>();
    private List<String> proof_obligation_names_ = new ArrayList<>();

    private boolean loaded_;
    private File buc_;
    private File bps_;
    private File bpo_;
    private Sys sys_;

    private SymbolTable symbol_table_;

    private EDKContext edk_context_; // Points to the proper EVBT supported EDK information.

    public Context(String n, Sys s, File f)
    {
        name_ = n;
        comment_ = "";
        extends_contexts_ = new ArrayList<>();
        sets_ = new HashMap<>();
        set_ordering_ = new ArrayList<>();
        set_names_ = new ArrayList<>();

        constants_ = new HashMap<>();
        constant_ordering_ = new ArrayList<>();
        constant_names_ = new ArrayList<>();

        axioms_ = new HashMap<>();
        axiom_ordering_ = new ArrayList<>();
        axiom_names_ = new ArrayList<>();

        loaded_ = false;
        buc_ = f;

        assert (buc_ != null) : "Source file must not be null!";

        bps_ = new File(f.getPath().replace(".buc", ".bps"));
        bpo_ = new File(f.getPath().replace(".buc", ".bpo"));

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

    public boolean hasExtend()
    {
        return extends_contexts_.size() > 0;
    }

    List<Context> extendsContexts()
    {
        return extends_contexts_;
    }

    SymbolTable symbolTable()
    {
        return symbol_table_;
    }

    public boolean hasSets()
    {
        return set_ordering_.size() > 0;
    }

    public boolean hasConstants()
    {
        return constant_ordering_.size() > 0;
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

    public void addSet(CarrierSet cs)
    {
        sets_.put(cs.name(), cs);
        set_ordering_.add(cs);
        set_names_ = sets_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public CarrierSet getSet(String name)
    {
        return sets_.get(name);
    }

    public CarrierSet getSetRecursive(String name)
    {
        CarrierSet cs = sets_.get(name);
        if (cs != null) return cs;
        for (Context c : extends_contexts_)
        {
            cs = c.getSetRecursive(name);
            if (cs != null) return cs;
        }
        return null;
    }

    public List<CarrierSet> setOrdering()
    {
        return set_ordering_;
    }

    public List<String> setNames()
    {
        return set_names_;
    }

    public void addConstant(Constant c)
    {
        constants_.put(c.name(), c);
        constant_ordering_.add(c);
        constant_names_ = constants_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public Constant getConstant(String name)
    {
        return constants_.get(name);
    }

    public Constant getConstantRecursive(String name)
    {
        Constant cs = constants_.get(name);
        if (cs != null) return cs;
        for (Context c : extends_contexts_)
        {
            cs = c.getConstantRecursive(name);
            if (cs != null) return cs;
        }
        return null;
    }

    public List<Constant> constantOrdering()
    {
        return constant_ordering_;
    }

    public List<String> constantNames()
    {
        return constant_names_;
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

    public boolean isEDKContext()
    {
        return edk_context_ != null;
    }

    public EDKContext edkContext()
    {
        return edk_context_;
    }

    public synchronized void loadBUC() throws Exception
    {
        if (loaded_) return;
        loaded_ = true;
        SAXReader reader = new SAXReader();

        log.debug("loading context "+buc_);

        Document document = reader.read(buc_);

        edk_context_ = sys().edk().lookup(name(), buc_);

        if (edk_context_ != null)
        {
            log.debug("detected EDK context "+name());
        }

        List<Node> context_comment = document.selectNodes("//org.eventb.core.contextFile");

        for (Node m : context_comment)
        {
            comment_ = m.valueOf("@org.eventb.core.comment");
        }

        // Does this context extend another context?
        List<Node> list = document.selectNodes("//org.eventb.core.extendsContext");
        for (Node n : list)
        {
            String name = n.valueOf("@org.eventb.core.target");
            Context c = sys_.getContext(name);
            assert (c != null) : "Error in loaded context xml file. Cannot find context "+name;
            extends_contexts_.add(c);
        }

        // Load the carrier sets.
        list = document.selectNodes("//org.eventb.core.carrierSet");
        for (Node n : list)
        {
            String name = n.valueOf("@org.eventb.core.identifier");
            String comment = n.valueOf("@org.eventb.core.comment");
            CarrierSet cs = new CarrierSet(name, this);
            cs.addComment(comment);
            addSet(cs);
        }

        // Load the constants.
        list = document.selectNodes("//org.eventb.core.constant");
        for (Node n : list)
        {
            String name = n.valueOf("@org.eventb.core.identifier");
            String comment = n.valueOf("@org.eventb.core.comment");
            Constant c = new Constant(name, this);
            c.addComment(comment);
            addConstant(c);
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
        Document document = reader.read(bps_);
        log.debug("loading context proof status file "+bps_);

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
        log.debug("loading checked types from context proof obligation file "+bpo_);

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

        // First take the machine variable types found in the
        // predicate set ABSHYP that are common for all proof obligations.
        List<Node> pos = document.selectNodes(
            "/org.eventb.core.poFile/org.eventb.core.poPredicateSet[@name='ABSHYP']/org.eventb.core.poIdentifier");
        for (Node r : pos)
        {
            String name = r.valueOf("@name").trim();
            String type = r.valueOf("@org.eventb.core.type").trim();
            Constant cons = getConstantRecursive(name);
            if (cons != null)
            {
                log.debug("found const identifier %s with type %s", name, type);
                cons.setCheckedTypeString(type); // sys_.typing().lookupCheckedType(type));
            }
            else
            {
                CarrierSet cs = getSetRecursive(name);
                if (cs != null)
                {
                    log.debug("found carrier set identifier %s with type %s", name, type);
                    // We do not need to set the checked type of a carrier set since
                    // the type is always by definition POW(CarrierSetName)
                }
                else
                {
                    if (name.endsWith("'")) continue;
                    log.error("could not find neither constant nor carrier set %s from file %s in context %s", name, bpo_, this);
                }
            }
        }
    }

    void buildSymbolTable()
    {
        if (symbol_table_ != null) return;

        log.debug("Building symbol table for context %s", this);

        List<SymbolTable> parents = new ArrayList<>();
        for (Context p : extends_contexts_)
        {
            p.buildSymbolTable();
            parents.add(p.symbolTable());
        }

        symbol_table_ = sys_.newSymbolTable(name_);
        symbol_table_.addParents(parents);

        for (CarrierSet cs : setOrdering())
        {
            log.debug("added carrier set %s to symbol table %s", cs.name(), symbol_table_.name());
            symbol_table_.addSet(cs);
        }
        for (Constant c : constantOrdering())
        {
            log.debug("added constant set %s to symbol table %s", c, symbol_table_.name());
            symbol_table_.addConstant(c);
        }
    }

    public void parse()
    {
        buildSymbolTable();
        log.debug("parsing %s", name());
        for (CarrierSet cs : setOrdering())
        {
            Formula f = FormulaFactory.newSetSymbol(cs.name());
            ImplType type = sys().typing().lookupImplType(f);
            log.debug("adding carrier set type: "+type.name());
        }
        for (Constant cons : constantOrdering())
        {
            cons.parseCheckedType(symbol_table_);
            log.debug("parsed checked type %s for constant %s", cons.checkedType(), cons.name());
        }
        for (Axiom a : axiomOrdering())
        {
            a.parse(symbol_table_);
            sys().typing().extractInfoFromAxiom(a.formula(), symbol_table_);
        }
    }
}
