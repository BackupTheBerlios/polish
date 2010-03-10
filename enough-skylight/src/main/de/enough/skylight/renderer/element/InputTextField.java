package de.enough.skylight.renderer.element;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemStateListener;
import de.enough.polish.ui.TextField;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.element.view.LayoutAttributes;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.partition.Partable;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class InputTextField extends TextField implements Partable, ItemStateListener {
	
	public InputTextField() {
		super(null,"", 512, TextField.ANY);
		
		setItemStateListener(this);
	}
	
	public String toString() {
		return "InputTextField [" + this.getText() + "]"; 
	}

	public void partition(PartitionList partitions) {
		Partition partition = Partition.partitionBlock(this);
		partitions.add(partition);
	}

	public void itemStateChanged(Item item) {
		CssElement element = LayoutAttributes.getCssElement(this);
		DomNode node = element.getNode();
		String newText = getText();
		//#debug
		System.out.println("Item changed to new text '"+newText+"'");
		node.setNodeValue(newText);
	}
	
	
}
