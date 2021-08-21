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

public abstract class EDKContext implements CommonCodeGenFunctions
{
    private static Log log = LogModule.lookup("edk");

    private EDK edk_;
    private Pattern pattern_;

    public EDKContext(EDK e)
    {
        edk_ = e;
        pattern_ = new Pattern();
    }

    public abstract void generateDeclarations(Canvas cnvs);
    public abstract void generateImplementationDeclarations(Canvas cnvs);
    public abstract void generateImplementationDefinitions(Canvas cnvs);

    protected Pattern pattern()
    {
        return pattern_;
    }

    public String translateImplType(ImplType type, SymbolTable symbols, BaseCodeGen bcg)
    {
        return null;
    }

    public boolean handleSetClear(Formula setvar, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handleAddToSet(Formula setvar, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handleMapOverride(Formula setvar, Formula index, Formula value, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handleMapDomainSubtraction(Formula setvar, Formula key, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handleMembershipInVariable(Formula setvar, Formula member, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handleMembershipNotInVariable(Formula setvar, Formula member, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handleAlwaysTrue(SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handleAlwaysFalse(SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handlePairMembershipInFunctionSetvar(Formula setvar, Formula key, Formula value,
                                                 SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handlePairMembershipNotInFunctionSetvar(Formula setvar, Formula key, Formula value,
                                                    SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handleFunctionalApplication(Formula setvar, Formula index, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handleFunctionalInvApplication(Formula setvar, Formula index, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handleFindExistentialQOverSet(Formula setvar, Formula iterator, Formula out, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

    public boolean handleFindUniversalQOverSet(Formula setvar, Formula iterator, Formula out, SymbolTable symbols, Canvas cnvs, Formula origin)
    {
        return false;
    }

}
