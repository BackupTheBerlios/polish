//#condition polish.TextField.useDirectInput && !polish.blackberry && polish.usePolishGui && polish.TextField.usePredictiveInput 
package de.enough.polish.predictive;

public class TrieRecord {
	private int id;
	private byte[] record;
	private int references;
	
	public TrieRecord(int id, byte[] record)
	{
		this.id = id;
		this.record = record;
		this.references = 0;
	}
	
	public void addReference()
	{
		this.references++;
	}
	
	public int getReferences()
	{
		return this.references;
	}

	public int getId() {
		return id;
	}

	public byte[] getRecord() {
		return record;
	}
}
