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
 *    Bob Jervis
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

//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;

import de.enough.polish.event.EventManager;

/**
 * This class represents the runtime context of an executing script.
 *
 * Before executing a script, an instance of Context must be created
 * and associated with the thread that will be executing the script.
 * The Context will be used to store information about the executing
 * of the script such as the call stack. Contexts are associated with
 * the current thread  using the {@link #call(ContextAction)}
 * or {@link #enter()} methods.<p>
 *
 * Different forms of script execution are supported. Scripts may be
 * evaluated from the source directly, or first compiled and then later
 * executed. Interactive execution is also supported.<p>
 *
 * Some aspects of script execution, such as type conversions and
 * object creation, may be accessed directly through methods of
 * Context.
 *
 * @see Scriptable
 * @author Norris Boyd
 * @author Brendan Eich
 */

public class Context
{
    /**
     * Language versions.
     *
     * All integral values are reserved for future version numbers.
     */

    /**
     * The unknown version.
     */
    public static final int VERSION_UNKNOWN =   -1;

    /**
     * The default version.
     */
    public static final int VERSION_DEFAULT =    0;

    /**
     * JavaScript 1.0
     */
    public static final int VERSION_1_0 =      100;

    /**
     * JavaScript 1.1
     */
    public static final int VERSION_1_1 =      110;

    /**
     * JavaScript 1.2
     */
    public static final int VERSION_1_2 =      120;

    /**
     * JavaScript 1.3
     */
    public static final int VERSION_1_3 =      130;

    /**
     * JavaScript 1.4
     */
    public static final int VERSION_1_4 =      140;

    /**
     * JavaScript 1.5
     */
    public static final int VERSION_1_5 =      150;

    /**
     * JavaScript 1.6
     */
    public static final int VERSION_1_6 =      160;

    /**
     * Controls behaviour of <tt>Date.prototype.getYear()</tt>.
     * If <tt>hasFeature(FEATURE_NON_ECMA_GET_YEAR)</tt> returns true,
     * Date.prototype.getYear subtructs 1900 only if 1900 <= date < 2000.
     * The default behavior of {@link #hasFeature(int)} is always to subtruct
     * 1900 as rquired by ECMAScript B.2.4.
     */
    public static final int FEATURE_NON_ECMA_GET_YEAR = 1;

    /**
     * Control if member expression as function name extension is available.
     * If <tt>hasFeature(FEATURE_MEMBER_EXPR_AS_FUNCTION_NAME)</tt> returns
     * true, allow <tt>function memberExpression(args) { body }</tt> to be
     * syntax sugar for <tt>memberExpression = function(args) { body }</tt>,
     * when memberExpression is not a simple identifier.
     * See ECMAScript-262, section 11.2 for definition of memberExpression.
     * By default {@link #hasFeature(int)} returns false.
     */
    public static final int FEATURE_MEMBER_EXPR_AS_FUNCTION_NAME = 2;

    /**
     * Control if reserved keywords are treated as identifiers.
     * If <tt>hasFeature(RESERVED_KEYWORD_AS_IDENTIFIER)</tt> returns true,
     * treat future reserved keyword (see  Ecma-262, section 7.5.3) as ordinary
     * identifiers but warn about this usage.
     *
     * By default {@link #hasFeature(int)} returns false.
     */
    public static final int FEATURE_RESERVED_KEYWORD_AS_IDENTIFIER = 3;

    /**
     * Control if <tt>toString()</tt> should returns the same result
     * as  <tt>toSource()</tt> when applied to objects and arrays.
     * If <tt>hasFeature(FEATURE_TO_STRING_AS_SOURCE)</tt> returns true,
     * calling <tt>toString()</tt> on JS objects gives the same result as
     * calling <tt>toSource()</tt>. That is it returns JS source with code
     * to create an object with all enumeratable fields of the original object
     * instead of printing <tt>[object <i>result of
     * {@link Scriptable#getClassName()}</i>]</tt>.
     * <p>
     * By default {@link #hasFeature(int)} returns true only if
     * the current JS version is set to {@link #VERSION_1_2}.
     */
    public static final int FEATURE_TO_STRING_AS_SOURCE = 4;

    /**
     * Control if properties <tt>__proto__</tt> and <tt>__parent__</tt>
     * are treated specially.
     * If <tt>hasFeature(FEATURE_PARENT_PROTO_PROPERTIES)</tt> returns true,
     * treat <tt>__parent__</tt> and <tt>__proto__</tt> as special properties.
     * <p>
     * The properties allow to query and set scope and prototype chains for the
     * objects. The special meaning of the properties is available
     * only when they are used as the right hand side of the dot operator.
     * For example, while <tt>x.__proto__ = y</tt> changes the prototype
     * chain of the object <tt>x</tt> to point to <tt>y</tt>,
     * <tt>x["__proto__"] = y</tt> simply assigns a new value to the property
     * <tt>__proto__</tt> in <tt>x</tt> even when the feature is on.
     *
     * By default {@link #hasFeature(int)} returns true.
     */
    public static final int FEATURE_PARENT_PROTO_PROPERTIES = 5;

	/**
	 * @deprecated In previous releases, this name was given to
	 * FEATURE_PARENT_PROTO_PROPERTIES.
	 */
    public static final int FEATURE_PARENT_PROTO_PROPRTIES = 5;
	
