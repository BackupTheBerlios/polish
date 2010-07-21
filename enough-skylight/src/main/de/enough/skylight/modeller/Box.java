package de.enough.skylight.modeller;

import de.enough.polish.util.ArrayList;
import de.enough.skylight.renderer.node.CssElement;

public class Box {

	public int absoluteX = 0;
	public int absoluteY = 0;

	public int x = 0;
	public int y = 0;
	public int contentWidth = 0;
	public int contentHeight = 0;

	public int marginLeft = 0;
	public int marginTop = 0;
	public int marginRight = 0;
	public int marginBottom = 0;

	public int paddingLeft = 0;
	public int paddingTop = 0;
	public int paddingRight = 0;
	public int paddingBottom = 0;

	public int borderLeft = 0;
	public int borderTop = 0;
	public int borderRight = 0;
	public int borderBottom = 0;

	public boolean clearLeft = false;
	public boolean clearRight = false;

	public boolean isFirstItemOnRow = false;

	public ArrayList children = new ArrayList();

	public CssElement correspondingNode = null;

	// On-screen parent
	public Box parent = null;

	// Actual DOM parent (if different than the on-screen parent, otherwise null
	// - for inline, this can be different fromt the on-screen parent)
	public Box DOMParent = null;

	public String comment = "";

	public boolean hasDynamicSize = false;
	public boolean useAllAvailableWidth = false;

	public boolean isInline = false;
	public boolean isFloat = false;
	public boolean isBlock = false;
	public boolean isListItem = false;
	public boolean firstElementInInline = false;
	public boolean lastElementInInline = false;

	public Box(CssElement correspondingNode) {
		this.correspondingNode = correspondingNode;
	}

	public void addChild(Box b) {
		b.parent = this;
		this.children.remove(b);
		this.children.add(b);
	}

	public void removeChild(Box b) {
		b.parent = null;
		this.children.remove(b);
	}

	public void removeAllChildren() {
		this.children.clear();
	}

	public int getAbsoluteX() {
		if (this.parent == null) {
			return 0;
		} else {
			return this.x + this.parent.getAbsoluteX() + this.parent.marginLeft
					+ this.parent.paddingLeft;
		}
	}

	public int getAbsoluteY() {
		if (this.parent == null) {
			return 0;
		} else {
			return this.y + this.parent.getAbsoluteY() + this.parent.marginTop
					+ this.parent.paddingTop;
		}
	}

	public int getTotalWidth() {
		if (this.contentWidth < 0 || this.paddingLeft < 0 || this.marginLeft < 0) {
			//
		}
		return this.contentWidth + this.paddingLeft + this.paddingRight + this.marginLeft
				+ this.marginRight + this.borderLeft + this.borderRight;
	}

	public int getTotalHeight() {
		return this.contentHeight + this.paddingTop + this.paddingBottom + this.marginBottom
				+ this.marginTop + this.borderBottom + this.borderTop;
	}

	public int getContentWidthFromTotalWidth(int totalWidth) {
		return totalWidth - this.paddingLeft - this.paddingRight - this.marginLeft
				- this.marginRight;
	}
	
	@Override
	public String toString() {
		return "Box[x:"+this.x+",y:"+this.y+",abX:"+this.absoluteX+",abY:"+this.absoluteY+",w:"+this.contentWidth+",h:"+this.contentHeight+",p:"+this.paddingLeft+";"+this.paddingTop+";"+this.paddingRight+";"+this.paddingBottom+",m:"+this.marginLeft+";"+this.marginTop+";"+this.marginRight+";"+this.marginBottom+",#kids:"+this.children.size()+"]";
	}
}
