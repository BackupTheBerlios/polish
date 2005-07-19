header{
package de.enough.polish.plugin.eclipse.css.parser;
}

// #################################################################
// Lexer

class CssLexer extends Lexer;

options {
    charVocabulary = '\3'..'\377';
    genHashLines=true;
    k=2; 
}

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
								|	'\t' //{this.offset++;} //TODO: mabye we do not need to advance the offset here because of consume.
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
	//: "/*"! (('\n' {newline();}|~('*'|'\n')))* "*/"! { _ttype = Token.SKIP; }
					: "/*" (  { LA(2) != '/' }? '*'| ~('*'))* "*/" {_ttype = Token.SKIP;}
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
	k=2;
}
{
public boolean isExtendToken(Token e) {
    return "extends".equalsIgnoreCase(e.getText());
}

public void configure(OffsetAST node,Token token) {
    node.setOffset(((OffsetToken)token).getOffset());
    node.setLength(token.getText().length());
}

}

styleSheet
	:  (styleSection)+ EOF! {#styleSheet = #([STYLE_SHEET,"StyleSheet"],#styleSheet); } //{styleSheet_AST.setOffset(((OffsetAST)styleSheet_AST.getFirstChild()).getOffset());
	;
	
styleSection
	: styleName:NAME {configure(styleName_AST,styleName);} (e:NAME! {isExtendToken(e)}? parent:NAME)? L_CURLY_BRACKET! (sectionOrAttributeName:NAME! {configure(sectionOrAttributeName_AST,sectionOrAttributeName);} (section:sectionBody[#sectionOrAttributeName] | attributeValuePair:attributeValueBody[#sectionOrAttributeName]))* R_CURLY_BRACKET! {#styleSection = #([STYLE_SECTION,"StyleSection"],#styleSection);configure(styleSection_AST,styleName);}
	;

sectionBody[OffsetAST sectionName]
	: L_CURLY_BRACKET! (attributeName:NAME! {attributeName_AST.setOffset(((OffsetToken)attributeName).getOffset());} attributeValueBody[#attributeName])* R_CURLY_BRACKET! {#sectionBody = #([SECTION,"Section"],#sectionName,#sectionBody);sectionBody_AST.setOffset(sectionName.getOffset());}
	;
	
attributeValueBody[OffsetAST attributeName]
	: string:ARBITRARY_STRING {#attributeValueBody = #([ATTRIBUTE_VALUE_PAIR,"AttributeValuePair"], #attributeName, #string);attributeValueBody_AST.setOffset(attributeName.getOffset());string_AST.setOffset(((OffsetToken)string).getOffset());}
	; 


// #################################################################
// Tree Grammar, we are dealing with AST nodes here.

class OffsetWalker extends TreeParser;
options{
	k = 1; //Maybe we need 2 here because of the ambigutiy between all rules starting with NAME. 
	ASTLabelType = "OffsetAST";
}

styleSheet
	: #(STYLE_SHEET (styleSection)*)
	;

styleSection
	: #(STYLE_SECTION name:NAME (NAME)? (att | section)+) {System.out.println("StyleSectionOffset:"+styleSection_AST_in.getOffset());}
	;

section
	: #(SECTION name:NAME (att)+) {System.out.println("SectionOffset:"+name.getOffset());}
	;

att
	: #(ATTRIBUTE_VALUE_PAIR name:NAME ARBITRARY_STRING) {System.out.println("AttributeOffset:"+name.getOffset());}
	;
	
	
