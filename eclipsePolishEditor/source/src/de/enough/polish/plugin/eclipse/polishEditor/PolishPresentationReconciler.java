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

import org.eclipse.jdt.internal.ui.text.JavaPresentationReconciler;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextInputListener;

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
public class PolishPresentationReconciler extends JavaPresentationReconciler {

    class PolishTextInputListener implements ITextInputListener{

        /* (non-Javadoc)
         * @see org.eclipse.jface.text.ITextInputListener#inputDocumentAboutToBeChanged(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IDocument)
         */
        public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
            /*
             * 
             */
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.text.ITextInputListener#inputDocumentChanged(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IDocument)
         */
        public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
            /*
             * 
             */
        }
        
    }
    
    //private ITextViewer viewer;
    //private PolishTextInputListener polishTextInputListener = new PolishTextInputListener();
    
    /*
    public void install(ITextViewer newViewer) {
        super.install(newViewer);
        this.viewer= newViewer;
        this.viewer.addTextInputListener(this.polishTextInputListener);
    }
    
    public void uninstall() {
        super.uninstall();
        this.viewer.removeTextInputListener(this.polishTextInputListener);
    }
    */
    
//    protected TextPresentation createPresentation(IRegion damage, IDocument document) {
//        try {
//			if (fRepairers == null || fRepairers.isEmpty()) {
//				TextPresentation presentation= new TextPresentation(damage, 1);
//				presentation.setDefaultStyleRange(new StyleRange(damage.getOffset(), damage.getLength(), null, null));
//				return presentation;
//			}
//			
//			TextPresentation presentation= new TextPresentation(damage, 1000);
//			
//			ITypedRegion[] partitioning= TextUtilities.computePartitioning(document, getDocumentPartitioning(), damage.getOffset(), damage.getLength(), false);
//			for (int i= 0; i < partitioning.length; i++) {
//				ITypedRegion r= partitioning[i];
//				IPresentationRepairer repairer= getRepairer(r.getType());
//				if (repairer != null)
//					repairer.createPresentation(presentation, r);
//			}
//			
//			return presentation;
//			
//		} catch (BadLocationException x) {
//		}
//		
//		return null;
//        
//        
//        
//        
//        
//        
//        TextPresentation textPresentation = super.createPresentation(damage, document);
//        
//        ITypedRegion[] partitioning;
//        try {
//            partitioning = TextUtilities.computePartitioning(document, IPolishContentTypes.POLISH_PARTITIONING, damage.getOffset(), damage.getLength(), false);
//        } catch (BadLocationException exception) {
//            System.out.println("PolishPresentationReconciler.createPresentation():badLocaltionException:"+exception);
//            return null;
//        }
//        for (int i= 0; i < partitioning.length; i++) {
//			ITypedRegion r= partitioning[i];
//			IPresentationRepairer repairer= getRepairer(r.getType());
//			if (repairer != null)
//				repairer.createPresentation(textPresentation, r);
//		}
//        return textPresentation;
//    }
   
   
}
