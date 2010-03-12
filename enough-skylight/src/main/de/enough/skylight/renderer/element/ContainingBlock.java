package de.enough.skylight.renderer.element;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;
import de.enough.skylight.renderer.partition.Partable;

public abstract class ContainingBlock extends Container implements Partable {
	public ContainingBlock(boolean focusFirstElement) {
		super(focusFirstElement);
	}
	
	public ContainingBlock(boolean focusFirstElement, Style style) {
		super(focusFirstElement, style);
	}
	
	protected void initContent(int firstLineWidth, int availWidth,
			int availHeight) {
		//super.initContent(firstLineWidth, availWidth, availHeight);
		Item[] items = getItems();
		synchronized(items) {
			if (this.containerView != null) {
				UiAccess.initView(this.containerView, this, firstLineWidth, availWidth, availHeight);
				if (this.defaultCommand != null || (this.commands != null && this.commands.size() > 0)) {
					this.appearanceMode = INTERACTIVE;
				} else {
					this.appearanceMode = this.containerView.getAppearanceMode();
				}
				if (this.isFocused && this.autoFocusEnabled) {
					if (this.autoFocusIndex >= 0 && this.appearanceMode != Item.PLAIN) {
						for (int i = this.autoFocusIndex; i < items.length; i++) {
							Item item = items[i];
							if (item.appearanceMode != Item.PLAIN) {
								this.autoFocusEnabled = false;
								focusChild( i, item, 0, true);
								break;
							}							
						}
					}
				}
				this.contentWidth = this.containerView.getContentWidth();
				this.contentHeight = this.containerView.getContentHeight();
			}
			else {
				//#debug error
				System.out.println("no ContainerView set for " + this);
			}
		}
	}

	public abstract void addToBody(Item item);
	
	public abstract void addToLeftFloat(Item item);
	
	public abstract void addToRightFloat(Item item);
	
	public abstract InlineContainingBlock getBody();
	
	public abstract InlineContainingBlock getLeftFloat();
	
	public abstract InlineContainingBlock getRightFloat();
		
	protected Style focus(Style focusStyle, int direction) {
		requestInit();
		Style style = super.focus(focusStyle, direction);
		return style;
	}

	public void defocus(Style originalStyle) {
		requestInit();
		super.defocus(originalStyle);
	}
}
