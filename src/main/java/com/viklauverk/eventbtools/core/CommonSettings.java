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
package com.viklauverk.eventbtools.core;

import java.util.List;
import java.util.ArrayList;

import java.io.File;

public class CommonSettings
{
    private boolean black_white_enabled_;
    private boolean verbose_enabled_;
    private boolean debug_enabled_;
    private boolean trace_enabled_;

    private String source_dir_;
    private String nick_name_;
    private String output_dir_ = ".";
    private List<String> machines_and_contexts_ = new ArrayList<>();

    private String source_file_;
    private String dest_file_;

    public CommonSettings()
    {
    }

    public boolean blackWhiteEnabled() { return black_white_enabled_; }
    public void setBlackWhiteEnabled(boolean v) { black_white_enabled_ = v; }

    public boolean verboseEnabled() { return verbose_enabled_; }
    public void setVerboseEnabled(boolean v) { verbose_enabled_ = v; }

    public boolean debugEnabled() { return debug_enabled_; }
    public void setDebugEnabled(boolean v) { debug_enabled_ = v; }

    public boolean traceEnabled() { return trace_enabled_; }
    public void setTraceEnabled(boolean v) { trace_enabled_ = v; }

    public String nickName() { return nick_name_; }
    public void setNickName(String n) { nick_name_ = n; }

    public String sourceDir() { return source_dir_; }
    public void setSourceDir(String sd)
    {
        if (sd.equals("")) return;
        while (sd.charAt(sd.length()-1) == '/')
        {
            sd = sd.substring(0, sd.length()-1);
        }
        File dir = new File(sd);
        if (!dir.exists() || !dir.isDirectory())
        {
            LogModule.usageErrorStatic("Not a directory \"%s\"", sd);
            System.exit(1);
        }
        source_dir_ = sd;
        int ls = sd.lastIndexOf(File.separator);
        if (nick_name_ == null)
        {
            setNickName(sd.substring(ls+1));
        }
    }

    public String outputDir() { return output_dir_; }
    public void setOutputDir(String d)
    {
        File dir = new File(d);
        if (!dir.exists())
        {
            LogModule.usageErrorStatic("Not a directory \"%s\"", d);
        }
        if (!dir.canWrite())
        {
            LogModule.usageErrorStatic("Cannot write to directory \"%s\"", d);
        }
        output_dir_ = d;
    }

    public void addMachineOrContext(String mc) { machines_and_contexts_.add(mc); }
    public List<String> machinesAndContexts() { return machines_and_contexts_; }

    public void setSourceFile(String f)
    {
        source_file_ = f;
    }

    public String sourceFile()
    {
        return source_file_;
    }

    public void setDestFile(String f)
    {
        dest_file_ = f;
    }

    public String destFile()
    {
        return dest_file_;
    }


}
