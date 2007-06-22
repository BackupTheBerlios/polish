package de.enough.polish.predictive;

public class TrieProperties {
	private int chunkSize;
	private int lineCount;
	private String prefix;
	private String vendor;
	private String suite;
	private boolean localRMS;
	
	public TrieProperties(String prefix, String vendor, String suite, boolean localRMS, int chunkSize, int lineCount) {
		this.chunkSize 	= chunkSize;
		this.lineCount 	= lineCount;
		this.prefix 	= prefix;
		this.vendor 	= vendor;
		this.suite 		= suite;
		this.localRMS 	= localRMS;
	}
	
	public int getLineCount() {
		return this.lineCount;
	}
	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}
	public int getChunkSize() {
		return this.chunkSize;
	}
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public boolean isLocalRMS() {
		return localRMS;
	}

	public void setLocalRMS(boolean localRMS) {
		this.localRMS = localRMS;
	}

	public String getSuite() {
		return suite;
	}

	public void setSuite(String suite) {
		this.suite = suite;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
}
