/*
 * Created on Feb 24, 2005 at 12:22:24 PM.
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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import de.enough.polish.plugin.eclipse.css.CssEditorPlugin;
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
public class DummyLabelProvider extends LabelProvider {

	Map imageCache;
	
	public DummyLabelProvider(){
		this.imageCache = new HashMap(10);
	}
	
	public String getText(Object element){
		System.out.println("DummyLabelProvider.getText():entered.");
		System.out.println("DummyLabelProvider.getText():element:"+element.getClass());
		
		if(element instanceof DummyParent){
			return ((DummyParent)element).getName();
		}
		if(element instanceof DummyChild){
			return ((DummyChild)element).getName() + ": "+((DummyChild)element).getValue();
		}
		return "NoName";
	}
	
	public Image getImage(Object element){
		Image image = null;
		if (element instanceof DummyParent) {
			image = CssEditorPlugin.getDefault().getImageRegistry().get("book.gif");
		} else if (element instanceof DummyElement) {
			image = CssEditorPlugin.getDefault().getImageRegistry().get("sample.gif");
		} else {
			System.out.println("DummyLabelprovider.getImage():element "+element+"is unknown.");
		}
		return image;
	}
}
