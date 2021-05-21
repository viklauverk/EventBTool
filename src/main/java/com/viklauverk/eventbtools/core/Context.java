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
    private Context extends_context_;

    private Map<String,CarrierSet> sets_;
    private List<CarrierSet> set_ordering_;
    private List<String> set_names_;

    private Map<String,Constant> constants_;
    private List<Constant> constant_ordering_;
    private List<String> constant_names_;

    private Map<String,Axiom> axioms_;
    private List<Axiom> axiom_ordering_;
    private List<String> axiom_names_;

    private Map<String,Theorem> theorems_;
    private List<Theorem> theorem_ordering_;
    private List<String> theorem_names_;

    private boolean loaded_;
    private File source_;
    private Sys sys_;

    private SymbolTable symbol_table_;

    private EDKContext edk_context_; // Points to the proper EVBT supported EDK information.

    public Context(String n, Sys s, File f)
    {
        name_ = n;
        comment_ = "";
        extends_context_ = null;
        sets_ = new HashMap<>();
        set_ordering_ = new ArrayList<>();
        set_names_ = new ArrayList<>();

        constants_ = new HashMap<>();
        constant_ordering_ = new ArrayList<>();
        constant_names_ = new ArrayList<>();

        axioms_ = new HashMap<>();
        axiom_ordering_ = new ArrayList<>();
        axiom_names_ = new ArrayList<>();

        theorems_ = new HashMap<>();
        theorem_ordering_ = new ArrayList<>();
        theorem_names_ = new ArrayList<>();

        loaded_ = false;
        source_ = f;

        assert (source_ != null) : "Source file must not be null!";

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
        return extends_context_ != null;
    }

    Context extendsContext()
    {
        return extends_context_;
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

    public boolean hasTheorems()
    {
        return theorem_ordering_.size() > 0;
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

    public void addTheorem(Theorem t)
    {
        theorems_.put(t.name(), t);
        theorem_ordering_.add(t);
        theorem_names_ = theorems_.keySet().stream().sorted().collect(Collectors.toList());
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

    public boolean isEDKContext()
    {
        return edk_context_ != null;
    }

    public EDKContext edkContext()
    {
        return edk_context_;
    }

    public synchronized void load() throws Exception
    {
        if (loaded_) return;
        loaded_ = true;
        SAXReader reader = new SAXReader();

        log.debug("loading context "+source_);

        Document document = reader.read(source_);

        edk_context_ = sys().edk().lookup(name(), source_);

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
            extends_context_ = sys_.getContext(name);
            assert (extends_context_ != null) : "Error in loaded context xml file. Cannot find context "+name;
            assert (list.size() == 1) : "Error in loaded context xml file, only one extends expected.";
        }

        // Load the carrier sets.
        list = document.selectNodes("//org.eventb.core.carrierSet");
        for (Node n : list)
        {
            String name = n.valueOf("@org.eventb.core.identifier");
            String comment = n.valueOf("@org.eventb.core.comment");
            CarrierSet cs = new CarrierSet(name);
            cs.addComment(comment);
            addSet(cs);
        }

        // Load the constants.
        list = document.selectNodes("//org.eventb.core.constant");
        for (Node n : list)
        {
            String name = n.valueOf("@org.eventb.core.identifier");
            String comment = n.valueOf("@org.eventb.core.comment");
            Constant c = new Constant(name);
            c.addComment(comment);
            addConstant(c);
        }

        // Load the axioms and theorems.
        boolean adding_axioms = true;
        list = document.selectNodes("//org.eventb.core.axiom");
        for (Node n : list)
        {
            String name = n.valueOf("@org.eventb.core.label");
            String pred = n.valueOf("@org.eventb.core.predicate");
            String comment = n.valueOf("@org.eventb.core.comment");
            String is_theorem = n.valueOf("@org.eventb.core.theorem");

            if (is_theorem.equals("true"))
            {
                // It seems like the start of the theorems is marked
                // with theorem=true, the the following theorems
                // are not marked....
                adding_axioms = false;
            }

            if (adding_axioms)
            {
                Axiom a = new Axiom(name, pred, comment);
                addAxiom(a);
            }
            else
            {
                Theorem t = new Theorem(name, pred, comment);
                addTheorem(t);
            }
        }
    }

    private void buildSymbolTable()
    {
        if (symbol_table_ != null) return;

        SymbolTable parent = null;
        if (extends_context_ != null)
        {
            extends_context_.buildSymbolTable();
            parent = extends_context_.symbolTable();
        }

        symbol_table_ = sys_.newSymbolTable(name_, parent);

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
            Type type = sys().typing().lookupType(f);
            log.debug("adding carrier set type: "+type.name());
        }
        for (Axiom a : axiomOrdering())
        {
            a.parse(symbol_table_);
            sys().typing().extractInfoFromAxiom(a.formula(), symbol_table_);
        }
        for (Theorem t : theoremOrdering())
        {
            t.parse(symbol_table_);
            sys().typing().extractInfoFromAxiom(t.formula(), symbol_table_);
        }
    }

    public void showwww(ShowSettings ss, Canvas canvas)
    {
        StringBuilder o = new StringBuilder();
        o.append(name_);
        if (extends_context_ != null)
        {
            o.append(" ⊏ "+extends_context_.name());
        }
        o.append("\n");
        o.append("-\n");
        for (String s : setNames())
        {
            o.append(s);
            o.append("\n");
        }
        o.append("-\n");
        for (String c : constantNames())
        {
            o.append(c);
            o.append("\n");
        }
        if (ss.showingAxioms())
        {
            o.append("-\n");
            for (Axiom a : axiomOrdering())
            {
                o.append(a.writeFormulaStringToCanvas(canvas));
                o.append("\n");
            }
        }
        String f = canvas.frame("", o.toString(), Canvas.sline);
        int w = Canvas.width(f);
        if (w < 40) w = 40;
        if (ss.showingComments())
        {
            String comment = Canvas.flow(w, comment_);
            canvas.appendBox(comment+f);
        }
        else
        {
            canvas.appendBox(f);
        }
    }
}
