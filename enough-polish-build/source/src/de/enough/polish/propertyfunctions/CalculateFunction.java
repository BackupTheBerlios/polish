/*
 * Created on 25-Apr-2005 at 15:09:59.
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
package de.enough.polish.propertyfunctions;

import de.enough.polish.BuildException;
import de.enough.polish.Environment;

/**
 * <p>Calculates the term embedded into the code.</p>
 * <p>The sub-values are separated by commas, e.g. "nokia-ui, mmapi, wmapi".</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        25-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CalculateFunction extends PropertyFunction {
	
	private static final char PLUS = '+';
	private static final char MINUS = '-';
	private static final char MULTIPLY = '*';
	private static final char DEVIDE = ':';

	/**
	 * Creates a new number function
	 */
	public CalculateFunction() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.propertyfunctions.PropertyFunction#process(java.lang.String, java.lang.String[], de.enough.polish.Environment)
	 */
	public String process(String input, String[] arguments, Environment env) {
		try {
		int result = 0;
		char lastOperator = PLUS;
		StringBuffer lastNumber = new StringBuffer();
		char[] chars = input.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (Character.isDigit(c) || ( c == MINUS && (lastNumber.length() == 0)) ) {
				lastNumber.append(c);
			} else {
				if (lastNumber.length() == 0) {
					throw new BuildException("No digit in term \"" + input + "\" at index " + i + ": " + c );
				}
				int part = Integer.parseInt( lastNumber.toString() );
				switch (lastOperator) {
				case PLUS:
					result += part;
					break;
				case MINUS:
					result -= part;
					break;
				case MULTIPLY:
					result *= part;
					break;
				case DEVIDE:
					result /= part;
					break;
				}
				// clear lastNumber buffer:
				lastNumber.delete(0, lastNumber.length() );
				// get next operator:
				boolean foundNextOperator = false;
				for (int j = i; j < chars.length; j++) {
					char d = chars[j];
					if (d == ' ' || d == '\t') {
						// ignore spaces
					} else {
						if (foundNextOperator) {
							break;
						} else {
							switch (d) {
							case PLUS: lastOperator = PLUS; break;
							case MINUS: lastOperator = MINUS; break;
							case MULTIPLY: lastOperator = MULTIPLY; break;
							case DEVIDE: lastOperator = DEVIDE; break;
							default: throw new BuildException("Unknown operator in term \"" + input + "\" at index " + j + ": " + d );
							}
							foundNextOperator = true;
						}
					}
					i = j;
				}
			}
		} // for each char
		if (lastNumber.length() != 0) {
			int part = Integer.parseInt( lastNumber.toString() );
			switch (lastOperator) {
			case PLUS:
				result += part;
				break;
			case MINUS:
				result -= part;
				break;
			case MULTIPLY:
				result *= part;
				break;
			case DEVIDE:
				result /= part;
				break;
			}
		}
		return Integer.toString( result );
		} catch (Exception e) {
			throw new BuildException("Unable to calculate input from term \"" + input + "\": " + e.toString() );
		}
	}

}
