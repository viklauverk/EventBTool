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

public class ImplementationCpp implements Implementation
{
    private static Log log = LogModule.lookup("implementationcpp");

    public void generateAddition(RenderFormula render, PlanImplementation plan, Formula i, Formula left, Formula right)
    {
        render.visitLeft(i);
        render.cnvs().append(" + /* PLUSS */ ");
        render.visitRight(i);
    }
}
