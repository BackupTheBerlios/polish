package de.enough.skylight.renderer.node;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.ToStringHelper;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.ViewportContext;
import de.enough.skylight.renderer.css.HtmlCssElement;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.element.InlineContainingBlock;
import de.enough.skylight.renderer.element.view.ContentView;
import de.enough.skylight.renderer.node.handler.html.TextHandler;

public class CssElement implements HtmlCssElement{
	String display = HtmlCssElement.Display.INLINE;
	
	String position = HtmlCssElement.Position.STATIC;
	
	String floating = HtmlCssElement.Float.NONE;
	
	String textAlign = HtmlCssElement.TextAlign.LEFT;
	
	String verticalAlign = HtmlCssElement.VerticalAlign.TOP;
	
	ViewportContext viewportContext;
	
	NodeHandler handler;
	
	DomNode node;
	
	ContainingBlock block;
	
	Item content;
	
	Style style;
	
	Style textStyle;
	
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
	
	public CssElement(NodeHandler handler, DomNode node, CssElement parent, ViewportContext viewportContext) {
		this.handler = handler;
		
		this.node = node;
		
		this.parent = parent;
		
		this.children = new ArrayList();
		
		this.viewportContext = viewportContext;
	}
	
	public void build() throws ClassCastException, IllegalArgumentException {
		this.handler.handle(this);
		
		buildStyle();
		
		buildContent();
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
	
	protected void buildContent() {
		this.content = createContent();
				
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

		if(this.content != null) {
			this.handler.setContent(this, this.content);
			this.content.requestInit();
			this.content.getScreen().repaint();
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
	
	public Style getTextStyle() {
		return this.textStyle;
	}
	
	Item createContent() {
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
	
	public Item getContent() {
		return this.content;
	}
	
	public NodeHandler getHandler() {
		return this.handler;
	}
	
	ContainingBlock createContainingBlock() {
		if(isDisplay(HtmlCssElement.Display.BLOCK_LEVEL)) {
			return new BlockContainingBlock(this.style);
		} else {
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

	public void setViewportContext(ViewportContext viewportContext) {
		this.viewportContext = viewportContext;
	}
	
	public ViewportContext getViewportContext() {
		return this.viewportContext;
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
