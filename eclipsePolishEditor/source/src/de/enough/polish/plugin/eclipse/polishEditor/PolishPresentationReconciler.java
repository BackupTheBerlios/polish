/*
 * Created on Apr 4, 2005 at 6:26:13 PM.
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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.presentation.PresentationReconciler;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Apr 4, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class PolishPresentationReconciler extends PresentationReconciler {

    class PolishTextInputListener implements ITextInputListener{

        /* (non-Javadoc)
         * @see org.eclipse.jface.text.ITextInputListener#inputDocumentAboutToBeChanged(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IDocument)
         */
        public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
            // TODO ricky implement inputDocumentAboutToBeChanged
            
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.text.ITextInputListener#inputDocumentChanged(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IDocument)
         */
        public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
            // TODO ricky implement inputDocumentChanged
            
        }
        
    }
    
    private ITextViewer viewer;
    private PolishTextInputListener polishTextInputListener = new PolishTextInputListener();
    
    public void install(ITextViewer newViewer) {
        super.install(newViewer);
        this.viewer= newViewer;
        this.viewer.addTextInputListener(this.polishTextInputListener);
    }
    
    public void uninstall() {
        super.uninstall();
        this.viewer.removeTextInputListener(this.polishTextInputListener);
    }
    
    
    protected TextPresentation createPresentation(IRegion damage,
            IDocument document) {
        // TODO ricky implement createPresentation
        return super.createPresentation(damage, document);
    }
}
