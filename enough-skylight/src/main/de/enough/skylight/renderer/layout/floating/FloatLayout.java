package de.enough.skylight.renderer.layout.floating;

import de.enough.polish.ui.Item;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.layout.block.InlineLayout;
import de.enough.skylight.renderer.linebox.InlineLinebox;
import de.enough.skylight.renderer.linebox.InlineLineboxList;
import de.enough.skylight.renderer.partition.Partition;

public class FloatLayout {
	public static int ORIENTATION_LEFT = 0x00;
	
	public static int ORIENTATION_RIGHT = 0x01;
	
	FloatLineboxList leftLineboxes;
	
	FloatLinebox leftLinebox;
	
	FloatLineboxList rightLineboxes;
	
	FloatLinebox rightLinebox;
	
	int y = 0;
	
	public FloatLayout() {
		this.leftLineboxes = new FloatLineboxList();
		this.rightLineboxes = new FloatLineboxList();
	}
	
	public void addLeft(Partition partition, InlineLayout blockLayout) {
//		if(this.leftLinebox == null || !this.leftLinebox.fits(partition)) {
//			this.rightLineboxes.getLineboxInLine(relativeY);
//			
//			this.leftLinebox = new FloatLinebox(ORIENTATION_LEFT, block);
//			this.leftLineboxes.add(this.leftLinebox);
//		} else {
//			this.leftLinebox.add(partition);
//		}
	}
	
	public void addRight(Partition partition, InlineLayout blockLayout) {
		
	}
}
