// $ANTLR 2.7.5 (20050128): "cssPolish.g" -> "OffsetWalker.java"$

package de.enough.polish.plugin.eclipse.css.parser;

import antlr.TreeParser;
import antlr.Token;
import antlr.collections.AST;
import antlr.RecognitionException;
import antlr.ANTLRException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.collections.impl.BitSet;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;


public class OffsetWalker extends antlr.TreeParser       implements CssLexerTokenTypes
 {
public OffsetWalker() {
	tokenNames = _tokenNames;
}

	public final void styleSheet(AST _t) throws RecognitionException {
		
		OffsetAST styleSheet_AST_in = (_t == ASTNULL) ? null : (OffsetAST)_t;
		
		try {      // for error handling
			AST __t32 = _t;
			OffsetAST tmp6_AST_in = (OffsetAST)_t;
			match(_t,STYLE_SHEET);
			_t = _t.getFirstChild();
			{
			_loop34:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==STYLE_SECTION)) {
					styleSection(_t);
					_t = _retTree;
				}
				else {
					break _loop34;
				}
				
			} while (true);
			}
			_t = __t32;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void styleSection(AST _t) throws RecognitionException {
		
		OffsetAST styleSection_AST_in = (_t == ASTNULL) ? null : (OffsetAST)_t;
		OffsetAST name = null;
		
		try {      // for error handling
			AST __t36 = _t;
			OffsetAST tmp7_AST_in = (OffsetAST)_t;
			match(_t,STYLE_SECTION);
			_t = _t.getFirstChild();
			name = (OffsetAST)_t;
			match(_t,NAME);
			_t = _t.getNextSibling();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case NAME:
			{
				OffsetAST tmp8_AST_in = (OffsetAST)_t;
				match(_t,NAME);
				_t = _t.getNextSibling();
				break;
			}
			case ATTRIBUTE_VALUE_PAIR:
			case SECTION:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			{
			int _cnt39=0;
			_loop39:
			do {
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case ATTRIBUTE_VALUE_PAIR:
				{
					att(_t);
					_t = _retTree;
					break;
				}
				case SECTION:
				{
					section(_t);
					_t = _retTree;
					break;
				}
				default:
				{
					if ( _cnt39>=1 ) { break _loop39; } else {throw new NoViableAltException(_t);}
				}
				}
				_cnt39++;
			} while (true);
			}
			_t = __t36;
			_t = _t.getNextSibling();
			System.out.println("StyleSectionOffset:"+styleSection_AST_in.getOffset());
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void att(AST _t) throws RecognitionException {
		
		OffsetAST att_AST_in = (_t == ASTNULL) ? null : (OffsetAST)_t;
		OffsetAST name = null;
		
		try {      // for error handling
			AST __t45 = _t;
			OffsetAST tmp9_AST_in = (OffsetAST)_t;
			match(_t,ATTRIBUTE_VALUE_PAIR);
			_t = _t.getFirstChild();
			name = (OffsetAST)_t;
			match(_t,NAME);
			_t = _t.getNextSibling();
			OffsetAST tmp10_AST_in = (OffsetAST)_t;
			match(_t,ARBITRARY_STRING);
			_t = _t.getNextSibling();
			_t = __t45;
			_t = _t.getNextSibling();
			System.out.println("AttributeOffset:"+name.getOffset());
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void section(AST _t) throws RecognitionException {
		
		OffsetAST section_AST_in = (_t == ASTNULL) ? null : (OffsetAST)_t;
		OffsetAST name = null;
		
		try {      // for error handling
			AST __t41 = _t;
			OffsetAST tmp11_AST_in = (OffsetAST)_t;
			match(_t,SECTION);
			_t = _t.getFirstChild();
			name = (OffsetAST)_t;
			match(_t,NAME);
			_t = _t.getNextSibling();
			{
			int _cnt43=0;
			_loop43:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==ATTRIBUTE_VALUE_PAIR)) {
					att(_t);
					_t = _retTree;
				}
				else {
					if ( _cnt43>=1 ) { break _loop43; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt43++;
			} while (true);
			}
			_t = __t41;
			_t = _t.getNextSibling();
			System.out.println("SectionOffset:"+name.getOffset());
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
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
	
	}
	
