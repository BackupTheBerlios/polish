package de.enough.skylight.renderer.node;

import de.enough.ovidiu.Box;
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
import de.enough.skylight.renderer.node.handler.html.BrHandler;
import de.enough.skylight.renderer.node.handler.html.ImgHandler;
import de.enough.skylight.renderer.node.handler.html.TextHandler;

public class CssElement implements HtmlCssElement, NodeInterface{
	
	public final static int CONTENT_CONTAINING_ELEMENT = 0x00;
	public final static int CONTENT_TEXT = 0x01;
	public final static int CONTENT_IMAGE = 0x02;
	public final static int CONTENT_CONTAINING_TEXT = 0x03;

        public final static int CONTENT_BR = 0x04;

        public Box box = null ;
	
	String display = HtmlCssElement.Display.INLINE;
	
	String position = HtmlCssElement.Position.STATIC;
	
	String floating = HtmlCssElement.Float.NONE;
	
	String textAlign = HtmlCssElement.TextAlign.LEFT;
	
	String verticalAlign = HtmlCssElement.VerticalAlign.TOP;
	
	ViewportContext viewportContext;
	
	NodeHandler handler;
	
	DomNodeImpl node;
	
	ContainingBlock block;
	
	Item item;
	
	Style style;
	
	Style textStyle;
	
	CssElement parent;
	
	ArrayList children;
	
	boolean interactive;
	
	public String getValue()
	{
		return getNode().getNodeValue();
	}	
	
	public static CssElement getElementWithNode(CssElement root, DomNode node) {
		if(root.hasElements()) {
			for (int i = 0; i < root.size(); i++) {
				CssElement child = root.get(i);
				if(child.hasNode(node)) {
					return child;
				} else {
					CssElement found = getElementWithNode(child,node);
					if(found != null) {
						return found;
					}
				}
			}
		}
		
		return null;
	}
	
	public CssElement(NodeHandler handler, DomNodeImpl node, CssElement parent, ViewportContext viewportContext) {
		this.handler = handler;
		
		this.node = node;
		
		this.parent = parent;
		
		this.children = new ArrayList();
		
		this.viewportContext = viewportContext;
	}
	
	public void build() throws ClassCastException, IllegalArgumentException {
		this.handler.handle(this);
		
		buildStyle();
		
		buildItem();
	}
	
	protected void buildStyle() {
		this.style = CssStyle.getStyle(this);
		
		setStyle(this.style);

		if(this.handler instanceof TextHandler) {
			this.textStyle = this.parent.getTextStyle();
		} else {
			if(this.parent != null) {
				Style parentTextStyle = this.parent.getTextStyle();
				
				this.textStyle = CssStyle.createTextStyle(parentTextStyle, this.style);
			} else {
				this.textStyle = CssStyle.createTextStyle(null, this.style);
			}
		}
	}
	
	protected void buildItem() {
		this.item = createItem();
				
		if(this.node.hasChildNodes()) {
			this.block = createContainingBlock();
			
			if(this.interactive) {
				//#debug sl.debug.event
				System.out.println("element " + this + " is interactive");
				this.block.setAppearanceMode(Item.INTERACTIVE);
			} 
		}
	}
	
	public void update() {
		this.handler.handle(this);
		
		this.style = CssStyle.getStyle(this);
		
		setStyle(this.style);

		if(this.item != null) {
			this.handler.setContent(this, this.item);
			this.item.requestInit();
			this.item.getScreen().repaint();
		}
		
		if(this.block != null) {
			setContainingBlock(this.block);
			this.block.requestInit();
			this.block.getScreen().repaint();
		}
	}
	
	public void setContainingBlock(ContainingBlock containingBlock) {
		if(containingBlock != null) {
			CssStyle.apply(this.style, containingBlock);
			
			if(this.interactive) {
				//#debug sl.debug.event
				System.out.println("element " + this + " is interactive");
				containingBlock.setAppearanceMode(Item.INTERACTIVE);
			} else {
				containingBlock.setAppearanceMode(Item.PLAIN);
			}
		}
	}
	
	public void add(CssElement child) {
		this.children.add(child);
	}
	
	public void remove(CssElement child)
	{
		this.children.remove(child);
	}
	
	public CssElement get(int index) {
		return (CssElement)this.children.get(index);
	}
	
	public int size() {
		return this.children.size();
	}
	
