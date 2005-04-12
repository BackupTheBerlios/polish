/*
 * Created on 09-Apr-2005
 */
package de.enough.polish.plugin.eclipse.utils;

import de.enough.polish.plugin.eclipse.utils.MultiValueStack;
import de.enough.testutils.PrivateAccessor;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author rickyn
 */
public class MultiValueStackTest extends TestCase {

    private MultiValueStack stack;
    
    protected void setUp() throws Exception {
        this.stack = new MultiValueStack();
    }
    
    public void test_pushElement(){
        this.stack.pushElement(5);
        this.stack.pushElement(6);
        int[] stackField;
        
        stackField = (int[]) PrivateAccessor.getPrivateField(this.stack,"stack");
        Assert.assertTrue(stackField[0] == 5);
        Assert.assertTrue(stackField[1] == 6);
    }
}
