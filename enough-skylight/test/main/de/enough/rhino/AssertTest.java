package de.enough.rhino;

import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;


public class AssertTest extends TestCase {

	private Scriptable scope;
	private Context context;
	
	@Override
	public void setUp() {
		this.context = Context.enter();
		this.context.setOptionOnErrorThrowExeption(true);
		this.scope = this.context.initStandardObjects();
		AssertScriptableObject assertScriptableObject = new AssertScriptableObject();
		assertScriptableObject.init();
		this.scope.put("Assert",this.scope,assertScriptableObject);
		
		
	}
	
	public void testEquals() throws FileNotFoundException, IOException {
		Script script = this.context.compileString("Assert.equals(\"a\",\"a\");", "test1", 1);
		script.exec(this.context, this.scope);
	}

	public void testEqualsFalse() throws FileNotFoundException, IOException {
		Script script = this.context.compileString("Assert.equals(\"a\",\"b\");", "test1", 1);
		try {
			script.exec(this.context, this.scope);
			fail();
		}
		catch(Error e) {/* Ok. */}
	}
	
	public void testNotNull() throws FileNotFoundException, IOException {
		Script script = this.context.compileString("Assert.notNull(\"a\")", "test1", 1);
		script.exec(this.context, this.scope);
	}
	
	public void testNotNullFalse() throws FileNotFoundException, IOException {
		Script script = this.context.compileString("Assert.notNull(\"0\")", "test1", 1);
		try {
			script.exec(this.context, this.scope);
			fail();
		} catch(Error e) {/* Ok. */}
	}
	
	
}
