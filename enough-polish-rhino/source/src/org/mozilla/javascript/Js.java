package org.mozilla.javascript;

import org.mozilla.javascript.ScriptableObject.Slot;

public class Js {

	public static Object getDefaultValue(Scriptable object, Class typeHint)
	    {
	        Context cx = null;
	        for (int i=0; i < 2; i++) {
	            boolean tryToString;
	            if (typeHint == ScriptRuntime.StringClass) {
	                tryToString = (i == 0);
	            } else {
	                tryToString = (i == 1);
	            }
	
	            String methodName;
	            Object[] args;
	            if (tryToString) {
	                methodName = "toString";
	                args = ScriptRuntime.emptyArgs;
	            } else {
	                methodName = "valueOf";
	                args = new Object[1];
	                String hint;
	                if (typeHint == null) {
	                    hint = "undefined";
	                } else if (typeHint == ScriptRuntime.StringClass) {
	                    hint = "string";
	                } else if (typeHint == ScriptRuntime.ScriptableClass) {
	                    hint = "object";
	                } else if (typeHint == ScriptRuntime.FunctionClass) {
	                    hint = "function";
	                } else if (typeHint == ScriptRuntime.BooleanClass
	//                           || typeHint == Boolean.TYPE
	                           )
	                {
	                    hint = "boolean";
	                } else if (typeHint == ScriptRuntime.NumberClass ||
	                         typeHint == ScriptRuntime.ByteClass ||
	//                       typeHint == Byte.TYPE ||
	                         typeHint == ScriptRuntime.ShortClass ||
	//                         typeHint == Short.TYPE ||
	                         typeHint == ScriptRuntime.IntegerClass ||
	//                         typeHint == Integer.TYPE ||
	                         typeHint == ScriptRuntime.FloatClass ||
	//                         typeHint == Float.TYPE ||
	                         typeHint == ScriptRuntime.DoubleClass 
	//                        || typeHint == Double.TYPE
	                         )
	                {
	                    hint = "number";
	                } else {
	                    throw Context.reportRuntimeError1(
	                        "msg.invalid.type", typeHint.toString());
	                }
	                args[0] = hint;
	            }
	            Object v = getProperty(object, methodName);
	            if (!(v instanceof Function))
	                continue;
	            Function fun = (Function) v;
	            if (cx == null)
	                cx = Context.getContext();
	            v = fun.call(cx, fun.getParentScope(), object, args);
	            if (v != null) {
	                if (!(v instanceof Scriptable)) {
	                    return v;
	                }
	                if (typeHint == ScriptRuntime.ScriptableClass
	                    || typeHint == ScriptRuntime.FunctionClass)
	                {
	                    return v;
	                }
	                if (tryToString && v instanceof Wrapper) {
	                    // Let a wrapped java.lang.String pass for a primitive
	                    // string.
	                    Object u = ((Wrapper)v).unwrap();
	                    if (u instanceof String)
	                        return u;
	                }
	            }
	        }
	        // fall through to error
	        String arg = (typeHint == null) ? "undefined" : typeHint.getName();
	        throw ScriptRuntime.typeError1("msg.default.value", arg);
	    }

	/**
	 * Utility method to add properties to arbitrary Scriptable object. If
	 * destination is instance of ScriptableObject, calls defineProperty there,
	 * otherwise calls put in destination ignoring attributes
	 */
	public static void defineProperty(Scriptable destination,
	                                  String propertyName, Object value,
	                                  int attributes)
	{
	    if (!(destination instanceof ScriptableObject)) {
	        destination.put(propertyName, destination, value);
	        return;
	    }
	    ScriptableObject so = (ScriptableObject)destination;
	    so.defineProperty(propertyName, value, attributes);
	}

	/**
	 * Utility method to add properties to arbitrary Scriptable object. If
	 * destination is instance of ScriptableObject, calls defineProperty there,
	 * otherwise calls put in destination ignoring attributes
	 */
	public static void defineConstProperty(Scriptable destination,
	                                       String propertyName)
	{
	    if (destination instanceof ConstProperties) {
	        ConstProperties cp = (ConstProperties)destination;
	        cp.defineConst(propertyName, destination);
	    } else
	        defineProperty(destination, propertyName, Undefined.instance, ScriptableObject.CONST);
	}

