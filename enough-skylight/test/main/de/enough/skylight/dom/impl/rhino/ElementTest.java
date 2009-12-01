package de.enough.skylight.dom.impl.rhino;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.mozilla.javascript.Script;

public class ElementTest extends AbstractJsTest {

	public void testTagName() throws FileNotFoundException, IOException {
		Script script = this.context.compileString("var element = document.getElementById('id2');Assert.equals('div',element.tagName)", "test1", 1);
		script.exec(this.context, this.scope);
	}
}
