package de.enough.skylight.dom.impl.rhino;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

import de.enough.rhino.AssertScriptableObject;
import de.enough.skylight.dom.impl.AbstractDomTest;

public class DocumentTest extends AbstractDomTest {

	private Scriptable scope;
	private Context context;
	
	@Override
	public void setUp() {
		super.setUp();
		this.context = Context.enter();
		this.context.setOptionOnErrorThrowExeption(true);
		this.scope = this.context.initStandardObjects();
		AssertScriptableObject assertScriptableObject = new AssertScriptableObject();
		assertScriptableObject.init();
		this.scope.put("Assert",this.scope,assertScriptableObject);
		this.scope.put("document", this.scope, this.document1.getScriptable());
		
	}
	
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
