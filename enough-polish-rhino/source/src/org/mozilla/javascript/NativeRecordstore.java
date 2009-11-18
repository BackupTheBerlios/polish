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

import java.util.Hashtable;

import javax.microedition.rms.RecordListener;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.mozilla.javascript.util.Util;




/**
 * This class implements the Date native object.
 * See ECMA 15.9.
 * @author Mike McCabe
 */
final public class NativeRecordstore extends IdScriptableObject
{
    public class JSRecordListener implements RecordListener {

    	private InterpretedFunction function;
		public JSRecordListener(InterpretedFunction function) {
			this.function = function;
		}

		public void recordAdded(RecordStore recordStore, int recordId) {
			callFunction(ADDED, recordId, recordStore);
		}

		public void recordChanged(RecordStore recordStore, int recordId) {
			callFunction(CHANGED, recordId, recordStore);
		}

		public void recordDeleted(RecordStore recordStore, int recordId) {
			callFunction(DELETED, recordId, recordStore);
		}
		
		private void callFunction(int type, int id, RecordStore rs) {
			Object[] args = new Object[3];

			Object[] argsRs = new Object[1];
			argsRs[0] = rs;
	        
	        Scriptable top = Js.getTopLevelScope(VMBridge.instance.getScope());
			args[0] = ScriptRuntime.newObject(VMBridge.instance.getContext(), top, "RecordStore", argsRs);
			args[1] = ScriptRuntime.wrapNumber(type);
			args[2] = ScriptRuntime.wrapNumber(id);
			
			function.call(VMBridge.instance.getContext(), VMBridge.instance.getScope(), VMBridge.instance.getScope(), args );
		}


	}

	//static final long serialVersionUID = -8307438915861678966L;

    private static final Object RECORDSTORE_TAG = new Object();
    private static final int ADDED = 1;
    private static final int CHANGED = 2;
    private static final int DELETED = 3;

    private RecordStore recordStore;
    private Hashtable recordStoreListeners;

    public NativeRecordstore(RecordStore rs) {
		this.recordStore = rs; 
	}

	public NativeRecordstore() {
	}

