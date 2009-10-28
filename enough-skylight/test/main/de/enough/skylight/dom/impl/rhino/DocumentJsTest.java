package de.enough.skylight.dom.impl.rhino;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

import de.enough.polish.util.StreamUtil;
import de.enough.skylight.dom.impl.DocumentImpl;
import de.enough.skylight.dom.impl.DomParser;
import de.enough.skylight.dom.impl.HtmlPages;

public class DocumentJsTest extends TestCase {

	private Scriptable scope;
	private Context context;
	
	@Override
	public void setUp() {
		this.context = Context.enter();
		this.scope = this.context.initStandardObjects();
		String jsAssert;
		String jsTestDocument;
		String jsFilePath = "";
		Script script;
		try {
			jsFilePath = JsScripts.JS_ASSERT;
			jsAssert = StreamUtil.getString(new FileInputStream(jsFilePath), null, StreamUtil.DEFAULT_BUFFER);
			script = this.context.compileString(jsAssert, jsFilePath, 1);
			script.exec(this.context, this.scope);
			jsFilePath = JsScripts.JS_TEST_DOCUMENT;
			jsTestDocument = StreamUtil.getString(new FileInputStream(jsFilePath), null, StreamUtil.DEFAULT_BUFFER);
			script = this.context.compileString(jsTestDocument, jsFilePath, 1);
			script.exec(this.context, this.scope);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not load file '"+jsFilePath+"'."+e);
		} catch (IOException e) {
			throw new RuntimeException("Could not load file '"+jsFilePath+"'."+e);
		}
		
		
	}
	
	public void testA() throws FileNotFoundException, IOException {
		
		
		DocumentImpl document  = null;
		DomParser domParser = new DomParser();
		String htmlDocument;
		htmlDocument = StreamUtil.getString(new FileInputStream(HtmlPages.HTML_PAGE_1), null, StreamUtil.DEFAULT_BUFFER);
		document = (DocumentImpl)domParser.parseTree(htmlDocument);
		
		this.scope.put("document", this.scope, document.getScriptable());
		
		String code = "unitTest.run();";
		Script script = this.context.compileString(code, "<cmd>", 1);
		
		script.exec(this.context, this.scope);
	}
}
