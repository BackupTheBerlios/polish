/*
 * Created on Feb 23, 2005
 */
package de.enough.polish.plugin.eclipse.css.parser;


/**
 * @author rickyn
 */
public class CssParser {
	
	private CssTokenizer tokenizer;
	private int pos;
	
	public CssParser(CssTokenizer tokenizer){
		this.tokenizer = tokenizer;
		this.pos = 0;
	}
	
	
	

	
	

	/**
	 * @return Returns the pos.
	 */
	public int getPos() {
		return this.pos;
	}
	/**
	 * @param pos The pos to set.
	 */
	public void setPos(int pos) {
		this.pos = pos;
	}
	/**
	 * @return Returns the tokenizer.
	 */
	public CssTokenizer getTokenizer() {
		return this.tokenizer;
	}
	/**
	 * @param tokenizer The tokenizer to set.
	 */
	public void setTokenizer(CssTokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}
}
