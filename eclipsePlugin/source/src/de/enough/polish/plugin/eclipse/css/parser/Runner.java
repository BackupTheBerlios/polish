/*
 * Created on Mar 11, 2005 at 3:02:13 PM.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.plugin.eclipse.css.parser;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Mar 11, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class Runner {
    public static void main(String[] args) {
     
        DataInputStream input;
        try {
            input = new DataInputStream(new FileInputStream("misc/example.css"));
        } catch (FileNotFoundException exception1) {
            // TODO ricky handle FileNotFoundException
            exception1.printStackTrace();
            return;
        }
        CssLexer lexer = new CssLexer(input);
        lexer.setTokenObjectClass("de.enough.polish.plugin.eclipse.css.parser.OffsetToken");

        CssParser parser = new CssParser(lexer);
        parser.setASTNodeClass("de.enough.polish.plugin.eclipse.css.parser.OffsetAST");
        
        try {
            parser.styleSheet();
        } catch (RecognitionException exception) {
            // TODO ricky handle RecognitionException
            exception.printStackTrace();
        } catch (TokenStreamException exception) {
            // TODO ricky handle TokenStreamException
            exception.printStackTrace();
        }
        AST astRoot = parser.getAST();
        if(astRoot == null) {
            System.out.println("Runner.main():No AST generated.");
            return;
        }
        
        /*
        DumpASTVisitor dumpASTVisitor = new DumpASTVisitor();
        dumpASTVisitor.visit(astRoot);

		*/
        OffsetWalker offsetWalker = new OffsetWalker();
        try {
            offsetWalker.styleSheet(astRoot);
            System.out.println("Walker finished.");
        } catch (RecognitionException exception) {
            System.out.println("INTERNAL:DEBUG:CssModel.reconcile():Parse Error in Tree walker."+exception);
        }
       
      }
    
    /**
     * @param astRoot
     */
    /*
    private static void printChildren(AST astRoot) {
        Object[] children = getChildren(astRoot);
        for(int i = 0; i < astRoot.getNumberOfChildren(); i++) {
            System.out.println(children[i]);
        }
    }

    private static Object[] getChildren(AST root) {
        if(root == null) {
            return null;
        }
        ArrayList result = new ArrayList();
        AST child = root.getFirstChild();
        while (child != null) {
            result.add(child);
            child = child.getNextSibling();
        }
        return result.toArray();
    }
    */
}
