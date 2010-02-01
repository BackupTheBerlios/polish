package de.enough.skylight.js;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.mozilla.javascript.Script;

public class EcmaTest extends AbstractJsTest {

	public void testExisting() throws FileNotFoundException, IOException {
		Script script = this.context.compileString("if(/hallo/.test('hallo')){} else{Assert.fail('RegEx not matched.')}", "test1", 1);
		script.exec(this.context, this.scope);
	}
}
