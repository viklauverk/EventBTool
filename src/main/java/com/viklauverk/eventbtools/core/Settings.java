/*
 Copyright (C) 2021-2024 Viklauverk AB

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

import java.util.List;
import java.util.ArrayList;

import com.viklauverk.eventbtools.core.CommonSettings;
import com.viklauverk.eventbtools.core.ConsoleSettings;
import com.viklauverk.eventbtools.core.ShowSettings;
import com.viklauverk.eventbtools.core.CodeGenSettings;
import com.viklauverk.eventbtools.core.DocGenSettings;
import com.viklauverk.eventbtools.core.ModelGenSettings;

public class Settings
{
    private CommonSettings common_settings_ = new CommonSettings();
    private ConsoleSettings console_settings_ = new ConsoleSettings();
    private ShowSettings show_settings_ = new ShowSettings();
    private CodeGenSettings codegen_settings_ = new CodeGenSettings();
    private DocGenSettings docgen_settings_ = new DocGenSettings();
    private ModelGenSettings modelgen_settings_ = new ModelGenSettings();

    public CommonSettings commonSettings() { return common_settings_; }
    public ShowSettings showSettings() { return show_settings_; }
    public CodeGenSettings codeGenSettings() { return codegen_settings_; }
    public DocGenSettings docGenSettings() { return docgen_settings_; }
    public ModelGenSettings modelGenSettings() { return modelgen_settings_; }
}
