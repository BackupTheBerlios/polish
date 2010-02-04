package de.enough.skylight.renderer.element;

import de.enough.polish.ui.StringItem;
import de.enough.skylight.renderer.partition.Partable;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class InputSubmitField extends StringItem implements Partable {
	
	public InputSubmitField() {
		super(null,"");
	}
	
	public String toString() {
		return "InputSubmitField [" + getText() + "]"; 
	}

	public void partition(PartitionList partitions) {
		Partition partition = Partition.partitionBlock(this);
		partitions.add(partition);
	}
}
