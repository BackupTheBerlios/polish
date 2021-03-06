package de.enough.skylight.js;


import java.io.IOException;

import de.enough.skylight.Services;
import de.enough.skylight.dom.impl.AbstractDomTest;
import de.enough.skylight.dom.impl.DocumentImpl;
import de.enough.skylight.js.test.JsUnitConfigurator;
import de.enough.skylight.test.TestResources;

public abstract class AbstractJsTest extends AbstractDomTest {

	protected JsEngine jsEngine;
	protected DocumentImpl documentJsEvent;
	
	@Override
	public void setUp() {
		super.setUp();
		
		this.jsEngine = Services.getInstance().getJsEngine();
		this.jsEngine.setDocument(this.document1);
		this.jsEngine.init(new JsUnitConfigurator());
		
		String jsEventDocument;
		try {
			jsEventDocument = extractString(TestResources.HTML_PAGE_JS_EVENT);
		} catch (IOException e) {
			throw new RuntimeException(e.toString());
		}
		this.documentJsEvent = this.domParser.parseDocument(jsEventDocument);
		assertNotNull(this.document1);
	}

}