package de.enough.skylight.renderer.element;

import de.enough.polish.ui.TextField;
import de.enough.skylight.renderer.partition.Partable;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class InputTextField extends TextField implements Partable {
	
	public InputTextField() {
		super(null,"", 512, TextField.ANY);
	}
	
	public String toString() {
		return "InputTextField [" + this.getText() + "]"; 
	}

	public void partition(PartitionList partitions) {
		Partition partition = Partition.partitionBlock(this);
		partitions.add(partition);
	}
}
