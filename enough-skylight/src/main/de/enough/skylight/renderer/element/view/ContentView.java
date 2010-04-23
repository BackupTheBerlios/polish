package de.enough.skylight.renderer.element.view;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Canvas;
import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemView;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;
import de.enough.skylight.event.UserEvent;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.ViewportContext;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.element.InputSubmitField;
import de.enough.skylight.renderer.element.LayoutDescriptor;
import de.enough.skylight.renderer.layout.floating.FloatLayout;
import de.enough.skylight.renderer.linebox.InlineLineboxList;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.partition.PartitionList;

public class ContentView extends ItemView implements LayoutDescriptor {

	public static LayoutDescriptor getLayoutDescriptor(Item item) {
		return (LayoutDescriptor) item.getView();
	}

	transient Item item;

	transient CssElement cssElement = null;

	transient ContainingBlock containingBlock = null;

	transient BlockContainingBlock block = null;

	transient InlineLineboxList lineboxes = new InlineLineboxList();

	transient PartitionList partitions = new PartitionList();

	int inlineRelativeOffset = 0;
	
	transient Viewport viewport;
	
	transient FloatLayout floatLayout;

	public static Dimension DIMENSION_ZERO = new Dimension(0);

	public ContentView(Item item) {
		this.item = item;
	}

	protected void initMargin(Style style, int availWidth) {
		super.initMargin(style, this.block.getAvailableContentWidth());
	}

	protected void initPadding(Style style, int availWidth) {
		super.initPadding(style, this.block.getAvailableContentWidth());
	}

	protected void initContent(Item parent, int firstLineWidth, int availWidth,
			int availHeight) {
		UiAccess.initContent(parent, firstLineWidth, availWidth, availHeight);
		this.contentWidth = parent.getContentWidth();
		this.contentHeight = parent.getContentHeight();
	}

	protected void paintContent(Item parent, int x, int y, int leftBorder,
			int rightBorder, Graphics g) {
		UiAccess.paintContent(parent, x, y, leftBorder, rightBorder, g);
	}

	public boolean handleKeyReleased(int keyCode, int gameAction) {
		boolean handled = false;

		if (gameAction == Canvas.FIRE) {
			handled = handleOnClick(this.cssElement);
		}
		
		if(!handled) {
			handled = super.handleKeyReleased(keyCode, gameAction);
		}
		
		return handled;
	}

	public boolean handlePointerReleased(int x, int y) {
		boolean handled = handleOnClick(this.cssElement);
		
		if(!handled) {
			handled = super.handlePointerReleased(x, y);
		}
		
		return handled;
	}
	
	public static boolean handleOnClick(CssElement cssElement) {
		if (cssElement != null && cssElement.isInteractive()) {
			ViewportContext context = cssElement.getViewportContext();
			UserEvent event = new UserEvent();
			context.notifyUserEvent(cssElement, event);
			return true;
		}
		
		return false;
	}

	public BlockContainingBlock getBlock() {
		return this.block;
	}

	public ContainingBlock getContainingBlock() {
		return this.containingBlock;
	}

	public CssElement getCssElement() {
		return this.cssElement;
	}

	public int getInlineRelativeOffset() {
		return this.inlineRelativeOffset;
	}

	public void setLineboxes(InlineLineboxList lineboxes) {
		this.lineboxes = lineboxes;
	}

	public InlineLineboxList getLineboxes() {
		return this.lineboxes;
	}

	public PartitionList getPartitions() {
		return this.partitions;
	}

	public void setBlock(BlockContainingBlock block) {
		this.block = block;
	}

	public void setContainingBlock(ContainingBlock containingBlock) {
		this.containingBlock = containingBlock;
	}

	public void setCssElement(CssElement cssElement) {
		this.cssElement = cssElement;
	}

	public void setInlineRelativeOffset(int inlineRelativeLeft) {
		this.inlineRelativeOffset = inlineRelativeLeft;
	}
	
	public Viewport getViewport() {
		return this.viewport;
	}

	public void setViewport(Viewport viewport) {
		this.viewport = viewport;
	}
	
	public FloatLayout getFloatLayout() {
		FloatLayout floatLayout = this.floatLayout;
		ContainingBlock containingBlock = getContainingBlock();
		
		while(floatLayout == null) {
				LayoutDescriptor descriptor = containingBlock.getLayoutDescriptor();
				containingBlock = descriptor.getContainingBlock();
				
				if(containingBlock != null) {
					LayoutDescriptor parentDescriptor = containingBlock.getLayoutDescriptor();
					floatLayout = parentDescriptor.getFloatLayout();
				} else {
					return null;
				}
		}
		
		return floatLayout;
	}

	public void setFloatLayout(FloatLayout floatLayout) {
		this.floatLayout = floatLayout;
	}
}
