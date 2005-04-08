/*
 * Created on Apr 1, 2005 at 6:13:33 PM.
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
package de.enough.polish.plugin.eclipse.polishEditor;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Apr 1, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class PolishDocumentSetupParticipant implements IDocumentSetupParticipant {

    // TODO: This will not work because // will match before //# is checked. What a pitty
    // but this extension concept is useless if you do nat have more control, e.g. order of calling.
    
    private IToken successToken = new Token(IPolishContentTypes.POLISH_PARTITIONING);
    
    /* (non-Javadoc)
     * @see org.eclipse.core.filebuffers.IDocumentSetupParticipant#setup(org.eclipse.jface.text.IDocument)
     */
    public void setup(IDocument document) {
        
        System.out.println("PolishDocumentSetupParticipant.setup():enter.");
        //TODO: The partitionScanner is different to the normal ruleBasedScanner.
        // Figure out what is wrong. We have a memory error. Again. Where does the EOF come frome?
        RuleBasedPartitionScanner scanner = new RuleBasedPartitionScanner();
        SingleLineRule rule = new SingleLineRule("//#","",this.successToken);
        scanner.setPredicateRules(new IPredicateRule[]{rule});
  /*
   	    DefaultPartitioner polishPartitioner = new DefaultPartitioner(scanner,new String[] {IPolishConstants.DIRECTIVE_PARTITION});
   	    IDocumentExtension3 documentExtension= (IDocumentExtension3) document;
   	    documentExtension.setDocumentPartitioner(IPolishContentTypes.POLISH_PARTITIONING,polishPartitioner);
        polishPartitioner.connect(document);
        */
    }

}
