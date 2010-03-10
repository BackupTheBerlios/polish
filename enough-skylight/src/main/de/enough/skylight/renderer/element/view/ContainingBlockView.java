package de.enough.skylight.renderer.element.view;

import de.enough.polish.ui.Canvas;
import de.enough.polish.ui.ContainerView;
import de.enough.skylight.event.UserEvent;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.node.CssElement;

public class ContainingBlockView extends ContainerView {
	
	public boolean handleKeyReleased(int keyCode, int gameAction) {
		boolean handled = super.handleKeyReleased(keyCode, gameAction);
		
		if(gameAction == Canvas.FIRE) {
			CssElement element = LayoutAttributes.getCssElement(this.parentItem);
			if(element != null && element.isInteractive()) {
				Viewport viewport = element.getViewport();
				UserEvent event = new UserEvent();
				viewport.notifyUserEvent(element, event);
			}
		}
		
		return handled;
	}
}
