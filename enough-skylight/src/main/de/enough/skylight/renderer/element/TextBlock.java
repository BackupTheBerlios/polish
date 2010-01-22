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
		
		int spaceWidth = font.charWidth(' ');
		
		StringTokenizer st = new StringTokenizer(text, " \r\n\t");
		
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			
			tokenWidth = font.stringWidth(token);
			right = left + tokenWidth;
			
			partitions.add(new Partition(left,right, height,this));
			
			left += tokenWidth;
			
			if(st.hasMoreTokens()) {
				right = left + spaceWidth;
				Partition partition = new Partition(left, right, height,this);
				partition.setWhitespace(true);
				partitions.add(partition);
				left += spaceWidth;
			}
		}
	}
}
