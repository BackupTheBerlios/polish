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
 * Portions created by the Initial Developer are Copyright (C) 1997-1999
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Peter Annema
 *   Norris Boyd
 *   Mike McCabe
 *   Ilya Frank
 *   
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

package org.mozilla.javascript;


import de.enough.polish.ui.Command;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemCommandListener;


/**
 * This class implements the Item native object.
 * NOTE: THIS CLASS WAS MISSING FROM ORIGINAL CONTRIBUTION AND IS NOT TESTED
 * 
 * @author JOM
 */
public class NativeItem extends IdScriptableObject  implements ItemCommandListener{
	
	private static final Object ITEM_TAG = new Object();
	protected Item item;

	static void init(Scriptable scope, boolean sealed) {
		NativeItem obj = new NativeItem();
		obj.item = null;
		obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, sealed);
	}

	public String getClassName() {
		return "ImageItem";
	}

	private NativeImage image = null;

	/* ECMA helper functions */

	protected void fillConstructorProperties(IdFunctionObject ctor) {
		super.fillConstructorProperties(ctor);
	}

    /* *******************************************************
     * 			Properties
     * ******************************************************* */
	
	
	
	private static final int 	
		Id_label    = 1,

		MAX_INSTANCE_ID = 1;

	protected int getMaxInstanceId() 
	{
		return MAX_INSTANCE_ID;
	}

	protected int findInstanceIdInfo(String s) 
	{
		int attr = 0;
		int id = 0;
        L0: { id = 0; String X = null; 
	        L: switch (s.length()) {
	           	case 5: X="label"; id = Id_label; attr = PERMANENT | DONTENUM; break L; 			
	        }
	        if (X!=null && X!=s && !X.equals(s))   
	        	return 0;
	        break L0;
		}
		return instanceIdInfo(attr, id);

	}

	protected String getInstanceIdName(int id) 
	{
		switch (id) {
		case Id_label:	return "label";
		}
		return super.getInstanceIdName(id);
	}

	protected Object getInstanceIdValue(int id) 
	{
//		switch (id) {
//		case Id_image:	return image;
//		}
		return super.getInstanceIdValue(id);
	}

	protected void setInstanceIdValue(int id, Object value) 
	{
//		if (id == Id_image) {
//			image = (NativeImage) value;
//			((ImageItem)item).setImage(image.image);
//			return;
//		}
		super.setInstanceIdValue(id, value);
	}

	
    /* *******************************************************
     * 			Methods
     * ******************************************************* */
	
	/**
	 * @param tag
	 * @param id
	 */
	public void initPrototypeId(Object tag, int id)
	{
		initPrototypeId(id);
		
	}

	private static final int 
		Id_addCommand 		 = 1,
		Id_removeCommand 	 = 2,
		Id_setDefaultCommand = 3,
		Id_setItemStateListener =  4,
		//Id_constructor 		 = 5, 
		
		MAX_PROTOTYPE_ID 	 = 4;

	
	protected void initPrototypeId(int id) {
		String s;
		int arity;
		switch (id) {
			case Id_addCommand:	arity = 1;	s = "addCommand";	break;
			case Id_removeCommand: arity = 1; s="removeCommand"; break;
			case Id_setDefaultCommand: arity = 1; s="setDefaultCommand"; break;
			case Id_setItemStateListener: arity = 1; s="setItemStateListener"; break;
			default:
				super.initPrototypeId(id); return;
		}
		initPrototypeMethod(ITEM_TAG, id, s, arity);
	}

	protected int findPrototypeId(String s) 
	{
		int id;
		L0: {
			id = 0;
			String X = null;
			L: switch (s.length()) {
				//case 11:	X = "constructor";	id = Id_constructor;	break L;
				//TODO resolve ids
			}
			if (X != null && X != s && !X.equals(s))
				id = 0;
			break L0;
		}
		if (id == 0) {
			id = super.findPrototypeId(s);
		}

		return id;
	}
	
	public Object execIdCall(int id, Context cx, Scriptable scope,
			Scriptable thisObj, Object[] args) 
	{
		
		//TODO implement me
		return null;
	}

	public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope,
			Scriptable thisObj, Object[] args) 
	{
		if (!f.hasTag(ITEM_TAG)) {
			return super.execIdCall(f, cx, scope, thisObj, args);
		}
		int id = f.getMethodId();
		switch (id) {
		//TODO realize methods
		case Id_addCommand: {
			return null;
		}
		}
		// The rest of Form.prototype methods require thisObj to be Form

		if (!(thisObj instanceof NativeItem))
			throw incompatibleCallError(f);

		return null; //execIdCall(id, cx, scope, thisObj, args);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemCommandListener#commandAction(javax.microedition.lcdui.Command, de.enough.polish.ui.Item)
	 */
	public void commandAction(Command c, Item item)
	{
		// TODO robertvirkus implement commandAction
		
	}



	
}
