package de.enough.skylight.renderer.element;

import de.enough.polish.ui.Canvas;
import de.enough.polish.ui.StringItem;
import de.enough.skylight.event.UserEvent;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.partition.Partable;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class InputSubmitField extends StringItem implements Partable {
	
	public InputSubmitField() {
		super(null,"");
	}
	
	public String toString() {
		return "InputSubmitField [" + getText() + "]"; 
	}

	public void partition(PartitionList partitions) {
		Partition partition = Partition.partitionBlock(this);
		partitions.add(partition);
	}
	
	public boolean handleKeyReleased(int keyCode, int gameAction) {
		boolean handled = super.handleKeyReleased(keyCode, gameAction);
		
		if(gameAction == Canvas.FIRE) {
			CssElement element = ElementAttributes.getCssElement(this);
			if(element != null && element.isInteractive()) {
				Viewport viewport = element.getViewport();
				UserEvent event = new UserEvent();
				viewport.notifyUserEvent(element, event);
			}
		}
		
		return handled;
	}
}