    /**
     * Control if dynamic scope should be used for name access.
     * If hasFeature(FEATURE_DYNAMIC_SCOPE) returns true, then the name lookup
     * during name resolution will use the top scope of the script or function
     * which is at the top of JS execution stack instead of the top scope of the
     * script or function from the current stack frame if the top scope of
     * the top stack frame contains the top scope of the current stack frame
     * on its prototype chain.
     * <p>
     * This is useful to define shared scope containing functions that can
     * be called from scripts and functions using private scopes.
     * <p>
     * By default {@link #hasFeature(int)} returns false.
     * @since 1.6 Release 1
     */
    public static final int FEATURE_DYNAMIC_SCOPE = 7;

    /**
     * Control if strict variable mode is enabled.
     * When the feature is on Rhino reports runtime errors if assignment
     * to a global variable that does not exist is executed. When the feature
     * is off such assignments creates new variable in the global scope  as
     * required by ECMA 262.
     * <p>
     * By default {@link #hasFeature(int)} returns false.
     * @since 1.6 Release 1
     */
    public static final int FEATURE_STRICT_VARS = 8;

    /**
     * Control if strict eval mode is enabled.
     * When the feature is on Rhino reports runtime errors if non-string
     * argument is passed to the eval function. When the feature is off
     * eval simply return non-string argument as is without performing any
     * evaluation as required by ECMA 262.
     * <p>
     * By default {@link #hasFeature(int)} returns false.
     * @since 1.6 Release 1
     */
    public static final int FEATURE_STRICT_EVAL = 9;

    /**
     * When the feature is on Rhino will add a "fileName" and "lineNumber"
     * properties to Error objects automatically. When the feature is off, you
     * have to explicitly pass them as the second and third argument to the
     * Error constructor. Note that neither behaviour is fully ECMA 262 
     * compliant (as 262 doesn't specify a three-arg constructor), but keeping 
     * the feature off results in Error objects that don't have
     * additional non-ECMA properties when constructed using the ECMA-defined
     * single-arg constructor and is thus desirable if a stricter ECMA 
     * compliance is desired, specifically adherence to the point 15.11.5. of
     * the standard.
     * <p>
     * By default {@link #hasFeature(int)} returns false.
     * @since 1.6 Release 6
     */
    public static final int FEATURE_LOCATION_INFORMATION_IN_ERROR = 10;

    /**
     * Controls whether JS 1.5 'strict mode' is enabled.
     * When the feature is on, Rhino reports more than a dozen different
     * warnings.  When the feature is off, these warnings are not generated.
     * FEATURE_STRICT_MODE implies FEATURE_STRICT_VARS and FEATURE_STRICT_EVAL.
     * <p>
     * By default {@link #hasFeature(int)} returns false.
     * @since 1.6 Release 6
     */
    public static final int FEATURE_STRICT_MODE = 11;

    /**
     * Controls whether a warning should be treated as an error.
     * @since 1.6 Release 6
     */
    public static final int FEATURE_WARNING_AS_ERROR = 12;

    public static final String languageVersionProperty = "language version";
    public static final String errorReporterProperty   = "error reporter";

    /**
     * Convenient value to use as zero-length array of objects.
     */
    public static final Object[] emptyArgs = ScriptRuntime.emptyArgs;

    /**
     * Create a new Context.
     *
     * Note that the Context must be associated with a thread before
     * it can be used to execute a script.
     *
     * @see #enter()
     * @see #call(ContextAction)
     */
    public Context()
    {
        setLanguageVersion(VERSION_DEFAULT);
        this.maximumInterpreterStackDepth = Integer.MAX_VALUE;
    }

    /**
     * Get the current Context.
     *
     * The current Context is per-thread; this method looks up
     * the Context associated with the current thread. <p>
     *
     * @return the Context associated with the current thread, or
     *         null if no context is associated with the current
     *         thread.
     * @see org.mozilla.javascript.Context#enter()
     * @see org.mozilla.javascript.Context#exit()
     */
    public static Context getCurrentContext()
    {
        Object helper = VMBridge.instance.getThreadContextHelper();
        return VMBridge.instance.getContext(helper);
    }

    /**
     * Get a context associated with the current thread, creating
     * one if need be.
     *
     * The Context stores the execution state of the JavaScript
     * engine, so it is required that the context be entered
     * before execution may begin. Once a thread has entered
     * a Context, then getCurrentContext() may be called to find
     * the context that is associated with the current thread.
     * <p>
     * Calling <code>enter()</code> will
     * return either the Context currently associated with the
     * thread, or will create a new context and associate it
     * with the current thread. Each call to <code>enter()</code>
     * must have a matching call to <code>exit()</code>. For example,
     * <pre>
     *      Context cx = Context.enter();
     *      try {
     *          ...
     *          cx.evaluateString(...);
     *      } finally {
     *          Context.exit();
     *      }
     * </pre>
     * Instead of using <tt>enter()</tt>, <tt>exit()</tt> pair consider using
     * {@link #call(ContextAction)} which guarantees proper
     * association of Context instances with the current thread and is faster.
     * With this method the above example becomes:
     * <pre>
     *      Context.call(new ContextAction() {
     *          public Object run(Context cx) {
     *              ...
     *              cx.evaluateString(...);
     *              return null;
     *          }
     *      });
     * </pre>
     *
     * @return a Context associated with the current thread
     * @see #getCurrentContext()
     * @see #exit()
     * @see #call(ContextAction)
     */
    public static Context enter()
    {
        return enter(null);
    }

