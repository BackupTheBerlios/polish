/*
 * DirectInvocationHandler.java
 *
 * Created on 17 February 2006, 23:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.ide.swing;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * @author enough
 */
public class DirectInvocationHandler implements InvocationHandler {

    private Object target;
    
    /** 
     * Creates a new instance of DirectInvocationHandler 
     */
    public DirectInvocationHandler( Object target ) {
        this.target = target;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke( this.target, args );
    }
    
}
