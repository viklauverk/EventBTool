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

package com.viklauverk.eventbtools.core;

public class Bounds
{
    final long lo;
    final long hi;

    // If low == Long.LONG_MIN then this means negative infinite lower bound.
    // If hi == Long.LONG_MAX then this means positive infinite upper bound.
    // There is no u64 because we use i64 to check overflow of u32 bites...
    // also hi of i64 is therefore not Long.LONG_MAX, but "only" Long.LONG_MAX-1
    // also lo of i64 is therefore not Long.LONG_MIN, but "only" Long.LONG_MIN+1
    public Bounds(long loo, long hii)
    {
        assert loo <= hii : "Internal Error. Bounds must be lo <= hi but lo "+loo+" > hi "+hii;
        lo = loo;
        hi = hii;
    }

    public boolean isLowerInfinite()
    {
        return lo == Long.MIN_VALUE;
    }

    public boolean isUpperInfinite()
    {
        return hi == Long.MAX_VALUE;
    }

    static long decode(String s)
    {
        if (s.equals("-i8")) return -128;
        if (s.equals("i8")) return 127;
        if (s.equals("ui8")) return 255;
        if (s.equals("-i16")) return -32768;
        if (s.equals("i16")) return 32767;
        if (s.equals("ui16")) return 65535;
        if (s.equals("-i32")) return Integer.MIN_VALUE;
        if (s.equals("i32")) return Integer.MAX_VALUE;
        if (s.equals("ui32")) return 4294967295L;
        if (s.equals("-i64")) return Long.MIN_VALUE+1;
        if (s.equals("i64")) return Long.MAX_VALUE-1;
        if (s.equals("-z")) return Long.MIN_VALUE;
        if (s.equals("z")) return Long.MAX_VALUE;

        try
        {
            return Long.parseLong(s);
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }

    static Bounds parse(String s)
    {
        int p = s.indexOf("..");
        int len = 2;
        if (p == -1)
        {
            p = s.indexOf("‥");
            len = 1;
        }
        String los = s.substring(0, p);
        String his = s.substring(p+len);

        return new Bounds(decode(los),decode(his));
    }

    static String recode(long v)
    {
        if (v == -128) return "-i8";
        if (v == 127) return "i8";
        if (v == 255) return "ui8";
        if (v == -32768) return "-i16";
        if (v == 32767) return "i16";
        if (v == 65535) return "ui16";
        if (v == Integer.MIN_VALUE) return "-i32";
        if (v == Integer.MAX_VALUE) return "i32";
        if (v == 4294967295L) return "ui32";
        if (v == Long.MIN_VALUE+1) return "-i64";
        if (v == Long.MAX_VALUE-1) return "i64";
        if (v == Long.MIN_VALUE) return "-z";
        if (v == Long.MAX_VALUE) return "z";

        return ""+v;
    }

    public String toString()
    {
        return recode(lo)+"‥"+recode(hi);
    }
}
