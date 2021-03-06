package de.enough.skylight.renderer.element;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.util.StringTokenizer;
import de.enough.polish.util.TextUtil;
import de.enough.skylight.renderer.element.view.ContentView;
import de.enough.skylight.renderer.linebox.InlineLinebox;
import de.enough.skylight.renderer.partition.Partable;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;
import de.enough.skylight.renderer.partition.TextPartition;

public class TextBlock extends StringItem implements Partable {
	
	PartitionList textPartitions;
	
	LayoutDescriptor layoutDescriptor;
	
	public TextBlock() {
		//#style text
		super(null,null);
		
		this.textPartitions = new PartitionList();
	}
	
	public TextBlock(Style style) {
		super(null,null, style);
		
		this.textPartitions = new PartitionList();
	}
	
	public void setText(String text) {
		text = prepareText(text);
		super.setText(text);
	}
	
	public String prepareText(String text) {
		String result = text.trim();
		result = TextUtil.replace(result, "\n", " ");
		return result;
	}
	
	protected void initContent(int firstLineWidth, int availWidth,
			int availHeight) {
		super.initContent(firstLineWidth, availWidth, availHeight);
		this.layoutDescriptor = ContentView.getLayoutDescriptor(this);
	}

	public void partition(PartitionList partitions) {
		this.textPartitions.clear();
		
		PartitionList itemPartitions = this.layoutDescriptor.getPartitions();
		itemPartitions.clear();
		
		String text = getText();
		Font font = getFont();
		
		int left = this.layoutDescriptor.getInlineRelativeOffset();
		int right;
		int height = font.getHeight();
		int tokenWidth;
		int tokenLength;
		
		int spaceWidth = font.charWidth(' ');
		
		StringTokenizer st = new StringTokenizer(text, " \r\n\t");
		
		int index = 0;
		
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			
			tokenWidth = font.stringWidth(token);
			tokenLength = token.length();
			right = left + tokenWidth;
			
			TextPartition textPartition = TextPartition.createText(index, tokenLength, left,right, height,this);
			this.textPartitions.add(textPartition);
			
			left += tokenWidth;
			index += token.length();
			
			if(st.hasMoreTokens()) {
				right = left + spaceWidth;
				Partition whiteSpacePartition = TextPartition.createWhiteSpace(index, left, right, height, this);
				whiteSpacePartition.setAttribute(Partition.ATTRIBUTE_WHITESPACE);
				this.textPartitions.add(whiteSpacePartition);
				left += spaceWidth;
				index++;
			}
		}
		
		itemPartitions.addAll(this.textPartitions);
		partitions.addAll(this.textPartitions);
	}
	
	public void drawString(String line, int x, int y, int anchor, Graphics g) {
		InlineLinebox linebox = this.layoutDescriptor.getBlock().getPaintLineBox();
		PartitionList lineboxPartitions = linebox.getPartitions();
		
		TextPartition firstPartition = (TextPartition)this.textPartitions.get(0);
		for (int i = 0; i < this.textPartitions.size(); i++) {
			TextPartition textPartition = (TextPartition)this.textPartitions.get(i);
			if(lineboxPartitions.contains(textPartition) && !textPartition.hasAttribute(Partition.ATTRIBUTE_WHITESPACE)) {
				drawTextPartition(textPartition, firstPartition, linebox, this.text, x, y, anchor, g);
			}
		}
	}
	
	public void drawTextPartition(TextPartition partition, TextPartition first, InlineLinebox linebox, String text, int x, int y, int anchor, Graphics g) {
		int index = partition.getIndex();
		int length = partition.getLength();
		
		int textXOffset = partition.getInlineRelativeLeft() - first.getInlineRelativeLeft();
		
		//#debug sl.debug.render
		System.out.println("drawing substring : \"" + text.substring(index, index + length) + "\" at " + (x + textXOffset) + "/" + y);
		
		g.drawSubstring(text, index, length, x + textXOffset, y, anchor);
	}	
	
	public String toString() {
		return "TextBlock [" + this.text + "]"; 
	}
}
