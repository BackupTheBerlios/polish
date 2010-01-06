//#condition polish.TextField.useVirtualKeyboard
package de.enough.polish.ui.keyboard;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.TextUtil;

public class KeyItem extends Item 
{
	public static final int POSITION_ROW = 0;
	public static final int POSITION_INDEX = 1;
	
	public static final char KEY_UNDEFINED = ' ';
	
	static final int DOUBLECLICK_INTERVAL = 1000;
	
	Keyboard keyboard;
	
	String keys;
	
	String keysUpper;
	
	char currentKey; 
	
	int currentIndex;
	
	int[] position;
	
	int stringWidth;
	
	int stringHeight;
	
	Font font;
	
	long lastTimeKeyReleased = -1;
	
	public KeyItem(Keyboard keyboard, String position, String keys) {
		this(keyboard, position, keys, null);
	}

	public KeyItem(Keyboard keyboard, String position, String keys, Style style) {
		super(style);
		
		setAppearanceMode(Item.INTERACTIVE);
		
		this.keyboard = keyboard;
		
		this.keys = keys;
		
		this.currentIndex = 0;
		
		this.position = getPosition(position);
	}
	
	int[] getPosition(String positionString) {
		String[] splitted = TextUtil.split(positionString, ',');
		
		int[] position = new int[2];
		
		position[POSITION_ROW] = Integer.parseInt(splitted[POSITION_ROW]);
		position[POSITION_INDEX] = Integer.parseInt(splitted[POSITION_INDEX]);
		
		return position;
	}
	
	protected String createCssSelector() {
		return null;
	}

	public Font getFont() {
		if (this.font == null) {
			if (this.style != null) {
				this.font = this.style.getFont();
			}
			if (this.font == null) {
				this.font = Font.getDefaultFont();
			}
		}
		return this.font;
	}

	protected void initContent(int firstLineWidth, int availWidth,
			int availHeight) {
		this.stringWidth = getFont().stringWidth(getKeys(true));
		this.contentHeight = getFont().getHeight();
		this.contentWidth = this.stringWidth;
	}

	protected void paintContent(int x, int y, int leftBorder, int rightBorder,
			Graphics g) {
		if (this.style != null) {
			g.setColor(this.style.getFontColor());
		} else {
			g.setColor(0x000000);
		}
		
		int width = rightBorder - leftBorder;
		
		x = (x + (width / 2)) - this.stringWidth / 2;
		
		g.drawString(getKeys(this.keyboard.isShift()), x, y, Item.LAYOUT_DEFAULT);
	}
	
	String getKeys(boolean shift) {
		if(shift) {
			if(this.keysUpper == null) {
				this.keysUpper = this.keys.toUpperCase();
			}
			return this.keysUpper;
		}
		else {
			return this.keys;
		}
	}
	
	public char getCurrentKey(boolean shift) {
		return getKeys(shift).charAt(this.currentIndex);
	}
	
	protected Keyboard getKeyboard() {
		return this.keyboard;
	}
	
	public int getRow() {
		return this.position[POSITION_ROW];
	}
	
	public int getIndex() {
		return this.position[POSITION_INDEX];
	}
	
	protected void apply(boolean doubleclick) {
		char key = getCurrentKey(this.keyboard.isShift());
		
		this.keyboard.apply(key, doubleclick, isMultiple());
	}
	
	public String toString() {
		return "KeyItem [" + this.keys + " : " + getRow() + "/" + getIndex() + "]";
	}
	
	protected boolean handlePointerReleased(int relX, int relY) {
		if(this.isInItemArea(relX, relY)) {
			super.handlePointerReleased(relX, relY);
		
			boolean multipleClick = isMultipleClick();
			
			if(multipleClick)
			{
				this.currentIndex = (this.currentIndex + 1) % this.keys.length();
			} else {
				this.currentIndex = 0;
			}
			
			apply(multipleClick);
			
			this.keyboard.lastKey = this;
			
			return true;
		} else {
			return false;
		}
	}
	
	boolean isMultipleClick() {
		long time = System.currentTimeMillis();
		if(this.lastTimeKeyReleased != -1) {
			long diff = time - this.lastTimeKeyReleased;
			this.lastTimeKeyReleased = time;
			if(diff < DOUBLECLICK_INTERVAL && this.keyboard.lastKey == this) {
				return true;
			}
		} else {
			this.lastTimeKeyReleased = time;
		}

		return false;
	}
	
	boolean isMultiple() {
		return this.keys.length() > 1;
	}
}