	/**
	 * Get the Object.prototype property.
	 * See ECMA 15.2.4.
	 */
	public static Scriptable getObjectPrototype(Scriptable scope) {
	    return getClassPrototype(scope, "Object");
	}

	/**
	 * Get the Function.prototype property.
	 * See ECMA 15.3.4.
	 */
	public static Scriptable getFunctionPrototype(Scriptable scope) {
	    return getClassPrototype(scope, "Function");
	}

	/**
	 * Get the prototype for the named class.
	 *
	 * For example, <code>getClassPrototype(s, "Date")</code> will first
	 * walk up the parent chain to find the outermost scope, then will
	 * search that scope for the Date constructor, and then will
	 * return Date.prototype. If any of the lookups fail, or
	 * the prototype is not a JavaScript object, then null will
	 * be returned.
	 *
	 * @param scope an object in the scope chain
	 * @param className the name of the constructor
	 * @return the prototype for the named class, or null if it
	 *         cannot be found.
	 */
	public static Scriptable getClassPrototype(Scriptable scope,
	                                           String className)
	{
	    scope = getTopLevelScope(scope);
	    Object ctor = getProperty(scope, className);
	    Object proto;
	    if (ctor instanceof BaseFunction) {
	        proto = ((BaseFunction)ctor).getPrototypeProperty();
	    } else if (ctor instanceof Scriptable) {
	        Scriptable ctorObj = (Scriptable)ctor;
	        proto = ctorObj.get("prototype", ctorObj);
	    } else {
	        return null;
	    }
	    if (proto instanceof Scriptable) {
	        return (Scriptable)proto;
	    }
	    return null;
	}

	/**
	 * Get the global scope.
	 *
	 * <p>Walks the parent scope chain to find an object with a null
	 * parent scope (the global object).
	 *
	 * @param obj a JavaScript object
	 * @return the corresponding global scope
	 */
	public static Scriptable getTopLevelScope(Scriptable obj)
	{
	    for (;;) {
	        Scriptable parent = obj.getParentScope();
	        if (parent == null) {
	            return obj;
	        }
	        obj = parent;
	    }
	}

	/**
	 * Gets a named property from an object or any object in its prototype chain.
	 * <p>
	 * Searches the prototype chain for a property named <code>name</code>.
	 * <p>
	 * @param obj a JavaScript object
	 * @param name a property name
	 * @return the value of a property with name <code>name</code> found in
	 *         <code>obj</code> or any object in its prototype chain, or
	 *         <code>Scriptable.NOT_FOUND</code> if not found
	 * @since 1.5R2
	 */
	public static Object getProperty(Scriptable obj, String name)
	{
	    Scriptable start = obj;
	    Object result;
	    do {
	        result = obj.get(name, start);
	        if (result != Scriptable.NOT_FOUND)
	            break;
	        obj = obj.getPrototype();
	    } while (obj != null);
	    return result;
	}

	/**
	 * Gets an indexed property from an object or any object in its prototype chain.
	 * <p>
	 * Searches the prototype chain for a property with integral index
	 * <code>index</code>. Note that if you wish to look for properties with numerical
	 * but non-integral indicies, you should use getProperty(Scriptable,String) with
	 * the string value of the index.
	 * <p>
	 * @param obj a JavaScript object
	 * @param index an integral index
	 * @return the value of a property with index <code>index</code> found in
	 *         <code>obj</code> or any object in its prototype chain, or
	 *         <code>Scriptable.NOT_FOUND</code> if not found
	 * @since 1.5R2
	 */
	public static Object getProperty(Scriptable obj, int index)
	{
	    Scriptable start = obj;
	    Object result;
	    do {
	        result = obj.get(index, start);
	        if (result != Scriptable.NOT_FOUND)
	            break;
	        obj = obj.getPrototype();
	    } while (obj != null);
	    return result;
	}

