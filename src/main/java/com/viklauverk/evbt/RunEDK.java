/*
 Copyright (C) 2021-2024 Viklauverk AB (agpl-3.0-or-later)

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

package com.viklauverk.evbt;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.viklauverk.evbt.common.log.Log;
import com.viklauverk.evbt.common.log.LogModule;
import com.viklauverk.evbt.core.Settings;

public class RunEDK
{
    private static Log log = LogModule.lookup("evbt.edk", RunEDK.class);

    public static void run(CmdArgs ca, Settings s)
        throws Exception
    {
        log.info("installing EDK into %s", s.commonSettings().outputDir());
        List<String> files = listEDKFiles();
        for (String f : files)
        {
            copy("/EDK/"+f, s.commonSettings().outputDir()+"/"+f, Main.class);
        }
    }

    public static List<String> listEDKFiles()
    {
        CodeSource src = Main.class.getProtectionDomain().getCodeSource();
        List<String> list = new ArrayList<String>();

        if (src != null)
        {
            URL jar = src.getLocation();
            try
            {
                ZipInputStream zip = new ZipInputStream( jar.openStream());
                ZipEntry ze = null;

                while( ( ze = zip.getNextEntry() ) != null )
                {
                    String entry = ze.getName();
                    int p = entry.indexOf("/EDK/EDK_");
                    if (p != -1)
                    {
                        entry = entry.substring(p+5);
                        if (entry.endsWith(".buc") ||
                            entry.endsWith(".bpr"))
                        {
                            list.add(entry);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                log.warn("%s", e.getMessage());
            }
        }
        return list;
    }

    public static boolean copy(String resource, String destination, Class<Main> c)
    {
        boolean ok = true;

        log.info("%s", destination);

        try
        {
            InputStream src = c.getResourceAsStream(resource);
            Files.copy(src, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException ex)
        {
            log.warn("%s", ex.getMessage());
            ok = false;
        }
        return ok;
    }
}
