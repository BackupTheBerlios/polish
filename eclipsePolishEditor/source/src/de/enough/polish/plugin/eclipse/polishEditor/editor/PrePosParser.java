/*
 * Created on May 20, 2005 at 2:56:21 PM.
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
package de.enough.polish.plugin.eclipse.polishEditor.editor;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;

import de.enough.polish.plugin.eclipse.polishEditor.PolishEditorPlugin;

//TODO: This class should parse a line with a preposPrefix and return a list of tokens.
public class PrePosParser {
    
    private static final String WHITESPACE_PATTERN = "(\\s+)";
    private static final String PREPOS_PATTERN = "//#\\w+";
    private static final String SYMBOL_PATTERN = "\\w+()";
    private static final String VARIABLE_BRACKETS = "\\$\\{[\\w.]+\\}"; // TODO: Do we need ':' as character, too?
    private static final String VARIABLE_BEFORE_OP = "[\\w.]+(?=\\s*(?:!=|[><]=?|==))";
    private static final String VARIABLE_AFTER_OP = "(?!(?:==|!=|=?[><]))\\s*[\\w.]+";
    
    private static final String PREPOS_LINE_PATTERN = "PREPOS_WHITESPACE_PATTERN";
    
    
    private IDocument document;
    private Pattern pattern;
    private Matcher matcher;
    
    public PrePosParser(IDocument document) {
        this.pattern = Pattern.compile(null);
        this.matcher = this.pattern.matcher("");
    }
    
    private boolean isConfigured() {
        return this.document != null;
    }
    
    private void throwExceptionIfNotConfigured() {
        if(!isConfigured()) {
            throw new IllegalStateException("PrePosParser:throwExceptionIfNotConfigured():this is not configured.");
        }
    }
    
    public Position parsePrePosLine(int startIndex, int length) {
        throwExceptionIfNotConfigured();
        return null;
    }
    
    public List tokenizePrePosLine(int startIndex, int length) {
        LinkedList listOfTokens = new LinkedList();
        try {
            String text = this.document.get(startIndex,length);
            int i = 0;
            while(true) {
                
            }
            
            
            
        }
        catch (BadLocationException exception) {
            PolishEditorPlugin.log("PrePosParser.tokenizePrePosLine:The parameters startIndex and length are invalid.",exception);
        }
        
        
        return listOfTokens;
    }

}
