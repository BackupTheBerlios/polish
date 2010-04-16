package de.enough.skylight.renderer.element.view;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.benchmark.Benchmark;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.element.InlineContainingBlock;
import de.enough.skylight.renderer.element.LayoutDescriptor;
import de.enough.skylight.renderer.layout.block.BlockLayout;
import de.enough.skylight.renderer.layout.floating.FloatLayout;
import de.enough.skylight.renderer.linebox.InlineLinebox;
import de.enough.skylight.renderer.linebox.InlineLineboxList;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class BlockContainingBlockView extends ContainingBlockView {
	
	transient InlineLineboxList bodyLines;
	
	transient InlineLineboxList floatLeftLines;
	
	transient InlineLineboxList floatRightLines;
	
	transient FloatLayout floatLayout;
	
	transient InlineLinebox paintLineBox;
	
	int availableWidth = -1;
	int availableHeight = -1;
	
	public BlockContainingBlockView(BlockContainingBlock parent) {
		super(parent);
	}
	
	protected void initContent(Item parentContainerItem, int firstLineWidth,
			int availWidth, int availHeight) {
		//#debug sl.debug.layout
		System.out.println("initContent for block " + this.parentContainingBlock + " : width : " + availWidth + " : height : " + availHeight);
		
		//#debug sl.profile.layout
		Benchmark.start("initContent for block " + this.parentContainingBlock);
		
		super.initContent(this.parentContainingBlock, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		//#debug sl.profile.layout
		Benchmark.stop("initContent for block " + this.containingBlock,"done");
		
		if(this.cssElement != null && this.cssElement.isInteractive()) {
			this.parentContainer.setAppearanceMode(Item.INTERACTIVE);
			setAppearanceMode(Item.INTERACTIVE);
		}
		
		InlineContainingBlock body = this.parentContainingBlock.getBody();
		
		if(body != null) {
			PartitionList bodyPartitions = new PartitionList();
			
			body.partition(bodyPartitions);
			
			bodyPartitions.sort();
			
			//#mdebug sl.debug.layout
			System.out.println("partitions for body " + body + " : ");
			for (int i = 0; i < bodyPartitions.size(); i++) {
				Partition partition = bodyPartitions.get(i);
				System.out.println(partition);
			}
			//#enddebug
			
			BlockLayout bodyLayout = new BlockLayout(availWidth);
			
			bodyLayout.addPartitions(bodyPartitions, body);
			
			this.bodyLines = bodyLayout.getLineBoxes();
			
			for (int i = 0; i < body.size(); i++) {
				Item item = body.get(i);
				positionItem(item);
			}
			
			//#mdebug sl.debug.layout
			System.out.println("lineboxes for body " + body + " : ");
			for (int i = 0; i < this.bodyLines.size(); i++) {
				InlineLinebox linebox = this.bodyLines.get(i);
				System.out.println(linebox);
			}
			//#enddebug
			
			this.contentWidth = availWidth;
			this.contentHeight = bodyLayout.getLayoutHeight();
			
			Item item = body.getFocusedItem();
			Container parent = this.parentContainer;
			if(item != null && parent.isFocused && !(item instanceof InlineContainingBlock)) {
				scroll( 0, item.relativeX, item.relativeY, item.itemWidth, item.itemHeight, true );
			}
			
			//#debug sl.debug.layout
			System.out.println(this.containingBlock + " has dimension : " + this.contentWidth + "/" + this.contentHeight);
		}
	}
	
	void positionItem(Item item) {
		LayoutDescriptor layoutDescriptor = ContentView.getLayoutDescriptor(item);
		
		if(layoutDescriptor.getPartitions().size() > 0) {
			Partition firstPartition = layoutDescriptor.getPartitions().get(0);
			item.relativeX = firstPartition.getLineboxRelativeX();
		}
		
		if(layoutDescriptor.getLineboxes().size() > 0) {
			InlineLinebox linebox = layoutDescriptor.getLineboxes().get(0);
			item.relativeY = linebox.getContextRelativeTop(); 
		}
	}
	
	public void paintLineBox(InlineLinebox linebox, InlineContainingBlock block, int x, int y, Graphics g) {
		x = x + linebox.getContextRelativeLeft();
		y = y + linebox.getContextRelativeTop();
		
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
		block.paint(x - left, y, leftBorder, rightBorder, g);

		g.setClip(clipX, clipY, clipWidth, clipHeight);
	}
	
	public InlineLinebox getPaintLineBox() {
		return this.paintLineBox;
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
				
				InlineLinebox linebox;
				if(this.bodyLines != null) {
					InlineContainingBlock body = this.parentContainingBlock.getBody();
					for (int index = 0; index < this.bodyLines.size(); index++) {
						linebox = this.bodyLines.get(index);
						this.paintLineBox = linebox;
						paintLineBox(linebox,body,x,y,g);
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
