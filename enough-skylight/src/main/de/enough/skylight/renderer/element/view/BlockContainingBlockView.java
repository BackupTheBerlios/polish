package de.enough.skylight.renderer.element.view;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.benchmark.Benchmark;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.InlineContainingBlock;
import de.enough.skylight.renderer.linebox.LineBox;
import de.enough.skylight.renderer.linebox.LineBoxLayout;
import de.enough.skylight.renderer.linebox.LineBoxList;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class BlockContainingBlockView extends ContainingBlockView {
	
	transient BlockContainingBlock block;
	
	transient LineBoxList bodyLines;
	
	transient LineBoxList floatLeftLines;
	
	transient LineBoxList floatRightLines;
	
	transient LineBox paintLineBox;
	
	public BlockContainingBlockView(BlockContainingBlock block) {
		this.block = block;
	}

	protected void initContent(Item parentContainerItem, int firstLineWidth,
			int availWidth, int availHeight) {
		//#debug sl.profile.layout
		Benchmark.start("initContent for block " + this.block);
		
		super.initContent(parentContainerItem, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		//#debug sl.profile.layout
		Benchmark.stop("initContent for block " + this.block,"done");
		
		CssElement element = LayoutAttributes.getCssElement(parentContainerItem);
		
		if(element != null && element.isInteractive()) {
			this.parentContainer.setAppearanceMode(Item.INTERACTIVE);
			setAppearanceMode(Item.INTERACTIVE);
		}
		
		InlineContainingBlock floatLeft = this.block.getLeftFloat();
		
		LineBoxLayout floatLeftLayout = null;
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
			
			floatLeftLayout = new LineBoxLayout(availWidth);
					
			floatLeftLayout.addPartitions(floatLeftPartitions, floatLeft);
			
			this.floatLeftLines = floatLeftLayout.getLineBoxes();
			
			//#mdebug sl.debug.layout
			System.out.println("lineboxes for left float " + floatLeft + " : ");
			for (int i = 0; i < this.floatLeftLines.size(); i++) {
				LineBox linebox = this.floatLeftLines.get(i);
				System.out.println(linebox);
			}
			//#enddebug
		}
		
		InlineContainingBlock floatRight = this.block.getRightFloat();
		
		LineBoxLayout floatRightLayout = null;
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
			
			floatRightLayout = new LineBoxLayout(availWidth);
			
			floatRightLayout.addPartitions(floatRightPartitions, floatRight);
			
			this.floatRightLines = floatRightLayout.getLineBoxes();
			
			//#mdebug sl.debug.layout
			System.out.println("lineboxes for right float " + floatRight + " : ");
			for (int i = 0; i < this.floatRightLines.size(); i++) {
				LineBox linebox = this.floatRightLines.get(i);
				System.out.println(linebox);
			}
			//#enddebug
		}
		
		InlineContainingBlock body = this.block.getBody();
		
		PartitionList bodyPartitions = new PartitionList();
		
		body.relativeX = 0;
		body.relativeY = 0;
		
		body.partition(bodyPartitions);
		
		bodyPartitions.sort();
		
		//#mdebug sl.debug.layout
		System.out.println("partitions for body " + body + " : ");
		for (int i = 0; i < bodyPartitions.size(); i++) {
			Partition partition = bodyPartitions.get(i);
			System.out.println(partition);
		}
		//#enddebug
		
		LineBoxLayout bodyLayout = new LineBoxLayout(availWidth, this.floatLeftLines, this.floatRightLines);
		
		bodyLayout.addPartitions(bodyPartitions, body);
		
		this.bodyLines = bodyLayout.getLineBoxes();
		
		//TODO set relative y for scrolling
		/*Item item = null;
		for (int i = 0; i < this.bodyLines.size(); i++) {
			LineBox linebox = this.bodyLines.get(i);
			
			int top = linebox.getTop();
			
			PartitionList partitions = linebox.getPartitions();
			for (int j = 0; j < partitions.size(); j++) {
				Partition partition = partitions.get(j);
				int x = partition.getLeft();
		
				Item parent = partition.getParent();
				if(parent instanceof TextBlock) {
					item = parent;
				}
				
				if(parent != null) {
					parent.relativeY = top;
				}
			}
		}	*/
		
		//#mdebug sl.debug.layout
		System.out.println("lineboxes for body " + body + " : ");
		for (int i = 0; i < this.bodyLines.size(); i++) {
			LineBox linebox = this.bodyLines.get(i);
			System.out.println(linebox);
		}
		//#enddebug

		this.contentWidth = getContentWidth(element, bodyLayout, availWidth);
		this.contentHeight = getContentHeight(bodyLayout,floatLeftLayout,floatRightLayout);
		
		//#debug sl.debug.layout
		System.out.println(this.block + " has dimension : " + this.contentWidth + "/" + this.contentHeight);
	}
	
	public int getContentWidth(CssElement element, LineBoxLayout bodyLayout, int availWidth) {
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
	
	public int getContentHeight(LineBoxLayout body, LineBoxLayout floatLeft, LineBoxLayout floatRight) {
		int height = body.getLayoutHeight();
		
		if(floatLeft != null && floatLeft.getLayoutHeight() > height) {
			height = floatLeft.getLayoutHeight();
		}
		
		if(floatRight != null && floatRight.getLayoutHeight() > height) {
			height = floatRight.getLayoutHeight();
		}

		return height;
	}

	protected void paintContent(Container container, Item[] myItems, int x,
			int y, int leftBorder, int rightBorder, int clipX, int clipY,
			int clipWidth, int clipHeight, Graphics g) {
		if(container instanceof Viewport) {
			paintLayout(x, y, leftBorder, rightBorder, g);
		} else {
			BlockContainingBlock block = LayoutAttributes.getBlock(container);
			Partition partition = LayoutAttributes.getPartition(container);
			LineBox linebox = block.getPaintLineBox();
			
			//TODO add culling
			
			//#debug sl.debug.render
			System.out.println("rendered " + this.block + " : linebox : " + linebox + " : partition : " + partition );
			paintLayout(x, y, leftBorder, rightBorder, g);
		}
	}
	
	public void paintLineBox(LineBox linebox, InlineContainingBlock block, int x, int y, Graphics g) {
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
	
	public void paintLayout(int x, int y, int leftBorder, int rightBorder,
			Graphics g) {
		LineBox linebox;
		
		if(this.floatLeftLines != null) {
			InlineContainingBlock floatLeft = this.block.getLeftFloat();
			for (int index = 0; index < this.floatLeftLines.size(); index++) {
				linebox = this.floatLeftLines.get(index);
				this.paintLineBox = linebox;
				paintLineBox(linebox,floatLeft,x,y,g);
			}
		}
		
		if(this.floatRightLines != null) {
			InlineContainingBlock floatRight = this.block.getRightFloat();
			for (int index = 0; index < this.floatRightLines.size(); index++) {
				linebox = this.floatRightLines.get(index);
				this.paintLineBox = linebox;
				paintLineBox(linebox,floatRight,x,y,g);
			}
		}
		
		if(this.bodyLines != null) {
			InlineContainingBlock body = this.block.getBody();
			for (int index = 0; index < this.bodyLines.size(); index++) {
				linebox = this.bodyLines.get(index);
				this.paintLineBox = linebox;
				paintLineBox(linebox,body,x,y,g);
			}
		}
	}
	
	public LineBox getPaintLineBox() {
		return this.paintLineBox;
	}
}
