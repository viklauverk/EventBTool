/* evbt - Copyright (C) 2025 Viklauverk AB (spdx: agpl-3.0-or-later)

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

package com.viklauverk.evbt.common.cli;

import java.util.List;
import java.util.LinkedList;

public class Command
{
    private AvailableCommands available_commands_;
    private String name_;
    private String help_;
    private CommandConstructor constructor_;
    private Environment environment_;
    private CommandSequence sequence_;

    public Command(String n, String h, CommandConstructor co)
    {
        name_ = n;
        help_ = h;
        constructor_ = co;
    }

    public String name()
    {
        return name_;
    }

    public String help()
    {
        return help_;
    }

    public AvailableCommands availableCommands()
    {
        return available_commands_;
    }

    public CommandConstructor constructor()
    {
        return constructor_;
    }

    public void setCommands(AvailableCommands ac)
    {
        available_commands_ = ac;
    }
}
