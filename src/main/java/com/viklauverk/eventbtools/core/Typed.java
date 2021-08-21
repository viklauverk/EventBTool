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

    // Core type calculated by Rodin. E.g. ℤ,ℙ(ℤ×ℤ),S,ℙ(S),ℙ(ℤ×S) etc
    protected CoreType core_type_;
    // Type that hints on intended usage of variable such as: vector,map,relation,set.
    // This information is used to pick an efficient implementation for storage.
    // Set to null if no suitable implementation type found yet.
    protected ImplType impl_type_;

    public CoreType coreType()
    {
        return core_type_;
    }

    public ImplType implType()
    {
        return impl_type_;
    }

    public ImplType updateImplType(ImplType t)
    {
        if (t == null) return impl_type_;
        setImplType(t);
        return impl_type_;
    }

    public void setImplType(ImplType t)
    {
        assert t != null : "Type must not be null!";

        if (impl_type_ == null)
        {
            log.debug("setting type %s for %s", t, this);
            impl_type_ = t;
        }
        else
        {
            if (impl_type_.equals(t))
            {
                log.debug("not setting type %s for %s since it the type was already set", t, this);
            }
            else
            {
                ImplType mst = Typing.mostSpecificImplType(impl_type_, t);
                log.debug("not setting type %s for %s since it the type was already set", t, this);
                log.debug("setting most specific type %s for %s (the choice was between %s and %s)", mst, this, impl_type_, t);
                impl_type_ = mst;
            }
        }
    }
}
