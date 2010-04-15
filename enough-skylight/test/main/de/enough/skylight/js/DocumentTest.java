package de.enough.skylight.js;


import java.io.FileNotFoundException;
import java.io.IOException;

public class DocumentTest extends AbstractJsTest {

	public void testExisting() throws FileNotFoundException, IOException {
		this.jsEngine.runScript("Assert.notNull(document)");
	}

	public void testGetElementByIde() throws FileNotFoundException, IOException {
		this.jsEngine.runScript("var element = document.getElementById('id2');Assert.notNull(element)");
	}
	
	public void testDocumentElement() throws FileNotFoundException, IOException {
		this.jsEngine.runScript("var element = document.documentElement;Assert.notNull(element); Assert.equals('html',element.nodeName)");
	}
	
	public void testGetElementByIdeFail() throws FileNotFoundException, IOException {
		try {
			this.jsEngine.runScript("var element = document.getElementById('kfjahdutrhwt');Assert.notNull(element)");
			fail();
		} catch (Throwable e) {/* Ok. */}
	}
}

