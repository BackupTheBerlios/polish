package de.enough.polish.test;

/**
 * Used to store and identify a selection of list
 * @author Andre
 *
 */
public class ListSelection {
	/**
	 * the entries of this selection
	 */
	private final Object[] entries;
	
	/**
	 * the start index within the original list
	 */
	private final int start;
	
	/**
	 * the end index within the original list
	 */
	private final int end;
	
	/**
	 * the total entry count of the original list
	 */
	private final int total;

	/**
	 * Creates a new ListPart instance
	 * @param entries the entries for this instance
	 * @param start the start index
	 * @param end the end index
	 * @param total the total count
	 */
	public ListSelection(final Object[] entries, final int start, final int end, final int total) 
	{
		this.entries = new Object[end - start + 1];
		System.arraycopy(entries, start, this.entries, 0, this.entries.length);

		this.start = start;
		this.end = (end > 0) ? end : 0;
		this.total = total;
	}

	/**
	 * Returns the entries
	 * @return the entries
	 */
	public Object[] getEntries() 
	{
		return this.entries;
	}

	/**
	 * Returns the start index
	 * @return the start index
	 */
	public int getStart() 
	{
		return this.start;
	}

	/**
	 * Returns the end index
	 * @return the end index
	 */
	public int getEnd() 
	{
		return this.end;
	}

	/**
	 * Returns the total count
	 * @return the total count
	 */
	public int getTotal() 
	{
		return this.total;
	}
	
	//#mdebug error
	public String toString()
	{
		return "ListSelection[start=" + start 
			+ ",end=" + end 
			+ ",total=" + total 
			+ "]";
	}
	//#enddebug
}
