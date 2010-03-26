package de.enough.skylight.renderer.element.view;

import de.enough.polish.ui.Canvas;
import de.enough.polish.ui.ContainerView;
import de.enough.skylight.event.UserEvent;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.layout.LayoutAttributes;
import de.enough.skylight.renderer.node.CssElement;

public class ContainingBlockView extends ContainerView {
	
	ContainingBlock containingBlock;
	
	public ContainingBlockView(ContainingBlock containingBlock) {
		this.parentContainer = containingBlock;
		this.parentItem = containingBlock;
		this.containingBlock = containingBlock;
	}
	
	public boolean handleKeyReleased(int keyCode, int gameAction) {
		boolean handled = super.handleKeyReleased(keyCode, gameAction);
		
		if(gameAction == Canvas.FIRE) {
			handleInteraction();
		}
		
		return handled;
	}

	public boolean handlePointerReleased(int x, int y) {
		boolean handled = super.handlePointerReleased(x, y);
		handleInteraction();
		return handled;
	}
	
	public void handleInteraction() {
		CssElement element = LayoutAttributes.get(this.parentItem).getElement();
		if(element != null && element.isInteractive()) {
			Viewport viewport = element.getViewport();
			UserEvent event = new UserEvent();
			viewport.notifyUserEvent(element, event);
		}
	}
}
