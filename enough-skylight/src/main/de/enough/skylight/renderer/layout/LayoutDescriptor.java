package de.enough.skylight.renderer.layout;

import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.linebox.LineboxList;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.partition.PartitionList;


public interface LayoutDescriptor {
	
	public CssElement getCssElement();

	public void setCssElement(CssElement element);

	public ContainingBlock getContainingBlock();

	public void setContainingBlock(ContainingBlock containingBlock);

	public BlockContainingBlock getBlock();

	public void setBlock(BlockContainingBlock block);
	
	public PartitionList getPartitions();

	public LineboxList getLineboxes();
	
	public int getInlineRelativeOffset();

	public void setInlineRelativeOffset(int inlineRelativeLeft);
}
