header{
package de.enough.polish.plugin.eclipse.css.parser;
}


// #################################################################
// Lexer

class CssLexer extends Lexer;

options {
    charVocabulary = '\3'..'\377';
}

{
int tabWidth = 1;

public void setTabWidth(int tabWidth){
	if(tabWidth < 1){
		tabWidth = 1;
	}
	this.tabWidth = tabWidth;
}
	
public void tab(){
	setColumn(getColumn()+4); //TODO: Acquire the concrete tabwidth from the PropertyStore.
}
}

L_CURLY_BRACKET : '{'
	;
	
R_CURLY_BRACKET : '}'
	;
	
COLON : ':'
	;
	
SEMICOLON : ';'
	;
	
NAME : ('a'..'z')+  // Need more allowed characters.
	;
	

ARBITRARY_STRING :  . //(~('\n'|';'))+ // Put also \r\n in the rule.*/
	;


WHITESPACE :	(' '
	|	'\t'
	|	'\n' {newline();}
	|	'\r')
		{ _ttype = Token.SKIP; }
	;

// #################################################################
// Parser

class CssParser extends Parser;

stylesheet
	: (styleSection)+
	;
	
styleSection
	: NAME (EXTENDS NAME)? L_CURLY_BRACKET (NAME (sectionBody | attributeValueBody))* R_CURLY_BRACKET
	;

sectionBody
	: L_CURLY_BRACKET (NAME attributeValueBody)* R_CURLY_BRACKET 
	;
	
attributeValueBody
	: COLON ARBITRARY_STRING SEMICOLON; // This needs work: We need another lexer state to parse a arbitrary string.

// #################################################################
// Tree Grammar


