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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.ui.internal.misc.Assert;
import de.enough.polish.plugin.eclipse.css.parser.CssToken;
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
	
	private ASTNode rootAST;
	private Node rootNode;
	private List tokenList;

	private IDocument document;
	private TreePrinter treePrinter;
	private PolishCssParser parser;
	private ASTBuilderAnalyzer astBuilder;
	
	public CssModel(){
		this.rootAST = new StyleSheet();
		this.tokenList = new ArrayList();
		this.modelListeners = new ArrayList();
		this.astBuilder = new ASTBuilderAnalyzer(this.rootAST);
		this.treePrinter = new TreePrinter(System.out);
	}
	
	public ASTNode getRoot(){
		
		return this.rootAST;
	}
	
	public void setRoot(ASTNode root){
		this.rootAST = root;
	}
	/**
	 * @return Returns the tokenList.
	 */
	public List getTokenList() {
		return this.tokenList;
	}
	
	/**
	 * @param tokenList The tokenList to set.
	 */
	public void setTokenList(List tokenList) {
		this.tokenList = tokenList;
	}
	
	public void addToken(CssToken cssToken){
		this.tokenList.add(cssToken);
	}
	

	/**
	 * Reconcile the model to the changed document.
	 * @param textEvent TODO
	 */
	public void reconcile(TextEvent textEvent) {
		
		if(this.document == null){
			System.out.println("DEBUG:CssModel.reconcile():document: is null.");
			return;
		}
		String inputString = this.document.get();
		// Parse.
		try {
			this.parser = new PolishCssParser(new StringReader(inputString));
			this.rootNode = null;
			this.rootNode = this.parser.parse();
			System.out.println("DEBUG:CssModel.reconcile():no parsing errors !!");
			this.treePrinter.analyze(this.rootNode);
			this.rootAST = new StyleSheet();
			this.astBuilder.setRoot(this.rootAST);
			this.astBuilder.analyze(this.rootNode,this.document);
		} catch (ParserCreationException exception) {
			System.out.println("DEBUG:CssModel.reconcile():parsing error:"+exception.getMessage());
		} catch (ParserLogException exception) {
			System.out.println("DEBUG:CssModel.reconcile():parsing error:"+exception.getMessage());
		}
		fireModelChangedEvent();
		
		
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
		Assert.isNotNull(listener);
		this.modelListeners.add(listener);
	}
	
	public void removeModelListener(IModelListener listener){
		Assert.isNotNull(listener);
		this.modelListeners.remove(listener);
	}
	
	
	/**
	 * Guaranteed to return a Node != null.
	 * @return Returns the rootNode.
	 */
	public Node getRootNode() {
		if(this.rootNode == null){
			this.rootNode = new Production(new ProductionPattern(PolishCssConstants.STYLESHEET,"RootStyleSheet"));
		}
		return this.rootNode;
	}
	/**
	 * @param rootNode The rootNode to set.
	 */
	public void setRootNode(Node rootNode) {
		this.rootNode = rootNode;
	}
	
	public void setDocument(IDocument document){
		this.document = document;
	}
	
	public IDocument getDocument(){
		/*if((this.documentProvider == null) || (this.editorInput == null)){
			return null;
		}
		IDocument document = this.documentProvider.getDocument(this.editorInput);
		return document;	
		*/
		return this.document;
	}
}
