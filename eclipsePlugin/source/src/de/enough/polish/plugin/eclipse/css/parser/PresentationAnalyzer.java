/*
 * Created on Mar 8, 2005 at 3:59:46 PM.
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
package de.enough.polish.plugin.eclipse.css.parser;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.RGB;

import net.percederberg.grammatica.parser.Node;
import net.percederberg.grammatica.parser.ParseException;
import net.percederberg.grammatica.parser.Production;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Mar 8, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class PresentationAnalyzer extends PolishCssAnalyzer {

	private TextPresentation textPresentation;
	private IDocument document;
	private ISharedTextColors colors;
	
	public PresentationAnalyzer(TextPresentation textPresentation, IDocument document, ISharedTextColors colors){
		this.textPresentation = textPresentation;
		this.document = document;
		this.colors = colors;
	}
	
	
	
	
	
	
	
	protected Node exitStyleSection(Production node) throws ParseException {
		Node childNode = node.getChildAt(0); // The first one is NAME.
		childNode = node.getChildAt(0); // The first one is NAME.
		
		int position = -1;
		int length = -1;
		try {
			position = this.document.getLineOffset(childNode.getStartLine()-1) + childNode.getStartColumn()-1;
			length = (this.document.getLineOffset(childNode.getEndLine()-1) + childNode.getEndColumn()) - position;
			this.document.get(position,length); //Test if the position and length is working.
			StyleRange styleRange = new StyleRange(position,length,this.colors.getColor(new RGB(200,0,0)),null);
			this.textPresentation.addStyleRange(styleRange);
		} catch (BadLocationException exception) {
			System.out.println("ERROR:SimpleReconcilerFacade.createStyleRange():position and length beyond document.position:"+position+".length:"+length);
		}
		System.out.println("position:length for child:"+position+". length"+length);
		
		return super.exitStyleSection(node);
	}
	
	/*
	protected Node exitName(Production node) throws ParseException {
		System.out.println("exitName");
		printInfos(node);
		return super.exitName(node);
	}
*/


	/**
	 * @param node
	 */
	/*
	private void printInfos(Production node) {
		if(node.getId() == PolishCssConstants.NAME){
			//TODO: We could change the token class to transport the position in addition to
			// line and offset.
			
			Node childNode;
			Token childToken;
			for(int i = 0; i < node.getChildCount(); i++){
				childNode = node.getChildAt(i);
				if(childNode instanceof Token){
					childToken = (Token)childNode;
					System.out.println("childToken.getImage():"+childToken.getImage());
				}
				else{
					System.out.println("childNode.getName():"+childNode.getName());
				}
			}
			
			int position = -1;
			int length = -1;
			try {
				position = this.document.getLineOffset(node.getStartLine()-1) + node.getStartColumn()-1;
				length = (this.document.getLineOffset(node.getEndLine()-1) + node.getEndColumn()) - position;
				this.document.get(position,length); //Test if the position and length is working.
				StyleRange styleRange = new StyleRange(position,length,this.colors.getColor(new RGB(0,200,0)),null);
				this.textPresentation.addStyleRange(styleRange);
			} catch (BadLocationException exception) {
				System.out.println("ERROR:SimpleReconcilerFacade.createStyleRange():position and length beyond document.position:"+position+".length:"+length);
			}
			System.out.println("position:length for child:"+position+". length"+length);
			
		}
		
	}
	*/
}
