package de.enough.skylight.dom.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.enough.polish.util.StreamUtil;
import junit.framework.TestCase;

public class DocumentTest extends TestCase {

	private DocumentImpl document;

	@Override
	public void setUp() {
		DomParser domParser = new DomParser();
		String htmlDocument;
		try {
			htmlDocument = StreamUtil.getString(new FileInputStream(HtmlPages.HTML_PAGE_1), null, StreamUtil.DEFAULT_BUFFER);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.toString());
		} catch (IOException e) {
			throw new RuntimeException(e.toString());
		}
		this.document = (DocumentImpl)domParser.parseTree(htmlDocument);
		assertNotNull(this.document);
	}
	
	public void testA() throws FileNotFoundException, IOException {
		
	}
}
