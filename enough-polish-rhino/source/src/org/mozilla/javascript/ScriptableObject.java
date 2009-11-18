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
 *   Norris Boyd
 *   Igor Bukanov
 *   Bob Jervis
 *   Roger Lawrence
 *   Steve Weiss
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import de.enough.polish.io.Externalizable;
import de.enough.polish.io.Serializer;

/**
 * This is the default implementation of the Scriptable interface. This
 * class provides convenient default behavior that makes it easier to
 * define host objects.
 * <p>
 * Various properties and methods of JavaScript objects can be conveniently
 * defined using methods of ScriptableObject.
 * <p>
 * Classes extending ScriptableObject must define the getClassName method.
 *
 * @see org.mozilla.javascript.Scriptable
 * @author Norris Boyd
 */

public abstract class ScriptableObject implements Scriptable, Externalizable, ConstProperties
{

    /**
     * The empty property attribute.
     *
     * Used by getAttributes() and setAttributes().
     *
     * @see org.mozilla.javascript.ScriptableObject#getAttributes(String)
     * @see org.mozilla.javascript.ScriptableObject#setAttributes(String, int)
     */
    public static final int EMPTY =     0x00;

    /**
     * Property attribute indicating assignment to this property is ignored.
     *
     * @see org.mozilla.javascript.ScriptableObject
     *      #put(String, Scriptable, Object)
     * @see org.mozilla.javascript.ScriptableObject#getAttributes(String)
     * @see org.mozilla.javascript.ScriptableObject#setAttributes(String, int)
     */
    public static final int READONLY =  0x01;

    /**
     * Property attribute indicating property is not enumerated.
     *
     * Only enumerated properties will be returned by getIds().
     *
     * @see org.mozilla.javascript.ScriptableObject#getIds()
     * @see org.mozilla.javascript.ScriptableObject#getAttributes(String)
     * @see org.mozilla.javascript.ScriptableObject#setAttributes(String, int)
     */
    public static final int DONTENUM =  0x02;

    /**
     * Property attribute indicating property cannot be deleted.
     *
     * @see org.mozilla.javascript.ScriptableObject#delete(String)
     * @see org.mozilla.javascript.ScriptableObject#getAttributes(String)
     * @see org.mozilla.javascript.ScriptableObject#setAttributes(String, int)
     */
    public static final int PERMANENT = 0x04;

    /**
     * Property attribute indicating that this is a const property that has not
     * been assigned yet.  The first 'const' assignment to the property will
     * clear this bit.
     */
    public static final int UNINITIALIZED_CONST = 0x08;

    public static final int CONST = PERMANENT|READONLY|UNINITIALIZED_CONST;
    /**
     * The prototype of this object.
     */
    private Scriptable prototypeObject;

    /**
     * The parent scope of this object.
     */
    private Scriptable parentScopeObject;

    private static final Slot REMOVED = new Slot(null, 0, READONLY);

    static {
        REMOVED.wasDeleted = 1;
    }

    private transient Slot[] slots;
    // If count >= 0, it gives number of keys or if count < 0,
    // it indicates sealed object where ~count gives number of keys
    private int count;

    // cache; may be removed for smaller memory footprint
    private transient Slot lastAccess = REMOVED;

    // associated values are not serialized
    private transient volatile Hashtable associatedValues;

    private static final int SLOT_QUERY = 1;
    private static final int SLOT_MODIFY = 2;
    private static final int SLOT_REMOVE = 3;
    private static final int SLOT_MODIFY_GETTER_SETTER = 4;
    private static final int SLOT_MODIFY_CONST = 5;

    public static class Slot implements Externalizable
    {
        //static final long serialVersionUID = -3539051633409902634L;

        String name; // This can change due to caching
        int indexOrHash;
        private volatile short attributes;
        transient volatile byte wasDeleted;
        volatile Object value;
        transient volatile Slot next;

        public Slot()
        {
        }
        Slot(String name, int indexOrHash, int attributes)
        {
            this.name = name;
            this.indexOrHash = indexOrHash;
            this.attributes = (short)attributes;
        }

        final int getAttributes()
        {
            return attributes;
        }

        final synchronized void setAttributes(int value)
        {
            Js.checkValidAttributes(value);
            attributes = (short)value;
        }

        final void checkNotReadonly()
        {
            if ((attributes & READONLY) != 0) {
                String str = (name != null ? name
                              : Integer.toString(indexOrHash));
                throw Context.reportRuntimeError1("msg.modify.readonly", str);
            }
        }

		
		public void read(DataInputStream in) throws IOException {
			attributes = in.readShort();
			indexOrHash = in.readInt();
			name = (String) Serializer.deserialize(in);
			value = Serializer.deserialize(in);
            if (name != null) {
                indexOrHash = name.hashCode();
            }
		}

		
		public void write(DataOutputStream out) throws IOException {
			out.writeShort(attributes);
			out.writeInt(indexOrHash);
			Serializer.serialize(name, out);
			Serializer.serialize(value, out);
			
		}

    }

    private static final class GetterSlot extends Slot
    {
        //static final long serialVersionUID = -4900574849788797588L;

        Object getter;
        Object setter;

        GetterSlot(String name, int indexOrHash, int attributes)
        {
            super(name, indexOrHash, attributes);
        }
    }

    public ScriptableObject()
    {
    }

    public ScriptableObject(Scriptable scope, Scriptable prototype)
    {
        if (scope == null)
            throw new IllegalArgumentException();

        parentScopeObject = scope;
        prototypeObject = prototype;
    }

    /**
     * Return the name of the class.
     *
     * This is typically the same name as the constructor.
     * Classes extending ScriptableObject must implement this abstract
     * method.
     */
    public abstract String getClassName();

    /**
     * Returns true if the named property is defined.
     *
     * @param name the name of the property
     * @param start the object in which the lookup began
     * @return true if and only if the property was found in the object
     */
    public boolean has(String name, Scriptable start)
    {
        return null != getSlot(name, 0, SLOT_QUERY);
    }

