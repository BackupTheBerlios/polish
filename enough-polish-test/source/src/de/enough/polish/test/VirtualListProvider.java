package de.enough.polish.test;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.UiAccess;

public abstract class VirtualListProvider extends BaseListProvider {

	VirtualListView view;

	public VirtualListProvider(Container container, Screen screen,
			ItemCommandListener commandListener, int bufferSize) {
		super(container, screen, commandListener);

		this.view = new VirtualListView(this, bufferSize);
		
		initView();
	}

	/**
	 * Initializes the VirtualListView
	 */
	public void initView() {
		// set the view if its not set
		if (this.container.getView() != this.view) {
			//#debug
			System.out.println("setting view");

			this.container.setView(this.view);
		}
	}

	/**
	 * Updates the current selection
	 * @param doFocus
	 */
	public void update(boolean doFocus) {
		update(null, doFocus);
	}

	/**
	 * Updates the list with a selection fitting to the given scope
	 * @param scope the scope 
	 * @param doFocus true if a focus should be set otherwise false
	 */
	public void update(Object scope, boolean doFocus) {
		if (!this.container.isInitialized()) {
			UiAccess.init(this.screen);
		}

		VirtualListRange range = this.view.getRange();

		ListSelection selection;
		//#if polish.hasPointerEvents
		if(scope == null)
		{
			selection = select(range);
		} else 
		//#endif
		{
			selection = select(scope, range);
			range.setRange(selection.getStart(), selection.getEnd(), selection
					.getTotal());
			range.setOffset(this.view.getVisibleOffset());
		}
		
		apply(null, selection.getEntries(), scope, doFocus);
	}

	/**
	 * Returns sample data to calculate the reference height
	 * @return
	 */
	abstract Object getSampleData();

	/**
	 * Returns the total amount of entries 
	 * @return the total amount of entries
	 */
	protected abstract int total();

	/**
	 * Selects entries fitting to the given range
	 * @param range the range
	 * @return the resulting selection
	 */
	protected abstract ListSelection select(VirtualListRange range);

	/**
	 * Selects entries fitting to the given scope
	 * @param scope the scope
	 * @param range the range
	 * @return the resulting selection
	 */
	protected abstract ListSelection select(Object scope, VirtualListRange range);

	/**
	 * Notifies the container item if they are active or not
	 * 
	 * @param item
	 *            the item
	 * @param active
	 *            true if the item is active otherwise false
	 */
	protected abstract void notify(Item item, boolean active);

	/**
	 * Applies a search
	 * 
	 * @param search
	 *            the search string
	 */
	protected abstract void search(String search);
}
