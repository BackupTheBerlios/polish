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

protected Token makeToken(int t) {
    Token token = super.makeToken(t);
    if (token != Token.badToken) {
        ((OffsetToken)token).setOffset(this.offset);
    }
    return token;
}

public void consume() throws CharStreamException {
	super.consume();
	this.offset = this.offset + 1;
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
	:  ((ML_COMMENT)? styleSection)+ EOF! {#styleSheet = #([STYLE_SHEET,"StyleSheet"],#styleSheet); } //{styleSheet_AST.setOffset(((OffsetAST)styleSheet_AST.getFirstChild()).getOffset());
	;
	
styleSection
	: styleName:NAME {}(e:NAME! {isExtendToken(e)}? parent:NAME)? L_CURLY_BRACKET! (sectionOrAttributeName:NAME! {sectionOrAttributeName_AST.setOffset(((OffsetToken)sectionOrAttributeName).getOffset());} (section:sectionBody[#sectionOrAttributeName] | attributeValuePair:attributeValueBody[#sectionOrAttributeName]))* R_CURLY_BRACKET! {#styleSection = #([STYLE_SECTION,"StyleSection"],#styleSection);styleSection_AST.setOffset(((OffsetToken)styleName).getOffset());}
	;

sectionBody[OffsetAST sectionName]
	: L_CURLY_BRACKET! (attributeName:NAME! attributeValueBody[#attributeName])* R_CURLY_BRACKET! {sectionBody_AST.setOffset(sectionName.getOffset());#sectionBody = #([SECTION,"Section"],#sectionName,#sectionBody);}
	;
	
attributeValueBody[OffsetAST attributeName]
	: string:ARBITRARY_STRING {attributeValueBody_AST.setOffset(attributeName.getOffset());#attributeValueBody = #([ATTRIBUTE_VALUE_PAIR,"AttributeValuePair"], #attributeName, #string);} // try to rename to attributeValuePair
	; 


// #################################################################
// Tree Grammar, we are dealing with AST nodes here.

class OffsetWalker extends TreeParser;
options{
	k = 2;
	ASTLabelType = "OffsetAST";
}

styleSheet
	: #(STYLE_SHEET (styleSection)*)
	;

styleSection
	: #(STYLE_SECTION name:NAME (NAME)? (att | section)+) {System.out.println("StyleSectionOffset:"+name.getOffset());}
	;

section
	: #(SECTION name:NAME (att)+) {System.out.println("SectionOffset:"+name.getOffset());}
	;

att
	: #(ATTRIBUTE_VALUE_PAIR name:NAME ARBITRARY_STRING) {System.out.println("AttributeOffset:"+name.getOffset());}
	;
	