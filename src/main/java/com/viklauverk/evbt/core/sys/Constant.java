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

package com.viklauverk.evbt.core.sys;

public class Constant extends Typed
{
    private String name_;
    private String comment_;
    private Formula definition_;
    private Context context_;

    public Constant(String n, Context c)
    {
        name_ = n;
        context_ = c;
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

    public Context context()
    {
        return context_;
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
}
