/*
 * Created on May 18, 2005 at 1:45:45 PM.
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

import org.eclipse.jdt.internal.ui.text.template.contentassist.PositionBasedCompletionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import de.enough.polish.plugin.eclipse.polishEditor.IPolishConstants;
import de.enough.polish.plugin.eclipse.polishEditor.PolishEditorPlugin;
import de.enough.polish.plugin.eclipse.polishEditor.utils.PolishDocumentUtils;

public class DirectiveContentAssistProcessor implements IContentAssistProcessor {

    private String errorMessage = null;
    //private Position wordAtCaret = null;
    
    //TODO: This class needs a major rewrite.
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
                                                            int offset) {
        System.out.println("DEBUG:PolishContentAssistProcessor.computeCompletionProposals(...):enter.");
        if( ! PolishDocumentUtils.isPolishLine(viewer.getDocument(),offset)) {
            return new ICompletionProposal[] {};
        }
        IDocument document = viewer.getDocument();
        
//        Position wordAtCaret;
//        wordAtCaret = getPositionFromDirectiveAtCaret(document, offset);
//        if(wordAtCaret != null) {
//            completeDirectives(wordAtCaret);
//        }
//        wordAtCaret = getPositionFromSybolAtCaret(document, offset);
//        if(wordAtCaret != null) {
//            completeSymbols(wordAtCaret);
//        }
//        wordAtCaret = getPositionFromVriableAtCaret(document, offset);
//        if(wordAtCaret != null) {
//            completeVariables(wordAtCaret);
//        }
//        List linkedList = new LinkedList();
//        return (ICompletionProposal[]) linkedList.toArray(new ICompletionProposal[linkedList.size()]);

        int lineInDocument;
        Position startOfDirectiveAsPosition;
        String startOfDirectiveAsString = "";
        try {
            lineInDocument = document.getLineOfOffset(offset);
            startOfDirectiveAsPosition = PolishDocumentUtils.getDirectiveFromLine(document,lineInDocument);
            startOfDirectiveAsString = PolishDocumentUtils.makeStringFromPosition(document,startOfDirectiveAsPosition);
            
        } catch (BadLocationException exception) {
            PolishEditorPlugin.log("Bad Location encountered while performing method.",exception);
            return new ICompletionProposal[] {};
        }
        List completionProposals = new LinkedList();
        
        // We are in not in the directive, so complete symbols or variables. Check if we are before the prefix !
        if(startOfDirectiveAsPosition != null) {
            for(int index = 0; index < IPolishConstants.POLISH_DIRECTIVES.length; index++) {
                if(IPolishConstants.POLISH_DIRECTIVES[index].startsWith(startOfDirectiveAsString)) {
                    StringBuffer replacementText = new StringBuffer();
                    replacementText.append(IPolishConstants.POLISH_DIRECTIVES[index]);
                    replacementText.append(" ");
                    completionProposals.add(new PositionBasedCompletionProposal(replacementText.toString(),startOfDirectiveAsPosition,IPolishConstants.POLISH_DIRECTIVES[index].length()+1));
                } 
            }
            return (ICompletionProposal[])completionProposals.toArray(new ICompletionProposal[completionProposals.size()]);
        }
        
        return new ICompletionProposal[] {};
    }
    

//  startOfDirectiveAsPosition = new Position(offset,0);
    public IContextInformation[] computeContextInformation(ITextViewer viewer,
                                                           int offset) {
        
        return null;
    }

    public char[] getCompletionProposalAutoActivationCharacters() {
        
        return null;
    }

    public char[] getContextInformationAutoActivationCharacters() {
        
        return null;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public IContextInformationValidator getContextInformationValidator() {
        // TODO ricky implement getContextInformationValidator
        return null;
    }

}
