package de.enough.skylight.dom.impl;

import de.enough.skylight.dom.Element;

public class DocumentImplTest extends AbstractDomTest {

	public void testGetDocumentElement() {
		assertNotNull(this.document1.getDocumentElement());
	}

	public void testGetElementById() {
		Element node;
		node = this.document1.getElementById("id2");
		assertNotNull(node);
		node = this.document1.getElementById("akfhjo th");
		assertNull(node);
	}

//	public void testGetElementsByTagName() {
//		fail("Not yet implemented");
//	}
//
//	public void testCreateEvent() {
//		fail("Not yet implemented");
//	}
//
//	public void testGetScriptable() {
//		fail("Not yet implemented");
//	}
//
//	public void testToXmlString() {
//		fail("Not yet implemented");
//	}
//
//	public void testCreateElement() {
//		fail("Not yet implemented");
//	}
//
//	public void testCacheNodeWithId() {
//		fail("Not yet implemented");
//	}
//
//	public void testCreateAttribute() {
//		fail("Not yet implemented");
//	}

}
