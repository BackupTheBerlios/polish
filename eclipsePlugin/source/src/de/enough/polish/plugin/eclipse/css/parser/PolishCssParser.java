/*
 * PolishCssParser.java
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


import net.percederberg.grammatica.parser.Analyzer;
import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.ProductionPattern;
import net.percederberg.grammatica.parser.ProductionPatternAlternative;
import net.percederberg.grammatica.parser.RecursiveDescentParser;

/**
 * A token stream parser.
 * 
 * @author   Richard Nkrumah <Richard.Nkrumah@enough.de
 * @version  1.0
 */
public class PolishCssParser extends RecursiveDescentParser {

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_1 = 3001;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_2 = 3002;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_3 = 3003;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_4 = 3004;

    /**
     * Creates a new parser.
     * 
     * @param in             the input stream to read from
     * 
     * @throws ParserCreationException if the parser couldn't be
     *             initialized correctly
     */
    public PolishCssParser(Reader in) throws ParserCreationException {
        super(new PolishCssTokenizer(in));
        createPatterns();
    }

    /**
     * Creates a new parser.
     * 
     * @param in             the input stream to read from
     * @param analyzer       the analyzer to use while parsing
     * 
     * @throws ParserCreationException if the parser couldn't be
     *             initialized correctly
     */
    public PolishCssParser(Reader in, Analyzer analyzer)
        throws ParserCreationException {

        super(new PolishCssTokenizer(in), analyzer);
        createPatterns();
    }

    /**
     * Initializes the parser by creating all the production patterns.
     * 
     * @throws ParserCreationException if the parser couldn't be
     *             initialized correctly
     */
    private void createPatterns() throws ParserCreationException {
        ProductionPattern             pattern;
        ProductionPatternAlternative  alt;

        pattern = new ProductionPattern(PolishCssConstants.STYLESHEET,
                                        "stylesheet");
        alt = new ProductionPatternAlternative();
        alt.addProduction(PolishCssConstants.STYLE_SECTION, 1, -1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(PolishCssConstants.NAME,
                                        "name");
        alt = new ProductionPatternAlternative();
        alt.addToken(PolishCssConstants.STRING, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(PolishCssConstants.STYLE_SECTION,
                                        "styleSection");
        alt = new ProductionPatternAlternative();
        alt.addProduction(PolishCssConstants.NAME, 1, 1);
        alt.addProduction(SUBPRODUCTION_1, 0, 1);
        alt.addToken(PolishCssConstants.LEFT_BRACE, 1, 1);
        alt.addProduction(SUBPRODUCTION_3, 0, -1);
        alt.addToken(PolishCssConstants.RIGHT_BRACE, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(PolishCssConstants.SECTION_BODY,
                                        "sectionBody");
        alt = new ProductionPatternAlternative();
        alt.addToken(PolishCssConstants.LEFT_BRACE, 1, 1);
        alt.addProduction(SUBPRODUCTION_4, 0, -1);
        alt.addToken(PolishCssConstants.RIGHT_BRACE, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(PolishCssConstants.ATTRIBUTE_VALUE_BODY,
                                        "attributeValueBody");
        alt = new ProductionPatternAlternative();
        alt.addToken(PolishCssConstants.ANYTHING_TO_SEMICOLON, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_1,
                                        "Subproduction1");
        pattern.setSyntetic(true);
        alt = new ProductionPatternAlternative();
        alt.addToken(PolishCssConstants.EXTENDS, 1, 1);
        alt.addProduction(PolishCssConstants.NAME, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_2,
                                        "Subproduction2");
        pattern.setSyntetic(true);
        alt = new ProductionPatternAlternative();
        alt.addProduction(PolishCssConstants.SECTION_BODY, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addProduction(PolishCssConstants.ATTRIBUTE_VALUE_BODY, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_3,
                                        "Subproduction3");
        pattern.setSyntetic(true);
        alt = new ProductionPatternAlternative();
        alt.addProduction(PolishCssConstants.NAME, 1, 1);
        alt.addProduction(SUBPRODUCTION_2, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_4,
                                        "Subproduction4");
        pattern.setSyntetic(true);
        alt = new ProductionPatternAlternative();
        alt.addProduction(PolishCssConstants.NAME, 1, 1);
        alt.addProduction(PolishCssConstants.ATTRIBUTE_VALUE_BODY, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);
    }
}
