package de.enough.skylight.renderer.element;

import de.enough.polish.ui.Item;
import de.enough.skylight.renderer.builder.Element;

public interface ContainingBlock {
	public void addToBody(Item item);
	
	public void addToLeftFloat(Item item);
	
	public void addToRightFloat(Item item);
	
	public Element getElement();
}
