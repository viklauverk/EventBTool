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

package com.viklauverk.evbt.core;

import java.util.List;
import java.util.LinkedList;

import com.viklauverk.evbt.core.OperatorNotationType;
import com.viklauverk.evbt.core.OperatorType;

public class Operator extends Typed
{
    private static Log log = LogModule.lookup("theory");

    private String name_; // add
    private String long_name_; // "x add y"
    private Formula formula_; // The long name parsed as a formula.
    private String comment_;

    private List<OperatorArgument> arguments_ = new LinkedList<>();
    private String definition_s_;
    private Formula definition_;
    private String predicate_; // Used by axiomatic operator definitions.

    private OperatorNotationType notation_type_;
    private OperatorType operator_type_;
    private SymbolTable operator_symbol_table_;

    private Theory theory_;

    public Operator(String n, Theory t, OperatorNotationType nt, OperatorType ot)
    {
        name_ = n;
        notation_type_ = nt;
        operator_type_ = ot;
        theory_ = t;
        operator_symbol_table_ = new SymbolTable("OP_"+name_);
    }

    @Override
    public String toString()
    {
        return name_;
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
        return comment_ != null && comment_.length() > 0;
    }

    public OperatorNotationType notation()
    {
        return notation_type_;
    }

    public Theory theory()
    {
        return theory_;
    }

    public void addComment(String c)
    {
        comment_ = c;
    }

    public void setPredicate(String p)
    {
        predicate_ = p;
    }

    public void setDefinitionString(String d)
    {
        definition_s_ = d;
    }

    public String definitionString()
    {
        return definition_s_;
    }

    public void setDefinition(Formula f)
    {
        definition_ = f;
    }

    public Formula definition()
    {
        return definition_;
    }

    public boolean hasDefinition()
    {
        return definition_ != null;
    }

    public OperatorNotationType notationType()
    {
        return notation_type_;
    }

    public OperatorType operatorType()
    {
        return operator_type_;
    }

    public boolean isOp(OperatorNotationType nt, OperatorType ot)
    {
        if (notation_type_ != nt) return false;
        if (operator_type_ != ot) return false;
        return true;
    }

    public void reparse()
    {
        try
        {
            if (!operator_symbol_table_.hasParents())
            {
                operator_symbol_table_.addParent(theory_.localSymbolTable());
            }
            if (definition_s_ != null)
            {
                definition_ = Formula.fromString(definition_s_, operator_symbol_table_);
            }
            if (notation_type_ == OperatorNotationType.PREFIX)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(name_);
                sb.append("(");
                boolean first = true;
                for (OperatorArgument oa : arguments_)
                {
                    if (!first) sb.append(",");
                    sb.append(oa.name());
                    first = false;
                }
                sb.append(")");
                long_name_ = sb.toString();
            }
            else
            {
                StringBuilder sb = new StringBuilder();
                if (arguments_.size() >= 2)
                {
                    OperatorArgument left = arguments_.get(0);
                    OperatorArgument right = arguments_.get(1);

                    sb.append(left.name());
                    sb.append(" ");
                    sb.append(name_);
                    sb.append(" ");
                    sb.append(right.name());
                }
                else
                {
                    sb.append("ERROR");
                }
                long_name_ = sb.toString();
            }
            formula_ = Formula.fromString(long_name_, operator_symbol_table_);
        }
        catch (Error e)
        {
            log.warn("reparse failed in operator: %s in theory: %s",
                     name_, theory_.name());
            throw e;
        }
    }

    public void addArgument(String name, String type)
    {
        operator_symbol_table_.addVariableSymbol(name);
        arguments_.add(new OperatorArgument(name, type, this));
    }

    public List<OperatorArgument> arguments()
    {
        return arguments_;
    }

    public Formula formula()
    {
        return formula_;
    }
}
