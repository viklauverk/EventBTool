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

public class Environment
{
    Config config_;

    public Environment(Config config)
    {
        config_ = config;
    }

    public boolean setup()
    {
        return true;
    }

    public void go(List<CommandWithArguments> cwas)
    {
        for (CommandWithArguments cwa : cwas)
        {
            try
            {
                CommandCommon cmd = cwa.cmd().constructor().create(this, config_);
                cmd.run(cwa.args());
            }
            catch (UsageError e)
            {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }
}
