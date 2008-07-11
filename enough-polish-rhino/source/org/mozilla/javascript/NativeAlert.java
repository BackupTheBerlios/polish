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
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.Alert;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;



/**
 * This class implements the Date native object.
 * See ECMA 15.9.
 * @author Mike McCabe
 */
final public class NativeAlert extends IdScriptableObject implements CommandListener
{
    //static final long serialVersionUID = -8307438915861678966L;

    private static final Object ALERT_TAG = new Object();

    private Alert alert;
    private Hashtable commands = new Hashtable();
    private Displayable previousScreen;
    
    private static final int ALARM = 1;
    private static final int INFO = 2;
    private static final int WARNING = 3;
    private static final int ERROR = 4;
    private static final int CONFIRMATION = 5;

    static void init(Scriptable scope, boolean sealed)
    {
        NativeAlert obj = new NativeAlert();
        obj.alert = null;

        obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, sealed);
    }

    public String getClassName()
    {
        return "Alert";
    }

    protected void fillConstructorProperties(IdFunctionObject ctor)
    {
        final int attr = ScriptableObject.DONTENUM |
                         ScriptableObject.PERMANENT |
                         ScriptableObject.READONLY;

        ctor.defineProperty("ALARM", ScriptRuntime.wrapNumber(ALARM), attr);
        ctor.defineProperty("INFO", ScriptRuntime.wrapNumber(INFO), attr);
        ctor.defineProperty("WARNING", ScriptRuntime.wrapNumber(WARNING), attr);
        ctor.defineProperty("ERROR", ScriptRuntime.wrapNumber(ERROR), attr);
        ctor.defineProperty("CONFIRMATION", ScriptRuntime.wrapNumber(CONFIRMATION), attr);

        super.fillConstructorProperties(ctor);
    }

    /* *******************************************************
     * 			Properties
     * ******************************************************* */
    private static final int
    Id_timeout	= 1,
    Id_height	= 2,
    Id_width	= 3,
    Id_title	= 4,
    Id_text		= 5,


    MAX_INSTANCE_ID  =  5;
   
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
	           case 4: 
	        	   X="text"; id=Id_text; attr = PERMANENT | DONTENUM | READONLY; break L;
	           case 5: 
	        	   switch(s.charAt(0)) {
	        	   case 't': X="title"; id=Id_title; attr = PERMANENT | DONTENUM; break L;
	        	   case 'w': X="width"; id=Id_width; attr = PERMANENT | DONTENUM | READONLY; break L;	        	   }
	        	   break L;
	           case 6: X="height"; id=Id_height; attr = PERMANENT | DONTENUM | READONLY; break L;
	           case 7: X="timeout"; id=Id_timeout; attr = PERMANENT | DONTENUM | READONLY; break L;
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
	    case Id_title:  	return "title";
	    case Id_height: 	return "height";
	    case Id_width:  	return "width";
	    case Id_timeout:  	return "timeout";
	    case Id_text:  	 	return "text";
	    }
	    return super.getInstanceIdName(id);
	}
	
	protected Object getInstanceIdValue(int id)
	{
	    switch (id) {
	    case Id_title:
	    	return ScriptRuntime.toString(this.alert.getTitle());
	    case Id_width:
	    	return ScriptRuntime.wrapNumber(this.alert.getWidth());
	    case Id_height:
	    	return ScriptRuntime.wrapNumber(this.alert.getHeight());
	    case Id_timeout:
	    	return ScriptRuntime.wrapNumber(this.alert.getTimeout());
	    case Id_text:
	    	return ScriptRuntime.toString(this.alert.getString());
	    }
	    return super.getInstanceIdValue(id);
	}
	
	protected void setInstanceIdValue(int id, Object value)
	{
		switch (id) {
		case Id_title:
			this.alert.setTitle(ScriptRuntime.toString(value)); return; 
		case Id_timeout:
			this.alert.setTimeout(ScriptRuntime.toInt32(value)); return; 
		case Id_text:
			this.alert.setString(ScriptRuntime.toString(value)); return; 
		case Id_height:
		case Id_width:
			return; // READONLY
		}
	    super.setInstanceIdValue(id, value);
	}  
	
	
    private static final int
    Id_constructor          =  1,
    Id_addCommand           =  2,
    Id_isShown		        =  3,
    Id_removeCommand        =  4,
    Id_show        			=  5,

    MAX_PROTOTYPE_ID        =  5;


	protected void initPrototypeId(int id)
    {
        String s;
        int arity;
        switch (id) {
		case Id_constructor:	arity = 2;	s = "constructor";		break;
		case Id_isShown:		arity = 0;	s = "isShown";			break;
		case Id_addCommand:		arity = 4;	s = "addCommand";		break;
		case Id_removeCommand:	arity = 1;	s = "removeCommand";	break;
		case Id_show:			arity = 0;	s = "show";				break;
		
		default:
			throw new IllegalArgumentException(String.valueOf(id));
		}
		initPrototypeMethod(ALERT_TAG, id, s, arity);
    }

	
    protected int findPrototypeId(String s)
    {
        int id;
        L0: { id = 0; String X = null; 
        L: switch (s.length()) {
            case 4: 
            	X="show";id=Id_show; break L;
        	case 7: 
        		X="isShown";id=Id_isShown; break L;
            case 10: 
        		X="addCommand";id=Id_addCommand; break L;
            case 11: 
            	X="constructor";id=Id_constructor; break L;
            case 13: 
            	X="removeCommand";id=Id_removeCommand; break L;

            }
            if (X!=null && X!=s && !X.equals(s)) id = 0;
            break L0;
        }
        return id;
    }


    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope,
                             Scriptable thisObj, Object[] args)
    {
        if (!f.hasTag(ALERT_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.getMethodId();
        switch (id) {
          case Id_constructor:
                return jsConstructor(args);
        }

        // The rest of Form.prototype methods require thisObj to be Form
        if (!(thisObj instanceof NativeAlert))
            throw incompatibleCallError(f);
        NativeAlert realThis = (NativeAlert)thisObj;

        switch (id) {
          case Id_show:
        	realThis.previousScreen = StyleSheet.display.getCurrent();
        	StyleSheet.setCurrent(StyleSheet.display, realThis.alert);

        	return realThis;
          case Id_isShown:
        	  return ScriptRuntime.wrapBoolean(realThis.alert.isShown());
	      case Id_addCommand:
	    	  return addCommand(realThis, args);
	      case Id_removeCommand:
	    	  return removeCommand(realThis, args);
	    	  
	      default: throw new IllegalArgumentException(String.valueOf(id));
        }

    }

    private static Object jsConstructor(Object[] args)
    {
        NativeAlert obj = new NativeAlert();
        String style = null;
        Style s = null;
        
        if (args.length < 3) {
        	ScriptRuntime.padArguments(args, 3);
        }
        AlertType at = AlertType.INFO;
        Image image = null;
        try {
	        switch(ScriptRuntime.toInt32(args[2])) {
	        case ALARM:
	        	at = AlertType.ALARM;
	        	image = Image.createImage("/Alert_Alarm.png");
	        	break;
	        case CONFIRMATION:
	        	at = AlertType.CONFIRMATION;
	        	image = Image.createImage("/Alert_Confirm.png");
	        	break;
	        case ERROR:
	        	at = AlertType.ERROR;
	        	image = Image.createImage("/Alert_Error.png");
	        	break;
	        case WARNING:
	        	at = AlertType.WARNING;
	        	image = Image.createImage("/Alert_Warning.png");
	        	break;
	        case INFO:
	       	default: 
	        	image = Image.createImage("/Alert_Info.png");
	        	at = AlertType.INFO;
	           	break;
	        }
        } catch (IOException ignored) {
        	
        }
        if (args.length > 3) {
            style = ScriptRuntime.toString(args[3]);
            if (style != null && !style.trim().equals("")) {
                s = StyleSheet.getStyle(style);
            }
        }
        
        if (s != null) {
            obj.alert = new Alert(ScriptRuntime.toString(args[0]), ScriptRuntime.toString(args[1]), image, at, s);
        } else {
            obj.alert = new Alert(ScriptRuntime.toString(args[0]), ScriptRuntime.toString(args[1]), image, at);
        }
        obj.alert.setCommandListener(obj);
        
        return obj;
    }

    private Command createCommand(NativeAlert obj, Object[] args) {
    	
    	if (args.length < 4) {
            args = ScriptRuntime.padArguments(args, 4);
    	}
    	int type = ScriptRuntime.toInt32(args[1]);
    	int prio = ScriptRuntime.toInt32(args[2]);
    	Command c = new Command(ScriptRuntime.toString(args[0]), type, prio);
    	
    	if (args[3] instanceof InterpretedFunction) {
    		obj.commands.put(c, args[3]);
    	}
    	return c;
    }
    
    private Object addCommand(NativeAlert obj, Object[] args) {
    	obj.alert.addCommand(createCommand(obj, args));
    	return obj;
    }

    private Object removeCommand(NativeAlert obj, Object[] args) {
        if (args.length == 0)
            args = ScriptRuntime.padArguments(args, 1);
        String sCommand = ScriptRuntime.toString(args[0]);
		Enumeration e = obj.commands.keys();
		while (e.hasMoreElements()) {
			Command c = (Command) e.nextElement();
			if (sCommand.equals(c.getLabel())) {
				obj.alert.removeCommand(c);
				obj.commands.remove(c);
			}
		}
  	  	return obj;
    }

    public void commandAction(Command command, Displayable display) {
		InterpretedFunction function = (InterpretedFunction) this.commands.get(command);
		if (function != null) {
			function.call(VMBridge.instance.getContext(), VMBridge.instance.getScope(), VMBridge.instance.getScope(), ScriptRuntime.emptyArgs);
		} else {
			StyleSheet.setCurrent( StyleSheet.display, this.previousScreen);
		}
	}
}