    /**
     * Returns true if the property index is defined.
     *
     * @param index the numeric index for the property
     * @param start the object in which the lookup began
     * @return true if and only if the property was found in the object
     */
    public boolean has(int index, Scriptable start)
    {
        return null != getSlot(null, index, SLOT_QUERY);
    }

    /**
     * Returns the value of the named property or NOT_FOUND.
     *
     * If the property was created using defineProperty, the
     * appropriate getter method is called.
     *
     * @param name the name of the property
     * @param start the object in which the lookup began
     * @return the value of the property (may be null), or NOT_FOUND
     */
    public Object get(String name, Scriptable start)
    {
        return getImpl(name, 0, start);
    }

    /**
     * Returns the value of the indexed property or NOT_FOUND.
     *
     * @param index the numeric index for the property
     * @param start the object in which the lookup began
     * @return the value of the property (may be null), or NOT_FOUND
     */
    public Object get(int index, Scriptable start)
    {
        return getImpl(null, index, start);
    }

    /**
     * Sets the value of the named property, creating it if need be.
     *
     * If the property was created using defineProperty, the
     * appropriate setter method is called. <p>
     *
     * If the property's attributes include READONLY, no action is
     * taken.
     * This method will actually set the property in the start
     * object.
     *
     * @param name the name of the property
     * @param start the object whose property is being set
     * @param value value to set the property to
     */
    public void put(String name, Scriptable start, Object value)
    {
        if (putImpl(name, 0, start, value, EMPTY))
            return;

        if (start == this) throw Kit.codeBug();
        start.put(name, start, value);
    }

    /**
     * Sets the value of the indexed property, creating it if need be.
     *
     * @param index the numeric index for the property
     * @param start the object whose property is being set
     * @param value value to set the property to
     */
    public void put(int index, Scriptable start, Object value)
    {
        if (putImpl(null, index, start, value, EMPTY))
            return;

        if (start == this) throw Kit.codeBug();
        start.put(index, start, value);
    }

    /**
     * Removes a named property from the object.
     *
     * If the property is not found, or it has the PERMANENT attribute,
     * no action is taken.
     *
     * @param name the name of the property
     */
    public void delete(String name)
    {
        checkNotSealed(name, 0);
        accessSlot(name, 0, SLOT_REMOVE);
    }

    /**
     * Removes the indexed property from the object.
     *
     * If the property is not found, or it has the PERMANENT attribute,
     * no action is taken.
     *
     * @param index the numeric index for the property
     */
    public void delete(int index)
    {
        checkNotSealed(null, index);
        accessSlot(null, index, SLOT_REMOVE);
    }

    /**
     * Sets the value of the named const property, creating it if need be.
     *
     * If the property was created using defineProperty, the
     * appropriate setter method is called. <p>
     *
     * If the property's attributes include READONLY, no action is
     * taken.
     * This method will actually set the property in the start
     * object.
     *
     * @param name the name of the property
     * @param start the object whose property is being set
     * @param value value to set the property to
     */
    public void putConst(String name, Scriptable start, Object value)
    {
        if (putImpl(name, 0, start, value, READONLY))
            return;

        if (start == this) throw Kit.codeBug();
        if (start instanceof ConstProperties)
            ((ConstProperties)start).putConst(name, start, value);
        else
            start.put(name, start, value);
    }

    public void defineConst(String name, Scriptable start)
    {
        if (putImpl(name, 0, start, Undefined.instance, UNINITIALIZED_CONST))
            return;

        if (start == this) throw Kit.codeBug();
        if (start instanceof ConstProperties)
            ((ConstProperties)start).defineConst(name, start);
    }
    /**
     * Returns true if the named property is defined as a const on this object.
     * @param name
     * @return true if the named property is defined as a const, false
     * otherwise.
     */
    public boolean isConst(String name)
    {
        Slot slot = getSlot(name, 0, SLOT_QUERY);
        if (slot == null) {
            return false;
        }
        return (slot.getAttributes() & (PERMANENT|READONLY)) ==
                                       (PERMANENT|READONLY);

    }

    /**
     * Get the attributes of a named property.
     *
     * The property is specified by <code>name</code>
     * as defined for <code>has</code>.<p>
     *
     * @param name the identifier for the property
     * @return the bitset of attributes
     * @exception EvaluatorException if the named property is not found
     * @see org.mozilla.javascript.ScriptableObject#has(String, Scriptable)
     * @see org.mozilla.javascript.ScriptableObject#READONLY
     * @see org.mozilla.javascript.ScriptableObject#DONTENUM
     * @see org.mozilla.javascript.ScriptableObject#PERMANENT
     * @see org.mozilla.javascript.ScriptableObject#EMPTY
     */
    public int getAttributes(String name)
    {
        return findAttributeSlot(name, 0, SLOT_QUERY).getAttributes();
    }

    /**
     * Get the attributes of an indexed property.
     *
     * @param index the numeric index for the property
     * @exception EvaluatorException if the named property is not found
     *            is not found
     * @return the bitset of attributes
     * @see org.mozilla.javascript.ScriptableObject#has(String, Scriptable)
     * @see org.mozilla.javascript.ScriptableObject#READONLY
     * @see org.mozilla.javascript.ScriptableObject#DONTENUM
     * @see org.mozilla.javascript.ScriptableObject#PERMANENT
     * @see org.mozilla.javascript.ScriptableObject#EMPTY
     */
    public int getAttributes(int index)
    {
        return findAttributeSlot(null, index, SLOT_QUERY).getAttributes();
    }

