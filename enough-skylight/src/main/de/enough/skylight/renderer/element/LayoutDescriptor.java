package de.enough.skylight.renderer.element;

import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.layout.floating.FloatLayout;
import de.enough.skylight.renderer.linebox.InlineLineboxList;
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
	
	public void setLineboxes(InlineLineboxList lineboxes);

	public InlineLineboxList getLineboxes();
	
	public int getInlineRelativeOffset();

	public void setInlineRelativeOffset(int inlineRelativeLeft);
	
	public Viewport getViewport();
	
	public void setViewport(Viewport viewport);
	
	public FloatLayout getFloatLayout();
	
	public void setFloatLayout(FloatLayout floatLayout);
}
