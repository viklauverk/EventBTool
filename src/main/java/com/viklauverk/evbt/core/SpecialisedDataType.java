/*
 Copyright (C) 2024 Viklauverk AB

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

public class SpecialisedDataType
{
    private static Log log = LogModule.lookup("theory");

    private PolymorphicDataType template_;
    private Formula specialisation_;
    private String long_name_;

    protected Implementation implementation_;

    public SpecialisedDataType(PolymorphicDataType t, Formula s)
    {
        template_ = t;
        specialisation_ = s;
        long_name_ = s.toString();
    }

    public Formula specialisation()
    {
        return specialisation_;
    }

    public String longName()
    {
        return long_name_;
    }

    public static void findSpecialisations(Formula f, Sys sys)
    {
        if (f.isPolymorphicDataType())
        {
            String name = Symbols.name(f.intData());
            PolymorphicDataType pdt = sys.getPolymorphicDataType(name);
            SpecialisedDataType sdt = new SpecialisedDataType(pdt, f);
            log.debug("specialise %s into %s", pdt.longName(), sdt.longName());
            sys.addSpecialisedDataType(sdt);
        }
        else
        {
            for (Formula ff : f.children())
            {
                findSpecialisations(ff, sys);
            }
        }
    }
}
