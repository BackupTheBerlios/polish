package de.enough.polish.test;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.UiAccess;

public abstract class VirtualListProvider extends BaseListProvider{

	VirtualListView view;
	
	public VirtualListProvider(Container container, Screen screen, ItemCommandListener commandListener, int bufferSize) {
		super(container, screen, commandListener);
		
		this.view = new VirtualListView(this,bufferSize);
		
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
		
		//#if polish.hasPointerEvents
		//# ListSelection selection = select(range);
		//# apply(null,selection.getEntries(),null,doFocus);
		//#else
			ListSelection selection = select(scope, range);
			range.setRange(selection.getStart(), selection.getEnd(), selection.getTotal());
			range.setOffset(this.view.getOffset());
			apply(null,selection.getEntries(),scope,doFocus);
		//#endif
	}
	
	protected abstract int total();
	
	protected abstract ListSelection select(VirtualListRange range);
	
	protected abstract ListSelection select(Object scope, VirtualListRange range);
}
