/*
 * PolishCssTokenizer.java
 * 
 * THIS FILE HAS BEEN GENERATED AUTOMATICALLY. DO NOT EDIT!
 * 
 * Permission is granted to copy this document verbatim in any
 * medium, provided that this copyright notice is left intact.
 * 
 * Copyright (c) 2005 Richard Nkrumah. All rights reserved.
 */

package de.enough.polish.plugin.eclipse.css.parser;

import java.io.Reader;


import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.TokenPattern;
import net.percederberg.grammatica.parser.Tokenizer;

/**
 * A character stream tokenizer.
 * 
 * @author   Richard Nkrumah <Richard.Nkrumah@enough.de
 * @version  1.0
 */
public class PolishCssTokenizer extends Tokenizer {

    /**
     * Creates a new tokenizer for the specified input stream.
     * 
     * @param input          the input stream to read
     * 
     * @throws ParserCreationException if the tokenizer couldn't be
     *             initialized correctly
     */
    public PolishCssTokenizer(Reader input)
        throws ParserCreationException {

        super(input);
        createPatterns();
    }

    /**
     * Initializes the tokenizer by creating all the token patterns.
     * 
     * @throws ParserCreationException if the tokenizer couldn't be
     *             initialized correctly
     */
    private void createPatterns() throws ParserCreationException {
        TokenPattern  pattern;

        pattern = new TokenPattern(PolishCssConstants.LEFT_BRACE,
                                   "LEFT_BRACE",
                                   TokenPattern.STRING_TYPE,
                                   "{");
        addPattern(pattern);

        pattern = new TokenPattern(PolishCssConstants.RIGHT_BRACE,
                                   "RIGHT_BRACE",
                                   TokenPattern.STRING_TYPE,
                                   "}");
        addPattern(pattern);

        pattern = new TokenPattern(PolishCssConstants.COLON,
                                   "COLON",
                                   TokenPattern.STRING_TYPE,
                                   ":");
        addPattern(pattern);

        pattern = new TokenPattern(PolishCssConstants.SEMI_COLON,
                                   "SEMI_COLON",
                                   TokenPattern.STRING_TYPE,
                                   ";");
        addPattern(pattern);

        pattern = new TokenPattern(PolishCssConstants.COMMA,
                                   "COMMA",
                                   TokenPattern.STRING_TYPE,
                                   ",");
        addPattern(pattern);

        pattern = new TokenPattern(PolishCssConstants.VBAR,
                                   "VBAR",
                                   TokenPattern.STRING_TYPE,
                                   "|");
        addPattern(pattern);

        pattern = new TokenPattern(PolishCssConstants.LEFT_ROUNDBRACE,
                                   "LEFT_ROUNDBRACE",
                                   TokenPattern.STRING_TYPE,
                                   "(");
        addPattern(pattern);

        pattern = new TokenPattern(PolishCssConstants.RIGHT_ROUNDBRACE,
                                   "RIGHT_ROUNDBRACE",
                                   TokenPattern.STRING_TYPE,
                                   ")");
        addPattern(pattern);

        pattern = new TokenPattern(PolishCssConstants.STRING,
                                   "STRING",
                                   TokenPattern.REGEXP_TYPE,
                                   "[a-zA-Z0-9_\\-%.]*");
        addPattern(pattern);

        pattern = new TokenPattern(PolishCssConstants.QUOTED_STRING,
                                   "QUOTED_STRING",
                                   TokenPattern.REGEXP_TYPE,
                                   "(\"[^\"]*\")|('[^']*')");
        addPattern(pattern);

        pattern = new TokenPattern(PolishCssConstants.WHITESPACE,
                                   "WHITESPACE",
                                   TokenPattern.REGEXP_TYPE,
                                   "[ \\t\\n\\r]+");
        pattern.setIgnore();
        addPattern(pattern);

        pattern = new TokenPattern(PolishCssConstants.IMPORTANT,
                                   "IMPORTANT",
                                   TokenPattern.REGEXP_TYPE,
                                   "![Ii][Mm][Pp][Oo][Rr][Tt][Aa][Nn][Tt]");
        addPattern(pattern);

        pattern = new TokenPattern(PolishCssConstants.MULTI_LINE_COMMENT,
                                   "MULTI_LINE_COMMENT",
                                   TokenPattern.REGEXP_TYPE,
                                   "/\\*([^*]|\\*[^/])*\\*/");
        pattern.setIgnore();
        addPattern(pattern);

        pattern = new TokenPattern(PolishCssConstants.ANYTHING_TO_SEMICOLON,
                                   "ANYTHING_TO_SEMICOLON",
                                   TokenPattern.REGEXP_TYPE,
                                   ":[^;]+?;");
        addPattern(pattern);

        pattern = new TokenPattern(PolishCssConstants.EXTENDS,
                                   "EXTENDS",
                                   TokenPattern.STRING_TYPE,
                                   "extends");
        addPattern(pattern);
    }
}
