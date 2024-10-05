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

package com.viklauverk.evbt.core;

public class Variable extends Typed
{
    private String name_;
    private String comment_;
    private boolean is_parameter_;
    private boolean is_out_;
    private Formula definition_;
    private Variable refines_; // Points to the same variable in the refined machine.
    private Machine machine_; // The machine in which this variable is defined.
    private Event   event_; // The event in which this variable is potentially defined.

    public Variable(String n, String c, Machine m, Event e)
    {
        name_ = n;
        comment_ = c;
        machine_ = m;
        event_ = e;
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

    public boolean hasComment()
    {
        return comment_ != null && comment_.length() > 0;
    }

    public String comment()
    {
        return comment_;
    }

    public Machine machine()
    {
        return machine_;
    }

    public Event event()
    {
        return event_;
    }

    public boolean isParameter()
    {
        return is_parameter_;
    }

    public boolean isOutParameter()
    {
        return is_out_;
    }

    public void markAsParameter()
    {
        is_parameter_ = true;
        if (name().startsWith("out_"))
        {
            is_out_ = true;
        }
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

    public void setRefines(Variable r)
    {
        refines_ = r;
    }

    @Override
    public ImplType implType()
    {
        if (impl_type_ != null) return impl_type_;
        if (refines_ != null) return refines_.implType();
        return null;
    }
}
