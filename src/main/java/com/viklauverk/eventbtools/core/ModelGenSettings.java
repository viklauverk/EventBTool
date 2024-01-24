/*
 Copyright (C) 2021-2023 Viklauverk AB

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
//
package com.viklauverk.eventbtools.core;

import java.util.Set;
import java.util.HashSet;

public class ModelGenSettings
{
    private ModelTarget model_target_;

    public ModelGenSettings()
    {
        model_target_ = ModelTarget.WHY3;
    }

    public ModelGenSettings(ModelTarget mt)
    {
        model_target_ = mt;
    }

    public ModelTarget modelTarget()
    {
        return model_target_;
    }

    public void setModelTarget(ModelTarget rt)
    {
        model_target_ = rt;
    }

}
