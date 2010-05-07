package de.enough.skylight.renderer.element.view;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.benchmark.Benchmark;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.ItemPreinit;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.css.HtmlCssElement;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.element.InlineContainingBlock;
import de.enough.skylight.renderer.element.LayoutDescriptor;
import de.enough.skylight.renderer.layout.block.InlineLayout;
import de.enough.skylight.renderer.layout.floating.FloatLayout;
import de.enough.skylight.renderer.linebox.InlineLinebox;
import de.enough.skylight.renderer.linebox.InlineLineboxList;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.partition.Partable;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class BlockContainingBlockView extends ContainingBlockView {
	
	InlineLinebox paintedLinebox;
	
	public BlockContainingBlockView(BlockContainingBlock parent) {
		super(parent);
	}
	
	protected void initContent(Item parentContainerItem, int firstLineWidth,
			int availWidth, int availHeight) {
		//#debug sl.debug.layout
		System.out.println("initContent for block " + this.parentContainingBlock + " : width : " + availWidth + " : height : " + availHeight);
		
		//#debug sl.profile.layout
		Benchmark.start("initContent for block " + this);
		
		int relativeY = 0;
		boolean interactive = false;
		
		if(this.cssElement != null && this.cssElement.isInteractive()) {
			interactive = true;
		}
		
		Item[] items = this.parentContainingBlock.getItems();
		for (int index = 0; index < items.length; index++) {
			Item item = items[index];
			
			LayoutDescriptor descriptor = ContentView.getLayoutDescriptor(item);
			
			CssElement cssElement = descriptor.getCssElement();
			
			if(cssElement != null && cssElement.isFloat()) {
				FloatLayout layout = descriptor.getFloatLayout();
			}
			
			if(item instanceof BlockContainingBlock) {
				// handle block elements
				item.relativeX = 0;
				item.relativeY = relativeY;
				ItemPreinit.preinit(item,availWidth, availHeight);
				relativeY += item.itemHeight;
			} else {
				// handle inline elements
				
				// initialize inline hierachy
				ItemPreinit.preinit(item,Integer.MAX_VALUE, Integer.MAX_VALUE);
				
				// partition inline hierachy
				PartitionList partitions = new PartitionList();
				
				if(item instanceof Partable) {
					// partition partable
					Partable partable = (Partable)item;
					partable.partition(partitions);
				} else {
					// partition item
					Partition.partition(item, partitions);
				}
				
				// sort the partitions
				partitions.sort();
				
				//#mdebug sl.debug.layout
				System.out.println("partitions for " + item + " : ");
				for (int i = 0; i < partitions.size(); i++) {
					Partition partition = partitions.get(i);
					System.out.println(partition);
				}
				//#enddebug
				
				// layout the partitions
				InlineLayout layout = new InlineLayout((BlockContainingBlock)this.parentContainingBlock);
				
				layout.layoutPartitions(partitions, null, relativeY);
				
				InlineLineboxList lineboxes = layout.getLineboxes();
				
				//#mdebug sl.debug.layout
				System.out.println("lineboxes for " + item + " : ");
				for (int i = 0; i < lineboxes.size(); i++) {
					InlineLinebox linebox = lineboxes.get(i);
					System.out.println(linebox);
				}
				//#enddebug
				
				descriptor.setLineboxes(lineboxes);	
				
				relativeY += layout.getLayoutHeight();
			}
			
			if (item.isInteractive()) {
				interactive = true;
			}
		}
		
		//#debug sl.profile.layout
		Benchmark.stop("initContent for block " + this,"done");
		
		if(interactive) {
			//#debug sl.debug.event
			System.out.println(this.parentContainingBlock + " has interactive children, setting to interactive");
			
			setAppearanceMode(Item.INTERACTIVE);
		}
		
		this.contentWidth = availWidth;
		this.contentHeight = relativeY;
		
		//setScrolling(body);
	}
	
	public void setScrolling(InlineContainingBlock body) {
		Item item = body.getFocusedItem();
		Container parent = this.parentContainer;
		if(item != null && parent.isFocused && !(item instanceof InlineContainingBlock)) {
			scroll( 0, item.relativeX, item.relativeY, item.itemWidth, item.itemHeight, true );
		}
	}
	
	void positionItem(Item item) {
		LayoutDescriptor layoutDescriptor = ContentView.getLayoutDescriptor(item);
		
		if(layoutDescriptor.getLineboxes().size() == 0) {
			// structural item, no need to position (?)
			return;
		}
		
		Partition firstPartition = layoutDescriptor.getPartitions().get(0);
		InlineLinebox linebox = layoutDescriptor.getLineboxes().get(0);
		
		item.relativeX = linebox.getBlockRelativeLeft() + firstPartition.getLineboxRelativeX();
		item.relativeY = linebox.getBlockRelativeTop();
	}
	
	public void paintLineBox(InlineLinebox linebox, Item item, int x, int y, Graphics g) {
		x = x + linebox.getBlockRelativeLeft();
		y = y + linebox.getBlockRelativeTop();
		
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();
		
		int left = linebox.getTrimmedInlineRelativeLeft();
		int width = linebox.getTrimmedWidth();
		int height = linebox.getLineHeight();
		
		g.clipRect(x, y, width, height);
		
		//#debug sl.debug.render
		System.out.println("set clipping for " + block + " : x : " + x + " : y :" + y + " : width : "+ width + " : height : " + height);

		int leftBorder = x - left;
		int rightBorder = (x - left) +  width;
		item.paint(x - left, y, leftBorder, rightBorder, g);

		g.setClip(clipX, clipY, clipWidth, clipHeight);
	}
	
	public InlineLinebox getPaintLineBox() {
		return this.paintedLinebox;
	}
	
	protected void paintContent(Item parent, int x, int y, int leftBorder,
			int rightBorder, Graphics g) {
		int stage = this.viewport.getRenderStage();
		switch(stage) {
			case Viewport.RenderStage.BODY :
				//#debug sl.debug.render
				System.out.println(this + " : rendering body");
				
				int clipX = g.getClipX();
				int clipY = g.getClipY();
				int clipWidth = g.getClipWidth();
				int clipHeight = g.getClipHeight();
				
				g.clipRect(x, y, this.contentWidth, this.contentHeight);
				
				Item[] items = this.parentContainingBlock.getItems();
				for (int index = 0; index < items.length; index++) {
					Item item = items[index];
					
					if(item instanceof BlockContainingBlock) {
						item.paint(x + item.relativeX, y + item.relativeY, leftBorder, rightBorder, g);
					} else {
						LayoutDescriptor descriptor = ContentView.getLayoutDescriptor(item);
						InlineLineboxList lineboxes = descriptor.getLineboxes();
						
						InlineLinebox linebox;
						for (int lineboxIndex = 0; lineboxIndex < lineboxes.size(); lineboxIndex++) {
							linebox = lineboxes.get(lineboxIndex);
							this.paintedLinebox = linebox;
							paintLineBox(linebox,item,x,y,g);
						}
					}
				}
				
				g.setClip(clipX, clipY, clipWidth, clipHeight);
			break;
			case Viewport.RenderStage.FLOAT :
				//#debug sl.debug.render
				System.out.println(this + " : rendering float");
			break;
		}
	}
}
