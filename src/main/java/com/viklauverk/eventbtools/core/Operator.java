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

import com.viklauverk.eventbtools.core.OperatorNotationType;
import com.viklauverk.eventbtools.core.OperatorType;

public class Operator extends Typed
{
    private String name_;
    private String comment_;
    private Formula definition_;
    private OperatorNotationType notation_type_;
    private OperatorType operator_type_;
    private Theory theory_;

    public Operator(String n, Theory t, OperatorNotationType nt, OperatorType ot)
    {
        name_ = n;
        notation_type_ = nt;
        operator_type_ = ot;
        theory_ = t;
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
}
