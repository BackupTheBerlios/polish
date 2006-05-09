// $ANTLR 2.7.5 (20050128): "cssPolish.g" -> "CssParser.java"$

package de.enough.polish.plugin.eclipse.css.parser;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.collections.AST;
import java.util.Hashtable;
import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;

public class CssParser extends antlr.LLkParser       implements CssLexerTokenTypes
 {

public boolean isExtendToken(Token e) {
    return "extends".equalsIgnoreCase(e.getText());
}

public void configure(OffsetAST node,Token token) {
    node.setOffset(((OffsetToken)token).getOffset());
    node.setLength(token.getText().length());
}


protected CssParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public CssParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected CssParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public CssParser(TokenStream lexer) {
  this(lexer,2);
}

public CssParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void styleSheet() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		OffsetAST styleSheet_AST = null;
		
		try {      // for error handling
			{
			int _cnt21=0;
			_loop21:
			do {
				if ((LA(1)==NAME)) {
					styleSection();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt21>=1 ) { break _loop21; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt21++;
			} while (true);
			}
			match(Token.EOF_TYPE);
			styleSheet_AST = (OffsetAST)currentAST.root;
			styleSheet_AST = (OffsetAST)astFactory.make( (new ASTArray(2)).add((OffsetAST)astFactory.create(STYLE_SHEET,"StyleSheet")).add(styleSheet_AST));
			currentAST.root = styleSheet_AST;
			currentAST.child = styleSheet_AST!=null &&styleSheet_AST.getFirstChild()!=null ?
				styleSheet_AST.getFirstChild() : styleSheet_AST;
			currentAST.advanceChildToEnd();
			styleSheet_AST = (OffsetAST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = styleSheet_AST;
	}
	
	public final void styleSection() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		OffsetAST styleSection_AST = null;
		Token  styleName = null;
		OffsetAST styleName_AST = null;
		Token  e = null;
		OffsetAST e_AST = null;
		Token  parent = null;
		OffsetAST parent_AST = null;
		Token  sectionOrAttributeName = null;
		OffsetAST sectionOrAttributeName_AST = null;
		OffsetAST section_AST = null;
		OffsetAST attributeValuePair_AST = null;
		
		try {      // for error handling
			styleName = LT(1);
			styleName_AST = (OffsetAST)astFactory.create(styleName);
			astFactory.addASTChild(currentAST, styleName_AST);
			match(NAME);
			configure(styleName_AST,styleName);
			{
			switch ( LA(1)) {
			case NAME:
			{
				e = LT(1);
				e_AST = (OffsetAST)astFactory.create(e);
				match(NAME);
				if (!(isExtendToken(e)))
				  throw new SemanticException("isExtendToken(e)");
				parent = LT(1);
				parent_AST = (OffsetAST)astFactory.create(parent);
				astFactory.addASTChild(currentAST, parent_AST);
				match(NAME);
				break;
			}
			case L_CURLY_BRACKET:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(L_CURLY_BRACKET);
			{
			_loop26:
			do {
				if ((LA(1)==NAME)) {
					sectionOrAttributeName = LT(1);
					sectionOrAttributeName_AST = (OffsetAST)astFactory.create(sectionOrAttributeName);
					match(NAME);
					configure(sectionOrAttributeName_AST,sectionOrAttributeName);
					{
					switch ( LA(1)) {
					case L_CURLY_BRACKET:
					{
						sectionBody(sectionOrAttributeName_AST);
						section_AST = (OffsetAST)returnAST;
						astFactory.addASTChild(currentAST, returnAST);
						break;
					}
					case ARBITRARY_STRING:
					{
						attributeValueBody(sectionOrAttributeName_AST);
						attributeValuePair_AST = (OffsetAST)returnAST;
						astFactory.addASTChild(currentAST, returnAST);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
				}
				else {
					break _loop26;
				}
				
			} while (true);
			}
			match(R_CURLY_BRACKET);
			styleSection_AST = (OffsetAST)currentAST.root;
			styleSection_AST = (OffsetAST)astFactory.make( (new ASTArray(2)).add((OffsetAST)astFactory.create(STYLE_SECTION,"StyleSection")).add(styleSection_AST));configure(styleSection_AST,styleName);
			currentAST.root = styleSection_AST;
			currentAST.child = styleSection_AST!=null &&styleSection_AST.getFirstChild()!=null ?
				styleSection_AST.getFirstChild() : styleSection_AST;
			currentAST.advanceChildToEnd();
			styleSection_AST = (OffsetAST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		returnAST = styleSection_AST;
	}
	
	public final void sectionBody(
		OffsetAST sectionName
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		OffsetAST sectionBody_AST = null;
		Token  attributeName = null;
		OffsetAST attributeName_AST = null;
		
		try {      // for error handling
			match(L_CURLY_BRACKET);
			{
			_loop29:
			do {
				if ((LA(1)==NAME)) {
					attributeName = LT(1);
					attributeName_AST = (OffsetAST)astFactory.create(attributeName);
					match(NAME);
					attributeName_AST.setOffset(((OffsetToken)attributeName).getOffset());
					attributeValueBody(attributeName_AST);
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop29;
				}
				
			} while (true);
			}
			match(R_CURLY_BRACKET);
			sectionBody_AST = (OffsetAST)currentAST.root;
			sectionBody_AST = (OffsetAST)astFactory.make( (new ASTArray(3)).add((OffsetAST)astFactory.create(SECTION,"Section")).add(sectionName).add(sectionBody_AST));sectionBody_AST.setOffset(sectionName.getOffset());
			currentAST.root = sectionBody_AST;
			currentAST.child = sectionBody_AST!=null &&sectionBody_AST.getFirstChild()!=null ?
				sectionBody_AST.getFirstChild() : sectionBody_AST;
			currentAST.advanceChildToEnd();
			sectionBody_AST = (OffsetAST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		returnAST = sectionBody_AST;
	}
	
	public final void attributeValueBody(
		OffsetAST attributeName
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		OffsetAST attributeValueBody_AST = null;
		Token  string = null;
		OffsetAST string_AST = null;
		
		try {      // for error handling
			string = LT(1);
			string_AST = (OffsetAST)astFactory.create(string);
			astFactory.addASTChild(currentAST, string_AST);
			match(ARBITRARY_STRING);
			attributeValueBody_AST = (OffsetAST)currentAST.root;
			attributeValueBody_AST = (OffsetAST)astFactory.make( (new ASTArray(3)).add((OffsetAST)astFactory.create(ATTRIBUTE_VALUE_PAIR,"AttributeValuePair")).add(attributeName).add(string_AST));attributeValueBody_AST.setOffset(attributeName.getOffset());string_AST.setOffset(((OffsetToken)string).getOffset());
			currentAST.root = attributeValueBody_AST;
			currentAST.child = attributeValueBody_AST!=null &&attributeValueBody_AST.getFirstChild()!=null ?
				attributeValueBody_AST.getFirstChild() : attributeValueBody_AST;
			currentAST.advanceChildToEnd();
			attributeValueBody_AST = (OffsetAST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		returnAST = attributeValueBody_AST;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"L_CURLY_BRACKET",
		"R_CURLY_BRACKET",
		"NAME",
		"a string",
		"WHITESPACE",
		"a multiline comment",
		"ATTRIBUTE_VALUE_PAIR",
		"STYLE_SECTION",
		"SECTION",
		"STYLE_SHEET"
	};
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 66L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 96L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	
	}