    /**
     * Get a Context associated with the current thread, using
     * the given Context if need be.
     * <p>
     * The same as <code>enter()</code> except that <code>cx</code>
     * is associated with the current thread and returned if
     * the current thread has no associated context and <code>cx</code>
     * is not associated with any other thread.
     * @param cx a Context to associate with the thread if possible
     * @return a Context associated with the current thread
     *
     * @see #enter()
     * @see #call(ContextAction)
     * @see ContextFactory#call(ContextAction)
     */
    public static Context enter(Context cx)
    {
        return enter(cx, ContextFactory.getGlobal());
    }
    
    static final Context enter(Context cx, ContextFactory factory)
    {
        Object helper = VMBridge.instance.getThreadContextHelper();
        Context old = VMBridge.instance.getContext(helper);
        if (old != null) {
            if (cx != null && cx != old && cx.enterCount != 0) {
                // The suplied context must be the context for
                // the current thread if it is already entered
                throw new IllegalArgumentException(
                    "Cannot enter Context active on another thread");
            }
            if (old.factory != null) {
                // Context with associated factory will be released
                // automatically and does not need to change enterCount
                return old;
            }
            if (old.sealed) onSealedMutation();
            cx = old;
        } else {
            if (cx == null) {
                cx = factory.makeContext();
            } else {
                if (cx.sealed) onSealedMutation();
            }
            if (cx.enterCount != 0 || cx.factory != null) {
                throw new IllegalStateException();
            }

            if (!cx.creationEventWasSent) {
                cx.creationEventWasSent = true;
                factory.onContextCreated(cx);
            }
        }

        if (old == null) {
            VMBridge.instance.setContext(helper, cx);
        }
        ++cx.enterCount;

        return cx;
     }

    /**
     * Exit a block of code requiring a Context.
     *
     * Calling <code>exit()</code> will remove the association between
     * the current thread and a Context if the prior call to
     * <code>enter()</code> on this thread newly associated a Context
     * with this thread.
     * Once the current thread no longer has an associated Context,
     * it cannot be used to execute JavaScript until it is again associated
     * with a Context.
     *
     * @see org.mozilla.javascript.Context#enter()
     * @see #call(ContextAction)
     * @see ContextFactory#call(ContextAction)
     */
    public static void exit()
    {
        exit(ContextFactory.getGlobal());
    }
    
    static void exit(ContextFactory factory)
    {
        Object helper = VMBridge.instance.getThreadContextHelper();
        Context cx = VMBridge.instance.getContext(helper);
        if (cx == null) {
            throw new IllegalStateException(
                "Calling Context.exit without previous Context.enter");
        }
        if (cx.factory != null) {
            // Context with associated factory will be released
            // automatically and does not need to change enterCount
            return;
        }
        if (cx.enterCount < 1) Kit.codeBug();
        if (cx.sealed) onSealedMutation();
        --cx.enterCount;
        if (cx.enterCount == 0) {
            VMBridge.instance.setContext(helper, null);
            factory.onContextReleased(cx);
        }
    }

    
    /**
     * Return {@link ContextFactory} instance used to create this Context
     * or the result of {@link ContextFactory#getGlobal()} if no factory
     * was used for Context creation.
     */
    public final ContextFactory getFactory()
    {
        ContextFactory result = factory;
        if (result == null) {
            result = ContextFactory.getGlobal();
        }
        return result;
    }

    static void onSealedMutation()
    {
        throw new IllegalStateException();
    }

    /**
     * Get the current language version.
     * <p>
     * The language version number affects JavaScript semantics as detailed
     * in the overview documentation.
     *
     * @return an integer that is one of VERSION_1_0, VERSION_1_1, etc.
     */
    public final int getLanguageVersion()
    {
       return version;
    }

    /**
     * Set the language version.
     *
     * <p>
     * Setting the language version will affect functions and scripts compiled
     * subsequently. See the overview documentation for version-specific
     * behavior.
     *
     * @param version the version as specified by VERSION_1_0, VERSION_1_1, etc.
     */
    public void setLanguageVersion(int version)
    {
        if (sealed) onSealedMutation();
        checkLanguageVersion(version);
//        Object listeners = propertyListeners;
//        if (listeners != null && version != this.version) {
//            firePropertyChangeImpl(listeners, languageVersionProperty,
//                               new Integer(this.version),
//                               new Integer(version));
//        }
        this.version = version;
    }

    public static boolean isValidLanguageVersion(int version)
    {
        switch (version) {
            case VERSION_DEFAULT:
            case VERSION_1_0:
            case VERSION_1_1:
            case VERSION_1_2:
            case VERSION_1_3:
            case VERSION_1_4:
            case VERSION_1_5:
            case VERSION_1_6:
                return true;
        }
        return false;
    }

    public static void checkLanguageVersion(int version)
    {
        if (isValidLanguageVersion(version)) {
            return;
        }
        throw new IllegalArgumentException("Bad language version: "+version);
    }

    /**
     * Get the current error reporter.
     *
     * @see org.mozilla.javascript.ErrorReporter
     */
    public final ErrorReporter getErrorReporter()
    {
        if (errorReporter == null) {
            return DefaultErrorReporter.instance;
        }
        return errorReporter;
    }

    /**
     * Register an object to receive notifications when a bound property
     * has changed
     * @see java.beans.PropertyChangeEvent
     * @see #removePropertyChangeListener(java.beans.PropertyChangeListener)
     * @param l the listener
     */
//    public final void addPropertyChangeListener(PropertyChangeListener l)
//    {
//        if (sealed) onSealedMutation();
//        propertyListeners = Kit.addListener(propertyListeners, l);
//    }

