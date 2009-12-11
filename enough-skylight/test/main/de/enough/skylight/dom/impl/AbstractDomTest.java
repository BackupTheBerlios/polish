package de.enough.skylight.dom.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.enough.polish.util.StreamUtil;
import de.enough.skylight.TestResources;
import junit.framework.TestCase;

public abstract class AbstractDomTest extends TestCase {

	protected DocumentImpl document1;
	protected DocumentImpl documentJsEvent;

	public AbstractDomTest() {
		super();
	}

	public AbstractDomTest(String name) {
		super(name);
	}

	@Override
	public void setUp() {
		DomParser domParser = new DomParser();
		String htmlDocument;
		String jsEventDocument;
		try {
			htmlDocument = extractString(TestResources.HTML_PAGE_1);
			jsEventDocument = extractString(TestResources.HTML_PAGE_JS_EVENT);
		} catch (IOException e) {
			throw new RuntimeException(e.toString());
		}
		this.document1 = (DocumentImpl)domParser.parseDocument(htmlDocument);
		this.documentJsEvent = (DocumentImpl)domParser.parseDocument(jsEventDocument);
		assertNotNull(this.document1);
	}

	private String extractString(String path) throws IOException, FileNotFoundException {
		return StreamUtil.getString(new FileInputStream(path), null, StreamUtil.DEFAULT_BUFFER);
	}

}