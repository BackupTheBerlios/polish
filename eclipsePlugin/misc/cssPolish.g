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
int offset = 0;

public void setTabWidth(int tabWidth){
	if(tabWidth < 1){
		tabWidth = 1;
	}
	this.tabWidth = tabWidth;
}
	
public void tab(){
	setColumn(getColumn()+4); //TODO: Acquire the concrete tabwidth from the PropertyStore.
	this.offset = this.offset + 4;
}


public void panic(String s) {
    System.err.println("CharScanner: Panic:"+s);
}

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
	:  ':'!  (options {greedy=false;} :.)*  ';'! //{$setText()}
    ;

    
WHITESPACE :	(' '
	|	'\t'
	|	'\n' {newline();}
	|	'\r')
		{ _ttype = Token.SKIP; }
	;

/* Use TokenStreamHiddenTokenFilter to hide the comment from the grammar but put in the ast.*/
/*So we can move the comment along the elements.*/
ML_COMMENT
options {
  paraphrase = "a multiline comment";
 
}
	: "/*"! (options {greedy=false;} : '\n' {newline();}|.)* "*/"! { _ttype = Token.SKIP; }
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
	ASTLabelType = "OffsetAST";
}

{
public boolean isExtendToken(Token e) {
    return "extends".equalsIgnoreCase(e.getText());
}
}

styleSheet
	:  ((ML_COMMENT)? styleSection)+ EOF! {#styleSheet = #([STYLE_SHEET,"StyleSheet"],#styleSheet); /*styleSheet_AST.setOffset(((OffsetAST)styleSheet_AST.getFirstChild()).getOffset());*/}
	;
	
styleSection
	: styleName:NAME  (e:NAME! {isExtendToken(e)}? parent:NAME)? L_CURLY_BRACKET! (name:NAME! (section:sectionBody[#name] | attributeValuePair:attributeValueBody[#name]))* R_CURLY_BRACKET! {#styleSection = #([STYLE_SECTION,"StyleSection"],#styleSection);/*styleSection_AST.setOffset(styleName.getLine());*/}
	;

sectionBody[AST sectionName]
	: L_CURLY_BRACKET! (attributeName:NAME! attributeValueBody[#attributeName])* R_CURLY_BRACKET! {#sectionBody = #([SECTION,"Section"],#sectionName,#sectionBody);}
	;
	
attributeValueBody[AST attributeName]
	: string:ARBITRARY_STRING {#attributeValueBody = #([ATTRIBUTE_VALUE_PAIR,"AttributeValuePair"], #attributeName, #string);} // try to rename to attributeValuePair
	; 


// #################################################################
// Tree Grammar

class OffsetWalker extends TreeParser;
options{
	k = 2;
}

styleSheet
	: #(STYLE_SHEET (styleSection)*)
	;

styleSection
	: #(STYLE_SECTION a:NAME (NAME)? (att | section)+) {System.out.println("Test:"+a);}
	;

section
	: #(SECTION NAME (att)+)
	;

att
	: #(ATTRIBUTE_VALUE_PAIR name:NAME ARBITRARY_STRING) {System.out.println(name.getLine());}
	;
	