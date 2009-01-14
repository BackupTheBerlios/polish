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
 * Portions created by the Initial Developer are Copyright (C) 1997-2000
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Igor Bukanov
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


import de.enough.polish.io.Externalizable;
import de.enough.polish.io.Serializer;

/**
 * Map to associate non-negative integers to objects or integers.
 * The map does not synchronize any of its operation, so either use
 * it from a single thread or do own synchronization or perform all mutation
 * operations on one thread before passing the map to others.
 *
 * @author Igor Bukanov
 *
 */

public class UintMap implements Externalizable
{
    //static final long serialVersionUID = 4242698212885848444L;

// Map implementation via hashtable,
// follows "The Art of Computer Programming" by Donald E. Knuth

    public UintMap() {
        this(4);
    }

    public UintMap(int initialCapacity) {
        if (initialCapacity < 0) Kit.codeBug();
        // Table grow when number of stored keys >= 3/4 of max capacity
        int minimalCapacity = initialCapacity * 4 / 3;
        int i;
        for (i = 2; (1 << i) < minimalCapacity; ++i) { }
        power = i;
        if (check && power < 2) Kit.codeBug();
    }

    /**
     * Get integer value assigned with key.
     * @return key integer value or defaultValue if key does not exist or does
     * not have int value
     * @throws RuntimeException if key does not exist
     */
    public int getExistingInt(int key) {
        if (key < 0) Kit.codeBug();
        int index = findIndex(key);
        if (0 <= index) {
            if (ivaluesShift != 0) {
                return iKeys[ivaluesShift + index];
            }
            return 0;
        }
        // Key must exist
        Kit.codeBug();
        return 0;
    }

    /**
     * Set int value of the key.
     * If key does not exist, also set its object value to null.
     */
    public void put(int key, int value) {
        if (key < 0) Kit.codeBug();
        int index1 = ensureIndex(key, true);
        if (ivaluesShift == 0) {
            int N = 1 << power;
            // keys.length can be N * 2 after clear which set ivaluesShift to 0
            if (iKeys.length != N * 2) {
                int[] tmp = new int[N * 2];
                System.arraycopy(iKeys, 0, tmp, 0, N);
                iKeys = tmp;
            }
            ivaluesShift = N;
        }
        iKeys[ivaluesShift + index1] = value;
    }

    private static int tableLookupStep(int fraction, int mask, int power) {
        int shift = 32 - 2 * power;
        if (shift >= 0) {
            return ((fraction >>> shift) & mask) | 1;
        }
        else {
            return (fraction & (mask >>> -shift)) | 1;
        }
    }

    private int findIndex(int key) {
        int[] keys1 = this.iKeys;
        if (keys1 != null) {
            int fraction = key * A;
            int index2 = fraction >>> (32 - power);
            int entry = keys1[index2];
            if (entry == key) { return index2; }
            if (entry != EMPTY) {
                // Search in table after first failed attempt
                int mask = (1 << power) - 1;
                int step = tableLookupStep(fraction, mask, power);
                int n = 0;
                do {
                    if (check) {
                        if (n >= occupiedCount) Kit.codeBug();
                        ++n;
                    }
                    index2 = (index2 + step) & mask;
                    entry = keys1[index2];
                    if (entry == key) { return index2; }
                } while (entry != EMPTY);
            }
        }
        return -1;
    }

// Insert key that is not present to table without deleted entries
// and enough free space
    private int insertNewKey(int key) {
        if (check && occupiedCount != keyCount) Kit.codeBug();
        if (check && keyCount == 1 << power) Kit.codeBug();
        int[] keys2 = this.iKeys;
        int fraction = key * A;
        int index3 = fraction >>> (32 - power);
        if (keys2[index3] != EMPTY) {
            int mask = (1 << power) - 1;
            int step = tableLookupStep(fraction, mask, power);
            int firstIndex = index3;
            do {
                if (check && keys2[index3] == DELETED) Kit.codeBug();
                index3 = (index3 + step) & mask;
                if (check && firstIndex == index3) Kit.codeBug();
            } while (keys2[index3] != EMPTY);
        }
        keys2[index3] = key;
        ++occupiedCount;
        ++keyCount;
        return index3;
    }

