package de.enough.skylight.dom.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.enough.polish.util.StreamUtil;
import de.enough.skylight.test.TestResources;
import junit.framework.TestCase;

public abstract class AbstractDomTest extends TestCase {

	protected DocumentImpl document1;
	protected DomParser domParser;

	public AbstractDomTest() {
		super();
	}

	public AbstractDomTest(String name) {
		super(name);
	}

	@Override
	public void setUp() {
		this.domParser = new DomParser();
		String htmlDocument;
		try {
			htmlDocument = extractString(TestResources.HTML_PAGE_1);
		} catch (IOException e) {
			throw new RuntimeException(e.toString());
		}
		this.document1 = this.domParser.parseDocument(htmlDocument);
		assertNotNull(this.document1);
	}

	protected String extractString(String path) throws IOException, FileNotFoundException {
		return StreamUtil.getString(new FileInputStream(path), null, StreamUtil.DEFAULT_BUFFER);
	}

}