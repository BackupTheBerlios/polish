/*
 * Created on Feb 23, 2005
 */
package de.enough.polish.plugin.eclipse.css.parser;

import org.eclipse.jface.text.IDocument;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;


/**
 * In a first step, get the ordering of the tokens. In a second step maintain positions. A thirst step
 * is needed for maintaining relative positions to reduce overhead.
 * @author rickyn
 */
public class CssTokenizer {
	
	private IDocument document;
	/** The document as a string. */
	private String stream; //TODO: Maybe we need a array here. Than getCharAt could be avoided. 
	
	/** Current position within the stream. */
	private int currentPos;
	
	//private boolean eof;
	
	/** The document as a list of tokens. */
	private List cssTokens;
	
	/** RegEx to recognize. */
	public String NAME_REGEX = "[a-zA-z]+"; //FIXME: May a name contain numbers?
	public String LB_REGEX = "{";
	public String RB_REGEX = "}";
	public String COLON_REGEX = ":";
	public String SEMICOLON_REGEX = ";";
	public String NUMBER_REGEX = "[0-9]+"; //FIXME: No float numbers recognized.
	public String WHITESPACE_REGEX = "[\\s]";
	public String COMMENT_REGEX = "/\\*.*\\*/";
	
	Pattern namePattern = Pattern.compile(this.NAME_REGEX);
	Pattern LBPattern = Pattern.compile(this.LB_REGEX);
	Pattern RBPattern = Pattern.compile(this.RB_REGEX);
	Pattern ColonPattern = Pattern.compile(this.COLON_REGEX);
	Pattern SemicolonPattern = Pattern.compile(this.SEMICOLON_REGEX);
	Pattern NumberPattern = Pattern.compile(this.NUMBER_REGEX);
	Pattern WhitespacePattern = Pattern.compile(this.WHITESPACE_REGEX);
	Pattern CommentPattern = Pattern.compile(this.COMMENT_REGEX);
	//Matcher nameMatcher = Pattern.matches()//TODO: It would be nice if the matcher would be a field.

	
	public CssTokenizer(IDocument document){
		this.document = document;
		this.currentPos = 0;
		this.stream = this.document.get();
		this.cssTokens = new ArrayList();
	}
	
	/**
	 * Constructor for easier testing.
	 * @param stream
	 */
	public CssTokenizer(String stream){
		this.document = null;
		this.currentPos = 0;
		this.stream = stream;
		this.cssTokens = new ArrayList();
	}
	
	/**
	 * Returns the next CssToken.
	 * <p>Imperative programming style. This way we see and understand the (simple) alorithm.
	 * @return CssToken The next token, null if no next token has been found.
	 */
	public CssToken getNextToken(){
		CssToken result;
		
		if(!hasNextChar()){
			result = new CssToken(this.currentPos+1,1,"EOF","");
		}
		else{
			result = getTokenFromRegEx(this.namePattern,"NAME");
				if(result != null){
					return result;
				}
		
		result = getTokenFromRegEx(this.LBPattern,"LB");
		if(result != null){
			return result;
		}
		return new CssToken(this.currentPos,1,"UNKNOWN",this.stream.substring(this.currentPos,this.currentPos+1));
		}
		return null;
	}

	public List tokenize(){
		CssToken cssToken = getNextToken();
		
		 while("EOF".equals(cssToken.getType())){
			this.cssTokens.add(cssToken);
		}
		 
		return this.cssTokens;
	}
	
	
	
	// ****************************************************
	// Methods to extract tokens.

	 // Generalized isName.
	private CssToken getTokenFromRegEx(Pattern pattern,String type){
		CssToken result = null;
		//int offset = this.currentPos;
		int length = 0;
		
		Matcher matcher = pattern.matcher(this.stream); // FIXME: convert matcher variables to fields.
		//matcher.reset(this.stream); // TODO: Use matcher as field in conjunction with reset.
		
		if(matcher.find(this.currentPos)){
			
			// We found a name _somewhere_. Discard the match if it is not at the interessting beginning.
			if(matcher.start() == this.currentPos){
				
				//We found the name at the offset, build a token then.
				length = matcher.end() - matcher.start(); // FIXME: check if matcher.start is equal to currentPos.
				result = new CssToken(this.currentPos,length,type,this.stream.substring(this.currentPos,this.currentPos+length));
			}
		}
		return result;
	}
	
	
	/*
	private CssToken isLB(){
		return null;
	}
	
	private CssToken isName(){
		CssToken result = null;
		int offset = this.currentPos;
		int length = 0;
		
		Matcher matcher = this.namePattern.matcher(this.stream); // FIXME: convert matcher variables to fields.
		matcher.reset(this.stream); // FIXME: If reset is to expensive find another way.
		
		if(matcher.find(offset)){
			// We found a name _somewhere_. Discard the match if it is not at the interessting beginning.
			if(matcher.start() == offset){
				//We found the name at the offset, build a token then.
				length = matcher.end() - matcher.start(); // FIXME: check if matcher.start is equal to currentPos.
				result = new CssToken(offset,length,"name",this.stream.substring(offset,offset+length));
			}
		}
		return result;
	}
	*/
	
	
	//*****************************************************
	// Not used anymore?
	/**
	 * Get the next char from the document. If there non left, keep current position and return last char.
	 * @return char The next character in the stream or the last character if eof is reached.
	 */
	public char getNextChar(){
		if(hasNextChar()){
			this.currentPos = this.currentPos + 1;
		}
		return this.stream.charAt(this.currentPos);
	}
	
	
	public boolean hasNextChar(){
		if(this.currentPos >= this.stream.length()){
			return false;
		}
		else{
			return true;
		}
			
	}
	


	
}
