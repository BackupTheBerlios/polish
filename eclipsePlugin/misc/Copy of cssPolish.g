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

/*
public int testLiteralsTable(String text, int ttype) {
    System.out.println("CssLexer.testLiteralsTable():text:"+text+".ttype:"+ttype);
	return super.testLiteralsTable(text, ttype);
}
*/
}

L_CURLY_BRACKET : '{'
	;
	
R_CURLY_BRACKET : '}'
	;
	
//COLON : ':'
//	;
	
//SEMICOLON : ';'
//	;
	
NAME : ('a'..'z'|'-'|'_'|'.'|'A'..'Z')+  // Need more allowed characters.
	;
	

ARBITRARY_STRING
options {
  paraphrase = "a string";
} 
	:  ':'! (options {greedy=false;} :.)* ';'!
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
options{
	buildAST=true;
}
{
public boolean isExtendToken(Token e) {
    return "extends".equalsIgnoreCase(e.getText());
}
}

stylesheet
	: (styleSection)+
	;
	
styleSection
	: NAME (e:NAME {isExtendToken(e)}? NAME)? L_CURLY_BRACKET (NAME (sectionBody | attributeValueBody))* R_CURLY_BRACKET
	;

sectionBody
	: L_CURLY_BRACKET (NAME attributeValueBody)* R_CURLY_BRACKET 
	;
	
attributeValueBody
	: ARBITRARY_STRING
	; // This needs work: We need another lexer state to parse a arbitrary string.

// #################################################################
// Tree Grammar


