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

import com.viklauverk.evbt.core.implementation.ImplType;
import com.viklauverk.evbt.core.implementation.Implementation;
import com.viklauverk.evbt.core.log.Log;
import com.viklauverk.evbt.core.log.LogModule;

public class Typed
{
    private static Log log = LogModule.lookup("typing", Typed.class);

    // Checked type calculated by Rodin for type checking. E.g. ℤ,ℙ(ℤ×ℤ),S,ℙ(S),ℙ(ℤ×S)
    protected String checked_type_string_;
    // The checked type when it is finally parsed, which can only be done
    // using the same symbol tables as when we are parsing the vars/consts.
    protected CheckedType checked_type_;
    // The implementation type hints at the intended usage of variable such as: vector,map,relation,set.
    // This information is used to pick an efficient implementation for storage.
    // Set to null if no suitable implementation type has been found yet.
    protected ImplType impl_type_;
    // This is the defacto chosen implementation for this variable/constant.
    // A default implementation for unbounded INTs can be chosen at code generation,
    // for example int32_t(C++) or int(Java), or unbounded Z class storing any size ints.
    // Calculations on such unbounded ints will be checked by overflow checks.
    // If a cardinality is known for the int, the the codegen can pick the smallest
    // storage container needed to store the int. E.g. x:1..100 means "int8_t x;" can be used.
    protected Implementation implementation_;

    public CheckedType checkedType()
    {
        return checked_type_;
    }

    public boolean hasCheckedTypeString()
    {
        return checked_type_string_ != null;
    }

    public boolean hasCheckedType()
    {
        return checked_type_ != null;
    }

    public ImplType implType()
    {
        return impl_type_;
    }

    public boolean hasImplType()
    {
        return impl_type_ != null;
    }

    public ImplType updateImplType(ImplType t)
    {
        if (t == null) return impl_type_;
        setImplType(t);
        return impl_type_;
    }

    public void setCheckedTypeString(String ct)
    {
        assert ct != null : "Checked type must not be null!";

        if (checked_type_string_ == null)
        {
            log.debug("setting checked type string %s for %s", ct, this);
            checked_type_string_ = ct;
        }
        else
        {
            if (checked_type_string_.equals(ct))
            {
                log.debug("ignoring second set checked type string %s for %s since it the checked type string was already set", ct, this);
            }
            else
            {
                log.info("cannot override checked type string %s with a different checked type %s for %s", checked_type_string_, ct, this);
            }
        }
    }

    public void parseCheckedType(SymbolTable st)
    {
        assert checked_type_ == null : "Already parsed checked type!";
        if (checked_type_string_ == null)
        {
            log.info("Could not find checked type for %s", this.toString());
        }
        else
        {
            checked_type_ = new CheckedType(Formula.fromString(checked_type_string_, st));
        }
    }

    public void setImplType(ImplType t)
    {
        assert t != null : "Impl type must not be null!";

        if (impl_type_ == null)
        {
            log.debug("setting impl type %s for %s", t, this);
            impl_type_ = t;
        }
        else
        {
            if (impl_type_.equals(t))
            {
                log.debug("not setting impl type %s for %s since it the type was already set", t, this);
            }
            else
            {
                ImplType mst = Typing.mostSpecificImplType(impl_type_, t);
                log.debug("setting most specific impl type %s for %s (the choice was between %s and %s)", mst, this, impl_type_, t);
                impl_type_ = mst;
            }
        }
    }
}
