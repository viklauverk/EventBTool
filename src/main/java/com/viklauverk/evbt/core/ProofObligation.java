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

package com.viklauverk.evbt.core;

import org.dom4j.Node;

public class ProofObligation
{
    String name_; // Name of the proof obligation.
    int confidence_; // 1000 means proven. 500 is used for reviewed. 0 means not proven.
    boolean manual_; // True if manual intervention was needed to perform the proof.

    public ProofObligation(String n, int c, boolean m)
    {
        name_ = n;
        confidence_ = c;
        manual_ = m;
    }

    public String name()
    {
        return name_;
    }

    public boolean hasProof()
    {
        // This includes proofs that have steps that have been manually reviewed.
        return confidence_ > 100;
    }

    public boolean isReviewed()
    {
        return confidence_ > 100 && confidence_ <= 500;
    }

    public boolean manual()
    {
        return manual_;
    }

    public boolean isProvedAuto()
    {
        return hasProof() && !manual() && !isReviewed();
    }

    public boolean isProvedManualNotReviewed()
    {
        return hasProof() && manual() && !isReviewed();
    }

    public boolean isProvedManualReviewed()
    {
        return hasProof() && manual() && isReviewed();
    }

}