	/**
	 * Returns whether a named property is defined in an object or any object
	 * in its prototype chain.
	 * <p>
	 * Searches the prototype chain for a property named <code>name</code>.
	 * <p>
	 * @param obj a JavaScript object
	 * @param name a property name
	 * @return the true if property was found
	 * @since 1.5R2
	 */
	public static boolean hasProperty(Scriptable obj, String name)
	{
	    return null != getBase(obj, name);
	}

	/**
	 * If hasProperty(obj, name) would return true, then if the property that
	 * was found is compatible with the new property, this method just returns.
	 * If the property is not compatible, then an exception is thrown.
	 *
	 * A property redefinition is incompatible if the first definition was a
	 * const declaration or if this one is.  They are compatible only if neither
	 * was const.
	 */
	public static void redefineProperty(Scriptable obj, String name,
	                                    boolean isConst)
	{
	    Scriptable baseOld = getBase(obj, name);
	    if (baseOld == null)
	        return;
	    if (baseOld instanceof ConstProperties) {
	        ConstProperties cp = (ConstProperties)baseOld;
	
	        if (cp.isConst(name))
	            throw Context.reportRuntimeError1("msg.const.redecl", name);
	    }
	    if (isConst)
	        throw Context.reportRuntimeError1("msg.var.redecl", name);
	}

	/**
	 * Returns whether an indexed property is defined in an object or any object
	 * in its prototype chain.
	 * <p>
	 * Searches the prototype chain for a property with index <code>index</code>.
	 * <p>
	 * @param obj a JavaScript object
	 * @param index a property index
	 * @return the true if property was found
	 * @since 1.5R2
	 */
	public static boolean hasProperty(Scriptable obj, int index)
	{
	    return null != getBase(obj, index);
	}

	/**
	 * Puts a named property in an object or in an object in its prototype chain.
	 * <p>
	 * Searches for the named property in the prototype chain. If it is found,
	 * the value of the property in <code>obj</code> is changed through a call
	 * to {@link Scriptable#put(String, Scriptable, Object)} on the
	 * prototype passing <code>obj</code> as the <code>start</code> argument.
	 * This allows the prototype to veto the property setting in case the
	 * prototype defines the property with [[ReadOnly]] attribute. If the
	 * property is not found, it is added in <code>obj</code>.
	 * @param obj a JavaScript object
	 * @param name a property name
	 * @param value any JavaScript value accepted by Scriptable.put
	 * @since 1.5R2
	 */
	public static void putProperty(Scriptable obj, String name, Object value)
	{
	    Scriptable baseOld = getBase(obj, name);
	    if (baseOld == null)
	        baseOld = obj;
	    baseOld.put(name, obj, value);
	}

	/**
	 * Puts a named property in an object or in an object in its prototype chain.
	 * <p>
	 * Searches for the named property in the prototype chain. If it is found,
	 * the value of the property in <code>obj</code> is changed through a call
	 * to {@link Scriptable#put(String, Scriptable, Object)} on the
	 * prototype passing <code>obj</code> as the <code>start</code> argument.
	 * This allows the prototype to veto the property setting in case the
	 * prototype defines the property with [[ReadOnly]] attribute. If the
	 * property is not found, it is added in <code>obj</code>.
	 * @param obj a JavaScript object
	 * @param name a property name
	 * @param value any JavaScript value accepted by Scriptable.put
	 * @since 1.5R2
	 */
	public static void putConstProperty(Scriptable obj, String name, Object value)
	{
	    Scriptable baseOld = getBase(obj, name);
	    if (baseOld == null)
	        baseOld = obj;
	    if (baseOld instanceof ConstProperties)
	        ((ConstProperties)baseOld).putConst(name, obj, value);
	}

