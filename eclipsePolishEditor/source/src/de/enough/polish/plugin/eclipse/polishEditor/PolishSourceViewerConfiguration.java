/*
 * Created on Mar 23, 2005 at 1:13:27 PM.
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

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.ITextEditor;


/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Mar 23, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class PolishSourceViewerConfiguration extends JavaSourceViewerConfiguration {

    //private PolishTextTools polishTextTools;
    private PolishSingleLineCommentScanner polishSingleLineCommentScanner;
    
	public PolishSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore, ITextEditor editor, String partitioning) {
		super(colorManager,preferenceStore,editor,partitioning);
		//this.polishTextTools = new PolishTextTools(preferenceStore);
		this.polishSingleLineCommentScanner = new PolishSingleLineCommentScanner(colorManager,preferenceStore);
	}

	
	
	
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        System.out.println("PolishSourceViewerConfiguration.getPresentationReconciler():enter.");
        return super.getPresentationReconciler(sourceViewer);
    }
    protected RuleBasedScanner getSinglelineCommentScanner() {
        System.out.println("PolishSourceViewerConfiguration.getSinglelineCommentScanner():enter.");
        //return this.polishTextTools.getSinglelineCommentScanner();
        return this.polishSingleLineCommentScanner;
    }
    
	/*// DO NOT USE: the super constructor will use this method which return an not initialzed value (null).
	protected IColorManager getColorManager() {
	    return this.colorManager;
	}
	*/

}
