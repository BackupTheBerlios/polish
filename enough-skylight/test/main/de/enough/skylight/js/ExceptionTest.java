package de.enough.skylight.js;


import java.io.FileNotFoundException;
import java.io.IOException;

public class ExceptionTest extends AbstractJsTest {

	// TODO: This test fails with Unable to call interpreted function java.lang.RuntimeException: The function 'getElementById' needs one parameter but got '0'.
	public void testException() throws FileNotFoundException, IOException {
		this.jsEngine.runScript("try{var failed = true; var element = document.getElementById();Assert.fail('An exception must be thrown by the previous call as the method requires a parameter')} catch(e){failed=false;e.message};if(failed){Assert.fail('No catch')}");
	}
}
	