	static void init(Scriptable scope, boolean sealed)
    {
        NativeRecordstore obj = new NativeRecordstore();
        // Set the value of the prototype Date to NaN ('invalid date');
        obj.recordStore = null;

        obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, sealed);
    }

    public String getClassName()
    {
        return "RecordStore";
    }

    protected void fillConstructorProperties(IdFunctionObject ctor)
    {
        final int attr = ScriptableObject.DONTENUM |
                         ScriptableObject.PERMANENT |
                         ScriptableObject.READONLY;

        ctor.defineProperty("CHANGED", ScriptRuntime.wrapNumber(CHANGED), attr);
        ctor.defineProperty("ADDED", ScriptRuntime.wrapNumber(ADDED), attr);
        ctor.defineProperty("DELETED", ScriptRuntime.wrapNumber(DELETED), attr);

        addIdFunctionProperty(ctor, RECORDSTORE_TAG, Id_deleteRecordStore, "deleteRecordStore", 1);
        super.fillConstructorProperties(ctor);
    }

    /* *******************************************************
     * 			Properties
     * ******************************************************* */
    private static final int
    Id_name	= 1,
    Id_numRecords		= 2,
    Id_nextRecordId	= 3,
    Id_size	= 4,
    Id_sizeAvailable	= 5,
    Id_version	= 6,


    MAX_INSTANCE_ID  =  6;
   
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
	           case 4: switch(s.charAt(0)) { 
		           case 'n' :
		        	   X="name"; id=Id_name; attr = PERMANENT | DONTENUM | READONLY; break L;
		           case 's' :
		        	   X="size"; id=Id_size; attr = PERMANENT | DONTENUM | READONLY; break L;
		           }
	           case 7: X="version"; id=Id_version; attr = PERMANENT | DONTENUM | READONLY; break L;
	           case 10: X="numRecords"; id=Id_numRecords; attr = PERMANENT | DONTENUM | READONLY; break L;
	           case 12: X="nextRecordId"; id=Id_nextRecordId; attr = PERMANENT | DONTENUM | READONLY; break L;
	           case 13: X="sizeAvailable"; id=Id_sizeAvailable; attr = PERMANENT | DONTENUM | READONLY; break L;
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
	    case Id_name:  	return "name";
	    case Id_numRecords:  	return "numRecords";
	    case Id_nextRecordId:  	return "nextRecordId";
	    case Id_size:  	return "size";
	    case Id_sizeAvailable:  	return "sizeAvailable";
	    case Id_version:  	return "version";
	    }
	    return super.getInstanceIdName(id);
	}
	
	protected Object getInstanceIdValue(int id)
	{
	    switch (id) {
	    case Id_name:
	    	try {
					return ScriptRuntime.toString(this.recordStore.getName());
				} catch (RecordStoreNotOpenException e) {
			       throw createErrorRecordStoreNotOpen();
				}
	    case Id_numRecords:
	    	try {
					return ScriptRuntime.wrapNumber(this.recordStore.getNumRecords());
				} catch (RecordStoreNotOpenException e) {
			        throw createErrorRecordStoreNotOpen();
				}
	    case Id_nextRecordId:
	    	try {
					return ScriptRuntime.wrapNumber(this.recordStore.getNextRecordID());
				} catch (RecordStoreNotOpenException e) {
					throw createErrorRecordStoreNotOpen();
				} catch (RecordStoreException e) {
			        throw createRecordStoreError(e.getMessage());
				}
	    case Id_size:
	    	try {
					return ScriptRuntime.wrapNumber(this.recordStore.getSize());
				} catch (RecordStoreNotOpenException e) {
					throw createErrorRecordStoreNotOpen();
				}
	    case Id_sizeAvailable:
	    	try {
					return ScriptRuntime.wrapNumber(this.recordStore.getSizeAvailable());
				} catch (RecordStoreNotOpenException e) {
					throw createErrorRecordStoreNotOpen();
				}
	    case Id_version:
	    	try {
					return ScriptRuntime.wrapNumber(this.recordStore.getVersion());
				} catch (RecordStoreNotOpenException e) {
					throw createErrorRecordStoreNotOpen();
				}
	    }
	    return super.getInstanceIdValue(id);
	}

	/**
	 * @throws EcmaError
	 */
	public static EcmaError createErrorRecordStoreNotOpen() {
		String msg = ScriptRuntime.getMessage0("msg.rs.notopen");
		return ScriptRuntime.constructError("Error", msg);
	}
	
	
    private static final int
    Id_deleteRecordStore	=  -1,
    Id_addRecord		    =  1,
    Id_addRecordListener    =  2,
    Id_close   		    	=  3,
    Id_deleteRecord			=  4,
    Id_getRecord		    =  5,
    Id_getRecordSize        =  6,
    Id_removeRecordListener =  7,
    Id_setRecord        	=  8,
    Id_constructor		    =  9,
    Id_enumerateRecords		=  10,

    
    MAX_PROTOTYPE_ID        =  10;

	public static final int RSTYPE_STRING = 1;
	public static final int RSTYPE_INT = 2;
	public static final int RSTYPE_LONG = 3;
	public static final int RSTYPE_DOUBLE = 4;
	public static final int RSTYPE_BYTEARRAY = 5;
	public static final int RSTYPE_DATE = 6;
	public static final int RSTYPE_BOOLEAN = 7;
	public static final int RSTYPE_IMAGE = 8;

	protected void initPrototypeId(int id)
    {
        String s;
        int arity;
        switch (id) {
		case Id_constructor:			arity = 1;	s = "constructor";		break;
		case Id_addRecord:		arity = 1;	s = "addRecord";		break;
		case Id_addRecordListener:	arity = 1;	s = "addRecordListener";		break;
		case Id_close:			arity = 0;	s = "close";		break;
		case Id_deleteRecord:	arity = 1;	s = "deleteRecord";				break;
		case Id_getRecord:		arity = 1;	s = "getRecord";			break;
		case Id_getRecordSize:	arity = 1;	s = "getRecordSize";			break;
		case Id_removeRecordListener:		arity = 1;	s = "removeRecordListener";		break;
		case Id_setRecord:	arity = 2;	s = "setRecord";	break;
		case Id_enumerateRecords:	arity = 0;	s = "enumerateRecords";	break;
		
		default:
			throw new IllegalArgumentException(String.valueOf(id));
		}
		initPrototypeMethod(RECORDSTORE_TAG, id, s, arity);
    }

	
    protected int findPrototypeId(String s)
    {
        int id;
        L0: { id = 0; String X = null; 
        L: switch (s.length()) {
            case 5: 
            	X="close";id=Id_close; break L;
            case 9: 
            	switch(s.charAt(0)) {
        		case 'a': X="addRecord";id=Id_addRecord; break L;
        		case 'g': X="getRecord";id=Id_getRecord; break L;
        		case 's': X="setRecord";id=Id_setRecord; break L;
            	}
        	case 11: 
        		X="constructor";id=Id_constructor; break L;
        	case 12: 
        		X="deleteRecord";id=Id_deleteRecord; break L;
        	case 13: 
        		X="getRecordSize";id=Id_getRecordSize; break L;
        	case 16: 
				X = "enumerateRecords";
				id = Id_enumerateRecords;
				break L;
        	case 17: 
				X = "addRecordListener";
				id = Id_addRecordListener;
				break L;
        	case 20: 
        		X="removeRecordListener";id=Id_removeRecordListener; break L;
            }
            if (X!=null && X!=s && !X.equals(s)) id = 0;
            break L0;
        }
        return id;
    }


    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope,
                             Scriptable thisObj, Object[] args)
    {
        if (!f.hasTag(RECORDSTORE_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        
        
        int id = f.getMethodId();
        switch (id) {
          case Id_constructor:
              return jsConstructor(cx, args);
          case Id_deleteRecordStore:
              deleteRecordStore(cx, args);
              return null;
        }

        // The rest of Form.prototype methods require thisObj to be Form
        if (!(thisObj instanceof NativeRecordstore))
            throw incompatibleCallError(f);
        NativeRecordstore realThis = (NativeRecordstore)thisObj;

        switch (id) {
          case Id_addRecord:
        	  return addRecord(realThis, args);
          case Id_close:
        	  close(realThis);
        	  return realThis;
          case Id_deleteRecord:
        	  deleteRecord(realThis, args);
        	  return realThis;
          case Id_setRecord:
        	  setRecord(realThis, args);
        	  return realThis;
          case Id_getRecord:
        	  return getRecord(cx, scope, realThis, args);
          case Id_addRecordListener:
        	  addRecordListener(realThis, args);
        	  return realThis;
          case Id_removeRecordListener:
        	  removeRecordListener(realThis, args);
        	  return realThis;
          case Id_enumerateRecords:
        	  return enumerateRecords(cx, scope, realThis);
 
	    	  
	      default: throw new IllegalArgumentException(String.valueOf(id));
        }

    }

	private static NativeRecordstore jsConstructor(Context cx, Object[] args)
    {
        NativeRecordstore obj = new NativeRecordstore();
        
        if (args.length == 0) {
          	args = ScriptRuntime.padArguments(args, 1);
        }
        //call from recordListener ?
        if (args[0] instanceof RecordStore) {
        	obj.recordStore = (RecordStore) args[0];
        } else {
	        try {
	        	final String rsName = ScriptRuntime.toString(args[0]); //RmsHandler.getRsName(cx.currentWidgetId, ScriptRuntime.toString(args[0])); 
				obj.recordStore = RecordStore.openRecordStore( rsName, true);
			} catch (RecordStoreException e) {
		        throw createRecordStoreError(e.getMessage());
			}
		}
        return obj;
    }

	private void close(NativeRecordstore rs) {
		if (rs.recordStore != null) {
			if (rs.recordStoreListeners != null) {
				rs.recordStoreListeners.clear();
			}
			try {
				rs.recordStore.closeRecordStore();
			} catch (RecordStoreNotOpenException ignore) {
				// Dann war eben noch kein Open, nicht dramatisch
			} catch (RecordStoreException e) {
		        throw createRecordStoreError(e.getMessage());
			}
		}
	}

    private static void deleteRecordStore(Context cx, Object[] args)
    {
        if (args.length == 0) {
          	args = ScriptRuntime.padArguments(args, 1);
        }
        final String rsName = ScriptRuntime.toString(args[0]); //RmsHandler.getRsName(cx.currentWidgetId, ScriptRuntime.toString(args[0])); 

        try {
        	if (rsName != null) {
        		RecordStore.deleteRecordStore(rsName);
        	}
		} catch (RecordStoreException e) {
	        throw createRecordStoreError(e.getMessage());
		}
    }

    private Object addRecord(NativeRecordstore rs, Object[] args) {
    	if (args.length != 1 && !(args[0] instanceof NativeArray) ) {
    		ScriptRuntime.typeError0("msg.arg.isnt.array");
    	}
    	byte[] record = Util.getRecordFromArray((NativeArray) args[0], this);
    	try {
			return ScriptRuntime.wrapNumber(rs.recordStore.addRecord(record, 0, record.length));
		} catch (RecordStoreException e) {
	        throw createRecordStoreError(e.getMessage());
		}
	}


    /**
	 * @param e
	 * @throws EcmaError
	 */
	public static EcmaError createRecordStoreError(String msg) {
		if (msg == null) {
			return ScriptRuntime.constructError("Error", ScriptRuntime.getMessage0("msg.rs.ex"));
		} else {
			return ScriptRuntime.constructError("Error", ScriptRuntime.getMessage1("msg.rs.ex", msg));
		}
	}
    
    private void deleteRecord(NativeRecordstore rs, Object[] args) {
    	if (args.length > 0) {
    		int id = ScriptRuntime.toInt32(args[0]);
    		try {
				rs.recordStore.deleteRecord(id);
			} catch (RecordStoreNotOpenException e) {
				throw createErrorRecordStoreNotOpen();
			} catch (RecordStoreException e) {
		        throw createRecordStoreError(e.getMessage());
			}
    	}
    }

    private void setRecord(NativeRecordstore rs, Object[] args) {
    	if (args.length < 2 && !(args[1] instanceof NativeArray) ) {
    		ScriptRuntime.typeError0("msg.arg.isnt.array");
    	}
    	byte[] record = Util.getRecordFromArray((NativeArray) args[1], this);
		int id = ScriptRuntime.toInt32(args[0]);
    	try {
			rs.recordStore.setRecord(id, record, 0, record.length);
		} catch (RecordStoreException e) {
	        throw createRecordStoreError(e.getMessage());
		}
	}

    private Object getRecord(Context cx, Scriptable scope, NativeRecordstore rs, Object[] args) {
    	if (args.length > 0) {
    		int id = ScriptRuntime.toInt32(args[0]);
    		try {
				byte[] record = rs.recordStore.getRecord(id);
				return Util.createArrayFromRecord(cx, scope, record);
			} catch (RecordStoreNotOpenException e) {
				throw createErrorRecordStoreNotOpen();
			} catch (RecordStoreException e) {
		        throw createRecordStoreError(e.getMessage());
			}
    	}
		throw ScriptRuntime.typeError0("msg.rs.arg.expected");
	}
    
    private void addRecordListener(NativeRecordstore rs, Object[] args) {
    	if (args.length > 0 
    			&& args[0] instanceof InterpretedFunction) {
    		InterpretedFunction function = (InterpretedFunction) args[0];
    		RecordListener rl = new JSRecordListener(function);
    		if (rs.recordStoreListeners == null) {
    			rs.recordStoreListeners = new Hashtable();
    		}
    		if (rs.recordStoreListeners.get(function.getFunctionName()) == null){
    			rs.recordStoreListeners.put(function.getFunctionName(), rl);
        		rs.recordStore.addRecordListener(rl);
    		}
    		return;
    	}
		throw ScriptRuntime.typeError0("msg.rs.function.expected");
	}
    
    private void removeRecordListener(NativeRecordstore rs, Object[] args) {
    	if (args.length > 0 
    			&& args[0] instanceof InterpretedFunction) {
    		InterpretedFunction function = (InterpretedFunction) args[0];
    		if (rs.recordStoreListeners != null) {
        		RecordListener rl = (RecordListener) rs.recordStoreListeners.get(function.getFunctionName());
        		rs.recordStoreListeners.remove(function.getFunctionName());
        		rs.recordStore.removeRecordListener(rl);
    		}
    		return;
    	}
		throw ScriptRuntime.typeError0("msg.rs.function.expected");
	}

    private Scriptable enumerateRecords(Context cx, Scriptable scope, NativeRecordstore rs) {
        Scriptable top = Js.getTopLevelScope(VMBridge.instance.getScope());
        Object[] args = new Object[1];
		args[0] = rs;
		return ScriptRuntime.newObject(VMBridge.instance.getContext(), top, "RecordEnumeration", args);
    }

	public RecordStore getRecordStore() {
		return recordStore;
	}
}


