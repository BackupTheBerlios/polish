package de.enough.skylight.renderer.node;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.ToStringHelper;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.css.HtmlCssElement;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.element.InlineContainingBlock;

public class CssElement implements HtmlCssElement{
	String display = HtmlCssElement.Display.INLINE;
	
	String position = HtmlCssElement.Position.STATIC;
	
	String floating = HtmlCssElement.Float.NONE;
	
	String textAlign = HtmlCssElement.TextAlign.LEFT;
	
	String verticalAlign = HtmlCssElement.VerticalAlign.TOP;
	
	Viewport viewport;
	
	NodeHandler handler;
	
	DomNode node;
	
	ContainingBlock block;
	
	Item content;
	
	Style style;
	
	CssElement parent;
	
	ArrayList children;
	
	boolean interactive;
	
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
	
	public CssElement(NodeHandler handler, DomNode node, CssElement parent, Viewport viewport) {
		this.handler = handler;
		
		this.node = node;
		
		this.parent = parent;
		
		this.children = new ArrayList();
		
		this.viewport = viewport;
	}
	
	public void build() throws ClassCastException, IllegalArgumentException {
		this.handler.handle(this);
		
		this.style = CssStyle.getStyle(this);
		
		setStyle(this.style);
		
		this.content = createContent();
		
		if(this.node.hasChildNodes()) {
			this.block = createContainingBlock();
		}
	}
	
	public void update() {
		this.handler.handle(this);
		
		this.style = CssStyle.getStyle(this);
		
		setStyle(this.style);

		if(this.content != null) {
			this.handler.setContent(this, this.content);
			this.content.requestInit();
			this.content.getScreen().repaint();
		}
		
		if(this.block != null) {
			setContainingBlock(this.block);
			Container container = this.block.getContainer();
			container.requestInit();
			container.getScreen().repaint();
		}
	}
	
	public void setContainingBlock(ContainingBlock containingBlock) {
		if(containingBlock != null) {
			Container container = containingBlock.getContainer();
			CssStyle.apply(this.style, container);
			
			if(this.interactive) {
				//#debug sl.debug.event
				System.out.println("element " + this + " is interactive");
				container.setAppearanceMode(Item.INTERACTIVE);
			} else {
				container.setAppearanceMode(Item.PLAIN);
			}
		}
	}
	
	public void add(CssElement child) {
		this.children.add(child);
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
		
	public DomNode getNode() {
		return this.node;
	}
	
	public Style getStyle() {
		return this.style;
	}
	
	Item createContent() {
		Item item = this.handler.createContent(this);
		
		if(item != null) {
//			ElementView view = new ElementView();
//			item.setView(view);
			
			if(isInteractive()) {
				//#debug sl.debug.event
				System.out.println("element " + this + " is interactive");
				item.setAppearanceMode(Item.INTERACTIVE);
			}
		}
		
		return item;
	}
	
	public Item getContent() {
		return this.content;
	}
	
	public NodeHandler getHandler() {
		return this.handler;
	}
	
	ContainingBlock createContainingBlock() {
		if(isDisplay(HtmlCssElement.Display.BLOCK_LEVEL)) {
			BlockContainingBlock block = new BlockContainingBlock(this, this.style);
			if(this.interactive) {
				//#debug sl.debug.event
				System.out.println("element " + this + " is interactive");
				block.getContainer().setAppearanceMode(Item.INTERACTIVE);
			} 
			return block;
		} else {
			InlineContainingBlock block = new InlineContainingBlock(this, this.style);
			if(this.interactive) {
				//#debug sl.debug.event
				System.out.println("element " + this + " is interactive");
				block.getContainer().setAppearanceMode(Item.INTERACTIVE);
			} 
			return block;
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

	public void setViewport(Viewport viewport) {
		this.viewport = viewport;
	}
	
	public Viewport getViewport() {
		return this.viewport;
	}

	public boolean isInteractive() {
		return this.interactive;
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
