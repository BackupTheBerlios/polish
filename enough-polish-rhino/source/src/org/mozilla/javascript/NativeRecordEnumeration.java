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

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.mozilla.javascript.util.Util;




/**
 * This class implements the Date native object.
 * See ECMA 15.9.
 * @author Mike McCabe
 */
final public class NativeRecordEnumeration extends IdScriptableObject
{
    public class JSRecordFilter implements RecordFilter {
    	
    	private InterpretedFunction function = null;
    	
		public JSRecordFilter(InterpretedFunction filterFunction) {
			this.function = filterFunction;
		}

		public boolean matches(byte[] record) {
			Object[] args = new Object[1];
			args[0] = Util.createArrayFromRecord(VMBridge.instance.getContext(), VMBridge.instance.getScope(), record);
			Object result = function.call(VMBridge.instance.getContext(), VMBridge.instance.getScope(), VMBridge.instance.getScope(), args );
			if (result instanceof NativeBoolean) {
				return ScriptRuntime.toBoolean(result);
			} else if (result instanceof Boolean) {
				return ((Boolean) result).booleanValue();
			} else {
				throw ScriptRuntime.typeError("msg.rs.boolean.expected");
			}
		}

	}

    public class JSRecordComparator implements RecordComparator {
    	
    	private InterpretedFunction function = null;
    	
		public JSRecordComparator(InterpretedFunction sortFunction) {
			this.function = sortFunction;
		}

		public int compare(byte[] record1, byte[] record2) {
			Object[] args = new Object[2];
			args[0] = Util.createArrayFromRecord(VMBridge.instance.getContext(), VMBridge.instance.getScope(), record1);
			args[1] = Util.createArrayFromRecord(VMBridge.instance.getContext(), VMBridge.instance.getScope(), record2);
			Object result = function.call(VMBridge.instance.getContext(), VMBridge.instance.getScope(), VMBridge.instance.getScope(), args );
			int res = 0;
			if (result instanceof Double) {
				res = ((Double) result).intValue();
			} else if (result instanceof NativeNumber) {
				res = ScriptRuntime.toInt32(result);
			} else {
				throw ScriptRuntime.typeError("msg.rs.number.expected");
			}
			return (res > 0) ? RecordComparator.FOLLOWS 
							: (res < 0) ? RecordComparator.PRECEDES : RecordComparator.EQUIVALENT;
		}
	}

	//static final long serialVersionUID = -8307438915861678966L;

    private static final Object RECORDENUMERATION_TAG = new Object();

    private NativeRecordstore recordStore;
    private RecordEnumeration recordEnumeration;
    private InterpretedFunction filterFunction;
    private InterpretedFunction sortFunction;

	public NativeRecordEnumeration() {
	}

