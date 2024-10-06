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

import com.viklauverk.common.log.Log;
import com.viklauverk.common.log.LogLevel;
import com.viklauverk.common.log.LogModule;

public class TestInternals
{
    public static void main(String[] args)
    {
        LogModule.setLogLevelFor("all", LogLevel.INFO);

        boolean ok = true;

        ok &= testContainingCardinalitys();
        ok &= testMetas();

        if (!ok)
        {
            System.out.println("ERROR(s) detected!");
            System.exit(1);
        }
        System.exit(0);
    }

    // Test choose ContainingCardinality fromBounds.
    public static boolean testfb(long lo, long hi, ContainingCardinality expected)
    {
        Bounds b = new Bounds(lo, hi);
        ContainingCardinality got = ContainingCardinality.fromBounds(b);
        if (expected != got)
        {
            System.out.println("From bounds "+b+" expected "+expected+" but got "+got);
            return false;
        }
        return true;
    }

    public static boolean testContainingCardinalitys()
    {
        boolean ok = true;
        ok &= testfb(0, 100, ContainingCardinality.INT8);
        ok &= testfb(128, 250, ContainingCardinality.UINT8);
        ok &= testfb(-1, 250, ContainingCardinality.INT16);
        ok &= testfb(-1, 1, ContainingCardinality.INT8);
        ok &= testfb(-1, 0, ContainingCardinality.INT8);
        ok &= testfb(-100, -10, ContainingCardinality.INT8);

        ok &= testfb(0, 1024, ContainingCardinality.INT16);
        ok &= testfb(0, 65535, ContainingCardinality.UINT16);
        ok &= testfb(65535, 65535, ContainingCardinality.UINT16);

        ok &= testfb(32767, 32767, ContainingCardinality.INT16);
        ok &= testfb(-1000000, 0, ContainingCardinality.INT32);

        ok &= testfb(-10000000000L, 0, ContainingCardinality.INT64);

        ok &= testfb(0, Long.MAX_VALUE, ContainingCardinality.Z);

        Bounds b = Bounds.parse("-i16..i16");
//        System.out.println(b.toString());

        return ok;
    }

    public static boolean testm(String in, String out, String out_with_metas,
                                String out_with_types, String out_with_metas_and_types)
    {
        SymbolTable fc = SymbolTable.PQR_EFG_STU_xyz_cdf_NM_ABC_H_cx_dx_op;

        Formula fo = Formula.fromString(in, fc);

        String o = fo.toString();
        String ot = fo.toStringWithTypes();
        String om = fo.toStringWithMetas();
        String omt = fo.toStringWithMetasAndTypes();

        boolean o1 = o.equals(out);
        boolean o2 = om.equals(out_with_metas);
        boolean o3 = ot.equals(out_with_types);
        boolean o4 = omt.equals(out_with_metas_and_types);

        boolean ok = o1 && o2 && o3 && o4;

        if (!ok)
        {
            System.out.println("Error parsing "+in);
        }

        if (!o1)
        {
            System.out.println("Expected plain \""+out+"\"");
            System.out.println("But got  plain \""+o+"\"");
        }
        if (!o2)
        {
            System.out.println("Expected metas \""+out_with_metas+"\"");
            System.out.println("But got  metas \""+om+"\"");
        }
        if (!o3)
        {
            System.out.println("Expected types \""+out_with_types+"\"");
            System.out.println("But got  types \""+ot+"\"");
        }
        if (!o4)
        {
            System.out.println("Expected metas&types \""+out_with_metas_and_types+"\"");
            System.out.println("But got  metas&types \""+omt+"\"");
        }

        return ok;
    }

    public static boolean testMetas()
    {
        boolean ok = true;
        ok &= testm("0..«8» 7 -->«8|->7» 0..«8+9» 255",
                    "0‥7→ 0‥255", // Plain out
                    "0‥«8»7→ «8↦7»0‥«8+9»255", // Plain with metas
                    "<TOTAL_FUNCTION <UP_TO <NUMBER 0>‥<NUMBER 7>>→ <UP_TO <NUMBER 0>‥<NUMBER 255>>>", // Types
                    "<TOTAL_FUNCTION <UP_TO <NUMBER 0>‥«<NUMBER 8>»<NUMBER 7>>→ «<MAPSTO <NUMBER 8>↦<NUMBER 7>>»<UP_TO <NUMBER 0>‥«<ADDITION <NUMBER 8>+<NUMBER 9>>»<NUMBER 255>>>"); // Types with metas

        ok &= testm("5«7»",
                    "5", // Plain out
                    "5«7»", // Plain with metas
                    "<NUMBER 5>", // Types
                    "<NUMBER 5«<NUMBER 7>»>"); // Types with metas

        return ok;
    }

}
