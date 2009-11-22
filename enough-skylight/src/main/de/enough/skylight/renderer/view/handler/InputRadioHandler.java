package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.ChoiceItem;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextField;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.viewport.AttributeUtils;
import de.enough.skylight.renderer.viewport.ElementHandler;

public class InputRadioHandler extends ElementHandler{
	public void handleNode(Container parent, DomNode node) {
		String name = AttributeUtils.getValue(node, "name");
		
		ChoiceGroup group = (ChoiceGroup)getDirectory().get(name);
		
		if(group == null) {
			//#style input_radio_group
			group = new ChoiceGroup(null,ChoiceGroup.EXCLUSIVE);
			
			getDirectory().put(name, group);
			
			parent.add(group);
		}
		
		//#style input_radio
		ChoiceItem choice = new ChoiceItem("",null,ChoiceGroup.EXCLUSIVE);
		getDirectory().put(node, choice);
		
		group.add(choice);
	}

	public void handleText(Container parent, String text, Style style, DomNode parentNode) {
		ChoiceItem choice = (ChoiceItem)getDirectory().get(parentNode);
		choice.setText(text);
	}
}
