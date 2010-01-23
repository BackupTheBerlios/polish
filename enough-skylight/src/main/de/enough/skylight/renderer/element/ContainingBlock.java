package de.enough.skylight.renderer.element;

import de.enough.polish.ui.Item;
import de.enough.skylight.renderer.node.CssElement;

public interface ContainingBlock {
	public void addToBody(Item item);
	
	public void addToLeftFloat(Item item);
	
	public void addToRightFloat(Item item);
	
	public CssElement getElement();
	
	public void setParentBlock(BlockContainingBlock block);
	
	public BlockContainingBlock getParentBlock();
}