    /**
     * Remove an object from the list of objects registered to receive
     * notification of changes to a bounded property
     * @see java.beans.PropertyChangeEvent
     * @see #addPropertyChangeListener(java.beans.PropertyChangeListener)
     * @param l the listener
     */
//    public final void removePropertyChangeListener(PropertyChangeListener l)
//    {
//        if (sealed) onSealedMutation();
//        propertyListeners = Kit.removeListener(propertyListeners, l);
//    }

    /**
     * Notify any registered listeners that a bounded property has changed
     * @see #addPropertyChangeListener(java.beans.PropertyChangeListener)
     * @see #removePropertyChangeListener(java.beans.PropertyChangeListener)
     * @see java.beans.PropertyChangeListener
     * @see java.beans.PropertyChangeEvent
     * @param  property  the bound property
     * @param  oldValue  the old value
     * @param  newValue   the new value
     */
//    final void firePropertyChange(String property, Object oldValue,
//                                  Object newValue)
//    {
//        Object listeners = propertyListeners;
//        if (listeners != null) {
//            firePropertyChangeImpl(listeners, property, oldValue, newValue);
//        }
//    }

//    private void firePropertyChangeImpl(Object listeners, String property,
//                                        Object oldValue, Object newValue)
//    {
//        for (int i = 0; ; ++i) {
//            Object l = Kit.getListener(listeners, i);
//            if (l == null)
//                break;
//            if (l instanceof PropertyChangeListener) {
//                PropertyChangeListener pcl = (PropertyChangeListener)l;
//                pcl.propertyChange(new PropertyChangeEvent(
//                    this, property, oldValue, newValue));
//            }
//        }
//    }

    /**
     * Report a warning using the error reporter for the current thread.
     *
     * @param message the warning message to report
     * @param sourceName a string describing the source, such as a filename
     * @param lineno the starting line number
     * @param lineSource the text of the line (may be null)
     * @param lineOffset the offset into lineSource where problem was detected
     * @see org.mozilla.javascript.ErrorReporter
     */
    public static void reportWarning(String message, String sourceName,
                                     int lineno, String lineSource,
                                     int lineOffset)
    {
        Context cx = Context.getContext();
        if (cx.hasFeature(FEATURE_WARNING_AS_ERROR))
            reportError(message, sourceName, lineno, lineSource, lineOffset);
        else
            cx.getErrorReporter().warning(message, sourceName, lineno,
                                          lineSource, lineOffset);
    }

    /**
     * Report a warning using the error reporter for the current thread.
     *
     * @param message the warning message to report
     * @see org.mozilla.javascript.ErrorReporter
     */
    public static void reportWarning(String message)
    {
        int[] linep = { 0 };
        String filename = getSourcePositionFromStack(linep);
        Context.reportWarning(message, filename, linep[0], null, 0);
    }

    /**
     * Report an error using the error reporter for the current thread.
     *
     * @param message the error message to report
     * @param sourceName a string describing the source, such as a filename
     * @param lineno the starting line number
     * @param lineSource the text of the line (may be null)
     * @param lineOffset the offset into lineSource where problem was detected
     * @see org.mozilla.javascript.ErrorReporter
     */
    public static void reportError(String message, String sourceName,
                                   int lineno, String lineSource,
                                   int lineOffset)
    {
        Context cx = getCurrentContext();
        if (cx != null) {
            cx.getErrorReporter().error(message, sourceName, lineno,
                                        lineSource, lineOffset);
        } else {
            throw new EvaluatorException(message, sourceName, lineno,
                                         lineSource, lineOffset);
        }
    }

    /**
     * Report a runtime error using the error reporter for the current thread.
     *
     * @param message the error message to report
     * @param sourceName a string describing the source, such as a filename
     * @param lineno the starting line number
     * @param lineSource the text of the line (may be null)
     * @param lineOffset the offset into lineSource where problem was detected
     * @return a runtime exception that will be thrown to terminate the
     *         execution of the script
     * @see org.mozilla.javascript.ErrorReporter
     */
    public static EvaluatorException reportRuntimeError(String message,
                                                        String sourceName,
                                                        int lineno,
                                                        String lineSource,
                                                        int lineOffset)
    {
        Context cx = getCurrentContext();
        if (cx != null) {
            return cx.getErrorReporter().
                            runtimeError(message, sourceName, lineno,
                                         lineSource, lineOffset);
        } else {
            throw new EvaluatorException(message, sourceName, lineno,
                                         lineSource, lineOffset);
        }
    }

    static EvaluatorException reportRuntimeError0(String messageId)
    {
        String msg = ScriptRuntime.getMessage0(messageId);
        return reportRuntimeError(msg);
    }

    static EvaluatorException reportRuntimeError1(String messageId,
                                                  Object arg1)
    {
        String msg = ScriptRuntime.getMessage1(messageId, arg1);
        return reportRuntimeError(msg);
    }

    static EvaluatorException reportRuntimeError2(String messageId,
                                                  Object arg1, Object arg2)
    {
        String msg = ScriptRuntime.getMessage2(messageId, arg1, arg2);
        return reportRuntimeError(msg);
    }

    /**
     * Report a runtime error using the error reporter for the current thread.
     *
     * @param message the error message to report
     * @see org.mozilla.javascript.ErrorReporter
     */
    public static EvaluatorException reportRuntimeError(String message)
    {
        int[] linep = { 0 };
        String filename = getSourcePositionFromStack(linep);
        return Context.reportRuntimeError(message, filename, linep[0], null, 0);
    }

