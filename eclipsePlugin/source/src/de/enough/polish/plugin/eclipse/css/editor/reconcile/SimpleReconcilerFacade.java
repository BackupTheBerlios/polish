/*
 * Created on Mar 1, 2005 at 5:36:58 PM.
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
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;

/**
 * <p>A facade for the presentationReconciler and the reconciler. This class will install and trigger
 * our own reconcilers to update the model and repair the screen. So we have full control over updates.</p>
 *<p>Another option is to subclass SourceViewer and get rid of the reconciling architecture in the heard of the application.
 *We need to control the sequence of calling of the reconcilers.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Mar 1, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class SimpleReconcilerFacade implements IPresentationReconciler, IReconciler{

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.presentation.IPresentationReconciler#install(org.eclipse.jface.text.ITextViewer)
	 */
	public void install(ITextViewer viewer) {
		// Beware that both interfaces have this method. So it will be called twice.
		System.out.println("SimpleReconcilerFacade.install().enter.");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.presentation.IPresentationReconciler#uninstall()
	 */
	public void uninstall() {
		System.out.println("SimpleReconcilerFacade.uninstall().enter.");
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.presentation.IPresentationReconciler#getDamager(java.lang.String)
	 */
	public IPresentationDamager getDamager(String contentType) {
		System.out.println("SimpleReconcilerFacade.getDamager().enter.");
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.presentation.IPresentationReconciler#getRepairer(java.lang.String)
	 */
	public IPresentationRepairer getRepairer(String contentType) {
		System.out.println("SimpleReconcilerFacade.getRepairer().enter.");
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.reconciler.IReconciler#getReconcilingStrategy(java.lang.String)
	 */
	public IReconcilingStrategy getReconcilingStrategy(String contentType) {
		System.out.println("SimpleReconcilerFacade.getReconcilingStrategy().enter.");
		return null;
	}

}
