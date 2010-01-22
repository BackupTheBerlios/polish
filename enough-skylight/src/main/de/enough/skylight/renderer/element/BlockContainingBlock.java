package de.enough.skylight.renderer.element;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.linebox.LineBox;
import de.enough.skylight.renderer.linebox.LineBoxLayout;
import de.enough.skylight.renderer.linebox.LineBoxList;
import de.enough.skylight.renderer.node.NodeElement;
import de.enough.skylight.renderer.partition.Partable;
import de.enough.skylight.renderer.partition.PartitionList;

public class BlockContainingBlock extends Container implements ContainingBlock, Partable {

	LineBoxList bodyLines;
	
	LineBoxList floatLeftLines;
	
	LineBoxList floatRightLines;
	
	protected InlineContainingBlock body;

	protected InlineContainingBlock floatLeft;
	
	protected InlineContainingBlock floatRight;
	
	NodeElement element;
	
	public BlockContainingBlock(NodeElement element) {
		this(element,element.getStyle());
	}
	
	public BlockContainingBlock(NodeElement element, Style style) {
		super(false,style);
		
		this.element = element;
		
		//#style element
		this.body = new InlineContainingBlock(null, this);
		
		add(this.body);
		
		setAppearanceMode(Item.PLAIN);
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
			this.floatLeft = new InlineContainingBlock(null, this);
			add(this.floatLeft);
		}
		
		this.floatLeft.add(item);
	}
	
	public void addToRightFloat(Item item) {
		if(this.floatRight == null) {
			//#style element
			this.floatRight = new InlineContainingBlock(null, this);
			add(this.floatRight);
		}
		
		this.floatRight.add(item);
	}
	
	public NodeElement getElement() {
		return this.element;
	}
	
	protected void initContent(int firstLineWidth, int availWidth,
			int availHeight) {
		super.initContent(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		LineBoxLayout floatLeftLayout = null;
		if(this.floatLeft != null) {
			PartitionList floatLeftPartitions = new PartitionList();
			
			this.floatLeft.partition(this, floatLeftPartitions);
			
			floatLeftPartitions.sort();
			
			floatLeftLayout = new LineBoxLayout(availWidth);
					
			floatLeftLayout.addPartitions(floatLeftPartitions, this.floatLeft);
			
			this.floatLeftLines = floatLeftLayout.getLineBoxes();
		}
		
		LineBoxLayout floatRightLayout = null;
		if(this.floatRight != null) {
			PartitionList floatRightPartitions = new PartitionList();
			
			this.floatRight.partition(this, floatRightPartitions);
			
			floatRightPartitions.sort();
			
			floatRightLayout = new LineBoxLayout(availWidth);
			
			floatRightLayout.addPartitions(floatRightPartitions, this.floatRight);
			
			this.floatRightLines = floatRightLayout.getLineBoxes();
		}
		
		PartitionList bodyPartitions = new PartitionList();
		
		this.body.partition(this, bodyPartitions);
		
		bodyPartitions.sort();
		
		LineBoxLayout bodyLayout = new LineBoxLayout(availWidth, this.floatLeftLines, this.floatRightLines);
		
		bodyLayout.addPartitions(bodyPartitions, this.body);
		
		this.bodyLines = bodyLayout.getLineBoxes();

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
		LineBox linebox;
		
		if(this.floatLeftLines != null) {
			for (int index = 0; index < this.floatLeftLines.size(); index++) {
				linebox = this.floatLeftLines.get(index);
				linebox.paint(x, y, g);
			}
		}
		
		if(this.floatRightLines != null) {
			for (int index = 0; index < this.floatRightLines.size(); index++) {
				linebox = this.floatRightLines.get(index);
				linebox.paint(x, y, g);
			}
		}
		
		if(this.bodyLines != null) {
			for (int index = 0; index < this.bodyLines.size(); index++) {
				linebox = this.bodyLines.get(index);
				linebox.paint(x, y, g);
			}
		}
	}
	
	public void partition(BlockContainingBlock block, PartitionList partitions) {
		PartitionList.partitionBlock(this, block, partitions);
	}
}