    private void rehashTable(boolean ensureIntSpace) {
        if (iKeys != null) {
            // Check if removing deleted entries would free enough space
            if (keyCount * 2 >= occupiedCount) {
                // Need to grow: less then half of deleted entries
                ++power;
            }
        }
        int N = 1 << power;
        int[] old = iKeys;
        int oldShift = ivaluesShift;
        if (oldShift == 0 && !ensureIntSpace) {
            iKeys = new int[N];
        }
        else {
            ivaluesShift = N; iKeys = new int[N * 2];
        }
        for (int i = 0; i != N; ++i) { iKeys[i] = EMPTY; }

        Object[] oldValues = values;
        if (oldValues != null) { values = new Object[N]; }

        int oldCount = keyCount;
        occupiedCount = 0;
        if (oldCount != 0) {
            keyCount = 0;
            for (int i = 0, remaining = oldCount; remaining != 0; ++i) {
                int key = old[i];
                if (key != EMPTY && key != DELETED) {
                    int index4 = insertNewKey(key);
                    if (oldValues != null) {
                        values[index4] = oldValues[i];
                    }
                    if (oldShift != 0) {
                        iKeys[ivaluesShift + index4] = old[oldShift + i];
                    }
                    --remaining;
                }
            }
        }
    }

// Ensure key index creating one if necessary
    private int ensureIndex(int key, boolean intType) {
        int index5 = -1;
        int firstDeleted = -1;
        int[] keys3 = this.iKeys;
        if (keys3 != null) {
            int fraction = key * A;
            index5 = fraction >>> (32 - power);
            int entry = keys3[index5];
            if (entry == key) { return index5; }
            if (entry != EMPTY) {
                if (entry == DELETED) { firstDeleted = index5; }
                // Search in table after first failed attempt
                int mask = (1 << power) - 1;
                int step = tableLookupStep(fraction, mask, power);
                int n = 0;
                do {
                    if (check) {
                        if (n >= occupiedCount) Kit.codeBug();
                        ++n;
                    }
                    index5 = (index5 + step) & mask;
                    entry = keys3[index5];
                    if (entry == key) { return index5; }
                    if (entry == DELETED && firstDeleted < 0) {
                        firstDeleted = index5;
                    }
                } while (entry != EMPTY);
            }
        }
        // Inserting of new key
        if (check && keys3 != null && keys3[index5] != EMPTY)
            Kit.codeBug();
        if (firstDeleted >= 0) {
            index5 = firstDeleted;
        }
        else {
            // Need to consume empty entry: check occupation level
            if (keys3 == null || occupiedCount * 4 >= (1 << power) * 3) {
                // Too litle unused entries: rehash
                rehashTable(intType);
                keys3 = this.iKeys;
                return insertNewKey(key);
            }
            ++occupiedCount;
        }
        keys3[index5] = key;
        ++keyCount;
        return index5;
    }

// A == golden_ratio * (1 << 32) = ((sqrt(5) - 1) / 2) * (1 << 32)
// See Knuth etc.
    private static final int A = 0x9e3779b9;

    private static final int EMPTY = -1;
    private static final int DELETED = -2;

// Structure of kyes and values arrays (N == 1 << power):
// keys[0 <= i < N]: key value or EMPTY or DELETED mark
// values[0 <= i < N]: value of key at keys[i]
// keys[N <= i < 2N]: int values of keys at keys[i - N]

    private transient int[] iKeys;
    private transient Object[] values;

    private int power;
    private int keyCount;
    private transient int occupiedCount; // == keyCount + deleted_count

    // If ivaluesShift != 0, keys[ivaluesShift + index] contains integer
    // values associated with keys
    private transient int ivaluesShift;

// If true, enables consistency checks
    private static final boolean check = false;
    
    
    


 // Structure of kyes and values arrays (N == 1 << power):
 // keys[0 <= i < N]: key value or EMPTY or DELETED mark
 // values[0 <= i < N]: value of key at keys[i]
 // keys[N <= i < 2N]: int values of keys at keys[i - N]