    /**
     * Initialize the standard objects.
     *
     * Creates instances of the standard objects and their constructors
     * (Object, String, Number, Date, etc.), setting up 'scope' to act
     * as a global object as in ECMA 15.1.<p>
     *
     * This method must be called to initialize a scope before scripts
     * can be evaluated in that scope.<p>
     *
     * This method does not affect the Context it is called upon.
     *
     * @return the initialized scope
     */
    public final ScriptableObject initStandardObjects()
    {
        return initStandardObjects(null, false);
    }

    /**
     * Initialize the standard objects.
     *
     * Creates instances of the standard objects and their constructors
     * (Object, String, Number, Date, etc.), setting up 'scope' to act
     * as a global object as in ECMA 15.1.<p>
     *
     * This method must be called to initialize a scope before scripts
     * can be evaluated in that scope.<p>
     *
     * This method does not affect the Context it is called upon.<p>
     *
     * This form of the method also allows for creating "sealed" standard
     * objects. An object that is sealed cannot have properties added, changed,
     * or removed. This is useful to create a "superglobal" that can be shared
     * among several top-level objects. Note that sealing is not allowed in
     * the current ECMA/ISO language specification, but is likely for
     * the next version.
     *
     * @param scope the scope to initialize, or null, in which case a new
     *        object will be created to serve as the scope
     * @param sealed whether or not to create sealed standard objects that
     *        cannot be modified.
     * @return the initialized scope. The method returns the value of the scope
     *         argument if it is not null or newly allocated scope object.
     * @since 1.4R3
     */
    public ScriptableObject initStandardObjects(ScriptableObject scope,
                                                boolean sealed)
    {
        return ScriptRuntime.initStandardObjects(this, scope, sealed);
    }

    /**
     * Compiles the source in the given string.
     * <p>
     * Returns a script that may later be executed.
     *
     * @param source the source string
     * @param sourceName a string describing the source, such as a filename
     * @param lineno the starting line number for reporting errors
     * @return a script that may later be executed
     * @see org.mozilla.javascript.Script
     */
    public final Script compileString(String source,
                                      String sourceName, int lineno)
    {
        if (lineno < 0) {
            // For compatibility IllegalArgumentException can not be thrown here
            lineno = 0;
        }
        return compileString(source, null, null, sourceName, lineno);
    }

    final Script compileString(String source,
                               Interpreter compiler,
                               ErrorReporter compilationErrorReporter,
                               String sourceName, int lineno)
    {
        try {
            return (Script) compileImpl(null, null, source, sourceName, lineno, false,
                                        compiler, compilationErrorReporter);
        } catch (IOException ex) {
            // Should not happen when dealing with source as string
            throw new RuntimeException();
        }
    }

    final Function compileFunction(Scriptable scope, String source,
                                   Interpreter compiler,
                                   ErrorReporter compilationErrorReporter,
                                   String sourceName, int lineno)
    {
        try {
            return (Function) compileImpl(scope, null, source, sourceName,
                                          lineno, true, compiler, compilationErrorReporter);
        }
        catch (IOException ioe) {
            // Should never happen because we just made the reader
            // from a String
            throw new RuntimeException();
        }
    }

    /**
     * Create a new JavaScript object.
     *
     * Equivalent to evaluating "new Object()".
     * @param scope the scope to search for the constructor and to evaluate
     *              against
     * @return the new object
     */
    public final Scriptable newObject(Scriptable scope)
    {
        return newObject(scope, "Object", ScriptRuntime.emptyArgs);
    }

    /**
     * Creates a new JavaScript object by executing the named constructor.
     *
     * Searches <code>scope</code> for the named constructor, calls it with
     * the given arguments, and returns the result.<p>
     *
     * The code
     * <pre>
     * Object[] args = { "a", "b" };
     * newObject(scope, "Foo", args)</pre>
     * is equivalent to evaluating "new Foo('a', 'b')", assuming that the Foo
     * constructor has been defined in <code>scope</code>.
     *
     * @param scope The scope to search for the constructor and to evaluate
     *              against
     * @param constructorName the name of the constructor to call
     * @param args the array of arguments for the constructor
     * @return the new object
     */
    public final Scriptable newObject(Scriptable scope, String constructorName,
                                      Object[] args)
    {
        scope = Js.getTopLevelScope(scope);
        Function ctor = ScriptRuntime.getExistingCtor(this, scope,
                                                      constructorName);
        if (args == null) { args = ScriptRuntime.emptyArgs; }
        return ctor.construct(this, scope, args);
    }

    /**
     * Get the elements of a JavaScript array.
     * <p>
     * If the object defines a length property convertible to double number,
     * then the number is converted Uint32 value as defined in Ecma 9.6
     * and Java array of that size is allocated.
     * The array is initialized with the values obtained by
     * calling get() on object for each value of i in [0,length-1]. If
     * there is not a defined value for a property the Undefined value
     * is used to initialize the corresponding element in the array. The
     * Java array is then returned.
     * If the object doesn't define a length property or it is not a number,
     * empty array is returned.
     * @param object the JavaScript array or array-like object
     * @return a Java array of objects
     * @since 1.4 release 2
     */
    public final Object[] getElements(Scriptable object)
    {
        return ScriptRuntime.getArrayElements(object);
    }

