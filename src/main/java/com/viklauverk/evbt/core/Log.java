/*
 Copyright (C) 2021 Viklauverk AB
 
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
// 
package com.viklauverk.evbt.core;

public interface Log
{
    /** Print the msg prefixed with the module and exit. */
    void error(String msg, Object... args);
    /** Print the msg without module prefix and exit. */
    void usageError(String msg, Object... args);
    /** Print the msg prefixed with the module and internal error and exit. */
    void internalError(String msg, Object... args);
    /** Print the msg prefixed with the module and continue. */
    void failure(String msg, Object... args);
    /** Print a warning and continue. */
    void warn(String msg, Object... args);
    /** Print info. */
    void info(String msg, Object... args);
    /** Print if verbose is enabled. */
    void verbose(String msg, Object... args);
    /** Print if deubg is enabled. */
    void debug(String msg, Object... args);
    /** Print with additional part name within parentheses. Eg. (codegen guard) */
    void debugp(String part, String msg, Object... args);
    /** Print if trace is enabled. */
    void trace(String msg, Object... args);
    /** Return true if verbose logging is enabled. */
    boolean verboseEnabled();
    /** Return true if debug logging is enabled. */
    boolean debugEnabled();
    /** Return true if trace logging is enabled. */
    boolean traceEnabled();

    /** Return true if debugging Canvas generation.
        I.e. insert debug printouts into the Canvas. */
    boolean debugCanvasEnabled();

}
