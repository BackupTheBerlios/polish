/*
 * Created on Mar 1, 2005 at 5:19:12 PM.
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.plugin.eclipse.css.editor.reconcile;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import de.enough.polish.plugin.eclipse.css.model.CssModel;
import de.enough.polish.plugin.eclipse.css.model.IModelListener;


/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Mar 1, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class SimplePresentationReconciler implements IPresentationReconciler /* extends PresentationReconciler*/ {

	private ITextViewer viewer;
	private ModelListener modelListener;
	private CssModel cssModel;
	
	private class ModelListener implements IModelListener{

		/* (non-Javadoc)
		 * @see de.enough.polish.plugin.eclipse.css.model.IModelListener#modelChanged()
		 */
		public void modelChanged() {
			System.out.println("DEBUG:ModelListener.modelChanged():enter.");
			updateViewer();
		}
		
	}
	
	public SimplePresentationReconciler(CssModel cssModel){
		this.modelListener = new ModelListener();
		this.cssModel = cssModel;
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.presentation.IPresentationReconciler#install(org.eclipse.jface.text.ITextViewer)
	 */
	public void install(ITextViewer newViewer) {
		System.out.println("DEBUG:SimplePresentationReconciler.install().enter.");
		this.viewer = newViewer;
		this.cssModel.addModelListener(this.modelListener);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.presentation.IPresentationReconciler#uninstall()
	 */
	public void uninstall() {
		System.out.println("DEBUG:SimplePresentationReconciler.uninstall().enter.");
		//super.uninstall();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.presentation.IPresentationReconciler#getDamager(java.lang.String)
	 */
	public IPresentationDamager getDamager(String contentType) {
		System.out.println("DEBUG:SimplePresentationReconciler.getDamager().enter.");
		
		return null;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.presentation.IPresentationReconciler#getRepairer(java.lang.String)
	 */
	public IPresentationRepairer getRepairer(String contentType) {
		System.out.println("DEBUG:SimplePresentationReconciler.getRepairer().enter.");
		return null;
	}
	
	public void updateViewer(){
		TextPresentation textPresentation = new TextPresentation();
		this.viewer.changeTextPresentation(textPresentation,false);
	}
}
