package de.enough.skylight.renderer.element;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.backgrounds.SimpleBackground;
import de.enough.polish.util.ItemPreinit;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.partition.Partable;
import de.enough.skylight.renderer.partition.PartitionList;

public class InlineContainingBlock extends Container implements Partable {

	BlockContainingBlock parentBlock;
	
	public InlineContainingBlock(BlockContainingBlock block) {
		this(block, null);
	}
	
	public InlineContainingBlock(BlockContainingBlock parentBlock, Style style) {
		super(false,style);
		
		this.parentBlock = parentBlock;
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
			int availWidth = this.parentBlock.getAvailableContentWidth();
			int availHeight = this.parentBlock.getAvailableContentHeight();
			
			ItemPreinit.preinit(item,availWidth,availHeight);
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

}
