package de.enough.polish.predictive;

public class TrieProperties {
	public int chunkSize;
	public int lineCount;
	
	public String prefix;
	public String vendorName;
	public String suiteName;
	
	
	public TrieProperties(int chunkSize, int lineCount) {
		super();
		this.chunkSize = chunkSize;
		this.lineCount = lineCount;
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

	public String getSuiteName() {
		return suiteName;
	}

	public void setSuiteName(String suiteName) {
		this.suiteName = suiteName;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
}
