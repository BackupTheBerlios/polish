package de.enough.skylight.renderer.element;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.benchmark.Benchmark;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.util.StringTokenizer;
import de.enough.polish.util.TextUtil;
import de.enough.skylight.renderer.linebox.LineBox;
import de.enough.skylight.renderer.partition.Partable;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;
import de.enough.skylight.renderer.partition.TextPartition;

public class TextBlock extends StringItem implements Partable {
	
	BlockContainingBlock block;
	
	PartitionList textPartitions;
	
	LineBox linebox;
	
	public TextBlock() {
		//#style text
		super(null,null);
		
		this.textPartitions = new PartitionList();
	}
	
	public void setText(String text) {
		text = prepareText(text);
		super.setText(text);
	}
	
	public String prepareText(String text) {
		text = text.trim();
		return TextUtil.replace(text, "\n", " ");
	}
	
	public void partition(BlockContainingBlock block, PartitionList partitions) {
		this.block = block;
		System.out.println("partition text " + this);
		this.textPartitions.clear();
		
		String text = getText();
		Font font = getFont();
		
		int left = PartitionList.getBlockRelativeX(this, block);
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
			
			this.textPartitions.add(new TextPartition(index, tokenLength, left,right, height,this));
			
			left += tokenWidth;
			index += token.length();
			
			if(st.hasMoreTokens()) {
				right = left + spaceWidth;
				Partition partition = new TextPartition(index, 1, left, right, height,this);
				partition.setWhitespace(true);
				this.textPartitions.add(partition);
				left += spaceWidth;
				index++;
			}
		}
		
		partitions.addAll(this.textPartitions);
	}
	
	public void drawString(String line, int x, int y, int anchor, Graphics g) {
		LineBox linebox = this.block.getPaintLineBox();
		PartitionList lineboxPartitions = linebox.getPartitions();
		
		TextPartition firstPartition = (TextPartition)this.textPartitions.get(0);
		for (int i = 0; i < this.textPartitions.size(); i++) {
			TextPartition textPartition = (TextPartition)this.textPartitions.get(i);
			if(lineboxPartitions.contains(textPartition) && !textPartition.isWhitespace()) {
				drawTextPartition(textPartition, firstPartition, linebox, this.text, x, y, anchor, g);
			}
		}
	}
	
	public void drawTextPartition(TextPartition partition, TextPartition first, LineBox linebox, String text, int x, int y, int anchor, Graphics g) {
		int index = partition.getIndex();
		int length = partition.getLength();
		
		int textXOffset = partition.getLeft() - first.getLeft();
		
		g.drawSubstring(text, index, length, x + textXOffset, y, anchor);
	}	
}
