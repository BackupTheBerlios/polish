/*
 * Created on 22-Feb-2005
 */
package de.enough.polish.plugin.eclipse.css.editor;

import org.eclipse.ui.editors.text.TextEditor;


/**
 * @author rickyn
 */
public class CssEditor extends TextEditor{

	public CssEditor(){
		setSourceViewerConfiguration(new CssSourceViewerConfiguration(this,getSharedColors()));
	}
	
	//TODO: Syntax highlight the input file.
	//TODO: Partition the input file.
	//TODO: Parse the input file into domain model.
	
	
	
	
}
