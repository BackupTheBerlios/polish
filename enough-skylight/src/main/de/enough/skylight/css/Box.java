package de.enough.skylight.css;

import de.enough.skylight.dom.DomNode;


/**
 * Objects represent boxes in the CSS model. A box contains CSS properties and a reference to its containing block.
 * Normally a DomNode should implement this interface
 * @author rickyn
 *
 */
public interface Box {

	public String setProperty(String propertyName, String computedValue);
	public String getProperty(String propertyName);
	public Box getContainingBlock();
	public DomNode getNode();
}