    /**
     * Convert the value to a JavaScript String value.
     * <p>
     * See ECMA 9.8.
     * <p>
     * @param value a JavaScript value
     * @return the corresponding String value converted using
     *         the ECMA rules
     */
    public static String toString(Object value)
    {
        return ScriptRuntime.toString(value);
    }

    /**
     * Convert the value to an JavaScript object value.
     * <p>
     * Note that a scope must be provided to look up the constructors
     * for Number, Boolean, and String.
     * <p>
     * See ECMA 9.9.
     * <p>
     * Additionally, arbitrary Java objects and classes will be
     * wrapped in a Scriptable object with its Java fields and methods
     * reflected as JavaScript properties of the object.
     *
     * @param value any Java object
     * @param scope global scope containing constructors for Number,
     *              Boolean, and String
     * @return new JavaScript object
     */
    public static Scriptable toObject(Object value, Scriptable scope)
    {
        return ScriptRuntime.toObject(scope, value);
    }

    /**
     * Convenient method to convert java value to its closest representation
     * in JavaScript.
     * <p>
     * If value is an instance of String, Number, Boolean, Function or
     * Scriptable, it is returned as it and will be treated as the corresponding
     * JavaScript type of string, number, boolean, function and object.
     * <p>
     * Note that for Number instances during any arithmetic operation in
     * JavaScript the engine will always use the result of
     * <tt>Number.doubleValue()</tt> resulting in a precision loss if
     * the number can not fit into double.
     * <p>
     * If value is an instance of Character, it will be converted to string of
     * length 1 and its JavaScript type will be string.
     * <p>
     * The rest of values will be wrapped as LiveConnect objects
     * by calling {@link WrapFactory#wrap(Context cx, Scriptable scope,
     * Object obj, Class staticType)} as in:
     * <pre>
     *    Context cx = Context.getCurrentContext();
     *    return cx.getWrapFactory().wrap(cx, scope, value, null);
     * </pre>
     *
     * @param value any Java object
     * @param scope top scope object
     * @return value suitable to pass to any API that takes JavaScript values.
     */
//    public static Object javaToJS(Object value, Scriptable scope)
//    {
//        if (value instanceof String || value instanceof Number
//            || value instanceof Boolean || value instanceof Scriptable)
//        {
//            return value;
//        } else if (value instanceof Character) {
//            return String.valueOf(((Character)value).charValue());
//        } else {
//            Context cx = Context.getContext();
//            return cx.getWrapFactory().wrap(cx, scope, value, null);
//        }
//    }

    /**
     * Convert a JavaScript value into the desired type.
     * Uses the semantics defined with LiveConnect3 and throws an
     * Illegal argument exception if the conversion cannot be performed.
     * @param value the JavaScript value to convert
     * @param desiredType the Java type to convert to. Primitive Java
     *        types are represented using the TYPE fields in the corresponding
     *        wrapper class in java.lang.
     * @return the converted value
     * @throws EvaluatorException if the conversion cannot be performed
     */
//    public static Object jsToJava(Object value, Class desiredType)
//        throws EvaluatorException
//    {
//        return NativeJavaObject.coerceTypeImpl(desiredType, value);
//    }

    /**
     * @deprecated
     * @see #jsToJava(Object, Class)
     * @throws IllegalArgumentException if the conversion cannot be performed.
     *         Note that {@link #jsToJava(Object, Class)} throws
     *         {@link EvaluatorException} instead.
     */
//    public static Object toType(Object value, Class desiredType)
//        throws IllegalArgumentException
//    {
//        try {
//            return jsToJava(value, desiredType);
//        } catch (EvaluatorException ex) {
//            IllegalArgumentException
//                ex2 = new IllegalArgumentException(ex.getMessage());
//            Kit.initCause(ex2, ex);
//            throw ex2;
//        }
//    }

    /**
     * Tell whether source information is being generated.
     * @since 1.3
     */
    public final boolean isGeneratingSource()
    {
        return generatingSource;
    }

    /**
     * Returns the maximum stack depth (in terms of number of call frames) 
     * allowed in a single invocation of interpreter. If the set depth would be
     * exceeded, the interpreter will throw an EvaluatorException in the script.
     * Defaults to Integer.MAX_VALUE. The setting only has effect for 
     * interpreted functions (those compiled with optimization level set to -1).
     * As the interpreter doesn't use the Java stack but rather manages its own
     * stack in the heap memory, a runaway recursion in interpreted code would 
     * eventually consume all available memory and cause OutOfMemoryError 
     * instead of a StackOverflowError limited to only a single thread. This
     * setting helps prevent such situations.
     *  
     * @return The current maximum interpreter stack depth.
     */
    public final int getMaximumInterpreterStackDepth()
    {
        return maximumInterpreterStackDepth;
    }

    /**
     * Return the current WrapFactory, or null if none is defined.
     * @see WrapFactory
     * @since 1.5 Release 4
     */
//    public final WrapFactory getWrapFactory()
//    {
//        if (wrapFactory == null) {
//            wrapFactory = new WrapFactory();
//        }
//        return wrapFactory;
//    }