    /**
     * Set the attributes of a named property.
     *
     * The property is specified by <code>name</code>
     * as defined for <code>has</code>.<p>
     *
     * The possible attributes are READONLY, DONTENUM,
     * and PERMANENT. Combinations of attributes
     * are expressed by the bitwise OR of attributes.
     * EMPTY is the state of no attributes set. Any unused
     * bits are reserved for future use.
     *
     * @param name the name of the property
     * @param attributes the bitset of attributes
     * @exception EvaluatorException if the named property is not found
     * @see org.mozilla.javascript.Scriptable#has(String, Scriptable)
     * @see org.mozilla.javascript.ScriptableObject#READONLY
     * @see org.mozilla.javascript.ScriptableObject#DONTENUM
     * @see org.mozilla.javascript.ScriptableObject#PERMANENT
     * @see org.mozilla.javascript.ScriptableObject#EMPTY
     */
    public void setAttributes(String name, int attributes)
    {
        checkNotSealed(name, 0);
        findAttributeSlot(name, 0, SLOT_MODIFY).setAttributes(attributes);
    }

    /**
     * XXX: write docs.
     */
    public void setGetterOrSetter(String name, int index,
                                  Callable getterOrSeter, boolean isSetter)
    {
        if (name != null && index != 0)
            throw new IllegalArgumentException(name);

        checkNotSealed(name, index);
        GetterSlot gslot = (GetterSlot)getSlot(name, index,
                                               SLOT_MODIFY_GETTER_SETTER);
        gslot.checkNotReadonly();
        if (isSetter) {
            gslot.setter = getterOrSeter;
        } else {
            gslot.getter = getterOrSeter;
        }
        gslot.value = Undefined.instance;
    }
    
    /**
     * Get the getter or setter for a given property. Used by __lookupGetter__
     * and __lookupSetter__.
     * 
     * @param name Name of the object. If nonnull, index must be 0.
     * @param index Index of the object. If nonzero, name must be null.
     * @param isSetter If true, return the setter, otherwise return the getter.
     * @exception IllegalArgumentException if both name and index are nonnull
     *            and nonzero respectively.
     * @return Null if the property does not exist. Otherwise returns either 
     *         the getter or the setter for the property, depending on 
     *         the value of isSetter (may be undefined if unset).
     */
    public Object getGetterOrSetter(String name, int index, boolean isSetter)
    {
        if (name != null && index != 0)
            throw new IllegalArgumentException(name);
        Slot slot = getSlot(name, index, SLOT_QUERY);
        if (slot == null)
            return null;
        if (slot instanceof GetterSlot) {
            GetterSlot gslot = (GetterSlot)slot;
            Object result = isSetter ? gslot.setter : gslot.getter;
            return result != null ? result : Undefined.instance;
        } else
            return Undefined.instance;
    }

//    void addLazilyInitializedValue(String name, int index,
//                                   LazilyLoadedCtor init, int attributes)
//    {
//        if (name != null && index != 0)
//            throw new IllegalArgumentException(name);
//        checkNotSealed(name, index);
//        GetterSlot gslot = (GetterSlot)getSlot(name, index,
//                                               SLOT_MODIFY_GETTER_SETTER);
//        gslot.setAttributes(attributes);
//        gslot.getter = null;
//        gslot.setter = null;
//        gslot.value = init;
//    }

    /**
     * Returns the prototype of the object.
     */
    public Scriptable getPrototype()
    {
        return prototypeObject;
    }

    /**
     * Sets the prototype of the object.
     */
    public void setPrototype(Scriptable m)
    {
        prototypeObject = m;
    }

    /**
     * Returns the parent (enclosing) scope of the object.
     */
    public Scriptable getParentScope()
    {
        return parentScopeObject;
    }

    /**
     * Sets the parent (enclosing) scope of the object.
     */
    public void setParentScope(Scriptable m)
    {
        parentScopeObject = m;
    }

    /**
     * Returns an array of ids for the properties of the object.
     *
     * <p>Any properties with the attribute DONTENUM are not listed. <p>
     *
     * @return an array of java.lang.Objects with an entry for every
     * listed property. Properties accessed via an integer index will
     * have a corresponding
     * Integer entry in the returned array. Properties accessed by
     * a String will have a String entry in the returned array.
     */
    public Object[] getIds() {
        return getIds(false);
    }

    /**
     * Implements the [[DefaultValue]] internal method.
     *
     * <p>Note that the toPrimitive conversion is a no-op for
     * every type other than Object, for which [[DefaultValue]]
     * is called. See ECMA 9.1.<p>
     *
     * A <code>hint</code> of null means "no hint".
     *
     * @param typeHint the type hint
     * @return the default value for the object
     *
     * See ECMA 8.6.2.6.
     */
    public Object getDefaultValue(Class typeHint)
    {
        return Js.getDefaultValue(this, typeHint);
    }
    
    /**
     * Implements the instanceof operator.
     *
     * <p>This operator has been proposed to ECMA.
     *
     * @param instance The value that appeared on the LHS of the instanceof
     *              operator
     * @return true if "this" appears in value's prototype chain
     *
     */
    public boolean hasInstance(Scriptable instance) {
        // Default for JS objects (other than Function) is to do prototype
        // chasing.  This will be overridden in NativeFunction and non-JS
        // objects.

        return ScriptRuntime.jsDelegatesTo(instance, this);
    }

    /**
     * Custom <tt>==</tt> operator.
     * Must return {@link Scriptable#NOT_FOUND} if this object does not
     * have custom equality operator for the given value,
     * <tt>Boolean.TRUE</tt> if this object is equivalent to <tt>value</tt>,
     * <tt>Boolean.FALSE</tt> if this object is not equivalent to
     * <tt>value</tt>.
     * <p>
     * The default implementation returns Boolean.TRUE
     * if <tt>this == value</tt> or {@link Scriptable#NOT_FOUND} otherwise.
     * It indicates that by default custom equality is available only if
     * <tt>value</tt> is <tt>this</tt> in which case true is returned.
     */
    protected Object equivalentValues(Object value)
    {
        return (this == value) ? Boolean.TRUE : Scriptable.NOT_FOUND;
    }

