package de.enough.polish.test;

import de.enough.polish.benchmark.Benchmark;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.ArrayList;

public abstract class BaseListProvider {
	
	ArrayList buffer;
	Container container;
	Screen screen;
	Object lock;
	ItemCommandListener commandListener;

	public BaseListProvider(Container container, Screen screen, ItemCommandListener commandListener) {
		this.buffer = new ArrayList();
		this.container = container;
		this.screen = screen;
		this.lock = screen.getPaintLock();
		this.commandListener = commandListener;
	}
	
	public Container getContainer() {
		return this.container;
	}

	abstract void updateItem(Item item, Object data);

	abstract Item createItem(Object data, ItemCommandListener commandListener);
	
	abstract Object getData(Item item);

	public synchronized void apply(Object collection, Object[] list, Object scope, boolean doFocus) {
		if (list != null && list.length > 0) {
			synchronized (this.lock) {
				scope = getScope(scope);
				
				this.container.focusChild(-1);
				int scopeIndex = applyListToBuffer(this.buffer, list, scope);
				int size = applyBufferToContainer(collection, this.buffer, list, scopeIndex, doFocus);
				trimContainer(this.container, size);
				
				UiAccess.init(this.screen);
			}
			
			trimBuffer(this.buffer, list.length);
		} else {
			clear();
		}
	}
	
	Object getScope(Object scope) {
		if(scope == null) {
			Item item = this.container.getFocusedItem();
			if(item != null) {
				return getData(item);
			}
		}
		
		return scope;
	}

	protected int applyListToBuffer(ArrayList buffer, Object[] list, Object scope) {
		int scopeIndex = -1; 
		for (int index = 0; index < list.length; index++) {
			Object data = list[index];

			Item item;
			if (index >= this.buffer.size()) {
				item = createItem(data, this.commandListener);
				buffer.add(item);
			} else {
				item = (Item) buffer.get(index);
				updateItem(item, data);
			}
			
			if(scope != null && data.equals(scope)) {
				scopeIndex = index;
			}
		}
		
		return scopeIndex;
	}

	protected int applyBufferToContainer(Object collection, ArrayList buffer, Object[] list, int scopeIndex, boolean doFocus) {
		for (int index = 0; index < list.length; index++) {
			Item item = (Item) buffer.get(index);
			applyItemToContainer(index, item);
		}
		
		setFocusToScope(scopeIndex, doFocus);
		
		return list.length;
	}
	
	protected void setFocusToScope(int scopeIndex, boolean doFocus) {
		if(doFocus) {
			this.container.focusChild(scopeIndex);
		} 
	}

	protected void applyItemToContainer(int index, Item item) {
		if (index >= container.size()) {
			this.container.add(item);
		} else {
			this.container.set(index, item);
		}
	}

	protected void trimBuffer(ArrayList buffer, int size) {
		while (buffer.size() > size) {
			Item item = (Item)buffer.remove(buffer.size() - 1);
			item.destroy();
		}
	}
	
	protected void clearBuffer(ArrayList buffer) {
		for (int i = 0; i < buffer.size(); i++) {
			Item item = (Item)buffer.get(i);
			item.destroy();
		}
		
		buffer.clear();
	}
	
	public void trimContainer(Container container, int size) {
		while (this.container.size() > size) {
			this.container.remove(this.container.size() - 1);
		}
	}

	public void clear() {
		synchronized (this.lock) {
			this.container.clear();
		}
		
		clearBuffer(this.buffer);
	}
}
