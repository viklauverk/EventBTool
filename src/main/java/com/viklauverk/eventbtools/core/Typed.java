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

public class Typed
{
    private static Log log = LogModule.lookup("typing");

    protected Type type_; // null if not typed yet.

    public Type type()
    {
        return type_;
    }

    public Type updateType(Type t)
    {
        if (t == null) return type_;
        setType(t);
        return type_;
    }

    public void setType(Type t)
    {
        assert t != null : "Type must not be null!";

        if (type_ == null)
        {
            log.debug("setting type %s for %s", t, this);
            type_ = t;
        }
        else
        {
            if (type_.equals(t))
            {
                log.debug("not setting type %s for %s since it the type was already set", t, this);
            }
            else
            {
                Type mst = Typing.mostSpecificType(type_, t);
                log.debug("not setting type %s for %s since it the type was already set", t, this);
                log.debug("setting most specific type %s for %s (the choice was between %s and %s)", mst, this, type_, t);
                type_ = mst;
            }
        }
    }
}