    /**
     * Controls certain aspects of script semantics.
     * Should be overwritten to alter default behavior.
     * <p>
     * The default implementation calls
     * {@link ContextFactory#hasFeature(Context cx, int featureIndex)}
     * that allows to customize Context behavior without introducing
     * Context subclasses.  {@link ContextFactory} documentation gives
     * an example of hasFeature implementation.
     *
     * @param featureIndex feature index to check
     * @return true if the <code>featureIndex</code> feature is turned on
     * @see #FEATURE_NON_ECMA_GET_YEAR
     * @see #FEATURE_MEMBER_EXPR_AS_FUNCTION_NAME
     * @see #FEATURE_RESERVED_KEYWORD_AS_IDENTIFIER
     * @see #FEATURE_TO_STRING_AS_SOURCE
     * @see #FEATURE_PARENT_PROTO_PROPRTIES
     * @see #FEATURE_DYNAMIC_SCOPE
     * @see #FEATURE_STRICT_VARS
     * @see #FEATURE_STRICT_EVAL
     * @see #FEATURE_LOCATION_INFORMATION_IN_ERROR
     * @see #FEATURE_STRICT_MODE
     * @see #FEATURE_WARNING_AS_ERROR
     */
    public boolean hasFeature(int featureIndex)
    {
        ContextFactory f = getFactory();
        return f.hasFeature(this, featureIndex);
    }
	
    /**
     * Allow application to monitor counter of executed script instructions
     * in Context subclasses.
     * Run-time calls this when instruction counting is enabled and the counter
     * reaches limit set by <code>setInstructionObserverThreshold()</code>.
     * The method is useful to observe long running scripts and if necessary
     * to terminate them.
     * <p>
     * The instruction counting support is available only for interpreted
     * scripts generated when the optimization level is set to -1.
     * <p>
     * The default implementation calls
     * {@link ContextFactory#observeInstructionCount(Context cx,
     *                                               int instructionCount)}
     * that allows to customize Context behavior without introducing
     * Context subclasses.
     *
     * @param instructionCount amount of script instruction executed since
     * last call to <code>observeInstructionCount</code>
     * @throws Error to terminate the script
     * @see #setOptimizationLevel(int)
     */
    protected void observeInstructionCount(int instructionCount)
    {
        ContextFactory f = getFactory();
        f.observeInstructionCount(this, instructionCount);
    }

    /**
     * Create class loader for generated classes.
     * The method calls {@link ContextFactory#createClassLoader(ClassLoader)}
     * using the result of {@link #getFactory()}.
     */
//    public GeneratedClassLoader createClassLoader(ClassLoader parent)
//    {
//        ContextFactory f = getFactory();
//        return f.createClassLoader(parent);
//    }

//    public final ClassLoader getApplicationClassLoader()
//    {
//        if (applicationClassLoader == null) {
//            ContextFactory f = getFactory();
//            ClassLoader loader = f.getApplicationClassLoader();
//            if (loader == null) {
//                ClassLoader threadLoader
//                    = VMBridge.instance.getCurrentThreadClassLoader();
//                if (threadLoader != null
//                    && Kit.testIfCanLoadRhinoClasses(threadLoader))
//                {
//                    // Thread.getContextClassLoader is not cached since
//                    // its caching prevents it from GC which may lead to
//                    // a memory leak and hides updates to
//                    // Thread.getContextClassLoader
//                    return threadLoader;
//                }
//                // Thread.getContextClassLoader can not load Rhino classes,
//                // try to use the loader of ContextFactory or Context
//                // subclasses.
//                Class fClass = f.getClass();
//                if (fClass != ScriptRuntime.ContextFactoryClass) {
//                    loader = fClass.getClassLoader();
//                } else {
//                    loader = getClass().getClassLoader();
//                }
//            }
//            applicationClassLoader = loader;
//        }
//        return applicationClassLoader;
//    }

//    public final void setApplicationClassLoader(ClassLoader loader)
//    {
//        if (sealed) onSealedMutation();
//        if (loader == null) {
//            // restore default behaviour
//            applicationClassLoader = null;
//            return;
//        }
//        if (!Kit.testIfCanLoadRhinoClasses(loader)) {
//            throw new IllegalArgumentException(
//                "Loader can not resolve Rhino classes");
//        }
//        applicationClassLoader = loader;
//    }

    /********** end of API **********/

    /**
     * Internal method that reports an error for missing calls to
     * enter().
     */
    static Context getContext()
    {
        Context cx = getCurrentContext();
        if (cx == null) {
            throw new RuntimeException(
                "No Context associated with current Thread");
        }
        return cx;
    }

    private Object compileImpl(Scriptable scope,
                               Reader sourceReader, String sourceString,
                               String sourceName, int lineno, boolean returnFunction,
                               Interpreter compiler, ErrorReporter compilationErrorReporter)
        throws IOException
    {
        // One of sourceReader or sourceString has to be null
        if (!(sourceReader == null ^ sourceString == null)) Kit.codeBug();
        // scope should be given if and only if compiling function
        if (!(scope == null ^ returnFunction)) Kit.codeBug();

        CompilerEnvirons compilerEnv = new CompilerEnvirons();
        compilerEnv.initFromContext(this);
        if (compilationErrorReporter == null) {
            compilationErrorReporter = compilerEnv.getErrorReporter();
        }

        Parser p = new Parser(compilerEnv, compilationErrorReporter);
        if (returnFunction) {
            p.calledByCompileFunction = true;
        }
        ScriptOrFnNode tree;
        if (sourceString != null) {
            tree = p.parse(sourceString, sourceName, lineno);
        } else {
            tree = p.parse(sourceReader, sourceName, lineno);
        }
        if (returnFunction) {
            if (!(tree.getFunctionCount() == 1
                  && tree.getFirstChild() != null
                  && tree.getFirstChild().getType() == Token.FUNCTION))
            {
                // XXX: the check just look for the first child
                // and allows for more nodes after it for compatibility
                // with sources like function() {};;;
                throw new IllegalArgumentException(
                    "compileFunction only accepts source with single JS function: "+sourceString);
            }
        }

        if (compiler == null) {
            compiler = createCompiler();
        }

        String encodedSource = p.getEncodedSource();

        Object bytecode = compiler.compile(compilerEnv,
                                           tree, encodedSource,
                                           returnFunction);

        Object result;
        if (returnFunction) {
            result = compiler.createFunctionObject(this, scope, bytecode);
        } else {
            result = compiler.createScriptObject(bytecode);
        }

        return result;
    }

