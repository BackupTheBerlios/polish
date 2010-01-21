package de.enough.skylight.renderer.element;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.builder.Element;
import de.enough.skylight.renderer.linebox.LineBox;
import de.enough.skylight.renderer.linebox.LineBoxFactory;
import de.enough.skylight.renderer.linebox.LineBoxList;
import de.enough.skylight.renderer.partition.Partable;
import de.enough.skylight.renderer.partition.PartitionList;

public class BlockContainingBlock extends Container implements ContainingBlock, Partable {

	LineBoxList lineboxes;
	
	protected InlineContainingBlock body;

	protected InlineContainingBlock floatLeft;
	
	protected InlineContainingBlock floatRight;
	
	Element element;
	
	public BlockContainingBlock(Element element) {
		this(element,element.getStyle());
	}
	
	public BlockContainingBlock(Element element, Style style) {
		super(false,style);
		
		this.element = element;
		
		//#style element
		this.body = new InlineContainingBlock(null, this);

		//#style element
		this.floatLeft = new InlineContainingBlock(null, this);
		
		//#style element
		this.floatRight = new InlineContainingBlock(null, this);
		
		add(this.body);
		add(this.floatLeft);
	}
	
	protected int getContentHeight(LineBoxList lineboxes) {
		int height = 0;
		for (int index = 0; index < lineboxes.size(); index++) {
			LineBox linebox = lineboxes.get(index);
			height += linebox.getHeight();
		}

		return height;
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
		this.floatLeft.add(item);
	}
	
	public void addToRightFloat(Item item) {
		this.floatRight.add(item);
	}
	
	public Element getElement() {
		return this.element;
	}
	
	protected void initContent(int firstLineWidth, int availWidth,
			int availHeight) {
		super.initContent(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		PartitionList partitionList = new PartitionList();
		
		this.body.partition(this, partitionList);
		
		partitionList.sort();
		
//		System.out.println("partitions for " + this + ":");
//		for (int i = 0; i < partitionList.size(); i++) {
//			System.out.println(partitionList.get(i));
//		}

		this.lineboxes = LineBoxFactory.getLineBoxes(partitionList, availWidth);

//		System.out.println("lineboxes for " + this + ":");
//		for (int i = 0; i < this.lineboxes.size(); i++) {
//			System.out.println(this.lineboxes.get(i));
//		}

		if(this.element != null && this.element.isParentFloat()) {
			this.contentWidth = getContentWidth(this.lineboxes);
		} else {
			this.contentWidth = availWidth;
		}
		this.contentHeight = getContentHeight(this.lineboxes);
	}
	
	protected void paintContent(int x, int y, int leftBorder, int rightBorder,
			Graphics g) {
		this.floatLeft.paint(x, y, leftBorder, rightBorder, g);
		for (int index = 0; index < this.lineboxes.size(); index++) {
			LineBox linebox = this.lineboxes.get(index);

			paintLinebox(x, y, linebox, this.body, g);

			int lineboxHeight = linebox.getHeight();
			y += lineboxHeight;
		}
	}

	protected void paintLinebox(int x, int y, LineBox linebox, InlineContainingBlock block, Graphics g) {
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();

		int left = linebox.getTrimmedLeft();
		int width = linebox.getTrimmedWidth();
		int height = linebox.getHeight();

		g.clipRect(x, y, width, height);

		int leftBorder = x - left;
		int rightBorder = (x + this.itemWidth) - left;
		
		block.paint(x - left, y, leftBorder, rightBorder, g);

		g.setClip(clipX, clipY, clipWidth, clipHeight);
	}
	
	public void partition(BlockContainingBlock block, PartitionList partitions) {
		PartitionList.partitionBlock(this, block, partitions);
	}

}
