/*
 * Created on Mar 21, 2005 at 10:46:12 AM.
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
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.plugin.eclipse.css.parser;

import antlr.ASTVisitor;
import antlr.collections.AST;



/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Mar 21, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class OffsetVisitor implements ASTVisitor {
 
    static int level = 0;
    
    private void tabs() {
        for (int i = 0; i < level; i++) {
            System.out.print("   ");
        }
    }

    /* (non-Javadoc)
     * @see antlr.ASTVisitor#visit(antlr.collections.AST)
     */
    public void visit(AST node) {
        if(node == null) {
            System.out.println("ERROR:OffsetVisitor.visit():Parameter is null.");
            return;
        }
        OffsetAST offsetAST = (OffsetAST)node;
        tabs();
        System.out.println(offsetAST.getText()+":Offset:"+offsetAST.getOffset());
        OffsetAST child;
        child = (OffsetAST)offsetAST.getFirstChild();
        if(child != null) {
            level++;
            visit(child);
            level--;
        }
        
        OffsetAST i = (OffsetAST)offsetAST.getNextSibling();
        while(i != null) {
            tabs();
            System.out.println(i.getText()+":Offset:"+i.getOffset());
            
            child = (OffsetAST)i.getFirstChild();
            if(child != null) {
                level++;
                visit(child);
                level--;
            }
            i = (OffsetAST)i.getNextSibling();
        }
        
        /*
        for(i = offsetAST;i != null; i = (OffsetAST)i.getNextSibling()) {
            tabs();
            System.out.println(i.getText()+":Offset:"+i.getOffset());
        
            OffsetAST child = (OffsetAST)i.getFirstChild();
            
            if(child != null) {
                level++;
                visit(child);
                level--;
            }
        }
        */
        
    }

    
    
}
