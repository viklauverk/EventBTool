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

package com.viklauverk.eventbtools.core;

import com.viklauverk.eventbtools.core.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.dom.DOMDocumentFactory;
import org.dom4j.Element;
import org.dom4j.Attribute;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class Machine
{
    private static LogModule log = LogModule.lookup("machine");
    private static LogModule log_codegen = LogModule.lookup("codegen");
    private static LogModule log_typing = LogModule.lookup("typing");

    private SymbolTable symbol_table_;

    private String name_;
    private Machine refines_;

    private Map<String,Context> contexts_ = new HashMap<>();
    private List<Context> context_ordering_ = new ArrayList<>();
    private List<String> context_names_ = new ArrayList<>();

    private Map<String,Variable> variables_ = new HashMap<>();
    private List<Variable> variable_ordering_ = new ArrayList<>();
    private List<String> variable_names_ = new ArrayList<>();

    private Map<String,Invariant> invariants_ = new HashMap<>();
    private List<Invariant> invariant_ordering_ = new ArrayList<>();
    private List<String> invariant_names_ = new ArrayList<>();

    private Map<String,Theorem> theorems_ = new HashMap<>();
    private List<Theorem> theorem_ordering_ = new ArrayList<>();
    private List<String> theorem_names_ = new ArrayList<>();

    private Map<String,Variant> variants_ = new HashMap<>();
    private List<Variant> variant_ordering_ = new ArrayList<>();
    private List<String> variant_names_ = new ArrayList<>();

    private Map<String,Event> events_ = new HashMap<>();
    private ArrayList<Event> event_ordering_ = new ArrayList<>();
    private List<String> event_names_ = new ArrayList<>();

    private Map<String,ProofObligation> proof_obligations_ = new HashMap<>();
    private List<ProofObligation> proof_obligation_ordering_ = new ArrayList<>();
    private List<String> proof_obligation_names_ = new ArrayList<>();

    // These are the calculated types that variables can be of.
    private Map<String,ImplType> types_;
    private List<String> type_names_;

    // The concrete events (merged with extended events) in
    // more abstract machines.
    private Map<String,Event> concrete_events_ = new HashMap<>();
    private List<Event> concrete_event_ordering_ = new ArrayList<>();
    private List<String> concrete_event_names_ = new ArrayList<>();

    String comment_; // Usually the copyright notice.

    Sys sys_;
    File bum_;
    File bpr_;
    File bps_;
    File bpo_;
    File bcc_;

    public Machine(String n, Sys s, File f)
    {
        name_ = n;

        types_ = new HashMap<>();
        type_names_ = new ArrayList<>();
        bum_ = f;
        sys_ = s;
        bum_ = f;

        bpr_ = new File(f.getPath().replace(".bum", ".bpr"));
        bps_ = new File(f.getPath().replace(".bum", ".bps"));
        bpo_ = new File(f.getPath().replace(".bum", ".bpo"));
        bcc_ = new File(f.getPath().replace(".bum", ".bcc"));
    }

    public SymbolTable symbolTable()
    {
        return symbol_table_;
    }

    public Sys sys()
    {
        return sys_;
    }

    public String info()
    {
        String s = "mch "+Util.padRightToLen(name_, ' ', 30)+" "+variables_.size()+" vars, "+events_.size()+" evts";
        if (refines_ != null)
        {
            s += " R "+refines_.name();
        }
        return s;
    }

    public String name()
    {
        return name_;
    }

    @Override
    public String toString()
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

    public Machine refines()
    {
        return refines_;
    }

    public boolean hasRefines()
    {
        return refines_ != null;
    }

    public String machineOrRefinement()
    {
        return (refines_ == null) ? "machine" : "refinement";
    }

    public Event getEvent(String name)
    {
        return events_.get(name);
    }

    public List<Event> eventOrdering()
    {
        return event_ordering_;
    }

    public boolean hasEvents()
    {
        return event_ordering_.size() > 0;
    }

    public List<String> eventNames()
    {
        return event_names_;
    }

    public boolean hasVariables()
    {
        return variables_.size() > 0;
    }

    public boolean hasInvariants()
    {
        return invariants_.size() > 0;
    }

    public boolean hasTheorems()
    {
        return theorems_.size() > 0;
    }

    public boolean hasVariants()
    {
        return variants_.size() > 0;
    }

    public Variable getVariable(String name)
    {
        return variables_.get(name);
    }

    public List<Variable> variableOrdering()
    {
        return variable_ordering_;
    }

    public List<String> variableNames()
    {
        return variable_names_;
    }

    public Invariant getInvariant(String name)
    {
        return invariants_.get(name);
    }

    public List<Invariant> invariantOrdering()
    {
        return invariant_ordering_;
    }

    public List<String> invariantNames()
    {
        return invariant_names_;
    }

    public Theorem getTheorem(String name)
    {
        return theorems_.get(name);
    }

    public List<Theorem> theoremOrdering()
    {
        return theorem_ordering_;
    }

    public List<String> theoremNames()
    {
        return theorem_names_;
    }

    public Variant getVariant(String name)
    {
        return variants_.get(name);
    }

    public List<Variant> variantOrdering()
    {
        return variant_ordering_;
    }

    public List<String> variantNames()
    {
        return variant_names_;
    }

    public boolean hasContexts()
    {
        return contexts_.size() > 0;
    }

    public Context getContext(String name)
    {
        return contexts_.get(name);
    }

    List<Context> contextOrdering()
    {
        return context_ordering_;
    }

    public List<String> contextNames()
    {
        return context_names_;
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

    public void addEvent(Event e)
    {
        events_.put(e.name(), e);
        event_ordering_.add(e);
        event_names_ = events_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public void addContext(Context c)
    {
        contexts_.put(c.name(), c);
        context_ordering_.add(c);
        context_names_ = contexts_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public void addVariable(Variable v)
    {
        variables_.put(v.name(), v);
        variable_ordering_.add(v);
        variable_names_ = variables_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public void addInvariant(Invariant inv)
    {
        invariants_.put(inv.name(), inv);
        invariant_ordering_.add(inv);
        invariant_names_ = invariants_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public void addTheorem(Theorem the)
    {
        theorems_.put(the.name(), the);
        theorem_ordering_.add(the);
        theorem_names_ = theorems_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public void addVariant(Variant variant)
    {
        variants_.put(variant.name(), variant);
        variant_ordering_.add(variant);
        variant_names_ = variants_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public void addProofObligation(ProofObligation po)
    {
        proof_obligations_.put(po.name(), po);
        proof_obligation_ordering_.add(po);
        proof_obligation_names_ = proof_obligations_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public void loadBUM() throws Exception
    {
        SAXReader reader = new SAXReader();
        Document document = null;
        try
        {
            document = reader.read(bum_);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.error("Failed loading machine from "+bum_);
        }
        log.debug("loading machine "+bum_);

        List<Node> machine_comment = document.selectNodes("//org.eventb.core.machineFile");

        for (Node m : machine_comment)
        {
            comment_ = m.valueOf("@org.eventb.core.comment");
        }

        List<Node> refines_machine = document.selectNodes("//org.eventb.core.refinesMachine");
        for (Node r : refines_machine)
        {
            String m = r.valueOf("@org.eventb.core.target");
            Machine mch = sys_.getMachine(m);
            if (mch == null) log.error("Error while loading machine %s, could not find refined machine %s", name(), m);
            if (refines_ != null) log.error("Error while loading machine %s, cannot refine more than one mache.");
            refines_ = mch;
        }

        List<Node> contexts = document.selectNodes("//org.eventb.core.seesContext");
        for (Node c : contexts)
        {
            String t = c.valueOf("@org.eventb.core.target");
            Context context = sys_.getContext(t);
            if (context == null) log.error("Error while loading machine %s, could not find context %s", name(), t);
            addContext(context);
        }

        List<Node> variables = document.selectNodes("//org.eventb.core.variable");
        for (Node v : variables)
        {
            String n = v.valueOf("@org.eventb.core.identifier");
            String c = v.valueOf("@org.eventb.core.comment");
            Variable var = new Variable(n, c, this, null);
            addVariable(var);
        }

        List<Node> invariants = document.selectNodes("//org.eventb.core.invariant");
        for (Node i : invariants)
        {
            String name = i.valueOf("@org.eventb.core.label").trim(); // Clean any spurious newlines at end of label.
            String fs = i.valueOf("@org.eventb.core.predicate");
            String comment = i.valueOf("@org.eventb.core.comment");
            String theorem = i.valueOf("@org.eventb.core.theorem");
            boolean is_theorem = theorem.equals("true");
            Invariant invar = new Invariant(name, fs, comment, is_theorem);
            addInvariant(invar);
        }

        List<Node> variants = document.selectNodes("//org.eventb.core.variant");
        for (Node i : variants)
        {
            String name = i.valueOf("@org.eventb.core.label");
            String expression = i.valueOf("@org.eventb.core.expression");
            String comment = Util.trimLines(i.valueOf("@org.eventb.core.comment"));
            Variant variant = new Variant(name, expression, comment);
            addVariant(variant);
        }

        List<Node> events = document.selectNodes("//org.eventb.core.event");
        for (Node e : events)
        {
            String name = e.valueOf("@org.eventb.core.label");
            String comment = e.valueOf("@org.eventb.core.comment");
            boolean ext = e.valueOf("@org.eventb.core.extended").equals("true");
            Convergence convergence = Convergence.from(e.valueOf("@org.eventb.core.convergence"));
            Event event = new Event(name, ext, comment, this, convergence);
            addEvent(event);

            List<Node> refines = e.selectNodes("org.eventb.core.refinesEvent");
            for (Node r : refines)
            {
                String identifier = r.valueOf("@org.eventb.core.target");
                event.addRefinesEventName(identifier);
            }
            if (event.name().equals("INITIALISATION") && event.extended())
            {
                event.addRefinesEventName("INITIALISATION");
            }
            List<Node> parameters = e.selectNodes("org.eventb.core.parameter");
            for (Node p : parameters)
            {
                String i = p.valueOf("@org.eventb.core.identifier");
                String c = p.valueOf("@org.eventb.core.comment");
                event.addParameter(new Variable(i, c, this, event));
            }

            List<Node> guards = e.selectNodes("org.eventb.core.guard");
            for (Node g : guards)
            {
                String l = g.valueOf("@org.eventb.core.label");
                String p = g.valueOf("@org.eventb.core.predicate");
                String c = g.valueOf("@org.eventb.core.comment");
                event.addGuard(new Guard(l, p, c));
            }

            List<Node> witnesses = e.selectNodes("org.eventb.core.witness");
            for (Node w : witnesses)
            {
                String l = w.valueOf("@org.eventb.core.label");
                String p = w.valueOf("@org.eventb.core.predicate");
                String c = w.valueOf("@org.eventb.core.comment");
                event.addWitness(new Witness(l, p, c));
            }

            List<Node> actions = e.selectNodes("org.eventb.core.action");
            for (Node a : actions)
            {
                String l = a.valueOf("@org.eventb.core.label");
                String f = a.valueOf("@org.eventb.core.assignment");
                String c = a.valueOf("@org.eventb.core.comment");
                event.addAction(new Action(l, f, c));
            }
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
        log.debug("loading machine proof status file "+bps_);

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

        log.debug("loading checked types from machine proof obligation file "+bpo_);

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
            log.debug("found identifier %s with type %s", name, type);
            Variable var = getVariable(name);
            if (var != null)
            {
                var.setCheckedTypeString(type); //sys_.typing().lookupCheckedType(type));
            }
            else
            {
                if (name.endsWith("'")) continue;
                log.info("could not find variable %s from file %s in machine %s", name, bpo_, this);
            }
        }

        // Now look for the checked types of the event parameters.
        for (Event event : eventOrdering())
        {
            // Look for the last PO for the event.
            List<Node> nodes = pofile.content();
            boolean found_event = false;
            for (Node i : nodes)
            {
                if (i.getNodeType() != Node.ELEMENT_NODE) continue;
                Element e = (Element)i;
                // Skip all sequents/predicate sets until the last poSequent for the current event.
                if (e.getName().equals("org.eventb.core.poSequent") &&
                    e.valueOf("@name").startsWith(event.name()))
                {
                    found_event = true;
                }
                else if (found_event)
                {
                    // We have passed the last sequent for this event.
                    // Is it no longer a predicate set? Then we are done!
                    if (!e.getName().equals("org.eventb.core.poPredicateSet")) break;
                    // Check all predicate sets following the last sequent. (Whew, weird format this is.)
                    // and look for identifiers....
                    List<Node> ids = e.selectNodes("//org.eventb.core.poIdentifier");
                    for (Node id : ids)
                    {
                        String name = id.valueOf("@name").trim();
                        String type = id.valueOf("@org.eventb.core.type").trim();
                        Variable p = event.getParameter(name);
                        if (p != null)
                        {
                            p.setCheckedTypeString(type); // sys_.typing().lookupCheckedType(type));
                        }
                        else
                        {
                            log.debug("no parameter %s in event %s in machine %s", name, event, this);
                        }
                    }
                }
            }
        }
    }

    void buildSymbolTable(SymbolTable parent)
    {
        if (symbol_table_ != null) return;

        log.debug("Building symbol table for machine %s", this);

        symbol_table_ = sys_.newSymbolTable(name_);
        symbol_table_.addParent(parent);

        if (refines_ != null)
        {
            refines_.buildSymbolTable(parent);
            symbol_table_.addParent(refines_.symbolTable());
        }

        for (Context c : contextOrdering())
        {
            symbol_table_.addParent(c.symbolTable());
        }

        for (Variable v : variableOrdering())
        {
            Variable vv  = symbol_table_.getVariable(v.name());
            if (vv != null)
            {
                SymbolTable st = symbol_table_.whichSymbolTableForVariable(v.name());
                log.debug("variable %s refines %s in %s", v, v, st.name());
                v.setRefines(vv);
            }
            symbol_table_.addVariable(v);
        }
    }

    public void parse(SymbolTable st)
    {
        buildSymbolTable(st);
        log.debug("parsing %s", name());

        for (Variable var : variableOrdering())
        {
            var.parseCheckedType(symbol_table_);
        }

        for (String name : invariantNames())
        {
            Invariant i = getInvariant(name);
            i.parse(symbol_table_);
            sys().typing().extractInfoFromInvariant(i.formula(), symbol_table_);
        }

        for (String name : variantNames())
        {
            Variant i = getVariant(name);
            i.parse(symbol_table_);
            sys().typing().extractInfoFromVariant(i.formula(), symbol_table_);
        }

        for (String name : eventNames())
        {
            Event e = getEvent(name);
            e.parse();
        }
    }

    public Event getConcreteEvent(String name)
    {
        return concrete_events_.get(name);
    }

    public List<Event> concreteEventOrdering()
    {
        return concrete_event_ordering_;
    }

    public List<String> concreteEventNames()
    {
        return concrete_event_names_;
    }

    public void buildImplementation() throws Exception
    {
        log_codegen.debug("Building implementation for %s", this);
        for (Event e : eventOrdering())
        {
            buildConcreteEvent(e);
        }
        log_codegen.debug("Done building implementation for %s", this);
        concrete_event_names_ = concrete_events_.keySet().stream().sorted().collect(Collectors.toList());
    }

    private void buildConcreteEvent(Event eventt) throws Exception
    {
        Event concrete_event = eventt.deepCopy();
        log_codegen.debug("Building concrete event %s:%s", eventt.machine(), concrete_event);
        concrete_events_.put(concrete_event.name(), concrete_event);
        concrete_event_ordering_.add(concrete_event);
        Event i = concrete_event;

        // Check if the event extends another event.
        while (i.extended())
        {
            Event extended = i.refinesEvents().get(0);
            String extended_event_name = extended.name();
            log_codegen.debug("Spec says event %s extends %s::%s", i.name(), i.machine().refines(), extended_event_name);

            Machine mch = i.machine().refines();
            Event extended_event = mch.getEvent(extended_event_name);
            if (extended_event == null)
            {
                log_codegen.error("Could not find extended event %s in machine %s!",
                                  extended_event_name, mch.name());
            }
            else
            {
                concrete_event.extendFrom(extended_event);
                log_codegen.debug("Extended from %s in %s", extended_event, mch);
                i = extended_event;
                // Loop around to check if this one also extends from another event.
            }
        }
        log_codegen.debug("Done building concrete event %s", concrete_event);
    }

}
