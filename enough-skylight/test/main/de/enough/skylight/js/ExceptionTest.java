package de.enough.skylight.js;


import java.io.FileNotFoundException;
import java.io.IOException;

import org.mozilla.javascript.Script;

public class ExceptionTest extends AbstractJsTest {

	// TODO: This test fails with Unable to call interpreted function java.lang.RuntimeException: The function 'getElementById' needs one parameter but got '0'.
	public void testException() throws FileNotFoundException, IOException {
		Script script = this.context.compileString("try{var failed = true; var element = document.getElementById();Assert.fail('An exception must be thrown by the previous call as the method requires a parameter')} catch(e){failed=false;e.message};if(failed){Assert.fail('No catch')}", "test1", 1);
		script.exec(this.context, this.scope);
	}
	
	// This test works but procudes a console output.
//	public void testGetElementByIdZeroParameters() throws FileNotFoundException, IOException {
//		Script script = this.context.compileString("var element = document.getElementById();Assert.fail('No exception was thrown although getElementById need at least one parameter.')", "test1", 1);
//		try {
//			script.exec(this.context, this.scope);
//			fail();
//		} catch (Throwable e) {/* Ok. */}
//	}
}

