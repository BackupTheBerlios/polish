package de.enough.polish.test;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.StringItem;

public class TestProvider extends VirtualListProvider{
	
	String[] data = new String[100];

	public TestProvider(Container container, Screen screen) {
		super(container, screen,null);
		    	
    	for (int i = 0; i < data.length; i++) {
    		data[i] = "entry " + i;
		}
	}
	
	public String[] getData() {
		return data;
	}
		
	Item createItem(Object data, ItemCommandListener commandListener) {
		String text = (String)data;
		//#style entry
		StringItem textItem = new StringItem(null,text);
		textItem.setAppearanceMode(Item.INTERACTIVE);
		return textItem;
	}

	void updateItem(Item item, Object data) {
		String text = (String)data;
		StringItem textItem = (StringItem)item;
		textItem.setText(text);
	}
		
	Object getData(Item item) {
		StringItem textItem = (StringItem)item;
		return textItem.getText();
	}
	
	Object getSampleData() {
		return "entry 666";
	}
	
	protected int total() {
		return 100;
	}

	protected ListSelection select(VirtualListRange range) {
		return new ListSelection(this.data, range.getStart(), range.getEnd(), range.getTotal());
	}
	
	protected ListSelection select(Object scope, VirtualListRange range) {
		ListSelection contacts = null;
		
		int mid = getIndex(scope);
		int number = range.getRange();

		int	from = (mid == -1) ? 0 : (mid - (number - 1) / 2);
		if (from < 0)
		{
			from = 0;
		}
		int to = (mid == -1) ? (number - 1) : (mid + number / 2);
		if (to > data.length - 1)
		{
			to = data.length - 1;
		}
		
		contacts = new ListSelection(data, from, to, data.length);
		
		return contacts;
	}
	
	public int getIndex(Object scope) {
		for (int i = 0; i < data.length; i++) {
			if(data[i] == scope) {
				return i;
			}
		}
		
		return -1;
	}
}