    /**
     * Defines JavaScript objects from a Java class that implements Scriptable.
     *
     * If the given class has a method
     * <pre>
     * static void init(Context cx, Scriptable scope, boolean sealed);</pre>
     *
     * or its compatibility form
     * <pre>
     * static void init(Scriptable scope);</pre>
     *
     * then it is invoked and no further initialization is done.<p>
     *
     * However, if no such a method is found, then the class's constructors and
     * methods are used to initialize a class in the following manner.<p>
     *
     * First, the zero-parameter constructor of the class is called to
     * create the prototype. If no such constructor exists,
     * a {@link EvaluatorException} is thrown. <p>
     *
     * Next, all methods are scanned for special prefixes that indicate that they
     * have special meaning for defining JavaScript objects.
     * These special prefixes are
     * <ul>
     * <li><code>jsFunction_</code> for a JavaScript function
     * <li><code>jsStaticFunction_</code> for a JavaScript function that
     *           is a property of the constructor
     * <li><code>jsGet_</code> for a getter of a JavaScript property
     * <li><code>jsSet_</code> for a setter of a JavaScript property
     * <li><code>jsConstructor</code> for a JavaScript function that
     *           is the constructor
     * </ul><p>
     *
     * If the method's name begins with "jsFunction_", a JavaScript function
     * is created with a name formed from the rest of the Java method name
     * following "jsFunction_". So a Java method named "jsFunction_foo" will
     * define a JavaScript method "foo". Calling this JavaScript function
     * will cause the Java method to be called. The parameters of the method
     * must be of number and types as defined by the FunctionObject class.
     * The JavaScript function is then added as a property
     * of the prototype. <p>
     *
     * If the method's name begins with "jsStaticFunction_", it is handled
     * similarly except that the resulting JavaScript function is added as a
     * property of the constructor object. The Java method must be static.
     *
     * If the method's name begins with "jsGet_" or "jsSet_", the method is
     * considered to define a property. Accesses to the defined property
     * will result in calls to these getter and setter methods. If no
     * setter is defined, the property is defined as READONLY.<p>
     *
     * If the method's name is "jsConstructor", the method is
     * considered to define the body of the constructor. Only one
     * method of this name may be defined.
     * If no method is found that can serve as constructor, a Java
     * constructor will be selected to serve as the JavaScript
     * constructor in the following manner. If the class has only one
     * Java constructor, that constructor is used to define
     * the JavaScript constructor. If the the class has two constructors,
     * one must be the zero-argument constructor (otherwise an
     * {@link EvaluatorException} would have already been thrown
     * when the prototype was to be created). In this case
     * the Java constructor with one or more parameters will be used
     * to define the JavaScript constructor. If the class has three
     * or more constructors, an {@link EvaluatorException}
     * will be thrown.<p>
     *
     * Finally, if there is a method
     * <pre>
     * static void finishInit(Scriptable scope, FunctionObject constructor,
     *                        Scriptable prototype)</pre>
     *
     * it will be called to finish any initialization. The <code>scope</code>
     * argument will be passed, along with the newly created constructor and
     * the newly created prototype.<p>
     *
     * @param scope The scope in which to define the constructor.
     * @param clazz The Java class to use to define the JavaScript objects
     *              and properties.
     * @exception IllegalAccessException if access is not available
     *            to a reflected class member
     * @exception InstantiationException if unable to instantiate
     *            the named class
     * @exception InvocationTargetException if an exception is thrown
     *            during execution of methods of the named class
     * @see org.mozilla.javascript.Function
     * @see org.mozilla.javascript.FunctionObject
     * @see org.mozilla.javascript.ScriptableObject#READONLY
     * @see org.mozilla.javascript.ScriptableObject
     *      #defineProperty(String, Class, int)
     */
//    public static void defineClass(Scriptable scope, Class clazz)
//        throws IllegalAccessException, InstantiationException,
//               InvocationTargetException
//    {
//        defineClass(scope, clazz, false, false);
//    }

