/*
 * Created on Feb 24, 2005 at 12:22:24 PM.
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.plugin.eclipse.css.editor.outline;

import org.eclipse.jface.viewers.LabelProvider;

import de.enough.polish.plugin.eclipse.css.parser.CssLexerTokenTypes;
import de.enough.polish.plugin.eclipse.css.parser.OffsetAST;

import antlr.collections.AST;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Feb 24, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class CssLabelProvider extends LabelProvider{

	public String getText(Object object){
		if( ! (object instanceof AST)){
			return "NoLabel for "+object;
		}
		AST node = (AST)object;
		
		switch(node.getType()) {
			case(CssLexerTokenTypes.ATTRIBUTE_VALUE_PAIR):
			    AST attributeName = node.getFirstChild();
			    return ( attributeName != null?attributeName.toString()+((OffsetAST)attributeName).getOffset():null);
			case(CssLexerTokenTypes.SECTION):
			    return node.getFirstChild().toString()+((OffsetAST)node).getOffset();
			case(CssLexerTokenTypes.STYLE_SECTION):
			    return computeStyleSectionName(node);
		}
		// Fallback.
		return node.toString();
	}
	
	// Be strict and asume incomplete AST.
	private String computeStyleSectionName(AST styleSection) {
	    StringBuffer result = new StringBuffer();
	    AST child = styleSection.getFirstChild();
	    if(child == null) {
	        return "";
	    }
	    result.append(child.toString());
	    child = child.getNextSibling();
	    result.append(((OffsetAST)child).getOffset());
	    if(child != null && child.getType() == CssLexerTokenTypes.NAME){
	        result.append(" (");
	        result.append(child.toString());  
	        result.append(" )");
	    }
	    return result.toString();
	}
	
}
