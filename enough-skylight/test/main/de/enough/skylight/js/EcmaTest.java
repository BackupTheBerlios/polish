package de.enough.skylight.js;

import java.io.FileNotFoundException;
import java.io.IOException;

public class EcmaTest extends AbstractJsTest {

	public void testExisting() throws FileNotFoundException, IOException {
		this.jsEngine.runScript("if(/hallo/.test('hallo')){} else{Assert.fail('RegEx not matched.')}");
	}
}
