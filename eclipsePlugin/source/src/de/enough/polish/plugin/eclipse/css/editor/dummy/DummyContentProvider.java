/*
 * Created on Feb 24, 2005 at 11:01:17 AM.
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
package de.enough.polish.plugin.eclipse.css.editor.dummy;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;

import de.enough.polish.plugin.eclipse.css.model.dummy.DummyChild;
import de.enough.polish.plugin.eclipse.css.model.dummy.DummyElement;
import de.enough.polish.plugin.eclipse.css.model.dummy.DummyParent;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Feb 24, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class DummyContentProvider implements ITreeContentProvider,ISelectionChangedListener {

	DummyElement rootParent;
	Viewer currentViewer;
	
	public DummyContentProvider(){
		this.rootParent = null;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// FIXME: Danger. Please implement disposable on the model for proper finalization.
		this.rootParent = null;

	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		System.out.println("DummyContentProvider.inputChanged(): enter");
		if(viewer != null){
			this.currentViewer = viewer;
			System.out.println("DummyContentProvider.inputChanged():oldInput:"+oldInput);
			System.out.println("DummyContentProvider.inputChanged():newInput:"+ ((newInput != null) ? newInput.getClass().toString() : "null"));
			this.rootParent = (DummyElement)newInput;
			this.currentViewer.addSelectionChangedListener(this);
		}
	}
	
	public DummyElement initialInput(){
		DummyParent root = new DummyParent("root",null);
		
		DummyParent parent1 = new DummyParent("parent1",root);
		
		DummyChild child1 = new DummyChild("Child1",1,root);
		DummyChild child2 = new DummyChild("Child2",2,parent1);
		DummyChild child3 = new DummyChild("Child3",3,parent1);
		DummyChild child4 = new DummyChild("Child4",4,root);
		
		parent1.getChildren().add(child2);
		parent1.getChildren().add(child3);
		
		root.getChildren().add(child1);
		root.getChildren().add(parent1);
		root.getChildren().add(child4);
		
		return root;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		System.out.println("DummyContentProvider.getChildren():entered");
		System.out.println("DummyContentProvider.getChildren():parentElement:"+parentElement);
		Object[] result = null;
		if(parentElement instanceof DummyParent){
			result = ((DummyParent)parentElement).getChildren().toArray();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		DummyElement result = null;
		if(element instanceof DummyElement){
			result = ((DummyElement)element).getParent();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		if(element instanceof DummyParent){
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		System.out.println("DummyContentProvider.getelements():entered");
		System.out.println("DummyContentProvider.getChildren():inputElement"+inputElement);
		
		/*Object[] result = null;
		if(inputElement instanceof DummyParent){
			result = new Object[]{((DummyParent)inputElement).getName()};
		}
		if(inputElement instanceof DummyChild){
			result = new Object[]{((DummyChild)inputElement).getName()};
		}
		
		return result;
		*/
		return getChildren(inputElement);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		System.out.println("DummyContentProvider.selectionChanged(): enter");
		System.out.println("DummyContentProvider.selectionChanged():event"+event);
		
	}

}
