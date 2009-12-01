package de.enough.skylight.dom.impl;

import de.enough.skylight.dom.Attr;
import de.enough.skylight.dom.Element;

public class ElementImplTest extends AbstractDomTest {

	public void testGetAttribute() {
		Element element = this.document1.getElementById("id2");
		String attribute = element.getAttribute("id");
		assertEquals("id2",attribute);
		assertEquals("",element.getAttribute("bjlakfjsd"));
	}

	public void testGetAttributeNode() {
		Element element = this.document1.getElementById("id2");
		Attr attribute = element.getAttributeNode("id");
		assertNotNull(attribute);
		assertEquals("id",attribute.getName());
		assertNull(element.getAttributeNode("bjlakfjsd"));
	}

	public void testGetTagName() {
		Element element = this.document1.getElementById("id2");
		String tagName = element.getTagName();
		assertEquals("div", tagName);
	}

//	public void testGetScriptable() {
//		fail("Not yet implemented");
//	}
//
//	public void testInit() {
//		fail("Not yet implemented");
//	}
//	
//	public void testGetElementsByTagName() {
//		fail("Not yet implemented");
//	}
//
//
//	public void testHasAttribute() {
//		fail("Not yet implemented");
//	}
//
//	public void testRemoveAttribute() {
//		fail("Not yet implemented");
//	}
//
//	public void testRemoveAttributeNode() {
//		fail("Not yet implemented");
//	}
//
//	public void testSetAttribute() {
//		fail("Not yet implemented");
//	}
//
//	public void testSetAttributeNode() {
//		fail("Not yet implemented");
//	}

}
