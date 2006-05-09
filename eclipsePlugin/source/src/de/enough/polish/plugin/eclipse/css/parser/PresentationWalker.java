// $ANTLR 2.7.5 (20050128): "presentationWalker.g" -> "PresentationWalker.java"$

package de.enough.polish.plugin.eclipse.css.parser;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.source.ISharedTextColors;

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


public class PresentationWalker extends antlr.TreeParser       implements PresentationWalkerTokenTypes
 {

TextPresentation textPresentation;
ISharedTextColors colors;

public void configure(TextPresentation textPresentation, ISharedTextColors sharedTextColors){
	this.textPresentation = textPresentation;
	this.colors = sharedTextColors;	
}

private void addTextStyle(int position, int length, int r, int g, int b){
	StyleRange styleRange = new StyleRange(position,length+1,this.colors.getColor(new RGB(r,g,b)),null);
	this.textPresentation.addStyleRange(styleRange);
}
public PresentationWalker() {
	tokenNames = _tokenNames;
}

	public final void styleSheet(AST _t) throws RecognitionException {
		
		OffsetAST styleSheet_AST_in = (_t == ASTNULL) ? null : (OffsetAST)_t;
		
		try {      // for error handling
			AST __t2 = _t;
			OffsetAST tmp1_AST_in = (OffsetAST)_t;
			match(_t,STYLE_SHEET);
			_t = _t.getFirstChild();
			{
			_loop4:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==STYLE_SECTION)) {
					styleSection(_t);
					_t = _retTree;
				}
				else {
					break _loop4;
				}
				
			} while (true);
			}
			_t = __t2;
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
			AST __t6 = _t;
			OffsetAST tmp2_AST_in = (OffsetAST)_t;
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
				OffsetAST tmp3_AST_in = (OffsetAST)_t;
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
			int _cnt9=0;
			_loop9:
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
					if ( _cnt9>=1 ) { break _loop9; } else {throw new NoViableAltException(_t);}
				}
				}
				_cnt9++;
			} while (true);
			}
			_t = __t6;
			_t = _t.getNextSibling();
			addTextStyle(name.getOffset(),name.getLength(),0,200,0);
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
			AST __t15 = _t;
			OffsetAST tmp4_AST_in = (OffsetAST)_t;
			match(_t,ATTRIBUTE_VALUE_PAIR);
			_t = _t.getFirstChild();
			name = (OffsetAST)_t;
			match(_t,NAME);
			_t = _t.getNextSibling();
			OffsetAST tmp5_AST_in = (OffsetAST)_t;
			match(_t,ARBITRARY_STRING);
			_t = _t.getNextSibling();
			_t = __t15;
			_t = _t.getNextSibling();
			addTextStyle(name.getOffset(),name.getLength(),0,0,200);
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
			AST __t11 = _t;
			OffsetAST tmp6_AST_in = (OffsetAST)_t;
			match(_t,SECTION);
			_t = _t.getFirstChild();
			name = (OffsetAST)_t;
			match(_t,NAME);
			_t = _t.getNextSibling();
			{
			int _cnt13=0;
			_loop13:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==ATTRIBUTE_VALUE_PAIR)) {
					att(_t);
					_t = _retTree;
				}
				else {
					if ( _cnt13>=1 ) { break _loop13; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt13++;
			} while (true);
			}
			_t = __t11;
			_t = _t.getNextSibling();
			addTextStyle(name.getOffset(),name.getLength(),200,0,0);
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
	
