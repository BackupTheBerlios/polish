/*
 * Created on Jul 27, 2006 at 4:49:15 PM.
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
package de.enough.polish.plugin.eclipse.polishEditor.editor.contentAssist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import de.enough.mepose.core.model.MeposeModel;
import de.enough.polish.plugin.eclipse.polishEditor.IPolishConstants;
import de.enough.polish.plugin.eclipse.polishEditor.utils.PolishDocumentUtils;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Jul 27, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class PolishContentAssist implements IContentAssistProcessor {

    private static ICompletionProposal[] emptyCompletionProposalArray = new ICompletionProposal[0];
    private MeposeModel meposeModel;
    
    /**
     * @param meposeModel
     */
    public PolishContentAssist(MeposeModel meposeModel) {
        this.meposeModel = meposeModel;
    }

    /*
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer, int)
     */
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
                                                            int offset) {
        if(viewer == null) {
            return emptyCompletionProposalArray;
        }
        
        IDocument document = viewer.getDocument();
        
        if( ! PolishDocumentUtils.isPolishLine(document,offset)) {
            return emptyCompletionProposalArray;
        }
        
        List completionProposals = new LinkedList();
        // Search for directives.
        Position startOfDirectiveAsPosition;
        startOfDirectiveAsPosition = PolishDocumentUtils.extractDirectiveAtOffset(document,offset);
        
        // We found a directive.
        if(startOfDirectiveAsPosition != null) {
            
            String startOfDirectiveAsString;
            startOfDirectiveAsString = PolishDocumentUtils.makeStringFromPosition(document,startOfDirectiveAsPosition);
            int numberOfDirectives = IPolishConstants.POLISH_DIRECTIVES.length;
            List names = new ArrayList();
            for(int index = 0; index < numberOfDirectives; index++) {
                if(IPolishConstants.POLISH_DIRECTIVES[index].startsWith(startOfDirectiveAsString)) {
                    StringBuffer replacementTextBuffer = new StringBuffer();
                    replacementTextBuffer.append(IPolishConstants.POLISH_DIRECTIVES[index]);
//                    replacementTextBuffer.append(" ");
                    String replacementTextString = replacementTextBuffer.toString();
                    names.add(replacementTextString);
                }
            }
            Collections.sort(names);
            for (Iterator iterator = names.iterator(); iterator.hasNext(); ) {
                String name = (String) iterator.next();
                completionProposals.add(new CompletionProposal(name,startOfDirectiveAsPosition.getOffset(),startOfDirectiveAsPosition.getLength(),name.length()));
            }
        }
        
        else {
            Position startOfVariableAsPosition;
            startOfVariableAsPosition = PolishDocumentUtils.extractWordAtOffset(document,offset);
            // There is no variable at offset.
            if(startOfVariableAsPosition == null) {
                return emptyCompletionProposalArray;
            }
            
            String startOfVariableAsString = "";
            startOfVariableAsString = PolishDocumentUtils.makeStringFromPosition(document,startOfVariableAsPosition);
            
//          Check if we have a current device. This is a kludge as it tests domain-specific logic
            // (do we have a device?) with testing a specific data structure.
            List names = new ArrayList();
            for (Iterator iterator = this.meposeModel.getVariables().keySet().iterator(); iterator.hasNext(); ) {
                String variableName = (String) iterator.next();
                if(variableName.startsWith(startOfVariableAsString)) {
                    names.add(variableName);
                }
            }
            Collections.sort(names);
            for (Iterator iterator = names.iterator(); iterator.hasNext(); ) {
                String name = (String) iterator.next();
                String value;
                value = this.meposeModel.getEnvironment().getVariable(name);
                if(value == null) {
                    if(this.meposeModel.getEnvironment().hasSymbol(name)) {
                        value = "true";
                    }
                    else {
                        value = "false";
                    }
                }
                CompletionProposal completionProposal = new CompletionProposal(name,
                                                                               startOfVariableAsPosition.getOffset(),
                                                                               startOfVariableAsPosition.getLength(),
                                                                               name.length(),
                                                                               null,
                                                                               name,
                                                                               null,
                                                                               value);
                completionProposals.add(completionProposal);
            }
        }
        return (ICompletionProposal[])completionProposals.toArray(new ICompletionProposal[completionProposals.size()]);
    }

    /*
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer, int)
     */
    public IContextInformation[] computeContextInformation(ITextViewer viewer,
                                                           int offset) {
        // TODO rickyn implement computeContextInformation
        return null;
    }

    /*
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
     */
    public char[] getCompletionProposalAutoActivationCharacters() {
        // TODO rickyn implement getCompletionProposalAutoActivationCharacters
        return null;
    }

    /*
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
     */
    public char[] getContextInformationAutoActivationCharacters() {
        // TODO rickyn implement getContextInformationAutoActivationCharacters
        return null;
    }

    /*
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
     */
    public String getErrorMessage() {
        // TODO rickyn implement getErrorMessage
        return null;
    }

    /*
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
     */
    public IContextInformationValidator getContextInformationValidator() {
        // TODO rickyn implement getContextInformationValidator
        return null;
    }

}
