header{
package de.enough.polish.plugin.eclipse.css.parser;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.source.ISharedTextColors;
}

class PresentationWalker extends TreeParser;

options{
	k = 1;
	ASTLabelType = "OffsetAST";
	importVocab=CssLexer;
}

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
}


styleSheet
	: #(STYLE_SHEET (styleSection)*)
	;

styleSection
	: #(STYLE_SECTION name:NAME (NAME)? (att | section)+) {addTextStyle(name.getOffset(),name.getLength(),0,200,0);}
	;
	
section
	: #(SECTION name:NAME (att)+) {addTextStyle(name.getOffset(),name.getLength(),200,0,0);}
	;

att
	: #(ATTRIBUTE_VALUE_PAIR name:NAME ARBITRARY_STRING) {addTextStyle(name.getOffset(),name.getLength(),0,0,200);}
	;
