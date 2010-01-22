package de.enough.skylight.renderer.element;

import de.enough.polish.ui.Item;
import de.enough.skylight.renderer.node.NodeElement;

public interface ContainingBlock {
	public void addToBody(Item item);
	
	public void addToLeftFloat(Item item);
	
	public void addToRightFloat(Item item);
	
	public NodeElement getElement();
}