    private static Class codegenClass = Kit.classOrNull(
                             "org.mozilla.javascript.optimizer.Codegen");

    private Interpreter createCompiler()
    {
        return new Interpreter();
    }

    static String getSourcePositionFromStack(int[] linep)
    {
        Context cx = getCurrentContext();
        if (cx == null)
            return null;
        if (cx.lastInterpreterFrame != null) {
            return Interpreter.getSourcePositionFromStack(cx, linep);
        }
        /**
         * A bit of a hack, but the only way to get filename and line
         * number from an enclosing frame.
         */
//        CharArrayWriter writer = new CharArrayWriter();
//        RuntimeException re = new RuntimeException();
//        re.printStackTrace(new PrintWriter(writer));
//        String s = writer.toString();
//        int open = -1;
//        int close = -1;
//        int colon = -1;
//        for (int i=0; i < s.length(); i++) {
//            char c = s.charAt(i);
//            if (c == ':')
//                colon = i;
//            else if (c == '(')
//                open = i;
//            else if (c == ')')
//                close = i;
//            else if (c == '\n' && open != -1 && close != -1 && colon != -1 &&
//                     open < colon && colon < close)
//            {
//                String fileStr = s.substring(open + 1, colon);
//                if (!fileStr.endsWith(".java")) {
//                    String lineStr = s.substring(colon + 1, close);
//                    try {
//                        linep[0] = Integer.parseInt(lineStr);
//                        if (linep[0] < 0) {
//                            linep[0] = 0;
//                        }
//                        return fileStr;
//                    }
//                    catch (NumberFormatException e) {
//                        // fall through
//                    }
//                }
//                open = close = colon = -1;
//            }
//        }

        return "sf: no filename available";
    }

    RegExpProxy getRegExpProxy()
    {
        if (regExpProxy == null) {
            Class cl = Kit.classOrNull(
                          "org.mozilla.javascript.regexp.RegExpImpl");
            if (cl != null) {
                regExpProxy = (RegExpProxy)Kit.newInstanceOrNull(cl);
            }
        }
        return regExpProxy;
    }

    final boolean isVersionECMA1()
    {
        return version == VERSION_DEFAULT || version >= VERSION_1_3;
    }

	public void setNextWidget(int id) {
		runWidgetId = id;
	}

	public void checkRunWidget() {
		if (this.runWidgetId != -1) {
			this.currentWidgetId = this.runWidgetId;
			this.runWidgetId = -1;
			EventManager.fireEvent("js.runwidget", this, new Integer( this.currentWidgetId ) );
		}
	}

	private ContextFactory factory;
    private boolean sealed;

    Scriptable topCallScope;
    NativeCall currentActivationCall;

    // for Objects, Arrays to tag themselves as being printed out,
    // so they don't print themselves out recursively.
    // Use ObjToIntMap instead of java.util.HashSet for JDK 1.1 compatibility
    ObjToIntMap iterating;

    int version;

    private ErrorReporter errorReporter;
    RegExpProxy regExpProxy;
    private boolean generatingSource=true;
    boolean compileFunctionsWithDynamicScopeFlag;
    boolean useDynamicScope;
    private int maximumInterpreterStackDepth;
//    private WrapFactory wrapFactory;
    private int enterCount;
//    private Object propertyListeners;
    private Hashtable hashtable;
//    private ClassLoader applicationClassLoader;
    private boolean creationEventWasSent;

    /**
     * This is the list of names of objects forcing the creation of
     * function activation records.
     */
    Hashtable activationNames;

    // For the interpreter to store the last frame for error reports etc.
    Object lastInterpreterFrame;

    // For the interpreter to store information about previous invocations
    // interpreter invocations
    ObjArray previousInterpreterInvocations;

    // For instruction counting (interpreter only)
    int instructionCount;
    int instructionThreshold;

    // It can be used to return the second index-like result from function
    int scratchIndex;

    // It can be used to return the second uint32 result from function
    long scratchUint32;

    // It can be used to return the second Scriptable result from function
    Scriptable scratchScriptable;
    
    // ID of the currentWidget
    int currentWidgetId;

    // ID of the nextWidget to run
    int runWidgetId = -1;

	private boolean optionOnErrorThrowException = false;
	private boolean optionOnErrorFireEvent = true;
	
	public void setCurrentWidgetId(int currentWidgetId) {
		this.currentWidgetId = currentWidgetId;
	}
	
	public void setOptionOnErrorThrowExeption(boolean optionOnErrorThrowException) {
		this.optionOnErrorThrowException = optionOnErrorThrowException;
	}

	public boolean isOptionOnErrorThrowException() {
		return this.optionOnErrorThrowException;
	}

	public void setOptionOnErrorFireEvent(boolean optionOnErrorFireEvent) {
		this.optionOnErrorFireEvent = optionOnErrorFireEvent;
	}

	public boolean isOptionOnErrorFireEvent() {
		return this.optionOnErrorFireEvent;
	}
}
