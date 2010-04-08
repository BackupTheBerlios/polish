package de.enough.skylight.js;


import java.io.IOException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import de.enough.skylight.TestResources;
import de.enough.skylight.dom.impl.AbstractDomTest;
import de.enough.skylight.dom.impl.DocumentImpl;
import de.enough.skylight.js.AssertScriptableObject;

public abstract class AbstractJsTest extends AbstractDomTest {

	protected Scriptable scope;
	protected Context context;
	protected DocumentImpl documentJsEvent;
	
	@Override
	public void setUp() {
		super.setUp();
		
		this.context = Context.enter();
		this.context.setOptionOnErrorThrowExeption(true);
		this.scope = this.context.initStandardObjects();
		AssertScriptableObject assertScriptableObject = new AssertScriptableObject();
		assertScriptableObject.init();
		this.scope.put("Assert",this.scope,assertScriptableObject);
		this.scope.put("document", this.scope, this.document1.getScriptable());
//		this.scope.put("alert", this.scope, new AlertScriptableObject());
		
		String jsEventDocument;
		try {
			jsEventDocument = extractString(TestResources.HTML_PAGE_JS_EVENT);
		} catch (IOException e) {
			throw new RuntimeException(e.toString());
		}
		this.documentJsEvent = (DocumentImpl)this.domParser.parseDocument(jsEventDocument);
		assertNotNull(this.document1);
	}

}