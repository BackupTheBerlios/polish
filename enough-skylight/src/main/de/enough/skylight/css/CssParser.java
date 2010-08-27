package de.enough.skylight.css;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.enough.polish.util.StreamUtil;

/**
 * This parser is crafted by hand because the CSS specification allows for a very loose interpretation of syntactic rules. 
 * @author rickyn
 *
 */
public class CssParser {

	private String documentCharset = "UTF-8";
	private int position = 0;
	private final SelectorTree selectorTree;
	private final InputStream rawInputStream;
	private final String text;
	private final String keywords = "@import@media@page";
	
//	private Tokenizer tokenizer;
//	public class Tokenizer{
//		public static final int EOF = -1;
//		private final String text;
//		private int tokenOffset = -1;
//		public Tokenizer(String text) {
//			if(text == null) {
//				this.text = "";
//			} else {
//				this.text = text;
//			}
//		}
//		public int getNextToken() {
//			return EOF;
//		}
//	}
	
	/**
	 * TODO: Think about using an InputStream to save space.
	 * @param useEncoding the encoding this parser should use, no matter what. If null, no encoding is enforced.
	 * @param defaultEncoding the encoding to use if no encoding is specified in the document itself.
	 */
	public CssParser(SelectorTree selectorTree, InputStream rawInputStream, String useEncoding, String defaultEncoding) throws ParseException {
		this.selectorTree = selectorTree;
		this.rawInputStream = rawInputStream;
		String encoding = useEncoding != null?useEncoding:defaultEncoding;
		try {
			this.text = StreamUtil.getString(this.rawInputStream, encoding, 1000);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ParseException();
		}
//		this.tokenizer = new Tokenizer(text);
	}
	
	public void parse() {
		parseStylesheet();
	}
	
	protected void parseStylesheet() {
//		int nextToken = this.tokenizer.getNextToken();
//		if(nextToken == Tokenizer.EOF) {
//			return;
//		}
		
		parseCharset();
		parseImports();
		parseStatements();
		
	}

	private void parseCharset() {
		// TODO: This is left out for the moment. To implement this, we need a SequenceInputStream.
		// Read the first bytes in the rawInputStream and look for '@charset'. If is present, continue the parsing until ';' is encountered.
		// Then create a InputStreamReader. If the charset token is not found, create an InputStream from the already read bytes and concat it
		// with the rawInputStream.
	}
	
	private void parseImports() {
		acceptWhitespace();
		// Look for @import
		boolean matches = this.text.regionMatches(false, this.position, this.keywords, 0, 7);
		if(!matches) {
			return;
		}
		// TODO: Find a nice scheme to implement alternatives.
		parseString();
	}

	private void parseStatements() {
		// TODO Auto-generated method stub
		acceptWhitespace();
		if(accept('@')) {
			parseAtRule();
		} else {
			parseRuleSet();
		}
	}

	private void parseRuleSet() {
		// TODO Auto-generated method stub
		
	}

	private void parseAtRule() {
		//CSS2.1,4.2: Ignore Invalid at-keywords
		// TODO Auto-generated method stub
	}

	private void parseBlock() {
		// TODO Auto-generated method stub
		
	}
	
	private void parseDeclaration() {
		//CSS2.1,4.2: Ignore illegal values
		//CSS2.1,4.2: Ignore unknown property
		//CSS2.1,4.2: Handle malformed declarations
	}
	
	private void parseSelector() {
		//CSS2.1,4.1.7: Ignore malformed selector and following block.
	}
	
	private void parseCssComment() {
		
	}
	
	private void parseHtmlComment() {
		
	}
	
	private boolean acceptWhitespace() {
		char charAt = this.text.charAt(this.position);
		if(' ' == charAt || '\t' == charAt) {
			this.position++;
			return true;
		}
		return false;
	}
	
	private boolean accept(char character) {
		if(this.text.charAt(this.position) == character) {
			this.position++;
			return true;
		}
		return false;
	}

	private void parseString() {
		//CSS2.1,4.2: Handle unexpected end of string
	}
	
	private void parseEscape() {
		
	}
}
