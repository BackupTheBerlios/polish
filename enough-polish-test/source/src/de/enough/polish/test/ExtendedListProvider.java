package de.enough.polish.test;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.Screen;
import de.enough.polish.util.ArrayList;

public abstract class ExtendedListProvider extends BaseListProvider {

	public ExtendedListProvider(Container container, Screen screen, ItemCommandListener commandListener) {
		super(container, screen, commandListener);
	}
	
	protected abstract Item getHeader(Object collection);
	
	protected abstract Item getTail(Object collection);
	
	protected abstract Item getSection(Object data);
	
	protected int applyBufferToContainer(Object collection, ArrayList buffer, Object[] list, int scopeIndex, boolean doFocus) {
		int additional = 0;
		
		Item header = getHeader(collection);
		if(header != null) {
			applyItemToContainer(0, header);
			additional++;
		}
		
		int index;
		for (index = 0; index < list.length; index++) {
			Item item = (Item) buffer.get(index);

			Object data = list[index];
			Item section = getSection(data);
			if (section != null) {
				applyItemToContainer(index + additional, section);
				additional++;
			}

			applyItemToContainer(index + additional, item);
			
			if(index == scopeIndex) {
				setFocusToScope(index + additional, doFocus);
			}
		}
		
		Item tail = getTail(collection);
		if(tail != null) {
			applyItemToContainer(index + additional, tail);
			additional++;
		}
		
		return list.length + additional;
	}

}
