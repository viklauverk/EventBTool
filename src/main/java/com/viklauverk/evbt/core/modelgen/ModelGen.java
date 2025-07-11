/*
 Copyright (C) 2021-2023 Viklauverk AB (agpl-3.0-or-later)

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

package com.viklauverk.evbt.core.modelgen;

import com.viklauverk.evbt.common.log.Log;
import com.viklauverk.evbt.common.log.LogModule;
import com.viklauverk.evbt.core.CommonSettings;
import com.viklauverk.evbt.core.sys.Sys;

public class ModelGen
{
    static Log log = LogModule.lookup("evbt.modelgen", ModelGen.class);

    public static BaseModelGen lookup(CommonSettings cs, ModelGenSettings ms, Sys sys)
    {
        switch (ms.modelTarget())
        {
        case WHY3:
            return new ModelGenWhy3(cs, ms, sys);
        }
        assert (false) : "Missing case for model target: "+ms.modelTarget();
        return null;
    }
}
