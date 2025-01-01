/*
 Copyright (C) 2024 Viklauverk AB (agpl-3.0-or-later)

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

package com.viklauverk.evbt.core.sys;

public enum NodeType
{
    PRE(1),
    EXP(2),
    SET(4),
    VAR(8),
    CON(16),
    SYM(32),
    REL(64),
    FUN(128),
    PDT(256),
    CNS(512),
    DES(1024),
    OPE(2048);

    public int bit;
    NodeType(int b)
    {
        bit = b;
    }
}
