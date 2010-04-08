package de.enough.skylight.js;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.mozilla.javascript.Script;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.Element;

public class DomNodeTest extends AbstractJsTest {

	public void testConstants() throws FileNotFoundException, IOException {
		Script script;
		Object result;
		script = this.context.compileString("var element = document.getElementById('id2');element.ELEMENT_NODE", "test1", 1);
		result = script.exec(this.context, this.scope);
		assertEquals(new Integer(DomNode.ELEMENT_NODE),result);

		script = this.context.compileString("var element = document.getElementById('id2');element.ATTRIBUTE_NODE", "test1", 1);
		result = script.exec(this.context, this.scope);
		assertEquals(new Integer(DomNode.ATTRIBUTE_NODE),result);

		script = this.context.compileString("var element = document.getElementById('id2');element.CDATA_SECTION_NODE", "test1", 1);
		result = script.exec(this.context, this.scope);
		assertEquals(new Integer(DomNode.CDATA_SECTION_NODE),result);
		
		script = this.context.compileString("var element = document.getElementById('id2');element.COMMENT_NODE", "test1", 1);
		result = script.exec(this.context, this.scope);
		assertEquals(new Integer(DomNode.COMMENT_NODE),result);
		
		script = this.context.compileString("var element = document.getElementById('id2');element.DOCUMENT_NODE", "test1", 1);
		result = script.exec(this.context, this.scope);
		assertEquals(new Integer(DomNode.DOCUMENT_NODE),result);
		
		script = this.context.compileString("var element = document.getElementById('id2');element.PROCESSING_INSTRUCTION_NODE", "test1", 1);
		result = script.exec(this.context, this.scope);
		assertEquals(new Integer(DomNode.PROCESSING_INSTRUCTION_NODE),result);
		
		script = this.context.compileString("var element = document.getElementById('id2');element.TEXT_NODE", "test1", 1);
		result = script.exec(this.context, this.scope);
		assertEquals(new Integer(DomNode.TEXT_NODE),result);
	}
	
	public void testNodeName() {
		Script script = this.context.compileString("var element = document.getElementById('id2'); Assert.equals('div',element.nodeName)", "test1", 1);
		script.exec(this.context, this.scope);
	}
	
	public void testNodeValue() {
		Script script = this.context.compileString("var element = document.getElementById('id2'); element.nodeValue = 'a'", "test1", 1);
		script.exec(this.context, this.scope);
		Element element = this.document1.getElementById("id2");
		String nodeValue = element.getNodeValue();
		assertEquals("a",nodeValue);
	}
	
	/*
	 * TODO: This should fail with en exception.
	 */
	public void testNodeNameReadOnly() {
		Script script = this.context.compileString("var element = document.getElementById('id2'); element.nodeName = 'bla'; Assert.equals('div',element.nodeName)", "test1", 1);
		script.exec(this.context, this.scope);
	}
	
	public void testChildNodes() {
		Script script = this.context.compileString("var element = document.getElementById('id2'); Assert.equals(1,element.childNodes.length)", "test1", 1);
		script.exec(this.context, this.scope);
	}
	
	public void testNextSibling() {
		Script script = this.context.compileString("var element = document.getElementById('id1'); nextSiblingId = element.nextSibling.getAttribute('id'); Assert.equals('id2',nextSiblingId)", "test1", 1);
		script.exec(this.context, this.scope);
	}
	
	public void testInsertBefore() {
		Script script = this.context.compileString("var parentElement = document.getElementById('id2'); var childElement = document.getElementById('id3'); var newElement = document.createElement('NEWCHILD'); parentElement.insertBefore(newElement,childElement); Assert.equals('NEWCHILD',parentElement.firstChild.tagName)", "test1", 1);
		script.exec(this.context, this.scope);
	}
	
	
	
}
