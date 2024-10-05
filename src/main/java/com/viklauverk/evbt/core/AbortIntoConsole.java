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

import org.antlr.v4.runtime.misc.ParseCancellationException;

public class AbortIntoConsole extends Error
{
    private static final long serialVersionUID = 1L;

    private String formula_;
    private transient SymbolTable symbol_table_;

    public AbortIntoConsole(String msg, String formula, SymbolTable st)
    {
        super(msg);
        formula_ = formula;
        symbol_table_ = st;
    }

    public String formula()
    {
        return formula_;
    }

    public SymbolTable symbolTable()
    {
        return symbol_table_;
    }
}