    /**
     * Defines JavaScript objects from a Java class, optionally
     * allowing sealing.
     *
     * Similar to <code>defineClass(Scriptable scope, Class clazz)</code>
     * except that sealing is allowed. An object that is sealed cannot have
     * properties added or removed. Note that sealing is not allowed in
     * the current ECMA/ISO language specification, but is likely for
     * the next version.
     *
     * @param scope The scope in which to define the constructor.
     * @param clazz The Java class to use to define the JavaScript objects
     *              and properties. The class must implement Scriptable.
     * @param sealed Whether or not to create sealed standard objects that
     *               cannot be modified.
     * @exception IllegalAccessException if access is not available
     *            to a reflected class member
     * @exception InstantiationException if unable to instantiate
     *            the named class
     * @exception InvocationTargetException if an exception is thrown
     *            during execution of methods of the named class
     * @since 1.4R3
     */
//    public static void defineClass(Scriptable scope, Class clazz,
//                                   boolean sealed)
//        throws IllegalAccessException, InstantiationException,
//               InvocationTargetException
//    {
//        defineClass(scope, clazz, sealed, false);
//    }

//    /**
//	 * Defines JavaScript objects from a Java class, optionally allowing sealing
//	 * and mapping of Java inheritance to JavaScript prototype-based
//	 * inheritance.
//	 * 
//	 * Similar to <code>defineClass(Scriptable scope, Class clazz)</code>
//	 * except that sealing and inheritance mapping are allowed. An object that
//	 * is sealed cannot have properties added or removed. Note that sealing is
//	 * not allowed in the current ECMA/ISO language specification, but is likely
//	 * for the next version.
//	 * 
//	 * @param scope
//	 *            The scope in which to define the constructor.
//	 * @param clazz
//	 *            The Java class to use to define the JavaScript objects and
//	 *            properties. The class must implement Scriptable.
//	 * @param sealed
//	 *            Whether or not to create sealed standard objects that cannot
//	 *            be modified.
//	 * @param mapInheritance
//	 *            Whether or not to map Java inheritance to JavaScript
//	 *            prototype-based inheritance.
//	 * @return the class name for the prototype of the specified class
//	 * @exception IllegalAccessException
//	 *                if access is not available to a reflected class member
//	 * @exception InstantiationException
//	 *                if unable to instantiate the named class
//	 * @exception InvocationTargetException
//	 *                if an exception is thrown during execution of methods of
//	 *                the named class
//	 * @since 1.6R2
//	 */
//	public static String defineClass(Scriptable scope, Class clazz,
//			boolean sealed, boolean mapInheritance)
//			throws IllegalAccessException, InstantiationException,
//			InvocationTargetException {
//		BaseFunction ctor = buildClassCtor(scope, clazz, sealed, mapInheritance);
//		if (ctor == null)
//			return null;
//		String name = ctor.getClassPrototype().getClassName();
//		defineProperty(scope, name, ctor, ScriptableObject.DONTENUM);
//		return name;
//	}
//
//	static BaseFunction buildClassCtor(Scriptable scope, Class clazz,
//			boolean sealed, boolean mapInheritance)
//			throws IllegalAccessException, InstantiationException,
//			InvocationTargetException {
//		Method[] methods = FunctionObject.getMethodList(clazz);
//		for (int i = 0; i < methods.length; i++) {
//			Method method = methods[i];
//			if (!method.getName().equals("init"))
//				continue;
//			Class[] parmTypes = method.getParameterTypes();
//			if (parmTypes.length == 3
//					&& parmTypes[0] == ScriptRuntime.ContextClass
//					&& parmTypes[1] == ScriptRuntime.ScriptableClass
//					&& parmTypes[2] == Boolean.TYPE
//					&& Modifier.isStatic(method.getModifiers())) {
//				Object args[] = { Context.getContext(), scope,
//						sealed ? Boolean.TRUE : Boolean.FALSE };
//				method.invoke(null, args);
//				return null;
//			}
//			if (parmTypes.length == 1
//					&& parmTypes[0] == ScriptRuntime.ScriptableClass
//					&& Modifier.isStatic(method.getModifiers())) {
//				Object args[] = { scope };
//				method.invoke(null, args);
//				return null;
//			}
//
//		}
//
//		// If we got here, there isn't an "init" method with the right
//		// parameter types.
//
//		Constructor[] ctors = clazz.getConstructors();
//		Constructor protoCtor = null;
//		for (int i = 0; i < ctors.length; i++) {
//			if (ctors[i].getParameterTypes().length == 0) {
//				protoCtor = ctors[i];
//				break;
//			}
//		}
//		if (protoCtor == null) {
//			throw Context.reportRuntimeError1("msg.zero.arg.ctor", clazz
//					.getName());
//		}
//
//		Scriptable proto = (Scriptable) protoCtor
//				.newInstance(ScriptRuntime.emptyArgs);
//		String className = proto.getClassName();
//
//		// Set the prototype's prototype, trying to map Java inheritance to JS
//		// prototype-based inheritance if requested to do so.
//		Scriptable superProto = null;
//		if (mapInheritance) {
//			Class superClass = clazz.getSuperclass();
//			if (ScriptRuntime.ScriptableClass.isAssignableFrom(superClass)
//					&& !Modifier.isAbstract(superClass.getModifiers())) {
//				String name = ScriptableObject.defineClass(scope, superClass,
//						sealed, mapInheritance);
//				if (name != null) {
//					superProto = ScriptableObject
//							.getClassPrototype(scope, name);
//				}
//			}
//		}
//		if (superProto == null) {
//			superProto = ScriptableObject.getObjectPrototype(scope);
//		}
//		proto.setPrototype(superProto);
//
//		// Find out whether there are any methods that begin with
//		// "js". If so, then only methods that begin with special
//		// prefixes will be defined as JavaScript entities.
//		final String functionPrefix = "jsFunction_";
//		final String staticFunctionPrefix = "jsStaticFunction_";
//		final String getterPrefix = "jsGet_";
//		final String setterPrefix = "jsSet_";
//		final String ctorName = "jsConstructor";
//
//		Member ctorMember = FunctionObject.findSingleMethod(methods, ctorName);
//
//		if (ctorMember == null) {
//			if (ctors.length == 1) {
//				ctorMember = ctors[0];
//			} else if (ctors.length == 2) {
//				if (ctors[0].getParameterTypes().length == 0)
//					ctorMember = ctors[1];
//				else if (ctors[1].getParameterTypes().length == 0)
//					ctorMember = ctors[0];
//			}
//			if (ctorMember == null) {
//				throw Context.reportRuntimeError1("msg.ctor.multiple.parms",
//						clazz.getName());
//			}
//		}
//
//		FunctionObject ctor = new FunctionObject(className, ctorMember, scope);
//		if (ctor.isVarArgsMethod()) {
//			throw Context.reportRuntimeError1("msg.varargs.ctor", ctorMember
//					.getName());
//		}
//		ctor.initAsConstructor(scope, proto);
//
//		Method finishInit = null;
//		for (int i = 0; i < methods.length; i++) {
//			if (methods[i] == ctorMember) {
//				continue;
//			}
//			String name = methods[i].getName();
//			if (name.equals("finishInit")) {
//				Class[] parmTypes = methods[i].getParameterTypes();
//				if (parmTypes.length == 3
//						&& parmTypes[0] == ScriptRuntime.ScriptableClass
//						&& parmTypes[1] == FunctionObject.class
//						&& parmTypes[2] == ScriptRuntime.ScriptableClass
//						&& Modifier.isStatic(methods[i].getModifiers())) {
//					finishInit = methods[i];
//					continue;
//				}
//			}
//			// ignore any compiler generated methods.
//			if (name.indexOf('$') != -1)
//				continue;
//			if (name.equals(ctorName))
//				continue;
//
//			String prefix = null;
//			if (name.startsWith(functionPrefix)) {
//				prefix = functionPrefix;
//			} else if (name.startsWith(staticFunctionPrefix)) {
//				prefix = staticFunctionPrefix;
//				if (!Modifier.isStatic(methods[i].getModifiers())) {
//					throw Context
//							.reportRuntimeError("jsStaticFunction must be used with static method.");
//				}
//			} else if (name.startsWith(getterPrefix)) {
//				prefix = getterPrefix;
//			} else if (name.startsWith(setterPrefix)) {
//				prefix = setterPrefix;
//			} else {
//				continue;
//			}
//			name = name.substring(prefix.length());
//			if (prefix == setterPrefix)
//				continue; // deal with set when we see get
//			if (prefix == getterPrefix) {
//				if (!(proto instanceof ScriptableObject)) {
//					throw Context.reportRuntimeError2("msg.extend.scriptable",
//							proto.getClass().toString(), name);
//				}
//				Method setter = FunctionObject.findSingleMethod(methods,
//						setterPrefix + name);
//				int attr = ScriptableObject.PERMANENT
//						| ScriptableObject.DONTENUM
//						| (setter != null ? 0 : ScriptableObject.READONLY);
//				((ScriptableObject) proto).defineProperty(name, null,
//						methods[i], setter, attr);
//				continue;
//			}
//
//			FunctionObject f = new FunctionObject(name, methods[i], proto);
//			if (f.isVarArgsConstructor()) {
//				throw Context.reportRuntimeError1("msg.varargs.fun", ctorMember
//						.getName());
//			}
//			Scriptable dest = prefix == staticFunctionPrefix ? ctor : proto;
//			defineProperty(dest, name, f, DONTENUM);
//			if (sealed) {
//				f.sealObject();
//			}
//		}
//
//		// Call user code to complete initialization if necessary.
//		if (finishInit != null) {
//			Object[] finishArgs = { scope, ctor, proto };
//			finishInit.invoke(null, finishArgs);
//		}
//
//		// Seal the object if necessary.
//		if (sealed) {
//			ctor.sealObject();
//			if (proto instanceof ScriptableObject) {
//				((ScriptableObject) proto).sealObject();
//			}
//		}
//
//		return ctor;
//	}

