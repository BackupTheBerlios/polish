/*
 * Created on Feb 28, 2005 at 2:32:29 PM.
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
package de.enough.polish.plugin.eclipse.css.model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.percederberg.grammatica.TreePrinter;
import net.percederberg.grammatica.parser.Node;
import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.ParserLogException;
import net.percederberg.grammatica.parser.Production;
import net.percederberg.grammatica.parser.ProductionPattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import de.enough.polish.plugin.eclipse.css.parser.PolishCssConstants;
import de.enough.polish.plugin.eclipse.css.parser.PolishCssParser;

/**
 * <p>Encapsulates the css model (domain model) and provides methods to deal with changes
 * to the model and to manipulate the model itself.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Feb 28, 2005 - ricky creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CssModel {

	private List modelListeners;
	
	private ASTNode rootASTNode;
	private Node rootParseNode;

	// TODO: Decide if the CssModel or Reconciler should listen to changes.
	// The CssModel could listen to the document, the Reconciler to its viewer.
	
	private IDocument document;
	private TreePrinter treePrinter;
	private PolishCssParser parser;
	private ASTBuilderAnalyzer astBuilder;
	
	public CssModel(){
		this.rootASTNode = new StyleSheet();
		this.modelListeners = new ArrayList();
		this.astBuilder = new ASTBuilderAnalyzer(this.rootASTNode);
		this.treePrinter = new TreePrinter(System.out);
	}
	
	public ASTNode getRoot(){
		
		return this.rootASTNode;
	}
	
	public void setRoot(ASTNode root){
		this.rootASTNode = root;
	}

	/**
	 * Reconcile the model to the changed document.
	 */
	public void reconcile() {
		
		if(this.document == null){
			System.out.println("DEBUG:CssModel.reconcile():document: is null.");
			return;
		}
		String inputString = this.document.get();
		// Parse.
		try {
			this.parser = new PolishCssParser(new StringReader(inputString));
			this.rootParseNode = null;
			this.rootParseNode = this.parser.parse();
			System.out.println("DEBUG:CssModel.reconcile():no parsing errors !!");
			this.treePrinter.analyze(this.rootParseNode);
			this.rootASTNode = new StyleSheet();
			this.astBuilder.setRoot(this.rootASTNode);
			this.astBuilder.analyze(this.rootParseNode,this.document);
		} catch (ParserCreationException exception) {
			System.out.println("DEBUG:CssModel.reconcile():parsing error:"+exception.getMessage());
		} catch (ParserLogException exception) {
			System.out.println("DEBUG:CssModel.reconcile():parsing error:"+exception.getMessage());
		}
		fireModelChangedEvent();
		
		
	}
	
	public int getLineOfASTNode(ASTNode node){
		int result = -1;
		try {
			result = this.document.getLineOfOffset(node.getOffset());
		} catch (BadLocationException exception) {
			System.out.println("DEBUG:CssContentProvider.selectionChanged():the offset is messed up.");
		}
		return result;
	}
	
	
	/**
	 * Is called when the model has changed because of a change in the document.
	 */
	private void fireModelChangedEvent() {
		Iterator iterator = this.modelListeners.iterator();
		IModelListener listener;
		while(iterator.hasNext()){
			listener = (IModelListener) iterator.next();
			listener.modelChanged();
		}
		
	}

	public void addModelListener(IModelListener listener){
		this.modelListeners.add(listener);
	}
	
	public void removeModelListener(IModelListener listener){
		this.modelListeners.remove(listener);
	}
	
	
	/**
	 * Guaranteed to return a Node != null.
	 * @return Returns the rootNode.
	 */
	public Node getRootNode() {
		if(this.rootParseNode == null){
			this.rootParseNode = new Production(new ProductionPattern(PolishCssConstants.STYLESHEET,"RootStyleSheet"));
		}
		return this.rootParseNode;
	}
	

	public void setRootNode(Node rootNode) {
		this.rootParseNode = rootNode;
	}
	
	
	public void setDocument(IDocument document){
		this.document = document;
	}
	
	
	public IDocument getDocument(){
		return this.document;
	}
}
