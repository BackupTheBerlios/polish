package de.enough.skylight.renderer.element;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.util.StringTokenizer;
import de.enough.polish.util.TextUtil;
import de.enough.skylight.renderer.partition.Partable;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;
import de.enough.skylight.renderer.partition.TextPartition;

public class TextBlock extends StringItem implements Partable {

	public TextBlock() {
		//#style text
		super(null,null);
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
			
			partitions.add(new TextPartition(index, tokenLength, left,right, height,this));
			
			left += tokenWidth;
			index += token.length();
			
			if(st.hasMoreTokens()) {
				right = left + spaceWidth;
				Partition partition = new Partition(left, right, height,this);
				partition.setWhitespace(true);
				partitions.add(partition);
				left += spaceWidth;
				index++;
			}
		}
	}

	public void paintContent(int x, int y, int leftBorder, int rightBorder,
			Graphics g) {
		super.paintContent(x, y, leftBorder, rightBorder, g);
	}
	
	
	
	
	
	
	
}
