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
// 
package com.viklauverk.evbt.core;

// Default is info, ie show errors, warning and info messages.
// Errors cannot be disabled, but warnings can.
public enum LogLevel
{
    ERROR(0), WARN(1), INFO(2), VERBOSE(3), DEBUG(4), TRACE(5);

    private int value_;

    LogLevel(int v)
    {
        value_ = v;
    }

    int value() { return value_; }
}
