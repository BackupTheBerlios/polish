/*
 * Created on Feb 28, 2005 at 2:44:50 PM.
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
package de.enough.polish.plugin.eclipse.css;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.*;
import org.osgi.framework.BundleContext;

import de.enough.polish.plugin.eclipse.css.editor.CssEditor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Feb 28, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class CssEditorPlugin extends AbstractUIPlugin {
	private static CssEditorPlugin plugin;
	private ResourceBundle resourceBundle;
	private BundleContext bundleContext;
	
	private CssEditor editor;
	
	
	public CssEditorPlugin() {
		super();
		plugin = this;
		try {
			this.resourceBundle = ResourceBundle.getBundle("CssEditor.CssEditorPluginResources");
		} catch (MissingResourceException x) {
			this.resourceBundle = null;
		}
	}

	/**
	 * This method is called upon plug-in activation.
	 * @param context
	 * @exception Exception
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		this.bundleContext = context;
	}

	/**
	 * This method is called when the plug-in is stopped.
	 * @param context
	 * @exception Exception
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 * @return CssEditorPlugin
	 */
	public static CssEditorPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 * @param key
	 * @return String
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = CssEditorPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle.
	 * @return ResourceBundle
	 */
	public ResourceBundle getResourceBundle() {
		return this.resourceBundle;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
	 */
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		URL iconDirectoryURL = this.bundleContext.getBundle().getEntry("/icons/");
		
		String[] iconNames = {"sample.gif","book.gif"};
		ImageDescriptor imageDescriptor = null;
		for(int i = 0; i < iconNames.length; i++){
			imageDescriptor = null;
			try {
				URL url = new URL(iconDirectoryURL, iconNames[i]);
				imageDescriptor = ImageDescriptor.createFromURL(url);
			} catch (MalformedURLException e) {
				// We miss some icons.
				imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
				System.out.println("ERROR:CssEditorPlugin.initializeImageRegistry():Icon not found.Exception:"+e.getMessage());
			}
			reg.put(iconNames[i],imageDescriptor);
		}
		
	}

	/**
	 * Set the editor instance of the plugin.
	 * @param editor
	 */
	public void setEditor(CssEditor editor) {
		this.editor = editor;
	}
	
	/**
	 * Get the editor instance of the plugin.
	 * @return CssEditor the editor instance.
	 */
	public CssEditor getEditor(){
		return this.editor;
	}
}
