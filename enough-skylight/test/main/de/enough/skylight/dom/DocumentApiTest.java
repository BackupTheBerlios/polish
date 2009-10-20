package de.enough.skylight.dom;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

import de.enough.polish.util.StreamUtil;
import de.enough.skylight.dom.impl.DomParser;

public class DocumentApiTest extends TestCase {

	public static final String HTML_PAGE_1 = "test/resources/test1.html";
	public static final String JS_ASSERT = "test/resources/assert.js";
	public static final String JS_TEST_DOCUMENT = "test/resources/documentApiTest.js";
	private Scriptable scope;
	private Context context;
	
	public void setUp() {
		this.context = Context.enter();
		this.scope = this.context.initStandardObjects();
		String jsAssert;
		String jsTestDocument;
		String file = "";
		try {
			file = JS_ASSERT;
			jsAssert = StreamUtil.getString(new FileInputStream(JS_ASSERT), null, StreamUtil.DEFAULT_BUFFER);
			file = JS_TEST_DOCUMENT;
			jsTestDocument = StreamUtil.getString(new FileInputStream(JS_TEST_DOCUMENT), null, StreamUtil.DEFAULT_BUFFER);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not load file '"+file+"'",e);
		} catch (IOException e) {
			throw new RuntimeException("Could not load file '"+file+"'",e);
		}
		Script script;
		script = this.context.compileString(jsAssert, JS_ASSERT, 1);
		script.exec(this.context, this.scope);
		
		script = this.context.compileString(jsTestDocument, JS_ASSERT, 1);
		script.exec(this.context, this.scope);
		
	}
	
	public void testA() throws FileNotFoundException, IOException {
		
		
		Document document  = null;
		DomParser domParser = new DomParser();
		String htmlDocument;
		htmlDocument = StreamUtil.getString(new FileInputStream(HTML_PAGE_1), null, StreamUtil.DEFAULT_BUFFER);
		document = domParser.parseTree(htmlDocument);
		
		this.scope.put("document", this.scope, document);
		
		String code = "unitTest.run();";
		Script script = this.context.compileString(code, "<cmd>", 1);
		
		Object result = script.exec(this.context, this.scope);
	}
}
