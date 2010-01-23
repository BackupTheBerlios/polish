package de.enough.skylight.renderer.element;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ItemPreinit;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.partition.Partable;
import de.enough.skylight.renderer.partition.PartitionList;

public class InlineContainingBlock extends Container implements ContainingBlock, Partable {

	CssElement element;
	
	BlockContainingBlock parentBlock;
	
	public InlineContainingBlock() {
		this(null,null);
	}
	
	public InlineContainingBlock(Style style) {
		this(null, style);
	}
	
	public InlineContainingBlock(CssElement element) {
		this(element, null);
	}
	
	public InlineContainingBlock(CssElement element, Style style) {
		super(false,style);
		
		setAppearanceMode(Item.PLAIN);
	}

	public void addToBody(Item item) {
		add(item);
	}

	public void addToLeftFloat(Item item) {
		this.parentBlock.addToLeftFloat(item);
	}
	
	public void addToRightFloat(Item item) {
		this.parentBlock.addToRightFloat(item);
	}
	
	public CssElement getElement() {
		return this.element;
	}
	
	protected void initContent(int firstLineWidth, int availWidth,
			int availHeight) {
		int maxHeight = 0;
		int completeWidth = 0;
		Item[] items = getItems();

		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			
			initItem(item);
			
			int itemHeight = item.itemHeight;
			int itemWidth = item.itemWidth;
			if (itemHeight > maxHeight ) {
				maxHeight = itemHeight;
			}
			item.relativeX = completeWidth;
			item.relativeY = 0;
			completeWidth += itemWidth;
		}
		
		this.contentHeight = maxHeight;
		this.contentWidth = completeWidth;
	}
	
	protected void initItem(Item item) {
		if(item instanceof BlockContainingBlock) {
			ItemPreinit.preinit(item,	this.parentBlock.getAvailableContentWidth(),
										this.parentBlock.getAvailableContentHeight());
		} else {
			ItemPreinit.preinit(item,Integer.MAX_VALUE,Integer.MAX_VALUE);
		}
	}
	
	public void partition(BlockContainingBlock block, PartitionList partitions) {
		PartitionList.partitionInline(this, block, partitions);
		
		for (int i = 0; i < this.itemsList.size(); i++) {
			Item item = (Item)this.itemsList.get(i);
			if(item instanceof Partable) {
				Partable partable = (Partable)item;
				partable.partition(block, partitions);
			} else {
				PartitionList.partitionInline(item, block, partitions);
			}
		}
	}

	public BlockContainingBlock getParentBlock() {
		return this.parentBlock;
	}

	public void setParentBlock(BlockContainingBlock block) {
		this.parentBlock = block;
	}	
}
