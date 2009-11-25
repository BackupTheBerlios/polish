package de.enough.skylight.dom.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.enough.polish.util.StreamUtil;
import junit.framework.TestCase;

public abstract class AbstractDomTest extends TestCase {

	protected DocumentImpl document1;

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
		try {
			htmlDocument = StreamUtil.getString(new FileInputStream(TestResources.HTML_PAGE_1), null, StreamUtil.DEFAULT_BUFFER);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.toString());
		} catch (IOException e) {
			throw new RuntimeException(e.toString());
		}
		this.document1 = (DocumentImpl)domParser.parseTree(htmlDocument);
		assertNotNull(this.document1);
	}

}