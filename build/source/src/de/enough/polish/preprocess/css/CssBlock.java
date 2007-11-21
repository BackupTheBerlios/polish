/*
 * Created on 02-Mar-2004 at 15:39:55.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.polish.preprocess.css;

import de.enough.polish.util.StringUtil;

import de.enough.polish.BuildException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Interpretes a single CSS block.</p>
 * <p>A block can consist of a single style or of the 
 *   colors, borders, backgrounds or fonts-definition.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        02-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class CssBlock {
	
	protected static final String ATTRIBUTE_NAME_STR = "[\\w|\\.|_|-]+";
	protected static final String ATTRIBUTE_SELECTOR_STR = ATTRIBUTE_NAME_STR + "(\\s+extends\\s+" + ATTRIBUTE_NAME_STR + ")?";
	protected static final String ATTRIBUTE_VALUE_STR = "[\\w|\\.|_|\\-|\\(|\\)|\\s|#|%|\\|]+";
	//protected static final String ATTRIBUTE_VALUE_STR = ".+;?";
	private static final String INNER_BLOCK_PATTERN_STR = ATTRIBUTE_SELECTOR_STR + "\\s*\\{\\s*(\\s*" + CssBlock.ATTRIBUTE_NAME_STR + "\\s*\\:\\s*" + CssBlock.ATTRIBUTE_VALUE_STR + "\\s*\\;?\\s*)+\\s*\\}";
	//private static final String INNER_BLOCK_PATTERN_STR = ATTRIBUTE_SELECTOR_STR + "\\s*\\{\\s*(\\s*" + CssBlock.ATTRIBUTE_NAME_STR + "\\s*\\:\\s*" + CssBlock.ATTRIBUTE_VALUE_STR + "\\s*)+\\s*\\}";
	protected static final Pattern INNER_BLOCK_PATTERN = Pattern.compile(INNER_BLOCK_PATTERN_STR);
	private String selector;
	private HashMap declarationsByName;
	private HashMap groupsByNames;
	private ArrayList groups;
	
	/**
	 * Creates a new CSS Block
	 * 
	 * @param cssCode the CSS code of this block.
	 * @throws BuildException when the code contains errors.
	 */
	public CssBlock( String cssCode ) {
		//System.out.println("CssBlock for block \n\"" + cssCode + "\"...");
		this.declarationsByName = new HashMap();
		this.groupsByNames = new HashMap();
		this.groups = new ArrayList();
		int parenthesisPos = cssCode.indexOf('{');
		this.selector = cssCode.substring(0, parenthesisPos ).trim();
		cssCode = cssCode.substring( parenthesisPos + 1, cssCode.length() -1 ).trim();
		// now read any inner blocks:
//		Matcher matcher = INNER_BLOCK_PATTERN.matcher(cssCode);
//		while (matcher.find()) {
//			String innerBlock = matcher.group();
//			System.out.println("INNER BLOCK FOUND: " + innerBlock);
//			String prepend = cssCode.substring(0, cssCode.indexOf(innerBlock));
//			int endIndex = prepend.lastIndexOf('{');
//			String blockName = null;
//			if (endIndex != -1) {
//				prepend = prepend.substring(0, endIndex - 1).trim();
//				int startIndex = prepend.lastIndexOf(' ');
//				if (startIndex < 0) {
//					startIndex = 0;
//				}
//				blockName = prepend.substring(startIndex);
//			}
//			
//			parseInnerBlock( blockName, innerBlock );
//			cssCode = StringUtil.replace( cssCode, innerBlock, "" );
//			matcher = INNER_BLOCK_PATTERN.matcher(cssCode);
//		}
		while ( (parenthesisPos = cssCode.indexOf('{')) != -1) {
			int start = cssCode.lastIndexOf(';', parenthesisPos);
			if (start == -1) {
				start = 0;
			} else {
				start++;
			}
			int end = cssCode.indexOf('}', parenthesisPos );
			if (end == -1) {
				throw new BuildException("Invalid CSS code: unable to parse [" + cssCode + "] - at least one closing parenthesis '}' is missing.");
			}
			String innerBlock = cssCode.substring( start, end + 1);
			parseInnerBlock( innerBlock );
			cssCode = StringUtil.replace( cssCode, innerBlock, "" );
		}
		// now read the rest of the definitions:
		String[] declarations = StringUtil.splitAndTrim(cssCode, ';');
		for (int i = 0; i < declarations.length; i++) {
			String declaration = declarations[i];
			if (declaration.length() > 0) {
				try {
					addDeclaration( declaration );
				} catch (BuildException e) {
					System.err.println("Invalid CSS code in black \"" + cssCode + "\":");
					throw e;
				}
			}
		}
	}

	/**
	 * Adds a declaration to this block.
	 * 
	 * @param declaration the declaration in the form "attribute: value"
	 */
	private void addDeclaration(String declaration) {
		int colonPos = declaration.indexOf(':');
		if (colonPos == -1) {
			throw new BuildException("Invalid CSS code: unable to parse declaration \"" + declaration + ";\" - found no colon between attribute and value.");
		}
		String attribute = declaration.substring(0, colonPos).trim();
		String value = declaration.substring(colonPos + 1).trim();
		String groupName = attribute;
		String subAttribute = attribute;
		int hyphenPos = attribute.indexOf('-');
		if (hyphenPos != - 1) {
			groupName = attribute.substring(0, hyphenPos);
			subAttribute = attribute.substring(hyphenPos + 1);
		}
		this.declarationsByName.put(attribute, value);
		HashMap group = (HashMap) this.groupsByNames.get( groupName );
		if (group == null) {
			group = new HashMap();
			this.groupsByNames.put( groupName, group );
			this.groups.add( groupName );
		}
		group.put( subAttribute, value );
	}

	/**
	 * @param code the CSS code
	 */
	private void parseInnerBlock(String code) {
		int parenthesisPos = code.indexOf('{');
		String blockName = code.substring(0, parenthesisPos ).trim() + '-';
		String declarationBlock = code.substring( parenthesisPos + 1, code.length() -1 ).trim();
		String[] declarations = StringUtil.splitAndTrim( declarationBlock, ';' );
		try {
			for (int i = 0; i < declarations.length; i++) {
				String declaration = declarations[i];
				if (declaration.length() > 0) {
					addDeclaration( blockName + declaration );
				}
			}
		} catch (BuildException e ) {
			throw new BuildException( "Invalid CSS code: inner block [" + code + "] contains an invalid declaration: " + e.getMessage(), e );
		}
	}
	


	/**
	 * @param code the CSS code
	 */
	private void parseInnerBlock(String blockName, String code) {
		int parenthesisPos = code.indexOf('{');
		String innerBlockName = code.substring(0, parenthesisPos ).trim() + '-';
		if (blockName == null) {
			blockName = innerBlockName;
		} else {
			blockName = blockName + "-" + innerBlockName;
		}
		String declarationBlock = code.substring( parenthesisPos + 1, code.length() -1 ).trim();
		String[] declarations = StringUtil.splitAndTrim( declarationBlock, ';' );
		try {
			for (int i = 0; i < declarations.length; i++) {
				String declaration = declarations[i];
				if (declaration.length() > 0) {
					addDeclaration( blockName + declaration );
				}
			}
		} catch (BuildException e ) {
			throw new BuildException( "Invalid CSS code: inner block [" + code + "] contains an invalid declaration: " + e.getMessage(), e );
		}
	}
	
	/**
	 * @return Returns the name of the selector of this block.
	 */
	public String getSelector() {
		return this.selector;
	}
	
	/**
	 * @return Returns all declarations stored in a HashMap
	 */
	public HashMap getDeclarationsMap() {
		return this.declarationsByName;
	}
	
	/**
	 * Retrieves the names of all found groups like "font" or "background"
	 * 
	 * @return String array with the names of all found groups
	 */
	public String[] getGroupNames() {
		return (String[]) this.groups.toArray( new String[ this.groups.size() ]);
	}
	
	/**
	 * Retrieves the declarations for the specific group.
	 * 
	 * @param group the name of the group.
	 * @return all declaration of the specified group in a HashMap
	 */
	public HashMap getGroupDeclarations( String group ) {
		return (HashMap) this.groupsByNames.get( group );
	}

	public void setSelector(String styleName) {
		this.selector = styleName;
	}
	

}
