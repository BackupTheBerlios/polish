package de.enough.skylight.renderer.node;

import de.enough.ovidiu.NodeInterface;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.ToStringHelper;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.impl.DomNodeImpl;
import de.enough.skylight.renderer.ViewportContext;
import de.enough.skylight.renderer.css.HtmlCssElement;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.element.FloatContainingBlock;
import de.enough.skylight.renderer.element.InlineContainingBlock;
import de.enough.skylight.renderer.element.view.ContentView;
import de.enough.skylight.renderer.node.handler.html.TextHandler;

public class TextCssElement extends CssElement
{
	String myValue  = null;
	
	public TextCssElement(NodeHandler handler, DomNodeImpl node, String text, CssElement parent, ViewportContext viewportContext)
	{
		super(handler,node,parent,viewportContext);
		myValue = text;
	}
	
	
	public String getValue()
	{
		return myValue;
	}
	
	public void setValue(String value)
	{
		myValue = value;
	}
	
	public int getContentType()
	{
		return CONTENT_TEXT;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringHelper("CssTextElement").
		add("text", getValue()).
		add("handler", this.handler).
		add("node", this.node).
		add("interactive", this.interactive).
		add("children.size", this.children.size()).
		add("float", this.floating).
		add("position", this.position).
		add("display", this.display).
		add("text-align", this.textAlign).
		add("vertical-align", this.verticalAlign).
		toString();
	}
	
}
