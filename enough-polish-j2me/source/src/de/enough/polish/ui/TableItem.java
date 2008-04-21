//#condition polish.usePolishGui
/*
 * Created on Apr 10, 2008 at 2:10:53 AM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.util.TableData;

/**
 * <p>Allows to manage items in a dynamic tabular layout</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TableItem 
//#if polish.LibraryBuild
	extends FakeCustomItem 
//#else
	//# extends Item
//#endif

{
	/** default style for lines between cells */
	public static int LINE_STYLE_SOLID = 0;
	/** dotted style for lines between cells */
	public static int LINE_STYLE_DOTTED = 2;
	/** no lines between cells */
	public static int LINE_STYLE_INVISIBLE = 2;
	
	private TableData tableData;
	private Font font;
	private int fontColor;
	private int[] rowHeights;
	private int[] columnWidths;
	
	private int lineStyle;
	private int lineColor = -1;
	private int completeWidth;
	
	private int xOffset;
	private int targetXOffset;

	/**
	 * Creates a new TableItem with an empty table data.
	 */
	public TableItem()
	{
		this(null, null);
	}

	/**
	 * Creates a new TableItem with an empty table data.
	 * @param style style of this item
	 */
	public TableItem(Style style)
	{
		this( null, null );
	}
	
	/**
	 * Creates a new TableItem with the specified dimensions
	 * 
	 * @param columns the number of columns 
	 * @param rows  the number of rows
	 */
	public TableItem(int columns, int rows)
	{
		this( columns, rows, null );
	}

	
	/**
	 * Creates a new TableItem with the specified dimensions
	 * 
	 * @param columns the number of columns 
	 * @param rows  the number of rows
	 * @param style the style of this item
	 */
	public TableItem(int columns, int rows, Style style)
	{
		this( new TableData( columns, rows), style );
	}

	
	/**
	 * Creates a new TableItem with an empty table data.
	 * @param tableData the data to be displayed in this table
	 */
	public TableItem(TableData tableData )
	{
		this(tableData, null);
	}
	
	/**
	 * Creates a new TableItem with an empty table data.
	 * @param tableData the data to be displayed in this table
	 * @param style style of this item
	 */
	public TableItem(TableData tableData, Style style )
	{
		super( style );
		this.tableData = tableData;
	}
	

	/**
	 * Sets the table data for this item
	 * @param tableData the new table data
	 */
	public void setTableData( TableData tableData ) {
		this.tableData = tableData;
		requestInit();
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initContent(int, int)
	 */
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		super.setStyle(style);
		this.font = style.font;
		this.fontColor = style.getFontColor();
		if (this.lineColor == -1) {
			this.lineColor = this.fontColor;
		}
	}
	
	/**
	 * Sets the line style and color.
	 * @param lineStyle the line style
	 * @param lineColor the color for lines
	 * @see #LINE_STYLE_SOLID
	 * @see #LINE_STYLE_DOTTED
	 * @see #LINE_STYLE_INVISIBLE
	 */
	public void setLineStyle( int lineStyle, int lineColor) {
		this.lineStyle = lineStyle;
		this.lineColor = lineColor;
	}

	protected void initContent(int firstLineWidth, int lineWidth)
	{
		if (this.tableData == null) {
			this.contentWidth = 0;
			this.contentHeight = 0;
			return;
		}
		if (this.font == null) {
			this.font = Font.getDefaultFont();
		}
		int numberOfColumns = this.tableData.getNumberOfColumns();
		int[] widths = new int[ numberOfColumns ];
		int numberOfRows = this.tableData.getNumberOfRows();
		int[] heights = new int[ numberOfRows ];
		int textHeight = this.font.getHeight();
		int width;
		int height;
		for (int col = 0; col < numberOfColumns; col++ ) {
			for (int row = 0; row < numberOfRows; row++ ) {
				Object data = this.tableData.get(col, row);
				if (data == null) {
					continue;
				}
				if (data instanceof Item) {
					Item item = (Item) data;
					width = item.getItemWidth(lineWidth, lineWidth);
					height = item.getItemHeight(lineWidth, lineWidth);
				} else {
					width = this.font.stringWidth( data.toString() );
					height = textHeight;
				}
				if (this.lineStyle != LINE_STYLE_INVISIBLE) {
					width += (this.paddingHorizontal << 1) + 1;
					height += (this.paddingVertical << 1) + 1;
				} else {
					width += (this.paddingHorizontal << 1);
					height += (this.paddingVertical << 1);
				}
				if (width > widths[col]) {
					widths[col] = width;
				}
				if (height > heights[row]) {
					heights[row] = height;
				}
			}
		}
		height = 0;
		width = 0;
		for (int col = 0; col < numberOfColumns; col++ ) {
			height = 0;
			for (int row = 0; row < numberOfRows; row++ ) {
				Object data = this.tableData.get(col, row);
				if (data instanceof Item) {
					Item item = (Item) data;
					item.relativeX = width;
					item.relativeY = height;
				}
				height += heights[row];
			}
			width += widths[col];
		}
		this.completeWidth = width;
		this.contentWidth = width;
		this.contentHeight = height;
		this.columnWidths = widths;
		this.rowHeights = heights;
		if (this.completeWidth > lineWidth) {
			this.appearanceMode = INTERACTIVE;
			this.contentWidth = lineWidth;
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder,
			Graphics g)
	{
		if (this.tableData == null) {
			return;
		}
		int clipX = 0;
		int clipY = 0;
		int clipWidth = 0;
		int clipHeight = 0;
		if (this.completeWidth > rightBorder - leftBorder){
			clipX = g.getClipX();
			clipY = g.getClipY();
			clipWidth = g.getClipWidth();
			clipHeight = g.getClipHeight();
			g.clipRect( x, clipY, rightBorder - leftBorder, clipHeight);
		}
		int numberOfColumns = this.tableData.getNumberOfColumns();
		int numberOfRows = this.tableData.getNumberOfRows();
		int[] widths = this.columnWidths;
		int[] heights = this.rowHeights;
		
		int height = 0;
		int width = 0;
		x += this.xOffset;
		for (int col = 0; col < numberOfColumns; col++ ) {
			height = 0;
			for (int row = 0; row < numberOfRows; row++ ) {
				Object data = this.tableData.get(col, row);
				if (data instanceof Item) {
					Item item = (Item) data;
					item.paint(x + width, y + height, x + width, x + width + widths[col] - (this.paddingHorizontal << 1), g);
//					g.setColor( 0x00ff00);
//					g.drawRect(x + width, y + height, widths[col] - (this.paddingHorizontal << 1), heights[row] - (this.paddingVertical << 1 ) );
				} else if (data != null) {
					g.setColor( this.fontColor );
					g.setFont( this.font );
					g.drawString( data.toString(), x + width, y + height, Graphics.LEFT | Graphics.TOP );
				}
				height += heights[row];
				if (this.lineStyle != LINE_STYLE_INVISIBLE && row != numberOfRows -1) {
					g.setColor( this.lineColor );
					if (this.lineStyle == LINE_STYLE_DOTTED) {
						g.setStrokeStyle( Graphics.DOTTED );
						g.drawLine( x, y + height - this.paddingVertical, x + this.completeWidth, y + height - this.paddingVertical );
						g.setStrokeStyle( Graphics.SOLID );
					} else {
						g.drawLine( x, y + height - this.paddingVertical, x + this.completeWidth, y + height - this.paddingVertical );
					}
				}
			}
			width += widths[col];
			if (this.lineStyle != LINE_STYLE_INVISIBLE && col != numberOfColumns -1) {
				g.setColor( this.lineColor );
				if (this.lineStyle == LINE_STYLE_DOTTED) {
					g.setStrokeStyle( Graphics.DOTTED );
					g.drawLine( x + width - this.paddingHorizontal, y, x + width - this.paddingHorizontal, y + this.contentHeight );
					g.setStrokeStyle( Graphics.SOLID );
				} else {
					g.drawLine( x + width - this.paddingHorizontal, y, x + width - this.paddingHorizontal, y + this.contentHeight );
				}
			}
		}
		if (this.completeWidth > rightBorder - leftBorder){
			g.setClip(clipX, clipY, clipWidth, clipHeight );
		}
	}
	
	
	

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeCustomItem#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion)
	{
		super.animate(currentTime, repaintRegion);
		//TODO animate currently selected item
		int current = this.xOffset;
		int target = this.targetXOffset;
		if (target != current) {
			int speed = (target - current) / 3;
			speed += target > current ? 1 : -1;
			current += speed;
			if ( ( speed > 0 && current > target) || (speed < 0 && current < target ) ) {
				current = target;
			}
			this.xOffset = current;
			addRelativeToContentRegion(repaintRegion, 0, 0, this.contentWidth, this.contentHeight );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeCustomItem#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction)
	{
		if (this.appearanceMode != PLAIN) {
			if (gameAction == Canvas.RIGHT 
					&& this.xOffset + this.completeWidth > this.contentWidth) 
			{
				int offset = this.targetXOffset -  (this.contentWidth >> 1);
				if (offset + this.completeWidth < this.contentWidth) {
					offset = this.contentWidth - this.completeWidth;
				}
				this.targetXOffset = offset;
				return true;
			} else if (gameAction == Canvas.LEFT 
					&& this.xOffset < 0)
			{
				int offset = this.targetXOffset + (this.contentWidth >> 1);
				if (offset > 0) {
					offset = 0;
				}
				this.targetXOffset = offset;
				return true;
			}
		}
		return super.handleKeyPressed(keyCode, gameAction);
	}
	
	
	//**************************** Table Management  ******************************************************************//
	

	/**
	 * Specifies a new dimension for this table
	 * 
	 * @param columns the number of columns 
	 * @param rows  the number of rows
	 */
	public void setDimension(int columns, int rows) {
		if (this.tableData == null) 
		{
			this.tableData = new TableData(columns, rows );
		} else {
			this.tableData.setDimension(columns, rows);
		}
		requestInit();
	}

	/**
	 * Retrieves the number of columns
	 * @return the number of columns, -1 when no table data is used
	 */
	public int getNumberOfColumns() {
		if (this.tableData == null) {
			return -1;
		}
		return this.tableData.getNumberOfColumns();
	}

	/**
	 * Retrieves the number of rows
	 * @return the number of rows, -1 when no table data is used
	 */
	public int getNumberOfRows() {
		if (this.tableData == null) {
			return -1;
		}
		return this.tableData.getNumberOfRows();
	}

	
	/**
	 * Adds a new column to this table.
	 * @return the index of the created column, the first/most left one has the index 0.
	 */
	public int addColumn() {
		if (this.tableData == null) {
			this.tableData = new TableData( 1, 0);
			requestInit();
			return 0;
		} else {
			requestInit();
			return this.tableData.addColumn();
		}
	}
	
	/**
	 * Inserts a column before the specified index.
	 * 
	 * @param index the index, use 0 for adding a column in the front, use getNumberOfColumns() for appending the column at the end
	 * @throws ArrayIndexOutOfBoundsException when the index is invalid
	 */
	public void insertColumn( int index ) {
		this.tableData.insertColumn(index);
		requestInit();
	}
	
	/**
	 * Removes the specified column
	 * 
	 * @param index the index. 0 is the first column
	 * @throws ArrayIndexOutOfBoundsException when the index is invalid
	 */
	public void removeColumn( int index ) {
		this.tableData.removeColumn(index);
		requestInit();
	}
	
	
	/**
	 * Adds a new row to this table.
	 * @return the index of the created row, the first/top one has the index 0.
	 */
	public int addRow() {
		if (this.tableData == null) {
			this.tableData = new TableData( 0, 1);
			requestInit();
			return 0;
		} else {
			requestInit();
			return this.tableData.addRow();
		}
	}
	
	/**
	 * Inserts a row before the specified index.
	 * 
	 * @param index the index, use 0 for adding a row in the top, use getNumberOfRows() for appending the column at the bottom
	 * @throws ArrayIndexOutOfBoundsException when the index is invalid
	 */
	public void insertRow( int index ) {
		this.tableData.insertRow(index);
		requestInit();
	}
	
	/**
	 * Removes the specified row
	 * 
	 * @param index the index. 0 is the first row
	 * @throws ArrayIndexOutOfBoundsException when the index is invalid
	 */
	public void removeRow( int index ) {
		this.tableData.removeRow(index);
		requestInit();
	}

	/**
	 * Sets the value of the given table position.
	 * @param column the horizontal position
	 * @param row the vertical position
	 * @param value the value, use null to delete a previous set value
	 * @throws ArrayIndexOutOfBoundsException when the column or row is invalid
	 * @see #get(int, int)
	 */
	public void set( int column, int row, Object value ) {
		this.tableData.set(column, row, value);
		repaint();
	}
	
	/**
	 * Sets the value of the given table position.
	 * @param column the horizontal position
	 * @param row the vertical position
	 * @param value the value, use null to delete a previous set value
	 * @param itemStyle the style of the added value item, if the value is not an Item it will be converted to a StringItem
	 * @throws ArrayIndexOutOfBoundsException when the column or row is invalid
	 * @see #get(int, int)
	 */
	public void set( int column, int row, Object value, Style itemStyle ) {
		if (itemStyle != null) {
			if (value instanceof Item) {
				((Item)value).setStyle(itemStyle);
			} else if (value != null) {
				StringItem item = new StringItem( null, value.toString(), itemStyle );
				value = item;
			}
		}
		this.tableData.set(column, row, value);
		repaint();
	}

	/**
	 * Sets the value of the given table position.
	 * @param column the horizontal position
	 * @param row the vertical position
	 * @return the value previously stored at the given position
	 * @throws ArrayIndexOutOfBoundsException when the column or row is invalid
	 * @see #set(int, int, Object)
	 */
	public Object get( int column, int row ) {
		return this.tableData.get(column, row);
	}
	
	


	//#ifdef polish.useDynamicStyles	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#createCssSelector()
	 */
	protected String createCssSelector()
	{
		return "table";
	}
	//#endif

}
