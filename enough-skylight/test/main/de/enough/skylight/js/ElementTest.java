package de.enough.skylight.js;

import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Assert;

public class ElementTest extends AbstractJsTest {

	public void testTagName() throws FileNotFoundException, IOException {
		this.jsEngine.runScript("var element = document.getElementById('id2');Assert.equals('div',element.tagName)");
	}
	
	public void testSetAttribute() throws FileNotFoundException, IOException {
		this.jsEngine.runScript("var element = document.getElementById('id1'); element.setAttribute('class','someclass');Assert.equals('someclass',element.getAttribute('class'))");
	}
	
	public void testPrototype() throws FileNotFoundException, IOException {
		Object result = this.jsEngine.runScript("var element1 = document.getElementById('id1'); var element2 = document.getElementById('id2'); element1.constructor.prototype.bla = 'value'; element2.bla");
		Assert.assertEquals("value",result);
	}
}
