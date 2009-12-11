package de.enough.skylight.dom.impl;

import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.Element;
import de.enough.skylight.dom.NamedNodeMap;
import de.enough.skylight.dom.NodeList;


public class DomNodeImplTest extends AbstractDomTest {

	public void testGetAttributes() {
		NamedNodeMap attributes;
		attributes = this.document1.getAttributes();
		assertNull(attributes);
		
		attributes = this.document1.getDocumentElement().getAttributes();
		assertEquals(1,attributes.getLength());
		// Attributes do not have attributes
		attributes = this.document1.getElementById("id1").getAttributes();
		assertNull(attributes.item(0).getAttributes());
	}

	public void testGetChildNodes() {
		NodeList childNodes = this.document1.getDocumentElement().getChildNodes();
		assertEquals(2, childNodes.getLength());
	}

	public void testGetFirstChild() {
		Element documentElement = this.document1.getDocumentElement();
		DomNode firstChild = documentElement.getFirstChild();
		assertEquals("head",firstChild.getNodeName());
	}

	public void testGetLastChild() {
		DomNode lastChild = this.document1.getDocumentElement().getLastChild();
		assertEquals("body",lastChild.getNodeName());
	}

	public void testGetNodeName() {
		DomNode node = this.document1.getElementById("id2");
		assertEquals("div",node.getNodeName());
	}

	public void testGetNodeType() {
		assertEquals(DomNode.DOCUMENT_NODE,this.document1.getNodeType());
		assertEquals(DomNode.ELEMENT_NODE,this.document1.getElementById("id2").getNodeType());
		assertEquals(DomNode.ATTRIBUTE_NODE,this.document1.getElementById("id2").getAttributes().item(0).getNodeType());
	}

	public void testGetOwnerDocument() {
		assertEquals(null,this.document1.getOwnerDocument());
		assertEquals(this.document1,this.document1.getDocumentElement().getOwnerDocument());
	}

	public void testHasAttributes() {
		assertEquals(false,this.document1.getDocumentElement().getFirstChild().hasAttributes());
		assertEquals(true,this.document1.getElementById("id1").hasAttributes());
	}

	public void testHasChildNodes() {
		assertEquals(true,this.document1.getDocumentElement().hasChildNodes());
		assertEquals(false,this.document1.getElementById("id1").hasChildNodes());
	}
	
	public void testGetNextSibling() {
		Element element = this.document1.getElementById("id1");
		DomNode nextSibling = element.getNextSibling();
		assertEquals("id2",nextSibling.getAttributes().item(0).getNodeValue());
	}

	public void testGetNodeValue() {
		String nodeValue = this.document1.getElementById("id1").getNodeValue();
		assertNull(nodeValue);
	}
	
//	public void testAddEventListener() {
//		fail("Not yet implemented");
//	}
//
//	public void testAppendChild() {
//		fail("Not yet implemented");
//	}
//
//	public void testCloneNode() {
//		fail("Not yet implemented");
//	}
//
//	public void testDispatchEvent() {
//		fail("Not yet implemented");
//	}
//
//	public void testInsertBefore() {
//		fail("Not yet implemented");
//	}
//
//	public void testIsSupported() {
//		fail("Not yet implemented");
//	}
//
//	public void testNormalize() {
//		fail("Not yet implemented");
//	}
//
//	public void testRemoveChild() {
//		fail("Not yet implemented");
//	}
//
//	public void testRemoveEventListener() {
//		fail("Not yet implemented");
//	}
//
//	public void testReplaceChild() {
//		fail("Not yet implemented");
//	}
//
//	public void testSetNodeValue() {
//		fail("Not yet implemented");
//	}
//
//	public void testSetPrefix() {
//		fail("Not yet implemented");
//	}
//
//	public void testGetLocalName() {
//		fail("Not yet implemented");
//	}
//
//	public void testGetNamespaceURI() {
//		fail("Not yet implemented");
//	}
//
//	public void testGetParentNode() {
//		fail("Not yet implemented");
//	}
//
//	public void testGetPrefix() {
//		fail("Not yet implemented");
//	}
//
//	public void testGetPreviousSibling() {
//		fail("Not yet implemented");
//	}

}
