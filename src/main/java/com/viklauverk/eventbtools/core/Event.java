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

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Event
{
    private static Log log = LogModule.lookup("event");

    private String name_;
    private boolean is_concrete_;
    private String comment_;
    private boolean extended_;
    private Convergence convergence_;
    private List<String> refines_event_names_ = new ArrayList<>();
    private List<Event> refines_events_ = new ArrayList<>();
    private boolean init_;
    private Sys sys_;
    private Machine machine_; // The machine in which this event is defined.

    private Map<String,Variable> parameters_;
    private List<Variable> parameter_ordering_;
    private List<String> parameter_names_;
    private Map<String,Guard> guards_;
    private List<Guard> guard_ordering_;
    private List<String> guard_names_;
    private Map<String,Action> actions_;
    private List<Action> action_ordering_;
    private List<String> action_names_;

    private SymbolTable symbol_table_;

    public Event(String n, boolean e, String comment, Machine m, Convergence c)
    {
        name_ = n;
        extended_ = e;
        convergence_ = c;
        comment_ = comment;
        if (comment_ == null) comment_ = "";
        machine_ = m;
        sys_ = machine_.sys();
        init_ = false;
        if (name_.equals("INITIALISATION")) init_ = true;

        parameters_ = new HashMap<>();
        parameter_ordering_ = new  ArrayList<>();
        parameter_names_ = new  ArrayList<>();

        guards_ = new HashMap<>();
        guard_ordering_ = new  ArrayList<>();
        guard_names_ = new  ArrayList<>();

        actions_ = new HashMap<>();
        action_ordering_ = new  ArrayList<>();
        action_names_ = new  ArrayList<>();
        action_names_ = new  ArrayList<>();
    }

    public SymbolTable symbolTable()
    {
        return symbol_table_;
    }

    @Override
    public String toString()
    {
        if (is_concrete_)
        {
            return name_+"_IMPL";
        }
        return name_;
    }

    public String name()
    {
        return name_;
    }

    public boolean extended()
    {
        return extended_;
    }

    public Convergence convergence()
    {
        return convergence_;
    }

    public boolean hasInformation()
    {
        return
            !extended_ ||
            parameters_.size() > 0 ||
            guards_.size() > 0 ||
            actions_.size() > 0;
    }

    public Sys sys()
    {
        return sys_;
    }

    public Machine machine()
    {
        return machine_;
    }

    public boolean hasRefines()
    {
        return refines_event_names_.size() > 0;
    }

    public void addRefinesEventName(String e)
    {
        refines_event_names_.add(e);
    }

    public List<String> refinesEventNames()
    {
        return refines_event_names_;
    }

    public List<Event> refinesEvents()
    {
        return refines_events_;
    }

    public boolean isInit()
    {
        return init_;
    }

    public boolean hasParameters()
    {
        return parameters_.size() > 0;
    }

    public boolean hasGuards()
    {
        return guards_.size() > 0;
    }

    public boolean hasActions()
    {
        return actions_.size() > 0;
    }

    public String comment()
    {
        return comment_;
    }

    public boolean hasComment()
    {
        return comment_.length() > 0;
    }

    public void addParameter(Variable p)
    {
        p.markAsParameter();
        parameters_.put(p.name(), p);
        parameter_ordering_.add(p);
        parameter_names_ = parameters_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public List<Variable> parameterOrdering()
    {
        return parameter_ordering_;
    }

    public List<String> parameterNames()
    {
        return parameter_names_;
    }

    public void addGuard(Guard guard)
    {
        guards_.put(guard.name(), guard);
        guard_ordering_.add(guard);
        guard_names_ = guards_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public List<Guard> guardOrdering()
    {
        return guard_ordering_;
    }

    public List<String> guardNames()
    {
        return guard_names_;
    }

    public Guard getGuard(String name)
    {
        return guards_.get(name);
    }

    public void addAction(Action action)
    {
        actions_.put(action.name(), action);
        action_ordering_.add(action);
        action_names_ = actions_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public List<Action> actionOrdering()
    {
        return action_ordering_;
    }

    public List<String> actionNames()
    {
        return action_names_;
    }

    public Action getAction(String name)
    {
        return actions_.get(name);
    }

    public void extendFrom(Event parent)
    {
        parameters_.putAll(parent.parameters_);
        parameter_ordering_.addAll(0, parent.parameter_ordering_);
        parameter_names_ = parameters_.keySet().stream().sorted().collect(Collectors.toList());
        guards_.putAll(parent.guards_);
        guard_ordering_.addAll(0, parent.guard_ordering_);
        guard_names_ = guards_.keySet().stream().sorted().collect(Collectors.toList());
        actions_.putAll(parent.actions_);
        action_ordering_.addAll(0, parent.action_ordering_);
        action_names_ = actions_.keySet().stream().sorted().collect(Collectors.toList());

        symbol_table_.addParent(parent.symbolTable());

        log.debug("add parent event %s to %s (%d pars %d guards %d actions)",
                  parent.name_, name_, parent.parameters_.size(),
                  parent.guards_.size(), parent.actions_.size());
    }

    public Event deepCopy()
    {
        Event e = new Event(name_, extended_, comment_, machine_, convergence_);
        e.is_concrete_ = true;
        e.comment_ = comment_;
        e.refines_event_names_ = new ArrayList<>(refines_event_names_);
        e.refines_events_ = new ArrayList<>(refines_events_);
        e.init_ = init_;

        e.parameters_ = new HashMap<>(parameters_);
        e.parameter_names_ = new ArrayList<>(parameter_names_);
        e.parameter_ordering_ = new ArrayList<>(parameter_ordering_);
        e.guards_ = new HashMap<>(guards_);
        e.guard_ordering_ = new ArrayList<>(guard_ordering_);
        e.guard_names_ = new ArrayList<>(guard_names_);
        e.actions_ = new HashMap<>(actions_);
        e.action_ordering_ = new ArrayList<>(action_ordering_);
        e.action_names_ = new ArrayList<>(action_names_);
        e.symbol_table_ = symbol_table_;

        return e;
    }

    private void addParentParameters(Event e, SymbolTable st)
    {
        if (e == null) return;
        if (e.extended_)
        {
            for (Event r : e.refines_events_)
            {
                addParentParameters(r, st);
            }
        }
        for (Variable p : e.parameterOrdering())
        {
            symbol_table_.addVariable(p);
        }
    }

    private void buildSymbolTable()
    {
        if (symbol_table_ != null) return;

        symbol_table_ = machine_.sys().newSymbolTable(name_, machine_.symbolTable());

        addParentParameters(this, symbol_table_);
    }

    public void parse()
    {
        for (String refines : refinesEventNames())
        {
            log.debug("lookup refines %s", refines);
            if (!machine().hasRefines())
            {
                // Event extends something but machines has not refines spec!
                // Rodin should not allow such events, but until Rodin is fixed.
                log.warn("Event %s/%s has extend/refines but machine refines nothing!", machine_.name(), name());
                refines_event_names_ = new ArrayList<>();
                refines_events_ = new ArrayList<>();
                extended_ = false;
            }
            else
            {
                refines_events_.add(machine().refines().getEvent(refines));
            }
        }

        log.debug("building symbol table");

        buildSymbolTable();

        log.debug("parsing %s", name());

        for (String name : guardNames())
        {
            Guard g = getGuard(name);
            g.parse(symbol_table_);
            sys().typing().extractInfoFromGuard(g, symbol_table_);
        }

        for (String name : actionNames())
        {
            Action a = getAction(name);
            a.parse(symbol_table_);
            sys().typing().extractInfoFromAction(a, symbol_table_);
        }
    }

    public boolean isEmpty()
    {
        return (parameterNames().size()==0 &&
                guardNames().size()==0 &&
                actionNames().size()==0);
    }

    public void show(ShowSettings ss, Canvas canvas)
    {
        StringBuilder o = new StringBuilder();
        String title = name_;
        boolean add_refine_at_end = false;
        /*
        if (refines_event_names_.size() > 0)
        {
            if (refined_event_.equals(name_))
            {
                title += " ⊏ ...";
            }
            else
            {
                add_refine_at_end = true;
            }
            }*/
        if (parameterOrdering().size()>0)
        {
            o.append("any\n  ");
            int c = 0;
            for (Variable v : parameterOrdering())
            {
                if (c > 0) o.append(" ");
                o.append(v.name());
                c += v.name().length();
                if (c > 40) o.append("\n");
            }
            if (o.charAt(o.length()-1) != '\n')
            {
                o.append("\n");
            }
        }
        if (guardOrdering().size()>0)
        {
            o.append("where\n");
            for (Guard g : guardOrdering())
            {
                o.append("  "+g.writeFormulaStringToCanvas(canvas));
                o.append("\n");
            }
        }
        if (actionOrdering().size()>0)
        {
            o.append("then\n");
            for (Action a : actionOrdering())
            {
                o.append("  "+a.writeFormulaStringToCanvas(canvas));
                o.append("\n");
            }
        }
        o.append("end\n");
        if (add_refine_at_end)
        {
            //o.append("⊏ "+refined_event_+"\n"); // ⊑
        }
        String f = canvas.frame(title, o.toString(), Canvas.sline);
        String comment = Canvas.flow(Canvas.width(f), comment_);
        canvas.appendBox(comment+f);
    }
}