    /**
	 * Define a JavaScript property.
	 * 
	 * Creates the property with an initial value and sets its attributes.
	 * 
	 * @param propertyName
	 *            the name of the property to define.
	 * @param value
	 *            the initial value of the property
	 * @param attributes
	 *            the attributes of the JavaScript property
	 * @see org.mozilla.javascript.Scriptable#put(String, Scriptable, Object)
	 */
    public void defineProperty(String propertyName, Object value,
                               int attributes)
    {
        checkNotSealed(propertyName, 0);
        put(propertyName, this, value);
        setAttributes(propertyName, attributes);
    }

    /**
     * Seal this object.
     *
     * A sealed object may not have properties added or removed. Once
     * an object is sealed it may not be unsealed.
     *
     * @since 1.4R3
     */
    public synchronized void sealObject() {
        if (count >= 0) {
            count = ~count;
        }
    }

    /**
     * Return true if this object is sealed.
     *
     * It is an error to attempt to add or remove properties to
     * a sealed object.
     *
     * @return true if sealed, false otherwise.
     * @since 1.4R3
     */
    public final boolean isSealed() {
        return count < 0;
    }

    private void checkNotSealed(String name, int index)
    {
        if (!isSealed())
            return;

        String str = (name != null) ? name : Integer.toString(index);
        throw Context.reportRuntimeError1("msg.modify.sealed", str);
    }

    /**
     * Get arbitrary application-specific value associated with this object.
     * @param key key object to select particular value.
     * @see #associateValue(Object key, Object value)
     */
    public final Object getAssociatedValue(Object key)
    {
        Hashtable h = associatedValues;
        if (h == null)
            return null;
        return h.get(key);
    }

    /**
     * Associate arbitrary application-specific value with this object.
     * Value can only be associated with the given object and key only once.
     * The method ignores any subsequent attempts to change the already
     * associated value.
     * <p> The associated values are not serilized.
     * @param key key object to select particular value.
     * @param value the value to associate
     * @return the passed value if the method is called first time for the
     * given key or old value for any subsequent calls.
     * @see #getAssociatedValue(Object key)
     */
    public final Object associateValue(Object key, Object value)
    {
        if (value == null) throw new IllegalArgumentException();
        Hashtable h = associatedValues;
        if (h == null) {
            synchronized (this) {
                h = associatedValues;
                if (h == null) {
                    h = new Hashtable();
                    associatedValues = h;
                }
            }
        }
        return Kit.initHash(h, key, value);
    }

    private Object getImpl(String name, int index, Scriptable start)
    {
        Slot slot = getSlot(name, index, SLOT_QUERY);
        if (slot == null) {
            return Scriptable.NOT_FOUND;
        }
        if (!(slot instanceof GetterSlot)) {
            return slot.value;
        }
        Object getterObj = ((GetterSlot)slot).getter;
        if (getterObj != null) {
//            if (getterObj instanceof MemberBox) {
//                MemberBox nativeGetter = (MemberBox)getterObj;
//                Object getterThis;
//                Object[] args;
//                if (nativeGetter.delegateTo == null) {
//                    getterThis = start;
//                    args = ScriptRuntime.emptyArgs;
//                } else {
//                    getterThis = nativeGetter.delegateTo;
//                    args = new Object[] { start };
//                }
//                return nativeGetter.invoke(getterThis, args);
//            } else {
                Callable f = (Callable)getterObj;
                Context cx = Context.getContext();
                return f.call(cx, ScriptRuntime.getTopCallScope(cx), start,
                              ScriptRuntime.emptyArgs);
//            }
        }
        Object value = slot.value;
//        if (value instanceof LazilyLoadedCtor) {
//            LazilyLoadedCtor initializer = (LazilyLoadedCtor)value;
//            try {
//                initializer.init();
//            } finally {
//                value = initializer.getValue();
//                slot.value = value;
//            }
//        }
        return value;
    }

