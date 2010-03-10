package de.enough.skylight.renderer.element.view;

import de.enough.polish.ui.Item;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.partition.Partition;

public class LayoutAttributes {
	static Integer SL_KEY_CONTAINING = new Integer(0x00);
	
	static Integer SL_KEY_BLOCK = new Integer(0x01);
	
	static Integer SL_KEY_ELEMENT = new Integer(0x02);
	
	static Integer SL_KEY_PARTITION = new Integer(0x03);
	
	static Integer SL_KEY_LINE_OFFSET = new Integer(0x04);
	
	public static void set(Item item, CssElement element, ContainingBlock containingBlock, BlockContainingBlock block) {
		setCssElement(item, element);
		setContainingBlock(item, containingBlock);
		setBlock(item, block);
		setPartition(item);
	}
	
	public static void setCssElement(Item item, CssElement element) {
		if(element != null) {
			item.setAttribute(SL_KEY_ELEMENT, element);
		}
	}
	
	public static CssElement getCssElement(Item item) {
		return (CssElement)item.getAttribute(SL_KEY_ELEMENT);
	}
	
	public static void setContainingBlock(Item item, ContainingBlock containingBlock) {
		if(containingBlock != null) {
			item.setAttribute(SL_KEY_CONTAINING, containingBlock);
		}
	}
	
	public static ContainingBlock getContainingBlock(Item item) {
		return (BlockContainingBlock)item.getAttribute(SL_KEY_CONTAINING);
	}
	
	public static void setBlock(Item item, BlockContainingBlock block) {
		if(block != null) {
			item.setAttribute(SL_KEY_BLOCK, block);
		}
	}
	
	public static BlockContainingBlock getBlock(Item item) {
		return (BlockContainingBlock)item.getAttribute(SL_KEY_BLOCK);
	}
	
	public static void setPartition(Item item) {
		Partition partition = Partition.partitionBlock(item); 
		item.setAttribute(SL_KEY_PARTITION, partition);
	}
	
	public static Partition getPartition(Item item) {
		Partition partition = (Partition)item.getAttribute(SL_KEY_PARTITION);
		
		if(partition == null) {
			setPartition(item);
			return (Partition)item.getAttribute(SL_KEY_PARTITION);
		} else {
			return partition;
		}	
	}
	
	public static String toString(Item item) {
		return item.getClass().getName() + LayoutAttributes.getCssElement(item);
	}
}
