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

import java.io.IOException;

import javax.microedition.lcdui.Image;

/**
 * This class implements the Date native object.
 * See ECMA 15.9.
 * @author JOM
 */
final public class NativeImage extends IdScriptableObject
{
    //static final long serialVersionUID = -8307438915861678966L;

    private static final Object IMAGE_TAG = new Object();

    Image image;

	static void init(Scriptable scope, boolean sealed)
    {
        NativeImage obj = new NativeImage();
        // Set the value of the prototype Date to NaN ('invalid date');
        obj.image = null;

        obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, sealed);
    }

    public String getClassName()
    {
        return "Image";
    }

    private static final int
    Id_constructor          =  1,
    Id_Height				=  2,
    Id_Width				=  3,

    MAX_PROTOTYPE_ID        =  3;

    protected void initPrototypeId(int id)
    {
        String s;
        int arity;
        switch (id) {
		case Id_constructor: 	arity = 1; 	s = "constructor"; 	break;
		case Id_Height:   		arity = 1; 	s = "getHeight"; 	break;
		case Id_Width:			arity = 1; 	s = "getWidth";		break;
		default:
			throw new IllegalArgumentException(String.valueOf(id));
		}
		initPrototypeMethod(IMAGE_TAG, id, s, arity);
    }

    protected int findPrototypeId(String s)
    {
        int id;
        L0: { id = 0; String X = null; 
        L: switch (s.length()) {
        	case 8:  X="getWidth";id=Id_Width; break L;
            case 9:  X="getHeight";id=Id_Height; break L;
            case 11: X="constructor";id=Id_constructor; break L;
           }
           if (X!=null && X!=s && !X.equals(s)) id = 0;
           break L0;
        }
        return id;
    }

    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope,
                             Scriptable thisObj, Object[] args)
    {
        if (!f.hasTag(IMAGE_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.getMethodId();
        switch (id) {

          case Id_constructor:
            {
                return jsConstructor(args);
            }
        }

        // The rest of Form.prototype methods require thisObj to be Form

        if (!(thisObj instanceof NativeImage))
            throw incompatibleCallError(f);
		NativeImage realThis = (NativeImage) thisObj;

		switch (id) {

		case Id_Height:
			return ScriptRuntime.wrapNumber(realThis.image.getHeight());
		case Id_Width:
			return ScriptRuntime.wrapNumber(realThis.image.getWidth());
	      default: throw new IllegalArgumentException(String.valueOf(id));
		}
    }

	/* the javascript constructor */
    private static Object jsConstructor(Object[] args)
    {
        NativeImage obj = new NativeImage();
        
        String url = "";
        try {
	        // if called with just one arg -
	        if (args.length > 0) {
	        	if (args[0] instanceof String) {
	        		url = ScriptRuntime.toString(args[0]);
	    			obj.image = Image.createImage(url);
	        	} else if (args[0] instanceof byte[]) {
	        		byte[] data = (byte[]) args[0];
	        		if (data.length > 0) {
	        			obj.image = Image.createImage(data, 0, data.length);
	        		}
	        	} else {
			        String msg = ScriptRuntime.getMessage1("msg.img.notavailable", url);
			        throw ScriptRuntime.constructError("Error", msg);
	        	}
	        }
		} catch (IOException e) {
	        String msg = ScriptRuntime.getMessage1("msg.img.notavailable", url);
	        throw ScriptRuntime.constructError("Error", msg);
		}

        return obj;
    }

	public Image getImage() {
		return image;
	}
}


