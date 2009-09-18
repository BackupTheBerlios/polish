/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Rhino code, released
 * May 6, 1999.
 *
 * The Initial Developer of the Original Code is
 * Netscape Communications Corporation.
 * Portions created by the Initial Developer are Copyright (C) 1997-2000
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU General Public License Version 2 or later (the "GPL"), in which
 * case the provisions of the GPL are applicable instead of those above. If
 * you wish to allow use of your version of this file only under the terms of
 * the GPL and not to allow others to use your version of this file under the
 * MPL, indicate your decision by deleting the provisions above and replacing
 * them with the notice and other provisions required by the GPL. If you do
 * not delete the provisions above, a recipient may use your version of this
 * file under either the MPL or the GPL.
 *
 * ***** END LICENSE BLOCK ***** */

// API class

package org.mozilla.javascript;

import java.util.Hashtable;

/**
 * 
 * <p>Optimized version of VMBridge that only supports a single VM type (J2ME)</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class VMBridge
{

    static final VMBridge instance = makeInstance();

    private static VMBridge makeInstance()
    {
    	return new VMBridge();
    }



    
    private Hashtable threadsWithContext = new Hashtable();
    private Context context;
    private Scriptable scope;

    /**
     * Return a helper object to optimize {@link Context} access.
     * <p>
     * The runtime will pass the resulting helper object to the subsequent
     * calls to {@link #getContext(Object contextHelper)} and
     * {@link #setContext(Object contextHelper, Context cx)} methods.
     * In this way the implementation can use the helper to cache
     * information about current thread to make {@link Context} access faster.
     */
    protected Object getThreadContextHelper()
    {
        return Thread.currentThread();
    }

    /**
     * Get {@link Context} instance associated with the current thread
     * or null if none.
     *
     * @param contextHelper The result of {@link #getThreadContextHelper()}
     *                      called from the current thread.
     */
    protected Context getContext(Object contextHelper)
    {
        Thread t = (Thread)contextHelper;
        Context cx = (Context)this.threadsWithContext.get(t);
        //in J2ME there may be 2 Threads one System Thread which calls startApp
        // and a UI-Thread which handles the userInput. They share the same mainContext
        if (cx == null) {
        	if (this.context == null) {
        		createContext();
        	}
        	cx = this.context;
        }
        return cx;
    }

    /**
     * Associate {@link Context} instance with the current thread or remove
     * the current association if <tt>cx</tt> is null.
     *
     * @param contextHelper The result of {@link #getThreadContextHelper()}
     *                      called from the current thread.
     */
    protected void setContext(Object contextHelper, Context cx)
    {
        Thread t = (Thread)contextHelper;
        if (cx == null) {
            // Allow to garbage collect thread reference
            this.threadsWithContext.remove(t);
        } else {
            this.threadsWithContext.put(t, cx);
        }
    }
    
    private void createContext() {
    	// rickyn: Calling Context.enter() causes an infinitve loop.
    	this.context = new Context();
    	this.scope = this.context.initStandardObjects();
    }
    
    public Context getContext() {
    	if (this.context == null) {
    		createContext();
    	}
    	return this.context;
    }
    public Scriptable getScope() {
    	if (this.context == null) {
    		createContext();
    	}
    	return this.scope;
    }

}
