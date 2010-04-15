package de.enough.skylight.renderer.element.view;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.DebugHelper;
import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ItemPreinit;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.element.InlineContainingBlock;
import de.enough.skylight.renderer.element.LayoutDescriptor;
import de.enough.skylight.renderer.element.TextBlock;
import de.enough.skylight.renderer.layout.floating.FloatLayout;
import de.enough.skylight.renderer.linebox.InlineLinebox;

public class InlineContainingBlockView extends ContainingBlockView {

	public static Dimension DIMENSION_ZERO = new Dimension(0);
	
	int[] itemXOffsets;
	
	int[] itemYOffsets;
	
	int inlineOffset;
	
	transient FloatLayout floatLayout;
	
	public InlineContainingBlockView(InlineContainingBlock parent) {
		super(parent);
	}
	
	protected void initMargin(Style style, int availWidth) {
		style.addAttribute("margin-top", DIMENSION_ZERO);
		style.addAttribute("margin-bottom", DIMENSION_ZERO);
		super.initMargin(style, this.block.getAvailableContentWidth());
	}

	protected void initPadding(Style style, int availWidth) {
		style.addAttribute("padding-top", DIMENSION_ZERO);
		style.addAttribute("padding-bottom", DIMENSION_ZERO);
		super.initPadding(style, this.block.getAvailableContentWidth());
	}

	protected void initItem(Item item, BlockContainingBlock block) {
		if(item instanceof BlockContainingBlock) {
			ItemPreinit.preinit(item,	block.getAvailableContentWidth(),
										block.getAvailableContentHeight());
		} else {
			ItemPreinit.preinit(item,Integer.MAX_VALUE,Integer.MAX_VALUE);
		}
	}
	
	protected void initContent(Item parentContainerItem, int firstLineWidth,
			int availWidth, int availHeight) {
		//#debug sl.debug.layout
		System.out.println("initializing " + this);
		
		int maxHeight = 0;
		int completeWidth = 0;
		
		boolean interactive = false;
		
		if(this.cssElement != null && this.cssElement.isInteractive()) {
			interactive = true;
			System.out.println(this.cssElement + " : interactive");
		}
		
		this.inlineOffset = this.inlineRelativeOffset + this.parentContainingBlock.getContentX();
		
		Item[] items = this.parentContainingBlock.getItems();
		
		int length = items.length;
		
		if(this.itemXOffsets == null || this.itemXOffsets.length != length) {
			this.itemXOffsets = new int[length];
			this.itemYOffsets = new int[length];
		}
		
		for (int index = 0; index < length; index++) {
			Item item = items[index];
			
			LayoutDescriptor descriptor = ContentView.getLayoutDescriptor(item);
			descriptor.setInlineRelativeOffset(this.inlineOffset + completeWidth);
			
			initItem(item, this.block);
			
			if (item.isInteractive()) {
				interactive = true;
			}
			
			int itemWidth = item.itemWidth;
			int itemHeight = item.itemHeight;
			
			if (itemHeight > maxHeight ) {
				maxHeight = itemHeight;
			}
			
			this.itemXOffsets[index] = completeWidth;
			this.itemYOffsets[index] = 0;
			
			completeWidth += itemWidth;
		}
		
		/*if(element != null) {
			for (int index = 0; index < length; index++) {
				Item item = items[index];
				if(element.isVerticalAlign(HtmlCssElement.VerticalAlign.BOTTOM)) {
					this.itemYOffsets[index] = LayoutUtils.getBottom(0, maxHeight, item.itemHeight);
				} else if(element.isVerticalAlign(HtmlCssElement.VerticalAlign.MIDDLE)) {
					this.itemYOffsets[index] = LayoutUtils.getMiddle(0, maxHeight, item.itemHeight);
				}
			}
		}*/
		
		if(interactive) {
			//#debug sl.debug.event
			System.out.println(this.containingBlock + " has interactive children, setting to interactive");
			
			this.parentContainer.setAppearanceMode(Item.INTERACTIVE);
			setAppearanceMode(Item.INTERACTIVE);
		}
		
		this.contentHeight = maxHeight;
		this.contentWidth = completeWidth;
		
		//#debug sl.debug.layout
		System.out.println(this.containingBlock + " has dimension : " + this.contentWidth + "/" + this.contentHeight);
	}
	
	
	protected void paintContent(Item parent, int x, int y, int leftBorder,
			int rightBorder, Graphics g) {
		InlineLinebox linebox = this.block.getPaintLineBox();
		
		int lineboxInlineRelativeLeft = linebox.getInlineRelativeLeft();
		int lineboxInlineRelativeRight = linebox.getInlineRelativeRight();
		
		Item[] items = this.parentContainer.getItems();
		
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();
		
		int itemInlineRelativeLeft;
		int itemInlineRelativeRight;
		
		for (int i = 0; i < items.length; i++) {
			if (i != this.focusedIndex) {
				Item item = items[i];
				
				int itemX = x + this.itemXOffsets[i];
				int itemY = y + this.itemYOffsets[i];
				
				itemInlineRelativeLeft = this.inlineOffset + this.itemXOffsets[i];
				itemInlineRelativeRight = itemInlineRelativeLeft + item.itemWidth;
				
				if(!(lineboxInlineRelativeRight <= itemInlineRelativeLeft ||
						lineboxInlineRelativeLeft >= itemInlineRelativeRight)) {
					leftBorder = itemX;
					rightBorder = itemX + item.itemWidth;
					
					paintItem(item, i, itemX, itemY, leftBorder, rightBorder, clipX, clipY, clipWidth, clipHeight, g);
				}
			}
		}
		
		Item focItem = this.focusedItem;
		
		if (focItem != null) {
			int focItemX = x + this.itemXOffsets[this.focusedIndex];
			int focItemY = y + this.itemYOffsets[this.focusedIndex];
			
			itemInlineRelativeLeft = this.inlineOffset + this.itemXOffsets[this.focusedIndex];
			itemInlineRelativeRight = itemInlineRelativeLeft + focItem.itemWidth;
			
			if(!(lineboxInlineRelativeRight <= itemInlineRelativeLeft ||
					lineboxInlineRelativeLeft >= itemInlineRelativeRight)) {
				paintItem(focItem, this.focusedIndex, focItemX, focItemY, focItemX, focItemX + focItem.itemWidth, clipX, clipY, clipWidth, clipHeight, g);
			}
		}
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
