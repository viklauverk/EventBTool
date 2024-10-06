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

public interface Implementation
{
    // The current implementation is potentially increased to cover the necessary type for i.
    // (uint32).merge(uint32) nothing happens
    // (uint32).merge(uint8) nothing happens
    // (uint8).merge(uint32) becomes uint32
    // (uint32).merge(uint64) becomes uint64
    // (0..7).merge(5..12) becomes (0..12)
    //void merge(Implementation i);

    void generateAddition(RenderFormula render, PlanImplementation plan, Formula i, Formula left, Formula right);
}
