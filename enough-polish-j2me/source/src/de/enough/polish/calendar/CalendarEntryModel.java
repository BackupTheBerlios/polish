/**
 * 
 */
package de.enough.polish.calendar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.TimeZone;

import de.enough.polish.io.Externalizable;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Arrays;
import de.enough.polish.util.Comparator;
import de.enough.polish.util.IdentityArrayList;
import de.enough.polish.util.IdentityHashMap;
import de.enough.polish.util.Iterator;
import de.enough.polish.util.LongHashMap;
import de.enough.polish.util.TimePeriod;
import de.enough.polish.util.TimePoint;
/**
 * Manages several calendar entries and categories.
 * 
 * @author Robert Virkus
 *
 */
public class CalendarEntryModel 
implements Externalizable, CalendarSubject 
{
	private static final int VERSION = 100;
	private TimeZone timeZone; 
	private IdentityHashMap calendarEntriesByCategory;
	private final IdentityArrayList rootCategories;
	private IdentityArrayList listeners;
	private boolean isDirty;
	private Comparator rootCategoriesComparator;
	private CalendarEntry recentChangedEntry;
	private CalendarCategory recentChangedCategory;
	
	private ArrayList observers = new ArrayList();
	
	
	/**
	 * Default constructor initializes calendar entries
	*/
	public CalendarEntryModel() {
		this.calendarEntriesByCategory = new IdentityHashMap();
		this.rootCategories = new IdentityArrayList();
	}
	
	
	/**
	 * Adds an entry to this model.
	 * If the corresponding category has not been added before, it will be added implicitly.
	 * @param entry the calendar entry
	 */
	public void addEntry(CalendarEntry entry) {
		addEntry( entry, false );
	}
	
	
	/**
	 * Adds an entry to this model.
	 * If the corresponding category has not been added before, it will be added implicitly.
	 * @param entry the calendar entry
	 * @param notifyObserver Notifies the observer if true.
	 */
	public void addEntry(CalendarEntry entry, boolean notifyObserver) {
		//#debug
		System.out.println("adding entry " + entry.getSummary());
		CalendarCategory category = entry.getCategory();
		CalendarEntryList list = (CalendarEntryList) this.calendarEntriesByCategory.get(category);
		if (list == null) {
			list = addCategory(category, false);
		}
		list.add(entry);
		this.isDirty = true;
		if (notifyObserver) {
			notifyObservers();
		}
	}

	/**
	 * Updates an event by first removing it and than adding it back again.
	 * @param entry The event to update.
	 */
	public void updateEntry(CalendarEntry entry) {
		updateEntry( entry, false );
	}

	
	/**
	 * Updates an event by first removing it and than adding it back again.
	 * @param entry The event to update.
	 * @param notifyObserver Notifies the observer if true.
	 */
	public void updateEntry(CalendarEntry entry, boolean notifyObserver) {
		removeEntry(entry);
		addEntry(entry);
		if (notifyObserver) {
			this.recentChangedEntry = entry;
			notifyObservers();
		}
	}
	
	
	
	/**
	 * Adds an (empty) category to this model.
	 * Any root category are added implicitly.
	 * @param category the category
	 * @return the empty list of events of the added category
	 */
	public CalendarEntryList addCategory( CalendarCategory category ) {
		return addCategory( category, false );
	}
	
	
	/**
	 * Adds an (empty) category to this model.
	 * Any root category are added implicitly.
	 * @param category the category.
	 * @param notifyObserver notifyObserver Notifies the observer if true.
	 * @return the empty list of events of the added category
	 */
	public CalendarEntryList addCategory(CalendarCategory category, boolean notifyObserver) {
		CalendarEntryList list = (CalendarEntryList) this.calendarEntriesByCategory.get(category);
		if (list == null) {
			CalendarCategory parent = category;
			while (parent.getParentCategory() != null) {
				parent = parent.getParentCategory();
			}
			if (!this.rootCategories.contains(parent)) {
				this.rootCategories.add(parent);
			}
			list = new CalendarEntryList();
			this.calendarEntriesByCategory.put(category, list);
			this.isDirty = true;		
			if (notifyObserver) {
				this.recentChangedCategory = category;
				notifyObservers();
			}
		}
		
		return list;
	}
	
	
	/**
	 * Removes a calendar entry.
	 * @param entry The event that should be removed
	 */
	public void removeEntry( CalendarEntry entry ) {
		CalendarCategory category = entry.getCategory();
		CalendarEntryList list = (CalendarEntryList) this.calendarEntriesByCategory.get(category);
		if (list != null) {
			list.remove(entry);
		}
	}
	
	
	/**
	 * Removes a calendar entry.
	 * When there are no other events/entries left, the corresponding parent categories will also be removed.
	 * @param entry The event that should be removed
	 */
	public void removeEntry(CalendarEntry entry, boolean removeEmptyCategory) {
		
		if (removeEmptyCategory) {
			CalendarCategory category = entry.getCategory();
			CalendarEntryList list = (CalendarEntryList) this.calendarEntriesByCategory.get(category);
			if (list != null) {
				list.remove(entry);
				if (list.size() == 0) {
					// remove category:
					this.calendarEntriesByCategory.remove(category);
					// check if root category should also be removed:
					boolean noEntriesFound = true;
					while (category.getParentCategory() != null && noEntriesFound) {
						category = category.getParentCategory();
						CalendarCategory[] children = category.getChildCategories();
						for (int i = 0; i < children.length; i++) {
							CalendarCategory child = children[i];
							if (this.calendarEntriesByCategory.get(child) != null) {
								noEntriesFound = false;
								break;
							}
						}
					}
					if (noEntriesFound) {
						this.rootCategories.remove(category);
					}
				}
			}
		} else {	// Do not remove empty categories;
			removeEntry(entry);
		}
	}
	
	
	/**
	 * Removes a calendar entry.
	 * When there are no other events/entries left, the corresponding parent categories will also be removed.
	 * @param entry the event that should be removed
	 */
	public void removeEntry(CalendarEntry entry, boolean removeEmptyCategory, boolean notifyObserver) {
		
		removeEntry(entry, removeEmptyCategory);
		
		if (notifyObserver) {
			notifyObservers();
		}
	}
	
	
	/**
	 * Adds all entries of the given model to this model.
	 * @param model The model of which entries should be copied to this model.
	 */
	public void addEntries(CalendarEntryModel model) {
		
		addEntries(model, false);
	}
	
	/**
	 * Adds all entries of the given model to this model.
	 * @param model The model of which entries should be copied to this model.
	 * @param notifyObserver Notifies the observer if true.
	 */
	public void addEntries(CalendarEntryModel model, boolean notifyObserver) {
	
		addEntries(model, notifyObserver, false);
	}
	
	
	/**
	 * Adds all entries of the given model to this model.
	 * @param model The model of which entries should be copied to this model.
	 * @param notifyObserver Notifies the observer if true.
	 * @param forEachEvent Notifies the observer every time an event is added if true.  
	 */
	public void addEntries(CalendarEntryModel model, boolean notifyObserver, boolean forEachEvent) {
		
		IdentityHashMap entriesByCategory = model.calendarEntriesByCategory;
		Iterator it = entriesByCategory.keysIterator();
		while (it.hasNext()) {
			CalendarCategory category = (CalendarCategory) it.next();
			// the same category might be used with different CalendarCategory instances in this model and the given model,
			// so use the GUID of the CalendarCategory instance to merge them:
			CalendarCategory original = getCategory( category.getGuid() );
			CalendarEntryList originalList = null;
			if (original != null) {
				originalList = (CalendarEntryList) this.calendarEntriesByCategory.get(original);
			}
			CalendarEntryList entriesList = (CalendarEntryList) entriesByCategory.get(category);
			Object[] entries = entriesList.getInternalArray();
			for (int i = 0; i < entries.length; i++) {
				CalendarEntry entry = (CalendarEntry) entries[i];
				if (entry == null) {
					if (i == 0 && original == null) {	// Adding a category even if it has no entries.
						addCategory(category);
					}
					break;
				}
				if (original != null) {
					entry.setCategory(original);
				}
				// check if the original does not already contain this entry (compare by GUID), in case the user has 
				// edited a global entry from entries.xml:
				if (originalList == null || !originalList.containsEntryWithGuid( entry.getGuid() )) {
					addEntry(entry, forEachEvent);
				}
			}
		}
		
		if (notifyObserver && !forEachEvent) {
			notifyObservers();
		}
	}
	
	
	/**
	 * Retrieves a category by its global unique ID
	 * @param guid the ID of the category
	 * @return the found category or null if no matching category has been found
	 * @see CalendarCategory#getGuid()
	 */
	public CalendarCategory getCategory(long guid) {
		for (int i=0; i<this.rootCategories.size(); i++) {
			CalendarCategory root = (CalendarCategory) this.rootCategories.get(i);
			CalendarCategory found = getCategory( guid, root );
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	private CalendarCategory getCategory(long guid, CalendarCategory root) {
		if (guid == root.getGuid()) {
			return root;
		}
		Object[] children = root.getChildCategoriesAsInternalArray();
		for (int i = 0; i < children.length; i++) {
			CalendarCategory child = (CalendarCategory) children[i];
			if (child == null) {
				break;
			}
			CalendarCategory found = getCategory( guid, child );
			if (found != null) {
				return found;
			}
		}
		return null;
	}
	
	
	/**
	 * Removes a category if it has no child categories. Note, this will also remove all entries in this category.
	 * @param category The category which will be removed.
	 */
	public void removeCategory(CalendarCategory category) {
		
		removeCategory(category, false);
	}
	
	
	/**
	 * Removes a category if it has no child categories. Note, this will also remove all entries in this category.
	 * @param category The category which will be removed.
	 * @param notifyObserver Notifies the observer if true.
	 * @throws IllegalStateException when this category has children categories (those would need to be removed first) 
	 */
	public void removeCategory(CalendarCategory category, boolean notifyObserver) {
		
		if ( category.hasChildCategories() ) {
			//#debug info
			System.out.println("Cannot delete category since it contains child categories. Can only delete categories on the lowest level.");
			throw new IllegalStateException();
		}
		
		// Removing all entries from the category before removing the category itself.
		CalendarEntryList entryList = getEntries(category);
		CalendarEntry[] entries = entryList.getEntries();
		for (int i = 0; i < entries.length; i++) {
			CalendarEntry entryToRemove = entries[i];
			removeEntry(entryToRemove);
		}
		// Removing the category.
		CalendarCategory parentCategory = category.getParentCategory();
		if (parentCategory != null) {
			ArrayList childCategoryList = parentCategory.getChildCategoriesAsList();
			childCategoryList.remove(category);
			parentCategory.setChildCategories(childCategoryList);
			
			if (notifyObserver) {
				this.recentChangedCategory = parentCategory;
				notifyObservers();
			} else {
				// Do nothing;
			}
		} else {
			//#debug info
			System.out.println("Cannot delete category since it is a root category.");
		}
	}

	/**
	* sets the calendar Time Zone
	* @param timeZone Calendar Time Zone
	*/
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}
	
	/**
	* gets the calendar Time Zone
	* @return Calendar Time Zone
	*/
	public TimeZone getTimeZone() {
		return this.timeZone;
	}
	
//   /**
//	* Adds a listener to this model
//	* @param listener the CalendarListener that is added
//	*/
//	public void addListener(CalendarListener listener) {
//		if (this.listeners == null) {
//			this.listeners = new IdentityArrayList();
//		}
//		this.listeners.add( listener );
//	}
//	
//	/**
//	 * Removes a listener from this model
//	 * @param listener the listener that should be removed
//	 */
//	public void removeListener( CalendarListener listener ) {
//		if (this.listeners != null) {
//			this.listeners.remove(listener);
//		}
//	}
	
	/**
	 * Enables the specified category (and its parent categories).
	 * @param category the category that should be enabled
	 */
	public void enableCategory(CalendarCategory category) {
		this.isDirty = category.setEnabled( true );
	}
	
	/**
	 * Disables the specified category (and possibly its parent categories).
	 * @param category the category that should be disabled
	 */
	public void disableCategory(CalendarCategory category) {
		this.isDirty = category.setEnabled(false);
	}

	/**
	 * Enables all categories that have the specified name
	 * 
	 * @param name the name of the category
	 * @return the number of categories that fit the specified name
	 */
	public int enableCategoryByName(String name) {
		return enableCategoryByName( name, true, false );
	}
	
	/**
	 * Disables all categories that have the specified name
	 * 
	 * @param name the name of the category
	 * @return the number of categories that fit the specified name
	 */
	public int disableCategoryByName(String name) {
		return enableCategoryByName( name, false, false );
	}
	
	/**
	 * Enables all categories that have the specified ID
	 * 
	 * @param name the ID of the category
	 * @return the number of categories that fit the specified ID
	 */
	public int enableCategoryById(String name) {
		return enableCategoryByName( name, true, true );
	}
	
	/**
	 * Disables all categories that have the specified ID
	 * 
	 * @param name the ID of the category
	 * @return the number of categories that fit the specified ID
	 */
	public int disableCategoryById(String name) {
		return enableCategoryByName( name, false, true );
	}
	
	private int enableCategoryByName(String name, boolean enable, boolean useId) {
		int matchingCategories = 0;
		for (int i=0; i<this.rootCategories.size(); i++) {
			CalendarCategory category = (CalendarCategory) this.rootCategories.get(i);
			matchingCategories +=  enableCategoryByName( category, name, enable, useId );
		}
		return matchingCategories;
	}


	private int enableCategoryByName(CalendarCategory category, String name, boolean enable, boolean useId) {
		int matchingCategories = 0;
		if (!useId && name.equals(category.getName())) {
			category.setEnabled(enable);
			matchingCategories++;
		} else if (useId && name.equals(category.getId())) {
			category.setEnabled(enable);
			matchingCategories++;
		}
		Object[] children = category.getChildCategoriesAsInternalArray();
		for (int i = 0; i < children.length; i++) {
			CalendarCategory  child = (CalendarCategory) children[i];
			if (child == null) {
				break;
			}
			matchingCategories += enableCategoryByName(child, name, enable, useId);
		}
		return matchingCategories;
	}

	/**
	 * Determines whether this model has been changed
	 * @return true when this model has been changed since either the creation or since the last reset of the dirty flag
	 * @see #resetDirtyFlag()
	 */
	public boolean isDirty() {
		return this.isDirty;
	}
	
	/**
	 * Sets the dirty state of this model to false.
	 */
	public void resetDirtyFlag() {
		this.isDirty = false;
	}
	
	/**
	 * Retrieves all root categories.
	 * If you want to sort the root categories please register a comparator with setRootCategiriesComparator().
	 * 
	 * @return an array root categories, can be empty but not null.
	 * @see #getEnabledRootCategories()
	 * @see #setRootCategoriesComparator(Comparator)
	 */
	public CalendarCategory[] getRootCategories() {
		CalendarCategory[] roots = (CalendarCategory[]) this.rootCategories.toArray(new CalendarCategory[ this.rootCategories.size()]);
		if (this.rootCategoriesComparator != null) {
			Arrays.sort(roots, this.rootCategoriesComparator);
		}
		return roots;
	}

	
	/**
	 * Retrieves all enabled root categories.
	 * If you want to sort the root categories please register a comparator with setRootCategiriesComparator().
	 * 
	 * @return an array of active root categories, can be empty but not null.
	 * @see #getRootCategories()
	 * @see #setRootCategoriesComparator(Comparator)
	 */
	public CalendarCategory[] getEnabledRootCategories() {
		IdentityArrayList list = new IdentityArrayList();
		for (int i=0; i<this.rootCategories.size(); i++) {
			CalendarCategory category = (CalendarCategory) this.rootCategories.get(i);
			if (category.isEnabled()) {
				list.add( category );
			}
		}
		CalendarCategory[] roots = (CalendarCategory[]) list.toArray( new CalendarCategory[ list.size() ]);
		if (this.rootCategoriesComparator != null) {
			Arrays.sort(roots, this.rootCategoriesComparator);
		}
		return roots;
	}
	
	/**
	 * Sets a comparator for sorting the root categories whenever calling getRootCategories or getEnabeldRootCategories.
	 * @param comparator the sorting comparator, null to deactive sorting
	 * @see #getRootCategories()
	 * @see #getEnabledRootCategories()
	 */
	public void setRootCategoriesComparator( Comparator comparator ) {
		this.rootCategoriesComparator = comparator;
	}
	
	/**
	 * Retrieves all calendar entries between the given start and end time
	 * @param period the time period
	 * @return any events that start or end within the given times
	 * @see #getEnabledEntries(TimePeriod)
	 */
	public CalendarEntryList getEntries( TimePeriod period ) {
		return getEntries( period, false );
	}
	
	/**
	 * Retrieves all calendar entries between the given start and end time that belong to enabled categories
	 * @param period the time period
	 * @return any events that start or end within the given times and that belong to an enabled category
	 * @see #getEntries(TimePeriod)
	 */
	public CalendarEntryList getEnabledEntries( TimePeriod period ) {
		return getEntries( period, true );
	}
	
	/**
	 * Retrieves all calendar entries for the specified period and category
	 * @param category the category
	 * @param period the time period
	 * @return all events that start or end within the given times and that belong to the specified category
	 */
	public CalendarEntryList getEntries(CalendarCategory category, TimePeriod period) {
		TimePeriod nextDatePeriod = new TimePeriod( period );
		CalendarEntryList list = new CalendarEntryList();
		getEntries( category, period, nextDatePeriod, list );
		return list;
	}


	private CalendarEntryList getEntries(TimePeriod period, boolean filterByEnabledCategories) {
		CalendarCategory[] categories = (CalendarCategory[]) this.calendarEntriesByCategory.keys( new CalendarCategory[ this.calendarEntriesByCategory.size() ] );
		TimePeriod nextDatePeriod = new TimePeriod( period );
		CalendarEntryList list = new CalendarEntryList();
//		Date dateStart = new Date( period.getStart() );
//		Calendar calStart = Calendar.getInstance();
//		Calendar calNextDate = Calendar.getInstance();
		for (int i = 0; i < categories.length; i++) {
			CalendarCategory category = categories[i];
			if (!filterByEnabledCategories || category.isEnabled()) {
				getEntries( category, period, nextDatePeriod, list );

			}
		}
		return list;
	}
	
	private void getEntries(CalendarCategory category, TimePeriod period, TimePeriod nextDatePeriod, CalendarEntryList list) 
	{
		CalendarEntryList calendarEntryList = (CalendarEntryList) this.calendarEntriesByCategory.get(category);
		if (calendarEntryList == null) {
			//#debug warn
			System.out.println("no entries found for cateogry " + category.getName());
			return;
		}
		Object[] entries = calendarEntryList.getInternalArray();
		for (int j = 0; j < entries.length; j++) {
			CalendarEntry entry = (CalendarEntry) entries[j];
			if (entry == null) {
				break;
			}
			EventRepeatRule rule = entry.getRepeat();
			if (rule == null) {
				if (entry.matches( period)) {
//					System.out.println("adding " + entry.getSummary() + "period: " + period.getStart() + ", " + period.getEnd() + ", entry=" + entry.getStartDate());
					list.add(entry);
				}
			} else {
				nextDatePeriod.setStart(period.getStart(), period.isIncludeStart());
				TimePoint nextDate;
				while ((nextDate = rule.getNextDate(entry, nextDatePeriod )) != null) {
//					System.out.println("matching next date " + nextDate + " for period ending on " + nextDatePeriod.getEnd());
					CalendarEntry copy = entry.clone( nextDate );
					list.add( copy );
					nextDatePeriod.setStart(nextDate, false);
				}
				
			}
		}		
	}

	/**
	 * Retrieves all stored CalendarEntries for the specified category.
	 * If the specified category has no entries we will look in its child categories for entries.
	 * @param category the category
	 * @return CalendarEntryList List of all entries of that category. An empty list if the category has no entries.
	 */
	public CalendarEntryList getEntries( CalendarCategory category ) {
		CalendarEntryList entryList = (CalendarEntryList) this.calendarEntriesByCategory.get(category);
		if (entryList == null) {
			CalendarEntryList returnList = new CalendarEntryList();
			while ( category.hasChildCategories() ) {
				CalendarCategory[] childCategory = category.getChildCategories();
				for (int i = 0; i < childCategory.length; i++) {
					entryList =  getEntries(childCategory[i]);
					Object[] objects = entryList.getInternalArray();
					for (int j = 0; j < objects.length; j++) {
						CalendarEntry entry = (CalendarEntry) objects[j];
						if (entry == null) {
							break;
						}
						returnList.add(entry);
					}
				}
			}
			return returnList;
		} else {
			return entryList;
		}
	}
	
	/**
	 * Helper method for setting a text resolver for a variety of CalendarEntries.
	 * @param category the category that contains the entries, when null is given all CalendarEntries that are stored in this model will be processed.
	 * @param entryType the type of the entry
	 * @param textResolver the text resolver that should be applied to entries with the given type
	 * @see CalendarEntry#getType()
	 * @see CalendarEntry#setType(int)
	 */
	public void setTextResolver( CalendarCategory category, int entryType, CalendarTextResolver textResolver ) {
		if (category != null) {
			setTextResolver( getEntries(category), entryType, textResolver );
		} else {
			Object[] keys = this.calendarEntriesByCategory.keys();
			for (int i = 0; i < keys.length; i++) {
				Object key = keys[i];
				CalendarEntryList list = (CalendarEntryList) this.calendarEntriesByCategory.get(key);
				setTextResolver( list, entryType, textResolver );
			}
		}
	}

	private void setTextResolver(CalendarEntryList entriesList, int entryType, CalendarTextResolver textResolver) {
		Object[] entries = entriesList.getInternalArray();
		for (int i = 0; i < entries.length; i++) {
			CalendarEntry entry = (CalendarEntry) entries[i];
			if (entry == null) {
				break;
			}
			if (entry.getType() == entryType) {
				entry.setTextResolver(textResolver);
			}
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		int version = in.readInt();
		if (version != VERSION) {
			throw new IOException("unknown version " + version);
		}
		boolean isNotNull = in.readBoolean();
		if (isNotNull) {
			this.timeZone = TimeZone.getTimeZone( in.readUTF() );
		}
		// read root categories:
		// and store the GUIDs to make categories available:
		int categoriesSize = in.readInt();
		CalendarCategory[] roots = new CalendarCategory[ categoriesSize ];
		LongHashMap categoriesByGuid = new LongHashMap();
		for (int i = 0; i < roots.length; i++) {
			CalendarCategory root = new CalendarCategory();
			root.read(in);
			roots[i] = root;
			storeCategoryGuids( root, categoriesByGuid );
		}
		
		// read entries:
		categoriesSize = in.readInt();
		for (int i=0; i<categoriesSize; i++) {
			long guid = in.readLong();
			CalendarCategory category = (CalendarCategory) categoriesByGuid.get(guid);
			if (category == null) {
				throw new IOException("Encountered invalid CalendarCategory GUID " + guid );
			}
			int entriesSize = in.readInt();
			for (int j=0; j<entriesSize; j++) {
				CalendarEntry entry = new CalendarEntry();
				entry.read(in);
				entry.setCategory(category);
				addEntry( entry );
			}
		}
	}

	private void storeCategoryGuids(CalendarCategory category, LongHashMap categoriesByGuid) {
		categoriesByGuid.put(category.getGuid(), category);
		Object[] objects = category.getChildCategoriesAsInternalArray();
		for (int i = 0; i < objects.length; i++) {
			CalendarCategory child = (CalendarCategory) objects[i];
			if (child == null) {
				break;
			}
			storeCategoryGuids(child, categoriesByGuid);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeInt( VERSION );
		boolean notNull = (this.timeZone != null);
		out.writeBoolean(notNull);
		if (notNull) {
			out.writeUTF( this.timeZone.getID() );
		}
		// store all categories first:
		CalendarCategory[] roots = getRootCategories();
		out.writeInt( roots.length );
		for (int i = 0; i < roots.length; i++) {
			CalendarCategory root = roots[i];
			root.write( out ); // each calendar category also stores all its children
		}
		
		CalendarCategory[] categories = (CalendarCategory[]) this.calendarEntriesByCategory.keys( new CalendarCategory[ this.calendarEntriesByCategory.size() ] );
		out.writeInt( categories.length );
		for (int i = 0; i < categories.length; i++) {
			CalendarCategory category = categories[i];
			out.writeLong( category.getGuid() );
			CalendarEntryList list = (CalendarEntryList) this.calendarEntriesByCategory.get(category);
			out.writeInt( list.size() );
			Object[] entries = list.getInternalArray();
			for (int j = 0; j < entries.length; j++) {
				CalendarEntry entry = (CalendarEntry) entries[j];
				if (entry == null) {
					break;
				}
				entry.setCategory(null); // make sure that categories are not saved all over again
				entry.write(out);
				entry.setCategory(category);
			}
		}
		this.isDirty = false;
	}

	
	// TODO
	public void addObserver(CalendarObserver observer) {

		if ( this.observers.contains(observer) ) {
			// Do nothing;
		} else {
			this.observers.add(observer);
		}
	}

	
	// TODO
	public void removeObserver(CalendarObserver observer) {
		
		this.observers.remove(observer);
	}

	
	private void notifyObservers() {
		
		// loop through and notify each observer
		for (int i = 0; i < observers.size(); i++) {
			CalendarObserver observer = (CalendarObserver)observers.get(i);
			observer.updatedCalendarModel(this, this.recentChangedEntry, this.recentChangedCategory);
		}
	}

	
	public void notifyObserversExplicitly() {
		
		notifyObservers();
	}


	
}
