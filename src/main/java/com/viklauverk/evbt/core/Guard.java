/*
 Copyright (C) 2021 Viklauverk AB
 
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

public class Guard extends IsAFormula
{
    private boolean defines_value_ = false;
    private boolean out_ = false;

    public Guard(String n, String fs, String c)
    {
        super(n, fs, c);
    }

    public void setAsDefiningValue()
    {
        defines_value_ = true;
    }

    public boolean definesValue()
    {
        return defines_value_;
    }

    public void setAsOut()
    {
        out_ = true;
    }

    public boolean isOut()
    {
        return out_;
    }

}
