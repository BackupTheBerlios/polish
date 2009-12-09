package de.enough.skylight.js;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.mozilla.javascript.Script;

public class ElementTest extends AbstractJsTest {

	public void testTagName() throws FileNotFoundException, IOException {
		Script script = this.context.compileString("var element = document.getElementById('id2');Assert.equals('div',element.tagName)", "test1", 1);
		script.exec(this.context, this.scope);
	}
	
	public void testSetAttribute() throws FileNotFoundException, IOException {
		Script script = this.context.compileString("var element = document.getElementById('id1'); element.setAttribute('class','someclass');Assert.equals('someclass',element.getAttribute('class'))", "test1", 1);
		script.exec(this.context, this.scope);
	}
}
