package de.enough.skylight.renderer.element;

import de.enough.polish.ui.TextField;
import de.enough.skylight.renderer.partition.Partable;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class InputPasswordField extends InputTextField {
	
	public InputPasswordField() {
		super();
		
		setConstraints(TextField.PASSWORD);
	}
	
	public String toString() {
		return "InputPasswordField [" + this.getText() + "]"; 
	}
}