	public boolean hasElements() {
		return size() > 0; 
	}
		
	public DomNodeImpl getNode() {
		return this.node;
	}
	
	public Style getStyle() {
		return this.style;
	}
	
	public Style getTextStyle() {
		return this.textStyle;
	}
	
	Item createItem() {
		Item item = this.handler.createContent(this);
		
		if(item != null) {
			item.setView(new ContentView(item));
			if(isInteractive()) {
				//#debug sl.debug.event
				System.out.println("element " + this + " is interactive");
				item.setAppearanceMode(Item.INTERACTIVE);
			}
		}
		
		return item;
	}
	
	public Item getItem() {
		return this.item;
	}
	
	public NodeHandler getHandler() {
		return this.handler;
	}
	
	ContainingBlock createContainingBlock() {
		if(isFloat()) {
			return new FloatContainingBlock(this.style);
		} else if(isDisplay(HtmlCssElement.Display.BLOCK_LEVEL)) {
			return new BlockContainingBlock(this.style);
		} else if(isDisplay(HtmlCssElement.Display.INLINE_BLOCK)) {
			return new InlineContainingBlock(this.style);
		} else if(isDisplay(HtmlCssElement.Display.LIST_ITEM)) {
			return new BlockContainingBlock(this.style);
		}else {
			return new InlineContainingBlock(this.style);
		} 
	}
	
	public ContainingBlock getContainingBlock() {
		return this.block;
	}
	
	public void setStyle(Style style) {
		String displayStr = style.getProperty("display");
		if(displayStr != null) {
			this.display = displayStr;
		}
		
		String positionStr = style.getProperty("position");
		if(positionStr != null) {
			this.position = positionStr;
		}
		
		String floatStr = style.getProperty("float");
		if(floatStr != null) {
			this.floating = floatStr;
		}
		
		String textAlignStr = style.getProperty("text-align");
		if(textAlignStr != null) {
			this.textAlign = textAlignStr;
		}
		
		String verticalAlignStr = style.getProperty("vertical-align");
		if(verticalAlignStr != null) {
			this.verticalAlign = verticalAlignStr;
		}
	}

	public boolean isDisplay(String display) {
		return this.display == display;
	}
	
	public boolean isPosition(String position) {
		return this.position == position;
	}
	
	public boolean isFloat(String floating) {
		return this.floating == floating;
	}

	public boolean isTextAlign(String textAlign) {
		return this.textAlign == textAlign;
	}
	
	public boolean isVerticalAlign(String verticalAlign) {
		return this.verticalAlign == verticalAlign;
	}
	
	public boolean isFloat() {
		return this.floating != HtmlCssElement.Float.NONE;
	}
	
	public boolean isParentFloat() {
		return this.parent.isFloat();
	}
	
	public boolean isNodeType(int type) {
		return this.node.getNodeType() == type;
	}
	
	public CssElement getParent() {
		return this.parent;
	}
	
	public ArrayList getChildren() {
		return this.children;
	}

	public void setViewportContext(ViewportContext viewportContext) {
		this.viewportContext = viewportContext;
	}
	
	public ViewportContext getViewportContext() {
		return this.viewportContext;
	}

	public boolean isInteractive() {
		return this.interactive;
	}
	
	public void setValue(String value)
	{
		getNode().setNodeValue(value);
	}

	public void setInteractive(boolean interactive) {
		this.interactive = interactive;
	}
	
	public boolean hasNode(DomNode node) {
		if(node == null || this.node == null) {
			return false;
		} else {
			return node.equals(this.node);
		}
	}

	public Object getContent() {
		// TODO Auto-generated method stub
		return getValue();
	}
	
	public void setContent(String value)
	{
		getNode().setNodeValue(value);
	}
	
	public int getContentType() {
		// TODO Auto-generated method stub
		
		if ( this.handler instanceof ImgHandler)
		{
			return CONTENT_IMAGE;
		}
		else if ( this.handler instanceof TextHandler )
		{
			if ( this.getChildren().size() == 0 )
			{
				return CONTENT_CONTAINING_TEXT ;
			}
		}
                else if ( this.handler instanceof BrHandler )
                {
                    System.out.println("BR");
                    return CONTENT_BR ;
                }
		
		return CONTENT_CONTAINING_ELEMENT;
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringHelper("CssElement").
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
