/*
 * Created on Mar 29, 2005 at 12:08:59 PM.
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

import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Mar 29, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 * @deprecated Do not use.
 */
public class PolishTextTools extends JavaTextTools {

    //private PolishSingleLineCommentScanner polishSingleLineCommentScanner;
    
    
    public PolishTextTools(IPreferenceStore preferenceStore) {
        super(preferenceStore);
        //this.polishSingleLineCommentScanner = new PolishSingleLineCommentScanner(getColorManager(),getPreferenceStore());
    }
    /*
	public IPreferenceStore getPreferenceStore() {
		return this.preferenceStore;
	}
*/
    /*
    public RuleBasedScanner getSinglelineCommentScanner() {
        return this.polishSingleLineCommentScanner;
    }
    
    public void dispose() {
        this.polishSingleLineCommentScanner = null;
        super.dispose();
    }
    */
    /*
    // Taken from org.eclipse.ajdt.internal.ui.editor.AspectJTextTools
	// (Luzius) Override to solve 67281
	public void setupJavaDocumentPartitioner(IDocument document, String partitioning) {
		IDocumentPartitioner partitioner= createDocumentPartitioner();
		
		//only change in function:
		//connect document before setting up partitioner
		//(otherwise we get a NPE when the partitioner tries to access
		//its document)
		partitioner.connect(document);
		
		if (document instanceof IDocumentExtension3) {
			IDocumentExtension3 extension3= (IDocumentExtension3) document;
			extension3.setDocumentPartitioner(partitioning, partitioner);
		} else {
			document.setDocumentPartitioner(partitioner);
		}
		//partitioner.connect(document);
	}
*/

}
