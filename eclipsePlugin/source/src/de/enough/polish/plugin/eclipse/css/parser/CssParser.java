/*
 * Created on Feb 23, 2005 at 11:04:08 AM.
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

import java.util.List;
import de.enough.polish.plugin.eclipse.css.model.ASTNode;
import de.enough.polish.plugin.eclipse.css.model.AttributeValuePair;
import de.enough.polish.plugin.eclipse.css.model.CssModel;
import de.enough.polish.plugin.eclipse.css.model.Problem;
import de.enough.polish.plugin.eclipse.css.model.Section;
import de.enough.polish.plugin.eclipse.css.model.StyleSheet;

//TODO: A possible recovery strategy would be to have a parserManager which is consulted when a
// problem occurs (not expected token). When it occurs, the production method ask the manager for a new
// offset after supplying the own class. Maybe the manager should be a visitor to handle elements differently.
// The visit method needs to return a new offset.


// FIXME: Recovery to be implemented: eat lines with errors. The parent will continue parsing after finding the next line offset
// beyond the errors. Try to extract a problem region.

// We have a strategy for problems: If there is a problem, try to go one parsing without building the
// AST any further. If there are no problems, build the AST. When a problem is encountered mark the node as
// dirty. Possible actions: Insert Token, drop token, panic synconize. What to do when?
// Situations:
// 0) token unknown: simply drop it.
// 1) token encountered which is not present in the entire production.
// 2) token would come after some other tokens: skip tokens, but not everything e.g. sync tokens.



// FIXME: Introduce commentStart and commentEnd regEx to the tokenizer.

// Use CssAttribute from polish to represent attributes. This class has a nice checkAttribute method.

// We are at parsing. If the production could be recognized, the parent inserts it into is child list. If
// somewhere in a production an error occurs the production files a problem on the production. The parent
// will not incorperate the production but trys to recover by syncronizing or simply skipping the trouble region.

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Feb 23, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class CssParser {
	
	private CssModel cssModel;
	private List tokenList;
	// FIXME: Convert to tokenList only. The cssModel is to high level here. Add a modelListener for
	// documentChanged and modelChanged. The parser will register to update own tokenList.
	// Caution: avoid caller loops with vistors changing the model. 
	
	private int offset;
	private CssToken currentToken; //CONTRACT: currentToken alsways != null.
	//private int start; // TODO:start and end for the region to parse. Adjusst getNextToken accordingly.
	//private int end;
	
	public CssParser(CssModel cssModel){
		this.cssModel = cssModel;
		this.tokenList = cssModel.getTokenList();
		this.currentToken = null;
		this.offset = 0;
	}
	
	// the parse methods need to remember the given offset and feed another one to there callees.
	// An iterator is not cloneable so how to give callees another iterator so they dont mess up the one
	// of the parent.
	public void parseAll(){
		this.offset = -1;
		ASTNode newRoot = parseStyleSheet();
		this.cssModel.setRoot(newRoot);
	}
	
	// TODO: Should the parse methods get a tokenlist or a ASTNode to operate on?
	// No bounce checking to speed up a bit. But someone sould take care of offset limits...
	private StyleSheet parseStyleSheet(){
		System.out.println("DEBUG:CssParser.parseStyleSheet():enter.offset:"+this.offset);
		StyleSheet result = new StyleSheet();
		Section section;
		// We need a "while" here to catch all productions.
		section = parseSection();
		// TODO: Replace null test with test about # of problems to decide which production to choose.
		onProblemSkipProblemLines(section);
		if(result != null){
			result.addSection(section);
		}
		System.out.println("DEBUG:CssParser.parseStyleSheet():leave.result:"+result);
		return result;
	}
	
	

	
	// Offset within tokenList.
	private Section parseSection(){
		System.out.println("DEBUG:CssParser.parseSection():enter.offset:"+this.offset);
		Section result = new Section();
		
		// Get the next token.
		getNextTokenIgnoreWhitespace();
		// TODO:Refactor this block into method. Caution: If a (identifier) value should be assigned consider
		// to change member variables into hasmap to access them dynamically.
		if( ! tokenIsTypeOf(CssTokenizer.TYPE_NAME)){
			// Issue a problem somehow.
			// Use the token offset, maybe we skipped whitespace.
			System.out.println("DEBUG:CssParser.parseSection():ERROR.Identifier expected.");
			result.addProblem(new Problem("identifier expected.",this.currentToken.getOffset(),this.currentToken.getLength()));
			return result;
		}
		result.setSectionName(this.currentToken.getValue());
		
		getNextTokenIgnoreWhitespace();
		if( ! tokenIsTypeOf(CssTokenizer.TYPE_LAB)){
			System.out.println("DEBUG:CssParser.parseSection():ERROR.Left angle bracket expected.");
			result.addProblem(new Problem("Left angle bracket expected.",this.currentToken.getOffset(),this.currentToken.getLength()));
			// Simply returning is wrong. Discard the production, add its problems to parent and try to go one.
			// E.g. try to syncronize with an error token.
			return result;
		}

		getNextTokenIgnoreWhitespace();
		while( ! tokenIsTypeOf(CssTokenizer.TYPE_RAB)){
			AttributeValuePair attributeValuePair = parseAttributeValuePair();
			if(attributeValuePair.hasProblems()){
				break;
			}
		}
		
		
		System.out.println("DEBUG:CssParser.parseSection().leave.result:"+result);
		return result;
	}
	
	/**
	 * @return something.
	 */
	private AttributeValuePair parseAttributeValuePair() {
		// TODO ricky implement parseAttributeValuePair
		return null;
	}

	//TODO: Csstoken should be subclass of IToken.
	// Refactor this method and extract getNextToken().
	private boolean getNextTokenIgnoreWhitespace(){
		int newOffset = this.offset+1;
		int lastOffset = this.tokenList.size()-1;
		
		if(newOffset > lastOffset){
			// When we reach the end, return the last token.
			this.offset = lastOffset;
			this.currentToken = (CssToken)this.tokenList.get(lastOffset);
			return false;
		}
		
		CssToken result;
		result = (CssToken)this.tokenList.get(newOffset);
		
		while(result.getType().equals(CssTokenizer.TYPE_WHITESPACE)){
			
			newOffset++;
			if(newOffset > lastOffset){
				
				// When we reach the end, return the last token.
				this.offset = lastOffset;
				this.currentToken = (CssToken)this.tokenList.get(lastOffset);
				return false;
			}
			result = (CssToken)this.tokenList.get(newOffset);
		}
		System.out.println("DEBUG:NextToken:"+result);
		this.offset = newOffset;
		this.currentToken = result;
		return true;
	}
	
	
	private boolean onProblemSkipProblemLines(ASTNode node){
		//if(node.hasProblems()){

		//}
		return false;
	}
	
	
	private boolean tokenIsTypeOf(String type){
		return this.currentToken.getType().equals(type);
	}
	
	
	public void parseIncremental(){
		System.out.println("CssParser.parseIncremental():Not implemented.");
		// Find dirty ASTNode and call parse on this node.
		// Then exchange new node with current one somehow.
	}
}
