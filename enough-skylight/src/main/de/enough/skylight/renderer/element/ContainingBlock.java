package de.enough.skylight.renderer.element;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.skylight.renderer.partition.Partable;

public interface ContainingBlock extends Partable {
	public void addToBody(Item item);
	
	public void addToLeftFloat(Item item);
	
	public void addToRightFloat(Item item);
	
	public Container getContainer();
}
