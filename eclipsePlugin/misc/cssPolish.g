header{
package de.enough.polish.plugin.eclipse.css.parser;
}

// #################################################################
// Lexer

class CssLexer extends Lexer;

options {
    charVocabulary = '\3'..'\377';
    genHashLines=true;
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

protected
ATTRIBUTE_VALUE_PAIR : ;
protected
STYLE_SECTION : ;
protected
SECTION : ;
protected
STYLE_SHEET : ;

// #################################################################
// Parser

class CssParser extends Parser;
options{
	buildAST=true;
	genHashLines=true;
}
{
public boolean isExtendToken(Token e) {
    return "extends".equalsIgnoreCase(e.getText());
}
}

styleSheet
	:  (styleSection)+ {#styleSheet = #([STYLE_SHEET,"StyleSheet"],#styleSheet);}
	;
	
styleSection
	: styleName:NAME (e:NAME! {isExtendToken(e)}? parent:NAME)? L_CURLY_BRACKET! (name:NAME! (section:sectionBody[#name] | attributeValuePair:attributeValueBody[#name]))* R_CURLY_BRACKET! {#styleSection = #([STYLE_SECTION,"StyleSection"],#styleSection);}
	;

sectionBody[AST sectionName]
	: L_CURLY_BRACKET! (attributeName:NAME! attributeValueBody[#attributeName])* R_CURLY_BRACKET! {#sectionBody = #([SECTION,"Section"],#sectionName,#sectionBody);}
	;
	
attributeValueBody[AST attributeName]
	: string:ARBITRARY_STRING {#attributeValueBody = #([ATTRIBUTE_VALUE_PAIR,"AttributeValuePair"], #attributeName, #string);} // try to rename to attributeValuePair
	; 

// #################################################################
// Tree Grammar


