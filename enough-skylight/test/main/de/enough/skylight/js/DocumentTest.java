package de.enough.skylight.js;

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
		} catch (Throwable e) {/* Ok. */}
	}
	
	public void testGetElementByIdZeroParametersWithTryInJs() throws FileNotFoundException, IOException {
		Script script = this.context.compileString("try{var element = document.getElementById();Assert.fail('bla')} catch(e){}", "test1", 1);
		try {
			script.exec(this.context, this.scope);
			fail();
		} catch (Throwable e) {/* Ok. */}
	}
	
	public void testGetElementByIdZeroParameters() throws FileNotFoundException, IOException {
		Script script = this.context.compileString("var element = document.getElementById();Assert.fail('No exception was thrown although getElementById need at least one parameter.')", "test1", 1);
		try {
			script.exec(this.context, this.scope);
			fail();
		} catch (Throwable e) {/* Ok. */}
	}
}