    /**
     *
     * @param name
     * @param index
     * @param start
     * @param value
     * @param constFlag EMPTY means normal put.  UNINITIALIZED_CONST means
     * defineConstProperty.  READONLY means const initialization expression.
     * @return false if this != start and no slot was found.  true if this == start
     * or this != start and a READONLY slot was found.
     */
    private boolean putImpl(String name, int index, Scriptable start,
                            Object value, int constFlag)
    {
        Slot slot;
        if (this != start) {
            slot = getSlot(name, index, SLOT_QUERY);
            if (slot == null) {
                return false;
            }
        } else {
            checkNotSealed(name, index);
            // either const hoisted declaration or initialization
            if (constFlag != EMPTY) {
                slot = getSlot(name, index, SLOT_MODIFY_CONST);
                int attr = slot.getAttributes();
                if ((attr & READONLY) == 0)
                    throw Context.reportRuntimeError1("msg.var.redecl", name);
                if ((attr & UNINITIALIZED_CONST) != 0) {
                    slot.value = value;
                    // clear the bit on const initialization
                    if (constFlag != UNINITIALIZED_CONST)
                        slot.setAttributes(attr & ~UNINITIALIZED_CONST);
                }
                return true;
            }
            slot = getSlot(name, index, SLOT_MODIFY);
        }
        if ((slot.getAttributes() & READONLY) != 0)
            return true;
        if (slot instanceof GetterSlot) {
            Object setterObj = ((GetterSlot)slot).setter;
            if (setterObj != null) {
                Context cx = Context.getContext();
//                if (setterObj instanceof MemberBox) {
//                    MemberBox nativeSetter = (MemberBox)setterObj;
//                    Class pTypes[] = nativeSetter.argTypes;
//                    // XXX: cache tag since it is already calculated in
//                    // defineProperty ?
//                    Class valueType = pTypes[pTypes.length - 1];
//                    int tag = FunctionObject.getTypeTag(valueType);
//                    Object actualArg = FunctionObject.convertArg(cx, start,
//                                                                 value, tag);
//                    Object setterThis;
//                    Object[] args;
//                    if (nativeSetter.delegateTo == null) {
//                        setterThis = start;
//                        args = new Object[] { actualArg };
//                    } else {
//                        setterThis = nativeSetter.delegateTo;
//                        args = new Object[] { start, actualArg };
//                    }
//                    nativeSetter.invoke(setterThis, args);
//                } else {
                    Callable f = (Callable)setterObj;
                    f.call(cx, ScriptRuntime.getTopCallScope(cx), start,
                           new Object[] { value });
//                }
                return true;
            }
        }
        if (this == start) {
            slot.value = value;
            return true;
        } else {
            return false;
        }
    }

    private Slot findAttributeSlot(String name, int index, int accessType)
    {
        Slot slot = getSlot(name, index, accessType);
        if (slot == null) {
            String str = (name != null ? name : Integer.toString(index));
            throw Context.reportRuntimeError1("msg.prop.not.found", str);
        }
        return slot;
    }

    /**
     * Locate the slot with given name or index.
     *
     * @param name property name or null if slot holds spare array index.
     * @param index index or 0 if slot holds property name.
     */
    private Slot getSlot(String name, int index, int accessType)
    {
        Slot slot;

        // Query last access cache and check that it was not deleted.
      lastAccessCheck:
        {
            slot = lastAccess;
            if (name != null) {
                if (name != slot.name)
                    break lastAccessCheck;
                // No String.equals here as successful slot search update
                // name object with fresh reference of the same string.
            } else {
                if (slot.name != null || index != slot.indexOrHash)
                    break lastAccessCheck;
            }

            if (slot.wasDeleted != 0)
                break lastAccessCheck;

            if (accessType == SLOT_MODIFY_GETTER_SETTER &&
                !(slot instanceof GetterSlot))
                break lastAccessCheck;

            return slot;
        }

        slot = accessSlot(name, index, accessType);
        if (slot != null) {
            // Update the cache
            lastAccess = slot;
        }
        return slot;
    }

