package de.enough.skylight.renderer.node;

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.TextUtil;
import de.enough.polish.util.ToStringHelper;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.impl.DomNodeImpl;
import de.enough.skylight.modeller.Box;
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

public class CssElement implements HtmlCssElement{
	
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
	
	final short FOCUSED_STYLE_KEY = 1;
	
	//#style element
	final Style defaultStyle = new Style();
	final Style defaultFocusedStyle = getFocusedStyle(this.defaultStyle);
	//#style text
	final Style defaultTextStyle =  new Style();
	
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
		this.style = getStyle(this);
		
		setStyle(this.style);

		if(this.handler instanceof TextHandler) {
			this.textStyle = this.parent.getTextStyle();
		} else {
			if(this.parent != null) {
				Style parentTextStyle = this.parent.getTextStyle();
				
				this.textStyle = createTextStyle(parentTextStyle, this.style);
			} else {
				this.textStyle = createTextStyle(null, this.style);
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
		
		this.style = getStyle(this);
		
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
			containingBlock.setStyle(this.style);
			
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
		set("handler", this.handler).
		set("node", this.node).
		set("interactive", this.interactive).
		set("children.size", this.children.size()).
		set("float", this.floating).
		set("position", this.position).
		set("display", this.display).
		set("text-align", this.textAlign).
		set("vertical-align", this.verticalAlign).
		toString();
	}
	
	private Style createTextStyle(Style baseStyle, Style extendStyle) {
		if(baseStyle != null && extendStyle != null) {
			if(baseStyle.name.equals(extendStyle.name)) {
				return baseStyle;
			}
		}
		
		//#mdebug sl.debug.style
		if(baseStyle != null) {
			System.out.println("creating text style : " + baseStyle.name + " / " + extendStyle.name);
		} else {
			System.out.println("creating text style from " + extendStyle.name);
		}
		//#enddebug
		
		Style resultStyle = new Style(defaultTextStyle);
		
		// get the attributes to extend the style 
		/*String x = extendStyle.getProperty("float");
		String y = extendStyle.getProperty("display");*/
		Color extendFontColor = extendStyle.getColorProperty("font-color");
		Integer extendFontStyle = extendStyle.getIntProperty("font-style");
		Integer extendFontSize = extendStyle.getIntProperty("font-size"); 
		Style extendFocusedStyle = getFocusedStyle(extendStyle);
               

                /*
                extendFontSize = extendStyle.getIntProperty("margin-left");
                extendFontSize = extendStyle.getIntProperty("margin-right");
                extendFontSize = extendStyle.getIntProperty("margin-top");
                extendFontSize = extendStyle.getIntProperty("margin-bottom");

                extendFontSize = extendStyle.getIntProperty("padding-left");
                extendFontSize = extendStyle.getIntProperty("padding-right");
                extendFontSize = extendStyle.getIntProperty("padding-top");
                extendFontSize = extendStyle.getIntProperty("padding-bottom"); */
		
		// if a base style is given ...
		if(baseStyle == null) {
			resultStyle.name = extendStyle.name;
			resultStyle.addAttribute("font-color", extendFontColor);
			resultStyle.addAttribute("font-style", extendFontStyle);
			resultStyle.addAttribute("font-size", extendFontSize);	
			resultStyle.addAttribute("focused-style", extendFocusedStyle);
		} else {
			// get default size and color from text style
			Integer defaultFontSize = resultStyle.getIntProperty("font-size");
			Color defaultFontColor = resultStyle.getColorProperty("font-color");
			
			// get font attributes from the base style
			Color baseFontColor = baseStyle.getColorProperty("font-color");
			Integer baseFontStyle = baseStyle.getIntProperty("font-style");
			Integer baseFontSize = baseStyle.getIntProperty("font-size");
			
			Color resultTextColor = (Color) extendValue(baseFontColor, extendFontColor, defaultFontColor);
			Integer resultTextStyle = extendFlag(baseFontStyle, extendFontStyle);
			Integer resultTextSize = (Integer) extendValue(baseFontSize, extendFontSize, defaultFontSize);
			
			Style baseFocusedStyle = getFocusedStyle(baseStyle);
			
			// add the resulting attributes to the text style
			resultStyle.name = baseStyle.name + "." + extendStyle.name;
			resultStyle.addAttribute("font-color", resultTextColor);
			resultStyle.addAttribute("font-style", resultTextStyle);
			resultStyle.addAttribute("font-size", resultTextSize);
			
			// extend the focused style
			if(extendFocusedStyle != null) {
				Style resultFocusedStyle = createTextStyle(baseFocusedStyle,extendFocusedStyle);
				resultStyle.addAttribute("focused-style", resultFocusedStyle);
			} 
		}
		return resultStyle;
	}
	
	private Integer extendFlag(Integer baseFlag, Integer extendFlag ) {
		if(extendFlag == null) {
			return baseFlag;
		} else {
			// extend the flags
			return new Integer( baseFlag.intValue() | extendFlag.intValue() );
		}
	}
	
	private Object extendValue(Object baseValue, Object extendValue, Object defaultValue) {
		// if the value to extend is not the default value ...
		Object resultValue = defaultValue;
		if(extendValue != null && !extendValue.equals(defaultValue)) {
			// set it as the result value
			resultValue = extendValue;
		} else {
			// set the base value as the result value
			resultValue = baseValue;
		}
		
		return resultValue;
	}
	
	private Style getFocusedStyle(Style style) {
		return (Style)style.getObjectProperty("focused-style");
	}
	
	private Style createStyle(Style baseStyle, Style extendStyle) {
		if(baseStyle != null && extendStyle != null) {
			if(baseStyle.name.equals(extendStyle.name)) {
				return baseStyle;
			}
		}
		
		//#debug sl.debug.style
		System.out.println("extending style " + baseStyle.name + " with style " + extendStyle.name);
		
		Style result = new Style(baseStyle);
		
		result.name = baseStyle.name + "." + extendStyle.name;
		
		if(extendStyle.layout != Item.LAYOUT_DEFAULT) {
			result.layout = extendStyle.layout;
		}
		
		if(extendStyle.border != null) {
			result.border = extendStyle.border;
		}
				
		if(extendStyle.background != null) {
			result.background = extendStyle.background;
		}
		
		Style baseFocusedStyle = (Style) baseStyle.getObjectProperty("focused-style");
		short[] keys = extendStyle.getRawAttributeKeys();
		
		if(keys != null) {
			for (int i = 0; i < keys.length; i++) {
				short key = keys[i];
				Object value = extendStyle.getObjectProperty(key);
				
				if(key == FOCUSED_STYLE_KEY && baseFocusedStyle != null && value != null) {
					Style extendFocusedStyle = (Style)value;
					Style resultFocusedStyle = createStyle(baseFocusedStyle, extendFocusedStyle);
					result.addAttribute(key, resultFocusedStyle);
				} else
				{
					if(value != null) {
						result.addAttribute(key, value);
					}
				}
			}
		}
		
		return result;
	}
	
	private Style getStyle(CssElement element) {
		NodeHandler handler = element.getHandler();
		DomNode node = element.getNode();
		
		String clazz = NodeUtils.getAttributeValue(node, "class");
		
		Style style = handler.getDefaultStyle(element);
		
		if(clazz != null) {
                        String [] classes = TextUtil.split(clazz, ' ');
                        int size = classes.length;
                        for (int i=0;i<size;i++)
                        {
                            Style classStyle = StyleSheet.getStyle(classes[i]);

                            if(classStyle != null) {
                                    //#debug sl.debug.style
                                    System.out.println("create style for " + handler.getTag() + " : " + classes[i]);

                                    style = createStyle(style, classStyle);
                            } else {
                                    //#debug error
                                    System.out.println("style " + classes[i] + " could not be found");
                            }
                        }
                        return style;

		}
		
		//#debug sl.debug.style
		System.out.println("returning default style for " + handler.getTag() + " : " + style.name);
		
		return style;
	}
	
}
