package de.enough.polish.test;

import de.enough.polish.ui.Canvas;
import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;

public class VirtualListView extends ContainerView{
	VirtualListProvider provider;
	
	VirtualListRange range;
	
	Container container;
	
	int bufferSize;
	
	long lastOffset = -1;
	
	public VirtualListView(VirtualListProvider provider, int bufferSize) {
		this.provider = provider;
		this.container = this.provider.getContainer();
		this.bufferSize = bufferSize;
	}
	
	public VirtualListRange getRange() {
		return this.range;
	}
	
	/**
	 * Returns the height of the first item
	 * 
	 * @return the height
	 */
	int getReferenceHeight(int width) {
		Item sampleItem = this.provider.createItem(this.provider.getSampleData(), null);
		return sampleItem.getItemHeight(width,width);
	}	
	
	public boolean handlePointerDragged(int x, int y) {
		this.parentContainer.focusChild(-1);
		return super.handlePointerDragged(x, y);
	}
	
	public int getDirection(long previousOffset, long currentOffset) {
		if(previousOffset > currentOffset) {
			return Canvas.DOWN;
		} else {
			return Canvas.UP;
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion) {
		super.animate(currentTime, repaintRegion);
		
		long currentOffset = Math.abs(this.container.getScrollYOffset());
		if(	this.lastOffset != currentOffset  
			//#if polish.hasPointerEvents
			//# && this.container.getScrollYOffset() == this.container.getCurrentScrollYOffset()
			//#endif
			) 
			{
			int direction = getDirection(this.lastOffset, currentOffset);
			
			if( (direction == Canvas.DOWN && this.range.belowRange(currentOffset)) || 
				(direction == Canvas.UP && this.range.overRange(currentOffset)) ||
				this.lastOffset == -1) {
				
				this.range.update(currentOffset,direction, this.provider.total());
				this.range.setOffset(-1);
				
				ListSelection selection = this.provider.select(this.range);
				
				this.provider.apply(null,selection.getEntries(),null,true);
			}
			
			this.lastOffset = currentOffset;
		} 
	}
	
	public void initRange(int availWidth) {
		int referenceHeight = getReferenceHeight(availWidth);
		this.range = new VirtualListRange(referenceHeight,availWidth,this.bufferSize);
		this.range.setRange(0, 0, this.provider.total());
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Item, int, int, int)
	 */
	protected void initContent(Item parentContainerItem, int firstLineWidth,
			int availWidth, int availHeight) {
		super.initContent(parentContainerItem, firstLineWidth, availWidth, availHeight);
		
		if(this.range == null) {
			initRange(availWidth);
		} 
		
		initListContent(this.range);
	}
	
	public int getOffset()
	{
		Item item = this.container.getFocusedItem();
		if(item != null)
		{
			return item.relativeY - (this.parentContainer.getScrollYOffset() * -1);
		}
		else
		{
			return -1;
		}
	}

	/**
	 * Adjusts the scroll offset
	 */
	protected void setScrollOffset(Container container, VirtualListRange range)
	{
		if(range.getOffset() != -1) {
			Item item = container.getFocusedItem();
			int index = container.getFocusedIndex();
			
			int visibleHeight = range.getAvailableHeight();
			int listHeight = range.getTotalHeight();
			int referenceHeight = range.getReferenceHeight();
			
			int yOffset = 0;
			
			if(listHeight > visibleHeight && this.container.getFocusedIndex() != 0)
			{
				if(index == this.container.size() - 1)
				{
					yOffset = (listHeight - visibleHeight) * -1;
				}
				else if(item != null && item.relativeY > range.getOffset())
				{
					yOffset = -item.relativeY + range.getOffset();
					
					if(item.relativeY > 0 && range.getOffset() == -1)
					{
						yOffset = -item.relativeY + referenceHeight;
					}
				}
			}
			
			
			this.parentContainer.setScrollYOffset(yOffset,false);
		}
	}
	/**
	 * Initializes the container settings to simulate a list consisting of the
	 * total count of entries
	 * 
	 * @param range
	 *            the range to initialize the container 
	 */
	void initListContent(VirtualListRange range) {
		//#debug debug
		System.out.println("initialising list content");

		int height = range.getReferenceHeight();
		int start = range.getStart();

		int itemOffset = height * start;
			
		for (int i = 0; i < this.parentContainer.size(); i++) {

			Item item = this.parentContainer.get(i);
			item.relativeY = itemOffset;
				
			itemOffset += height;
		}
		this.contentHeight = range.getTotalHeight();
		
		setScrollOffset(this.container, this.range);
	}
}
