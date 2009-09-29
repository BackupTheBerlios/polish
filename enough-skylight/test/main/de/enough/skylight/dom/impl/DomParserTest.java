package de.enough.skylight.dom.impl;

import junit.framework.TestCase;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.impl.DomNodeImpl;
import de.enough.skylight.dom.impl.DomParser;

public class DomParserTest extends TestCase {


	private String xmlString1 = "<html><head></head><body id=\"bla\">Hallo<a id=\"myA\" href=\"http://www.enough.de\">Go to Enough Software</a>Welt</body></html>";

	public void testParser1() {
		Document document  = null;
		DomParser domParser = new DomParser();
		document = domParser.parseTree("<html><head></head><body>Hallo<a href=\"http://www.enough.de\">Go to Enough Software</a>Welt</body></html>");
		assertNotNull(document);
	}
	
	public void testGetElementById1() {
		Context context;
		context = Context.enter();
		Scriptable scope = context.initStandardObjects();
		
		Document document  = null;
		DomParser domParser = new DomParser();
		document = domParser.parseTree(this.xmlString1);
		
		scope.put("document", scope, document);
		
		String code = "document.getElementById(\"myA\")";
		Script script = context.compileString(code, "<cmd>", 1);
		
		Object result = script.exec(context, scope);
		assertEquals(DomNodeImpl.class, result.getClass());
		assertEquals("a", ((DomNodeImpl)result).getNodeName());
		
		System.out.println(this.xmlString1);
		System.out.println(code);
		System.out.println(result);
		System.out.println("END");
		Context.exit();
	}
	
	public void testHostObject2() {
		Context context;
		context = Context.enter();
		Scriptable scope = context.initStandardObjects();
		
		Document document  = null;
		DomParser domParser = new DomParser();
		document = domParser.parseTree(this.xmlString1);
		
		scope.put("document", scope, document);
		
		String code = "document.getElementById(\"myA\").hasAttributes()";
		Script script = context.compileString(code, "<cmd>", 1);
		
		Object result = script.exec(context, scope);
		assertEquals(Boolean.TRUE, result);
		
		System.out.println(this.xmlString1);
		System.out.println(code);
		System.out.println(result);
		System.out.println("END");
		Context.exit();
	}
	
	public void testHostObject3() {
		Context context;
		context = Context.enter();
		Scriptable scope = context.initStandardObjects();
		
		Document document  = null;
		DomParser domParser = new DomParser();
		document = domParser.parseTree(this.xmlString1);
		
		scope.put("document", scope, document);
		
		String code = "document.getElementById(\"myA\").attributes.item(1).name;";
		Script script = context.compileString(code, "<cmd>", 1);
		
		Object result = script.exec(context, scope);
		assertEquals("id", result);
		
		System.out.println(this.xmlString1);
		System.out.println(code);
		System.out.println(result);
		System.out.println("END");
		Context.exit();
	}
	
	public void testConstant() {
		Context context;
		context = Context.enter();
		Scriptable scope = context.initStandardObjects();
		
		Document document  = null;
		DomParser domParser = new DomParser();
		document = domParser.parseTree(this.xmlString1);
		StringBuffer buffer = new StringBuffer();
		document.toXmlString(buffer);
		System.out.println("XML:"+buffer.toString());
		scope.put("document", scope, document);
		
		String code = "document.getElementById(\"myA\").nodeType";
		Script script = context.compileString(code, "<cmd>", 1);
		
		Object result = script.exec(context, scope);
		
		System.out.println(this.xmlString1);
		System.out.println(code);
		System.out.println(result);
		System.out.println("END");
	}
	
	public void testPut() {
		Context context;
		context = Context.enter();
		Scriptable scope = context.initStandardObjects();
		
		Document document  = null;
		DomParser domParser = new DomParser();
		document = domParser.parseTree(this.xmlString1);
		
		scope.put("document", scope, document);

		String code = "document.nodeValue = \"Hello\"";
		Script script = context.compileString(code, "<cmd>", 1);
		
		Object result = script.exec(context, scope);
		assertEquals("Hello", result);
		System.out.println(this.xmlString1);
		System.out.println(code);
		System.out.println(result);
		System.out.println("END");
	}
	
}
