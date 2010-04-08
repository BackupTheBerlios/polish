package de.enough.skylight.dom.impl;

import org.mozilla.javascript.Scriptable;

/**
 * Objects implementing this interface are able to return a Scriptable object which can be used to provide host objects
 * in a Rhino JavaScript instance.
 * @author rickyn
 *
 */
public interface ScriptableAdapter {

	/**
	 * 
	 * @return The scriptable which corresponds to this object
	 */
	public Scriptable getScriptable();

	/**
	 * 
	 * @return true if the scriptable object which corresponds to this object was already initialized
	 */
	public boolean hasScriptable();
	
}
