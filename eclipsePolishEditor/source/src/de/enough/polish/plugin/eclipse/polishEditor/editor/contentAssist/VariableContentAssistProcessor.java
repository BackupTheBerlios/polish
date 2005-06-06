/*
 * Created on May 24, 2005 at 6:29:23 PM.
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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.internal.ui.text.template.contentassist.PositionBasedCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import de.enough.polish.Environment;
import de.enough.polish.plugin.eclipse.core.IObjectChangeListener;
import de.enough.polish.plugin.eclipse.core.ObjectChangeEvent;
import de.enough.polish.plugin.eclipse.polishEditor.utils.PolishDocumentUtils;

// TODO:Find a common superclass.
public class VariableContentAssistProcessor implements IContentAssistProcessor, IObjectChangeListener {

    private String errorMessage = null;
    private static ICompletionProposal[] emptyCompletionProposalArray = new ICompletionProposal[0];
    private Environment deviceEnvironment;
    
    
    public VariableContentAssistProcessor(Environment deviceEnvironment) {
        // Having a null environment sould be okay, as it is possible that no build.xml is
        // specified for the editor. No variable completion is possible in this case.
        this.deviceEnvironment = deviceEnvironment;
    }
    
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
                                                            int offset) {
        System.out.println("DEBUG:PolishVariableAssistProcessor.computeCompletionProposals(...):enter.");
        
        if( ! PolishDocumentUtils.isPolishLine(viewer.getDocument(),offset)) {
            return emptyCompletionProposalArray;
        }
        IDocument document = viewer.getDocument();
        
        //int lineInDocument;
        Position startOfVariableAsPosition;
        startOfVariableAsPosition = PolishDocumentUtils.extractVariableAtOffset(document,offset);
        // There is no variable at offset.
        if(startOfVariableAsPosition == null) {
            return emptyCompletionProposalArray;
        }
        
        String startOfVariableAsString = "";
        startOfVariableAsString = PolishDocumentUtils.makeStringFromPosition(document,startOfVariableAsPosition);
        
        List completionProposals = new LinkedList();
        // No environment means we know nothing about possible variables.
        if(this.deviceEnvironment == null) {
            return emptyCompletionProposalArray;
        }
        Map variableToValueMapping = this.deviceEnvironment.getVariables();
        for (Iterator iterator = variableToValueMapping.keySet().iterator(); iterator.hasNext(); ) {
            String variableName = (String) iterator.next();
            if(variableName.startsWith(startOfVariableAsString)) {
                completionProposals.add(new PositionBasedCompletionProposal(variableName,startOfVariableAsPosition,1,null,null,null,(String)variableToValueMapping.get(variableName)));
            }
        }
        //completionProposals.add(new PositionBasedCompletionProposal("Hallo ",startOfVariableAsPosition,4));
        //completionProposals.add(new PositionBasedCompletionProposal("Hallo ",startOfVariableAsPosition,6));
//        for(int index = 0; index < IPolishConstants.POLISH_DIRECTIVES.length; index++) {
//            if(IPolishConstants.POLISH_DIRECTIVES[index].startsWith(startOfDirectiveAsString)) {
//                StringBuffer replacementText = new StringBuffer();
//                replacementText.append(IPolishConstants.POLISH_DIRECTIVES[index]);
//                replacementText.append(" ");
//                completionProposals.add(new PositionBasedCompletionProposal(replacementText.toString(),startOfVariableAsPosition,IPolishConstants.POLISH_DIRECTIVES[index].length()+1));
//            } 
//        }
        return (ICompletionProposal[])completionProposals.toArray(new ICompletionProposal[completionProposals.size()]);
    }


    public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
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
        return null;
    }

    /*
     * @see de.enough.polish.plugin.eclipse.core.IObjectChangeListener#handleObjectChangedEvent(de.enough.polish.plugin.eclipse.core.ObjectChangeEvent)
     */
    public void handleObjectChangedEvent(ObjectChangeEvent objectChangeEvent) {
        if(objectChangeEvent == null){
            throw new IllegalArgumentException("ERROR:VariableContentAssistProcessor.handleObjectChangedEvent(...):Parameter 'objectChangeEvent' is null.");
        }
        
    }
    
}
