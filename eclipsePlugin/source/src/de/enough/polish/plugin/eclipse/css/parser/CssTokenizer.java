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
	private String stream;
	
	/** Current position within the stream. */
	private int currentPos;
	
	/** The document as a list of tokens. */
	private List cssTokens;
	
	// Do not use groups in the patterns, it would break the group to type mapping.
	public static String REGEX_NAME = "[a-zA-Z][a-zA-Z\\-_]*";
	public static String REGEX_LRB = "\\(";
	public static String REGEX_RRB = "\\)";
	public static String REGEX_LAB = "\\{";
	public static String REGEX_RAB = "\\}";
	public static String REGEX_COLON = ":";
	public static String REGEX_SEMICOLON = ";";
	public static String REGEX_NUMBER = "\\d+\\.\\d+|\\d+"; // Hmm, not commutative. But should be because of greedy search...
	public static String REGEX_WHITESPACE = "\\s+";
	public static String REGEX_COMMENT = "/\\*.*\\*/";
	public static String REGEX_PERCENT = "%";
	public static String REGEX_COMMA = ",";
	public static String REGEX_VBAR = "\\|";
	
	// Caution! The ordering of this array must be congruent to MATCH_ALL.
	public static String[] GROUP_REGEX_MAPPING = {
								REGEX_NAME,
								REGEX_LRB,
								REGEX_RRB,
								REGEX_LAB,
								REGEX_RAB,
								REGEX_COLON,
								REGEX_SEMICOLON,
								REGEX_NUMBER,
								REGEX_WHITESPACE,
								REGEX_COMMENT,
								REGEX_PERCENT,
								REGEX_COMMA,
								REGEX_VBAR};
	
	
	public static String TYPE_NAME = "NAME";
	public static String TYPE_LRB = "LRB";
	public static String TYPE_RRB = "RRB";
	public static String TYPE_LAB = "LAB";
	public static String TYPE_RAB = "RAB";
	public static String TYPE_COLON = "COLON";
	public static String TYPE_SEMICOLON = "SEMICOLON";
	public static String TYPE_NUMBER = "NUMBER";
	public static String TYPE_WHITESPACE = "WHITESPACE";
	public static String TYPE_COMMENT = "COMMENT";
	public static String TYPE_PERCENT = "PERCENT";
	public static String TYPE_COMMA = "COMMA";
	public static String TYPE_VBAR = "VBAR";
	
	public static String[] GROUP_TYPE_MAPPING = {
								"ERROR:CssTokenizer.GROUP_TYPE_MAPPING:GROUP 0 (whole string) was selected.But this group doesnt have a type.",
								TYPE_NAME,
								TYPE_LRB,
								TYPE_RRB,
								TYPE_LAB,
								TYPE_RAB,
								TYPE_COLON,
								TYPE_SEMICOLON,
								TYPE_NUMBER,
								TYPE_WHITESPACE,
								TYPE_COMMENT,
								TYPE_PERCENT,
								TYPE_COMMA,
								TYPE_VBAR};
	
	
	// TODO: The idea is to match in one step hoping the regEx implementation construct
	// one large FSA. This will be significantly faster than matching one at a time.
	public static String REGEX_CSS =
							"("+REGEX_NAME +")|"+
							"("+REGEX_LRB+")|"+
							"("+REGEX_RRB+")|"+
							"("+REGEX_LAB+")|"+
							"("+REGEX_RAB+")|"+
							"("+REGEX_COLON+")|"+
							"("+REGEX_SEMICOLON+")|"+
							"("+REGEX_NUMBER+")|"+
							"("+REGEX_WHITESPACE+")|"+
							"("+REGEX_COMMENT+")|"+
							"("+REGEX_PERCENT+")|"+
							"("+REGEX_COMMA+")|"+
							"("+REGEX_VBAR+")";
	
	Matcher matcher;
	
	public CssTokenizer(IDocument document){
		this.document = document;
		this.currentPos = 0;
		this.stream = this.document.get();
		this.cssTokens = new ArrayList();
		Pattern pattern = Pattern.compile(REGEX_CSS);
		this.matcher = pattern.matcher(this.stream);
		//TODO: Do reset from the outside when something changed in the input.
		this.matcher.reset(this.stream);
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
		Pattern pattern = Pattern.compile(REGEX_CSS);
		this.matcher = pattern.matcher(stream);
		//TODO: Do reset from the outside when something changed in the input.
		this.matcher.reset(this.stream);
	}
	
	// If a file has x characters, EOF has an offset of x+1.
	public CssToken getNextToken(){
		
		if(!hasNextChar()){
			// dont increase currentPos, we are at the end already.
			return new CssToken(this.currentPos+1,0,"EOF","");
		}
		
		CssToken result;
		
		// As a default the unknown token.
		result = new CssToken(this.currentPos,1,"UNKNOWN",this.stream.substring(this.currentPos,this.currentPos+1));
		
		boolean matchedSomething;
		matchedSomething = this.matcher.find(this.currentPos);
		
		if(!matchedSomething){
			// We havent matched anything, go to the next. The default UNKNOWN token is returned.
			this.currentPos = this.currentPos + result.getLength();
			return result;
		}
		
		// We found something.
		// matcher.find searches somewhere in the input. But we need a match at the start.
		// The caller gave us the offset, he knows where to search. This condition can be avoided,
		// if we have a regEx library with lookAt(offset), but we do not have one.
		if(this.matcher.start() != this.currentPos){
			this.currentPos = this.currentPos + result.getLength();
			return result;
		}
		
		// We have something and is at the offset. Find out what it is.
		for(int i = 1; i <= this.matcher.groupCount();i++){
			String value = this.matcher.group(i);
			
			// In theory we pass this condition exactly once. the tokenlanguage is LL so only one
			// group match. And because we have some groups we do have a group != "", too.
			if((value != null) && (value.length() > 0)){ // Arcording to API sometimes "" can be a group "a*" as an example.
				
				// We have a group that matched. Get the type and create a token
				int length = this.matcher.end() - this.matcher.start(); // FIXME: check if matcher.start is equal to currentPos.
				result = new CssToken(this.currentPos,length,GROUP_TYPE_MAPPING[i],this.stream.substring(this.currentPos,this.currentPos+length));
				this.currentPos = this.currentPos + result.getLength();
				break;
			}
		}
		return result;
	}
	
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
	
	/**
	 * Creates a list of tokens and returns it.
	 * @return List
	 */
	public List tokenize(){
		CssToken cssToken;
		cssToken = getNextToken();
		//int i = 100; //Dont do this at home! Its for debugging invinite loops only.
		while( ! ("EOF".equals(cssToken.getType())) /*&& (i > 0)*/){
			this.cssTokens.add(cssToken);
			cssToken = getNextToken();
			//i--;
		}
		this.cssTokens.add(cssToken); // Put EOF also in the list.
		return this.cssTokens;
	}
	
	
	// This method is needed for incremental scanning.
	public List getTokensInRange(int offset, int length){
		return null;
	}
	
	// *****************************************************
	// Getter and Setter.
	
	/**
	 * @return Returns the cssTokens.
	 */
	public List getCssTokens() {
		return this.cssTokens;
	}
	/**
	 * @param cssTokens The cssTokens to set.
	 */
	public void setCssTokens(List cssTokens) {
		this.cssTokens = cssTokens;
	}
	
	
	/**
	 * @return Returns the currentPos.
	 */
	public int getCurrentPos() {
		return this.currentPos;
	}
	
	/**
	 * @param currentPos The currentPos to set.
	 */
	public void setCurrentPos(int currentPos) {
		this.currentPos = currentPos;
	}
	/**
	 * @return Returns the document.
	 */
	public IDocument getDocument() {
		return this.document;
	}
	/**
	 * @param document The document to set.
	 */
	public void setDocument(IDocument document) {
		this.document = document;
	}

	// *****************************************************
	// Not used anymore?
	
	 // Generalized isName.
	// we do not increase currentPos directly in the method because maybe we do not want
	// this token but another one. The caller should decide about that. Probably give the starting pos
	// as parameter to decuple explicitly.
	/*
	 private CssToken getTokenFromRegEx(Pattern pattern,String type){
		CssToken result = null;
		//int offset = this.currentPos;
		int length = 0;
		
		Matcher matcher2 = pattern.matcher(this.stream); // FIXME: convert matcher variables to fields.
		//matcher.reset(this.stream); // TODO: Use matcher as field in conjunction with reset.
		
		if(matcher2.find(this.currentPos)){
			
			// We found a name _somewhere_. Discard the match if it is not at the interessting beginning.
			if(matcher2.start() == this.currentPos){
				
				//We found the name at the offset, build a token then.
				length = matcher2.end() - matcher2.start(); // FIXME: check if matcher.start is equal to currentPos.
				result = new CssToken(this.currentPos,length,type,this.stream.substring(this.currentPos,this.currentPos+length));
			}
		}
		return result;
	}
	*/
	
	
	/*
	Pattern namePattern = Pattern.compile(REGEX_NAME);
	Pattern LRBPattern = Pattern.compile(REGEX_LRB);
	Pattern RRBPattern = Pattern.compile(REGEX_RRB);
	Pattern LABPattern = Pattern.compile(REGEX_LAB);
	Pattern RABPattern = Pattern.compile(REGEX_RAB);
	Pattern ColonPattern = Pattern.compile(REGEX_COLON);
	Pattern SemicolonPattern = Pattern.compile(REGEX_SEMICOLON);
	Pattern NumberPattern = Pattern.compile(REGEX_NUMBER);
	Pattern WhitespacePattern = Pattern.compile(REGEX_WHITESPACE);
	Pattern CommentPattern = Pattern.compile(REGEX_COMMENT);
	//Matcher nameMatcher = Pattern.matches()//TODO: It would be nice if the matcher would be a field.
	*/
	
	/**
	 * Returns the next CssToken.
	 * <p>Imperative programming style. This way we see and understand the (simple) alorithm.
	 * @return CssToken The next token, null if no next token has been found.
	 */
	/*
	public CssToken getNextToken(){
		CssToken result;
		
		if(!hasNextChar()){
			// dont increase currentPos, we are at the end already.
			return new CssToken(this.currentPos+1,1,"EOF","");
		}
		result = getTokenFromRegEx(this.namePattern,"NAME");
		if(result != null){
			this.currentPos = this.currentPos + result.getLength();
			return result;
		}
		result = getTokenFromRegEx(this.LRBPattern,"LRB");
		if(result != null){
			this.currentPos = this.currentPos + result.getLength();
			return result;
		}
		result = getTokenFromRegEx(this.RRBPattern,"RRB");
		if(result != null){
			this.currentPos = this.currentPos + result.getLength();
			return result;
		}
		result = getTokenFromRegEx(this.LABPattern,"LAB");
		if(result != null){
			this.currentPos = this.currentPos + result.getLength();
			return result;
		}
		result = getTokenFromRegEx(this.RABPattern,"RAB");
		if(result != null){
			this.currentPos = this.currentPos + result.getLength();
			return result;
		}
		result = getTokenFromRegEx(this.WhitespacePattern,"WHITESPACE");
		if(result != null){
			this.currentPos = this.currentPos + result.getLength();
			return result;
		}
		result = getTokenFromRegEx(this.ColonPattern,"COLON");
		if(result != null){
			this.currentPos = this.currentPos + result.getLength();
			return result;
		}
		result = getTokenFromRegEx(this.SemicolonPattern,"SEMICOLON");
		if(result != null){
			this.currentPos = this.currentPos + result.getLength();
			return result;
		}
		result = getTokenFromRegEx(this.CommentPattern,"COMMENT");
		if(result != null){
			this.currentPos = this.currentPos + result.getLength();
			return result;
		}
		result = getTokenFromRegEx(this.NumberPattern,"NUMBER");
		if(result != null){
			this.currentPos = this.currentPos + result.getLength();
			return result;
		}
		// We dont know the character, make a unknown token and go to the next one. 
		result = new CssToken(this.currentPos,1,"UNKNOWN",this.stream.substring(this.currentPos,this.currentPos+1));
		this.currentPos = this.currentPos + 1;
		return result;
	}
	*/
}
