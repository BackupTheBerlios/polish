package de.enough.polish.test;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.UiAccess;

public abstract class VirtualListProvider extends BaseListProvider{

	VirtualListView view;
	
	public VirtualListProvider(Container container, Screen screen, ItemCommandListener commandListener) {
		super(container, screen, commandListener);
		
		this.view = new VirtualListView(this);
		
		initView();
	}
	
	abstract Object getSampleData();
	
	public void initView() {
		// set the view if its not set
		if (this.container.getView() != this.view) {
			//#debug
			System.out.println("setting view");

			this.container.setView(this.view);
		}
	}
	
	public void update(boolean doFocus) {
		update(null, doFocus);
	}
	
	public void update(Object scope, boolean doFocus) {
		if(!this.container.isInitialized()) {
			UiAccess.init(this.screen);
		}
		
		VirtualListRange range = this.view.getRange();
		
		ListSelection selection = select(scope, range);
		
		range.setRange(selection.getStart(), selection.getEnd(), selection.getTotal());
		range.setOffset(this.view.getOffset());
		
		apply(null,selection.getEntries(),scope,doFocus);
	}
	
	protected abstract int total();
	
	protected abstract ListSelection select(VirtualListRange range);
	
	protected abstract ListSelection select(Object scope, VirtualListRange range);
}
