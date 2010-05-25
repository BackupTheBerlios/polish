package de.enough.ovidiu;

import de.enough.polish.ui.Style;

import de.enough.polish.util.ArrayList;

public interface NodeInterface {		
	
	public Style getStyle();
	
	public int getContentType();
	
	public Object getContent();
	
	public NodeInterface getParent();
	
	public ArrayList getChildren();	

}
