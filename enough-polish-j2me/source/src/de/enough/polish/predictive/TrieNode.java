package de.enough.polish.predictive;

public class TrieNode {
	private StringBuffer word;
	private int reference;
	
	public TrieNode() {
		this.word = new StringBuffer(5);
		this.reference = 0;
	}
	
	public int getReference() {
		return reference;
	}
	public void setReference(int reference) {
		this.reference = reference;
	}
	
	public void appendToWord(char letter)
	{
		this.word.append(letter);
	}
	
	public void appendToWord(String word)
	{
		this.word.append(word);
	}
	
	public String getWord() {
		return this.word.toString();
	}
}
