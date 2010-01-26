package de.enough.skylight.renderer.element;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.linebox.LineBox;
import de.enough.skylight.renderer.linebox.LineBoxLayout;
import de.enough.skylight.renderer.linebox.LineBoxList;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.partition.Partable;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class BlockContainingBlock extends Container implements ContainingBlock, Partable {

	LineBoxList bodyLines;
	
	LineBoxList floatLeftLines;
	
	LineBoxList floatRightLines;
	
	protected InlineContainingBlock body;

	protected InlineContainingBlock floatLeft;
	
	protected InlineContainingBlock floatRight;
	
	CssElement element;
	
	BlockContainingBlock parentBlock;
	
	PartitionList blockPartitions;
	
	public BlockContainingBlock(CssElement element) {
		this(element,element.getStyle());
	}
	
	public BlockContainingBlock(CssElement element, Style style) {
		super( false, style );
		
		this.element = element;
		
		//#style element
		this.body = new InlineContainingBlock();
		this.body.setParentBlock(this);
		
		this.blockPartitions = new PartitionList();
		
		add(this.body);
		
		//setAppearanceMode(Item.PLAIN);
	}
	
	protected int getContentWidth(LineBoxList lineboxes) {
		int width = 0;
		for (int index = 0; index < lineboxes.size(); index++) {
			LineBox linebox = lineboxes.get(index);
			if(linebox.getWidth() > width) {
				width = linebox.getWidth();
			}
		}

		return width;
	}
	
	public void addToBody(Item item) {
		this.body.add(item);
	}
	
	public void addToLeftFloat(Item item) {
		if(this.floatLeft == null) {
			//#style element
			this.floatLeft = new InlineContainingBlock();
			this.floatLeft.setParentBlock(this);
			add(this.floatLeft);
		}
		
		this.floatLeft.add(item);
	}
	
	public void addToRightFloat(Item item) {
		if(this.floatRight == null) {
			//#style element
			this.floatRight = new InlineContainingBlock();
			this.floatRight.setParentBlock(this);
			add(this.floatRight);
		}
		
		this.floatRight.add(item);
	}
	
	public CssElement getElement() {
		return this.element;
	}
	
	protected void initContent(int firstLineWidth, int availWidth,
			int availHeight) {
		super.initContent(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		LineBoxLayout floatLeftLayout = null;
		if(this.floatLeft != null) {
			this.floatLeft.relativeX = 0;
			this.floatLeft.relativeY = 0;
			
			PartitionList floatLeftPartitions = new PartitionList();
			
			this.floatLeft.partition(this, floatLeftPartitions);
			
			floatLeftPartitions.sort();
			
			floatLeftLayout = new LineBoxLayout(availWidth);
					
			floatLeftLayout.addPartitions(floatLeftPartitions, this.floatLeft);
			
			this.floatLeftLines = floatLeftLayout.getLineBoxes();
		}
		
		LineBoxLayout floatRightLayout = null;
		if(this.floatRight != null) {
			this.floatRight.relativeX = 0;
			this.floatRight.relativeY = 0;
			
			PartitionList floatRightPartitions = new PartitionList();
			
			this.floatRight.partition(this, floatRightPartitions);
			
			floatRightPartitions.sort();
			
			floatRightLayout = new LineBoxLayout(availWidth);
			
			floatRightLayout.addPartitions(floatRightPartitions, this.floatRight);
			
			this.floatRightLines = floatRightLayout.getLineBoxes();
		}
		
		PartitionList bodyPartitions = new PartitionList();
		
		this.body.relativeX = 0;
		this.body.relativeY = 0;
		
		this.body.partition(this, bodyPartitions);
		
		bodyPartitions.sort();
		
		LineBoxLayout bodyLayout = new LineBoxLayout(availWidth, this.floatLeftLines, this.floatRightLines);
		
		bodyLayout.addPartitions(bodyPartitions, this.body);
		
		this.bodyLines = bodyLayout.getLineBoxes();
		
		for (int i = 0; i < this.bodyLines.size(); i++) {
			LineBox linebox = this.bodyLines.get(i);
			
			int top = linebox.getTop();
			
			PartitionList partitions = linebox.getPartitions();
			for (int j = 0; j < partitions.size(); j++) {
				Partition partition = partitions.get(j);
				int x = partition.getLeft();
				
				Item parent = partition.getParent();
				if(parent != null) {
					System.out.println(parent);
					System.out.println("x : " + (x - linebox.getLeft() + linebox.getOffset()));
					System.out.println("y : " + top);
				}
			}
		}
		
		if(this.element != null && (this.element.isFloat() || this.element.isParentFloat())) {
			this.contentWidth = bodyLayout.getWidth();
		} else {
			this.contentWidth = availWidth;
		}
		
		this.contentHeight = getLayoutHeight(bodyLayout,floatLeftLayout,floatRightLayout);
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
	
	protected void paintContent(int x, int y, int leftBorder, int rightBorder,
			Graphics g) {

		if(this.blockPartitions.size() > 0) { 
			Partition blockPartition = this.blockPartitions.get(0);
			
			LineBox linebox = this.parentBlock.getPaintLineBox();
			
			if(	(	blockPartition.getLeft() >= linebox.getLeft() && 
					blockPartition.getLeft() <= linebox.getRight()) ||
				(	blockPartition.getRight() >= linebox.getLeft() && 
					blockPartition.getRight() <= linebox.getRight()) ||
				(	blockPartition.getLeft() <= linebox.getLeft() && 
					blockPartition.getRight() >= linebox.getRight())) {
				paintLayout(x, y, leftBorder, rightBorder, g);
			} 
		} else {
			paintLayout(x, y, leftBorder, rightBorder, g);
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

		int leftBorder = x - left;
		int rightBorder = (x - left) +  width;
		block.paint(x - left, y, leftBorder, rightBorder, g);

		g.setClip(clipX, clipY, clipWidth, clipHeight);
	}
	
	LineBox paintLineBox;
	
	public LineBox getPaintLineBox() {
		return this.paintLineBox;
	}
	
	public void paintLayout(int x, int y, int leftBorder, int rightBorder,
			Graphics g) {
		LineBox linebox;
		
		if(this.floatLeftLines != null) {
			for (int index = 0; index < this.floatLeftLines.size(); index++) {
				linebox = this.floatLeftLines.get(index);
				this.paintLineBox = linebox;
				paintLineBox(linebox,this.floatLeft,x,y,g);
			}
		}
		
		if(this.floatRightLines != null) {
			for (int index = 0; index < this.floatRightLines.size(); index++) {
				linebox = this.floatRightLines.get(index);
				this.paintLineBox = linebox;
				paintLineBox(linebox,this.floatRight,x,y,g);
			}
		}
		
		if(this.bodyLines != null) {
			// painting lineboxes
			for (int index = 0; index < this.bodyLines.size(); index++) {
				linebox = this.bodyLines.get(index);
				this.paintLineBox = linebox;
				paintLineBox(linebox,this.body,x,y,g);
			}
		}
	}
	
	public void partition(BlockContainingBlock block, PartitionList partitions) {
		this.blockPartitions.clear();
		PartitionList.partitionBlock(this, block, this.blockPartitions);
		
		partitions.addAll(this.blockPartitions);
	}
	
	public BlockContainingBlock getParentBlock() {
		return this.parentBlock;
	}

	public void setParentBlock(BlockContainingBlock block) {
		this.parentBlock = block;
	}
	
	public String toString() {
		return "BlockContainingBlock [" + this.element + "]";
	}
}
