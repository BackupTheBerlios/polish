package de.enough.skylight.renderer.element;

import de.enough.polish.ui.TextField;

public class InputPasswordField extends InputTextField{
	
	public InputPasswordField() {
		super();
		
		setConstraints(TextField.PASSWORD);
	}
	
	public String toString() {
		return "InputPasswordField [" + this.getText() + "]"; 
	}
}
