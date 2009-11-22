package de.enough.skylight.renderer.view.element;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.containerviews.Midp2ContainerView;
import de.enough.skylight.renderer.viewport.ElementHandler;

public class FormattingBlock extends Container {
	
	public static int INLINE = 0x00;
	public static int BLOCK = 0x01;
	
	int display;
	
	ElementHandler handler;
	
	public boolean isInline() {
		return this.display == INLINE;
	}
	
	public boolean isBlock() {
		return this.style.getLayout() == (LAYOUT_NEWLINE_AFTER | LAYOUT_NEWLINE_BEFORE);
	}
	
	public FormattingBlock(ElementHandler handler) {
		super(false);
		
		this.handler = handler;
	}
	
	public FormattingBlock(ElementHandler handler, Style style) {
		super(false, style);
		
		this.handler = handler;
	}
}
