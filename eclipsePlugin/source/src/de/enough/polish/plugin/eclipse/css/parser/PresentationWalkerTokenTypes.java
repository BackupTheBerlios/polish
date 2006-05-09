// $ANTLR 2.7.5 (20050128): "presentationWalker.g" -> "PresentationWalker.java"$

package de.enough.polish.plugin.eclipse.css.parser;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.source.ISharedTextColors;

public interface PresentationWalkerTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int L_CURLY_BRACKET = 4;
	int R_CURLY_BRACKET = 5;
	int NAME = 6;
	int ARBITRARY_STRING = 7;
	int WHITESPACE = 8;
	int ML_COMMENT = 9;
	int ATTRIBUTE_VALUE_PAIR = 10;
	int STYLE_SECTION = 11;
	int SECTION = 12;
	int STYLE_SHEET = 13;
}