	/**
	 * Puts an indexed property in an object or in an object in its prototype chain.
	 * <p>
	 * Searches for the indexed property in the prototype chain. If it is found,
	 * the value of the property in <code>obj</code> is changed through a call
	 * to {@link Scriptable#put(int, Scriptable, Object)} on the prototype
	 * passing <code>obj</code> as the <code>start</code> argument. This allows
	 * the prototype to veto the property setting in case the prototype defines
	 * the property with [[ReadOnly]] attribute. If the property is not found, 
	 * it is added in <code>obj</code>.
	 * @param obj a JavaScript object
	 * @param index a property index
	 * @param value any JavaScript value accepted by Scriptable.put
	 * @since 1.5R2
	 */
	public static void putProperty(Scriptable obj, int index, Object value)
	{
	    Scriptable baseOld = getBase(obj, index);
	    if (baseOld == null)
	        baseOld = obj;
	    baseOld.put(index, obj, value);
	}

	/**
	 * Removes the property from an object or its prototype chain.
	 * <p>
	 * Searches for a property with <code>name</code> in obj or
	 * its prototype chain. If it is found, the object's delete
	 * method is called.
	 * @param obj a JavaScript object
	 * @param name a property name
	 * @return true if the property doesn't exist or was successfully removed
	 * @since 1.5R2
	 */
	public static boolean deleteProperty(Scriptable obj, String name)
	{
	    Scriptable baseOld = getBase(obj, name);
	    if (baseOld == null)
	        return true;
	    baseOld.delete(name);
	    return !baseOld.has(name, obj);
	}

	/**
	 * Removes the property from an object or its prototype chain.
	 * <p>
	 * Searches for a property with <code>index</code> in obj or
	 * its prototype chain. If it is found, the object's delete
	 * method is called.
	 * @param obj a JavaScript object
	 * @param index a property index
	 * @return true if the property doesn't exist or was successfully removed
	 * @since 1.5R2
	 */
	public static boolean deleteProperty(Scriptable obj, int index)
	{
	    Scriptable baseOld = getBase(obj, index);
	    if (baseOld == null)
	        return true;
	    baseOld.delete(index);
	    return !baseOld.has(index, obj);
	}

	static void checkValidAttributes(int attributes)
	{
	    final int mask = ScriptableObject.READONLY | ScriptableObject.DONTENUM | ScriptableObject.PERMANENT | ScriptableObject.UNINITIALIZED_CONST;
	    if ((attributes & ~mask) != 0) {
	        throw new IllegalArgumentException(String.valueOf(attributes));
	    }
	}

	/**
	 * Add slot with keys that are known to absent from the table.
	 * This is an optimization to use when inserting into empty table,
	 * after table growth or during deserialization.
	 */
	static void addKnownAbsentSlot(ScriptableObject.Slot[] slots, ScriptableObject.Slot slot, int insertPos)
	{
	    if (slots[insertPos] == null) {
	        slots[insertPos] = slot;
	    } else {
	        ScriptableObject.Slot prev = slots[insertPos];
	        while (prev.next != null) {
	            prev = prev.next;
	        }
	        prev.next = slot;
	    }
	}

	static Scriptable getBase(Scriptable obj, int index)
	{
	    do {
	        if (obj.has(index, obj))
	            break;
	        obj = obj.getPrototype();
	    } while(obj != null);
	    return obj;
	}

	static Scriptable getBase(Scriptable obj, String name)
	{
	    do {
	        if (obj.has(name, obj))
	            break;
	        obj = obj.getPrototype();
	    } while(obj != null);
	    return obj;
	}

	static int getSlotIndex(int tableSize, int indexOrHash)
	{
	    return (indexOrHash & 0x7fffffff) % tableSize;
	}

	// Must be inside synchronized (this)
    public static void copyTable(Slot[] slots, Slot[] newSlots, int count)
    {
        if (count == 0) throw Kit.codeBug();

        int tableSize = newSlots.length;
        int i = slots.length;
        for (;;) {
            --i;
            Slot slot = slots[i];
            while (slot != null) {
                int insertPos = Js.getSlotIndex(tableSize, slot.indexOrHash);
                Slot next = slot.next;
                Js.addKnownAbsentSlot(newSlots, slot, insertPos);
                slot.next = null;
                slot = next;
                if (--count == 0)
                    return;
            }
        }
    }
}
