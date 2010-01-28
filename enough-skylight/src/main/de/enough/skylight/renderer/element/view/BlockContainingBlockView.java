package de.enough.skylight.renderer.element.view;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.benchmark.Benchmark;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ElementAttributes;
import de.enough.skylight.renderer.element.InlineContainingBlock;
import de.enough.skylight.renderer.element.render.Culling;
import de.enough.skylight.renderer.linebox.LineBox;
import de.enough.skylight.renderer.linebox.LineBoxLayout;
import de.enough.skylight.renderer.linebox.LineBoxList;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class BlockContainingBlockView extends ContainerView {
	
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
		
		//#mdebug sl.debug.layout
		System.out.println("lineboxes for body " + body + " : ");
		for (int i = 0; i < this.bodyLines.size(); i++) {
			LineBox linebox = this.bodyLines.get(i);
			System.out.println(linebox);
		}
		//#enddebug

		CssElement element = ElementAttributes.getCssElement(parentContainerItem);
		if(element != null && (element.isFloat() || element.isParentFloat())) {
			this.contentWidth = bodyLayout.getWidth();
		} else {
			this.contentWidth = availWidth;
		}
		
		this.contentHeight = getLayoutHeight(bodyLayout,floatLeftLayout,floatRightLayout);
		
		//#debug sl.debug.layout
		System.out.println(this.block + " has dimension : " + this.contentWidth + "/" + this.contentHeight);
	}
	
	public void setRelativePositions() {
//		Item item = null;
//		for (int i = 0; i < this.bodyLines.size(); i++) {
//			LineBox linebox = this.bodyLines.get(i);
//			
//			int top = linebox.getTop();
//			
//			PartitionList partitions = linebox.getPartitions();
//			for (int j = 0; j < partitions.size(); j++) {
//				Partition partition = partitions.get(j);
//				int x = partition.getLeft();
//				
//				Item parent = partition.getParent();
//				if(parent instanceof TextBlock) {
//					item = parent;
//				}
//				
//				if(parent != null) {
//					System.out.println(parent);
//					System.out.println("x : " + (x - linebox.getLeft() + linebox.getOffset()));
//					System.out.println("y : " + top);
//					parent.relativeX = (x - linebox.getLeft() + linebox.getOffset());
//					parent.relativeY = top;
//				}
//			}
//		}
//		
//		if(item != null) {
//		System.out.println("item : " + item);
//		System.out.println(item.getAbsoluteX());
//		System.out.println(item.getAbsoluteY());
//		}
	}
	
	public int getLayoutHeight(LineBoxLayout body, LineBoxLayout floatLeft, LineBoxLayout floatRight) {
		int height = body.getHeight();
		
		if(floatLeft != null && floatLeft.getHeight() > height) {
			height = floatLeft.getHeight();
		}
		
		if(floatRight != null && floatRight.getHeight() > height) {
			height = floatRight.getHeight();
		}

		return height;
	}

	protected void paintContent(Container container, Item[] myItems, int x,
			int y, int leftBorder, int rightBorder, int clipX, int clipY,
			int clipWidth, int clipHeight, Graphics g) {
		if(container instanceof Viewport) {
			paintLayout(x, y, leftBorder, rightBorder, g);
		} else {
			BlockContainingBlock block = ElementAttributes.getBlock(container);
			Partition partition = ElementAttributes.getPartition(container);
			LineBox linebox = block.getPaintLineBox();
			
			if(	Culling.isVisible(partition, linebox)) {
				//#debug sl.debug.render
				System.out.println("painted " + this.block + " : linebox : " + linebox + " : partition : " + partition );
				paintLayout(x, y, leftBorder, rightBorder, g);
			} else {
				//#debug sl.debug.render
				System.out.println("culled " + this.block + " : linebox : " + linebox + " : partition : " + partition );
			}
		}
	}
	
	public void paintLineBox(LineBox linebox, InlineContainingBlock block, int x, int y, Graphics g) {
		x = x + linebox.getOffset();
		y = y + linebox.getTop();
		
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();
		
		int left = linebox.getTrimmedLeft();
		int width = linebox.getTrimmedWidth();
		int height = linebox.getHeight();
		
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
