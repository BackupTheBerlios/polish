///*
// * Created on Mar 9, 2005 at 11:01:58 AM.
// * 
// * Copyright (c) 2005 Robert Virkus / Enough Software
// *
// * This file is part of J2ME Polish.
// *
// * J2ME Polish is free software; you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation; either version 2 of the License, or
// * (at your option) any later version.
// * 
// * J2ME Polish is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// * 
// * You should have received a copy of the GNU General Public License
// * along with Foobar; if not, write to the Free Software
// * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
// * 
// * Commercial licenses are also available, please
// * refer to the accompanying LICENSE.txt or visit
// * http://www.j2mepolish.org for details.
// */
//package de.enough.polish.plugin.eclipse.css.model;
//
//import org.eclipse.jface.text.BadLocationException;
//import org.eclipse.jface.text.IDocument;
//import net.percederberg.grammatica.parser.Node;
//import net.percederberg.grammatica.parser.Token;
//import de.enough.polish.plugin.eclipse.css.parser.PolishCssAnalyzer;
//import de.enough.polish.plugin.eclipse.css.parser.PolishCssConstants;
//
//
///**
// * <p></p>
// *
// * <p>Copyright Enough Software 2005</p>
// * <pre>
// * history
// *        Mar 9, 2005 - ricky creation
// * </pre>
// * @author Richard Nkrumah, Richard.Nkrumah@enough.de
// */
//public class ASTBuilderAnalyzer extends PolishCssAnalyzer {
//
//	ASTNode rootAST;
//	ASTNode lastNode;
//	IDocument document;
//	
//	/**
//	 * @param rootAST
//	 */
//	public ASTBuilderAnalyzer(ASTNode rootAST){
//		this.rootAST = rootAST;
//	}
//	
//	public void setRoot(ASTNode rootAST){
//		this.rootAST = rootAST;
//	}
//
//	
//	public Node analyze(Node node, IDocument newDocument){
//		if(newDocument == null){
//			System.out.println("DEBUG:ASTBuilder.analyze():document: parameter is null.");
//			return null;
//		}
//		this.document = newDocument;
//		if(node.getId() != PolishCssConstants.STYLESHEET){
//			System.out.println("ERROR:ASTBuilderAnalyzer.analyze().node:parameter is null.");
//			return null;
//		}
//		// We know that all children are StyleSections, so process them.
//		for(int i = 0; i < node.getChildCount(); i++){
//			buildStyleSection(node.getChildAt(i),this.rootAST);
//		}
//		return null;
//	}
//	
//	
//	private void buildStyleSection(Node node, ASTNode parent){
//		StyleSection styleSection = new StyleSection();
//		styleSection.setParent(parent);
//		parent.addChild(styleSection);
//		
//		int nextChildIndex = 0;
//		int childCount = node.getChildCount();
//		Node child;
//		// No typechecking is needed because of the fixed structure. When we have a incremental parser
//		// this situation may change and an ASTNode can be inconsistent.
//		String sectionName = ((Token)node.getChildAt(0).getChildAt(0)).getImage();
//		styleSection.setSectionName(sectionName);
//		
//		styleSection.setOffset(computePostionLength((Token)node.getChildAt(0).getChildAt(0)).position);
//		nextChildIndex++;
//		
//		child= node.getChildAt(nextChildIndex);
//		if(child.getId() == PolishCssConstants.EXTENDS){
//			System.out.println("Y:"+child);
//			// We have a parent style.
//			nextChildIndex++;
//			String parentName = ((Token)node.getChildAt(nextChildIndex).getChildAt(0)).getImage();
//			styleSection.setParentStyle(parentName);
//			nextChildIndex++;
//			
//		}
//		nextChildIndex++; //Eat the left angle bracket.
//		
//		while(nextChildIndex < childCount-1){ // every entering means there is a another name value pair left. Minus one because of the last bracket.
//			String name = ((Token)node.getChildAt(nextChildIndex).getChildAt(0)).getImage();
//			nextChildIndex++;
//			child = node.getChildAt(nextChildIndex); // A sectionBody or a AttributeBody.
//			if(child.getId() == PolishCssConstants.SECTION_BODY){
//				buildSection(child,styleSection,name);
//			}
//			else if(child.getId() == PolishCssConstants.ATTRIBUTE_VALUE_BODY){
//				buildAttributeValuePair(child,styleSection,name);
//			}
//			else{
//				System.out.println("ERROR:ASTBuilderAnalyzer.buildStyleSection():Child has wrong type.type:"+child.getId());
//			}
//			nextChildIndex++;
//		}
//		
//	}
//
//	/**
//	 * @param attributeValueBody
//	 * @param parent
//	 * @param name
//	 */
//	private void buildAttributeValuePair(Node attributeValueBody, Section parent, String name) {
//		
//		if(attributeValueBody.getId() != PolishCssConstants.ATTRIBUTE_VALUE_BODY){
//			System.out.println("Error:ASTBuilderAnalyzer.buildAttributeValuePair():attributeValueBody: has wrong type.type:"+attributeValueBody.getId());
//			return;
//		}
//		
//		AttributeValuePair attributeValuePair = new AttributeValuePair();
//		attributeValuePair.setParent(parent);
//		parent.addChild(attributeValuePair);
//		
//		String value = ((Token)attributeValueBody.getChildAt(0)).getImage();
//		int valueLength = value.length();
//		if( valueLength >= 2){ // Workaround for the ugly ATTRIBUTEVALUEPAIR-Token which as : and ; in it.
//			value = value.substring(1,valueLength-1);
//		}
//		attributeValuePair.setAttribute(name);
//		attributeValuePair.setValue(value);
//	}
//
//
//
//
//	/**
//	 * @param sectionBody
//	 * @param parent
//	 * @param sectionName
//	 */
//	private void buildSection(Node sectionBody, Section parent, String sectionName) {
//		System.out.println();
//		Section section = new Section();
//		section.setParent(parent);
//		parent.addChild(section);
//		
//		section.setSectionName(sectionName);
//
//		int nextChildIndex = 0;
//		int childCount = sectionBody.getChildCount();
//		Node child;
//		
//		nextChildIndex++; // Eat left bracket.
//		
//		while(nextChildIndex < childCount-1){
//			System.out.println("buildSection.name?"+sectionBody.getChildAt(nextChildIndex));
//			String attributeName = ((Token)sectionBody.getChildAt(nextChildIndex).getChildAt(0)).getImage();
//			nextChildIndex++;
//			System.out.println("buildSection.attri?"+sectionBody.getChildAt(nextChildIndex));
//			child = sectionBody.getChildAt(nextChildIndex); // AttributeValueBody.
//			nextChildIndex++;
//			buildAttributeValuePair(child,section,attributeName);
//		}
//
//		
//		
//		
//	}
//	
//	public PositionLengthPair computePostionLength(Token token){
//		
//		int position = -1;
//		int length = -1;
//		try {
//			position = this.document.getLineOffset(token.getStartLine()-1) + token.getStartColumn()-1;
//			length = (this.document.getLineOffset(token.getEndLine()-1) + token.getEndColumn()) - position;
//			this.document.get(position,length); //Test if the position and length is working.
//		}
//		catch (BadLocationException exception) {
//			System.out.println("ERROR:ASTBuilderAlanyzer.computePositionLength():position and length beyond document.position:"+position+".length:"+length);
//			return null;
//		}
//		return new PositionLengthPair(position,length);
//	}
//	
//	class PositionLengthPair{
//		public int position;
//		public int length;
//		public PositionLengthPair(int position, int length){
//			this.position = position;
//			this.length = length;
//		}
//	}
//}
