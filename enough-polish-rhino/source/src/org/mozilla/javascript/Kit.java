/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Rhino code, released
 * May 6, 1999.
 *
 * The Initial Developer of the Original Code is
 * Netscape Communications Corporation.
 * Portions created by the Initial Developer are Copyright (C) 1997-1999
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Igor Bukanov, igor@fastmail.fm
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU General Public License Version 2 or later (the "GPL"), in which
 * case the provisions of the GPL are applicable instead of those above. If
 * you wish to allow use of your version of this file only under the terms of
 * the GPL and not to allow others to use your version of this file under the
 * MPL, indicate your decision by deleting the provisions above and replacing
 * them with the notice and other provisions required by the GPL. If you do
 * not delete the provisions above, a recipient may use your version of this
 * file under either the MPL or the GPL.
 *
 * ***** END LICENSE BLOCK ***** */

package org.mozilla.javascript;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;

/**
 * Collection of utilities
 */

public class Kit
{

    public static Class classOrNull(String className)
    {
        try {
            return Class.forName(className);
        } catch  (ClassNotFoundException ex) {
        } catch  (SecurityException ex) {
//        } catch  (LinkageError ex) {
        } catch (IllegalArgumentException e) {
            // Can be thrown if name has characters that a class name
            // can not contain
        }
        return null;
    }

    static Object newInstanceOrNull(Class cl)
    {
        try {
            return cl.newInstance();
        } catch (SecurityException x) {
//        } catch  (LinkageError ex) {
        } catch (InstantiationException x) {
        } catch (IllegalAccessException x) {
        }
        return null;
    }

    /**
     * Split string into array of strings using semicolon as string terminator
     * (; after the last string is required).
     */
    public static String[] semicolonSplit(String s)
    {
        String[] array = null;
        for (;;) {
            // loop 2 times: first to count semicolons and then to fill array
            int count = 0;
            int cursor = 0;
            for (;;) {
                int next = s.indexOf(';', cursor);
                if (next < 0) {
                    break;
                }
                if (array != null) {
                    array[count] = s.substring(cursor, next);
                }
                ++count;
                cursor = next + 1;
            }
            // after the last semicolon
            if (array == null) {
                // array size counting state:
                // check for required terminating ';'
                if (cursor != s.length())
                    throw new IllegalArgumentException();
                array = new String[count];
            } else {
                // array filling state: stop the loop
                break;
            }
        }
        return array;
    }

    /**
     * If character <tt>c</tt> is a hexadecimal digit, return
     * <tt>accumulator</tt> * 16 plus corresponding
     * number. Otherise return -1.
     */
    public static int xDigitToInt(int c, int accumulator)
    {
        check: {
            // Use 0..9 < A..Z < a..z
            if (c <= '9') {
                c -= '0';
                if (0 <= c) { break check; }
            } else if (c <= 'F') {
                if ('A' <= c) {
                    c -= ('A' - 10);
                    break check;
                }
            } else if (c <= 'f') {
                if ('a' <= c) {
                    c -= ('a' - 10);
                    break check;
                }
            }
            return -1;
        }
        return (accumulator << 4) | c;
    }

    /**
     * Get listener at <i>index</i> position in <i>bag</i> or null if
     * <i>index</i> equals to number of listeners in <i>bag</i>.
     * <p>
     * For usage example, see {@link #addListener(Object bag, Object listener)}.
     *
     * @param bag Current collection of listeners.
     * @param index Index of the listener to access.
     * @return Listener at the given index or null.
     * @see #addListener(Object bag, Object listener)
     * @see #removeListener(Object bag, Object listener)
     */
    public static Object getListener(Object bag, int index)
    {
        if (index == 0) {
            if (bag == null)
                return null;
            if (!(bag instanceof Object[]))
                return bag;
            Object[] array = (Object[])bag;
            // bag has at least 2 elements if it is array
            if (array.length < 2) throw new IllegalArgumentException();
            return array[0];
        } else if (index == 1) {
            if (!(bag instanceof Object[])) {
                if (bag == null) throw new IllegalArgumentException();
                return null;
            }
            Object[] array = (Object[])bag;
            // the array access will check for index on its own
            return array[1];
        } else {
            // bag has to array
            Object[] array = (Object[])bag;
            int L = array.length;
            if (L < 2) throw new IllegalArgumentException();
            if (index == L)
                return null;
            return array[index];
        }
    }

    static Object initHash(Hashtable h, Object key, Object initialValue)
    {
        synchronized (h) {
            Object current = h.get(key);
            if (current == null) {
                h.put(key, initialValue);
            } else {
                initialValue = current;
            }
        }
        return initialValue;
    }

    /**
     * Throws RuntimeException to indicate failed assertion.
     * The function never returns and its return type is RuntimeException
     * only to be able to write <tt>throw Kit.codeBug()</tt> if plain
     * <tt>Kit.codeBug()</tt> triggers unreachable code error.
     */
    public static RuntimeException codeBug()
        throws RuntimeException
    {
        RuntimeException ex = new IllegalStateException("FAILED ASSERTION");
        // Print stack trace ASAP
        ex.printStackTrace();
        throw ex;
    }
}

