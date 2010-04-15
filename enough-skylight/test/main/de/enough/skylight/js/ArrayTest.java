package de.enough.skylight.js;

import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Assert;

public class ArrayTest extends AbstractJsTest {

	public void testTagName() throws FileNotFoundException, IOException {
		Object result = this.jsEngine.runScript("var a = [0,1,2]; var b = [3,4,5]; a.constructor.prototype.bla = 'value'; b.bla");
		Assert.assertEquals("value", result);
	}
	
}
