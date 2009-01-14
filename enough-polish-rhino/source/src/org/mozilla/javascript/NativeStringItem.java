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

import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;



/**
 * This class implements the StringItem native object.
 * 
 * @author Jörg Miesner
 */
final public class NativeStringItem extends NativeItem implements
		ItemCommandListener {
	private static final Object STRINGITEM_TAG = new Object();

	static void init(Scriptable scope, boolean sealed) {
		NativeStringItem obj = new NativeStringItem();
		// Set the value of the prototype Date to NaN ('invalid date');

		obj.item = null;
		obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, sealed);
	}

	public String getClassName() {
		return "StringItem";
	}

	protected void initPrototypeId(int id) {
		String s;
		int arity;
		switch (id) {
		case Id_constructor:
			arity = 3;
			s = "constructor";
			break;
		default:
			initPrototypeId(STRINGITEM_TAG, id);
			return;
		}
		initPrototypeMethod(STRINGITEM_TAG, id, s, arity);
	}

	public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope,
			Scriptable thisObj, Object[] args) {
		if (!f.hasTag(STRINGITEM_TAG)) {
			return super.execIdCall(f, cx, scope, thisObj, args);
		}
		int id = f.getMethodId();
		switch (id) {

		case Id_constructor: {
			return jsConstructor(args);
		}
		}
		// The rest of Form.prototype methods require thisObj to be Form

		if (!(thisObj instanceof NativeStringItem))
			throw incompatibleCallError(f);

		return execIdCall(id, cx, scope, thisObj, args);
	}

	/* the javascript constructor */
	public static Object jsConstructor(Object[] args) {
		NativeStringItem obj = new NativeStringItem();

		String label = "";
		String text = "";
		int type = Item.PLAIN;
		String style = null;
		Style s = null;
		if (args.length > 0) {
			label = ScriptRuntime.toString(args[0]);
		}
		if (args.length > 1) {
			text = ScriptRuntime.toString(args[1]);
		}
		if (args.length > 3) {
			type = ScriptRuntime.toInt32(args[3]);
		}
		if (args.length > 2) {
            style = ScriptRuntime.toString(args[2]);
            if (style != null && !style.trim().equals("")) {
                s = StyleSheet.getStyle(style);
            }
        }
		if (s != null) {
		    obj.item = new StringItem(label, text, type, s);
		} else{
		    obj.item = new StringItem(label, text, type);
		}
		obj.item.setItemCommandListener(obj);

		return obj;
	}

	/* ECMA helper functions */

	/* compute the time in msec (unclipped) from the given args */

	protected void fillConstructorProperties(IdFunctionObject ctor) {
		super.fillConstructorProperties(ctor);
	}

	
	
	//Id_label    = 1,
	private static final int Id_text = 2,

	MAX_INSTANCE_ID = 2;

	protected int getMaxInstanceId() {
		return MAX_INSTANCE_ID;
	}

	protected int findInstanceIdInfo(String s) {
		int id;
		// #generated# Last update: 2007-05-09 08:16:24 EDT
		L0: {
			id = 0;
			int s_length = s.length();
			if (s_length == 4) {
				id = Id_text;
			}
			break L0;
		}
		// #/generated#
		// #/string_id_map#

		if (id == 0)
			return super.findInstanceIdInfo(s);

		int attr;
		switch (id) {
		case Id_text:
			attr = PERMANENT | DONTENUM;
			break;
		default:
			throw new IllegalStateException();
		}
		return instanceIdInfo(attr, id);
	}

	protected String getInstanceIdName(int id) {
		switch (id) {
		case Id_text:
			return "text";
		}
		return super.getInstanceIdName(id);
	}

	protected Object getInstanceIdValue(int id) {
		switch (id) {
		case Id_text:
			return ScriptRuntime.toString(((StringItem)item).getText());
		}
		return super.getInstanceIdValue(id);
	}

	protected void setInstanceIdValue(int id, Object value) {
		if (id == Id_text) {
			((StringItem)item).setText(ScriptRuntime.toString(value));
			return;
		}
		super.setInstanceIdValue(id, value);
	}

	protected int findPrototypeId(String s) {
		int id;
		// #generated# Last update: 2007-05-09 08:15:38 EDT
		L0: {
			id = 0;
			String X = null;
			L: switch (s.length()) {
			case 11:
				X = "constructor";
				id = Id_constructor;
				break L;
			}
			if (X != null && X != s && !X.equals(s))
				id = 0;
			break L0;
		}
		if (id == 0) {
			id = super.findPrototypeId(s);
		}
		// #/generated#
		return id;
	}

	private static final int Id_constructor = 5, MAX_PROTOTYPE_ID = 5;
}
