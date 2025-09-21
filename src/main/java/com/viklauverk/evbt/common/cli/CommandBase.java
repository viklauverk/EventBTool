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

public abstract class CommandBase
{
    private Environment environment_;
    private CommandSequence sequence_;

    public CommandBase(Environment environment, CommandSequence sequence)
    {
        environment_ = environment;
        sequence_ = sequence;
    }

    CommandSequence sequence()
    {
        return sequence_;
    }

    Environment environment()
    {
        return environment_;
    }

    public abstract void run(String[] args);
    public abstract String help();
}