     private transient int[] keys;


    

    /**
     * Get integer value assigned with key.
     * @return key integer value or defaultValue if key is absent
     */
    public int getInt(int key, int defaultValue) {
        if (key < 0) Kit.codeBug();
        int index = findIndex(key);
        if (0 <= index) {
            if (this.ivaluesShift != 0) {
                return keys[this.ivaluesShift + index];
            }
            return 0;
        }
        return defaultValue;
    }


	
	public void read(DataInputStream in) throws IOException {
	    power = in.readInt();
	    keyCount = in.readInt();
        int writtenKeyCount = keyCount;
        if (writtenKeyCount != 0) {
            keyCount = 0;
            boolean hasIntValues = in.readBoolean();
            boolean hasObjectValues = in.readBoolean();

            int N = 1 << power;
            if (hasIntValues) {
                iKeys = new int[2 * N];
                ivaluesShift = N;
            }else {
                iKeys = new int[N];
            }
            for (int i = 0; i != N; ++i) {
                iKeys[i] = EMPTY;
            }
            if (hasObjectValues) {
                values = new Object[N];
            }
            for (int i = 0; i != writtenKeyCount; ++i) {
                int key = in.readInt();
                int index6 = insertNewKey(key);
                if (hasIntValues) {
                    int ivalue = in.readInt();
                    iKeys[ivaluesShift + index6] = ivalue;
                }
                if (hasObjectValues) {
                    values[index6] = Serializer.deserialize(in);
                }
            }
        }

	}

	
	public void write(DataOutputStream out) throws IOException {
		out.writeInt(power);
		out.writeInt(keyCount);
        int count = keyCount;
        if (count != 0) {
            boolean hasIntValues = (ivaluesShift != 0);
            boolean hasObjectValues = (values != null);
            out.writeBoolean(hasIntValues);
            out.writeBoolean(hasObjectValues);

            for (int i = 0; count != 0; ++i) {
                int key = iKeys[i];
                if (key != EMPTY && key != DELETED) {
                    --count;
                    out.writeInt(key);
                    if (hasIntValues) {
                        out.writeInt(iKeys[ivaluesShift + i]);
                    }
                    if (hasObjectValues) {
                        Serializer.serialize(values[i], out);
                    }
                }
            }
        }

	}

/* TEST START

    public static void main(String[] args) {
        if (!check) {
            System.err.println("Set check to true and re-run");
            throw new RuntimeException("Set check to true and re-run");
        }

        UintMap map;
        map = new UintMap();
        testHash(map, 2);
        map = new UintMap();
        testHash(map, 10 * 1000);
        map = new UintMap(30 * 1000);
        testHash(map, 10 * 100);
        map.clear();
        testHash(map, 4);
        map = new UintMap(0);
        testHash(map, 10 * 100);
    }

    private static void testHash(UintMap map, int N) {
        System.out.print("."); System.out.flush();
        for (int i = 0; i != N; ++i) {
            map.put(i, i);
            check(i == map.getInt(i, -1));
        }

        System.out.print("."); System.out.flush();
        for (int i = 0; i != N; ++i) {
            map.put(i, i);
            check(i == map.getInt(i, -1));
        }

        System.out.print("."); System.out.flush();
        for (int i = 0; i != N; ++i) {
            map.put(i, new Integer(i));
            check(-1 == map.getInt(i, -1));
            Integer obj = (Integer)map.getObject(i);
            check(obj != null && i == obj.intValue());
        }

        check(map.size() == N);

        System.out.print("."); System.out.flush();
        int[] keys = map.getKeys();
        check(keys.length == N);
        for (int i = 0; i != N; ++i) {
            int key = keys[i];
            check(map.has(key));
            check(!map.isIntType(key));
            check(map.isObjectType(key));
            Integer obj = (Integer) map.getObject(key);
            check(obj != null && key == obj.intValue());
        }


        System.out.print("."); System.out.flush();
        for (int i = 0; i != N; ++i) {
            check(-1 == map.getInt(i, -1));
        }

        System.out.print("."); System.out.flush();
        for (int i = 0; i != N; ++i) {
            map.put(i * i, i);
            check(i == map.getInt(i * i, -1));
        }

        System.out.print("."); System.out.flush();
        for (int i = 0; i != N; ++i) {
            check(i == map.getInt(i * i, -1));
        }

        System.out.print("."); System.out.flush();
        for (int i = 0; i != N; ++i) {
            map.put(i * i, new Integer(i));
            check(-1 == map.getInt(i * i, -1));
            map.remove(i * i);
            check(!map.has(i * i));
            map.put(i * i, i);
            check(map.isIntType(i * i));
            check(null == map.getObject(i * i));
            map.remove(i * i);
            check(!map.isObjectType(i * i));
            check(!map.isIntType(i * i));
        }

        int old_size = map.size();
        for (int i = 0; i != N; ++i) {
            map.remove(i * i);
            check(map.size() == old_size);
        }

        System.out.print("."); System.out.flush();
        map.clear();
        check(map.size() == 0);
        for (int i = 0; i != N; ++i) {
            map.put(i * i, i);
            map.put(i * i + 1, new Double(i+0.5));
        }
        checkSameMaps(map, (UintMap)writeAndRead(map));

        System.out.print("."); System.out.flush();
        map = new UintMap(0);
        checkSameMaps(map, (UintMap)writeAndRead(map));
        map = new UintMap(1);
        checkSameMaps(map, (UintMap)writeAndRead(map));
        map = new UintMap(1000);
        checkSameMaps(map, (UintMap)writeAndRead(map));

        System.out.print("."); System.out.flush();
        map = new UintMap(N / 10);
        for (int i = 0; i != N; ++i) {
            map.put(2*i+1, i);
        }
        checkSameMaps(map, (UintMap)writeAndRead(map));

        System.out.print("."); System.out.flush();
        map = new UintMap(N / 10);
        for (int i = 0; i != N; ++i) {
            map.put(2*i+1, i);
        }
        for (int i = 0; i != N / 2; ++i) {
            map.remove(2*i+1);
        }
        checkSameMaps(map, (UintMap)writeAndRead(map));

        System.out.print("."); System.out.flush();
        map = new UintMap();
        for (int i = 0; i != N; ++i) {
            map.put(2*i+1, new Double(i + 10));
        }
        for (int i = 0; i != N / 2; ++i) {
            map.remove(2*i+1);
        }
        checkSameMaps(map, (UintMap)writeAndRead(map));

        System.out.println(); System.out.flush();

    }

    private static void checkSameMaps(UintMap map1, UintMap map2) {
        check(map1.size() == map2.size());
        int[] keys = map1.getKeys();
        check(keys.length == map1.size());
        for (int i = 0; i != keys.length; ++i) {
            int key = keys[i];
            check(map2.has(key));
            check(map1.isObjectType(key) == map2.isObjectType(key));
            check(map1.isIntType(key) == map2.isIntType(key));
            Object o1 = map1.getObject(key);
            Object o2 = map2.getObject(key);
            if (map1.isObjectType(key)) {
                check(o1.equals(o2));
            }else {
                check(map1.getObject(key) == null);
                check(map2.getObject(key) == null);
            }
            if (map1.isIntType(key)) {
                check(map1.getExistingInt(key) == map2.getExistingInt(key));
            }else {
                check(map1.getInt(key, -10) == -10);
                check(map1.getInt(key, -11) == -11);
                check(map2.getInt(key, -10) == -10);
                check(map2.getInt(key, -11) == -11);
            }
        }
    }

    private static void check(boolean condition) {
        if (!condition) Kit.codeBug();
    }

    private static Object writeAndRead(Object obj) {
        try {
            java.io.ByteArrayOutputStream
                bos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutputStream
                out = new java.io.ObjectOutputStream(bos);
            out.writeObject(obj);
            out.close();
            byte[] data = bos.toByteArray();
            java.io.ByteArrayInputStream
                bis = new java.io.ByteArrayInputStream(data);
            java.io.ObjectInputStream
                in = new java.io.ObjectInputStream(bis);
            Object result = in.readObject();
            in.close();
            return result;
        }catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Unexpected");
        }
    }

// TEST END */
}
