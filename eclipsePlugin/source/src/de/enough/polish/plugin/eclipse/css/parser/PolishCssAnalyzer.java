/*
 * PolishCssAnalyzer.java
 * 
 * THIS FILE HAS BEEN GENERATED AUTOMATICALLY. DO NOT EDIT!
 * 
 * Permission is granted to copy this document verbatim in any
 * medium, provided that this copyright notice is left intact.
 * 
 * Copyright (c) 2005 Richard Nkrumah. All rights reserved.
 */

package de.enough.polish.plugin.eclipse.css.parser;

import net.percederberg.grammatica.parser.Analyzer;
import net.percederberg.grammatica.parser.Node;
import net.percederberg.grammatica.parser.ParseException;
import net.percederberg.grammatica.parser.Production;
import net.percederberg.grammatica.parser.Token;

/**
 * A class providing callback methods for the parser.
 * 
 * @author   Richard Nkrumah <Richard.Nkrumah@enough.de
 * @version  1.0
 */
public abstract class PolishCssAnalyzer extends Analyzer {

    /**
     * Called when entering a parse tree node.
     * 
     * @param node           the node being entered
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enter(Node node) throws ParseException {
        switch (node.getId()) {
        case PolishCssConstants.LEFT_BRACE:
            enterLeftBrace((Token) node);
            break;
        case PolishCssConstants.RIGHT_BRACE:
            enterRightBrace((Token) node);
            break;
        case PolishCssConstants.COLON:
            enterColon((Token) node);
            break;
        case PolishCssConstants.SEMI_COLON:
            enterSemiColon((Token) node);
            break;
        case PolishCssConstants.COMMA:
            enterComma((Token) node);
            break;
        case PolishCssConstants.VBAR:
            enterVbar((Token) node);
            break;
        case PolishCssConstants.LEFT_ROUNDBRACE:
            enterLeftRoundbrace((Token) node);
            break;
        case PolishCssConstants.RIGHT_ROUNDBRACE:
            enterRightRoundbrace((Token) node);
            break;
        case PolishCssConstants.STRING:
            enterString((Token) node);
            break;
        case PolishCssConstants.QUOTED_STRING:
            enterQuotedString((Token) node);
            break;
        case PolishCssConstants.IMPORTANT:
            enterImportant((Token) node);
            break;
        case PolishCssConstants.ANYTHING_TO_SEMICOLON:
            enterAnythingToSemicolon((Token) node);
            break;
        case PolishCssConstants.EXTENDS:
            enterExtends((Token) node);
            break;
        case PolishCssConstants.STYLESHEET:
            enterStylesheet((Production) node);
            break;
        case PolishCssConstants.NAME:
            enterName((Production) node);
            break;
        case PolishCssConstants.STYLE_SECTION:
            enterStyleSection((Production) node);
            break;
        case PolishCssConstants.SECTION_BODY:
            enterSectionBody((Production) node);
            break;
        case PolishCssConstants.ATTRIBUTE_VALUE_BODY:
            enterAttributeValueBody((Production) node);
            break;
        }
    }

    /**
     * Called when exiting a parse tree node.
     * 
     * @param node           the node being exited
     * 
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exit(Node node) throws ParseException {
        switch (node.getId()) {
        case PolishCssConstants.LEFT_BRACE:
            return exitLeftBrace((Token) node);
        case PolishCssConstants.RIGHT_BRACE:
            return exitRightBrace((Token) node);
        case PolishCssConstants.COLON:
            return exitColon((Token) node);
        case PolishCssConstants.SEMI_COLON:
            return exitSemiColon((Token) node);
        case PolishCssConstants.COMMA:
            return exitComma((Token) node);
        case PolishCssConstants.VBAR:
            return exitVbar((Token) node);
        case PolishCssConstants.LEFT_ROUNDBRACE:
            return exitLeftRoundbrace((Token) node);
        case PolishCssConstants.RIGHT_ROUNDBRACE:
            return exitRightRoundbrace((Token) node);
        case PolishCssConstants.STRING:
            return exitString((Token) node);
        case PolishCssConstants.QUOTED_STRING:
            return exitQuotedString((Token) node);
        case PolishCssConstants.IMPORTANT:
            return exitImportant((Token) node);
        case PolishCssConstants.ANYTHING_TO_SEMICOLON:
            return exitAnythingToSemicolon((Token) node);
        case PolishCssConstants.EXTENDS:
            return exitExtends((Token) node);
        case PolishCssConstants.STYLESHEET:
            return exitStylesheet((Production) node);
        case PolishCssConstants.NAME:
            return exitName((Production) node);
        case PolishCssConstants.STYLE_SECTION:
            return exitStyleSection((Production) node);
        case PolishCssConstants.SECTION_BODY:
            return exitSectionBody((Production) node);
        case PolishCssConstants.ATTRIBUTE_VALUE_BODY:
            return exitAttributeValueBody((Production) node);
        }
        return node;
    }

    /**
     * Called when adding a child to a parse tree node.
     * 
     * @param node           the parent node
     * @param child          the child node, or null
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void child(Production node, Node child)
        throws ParseException {

        switch (node.getId()) {
        case PolishCssConstants.STYLESHEET:
            childStylesheet(node, child);
            break;
        case PolishCssConstants.NAME:
            childName(node, child);
            break;
        case PolishCssConstants.STYLE_SECTION:
            childStyleSection(node, child);
            break;
        case PolishCssConstants.SECTION_BODY:
            childSectionBody(node, child);
            break;
        case PolishCssConstants.ATTRIBUTE_VALUE_BODY:
            childAttributeValueBody(node, child);
            break;
        }
    }

    /**
     * Called when entering a parse tree node.
     * 
     * @param node           the node being entered
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterLeftBrace(Token node) throws ParseException {
    // Can be overwritten by clients.
    	}

    /**
     * Called when exiting a parse tree node.
     * 
     * @param node           the node being exited
     * 
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitLeftBrace(Token node) throws ParseException {
        return node;
    }

    /**
     * Called when entering a parse tree node.
     * 
     * @param node           the node being entered
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterRightBrace(Token node) throws ParseException {
    	// Can be overwritten by clients.
		}

    /**
     * Called when exiting a parse tree node.
     * 
     * @param node           the node being exited
     * 
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitRightBrace(Token node) throws ParseException {
        return node;
    }

    /**
     * Called when entering a parse tree node.
     * 
     * @param node           the node being entered
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterColon(Token node) throws ParseException {
//    	 Can be overwritten by clients.
    	}

    /**
     * Called when exiting a parse tree node.
     * 
     * @param node           the node being exited
     * 
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitColon(Token node) throws ParseException {
        return node;
    }

    /**
     * Called when entering a parse tree node.
     * 
     * @param node           the node being entered
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterSemiColon(Token node) throws ParseException {
//    	 Can be overwritten by clients.
    	}

    /**
     * Called when exiting a parse tree node.
     * 
     * @param node           the node being exited
     * 
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitSemiColon(Token node) throws ParseException {
        return node;
    }

    /**
     * Called when entering a parse tree node.
     * 
     * @param node           the node being entered
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterComma(Token node) throws ParseException {
//    	 Can be overwritten by clients.
    	}

    /**
     * Called when exiting a parse tree node.
     * 
     * @param node           the node being exited
     * 
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitComma(Token node) throws ParseException {
        return node;
    }

    /**
     * Called when entering a parse tree node.
     * 
     * @param node           the node being entered
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterVbar(Token node) throws ParseException {
//    	 Can be overwritten by clients.
    	}

    /**
     * Called when exiting a parse tree node.
     * 
     * @param node           the node being exited
     * 
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitVbar(Token node) throws ParseException {
        return node;
    }

    /**
     * Called when entering a parse tree node.
     * 
     * @param node           the node being entered
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterLeftRoundbrace(Token node)
        throws ParseException {
//    	 Can be overwritten by clients.
    	}

    /**
     * Called when exiting a parse tree node.
     * 
     * @param node           the node being exited
     * 
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitLeftRoundbrace(Token node)
        throws ParseException {

        return node;
    }

    /**
     * Called when entering a parse tree node.
     * 
     * @param node           the node being entered
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterRightRoundbrace(Token node)
        throws ParseException {
//    	 Can be overwritten by clients.
    	}

    /**
     * Called when exiting a parse tree node.
     * 
     * @param node           the node being exited
     * 
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitRightRoundbrace(Token node)
        throws ParseException {

        return node;
    }

    /**
     * Called when entering a parse tree node.
     * 
     * @param node           the node being entered
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterString(Token node) throws ParseException {
//    	 Can be overwritten by clients.
    	}

    /**
     * Called when exiting a parse tree node.
     * 
     * @param node           the node being exited
     * 
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitString(Token node) throws ParseException {
        return node;
    }

    /**
     * Called when entering a parse tree node.
     * 
     * @param node           the node being entered
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterQuotedString(Token node) throws ParseException {
//    	 Can be overwritten by clients.
    	}

    /**
     * Called when exiting a parse tree node.
     * 
     * @param node           the node being exited
     * 
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitQuotedString(Token node) throws ParseException {
        return node;
    }

    /**
     * Called when entering a parse tree node.
     * 
     * @param node           the node being entered
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterImportant(Token node) throws ParseException {
//    	 Can be overwritten by clients.
    	}

    /**
     * Called when exiting a parse tree node.
     * 
     * @param node           the node being exited
     * 
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitImportant(Token node) throws ParseException {
        return node;
    }

    /**
     * Called when entering a parse tree node.
     * 
     * @param node           the node being entered
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterAnythingToSemicolon(Token node)
        throws ParseException {
//    	 Can be overwritten by clients.
    	}

    /**
     * Called when exiting a parse tree node.
     * 
     * @param node           the node being exited
     * 
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitAnythingToSemicolon(Token node)
        throws ParseException {

        return node;
    }

    /**
     * Called when entering a parse tree node.
     * 
     * @param node           the node being entered
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterExtends(Token node) throws ParseException {
//    	 Can be overwritten by clients.
    	}

    /**
     * Called when exiting a parse tree node.
     * 
     * @param node           the node being exited
     * 
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitExtends(Token node) throws ParseException {
        return node;
    }

    /**
     * Called when entering a parse tree node.
     * 
     * @param node           the node being entered
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterStylesheet(Production node)
        throws ParseException {
//    	 Can be overwritten by clients.
    	}

    /**
     * Called when exiting a parse tree node.
     * 
     * @param node           the node being exited
     * 
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitStylesheet(Production node)
        throws ParseException {

        return node;
    }

    /**
     * Called when adding a child to a parse tree node.
     * 
     * @param node           the parent node
     * @param child          the child node, or null
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void childStylesheet(Production node, Node child)
        throws ParseException {

        node.addChild(child);
    }

    /**
     * Called when entering a parse tree node.
     * 
     * @param node           the node being entered
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterName(Production node) throws ParseException {
//    	 Can be overwritten by clients.
    	}

    /**
     * Called when exiting a parse tree node.
     * 
     * @param node           the node being exited
     * 
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitName(Production node) throws ParseException {
        return node;
    }

    /**
     * Called when adding a child to a parse tree node.
     * 
     * @param node           the parent node
     * @param child          the child node, or null
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void childName(Production node, Node child)
        throws ParseException {

        node.addChild(child);
    }

    /**
     * Called when entering a parse tree node.
     * 
     * @param node           the node being entered
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterStyleSection(Production node)
        throws ParseException {
//    	 Can be overwritten by clients.
    	}

    /**
     * Called when exiting a parse tree node.
     * 
     * @param node           the node being exited
     * 
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitStyleSection(Production node)
        throws ParseException {

        return node;
    }

    /**
     * Called when adding a child to a parse tree node.
     * 
     * @param node           the parent node
     * @param child          the child node, or null
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void childStyleSection(Production node, Node child)
        throws ParseException {

        node.addChild(child);
    }

    /**
     * Called when entering a parse tree node.
     * 
     * @param node           the node being entered
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterSectionBody(Production node)
        throws ParseException {
//    	 Can be overwritten by clients.
    	}

    /**
     * Called when exiting a parse tree node.
     * 
     * @param node           the node being exited
     * 
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitSectionBody(Production node)
        throws ParseException {

        return node;
    }

    /**
     * Called when adding a child to a parse tree node.
     * 
     * @param node           the parent node
     * @param child          the child node, or null
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void childSectionBody(Production node, Node child)
        throws ParseException {

        node.addChild(child);
    }

    /**
     * Called when entering a parse tree node.
     * 
     * @param node           the node being entered
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterAttributeValueBody(Production node)
        throws ParseException {
//    	 Can be overwritten by clients.
    	}

    /**
     * Called when exiting a parse tree node.
     * 
     * @param node           the node being exited
     * 
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitAttributeValueBody(Production node)
        throws ParseException {

        return node;
    }

    /**
     * Called when adding a child to a parse tree node.
     * 
     * @param node           the parent node
     * @param child          the child node, or null
     * 
     * @throws ParseException if the node analysis discovered errors
     */
    protected void childAttributeValueBody(Production node, Node child)
        throws ParseException {

        node.addChild(child);
    }
}
