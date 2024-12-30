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

import com.viklauverk.common.console.Canvas;

public interface CommonCodeGenFunctions
{
    boolean handleSetClear(Formula setvar, SymbolTable symbols, Canvas cnvs, Formula origin);
    boolean handleAddToSet(Formula setvar, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin);
    boolean handleMapOverride(Formula setvar, Formula index, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin);
    boolean handleMapDomainSubtraction(Formula setvar, Formula key, SymbolTable symbols, Canvas cnvs, Formula origin);
    boolean handleMembershipInVariable(Formula setvar, Formula member, SymbolTable symbols, Canvas cnvs, Formula origin);
    boolean handleMembershipNotInVariable(Formula setvar, Formula member, SymbolTable symbols, Canvas cnvs, Formula origin);
    boolean handleAlwaysTrue(SymbolTable symbols, Canvas cnvs, Formula origin);
    boolean handleAlwaysFalse(SymbolTable symbols, Canvas cnvs, Formula origin);
    boolean handlePairMembershipInFunctionSetvar(Formula setvar, Formula key, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin);
    boolean handlePairMembershipNotInFunctionSetvar(Formula setvar, Formula key, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin);
    boolean handleMembershipInFixedVector(Formula set, Formula key, Formula size, SymbolTable symbols, Canvas cnvs,
                                          Formula origin, boolean yesno);
    boolean handleFunctionalApplication(Formula setvar, Formula index, SymbolTable symbols, Canvas cnvs, Formula origin);
    boolean handleFunctionalInvApplication(Formula setvar, Formula index, SymbolTable symbols, Canvas cnvs, Formula origin);

    boolean handleFindExistentialQOverSet(Formula setvar, Formula iterator, Formula out, SymbolTable symbols, Canvas cnvs, Formula origin);
    boolean handleFindUniversalQOverSet(Formula setvar, Formula iterator, Formula out, SymbolTable symbols, Canvas cnvs, Formula origin);
}
