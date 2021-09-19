/*
 Copyright (C) 2021 Viklauverk AB
 Author Fredrik Öhrström

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

public enum ContainingCardinality
{
    INT8, INT16, INT32, INT64, UINT8, UINT16, UINT32, Z;

    final static long uint8_hi = 255L;
    final static long int8_hi = 127L;
    final static long int8_lo = -128L;

    final static long uint16_hi = 65535L;
    final static long int16_hi = 32767L;
    final static long int16_lo = -32768L;

    final static long uint32_hi = 4294967295L;
    final static long int32_hi = Integer.MAX_VALUE;
    final static long int32_lo = Integer.MIN_VALUE;

    static ContainingCardinality fromBounds(Bounds b)
    {
        // Infinite boundaries? Recommend dynamic storage.
        // This can be overriden, but then each operation is bounds checked.
        if (b.isLowerInfinite() || b.isUpperInfinite()) return Z;

        // Use unsigned only when we have to....
        if (b.lo >= 0)
        {
            if (b.hi <= uint8_hi && b.hi > int8_hi) return UINT8;
            if (b.hi <= uint16_hi && b.hi > int16_hi) return UINT16;
            if (b.hi <= uint32_hi && b.hi > int32_hi) return UINT32;
        }

        // Remaining ranges fit in signed storage.
        if (b.lo >= int8_lo && b.hi <= int8_hi) return INT8;
        if (b.lo >= int16_lo && b.hi <= int16_hi) return INT16;
        if (b.lo >= int32_lo && b.hi <= int32_hi) return INT32;
        return INT64;
    }

}
