package de.enough.skylight.renderer.layout.floating;

import de.enough.polish.ui.Item;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.linebox.InlineLinebox;
import de.enough.skylight.renderer.linebox.InlineLineboxList;
import de.enough.skylight.renderer.partition.Partition;

public class FloatLayout {
	public static int ORIENTATION_LEFT = 0x00;
	
	public static int ORIENTATION_RIGHT = 0x01;
	
	FloatLineboxList leftLineboxes;
	
	FloatLinebox leftLinebox;
	
	FloatLineboxList rightLineboxes;
	
	FloatLinebox currentRightLinebox;
	
	public FloatLayout() {
		this.leftLineboxes = new FloatLineboxList();
		this.rightLineboxes = new FloatLineboxList();
	}
	
	public void addLeft(Item item, BlockContainingBlock block) {
		if(this.leftLineboxes == null) {
			this.leftLineboxes = new FloatLineboxList();
		}
		
		//TODO get correct y offset
		int relativeY = 0; 
		
		FloatLinebox linebox = this.leftLineboxes.getLineboxByBlock(block);
		
		if(linebox == null) {
			FloatLinebox leftLineBoxInLine = this.leftLineboxes.getLineboxInLine(relativeY);
			
		}

		
		FloatLinebox rightLineBoxInLine = this.rightLineboxes.getLineboxInLine(relativeY);
	}
}
