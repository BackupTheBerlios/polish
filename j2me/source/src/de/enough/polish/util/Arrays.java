/*
 * Created on 05.01.2006 at 15:14:27.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.util;

public final class Arrays implements Comparator { 
	
	//TODO: JavaDoc, make sort work, ensure TestArrays testcase works
	private static final Comparator STRING_COMPARATOR = new Arrays();
	// instantiation is not allowed
	private Arrays() {
		super();
	}
	
	public int compare(Object o1, Object o2) {
		return o1.toString().compareTo(o2.toString());
	}

    public static void sort(Object[] obj) {
    	sort( obj, STRING_COMPARATOR );
    }

    public static void quicksort(Object[] obj) {
    	quicksort( obj,obj.length, STRING_COMPARATOR );
    }
    
    public static void iQuick(int array[], int elements)
    {
            int left=0, right=elements-1, top=0;
            int sSize = elements/2;
            int lStack[] = new int[sSize];
            int rStack[] = new int[sSize];
            int tmp;
            int i, j, x;

            lStack[top] = left; rStack[top] = right;

            while (top >= 0)
            {
                    left = lStack[top];
                    right = rStack[top];
                    top--;

                    while (left < right)
                    {
                            i = left;
                            j = right;
                            x = array[(left+right)/2];

                            while (i <= j)
                            {
                                while (array[i] < x) i++;
                                while (array[j] > x) j--;

                                    if (i<=j)
                                    {
                                            { // SWAP
                                                    tmp = array[i];
                                                    array[i] = array[j];
                                                    array[j] = tmp;
                                            }
                                            i++;
                                            j--;
                                    }
                            }

                            if (j-left < right-i)
                            {
                                    if (i < right)
                                    {
                                            top++;
                                            lStack[top] = i;
                                            rStack[top] = right;
                                    }
                                    right = j;
                            }
                            else
                            {
                                    if (left < j)
                                    {
                                            top++;
                                            lStack[top] = left;
                                            rStack[top] = j;
                                    }
                                    left = i;
                            }
                    }
            }
    }
    
    public static void quicksort(Object obj[], int elements, Comparator comparator)
    {
        int left=0, right=elements-1, top=0;
        int sSize = elements/2;
        int lStack[] = new int[sSize];
        int rStack[] = new int[sSize];
        Object tmp;
        int i, j, x;

        lStack[top] = left; rStack[top] = right;

        while (top >= 0)
        {
                left = lStack[top];
                right = rStack[top];
                top--;

                while (left < right)
                {
                        i = left;
                        j = right;
                        tmp = obj[(left+right)/2];

                        while (i <= j)
                        {
                        	while (comparator.compare(tmp,obj[i]) > 0 )i++;
                            while (comparator.compare(tmp,obj[j]) < 0) j--;

                                if (i<=j)
                                {
                                        { // SWAP
                                                tmp = obj[i];
                                                obj[i] = obj[j];
                                                obj[j] = tmp;
                                        }
                                        i++;
                                        j--;
                                }
                        }

                        if (j-left < right-i)
                        {
                                if (i < right)
                                {
                                        top++;
                                        lStack[top] = i;
                                        rStack[top] = right;
                                }
                                right = j;
                        }
                        else
                        {
                                if (left < j)
                                {
                                        top++;
                                        lStack[top] = left;
                                        rStack[top] = j;
                                }
                                left = i;
                        }
                }
        }
    }
    
    
    public static void sort(Object[] obj, Comparator comparator) {
    	int elements = obj.length;
    	int left=0, right=elements-1, top=0;
    	int sSize = elements/2;
    	int lStack[] = new int[sSize];
    	int rStack[] = new int[sSize];
    	Object tmp;
    	int i = 0, j = 0, x = 0;
    	lStack[top] = left; rStack[top] = right;
        while (top >= 0) {
            left = lStack[top];
            right = rStack[top];
            top--;
            do{
                i = left;
                j = right;
                x = (left+right)/2;
                while (i <= j) {    	
                    while (comparator.compare(obj[x],obj[i]) >= 0) {
                    	i++;
                    }
                    while (comparator.compare(obj[x],obj[j]) <= 0) {
                    	j--;
                    }
                    if (i<=j) {
                        // SWAP
                        tmp = obj[i];
                        obj[i] = obj[j];
                        obj[j] = tmp;         
                        i++;
                        j--;
                    }
                }

                if (j-left < right-i) {
                    if (i < right) {
                        top++;
                        lStack[top] = i;
                        rStack[top] = right;
                    }
                    right = j;
                } else {
                    if (left < j) {
                        top++;
                        lStack[top] = left;
                        rStack[top] = j;
                    }
                    left = i;
                }
            }while (left <= right) ;
        }
    }
}
