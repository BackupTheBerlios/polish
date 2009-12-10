package de.enough.skylight.renderer.view.element;

import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;

public class BlockLevelView extends ContainerView{

	protected void initContent(Item parentContainerItem, int firstLineWidth,
			int availWidth, int availHeight) {
		super.initContent(parentContainerItem, firstLineWidth, availWidth, availHeight);
		
		// expand to available width
		this.contentWidth = availWidth;
	}
	
}
