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
import java.util.Set;
import java.util.HashSet;

public class AvailableCommands
{
    private Set<String> command_names_ = new HashSet<>();
    private List<Command> commands_ = new LinkedList<>();

    public void add(Command c)
    {
        commands_.add(c);
        command_names_.add(c.name());
        c.setCommands(this);
    }

    public boolean isCommand(String s)
    {
        return command_names_.contains(s);
    }

    public Command lookup(String s)
    {
        for (Command c : commands_)
        {
            if (c.name().equals(s)) return c;
        }
        return null;
    }

    public List<Command> list()
    {
        return commands_;
    }
}
