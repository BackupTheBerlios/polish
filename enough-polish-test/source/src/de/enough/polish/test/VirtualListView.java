package de.enough.polish.test;

import de.enough.polish.ui.Canvas;
import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;
import de.enough.polish.util.ArrayList;

public class VirtualListView extends ContainerView{
	transient VirtualListProvider provider;
	
	transient VirtualListRange range;
	
	transient Container container;
	
	int bufferSize;
	
	ArrayList activeItems;
	
	long lastScrollOffset = -1;
	
	long lastNoticationOffset = -1;
	
	public VirtualListView(VirtualListProvider provider, int bufferSize) {
		this.provider = provider;
		this.container = this.provider.getContainer();
		this.bufferSize = bufferSize;
		this.activeItems = new ArrayList();
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
		
		// get the current absolute scroll offset
		long currentOffset = Math.abs(this.container.getScrollYOffset());
		if(	this.lastScrollOffset != currentOffset  
			//#if polish.hasPointerEvents
			//# && this.container.getScrollYOffset() == this.container.getCurrentScrollYOffset()
			//#endif
			) {
			// get the scroll direction
			int direction = getDirection(this.lastScrollOffset, currentOffset);
			
			/* 	if the scroll direction is DOWN (offset is decreased) and the scroll offset
			 *	is below the current range or if the scroll direction is UP (offset is increased) 
			 *	and the scroll offset is above the current range
			 */
			if( (direction == Canvas.DOWN && this.range.belowRange(currentOffset)) || 
				(direction == Canvas.UP && this.range.overRange(currentOffset)) ||
				this.lastScrollOffset == -1) {
				
				// update the range
				this.range.update(currentOffset,direction, this.provider.total());
				this.range.setOffset(-1);

				// select for the new range
				ListSelection selection = this.provider.select(this.range);
				
				// apply the selection
				this.provider.apply(null,selection.getEntries(),null,true);
			}
			this.lastScrollOffset = currentOffset;
		} else {
			// if screen is not interacted with for 500 ms ...
			if( !this.provider.getScreen().isInteracted(500)) {
				// and it was scrolled ...
				if(this.lastNoticationOffset != currentOffset) {
					// notify the inactive and active items
					notifyInactiveItems();
					notifyActiveItems();
					this.lastNoticationOffset = currentOffset;
				}
			}
		}
	}
	
	/**
	 * Initializes the range
	 * @param availWidth the available width
	 */
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
	
	/**
	 * Returns the visible offset
	 * @return the visible offset
	 */
	public int getVisibleOffset()
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
	

	/**
	 * Notifies active items
	 */
	public void notifyActiveItems()
	{
		for (int i = 0; i < this.container.size(); i++) {
			Item item = this.container.get(i);
			if(isItemShown(item))
			{
				this.provider.notify(item, true);
				this.activeItems.add(item);
			}
		}
	}
	
	/**
	 * Notifies inactive items
	 */
	public void notifyInactiveItems()
	{
		while(this.activeItems.size() > 0) {
			Item item = (Item)this.activeItems.get(0);
			this.provider.notify(item, false);
			this.activeItems.remove(0);
		}
	}
	
	/**
	 * Returns true if an item is in the visible area of the managed container
	 * @param item the item
	 * @return true if an item is in the visible area of the managed container otherwise false
	 */
	public boolean isItemShown(Item item)
	{
		int verticalMin = this.container.getAbsoluteY() + Math.abs(this.container.getScrollYOffset());
		int verticalMax = verticalMin + this.container.getScrollHeight();
		
		int itemCenter = item.getAbsoluteY() + (item.itemHeight/2);
		
		boolean shownVertical = (itemCenter > verticalMin && itemCenter < verticalMax);
		
		return shownVertical;
	}
}
