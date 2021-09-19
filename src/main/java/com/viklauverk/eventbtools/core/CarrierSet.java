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
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

public class CarrierSet
{
    private String name_;
    private String comment_;
    private Map<String,Constant> members_;
    private List<Constant> member_ordering_;
    private List<String> member_names_;
    private Context context_;
    // The
    protected Implementation implementation_;

    public CarrierSet(String n, Context c)
    {
        name_ = n;
        members_ = new HashMap<>();
        member_names_ = new ArrayList<>();
        context_ = c;
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

    public void addMember(Constant c)
    {
        members_.put(c.name(), c);
        member_names_ = members_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public Constant getMember(String name)
    {
        return members_.get(name);
    }

    public List<Constant> memberOrdering()
    {
        return member_ordering_;
    }

    public List<String> memberNames()
    {
        return member_names_;
    }

}
