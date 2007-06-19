package de.enough.polish.predictive;

public class Node {
	public Node() {
		this.word = "";
		this.reference = 0;
	}
	
	private String word;
	private int reference;
	
	public int getReference() {
		return reference;
	}
	public void setReference(int reference) {
		this.reference = reference;
	}
	public String getWord() {
		return word;
	}
	
	public void setWord(String word) {
		this.word = word;
	}
	
	
}
