package de.enough.skylight.renderer.element;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemStateListener;
import de.enough.polish.ui.TextField;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.element.view.ContentView;
import de.enough.skylight.renderer.node.CssElement;

public class InputTextField extends TextField implements ItemStateListener {
	
	CssElement cssElement;
	
	public InputTextField() {
		super(null,"", 512, TextField.ANY);
		
		setItemStateListener(this);
	}
	
	protected void initContent(int firstLineWidth, int availWidth,
			int availHeight) {
		super.initContent(firstLineWidth, availWidth, availHeight);
		LayoutDescriptor layoutDescriptor = ContentView.getLayoutDescriptor(this);
		this.cssElement = layoutDescriptor.getCssElement();
	}

	public void itemStateChanged(Item item) {
		DomNode node = this.cssElement.getNode();
		String newText = getText();
		//#debug
		System.out.println("Item changed to new text '"+newText+"'");
		node.setNodeValue(newText);
	}
		
	public String toString() {
		return "InputTextField [" + this.getText() + "]"; 
	}	
}
