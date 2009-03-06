/*
 * Created on Mar 29, 2006 at 3:48:02 PM.
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

/**
 * Source:http://www.merriampark.com/ld.htm
 * 
 * The Algorithm
 * 1   Set n to be the length of s.
 *     Set m to be the length of t.
 *     If n = 0, return m and exit.
 *     If m = 0, return n and exit.
 *     Construct a matrix containing 0..m rows and 0..n columns.   
 * 2   Initialize the first row to 0..n.
 *     Initialize the first column to 0..m.
 * 3   Examine each character of s (i from 1 to n).    
 * 4   Examine each character of t (j from 1 to m).    
 * 5   If s[i] equals t[j], the cost is 0.
 *     If s[i] doesn't equal t[j], the cost is 1.  
 * 6   Set cell d[i,j] of the matrix equal to the minimum of:
 *     a. The cell immediately above plus 1: d[i-1,j] + 1.
 *     b. The cell immediately to the left plus 1: d[i,j-1] + 1.
 *     c. The cell diagonally above and to the left plus the cost: d[i-1,j-1] + cost.
 * 7   After the iteration steps (3, 4, 5, 6) are complete, the distance is found in cell d[n,m].
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Mar 29, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class LevenshteinDistance {

    //****************************
    // Get minimum of three values
    //****************************

    private static int minimum (int a, int b, int c) {
    int mi;

      mi = a;
      if (b < mi) {
        mi = b;
      }
      if (c < mi) {
        mi = c;
      }
      return mi;

    }

    //*****************************
    // Compute Levenshtein distance
    //*****************************

    public static int distance (String s, String t) {
    int d[][]; // matrix
    int n; // length of s
    int m; // length of t
    int i; // iterates through s
    int j; // iterates through t
    char s_i; // ith character of s
    char t_j; // jth character of t
    int cost; // cost

      // Step 1

      n = s.length ();
      m = t.length ();
      if (n == 0) {
        return m;
      }
      if (m == 0) {
        return n;
      }
      d = new int[n+1][m+1];

      // Step 2

      for (i = 0; i <= n; i++) {
        d[i][0] = i;
      }

      for (j = 0; j <= m; j++) {
        d[0][j] = j;
      }

      // Step 3

      for (i = 1; i <= n; i++) {

        s_i = s.charAt (i - 1);

        // Step 4

        for (j = 1; j <= m; j++) {

          t_j = t.charAt (j - 1);

          // Step 5

          if (s_i == t_j) {
            cost = 0;
          }
          else {
            cost = 1;
          }

          // Step 6

          d[i][j] = minimum (d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1] + cost);

        }

      }

      // Step 7

      return d[n][m];

    }

  }