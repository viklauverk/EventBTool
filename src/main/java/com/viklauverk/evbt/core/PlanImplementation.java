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

import com.viklauverk.common.log.Log;
import com.viklauverk.common.log.LogModule;
import java.util.HashMap;

public class PlanImplementation extends WalkFormula
{
    private static Log log = LogModule.lookup("planimplementation", PlanImplementation.class);

    private BaseCodeGen codegen_;
    private HashMap<Formula,Implementation> imps_;

    PlanImplementation(BaseCodeGen bcg)
    {
        codegen_ = bcg;
        imps_ = new HashMap<>();
    }

    Implementation getImplementation(Formula i)
    {
        Implementation imp = imps_.get(i);
        if (imp == null)
        {
            log.internalError("No such implementation planned for formula: %s", i);
        }
        return imp;
    }

    @Override
    public Formula visit_ADDITION(Formula i)
    {
        /*
        Formula left = visitLeft(i);
        Formula right = visitRight(i);
        Implementation v = codegen_.newInteger();
        //v.merge(left);
        //v.merge(right);
        imps_.put(i, v);*/
        return i;
    }
}
