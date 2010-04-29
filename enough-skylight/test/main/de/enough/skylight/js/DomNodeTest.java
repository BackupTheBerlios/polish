package de.enough.skylight.js;

import java.io.FileNotFoundException;
import java.io.IOException;

import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.Element;

public class DomNodeTest extends AbstractJsTest {

	public void testConstants() throws FileNotFoundException, IOException {
		Object result = this.jsEngine.runScript("var element = document.getElementById('id2');element.ELEMENT_NODE");
		assertEquals(new Integer(DomNode.ELEMENT_NODE),result);
	}
	
	public void testNodeName() {
		this.jsEngine.runScript("var element = document.getElementById('id2'); Assert.equals('div',element.nodeName)");
	}
	
	// TODO: This tests fails because the prototyping does not work.
	public void testNodeValue() {
		this.jsEngine.runScript("var element = document.getElementById('id2'); element.nodeValue = 'a'");
		Element element = this.document1.getElementById("id2");
		String nodeValue = element.getNodeValue();
		assertEquals("a",nodeValue);
	}
	
	/*
	 * TODO: This should fail with en exception.
	 */
	public void testNodeNameReadOnly() {
		this.jsEngine.runScript("var element = document.getElementById('id2'); element.nodeName = 'bla'; Assert.equals('div',element.nodeName)");
	}
	
	public void testChildNodes() {
		Object result = this.jsEngine.runScript("var element = document.getElementById('id2'); element.childNodes.length");
		assertEquals(new Integer(1), result);
	}
	
	public void testNextSibling() {
		this.jsEngine.runScript("var element = document.getElementById('id1'); nextSiblingId = element.nextSibling.getAttribute('id'); Assert.equals('id2',nextSiblingId)");
	}
	
	public void testInsertBefore() {
		this.jsEngine.runScript("var parentElement = document.getElementById('id2'); var childElement = document.getElementById('id3'); var newElement = document.createElement('NEWCHILD'); parentElement.insertBefore(newElement,childElement); Assert.equals('NEWCHILD',parentElement.firstChild.tagName)");
	}
	
	
	
}
