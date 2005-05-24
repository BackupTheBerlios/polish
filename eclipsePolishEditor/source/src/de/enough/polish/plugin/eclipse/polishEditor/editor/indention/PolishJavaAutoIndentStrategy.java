/*
 * Created on May 19, 2005 at 12:53:54 PM.
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
package de.enough.polish.plugin.eclipse.polishEditor.editor.indention;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jdt.internal.corext.Assert;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.text.FastJavaPartitionScanner;
import org.eclipse.jdt.internal.ui.text.JavaHeuristicScanner;
import org.eclipse.jdt.internal.ui.text.JavaIndenter;
import org.eclipse.jdt.internal.ui.text.Symbols;
import org.eclipse.jdt.internal.ui.text.java.JavaAutoIndentStrategy;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.rules.FastPartitioner;

/**
 * This class is copied nearly verbatim from JavaautoIndentStrategy. This was nessessary to circumvent
 * the 'pasting comment' behavior (splitting the comment '//    #def' on paste). The only real difference is in addIndent(...)
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        May 24, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class PolishJavaAutoIndentStrategy extends JavaAutoIndentStrategy {

    private static final String LINE_COMMENT= "//"; //$NON-NLS-1$
    private static final String POLISH_LINE_START = LINE_COMMENT + "#";
        IJavaProject project;
    public PolishJavaAutoIndentStrategy(String partitioning, IJavaProject project) {
        super(partitioning, project);
        this.project = project;
    }

    public void customizeDocumentCommand(IDocument d, DocumentCommand c) {
        System.out.println("DEBUG:PolishJavaAutoIndentStrategy.customizeDocumentCommand(...):enter.");
        //TODO: Problem: In our polish preference store we do not have the properties from the JavaEditor.
        if (c.text.length() > 1 && getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_SMART_PASTE)) {
            System.out.println("DEBUG:PolishJavaAutoIndentStrategy.customizeDocumentCommand(...):try to start polishSmartPaste.");
            polishSmartPaste(d, c);
            return;
        }
        super.customizeDocumentCommand(d,c);
    }
    
    // Keep the javaplugin preferenceStore as we need to look up jdt specific settings only.
    private static IPreferenceStore getPreferenceStore() {
        return JavaPlugin.getDefault().getPreferenceStore();
    }
    
    private void polishSmartPaste(IDocument document,DocumentCommand command) {
        int newOffset= command.offset;
        int newLength= command.length;
        String newText= command.text;
        
        try {
            JavaHeuristicScanner scanner= new JavaHeuristicScanner(document);
            JavaIndenter indenter= new JavaIndenter(document, scanner);
            int offset= newOffset;
            
            // reference position to get the indent from
            int refOffset= indenter.findReferencePosition(offset);
            if (refOffset == JavaHeuristicScanner.NOT_FOUND)
                return;
            int peerOffset= getPeerPosition(document, command);
            peerOffset= indenter.findReferencePosition(peerOffset);
            refOffset= Math.min(refOffset, peerOffset);
            
            // eat any WS before the insertion to the beginning of the line
            int firstLine= 1; // don't format the first line per default, as it has other content before it
            IRegion line= document.getLineInformationOfOffset(offset);
            String notSelected= document.get(line.getOffset(), offset - line.getOffset());
            if (notSelected.trim().length() == 0) {
                newLength += notSelected.length();
                newOffset= line.getOffset();
                firstLine= 0;
            }
            
            // prefix: the part we need for formatting but won't paste
            IRegion refLine= document.getLineInformationOfOffset(refOffset);
            String prefix= document.get(refLine.getOffset(), newOffset - refLine.getOffset());
            
            // handle the indentation computation inside a temporary document
            Document temp= new Document(prefix + newText);
            scanner= new JavaHeuristicScanner(temp);
            indenter= new JavaIndenter(temp, scanner);
            installJavaStuff(temp);
            
            // indent the first and second line
            // compute the relative indentation difference from the second line
            // (as the first might be partially selected) and use the value to
            // indent all other lines.
            boolean isIndentDetected= false;
            StringBuffer addition= new StringBuffer();
            int insertLength= 0;
            int first= document.computeNumberOfLines(prefix) + firstLine; // don't format first line
            int lines= temp.getNumberOfLines();
            for (int l= first; l < lines; l++) { // we don't change the number of lines while adding indents
                
                IRegion r= temp.getLineInformation(l);
                int lineOffset= r.getOffset();
                int lineLength= r.getLength();
                
                if (lineLength == 0) // don't modify empty lines
                    continue;
                
                if (!isIndentDetected){
                    
                    // indent the first pasted line
                    String current= getCurrentIndent(temp, l);
                    StringBuffer correct= indenter.computeIndentation(lineOffset);
                    if (correct == null)
                        return; // bail out
                    
                    insertLength= subtractIndent(correct, current, addition);
                    if (l != first)
                        isIndentDetected= true;
                }
                
                // relatively indent all pasted lines 
                if (insertLength > 0)
                    addIndent(temp, l, addition);
                else if (insertLength < 0)
                    cutIndent(temp, l, -insertLength);
                
            }
            
            newText= temp.get(prefix.length(), temp.getLength() - prefix.length());
            
            // only modify the command if it will be different from what
            // the normal insertion would produce
            String orig= document.get(newOffset, command.offset - newOffset);
            orig += command.text;
            orig += document.get(command.offset + command.length, newLength - command.length - command.offset + newOffset);

            if (!orig.equals(newText)) {
                command.offset= newOffset;
                command.length= newLength;
                command.text= newText;
            }
            
        } catch (BadLocationException e) {
            JavaPlugin.log(e);
        }
    }
    
    private static int getPeerPosition(IDocument document, DocumentCommand command) {
        if (document.getLength() == 0)
            return 0;
        /*
         * Search for scope closers in the pasted text and find their opening peers
         * in the document. 
         */
        Document pasted= new Document(command.text);
        installJavaStuff(pasted);
        int firstPeer= command.offset;
        
        JavaHeuristicScanner pScanner= new JavaHeuristicScanner(pasted);
        JavaHeuristicScanner dScanner= new JavaHeuristicScanner(document);
        
        // add scope relevant after context to peer search
        int afterToken= dScanner.nextToken(command.offset + command.length, JavaHeuristicScanner.UNBOUND);
        try {
            switch (afterToken) {
            case Symbols.TokenRBRACE:
                pasted.replace(pasted.getLength(), 0, "}"); //$NON-NLS-1$
                break;
            case Symbols.TokenRPAREN:
                pasted.replace(pasted.getLength(), 0, ")"); //$NON-NLS-1$
                break;
            case Symbols.TokenRBRACKET:
                pasted.replace(pasted.getLength(), 0, "]"); //$NON-NLS-1$
                break;
            }
        } catch (BadLocationException e) {
            // cannot happen
            Assert.isTrue(false);
        }
        
        int pPos= 0; // paste text position (increasing from 0)
        int dPos= Math.max(0, command.offset - 1); // document position (decreasing from paste offset)
        while (true) {
            int token= pScanner.nextToken(pPos, JavaHeuristicScanner.UNBOUND);
            pPos= pScanner.getPosition();
            switch (token) {
                case Symbols.TokenLBRACE:
                case Symbols.TokenLBRACKET:
                case Symbols.TokenLPAREN:
                    pPos= skipScope(pScanner, pPos, token);
                    if (pPos == JavaHeuristicScanner.NOT_FOUND)
                        return firstPeer;
                    break; // closed scope -> keep searching
                case Symbols.TokenRBRACE:
                    int peer= dScanner.findOpeningPeer(dPos, '{', '}');
                    dPos= peer - 1; 
                    if (peer == JavaHeuristicScanner.NOT_FOUND)
                        return firstPeer;
                    firstPeer= peer;
                    break; // keep searching
                case Symbols.TokenRBRACKET:
                    peer= dScanner.findOpeningPeer(dPos, '[', ']');
                    dPos= peer - 1; 
                    if (peer == JavaHeuristicScanner.NOT_FOUND)
                        return firstPeer;
                    firstPeer= peer;
                    break; // keep searching
                case Symbols.TokenRPAREN:
                    peer= dScanner.findOpeningPeer(dPos, '(', ')');
                    dPos= peer - 1; 
                    if (peer == JavaHeuristicScanner.NOT_FOUND)
                        return firstPeer;
                    firstPeer= peer;
                    break; // keep searching
                case Symbols.TokenCASE:
                case Symbols.TokenDEFAULT:
                    JavaIndenter indenter= new JavaIndenter(document, dScanner);
                    peer= indenter.findReferencePosition(dPos, false, false, false, true);
                    if (peer == JavaHeuristicScanner.NOT_FOUND)
                        return firstPeer;
                    firstPeer= peer;
                    break; // keep searching
                    
                case Symbols.TokenEOF:
                    return firstPeer;
                default:
                    // keep searching
            }
        }
    }
            
    private static void installJavaStuff(Document document) {
        String[] types= new String[] {
                                      IJavaPartitions.JAVA_DOC,
                                      IJavaPartitions.JAVA_MULTI_LINE_COMMENT,
                                      IJavaPartitions.JAVA_SINGLE_LINE_COMMENT,
                                      IJavaPartitions.JAVA_STRING,
                                      IJavaPartitions.JAVA_CHARACTER,
                                      IDocument.DEFAULT_CONTENT_TYPE
        };
        FastPartitioner partitioner= new FastPartitioner(new FastJavaPartitionScanner(), types);
        partitioner.connect(document);
        document.setDocumentPartitioner(IJavaPartitions.JAVA_PARTITIONING, partitioner);
    }
    
    private static String getCurrentIndent(Document document, int line) throws BadLocationException {
        IRegion region= document.getLineInformation(line);
        int from= region.getOffset();
        int endOffset= region.getOffset() + region.getLength();
        
        // go behind line comments
        int to= from;
        while (to < endOffset - 2 && document.get(to, 2).equals(LINE_COMMENT))
            to += 2;
        
        while (to < endOffset) {
            char ch= document.getChar(to);
            if (!Character.isWhitespace(ch))
                break;
            to++;
        }
        
        // don't count the space before javadoc like, asterix-style comment lines
        if (to > from && to < endOffset - 1 && document.get(to - 1, 2).equals(" *")) { //$NON-NLS-1$
            String type= TextUtilities.getContentType(document, IJavaPartitions.JAVA_PARTITIONING, to, true);
            if (type.equals(IJavaPartitions.JAVA_DOC) || type.equals(IJavaPartitions.JAVA_MULTI_LINE_COMMENT))
                to--;
        }
        
        return document.get(from, to - from);
    }
        
    private int subtractIndent(CharSequence correct, CharSequence current, StringBuffer difference) {
        int c1= computeVisualLength(correct);
        int c2= computeVisualLength(current);
        int diff= c1 - c2;
        if (diff <= 0)
            return diff;
        
        difference.setLength(0);
        int len= 0, i= 0;
        while (len < diff) {
            char c= correct.charAt(i++);
            difference.append(c);
            len += computeVisualLength(c);
        }
        
        
        return diff;
    }
        
    private static void addIndent(Document document, int line, CharSequence indent) throws BadLocationException {
        IRegion region= document.getLineInformation(line);
        int insert= region.getOffset();
        int endOffset= region.getOffset() + region.getLength();
        
        //TODO: Use trim to eat whitespaces at the start of the line.
        if(document.get(insert, 3).equals(POLISH_LINE_START)) {
            document.replace(insert, 0, indent.toString()+"  ");
            return;
        }
        // go behind line comments
        while (insert < endOffset - 2 && document.get(insert, 2).equals(LINE_COMMENT))
            insert += 2;
        
        // insert indent
        document.replace(insert, 0, indent.toString());
    }
    
    private int computeVisualLength(CharSequence seq) {
        int size= 0;
        int tablen= getVisualTabLengthPreference();
        
        for (int i= 0; i < seq.length(); i++) {
            char ch= seq.charAt(i);
            if (ch == '\t')
                size += tablen - size % tablen;
            else
                size++;
        }
        return size;
    }
    
    private int getVisualTabLengthPreference() {
        String val;
        if (this.project == null)
            val= JavaCore.getOption(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
        else
            val= this.project.getOption(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, true);
        
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            // sensible default
            return 4;
        }
    }
    
    private void cutIndent(Document document, int line, int toDelete) throws BadLocationException {
        IRegion region= document.getLineInformation(line);
        int from= region.getOffset();
        int endOffset= region.getOffset() + region.getLength();
        
        // go behind line comments
        while (from < endOffset - 2 && document.get(from, 2).equals(LINE_COMMENT))
            from += 2;
        
        int to= from;
        while (toDelete > 0 && to < endOffset) {
            char ch= document.getChar(to);
            if (!Character.isWhitespace(ch))
                break;
            toDelete -= computeVisualLength(ch);
            if (toDelete >= 0)
                to++;
            else
                break;
        }  
        document.replace(from, to - from, null);
    }
    
    private static int skipScope(JavaHeuristicScanner scanner, int pos, int token) {
        int openToken= token;
        int closeToken;
        switch (token) {
            case Symbols.TokenLPAREN:
                closeToken= Symbols.TokenRPAREN;
                break;
            case Symbols.TokenLBRACKET:
                closeToken= Symbols.TokenRBRACKET;
                break;
            case Symbols.TokenLBRACE:
                closeToken= Symbols.TokenRBRACE;
                break;
            default:
                Assert.isTrue(false);
                return -1; // dummy
        }
        
        int depth= 1;
        int p= pos;

        while (true) {
            int tok= scanner.nextToken(p, JavaHeuristicScanner.UNBOUND);
            p= scanner.getPosition();
            
            if (tok == openToken) {
                depth++;
            } else if (tok == closeToken) {
                depth--;
                if (depth == 0)
                    return p + 1;
            } else if (tok == Symbols.TokenEOF) {
                return JavaHeuristicScanner.NOT_FOUND;
            }
        }
    }
    
    private int computeVisualLength(char ch) {
        if (ch == '\t') {
            return getVisualTabLengthPreference();
        }
        return 1;
    }
}
