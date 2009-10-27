package de.enough.skylight.css;

import de.enough.skylight.dom.DomNode;


/**
 * These objects represent boxes in the CSS model. A box contains CSS properties and a reference to its containing block.
 * Normally a DomNode should implement this interface
 * @author rickyn
 *
 */
public interface Box {

	public String setProperty(String propertyName, String computedValue);
	public String getProperty(String propertyName);
	/**
	 * The box this box is contained in.
	 * @return
	 */
	public Box getContainingBlock();
	
	public DomNode getNode();
	public void addChild(Box child);
	public void prependChild(Box referenceChild, Box childToPrepend);
	public void appendChild(Box referenceChild, Box childToAppend);
}