package de.enough.skylight.js;

import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Assert;

import org.mozilla.javascript.Script;
import org.mozilla.javascript.Undefined;

public class Element2Test extends AbstractJsTest {

	public void testPrototypeNotPresentOnObject() throws FileNotFoundException, IOException {
		Object result = this.jsEngine.runScript("var element = document.getElementById('id2');element.prototype;");
		Assert.assertEquals(Undefined.instance, result);
	}
	public void testConstructorPresentOnObject() throws FileNotFoundException, IOException {
		Object result = this.jsEngine.runScript("var element = document.getElementById('id2');element.constructor.prototype;");
		Assert.assertEquals(ElementIdScriptableObject.class, result.getClass());
	}
	
	public void testPrototypeMechanismWorks() throws FileNotFoundException, IOException {
		Object result = this.jsEngine.runScript("var element1 = document.getElementById('id2'); var element2 = document.getElementById('id1'); element1.constructor.prototype.field1 = 'aValue'; element2.field1");
		Assert.assertEquals("aValue", result);
	}
	
}
