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
 *   Igor Bukanov
 *   Bob Jervis
 *   Roger Lawrence
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


import de.enough.polish.event.EventManager;
import de.enough.polish.io.Serializer;

final public class InterpretedFunction extends NativeFunction implements Script
{
    //static final long serialVersionUID = 541475680333911468L;

    InterpreterData idata;
    Scriptable[] functionRegExps;

    public InterpretedFunction() {
    	// for implementing externalizable
    }

    private InterpretedFunction(InterpreterData idata)
    {
        this.idata = idata;

        // Always get Context from the current thread to
        // avoid security breaches via passing mangled Context instances
        // with bogus SecurityController
    }

    private InterpretedFunction(InterpretedFunction parent, int index)
    {
        this.idata = parent.idata.itsNestedFunctions[index];
    }

    /**
     * Create script from compiled bytecode.
     */
    static InterpretedFunction createScript(InterpreterData idata)
    {
        InterpretedFunction f;
        f = new InterpretedFunction(idata);
        return f;
    }

    /**
     * Create function compiled from Function(...) constructor.
     */
    static InterpretedFunction createFunction(Context cx,Scriptable scope,
                                              InterpreterData idata)
    {
        InterpretedFunction f;
        f = new InterpretedFunction(idata);
        f.initInterpretedFunction(cx, scope);
        return f;
    }

    /**
     * Create function embedded in script or another function.
     */
    static InterpretedFunction createFunction(Context cx, Scriptable scope,
                                              InterpretedFunction  parent,
                                              int index)
    {
        InterpretedFunction f = new InterpretedFunction(parent, index);
        f.initInterpretedFunction(cx, scope);
        return f;
    }

    Scriptable[] createRegExpWraps(Context cx, Scriptable scope)
    {
        if (this.idata.itsRegExpLiterals == null) Kit.codeBug();

        RegExpProxy rep = ScriptRuntime.checkRegExpProxy(cx);
        int N = this.idata.itsRegExpLiterals.length;
        Scriptable[] array = new Scriptable[N];
        for (int i = 0; i != N; ++i) {
            array[i] = rep.wrapRegExp(cx, scope, this.idata.itsRegExpLiterals[i]);
        }
        return array;
    }

    private void initInterpretedFunction(Context cx, Scriptable scope)
    {
        initScriptFunction(cx, scope);
        if (this.idata.itsRegExpLiterals != null) {
            this.functionRegExps = createRegExpWraps(cx, scope);
        }
    }

    public String getFunctionName()
    {
        return (this.idata.itsName == null) ? "" : this.idata.itsName;
    }

    /**
     * Calls the function.
     * @param cx the current context 
     * @param scope the scope used for the call
     * @param thisObj the value of "this"
     * @param args function arguments. Must not be null. You can use 
     * {@link ScriptRuntime#emptyArgs} to pass empty arguments.
     * @return the result of the function call.
     */
    public Object call(Context cx, Scriptable scope, Scriptable thisObj,
                       Object[] args)
    {
    	try {
	        if (!ScriptRuntime.hasTopCall(cx)) {
	            return ScriptRuntime.doTopCall(this, cx, scope, thisObj, args);
	        }
	        Object ret = Interpreter.interpret(this, cx, scope, thisObj, args);
	        cx.checkRunWidget();
	        return ret;
    	} catch (RhinoException e){
    		//#debug error
    		System.out.println("Unable to call interpreted function " + e );
    		if(cx.isOptionOnErrorFireEvent()) {
    			EventManager.fireEvent("js.error", this, e );
    		}
    		if(cx.isOptionOnErrorThrowException()) {
    			throw e;
    		}
    	} catch (Exception e){
    		//#debug error
    		System.out.println("Unable to call interpreted function" + e );
    		if(cx.isOptionOnErrorFireEvent()) {
    			StringBuffer errorText = new StringBuffer(e.getClass().toString())
    			.append(" ")
    			.append(e.getMessage())
    			.append(" lineNo: ")
    			.append(Interpreter.exceptionLineNo[0])
    			.append(" file: ")
    			.append(Interpreter.exceptionSourceFile);
    			EventManager.fireEvent("js.error", this, errorText );
    		}
    		if(cx.isOptionOnErrorThrowException()) {
    			throw new RuntimeException(e);
    		}
	    }
    	return null;
    }

    public Object exec(Context cx, Scriptable scope)
    {
        if (this.idata.itsFunctionType != 0) {
            // Can only be applied to scripts
            throw new IllegalStateException();
        }
        if (!ScriptRuntime.hasTopCall(cx)) {
            // It will go through "call" path. but they are equivalent
            return ScriptRuntime.doTopCall(
                this, cx, scope, scope, ScriptRuntime.emptyArgs);
        }
        return Interpreter.interpret(
            this, cx, scope, scope, ScriptRuntime.emptyArgs);
    }

    protected int getLanguageVersion()
    {
        return this.idata.languageVersion;
    }

    protected int getParamCount()
    {
        return this.idata.argCount;
    }

    protected int getParamAndVarCount()
    {
        return this.idata.argNames.length;
    }

    protected String getParamOrVarName(int index)
    {
        return this.idata.argNames[index];
    }

    protected boolean getParamOrVarConst(int index)
    {
        return this.idata.argIsConst[index];
    }

	
	public void read(DataInputStream in) throws IOException {
		super.read(in);
		this.idata = (InterpreterData) Serializer.deserialize(in);
		Object[] functions = (Object[]) Serializer.deserialize(in);
		Scriptable[] scriptables = new Scriptable[functions.length];
		System.arraycopy(functions, 0, scriptables, 0, functions.length );
		this.functionRegExps = scriptables; 
	}

	
	public void write(DataOutputStream out) throws IOException {
		super.write(out);
		Serializer.serialize(this.idata, out);
		Serializer.serialize(this.functionRegExps, out);
	}
}

