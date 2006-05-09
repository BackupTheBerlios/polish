// $ANTLR 2.7.5 (20050128): "cssPolish.g" -> "CssLexer.java"$

package de.enough.polish.plugin.eclipse.css.parser;

import java.io.InputStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.ANTLRException;
import java.io.Reader;
import java.util.Hashtable;
import antlr.CharScanner;
import antlr.InputBuffer;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.Token;
import antlr.CommonToken;
import antlr.RecognitionException;
import antlr.NoViableAltForCharException;
import antlr.MismatchedCharException;
import antlr.TokenStream;
import antlr.ANTLRHashString;
import antlr.LexerSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.SemanticException;

public class CssLexer extends antlr.CharScanner implements CssLexerTokenTypes, TokenStream
 {

int tabWidth = 1;
int offset = 0;
int i = 1;

public void setTabWidth(int tabWidth){
	if(tabWidth < 1){
		tabWidth = 1;
	}
	this.tabWidth = tabWidth;
}
	
public void tab(){
	setColumn(getColumn()+4); //TODO: Acquire the concrete tabwidth from the PropertyStore.
	//this.i = this.i + 4; // I do not know we this must not be active to get the offset counting to work.
}

// TODO:Optimize token handling. remove line, column and text information. Provide only offset and length.
protected Token makeToken(int t) {
    Token token = super.makeToken(t);
    if (token != Token.badToken) {
        ((OffsetToken)token).setOffset(this.offset);
        this.offset = this.offset + i;
        this.i = 0;
        
    }
    return token;
}

public void consume() throws CharStreamException {
	super.consume();
	this.i++;
	//this.offset = this.offset + 1;
}
/*
public void newline(){
	super.newl3ine();
	this.offset++;
}
*/
public void panic(String s) {
    System.err.println("CharScanner: Panic:"+s);
}

public CssLexer(InputStream in) {
	this(new ByteBuffer(in));
}
public CssLexer(Reader in) {
	this(new CharBuffer(in));
}
public CssLexer(InputBuffer ib) {
	this(new LexerSharedInputState(ib));
}
public CssLexer(LexerSharedInputState state) {
	super(state);
	caseSensitiveLiterals = true;
	setCaseSensitive(true);
	literals = new Hashtable();
}

public Token nextToken() throws TokenStreamException {
	Token theRetToken=null;
tryAgain:
	for (;;) {
		Token _token = null;
		int _ttype = Token.INVALID_TYPE;
		resetText();
		try {   // for char stream error handling
			try {   // for lexical error handling
				switch ( LA(1)) {
				case '{':
				{
					mL_CURLY_BRACKET(true);
					theRetToken=_returnToken;
					break;
				}
				case '}':
				{
					mR_CURLY_BRACKET(true);
					theRetToken=_returnToken;
					break;
				}
				case '-':  case '.':  case 'A':  case 'B':
				case 'C':  case 'D':  case 'E':  case 'F':
				case 'G':  case 'H':  case 'I':  case 'J':
				case 'K':  case 'L':  case 'M':  case 'N':
				case 'O':  case 'P':  case 'Q':  case 'R':
				case 'S':  case 'T':  case 'U':  case 'V':
				case 'W':  case 'X':  case 'Y':  case 'Z':
				case '_':  case 'a':  case 'b':  case 'c':
				case 'd':  case 'e':  case 'f':  case 'g':
				case 'h':  case 'i':  case 'j':  case 'k':
				case 'l':  case 'm':  case 'n':  case 'o':
				case 'p':  case 'q':  case 'r':  case 's':
				case 't':  case 'u':  case 'v':  case 'w':
				case 'x':  case 'y':  case 'z':
				{
					mNAME(true);
					theRetToken=_returnToken;
					break;
				}
				case ':':
				{
					mARBITRARY_STRING(true);
					theRetToken=_returnToken;
					break;
				}
				case '\t':  case '\n':  case '\r':  case ' ':
				{
					mWHITESPACE(true);
					theRetToken=_returnToken;
					break;
				}
				case '/':
				{
					mML_COMMENT(true);
					theRetToken=_returnToken;
					break;
				}
				default:
				{
					if (LA(1)==EOF_CHAR) {uponEOF(); _returnToken = makeToken(Token.EOF_TYPE);}
				else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				}
				if ( _returnToken==null ) continue tryAgain; // found SKIP token
				_ttype = _returnToken.getType();
				_ttype = testLiteralsTable(_ttype);
				_returnToken.setType(_ttype);
				return _returnToken;
			}
			catch (RecognitionException e) {
				throw new TokenStreamRecognitionException(e);
			}
		}
		catch (CharStreamException cse) {
			if ( cse instanceof CharStreamIOException ) {
				throw new TokenStreamIOException(((CharStreamIOException)cse).io);
			}
			else {
				throw new TokenStreamException(cse.getMessage());
			}
		}
	}
}

	public final void mL_CURLY_BRACKET(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = L_CURLY_BRACKET;
		int _saveIndex;
		
		match('{');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mR_CURLY_BRACKET(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = R_CURLY_BRACKET;
		int _saveIndex;
		
		match('}');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mNAME(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NAME;
		int _saveIndex;
		
		{
		int _cnt5=0;
		_loop5:
		do {
			switch ( LA(1)) {
			case 'a':  case 'b':  case 'c':  case 'd':
			case 'e':  case 'f':  case 'g':  case 'h':
			case 'i':  case 'j':  case 'k':  case 'l':
			case 'm':  case 'n':  case 'o':  case 'p':
			case 'q':  case 'r':  case 's':  case 't':
			case 'u':  case 'v':  case 'w':  case 'x':
			case 'y':  case 'z':
			{
				matchRange('a','z');
				break;
			}
			case '-':
			{
				match('-');
				break;
			}
			case '_':
			{
				match('_');
				break;
			}
			case '.':
			{
				match('.');
				break;
			}
			case 'A':  case 'B':  case 'C':  case 'D':
			case 'E':  case 'F':  case 'G':  case 'H':
			case 'I':  case 'J':  case 'K':  case 'L':
			case 'M':  case 'N':  case 'O':  case 'P':
			case 'Q':  case 'R':  case 'S':  case 'T':
			case 'U':  case 'V':  case 'W':  case 'X':
			case 'Y':  case 'Z':
			{
				matchRange('A','Z');
				break;
			}
			default:
			{
				if ( _cnt5>=1 ) { break _loop5; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			}
			_cnt5++;
		} while (true);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mARBITRARY_STRING(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ARBITRARY_STRING;
		int _saveIndex;
		
		_saveIndex=text.length();
		match(':');
		text.setLength(_saveIndex);
		{
		_loop8:
		do {
			// nongreedy exit test
			if ((LA(1)==';') && (true)) break _loop8;
			if (((LA(1) >= '\u0003' && LA(1) <= '\u00ff')) && ((LA(2) >= '\u0003' && LA(2) <= '\u00ff'))) {
				matchNot(EOF_CHAR);
			}
			else {
				break _loop8;
			}
			
		} while (true);
		}
		_saveIndex=text.length();
		match(';');
		text.setLength(_saveIndex);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mWHITESPACE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = WHITESPACE;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case ' ':
		{
			match(' ');
			break;
		}
		case '\t':
		{
			match('\t');
			break;
		}
		case '\n':
		{
			match('\n');
			newline();
			break;
		}
		case '\r':
		{
			match('\r');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		_ttype = Token.SKIP;
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mML_COMMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ML_COMMENT;
		int _saveIndex;
		
		match("/*");
		{
		_loop14:
		do {
			if (((LA(1)=='*') && ((LA(2) >= '\u0003' && LA(2) <= '\u00ff')))&&( LA(2) != '/' )) {
				match('*');
			}
			else if ((_tokenSet_0.member(LA(1)))) {
				{
				match(_tokenSet_0);
				}
			}
			else {
				break _loop14;
			}
			
		} while (true);
		}
		match("*/");
		_ttype = Token.SKIP;
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mATTRIBUTE_VALUE_PAIR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ATTRIBUTE_VALUE_PAIR;
		int _saveIndex;
		
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mSTYLE_SECTION(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = STYLE_SECTION;
		int _saveIndex;
		
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mSECTION(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SECTION;
		int _saveIndex;
		
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mSTYLE_SHEET(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = STYLE_SHEET;
		int _saveIndex;
		
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	
	private static final long[] mk_tokenSet_0() {
		long[] data = new long[8];
		data[0]=-4398046511112L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	
	}
