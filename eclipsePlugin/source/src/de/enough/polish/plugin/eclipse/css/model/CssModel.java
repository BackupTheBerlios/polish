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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import antlr.RecognitionException;
import antlr.collections.AST;
import de.enough.polish.plugin.eclipse.css.CssEditorPlugin;
import de.enough.polish.plugin.eclipse.css.parser.CssLexer;
import de.enough.polish.plugin.eclipse.css.parser.CssParser;
import de.enough.polish.plugin.eclipse.css.parser.OffsetWalker;

/**
 * <p>Encapsulates the css model (domain model) and provides methods to deal with changes
 * to the model and to manipulate the model itself.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Feb 28, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class CssModel {

	private List modelListeners;

	// TODO: Decide if the CssModel or the Reconciler should listen to changes.
	// The CssModel could listen to the document, the Reconciler to its viewer.
	
	private IDocument document;

    private CssLexer lexer;
    private CssParser parser;
    private AST astRoot;
    CssEditorPlugin plugin = CssEditorPlugin.getDefault();
    
	
	public CssModel(){
		this.modelListeners = new ArrayList();
	}
	
	/**
	 * Reconcile the model to the changed document.
	 */
	public void reconcile() {
		
		if(this.document == null){
			return;
		}
		String inputString = this.document.get();
		StringReader stringReader = new StringReader(inputString);
		//DataInputStream dataInputStream = new DataInputStream();
		
		this.lexer = new CssLexer(stringReader);
        this.lexer.setTokenObjectClass("de.enough.polish.plugin.eclipse.css.parser.OffsetToken");

        this.parser = new CssParser(this.lexer);
        this.parser.setASTNodeClass("de.enough.polish.plugin.eclipse.css.parser.OffsetAST");
		
		try {
            this.parser.styleSheet();
        }
		catch(Exception exception) {
		    CssEditorPlugin.log("INTERNAL:DEBUG:CssModel.reconcile():Parse error.",exception);
		}
        this.astRoot = this.parser.getAST();

        if(this.astRoot == null) {
            System.out.println("CssModel.reconcile():No AST generated.");
            return;
        }
        
        OffsetWalker offsetWalker = new OffsetWalker();
        try {
            offsetWalker.styleSheet(this.astRoot);
            System.out.println("Walker finished.");
        } catch (RecognitionException exception) {
            CssEditorPlugin.log("INTERNAL:DEBUG:CssModel.reconcile():Parse Error in Tree walker.",exception);
        }
        
        /*DumpASTVisitor dumpASTVisitor = new DumpASTVisitor();
        dumpASTVisitor.visit(this.astRoot);
        */
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
		this.modelListeners.add(listener);
	}
	
	public void removeModelListener(IModelListener listener){
		this.modelListeners.remove(listener);
	}
	
	

	public void setDocument(IDocument document){
		this.document = document;
	}
	
	
	public IDocument getDocument(){
		return this.document;
	}
	
	
	public AST getAstRoot() {
	    return this.astRoot;
    }

    public void setAstRoot(AST astRoot) {
        this.astRoot = astRoot;
    }

    /**
     * @param firstSelectedNode
     * @return the offset within the document from the given AST. -1 if an error occured.
     */
    public int getOffsetFromAST(AST firstSelectedNode) {
        if(this.document == null) {
            return -1;
        }
        int result = -1;
        // We have a nonterminal node, get the line from the first terminal node.
        if(firstSelectedNode.getLine() == 0) {
            AST child = firstSelectedNode.getFirstChild();
            if(child == null) { // We have a nonterminal as leaf, that should not happen.
                
                return -1;
            }
        }
        try {
            int lineOffset = this.document.getLineOffset(firstSelectedNode.getLine());
            result = lineOffset + firstSelectedNode.getColumn();
            this.document.get(result,1); // Check if computed offset is valid by trying to retrieve it from the document.
        } catch (BadLocationException exception) {
            CssEditorPlugin.log("INTERNAL:ERROR:CssModel.getOffsetFromAST():Wrong line parameter to document.getLineOffset():"+firstSelectedNode.getLine(),exception);
            return -1;
        }
        return result;
    }
}
