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

public class ProofObligation
{
    String name_; // Name of the proof obligation.
    int confidence_; // 1000 means proven. Less means not proven.
    boolean manual_; // True if manual intervention was needed to perform the proof.
    boolean reviewed_; // True if some part of the proof tree has been reviewed manually and declared true.

    public ProofObligation(String n, int c, boolean m, boolean r)
    {
        name_ = n;
        confidence_ = c;
        manual_ = m;
        reviewed_ = r;
    }

    public String name()
    {
        return name_;
    }

    public boolean isProved()
    {
        return confidence_ == 1000;
    }

    public boolean manual()
    {
        return manual_;
    }

    public boolean reviewed()
    {
        return reviewed_;
    }

}
