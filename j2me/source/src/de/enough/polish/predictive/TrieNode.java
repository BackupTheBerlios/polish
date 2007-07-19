//#condition polish.TextField.useDirectInput && !polish.blackberry && polish.usePolishGui 
package de.enough.polish.predictive;

public class TrieNode {
	private StringBuffer word;
	private char reference;
	
	public TrieNode() {
		this.word = new StringBuffer(5);
		this.reference = 0;
	}
	
	public int getReference() {
		return reference;
	}
	public void setReference(char reference) {
		this.reference = reference;
	}
	
	public void appendToWord(char letter)
	{
		this.word.append(letter);
	}
	
	public void appendToWord(StringBuffer word)
	{
		if(word != null)
			this.word.append(word);
	}
	
	public StringBuffer getWord() {
		return this.word;
	}
}
