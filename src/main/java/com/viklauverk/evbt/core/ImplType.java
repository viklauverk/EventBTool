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

public class ImplType extends IsAFormula
{
    public ImplType(Formula f)
    {
        super(f);
    }

    @Override
    public String toString()
    {
        if (formula() != null) return formula().toString();
        return "?";
    }

    public boolean isSubsetOf(ImplType type)
    {
        // For example NAT1 is a subset of NAT which is a subset of INT
        // TODO
        return false;
    }

    public boolean isCarrierSet()
    {
        return formula().node() == Node.SET_SYMBOL;
    }

    public boolean isPolymorphicDataType()
    {
        return formula().node() == Node.POLYMORPHIC_DATA_TYPE_SYMBOL;
    }

    public boolean isVector()
    {
        if (formula().node() == Node.TOTAL_FUNCTION &&
            formula().left().node() == Node.UP_TO &&
            formula().left().left().isNumber() &&
            formula().left().left().intData() == 1)
        {
            // Matches 1..7 --> S
            if (formula().left().right().isNumber()) return true;
            // Matches 1..FIX --> S
            if (formula().left().right().isConstant()) return true;
            // Matches 1..size --> S
            if (formula().left().right().isVariable()) return true;
        }
        return false;
    }



}