    private Slot accessSlot(String name, int index, int accessType)
    {
        int indexOrHash = (name != null ? name.hashCode() : index);

        if (accessType == SLOT_QUERY ||
            accessType == SLOT_MODIFY ||
            accessType == SLOT_MODIFY_CONST ||
            accessType == SLOT_MODIFY_GETTER_SETTER)
        {
            // Check the hashtable without using synchronization

            Slot[] slotsLocalRef = slots; // Get stable local reference
            if (slotsLocalRef == null) {
                if (accessType == SLOT_QUERY)
                    return null;
            } else {
                int tableSize = slotsLocalRef.length;
                int slotIndex = Js.getSlotIndex(tableSize, indexOrHash);
                Slot slot = slotsLocalRef[slotIndex];
                while (slot != null) {
                    String sname = slot.name;
                    if (sname != null) {
                        if (sname == name)
                            break;
                        if (name != null && indexOrHash == slot.indexOrHash) {
                            if (name.equals(sname)) {
                                // This will avoid calling String.equals when
                                // slot is accessed with same string object
                                // next time.
                                slot.name = name;
                                break;
                            }
                        }
                    } else if (name == null &&
                               indexOrHash == slot.indexOrHash) {
                        break;
                    }
                    slot = slot.next;
                }
                if (accessType == SLOT_QUERY) {
                    return slot;
                } else if (accessType == SLOT_MODIFY) {
                    if (slot != null)
                        return slot;
                } else if (accessType == SLOT_MODIFY_GETTER_SETTER) {
                    if (slot instanceof GetterSlot)
                        return slot;
                } else if (accessType == SLOT_MODIFY_CONST) {
                    if (slot != null)
                        return slot;
                }
            }

            // A new slot has to be inserted or the old has to be replaced
            // by GetterSlot. Time to synchronize.

            synchronized (this) {
                // Refresh local ref if another thread triggered grow
                slotsLocalRef = slots;
                int insertPos;
                if (count == 0) {
                    // Always throw away old slots if any on empty insert
                    slotsLocalRef = new Slot[5];
                    slots = slotsLocalRef;
                    insertPos = Js.getSlotIndex(slotsLocalRef.length, indexOrHash);
                } else {
                    int tableSize = slotsLocalRef.length;
                    insertPos = Js.getSlotIndex(tableSize, indexOrHash);
                    Slot prev = slotsLocalRef[insertPos];
                    Slot slot = prev;
                    while (slot != null) {
                        if (slot.indexOrHash == indexOrHash &&
                            (slot.name == name ||
                             (name != null && name.equals(slot.name))))
                        {
                            break;
                        }
                        prev = slot;
                        slot = slot.next;
                    }

                    if (slot != null) {
                        // Another thread just added a slot with same
                        // name/index before this one entered synchronized
                        // block. This is a race in application code and
                        // probably indicates bug there. But for the hashtable
                        // implementation it is harmless with the only
                        // complication is the need to replace the added slot
                        // if we need GetterSlot and the old one is not.
                        if (accessType == SLOT_MODIFY_GETTER_SETTER &&
                            !(slot instanceof GetterSlot))
                        {
                            GetterSlot newSlot = new GetterSlot(name, indexOrHash,
                                    slot.getAttributes());
                            newSlot.value = slot.value;
                            newSlot.next = slot.next;
                            if (prev == slot) {
                                slotsLocalRef[insertPos] = newSlot;
                            } else {
                                prev.next = newSlot;
                            }
                            slot.wasDeleted = (byte)1;
                            if (slot == lastAccess) {
                                lastAccess = REMOVED;
                            }
                            slot = newSlot;
                        } else if (accessType == SLOT_MODIFY_CONST) {
                            return null;
                        }
                        return slot;
                    }

                    // Check if the table is not too full before inserting.
                    if (4 * (count + 1) > 3 * slotsLocalRef.length) {
                        slotsLocalRef = new Slot[slotsLocalRef.length * 2 + 1];
                        Js.copyTable(slots, slotsLocalRef, count);
                        slots = slotsLocalRef;
                        insertPos = Js.getSlotIndex(slotsLocalRef.length,
                                indexOrHash);
                    }
                }

                Slot newSlot = (accessType == SLOT_MODIFY_GETTER_SETTER
                                ? new GetterSlot(name, indexOrHash, 0)
                                : new Slot(name, indexOrHash, 0));
                if (accessType == SLOT_MODIFY_CONST)
                    newSlot.setAttributes(CONST);
                ++count;
                Js.addKnownAbsentSlot(slotsLocalRef, newSlot, insertPos);
                return newSlot;
            }

        } else if (accessType == SLOT_REMOVE) {
            synchronized (this) {
                Slot[] slotsLocalRef = slots;
                if (count != 0) {
                    int tableSize = slots.length;
                    int slotIndex = Js.getSlotIndex(tableSize, indexOrHash);
                    Slot prev = slotsLocalRef[slotIndex];
                    Slot slot = prev;
                    while (slot != null) {
                        if (slot.indexOrHash == indexOrHash &&
                            (slot.name == name ||
                             (name != null && name.equals(slot.name))))
                        {
                            break;
                        }
                        prev = slot;
                        slot = slot.next;
                    }
                    if (slot != null && (slot.getAttributes() & PERMANENT) == 0) {
                        count--;
                        if (prev == slot) {
                            slotsLocalRef[slotIndex] = slot.next;
                        } else {
                            prev.next = slot.next;
                        }
                        // Mark the slot as removed to handle a case when
                        // another thread manages to put just removed slot
                        // into lastAccess cache.
                        slot.wasDeleted = (byte)1;
                        if (slot == lastAccess) {
                            lastAccess = REMOVED;
                        }
                    }
                }
            }
            return null;

        } else {
            throw Kit.codeBug();
        }
    }

    Object[] getIds(boolean getAll) {
        Slot[] s = slots;
        Object[] a = ScriptRuntime.emptyArgs;
        if (s == null)
            return a;
        int c = 0;
        for (int i=0; i < s.length; i++) {
            Slot slot = s[i];
            while (slot != null) {
                if (getAll || (slot.getAttributes() & DONTENUM) == 0) {
                    if (c == 0)
                        a = new Object[s.length];
                    a[c++] = (slot.name != null ? (Object) slot.name
                              : new Integer(slot.indexOrHash));
                }
                slot = slot.next;
            }
        }
        if (c == a.length)
            return a;
        Object[] result = new Object[c];
        System.arraycopy(a, 0, result, 0, c);
        return result;
    }

	public void read(DataInputStream in) throws IOException {
		count = in.readInt();
		parentScopeObject = (Scriptable) Serializer.deserialize(in);
		prototypeObject = (Scriptable) Serializer.deserialize(in);
        lastAccess = REMOVED;

        int tableSize = in.readInt();
        if (tableSize != 0) {
            slots = new Slot[tableSize];
            int objectsCount = count;
            if (objectsCount < 0) {
                // "this" was sealed
                objectsCount = ~objectsCount;
            }
            for (int i = 0; i != objectsCount; ++i) {
                Slot slot = (Slot)Serializer.deserialize(in);
                int slotIndex = Js.getSlotIndex(tableSize, slot.indexOrHash);
                Js.addKnownAbsentSlot(slots, slot, slotIndex);
            }
        }

	}

	
	public void write(DataOutputStream out) throws IOException {
		out.writeInt(count);
		Serializer.serialize(parentScopeObject, out);
		Serializer.serialize(prototypeObject, out);
        int objectsCount = count;
        if (objectsCount < 0) {
            // "this" was sealed
            objectsCount = ~objectsCount;
        }
        if (objectsCount == 0) {
            out.writeInt(0);
        } else {
            out.writeInt(slots.length);
            for (int i = 0; i < slots.length; ++i) {
                Slot slot = slots[i];
                while (slot != null) {
                    Serializer.serialize(slot, out);
                    slot = slot.next;
                    if (--objectsCount == 0)
                        return;
                }
            }
        }

	}

}