	static void init(Scriptable scope, boolean sealed)
    {
        NativeRecordEnumeration obj = new NativeRecordEnumeration();
        // Set the value of the prototype Date to NaN ('invalid date');
        obj.recordStore = null;

        obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, sealed);
    }

    public String getClassName()
    {
        return "RecordEnumeration";
    }

    /* *******************************************************
     * 			Properties
     * ******************************************************* */
    private static final int
    Id_hasNextElement	= 1,
    Id_hasPreviousElement		= 2,
    Id_nextRecordId	= 3,
    Id_numRecords	= 4,
    Id_previousRecordId	= 5,

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
	           case 10: X="numRecords"; id=Id_numRecords; attr = PERMANENT | DONTENUM | READONLY; break L;
	           case 12: X="nextRecordId"; id=Id_nextRecordId; attr = PERMANENT | DONTENUM | READONLY; break L;
	           case 14: X="hasNextElement"; id=Id_hasNextElement; attr = PERMANENT | DONTENUM | READONLY; break L;
	           case 16: X="previousRecordId"; id=Id_previousRecordId; attr = PERMANENT | DONTENUM | READONLY; break L;
	           case 18: X="hasPreviousElement"; id=Id_hasPreviousElement; attr = PERMANENT | DONTENUM | READONLY; break L;
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
	    case Id_hasNextElement:  	return "hasNextElement";
	    case Id_numRecords:  	return "numRecords";
	    case Id_nextRecordId:  	return "nextRecordId";
	    case Id_previousRecordId:  	return "previousRecordId";
	    case Id_hasPreviousElement:  	return "hasPreviousElement";
	    }
	    return super.getInstanceIdName(id);
	}
	
	protected Object getInstanceIdValue(int id)
	{
		if (recordEnumeration == null) {
			startEnumeration(this);
		}
		
	    switch (id) {
	    case Id_hasNextElement:
					return ScriptRuntime.wrapBoolean(recordEnumeration.hasNextElement());
	    case Id_hasPreviousElement:
			return ScriptRuntime.wrapBoolean(recordEnumeration.hasPreviousElement());
	    case Id_numRecords:
					return ScriptRuntime.wrapNumber(recordEnumeration.numRecords());
	    case Id_nextRecordId:
				try {
					return ScriptRuntime.wrapNumber(recordEnumeration.nextRecordId());
				} catch (InvalidRecordIDException e) {
					throw NativeRecordstore.createRecordStoreError(e.getMessage());
				}
	    case Id_previousRecordId:
				try {
					return ScriptRuntime.wrapNumber(recordEnumeration.previousRecordId());
				} catch (InvalidRecordIDException e) {
					throw NativeRecordstore.createRecordStoreError(e.getMessage());
				}
	    }
	    return super.getInstanceIdValue(id);
	}


	/**
	 * @throws EcmaError
	 */
	private EcmaError createErrorRecordStoreNotOpen() {
		String msg = ScriptRuntime.getMessage0("msg.rs.notopen");
		return ScriptRuntime.constructError("Error", msg);
	}
	
	
    private static final int
    Id_constructor		 =  1,
    Id_destroy		     =  2,
    Id_nextRecord        =  3,
    Id_previousRecord    =  4,
    Id_rebuild			 =  5,
    Id_reset		     =  6,
    Id_setFilterFunction =  7,
    Id_setSortFunction	 =  8,

    
    MAX_PROTOTYPE_ID     =  8;

	protected void initPrototypeId(int id)
    {
        String s;
        int arity;
        switch (id) {
		case Id_constructor:		arity = 1;	s = "constructor";		break;
		case Id_destroy:		arity = 0;	s = "destroy";		break;
		case Id_nextRecord:	arity = 0;	s = "nextRecord";		break;
		case Id_previousRecord:			arity = 0;	s = "previousRecord";		break;
		case Id_rebuild:	arity = 0;	s = "rebuild";				break;
		case Id_reset:		arity = 0;	s = "reset";			break;
		case Id_setFilterFunction:	arity = 1;	s = "setFilterFunction";			break;
		case Id_setSortFunction:		arity = 1;	s = "setSortFunction";		break;
		
		default:
			throw new IllegalArgumentException(String.valueOf(id));
		}
		initPrototypeMethod(RECORDENUMERATION_TAG, id, s, arity);
    }

	
    protected int findPrototypeId(String s)
    {
        int id;
        L0: { id = 0; String X = null; 
        L: switch (s.length()) {
            case 5: X="reset";id=Id_reset; break L;
            case 7: 
            	switch(s.charAt(0)) {
            	case 'd': X="destroy";id=Id_destroy; break L;
            	case 'r': X="rebuild";id=Id_rebuild; break L;
            	}
    		case 10: 
    			X = "nextRecord"; id = Id_nextRecord; break L;
        	case 11: 
        		X="constructor";id=Id_constructor; break L;
        	case 14: 
            	X="previousRecord";id=Id_previousRecord; break L;
        	case 15: 
        		X="setSortFunction";id=Id_setSortFunction; break L;
        	case 17: 
        		X="setFilterFunction";id=Id_setFilterFunction; break L;
            }
            if (X!=null && X!=s && !X.equals(s)) id = 0;
            break L0;
        }
        return id;
    }


    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope,
                             Scriptable thisObj, Object[] args)
    {
        if (!f.hasTag(RECORDENUMERATION_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        
        
        int id = f.getMethodId();
        switch (id) {
          case Id_constructor:
              return jsConstructor(args);
        }

        // The rest of Form.prototype methods require thisObj to be Form
        if (!(thisObj instanceof NativeRecordEnumeration))
            throw incompatibleCallError(f);
        NativeRecordEnumeration realThis = (NativeRecordEnumeration)thisObj;

        switch (id) {
	        case Id_setFilterFunction:
	        	if (args.length > 0 && args[0] instanceof InterpretedFunction) {
	        		realThis.filterFunction = (InterpretedFunction) args[0];
	        	}
	      	  	return realThis;
	        case Id_setSortFunction:
	        	if (args.length > 0 && args[0] instanceof InterpretedFunction) {
	        		realThis.sortFunction = (InterpretedFunction) args[0];
	        	}
	      	  	return realThis;
        }

        // from here on the enumeration must be started
  		if (realThis.recordEnumeration == null) {
			startEnumeration(realThis);
		}
        switch (id) {
          case Id_destroy:
        	  realThis.recordEnumeration.destroy();
        	  return realThis;
          case Id_previousRecord:
        	  return prevRecord(cx, scope, realThis);
          case Id_rebuild:
        	  realThis.recordEnumeration.rebuild();
        	  return realThis;
          case Id_reset:
        	  realThis.recordEnumeration.reset();
        	  return realThis;
          case Id_nextRecord:
        	  return nextRecord(cx, scope, realThis);
	    	  
	      default: throw new IllegalArgumentException(String.valueOf(id));
        }

    }

	/**
	 * @param cx
	 * @param scope
	 * @return
	 * @throws InvalidRecordIDException
	 * @throws RecordStoreNotOpenException
	 * @throws RecordStoreException
	 */
	private Object nextRecord(Context cx, Scriptable scope, NativeRecordEnumeration en) {
		try {
			return Util.createArrayFromRecord(cx, scope, en.recordEnumeration.nextRecord());
		} catch (RecordStoreNotOpenException e) {
			throw NativeRecordstore.createErrorRecordStoreNotOpen();
		} catch (RecordStoreException e) {
	        throw NativeRecordstore.createRecordStoreError(e.getMessage());
		}
	}

	/**
	 * @param cx
	 * @param scope
	 * @return
	 * @throws InvalidRecordIDException
	 * @throws RecordStoreNotOpenException
	 * @throws RecordStoreException
	 */
	private Object prevRecord(Context cx, Scriptable scope, NativeRecordEnumeration en) {
		try {
			return Util.createArrayFromRecord(cx, scope, en.recordEnumeration.previousRecord());
		} catch (RecordStoreNotOpenException e) {
			throw NativeRecordstore.createErrorRecordStoreNotOpen();
		} catch (RecordStoreException e) {
	        throw NativeRecordstore.createRecordStoreError(e.getMessage());
		}
	}

	private static NativeRecordEnumeration jsConstructor(Object[] args)
    {
        NativeRecordEnumeration obj = new NativeRecordEnumeration();
        
        //call from recordListener ?
        if (args.length == 1 && args[0] instanceof NativeRecordstore) {
        	obj.recordStore = (NativeRecordstore) args[0];
        } else {
        	ScriptRuntime.typeError0("msg.rs.recstore.expected");
		}
        return obj;
    }

	private void startEnumeration(NativeRecordEnumeration en) {
		RecordFilter rf = null;
		RecordComparator rc = null;
		if (filterFunction != null) {
			rf = new JSRecordFilter(filterFunction);
		}
		if (sortFunction != null) {
			rc = new JSRecordComparator(sortFunction);
		}
		try {
			en.recordEnumeration = en.recordStore.getRecordStore().enumerateRecords(rf, rc, false);
		} catch (RecordStoreNotOpenException e) {
			throw NativeRecordstore.createErrorRecordStoreNotOpen();
		}
	}

}


