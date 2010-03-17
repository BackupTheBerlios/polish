package de.enough.skylight.renderer.element.view;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.benchmark.Benchmark;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.util.ItemPreinit;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.element.InlineContainingBlock;
import de.enough.skylight.renderer.layout.BlockLayout;
import de.enough.skylight.renderer.layout.LayoutAttributes;
import de.enough.skylight.renderer.linebox.Linebox;
import de.enough.skylight.renderer.linebox.LineboxList;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class BlockContainingBlockView extends ContainingBlockView {
	
	transient LineboxList bodyLines;
	
	transient LineboxList floatLeftLines;
	
	transient LineboxList floatRightLines;
	
	transient Linebox paintLineBox;
	
	public BlockContainingBlockView(BlockContainingBlock block) {
		super(block);
	}

	protected void initContent(Item parentContainerItem, int firstLineWidth,
			int availWidth, int availHeight) {
		ContainingBlock containingBlock = (ContainingBlock)parentContainerItem;
		LayoutAttributes attributes = containingBlock.getLayoutAttributes();
		CssElement element = attributes.getElement(); 
		
		//#debug sl.profile.layout
		Benchmark.start("initContent for block " + this.containingBlock);
		
		super.initContent(parentContainerItem, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		//#debug sl.profile.layout
		Benchmark.stop("initContent for block " + this.containingBlock,"done");
		
		if(element != null && element.isInteractive()) {
			this.parentContainer.setAppearanceMode(Item.INTERACTIVE);
			setAppearanceMode(Item.INTERACTIVE);
		}
		
		InlineContainingBlock floatLeft = this.containingBlock.getLeftFloat();
		
		BlockLayout floatLeftLayout = null;
		if(floatLeft != null) {
			floatLeft.relativeX = 0;
			floatLeft.relativeY = 0;
			
			PartitionList floatLeftPartitions = new PartitionList();
			
			//#debug sl.profile.layout
			Benchmark.start("partitioning : " + floatLeft);
			
			floatLeft.partition(floatLeftPartitions);
			floatLeftPartitions.sort();
			
			//#debug sl.profile.layout
			Benchmark.stop("partitioning : " + floatLeft,"done");
			
			//#mdebug sl.debug.layout
			System.out.println("partitions for right float " + floatLeft + " : ");
			for (int i = 0; i < floatLeftPartitions.size(); i++) {
				Partition partition = floatLeftPartitions.get(i);
				System.out.println(partition);
			}
			//#enddebug
			
			floatLeftLayout = new BlockLayout(availWidth);
					
			floatLeftLayout.addPartitions(floatLeftPartitions, floatLeft);
			
			this.floatLeftLines = floatLeftLayout.getLineBoxes();
			
			//#mdebug sl.debug.layout
			System.out.println("lineboxes for left float " + floatLeft + " : ");
			for (int i = 0; i < this.floatLeftLines.size(); i++) {
				Linebox linebox = this.floatLeftLines.get(i);
				System.out.println(linebox);
			}
			//#enddebug
		}
		
		InlineContainingBlock floatRight = this.containingBlock.getRightFloat();
		
		BlockLayout floatRightLayout = null;
		if(floatRight != null) {
			floatRight.relativeX = 0;
			floatRight.relativeY = 0;
			
			PartitionList floatRightPartitions = new PartitionList();
			
			//#debug sl.profile.layout
			Benchmark.start("partitioning : " + floatRight);
			
			floatRight.partition(floatRightPartitions);
			
			floatRightPartitions.sort();
			
			//#debug sl.profile.layout
			Benchmark.stop("partitioning : " + floatRight,"done");
			
			//#mdebug sl.debug.layout
			System.out.println("partitions for right float " + floatRight + " : ");
			for (int i = 0; i < floatRightPartitions.size(); i++) {
				Partition partition = floatRightPartitions.get(i);
				System.out.println(partition);
			}
			//#enddebug
			
			floatRightLayout = new BlockLayout(availWidth);
			
			floatRightLayout.addPartitions(floatRightPartitions, floatRight);
			
			this.floatRightLines = floatRightLayout.getLineBoxes();
			
			//#mdebug sl.debug.layout
			System.out.println("lineboxes for right float " + floatRight + " : ");
			for (int i = 0; i < this.floatRightLines.size(); i++) {
				Linebox linebox = this.floatRightLines.get(i);
				System.out.println(linebox);
			}
			//#enddebug
		}
		
		InlineContainingBlock body = this.containingBlock.getBody();
		
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
		
		BlockLayout bodyLayout = new BlockLayout(availWidth, this.floatLeftLines, this.floatRightLines);
		
		bodyLayout.addPartitions(bodyPartitions, body);
		
		this.bodyLines = bodyLayout.getLineBoxes();
		
		for (int i = 0; i < body.size(); i++) {
			Item item = body.get(i);
			item.relativeX = LayoutAttributes.getRelativeX(item);
			item.relativeY = LayoutAttributes.getRelativeY(item);
		}
		
		//#mdebug sl.debug.layout
		System.out.println("lineboxes for body " + body + " : ");
		for (int i = 0; i < this.bodyLines.size(); i++) {
			Linebox linebox = this.bodyLines.get(i);
			System.out.println(linebox);
		}
		//#enddebug

		this.contentWidth = getContentWidth(element, bodyLayout, availWidth);
		this.contentHeight = getContentHeight(bodyLayout,floatLeftLayout,floatRightLayout);
		
		Item item = body.getFocusedItem();
		Container parent = this.parentContainer;
		if(item != null && parent.isFocused && !(item instanceof InlineContainingBlock)) {
			scroll( 0, item.relativeX, item.relativeY, item.itemWidth, item.itemHeight, true );
		}
		
		//#debug sl.debug.layout
		System.out.println(this.containingBlock + " has dimension : " + this.contentWidth + "/" + this.contentHeight);
	}
	
	public int getContentWidth(CssElement element, BlockLayout bodyLayout, int availWidth) {
		if(element != null && (element.isFloat() || element.isParentFloat())) {
			return bodyLayout.getLayoutWidth();
		} else {
			if(this.parentContainer.getMinimumWidth() != 0) {
				return availWidth;
			} else {
				return availWidth;
			}
		}
	}
	
	public int getContentHeight(BlockLayout body, BlockLayout floatLeft, BlockLayout floatRight) {
		int height = body.getLayoutHeight();
		
		if(floatLeft != null && floatLeft.getLayoutHeight() > height) {
			height = floatLeft.getLayoutHeight();
		}
		
		if(floatRight != null && floatRight.getLayoutHeight() > height) {
			height = floatRight.getLayoutHeight();
		}

		return height;
	}
	
	protected void paintContent(Item parent, int x, int y, int leftBorder,
			int rightBorder, Graphics g) {
		Linebox linebox;
		
		if(this.floatLeftLines != null) {
			InlineContainingBlock floatLeft = this.containingBlock.getLeftFloat();
			for (int index = 0; index < this.floatLeftLines.size(); index++) {
				linebox = this.floatLeftLines.get(index);
				this.paintLineBox = linebox;
				paintLineBox(linebox,floatLeft,x,y,g);
			}
		}
		
		if(this.floatRightLines != null) {
			InlineContainingBlock floatRight = this.containingBlock.getRightFloat();
			for (int index = 0; index < this.floatRightLines.size(); index++) {
				linebox = this.floatRightLines.get(index);
				this.paintLineBox = linebox;
				paintLineBox(linebox,floatRight,x,y,g);
			}
		}
		
		if(this.bodyLines != null) {
			InlineContainingBlock body = this.containingBlock.getBody();
			for (int index = 0; index < this.bodyLines.size(); index++) {
				linebox = this.bodyLines.get(index);
				this.paintLineBox = linebox;
				paintLineBox(linebox,body,x,y,g);
			}
		}
	}
	
	public void paintLineBox(Linebox linebox, InlineContainingBlock block, int x, int y, Graphics g) {
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
		block.paint(x - left, y, leftBorder, rightBorder, g);

		g.setClip(clipX, clipY, clipWidth, clipHeight);
	}
	
	public Linebox getPaintLineBox() {
		return this.paintLineBox;
	}
}
