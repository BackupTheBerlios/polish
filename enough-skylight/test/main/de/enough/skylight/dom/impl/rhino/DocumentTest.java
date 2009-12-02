package de.enough.skylight.dom.impl.rhino;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.mozilla.javascript.Script;


public class DocumentTest extends AbstractJsTest {

	public void testExisting() throws FileNotFoundException, IOException {
		Script script = this.context.compileString("Assert.notNull(document)", "test1", 1);
		script.exec(this.context, this.scope);
	}

	public void testGetElementByIde() throws FileNotFoundException, IOException {
		Script script = this.context.compileString("var element = document.getElementById('id2');Assert.notNull(element)", "test1", 1);
		script.exec(this.context, this.scope);
	}
	
	public void testDocumentElement() throws FileNotFoundException, IOException {
		Script script = this.context.compileString("var element = document.documentElement;Assert.notNull(element); Assert.equals('html',element.nodeName)", "test1", 1);
		script.exec(this.context, this.scope);
	}
	
	public void testGetElementByIdeFail() throws FileNotFoundException, IOException {
		Script script = this.context.compileString("var element = document.getElementById('kfjahdutrhwt');Assert.notNull(element)", "test1", 1);
		try {
			script.exec(this.context, this.scope);
			fail();
		} catch (Error e) {/* Ok. */}
	}
}