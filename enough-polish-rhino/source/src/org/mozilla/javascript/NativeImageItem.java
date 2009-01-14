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

import javax.microedition.lcdui.Image;

import de.enough.polish.ui.ImageItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;


/**
 * This class implements the StringItem native object.
 * 
 * @author JOM
 */
final public class NativeImageItem extends NativeItem implements ItemCommandListener {
	
	private static final Object IMAGEITEM_TAG = new Object();

	static void init(Scriptable scope, boolean sealed) {
		NativeImageItem obj = new NativeImageItem();
		// Set the value of the prototype Date to NaN ('invalid date');

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
//		Id_label    = 1 (extends NativeItem)
		Id_image 	= 2,

		MAX_INSTANCE_ID = 2;

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
	           	case 5: X="image"; id = Id_image; attr = PERMANENT | DONTENUM; break L; 			
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
		case Id_image:	return "image";
		}
		return super.getInstanceIdName(id);
	}

	protected Object getInstanceIdValue(int id) 
	{
		switch (id) {
		case Id_image:	return image;
		}
		return super.getInstanceIdValue(id);
	}

	protected void setInstanceIdValue(int id, Object value) 
	{
		if (id == Id_image) {
			image = (NativeImage) value;
			((ImageItem)item).setImage(image.image);
			return;
		}
		super.setInstanceIdValue(id, value);
	}

	
    /* *******************************************************
     * 			Methods
     * ******************************************************* */

	private static final int 
	//	Id_addCommand 		 = 1 (extends NativeItem) 
	//	Id_removeCommand 	 = 2 (extends NativeItem)
	//	Id_setDefaultCommand = 3 (extends NativeItem)
	//  Id_setItemStateListener =  4 (extends NativeItem)
		Id_constructor 		 = 5, 
		
		MAX_PROTOTYPE_ID 	 = 5;

	
	protected void initPrototypeId(int id) {
		String s;
		int arity;
		switch (id) {
			case Id_constructor:	arity = 3;	s = "constructor";	break;
			default:
				super.initPrototypeId(IMAGEITEM_TAG, id);	return;
		}
		initPrototypeMethod(IMAGEITEM_TAG, id, s, arity);
	}

	protected int findPrototypeId(String s) 
	{
		int id;
		L0: {
			id = 0;
			String X = null;
			L: switch (s.length()) {
				case 11:	X = "constructor";	id = Id_constructor;	break L;
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

	public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope,
			Scriptable thisObj, Object[] args) 
	{
		if (!f.hasTag(IMAGEITEM_TAG)) {
			return super.execIdCall(f, cx, scope, thisObj, args);
		}
		int id = f.getMethodId();
		switch (id) {

		case Id_constructor: {
			return jsConstructor(args);
		}
		}
		// The rest of Form.prototype methods require thisObj to be Form

		if (!(thisObj instanceof NativeImageItem))
			throw incompatibleCallError(f);

		return execIdCall(id, cx, scope, thisObj, args);
	}

	/* the javascript constructor */
	public static Object jsConstructor(Object[] args) 
	{
		NativeImageItem obj = new NativeImageItem();

		String label = "";
		int type = Item.PLAIN;
		Image img = null;
		String style = null;
        Style s = null;
    	if (args.length < 2) {
            args = ScriptRuntime.padArguments(args, 2);
    	}

		label = ScriptRuntime.toString(args[0]);
		try {
			img = ((NativeImage) args[1]).image;
		} catch (ClassCastException e) {
			throw ScriptRuntime.typeError0("msg.img.expected");
		}

		if (args.length > 2) {
			type = ScriptRuntime.toInt32(args[2]);
		}
		if (args.length > 3) {
            style = ScriptRuntime.toString(args[3]);
            if (style != null && !style.trim().equals("")) {
                s = StyleSheet.getStyle(style);
                if (s == null) {
                    System.out.println("ImageItem style provided but not found! Please check name and/or css file.");
                }
            }
        }
		
		if (s != null) {
		    obj.item = new ImageItem(label, img, ImageItem.LAYOUT_DEFAULT, null, type, s);
		} else {
		    obj.item = new ImageItem(label, img, ImageItem.LAYOUT_DEFAULT, null, type);
		}
		obj.item.setItemCommandListener(obj);

		return obj;
	}
	
}
