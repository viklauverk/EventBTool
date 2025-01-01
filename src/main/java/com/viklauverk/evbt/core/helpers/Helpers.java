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

package com.viklauverk.evbt.core.helpers;

import java.util.List;

public class Helpers
{
    public static <E> boolean isEmpty(List<E> l)
    {
        return l.size() == 0;
    }

    public static <E> E back(List<E> l)
    {
        if (l.size() == 0) return null;
        return l.get(l.size()-1);
    }

    public static <E> E front(List<E> l)
    {
        if (l.size() == 0) return null;
        return l.get(0);
    }

    public static <E> E popBack(List<E> l)
    {
        if (l.size() == 0) return null;
        E e = l.get(l.size()-1);
        l.remove(l.size()-1);
        return e;
    }

    public static <E> E popFront(List<E> l)
    {
        if (l.size() == 0) return null;
        E e = l.get(0);
        l.remove(0);
        return e;
    }

    public static <E> void pushBack(List<E> l, E e)
    {
        l.add(e);
    }

    public static <E> void pushFront(List<E> l, E e)
    {
        l.add(0, e);
    }

}
