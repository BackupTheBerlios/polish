//#condition (polish.midp1 && ! polish.api.siemens-color-game-api) || (polish.usePolishGameApi == true) || ((polish.blackberry || polish.doja || polish.android) && polish.usePolishGui)

// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Sat Dec 06 15:06:44 CET 2003
/*
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.polish.ui.game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

//#if (polish.TiledLayer.splitImage == true) && polish.api.nokia-ui
	import com.nokia.mid.ui.DirectGraphics;
	import com.nokia.mid.ui.DirectUtils;
//#endif


/**
 * A TiledLayer is a visual element composed of a grid of cells that
 * can be filled with a set of
 * tile images.  This class allows large virtual layers to be created
 * without the need for an
 * extremely large Image.  This technique is commonly used in 2D
 * gaming platforms to create
 * very large scrolling backgrounds,
 * <h3>Tiles</h3>
 * The tiles used to fill the TiledLayer's cells are provided in a
 * single Image object which
 * may be mutable or immutable.  The Image is broken up into a series
 * of equally-sized tiles;
 * the tile size is specified along with the Image.  As shown in the
 * figure below, the same
 * tile set can be stored in several different arrangements depending
 * on what is the most
 * convenient for the game developer.
 * <br>
 * <center><img src="doc-files/tiles.gif" width=588 height=412
 * ALT="Tiles"></center>
 * <br>
 * Each tile is assigned a unique index number.  The tile located in
 * the upper-left corner
 * of the Image is assigned an index of 1.  The remaining tiles are
 * then numbered consecutively
 * in row-major order (indices are assigned across the first row, then
 * the second row, and so on).
 * These tiles are regarded as <em>static tiles</em> because there is
 * a fixed link between
 * the tile and the image data associated with it.
 * A static tile set is created when the TiledLayer is instantiated;
 * it can also be updated
 * at any time using the <A HREF="../../../../de/enough/polish/ui/game/TiledLayer.html#setStaticTileSet(javax.microedition.lcdui.Image, int, int)"><CODE>setStaticTileSet(javax.microedition.lcdui.Image, int, int)</CODE></A> method.
 * In addition to the static tile set, the developer can also define
 * several <em>animated tiles</em>.
 * An animated tile is a virtual tile that is dynamically associated
 * with a static tile; the appearance
 * of an animated tile will be that of the static tile that it is
 * currently associated with.
 * Animated tiles allow the developer to change the appearance of a
 * group of cells
 * very easily.  With the group of cells all filled with the animated
 * tile, the appearance
 * of the entire group can be changed by simply changing the static
 * tile associated with the
 * animated tile.  This technique is very useful for animating large
 * repeating areas without
 * having to explicitly change the contents of numerous cells.
 * Animated tiles are created using the <A HREF="../../../../de/enough/polish/ui/game/TiledLayer.html#createAnimatedTile(int)"><CODE>createAnimatedTile(int)</CODE></A>
 * method, which returns the
 * index to be used for the new animated tile.  The animated tile
 * indices are always negative
 * and consecutive, beginning with -1.  Once created, the static tile
 * associated with an
 * animated tile can be changed using the <A HREF="../../../../de/enough/polish/ui/game/TiledLayer.html#setAnimatedTile(int, int)"><CODE>setAnimatedTile(int, int)</CODE></A>
 * method.
 * <h3>Cells</h3>
 * The TiledLayer's grid is made up of equally sized cells; the number
 * of rows and
 * columns in the grid are specified in the constructor, and the
 * physical size of the cells
 * is defined by the size of the tiles.
 * The contents of each cell is specified by means of a tile index; a
 * positive tile index refers
 * to a static tile, and a negative tile index refers to an animated
 * tile.  A tile index of 0
 * indicates that the cell is empty; an empty cell is fully
 * transparent and nothing is drawn
 * in that area by the TiledLayer.  By default, all cells contain tile
 * index 0.
 * The contents of cells may be changed using <A HREF="../../../../de/enough/polish/ui/game/TiledLayer.html#setCell(int, int, int)"><CODE>setCell(int, int, int)</CODE></A> and
 * <A HREF="../../../../de/enough/polish/ui/game/TiledLayer.html#fillCells(int, int, int, int, int)"><CODE>fillCells(int, int, int, int, int)</CODE></A>.  Several
 * cells may contain the same tile; however, a single cell cannot
 * contain more than one tile.
 * The following example illustrates how a simple background can be
 * created using a TiledLayer.
 * <br>
 * <center><img src="doc-files/grid.gif" width=735 height=193
 * ALT="TiledLayer Grid"></center>
 * <br>
 * In this example, the area of water is filled with an animated tile
 * having an index of -1, which
 * is initially associated with static tile 5.  The entire area of
 * water may be animated by simply
 * changing the associated static tile using <code>setAnimatedTile(-1,
 * 7)</code>.
 * <br>
 * <center><img src="doc-files/grid2.gif" width=735 height=193
 * ALT="TiledLayer Grid 2"></center>
 * <br>
 * <h3>Rendering a TiledLayer</h3>
 * A TiledLayer can be rendered by manually calling its paint method;
 * it can also be rendered
 * automatically using a LayerManager object.
 * The paint method will attempt to render the entire TiledLayer
 * subject to the
 * clip region of the Graphics object; the upper left corner of the
 * TiledLayer is rendered at
 * its current (x,y) position relative to the Graphics object's
 * origin.  The rendered region
 * may be controlled by setting the clip region of the Graphics object
 * accordingly.
 * <p>Copyright Enough Software 2005 - 2009</p>
 * 
 * @author Robert Virkus (initial implementation)
 * @author Jan Peknik (Optimizations)
 * 
 * @since MIDP 2.0
 */
public class TiledLayer
extends Layer
{
	//#if polish.TiledLayer.splitImage == true
		//#define tmp.splitTiles
	//#endif
	//#if polish.TiledLayer.useBackBuffer == true
		//#define tmp.useBackBuffer
		//#define tmp.splitTiles
	//#endif
	protected int cellWidth;
	protected int cellHeight;
	//#ifdef polish.TiledLayer.GridType:defined
		//#= private ${ polish.TiledLayer.GridType }[][] grid;
	//#else
		private byte[][] grid;
	//#endif
	//#ifdef tmp.splitTiles
		private Image[] tiles;
	//#else
		private Image image;
		private int[] tileXPositions;
		private int[] tileYPositions;
	//#endif
	//#ifdef tmp.useBackBuffer
		//#ifdef polish.TiledLayer.GridType:defined
			//#= private ${ polish.TiledLayer.GridType }[][] bufferGrid;
		//#else
			private byte[][] bufferGrid;
		//#endif
		private int bufferFirstColumn;
		private int bufferFirstRow;
		private int bufferLastColumn;
		private int bufferLastRow;
		private Image bufferImage;
		private Graphics bufferGraphics;
		//#ifdef polish.TiledLayer.TransparentTileColor:defined
			//#= private final int bufferTransparentColor = ${polish.TiledLayer.TransparentTileColor};
		//#else
			private final int bufferTransparentColor = 0;
		//#endif
	//#endif
	//#ifdef polish.TiledLayer.GridType:defined
		//#= private ${ polish.TiledLayer.GridType }[] animatedTiles;
	//#else
		private byte[] animatedTiles;
	//#endif
	private int animatedTilesLength;
	private int numberOfTiles;
	private int numberOfColumns;
	private int gridColumns;
	private int gridRows;

	/**
	 * Creates a new TiledLayer.  <p>
	 * 
	 * The TiledLayer's grid will be <code>rows</code> cells high and
	 * <code>columns</code> cells wide.  All cells in the grid are initially
	 * empty (i.e. they contain tile index 0).  The contents of the grid may
	 * be modified through the use of <A HREF="../../../../de/enough/polish/ui/game/TiledLayer.html#setCell(int, int, int)"><CODE>setCell(int, int, int)</CODE></A> and <A HREF="../../../../javax/microedition/lcdui/game/TiledLayer.html#fillCells(int, int, int, int, int)"><CODE>fillCells(int, int, int, int, int)</CODE></A>.
	 * <P>
	 * The static tile set for the TiledLayer is created from the specified
	 * Image with each tile having the dimensions of tileWidth x tileHeight.
	 * The width of the source image must be an integer multiple of
	 * the tile width, and the height of the source image must be an integer
	 * multiple of the tile height; otherwise, an IllegalArgumentException
	 * is thrown;<p>
	 * 
	 * The entire static tile set can be changed using
	 * <A HREF="../../../../de/enough/polish/ui/game/TiledLayer.html#setStaticTileSet(javax.microedition.lcdui.Image, int, int)"><CODE>setStaticTileSet(Image, int, int)</CODE></A>.
	 * These methods should be used sparingly since they are both
	 * memory and time consuming.
	 * Where possible, animated tiles should be used instead to
	 * animate tile appearance.<p>
	 * 
	 * @param columns the width of the TiledLayer, expressed as a number of cells
	 * @param rows the height of the TiledLayer, expressed as a number of cells
	 * @param image the Image to use for creating the static tile set
	 * @param tileWidth the width in pixels of a single tile
	 * @param tileHeight the height in pixels of a single tile
	 * @throws NullPointerException if image is null
	 * @throws IllegalArgumentException if the number of rows or columns is less than 1
	 *									or if tileHeight or tileWidth is less than 1
	 *									or if the image width is not an integer multiple of the tileWidth
	 *									or if the image height is not an integer multiple of the tileHeight
	 */
	public TiledLayer(int columns, int rows, Image image, int tileWidth, int tileHeight)
	{
		//#ifdef polish.TiledLayer.GridType:defined
			//#= this.grid = new ${ polish.TiledLayer.GridType }[ columns ] [ rows ];
		//#else
			this.grid = new byte[ columns ] [ rows ];
		//#endif
		this.gridColumns = columns;
		this.gridRows = rows;
		this.width = columns * tileWidth;
		this.height = rows * tileHeight;
		setStaticTileSet(image, tileWidth, tileHeight);
	}

	/**
	 * Change the static tile set.  
	 * <p>
	 * Replaces the current static tile set with a new static tile set.
	 * See the constructor <A HREF="../../../../de/enough/polish/ui/game/TiledLayer.html#TiledLayer(int, int, javax.microedition.lcdui.Image, int, int)"><CODE>TiledLayer(int, int, Image, int, int)</CODE></A>
	 * for information on how the tiles are created from the
	 * image.<p>
	 * 
	 * If the new static tile set has as many or more tiles than the
	 * previous static tile set,
	 * the the animated tiles and cell contents will be preserve.  If
	 * not, the contents of
	 * the grid will be cleared (all cells will contain index 0) and
	 * all animated tiles
	 * will be deleted.
	 * <P>
	 * 
	 * @param image the Image to use for creating the static tile set
	 * @param tileWidth the width in pixels of a single tile
	 * @param tileHeight the height in pixels of a single tile
	 * @throws NullPointerException if image is null
	 * @throws IllegalArgumentException if tileHeight or tileWidth is less than 1
	 * 									or if the image width is not an integer  multiple of the tileWidth
	 *									or if the image height is not an integer  multiple of the tileHeight
	 */
	public void setStaticTileSet( Image image, int tileWidth, int tileHeight)
	{
		this.cellWidth = tileWidth;
		this.cellHeight = tileHeight;
		int columns = image.getWidth() / tileWidth;
		int rows = image.getHeight() / tileHeight;
		//#ifndef tmp.splitTiles
			this.image = image;
			this.tileXPositions = new int[ columns ];
			int pos = 0;
			for (int i = 0; i < columns; i++ ) {
				this.tileXPositions[ i ] = pos;
				pos += tileWidth;
			}
			this.tileYPositions = new int[ rows ];
			pos = 0;
			for (int i = 0; i < rows; i++ ) {
				this.tileYPositions[ i ] = pos;
				pos += tileHeight;
			}
		//#else
			// split the image into single tiles:
			Image[] tileImages = new Image[ columns * rows ];
			int index = 0;
			for (int row = 0; row < rows; row++) {
				for (int column = 0; column < columns; column++) {
					//#if polish.api.nokia-ui && !tmp.useBackBuffer
						Image tile = DirectUtils.createImage( tileWidth, tileHeight, 0x00FFFFFF );
						Graphics g = tile.getGraphics();
						// when creating an transparent image, one must not "touch"
						// that image with an ordinary Graphics-object --- instead
						// ALWAYS an DirectGraphics-object needs to be used. Sigh!
						//g.drawImage(this.image, 0, 0, Graphics.TOP | Graphics.LEFT );
						DirectGraphics dg = DirectUtils.getDirectGraphics(g);
						dg.drawImage(image, -1 * column * tileWidth, -1 * row * tileHeight, Graphics.TOP | Graphics.LEFT, 0 );
					//#else
						//# Image tile = Image.createImage( tileWidth, tileHeight );
						//# Graphics g = tile.getGraphics();
						g.drawImage( image, -1 * column * tileWidth, -1 * row * tileHeight, Graphics.TOP | Graphics.LEFT );
					//#endif
					tileImages[index] = tile;
					index++;
				}
			}
			this.tiles = tileImages;
		//#endif
				
		if ( columns * rows < this.numberOfTiles ) {
			// clear the grid, when there are not as many tiles as in the previous set:
			for (int i = 0; i < this.grid.length; i++) {
				for (int j = 0; j < this.grid[i].length; j++) {
					this.grid[i][j] = 0;
				}
			}
		}
		this.numberOfTiles = columns * rows;
		this.numberOfColumns = columns;
		//this.numberOfRows = rows;
	}
	
	/**
	 * Creates a new animated tile and returns the index that refers
	 * to the new animated tile.  It is initially associated with
	 * the specified tile index (either a static tile or 0).
	 * <P>
	 * The indices for animated tiles are always negative.  The first
	 * animated tile shall have the index -1, the second, -2, etc.
	 * 
	 * @param staticTileIndex the index of the associated tile  (must be 0 or a valid static tile index)
	 * @return the index of newly created animated tile
	 * @throws IndexOutOfBoundsException if the staticTileIndex is invalid
	 */
	public int createAnimatedTile(int staticTileIndex)
	{
		if (staticTileIndex >= this.numberOfTiles ) {
			//#ifdef polish.debugVerbose
				throw new IllegalArgumentException("invalid static tile index: " + staticTileIndex + " (there are only [" + this.numberOfTiles + "] tiles available.");
			//#else
				//# throw new IllegalArgumentException();
			//#endif
		}
		if (this.animatedTiles == null) {
			//#ifdef polish.TiledLayer.GridType:defined
				//#= this.animatedTiles = new ${ polish.TiledLayer.GridType }[4];
			//#else
				this.animatedTiles = new byte[4];
			//#endif
		} else if (this.animatedTilesLength == this.animatedTiles.length) {
			//#ifdef polish.TiledLayer.GridType:defined
				//#= ${ polish.TiledLayer.GridType }[] newAnimatedTiles = new ${ polish.TiledLayer.GridType }[ this.animatedTilesLength * 2 ];
			//#else
				byte[] newAnimatedTiles = new byte[ this.animatedTilesLength * 2 ];
			//#endif
			System.arraycopy( this.animatedTiles, 0, newAnimatedTiles, 0, this.animatedTilesLength);
			this.animatedTiles = newAnimatedTiles;
		}
		//#ifdef polish.TiledLayer.GridType:defined
			//#= this.animatedTiles[ this.animatedTilesLength ] = (${ polish.TiledLayer.GridType }) staticTileIndex;
		//#else
			this.animatedTiles[ this.animatedTilesLength ] = (byte) staticTileIndex;
		//#endif
		this.animatedTilesLength++;
		return -1 * this.animatedTilesLength;
	}

	/**
	 * Associates an animated tile with the specified static tile.
	 * 
	 * @param animatedTileIndex the index of the animated tile
	 * @param staticTileIndex the index of the associated tile (must be 0 or a valid static tile index)
	 * @throws IndexOutOfBoundsException if the staticTileIndex is invalid
	 *									 or if the animated tile index is invalid
	 * @see #getAnimatedTile(int)
	 */
	public void setAnimatedTile(int animatedTileIndex, int staticTileIndex)
	{
		//#ifndef polish.skipArgumentCheck
			if (staticTileIndex > this.numberOfTiles ) {
				//#ifdef polish.debugVerbose
					throw new IllegalArgumentException("invalid static tile index: " + staticTileIndex + " (there are only [" + this.numberOfTiles + "] tiles available.");
				//#else
					//# throw new IllegalArgumentException();
				//#endif
			}
		//#endif
		int animatedIndex = (-1 * animatedTileIndex) - 1;
		//#ifdef polish.TiledLayer.GridType:defined
			//#= this.animatedTiles[ animatedIndex ] = (${ polish.TiledLayer.GridType }) staticTileIndex;
		//#else
			this.animatedTiles[ animatedIndex ] = (byte) staticTileIndex;
		//#endif
	}

	/**
	 * Gets the tile referenced by an animated tile.  
	 * <p>
	 * 
	 * Returns the tile index currently associated with the
	 * animated tile.
	 * 
	 * @param animatedTileIndex the index of the animated tile
	 * @return the index of the tile reference by the animated tile
	 * @throws IndexOutOfBoundsException if the animated tile index is invalid
	 * @see #setAnimatedTile(int, int)
	 */
	public int getAnimatedTile(int animatedTileIndex)
	{
		int animatedIndex = (-1 * animatedTileIndex) - 1;
		return this.animatedTiles[animatedIndex ];
	}

	/**
	 * Sets the contents of a cell.  
	 * 
	 * <P>
	 * The contents may be set to a static tile index, an animated
	 * tile index, or it may be left empty (index 0)
	 * 
	 * @param col the column of cell to set
	 * @param row the row of cell to set
	 * @param tileIndex the index of tile to place in cell
	 * @throws IndexOutOfBoundsException if there is no tile with index tileIndex
	 *									 or if row or col is outside the bounds of the TiledLayer grid
	 * @see #getCell(int, int)
	 * @see #fillCells(int, int, int, int, int)
	 */
	public void setCell(int col, int row, int tileIndex)
	{
		//#ifndef polish.skipArgumentCheck
			if (tileIndex > this.numberOfTiles ) {
				//#ifdef polish.debugVerbose
					throw new IllegalArgumentException("invalid static tile index: " + tileIndex + " (there are only [" + this.numberOfTiles + "] tiles available.");
				//#else
					//# throw new IllegalArgumentException();
				//#endif
			}
		//#endif
		//#ifdef polish.TiledLayer.GridType:defined
			//#= this.grid[ col ][ row ] = (${ polish.TiledLayer.GridType }) tileIndex;
		//#else
			this.grid[ col ][ row ] = (byte) tileIndex;
		//#endif
	}

	/**
	 * Gets the contents of a cell.  
	 * 
	 * <p>
	 * Gets the index of the static or animated tile currently displayed in
	 * a cell.  The returned index will be 0 if the cell is empty.
	 * 
	 * @param col the column of cell to check
	 * @param row the row of cell to check
	 * @return the index of tile in cell
	 * @throws IndexOutOfBoundsException if row or col is outside the bounds of the TiledLayer grid
	 * @see #setCell(int, int, int)
	 * @see #fillCells(int, int, int, int, int)
	 */
	public int getCell(int col, int row)
	{
		return this.grid[ col ][ row ];
	}

	/**
	 * Fills a region cells with the specific tile.  The cells may be filled
	 * with a static tile index, an animated tile index, or they may be left
	 * empty (index <code>0</code>).
	 * 
	 * @param col the column of top-left cell in the region
	 * @param row the row of top-left cell in the region
	 * @param numCols the number of columns in the region
	 * @param numRows the number of rows in the region
	 * @param tileIndex the Index of the tile to place in all cells in the  specified region
	 * @throws IndexOutOfBoundsException if the rectangular region defined by the parameters extends beyond the bounds of the TiledLayer grid
	 *									 or if there is no tile with index tileIndex
	 * @throws IllegalArgumentException if numCols is less than zero
	 *									or if numRows is less than zero
	 * @see #setCell(int, int, int)
	 * @see #getCell(int, int)
	 */
	public void fillCells(int col, int row, int numCols, int numRows, int tileIndex)
	{
		//#ifndef polish.skipArgumentCheck
			if (tileIndex > this.numberOfTiles) {
				//#ifdef polish.debugVerbose
					throw new IllegalArgumentException("invalid static tile index: " + tileIndex + " (there are only [" + this.numberOfTiles + "] tiles available.");
				//#else
					//# throw new IllegalArgumentException();
				//#endif
			}
		//#endif
		int endCols = col + numCols;
		int endRows = row + numRows;
		for (int i = col; i < endCols; i++ ) {
			for (int j = row; j < endRows; j++) {
				//#ifdef polish.TiledLayer.GridType:defined
					//#= this.grid[ i ][ j] = (${ polish.TiledLayer.GridType }) tileIndex;
				//#else
					this.grid[ i ][ j ] = (byte) tileIndex;
				//#endif
			}
		}
	}

	/**
	 * Gets the width of a single cell, in pixels.
	 * 
	 * @return the width in pixels of a single cell in the  TiledLayer grid
	 */
	public final int getCellWidth()
	{
		return this.cellWidth;
	}

	/**
	 * Gets the height of a single cell, in pixels.
	 * 
	 * @return the height in pixels of a single cell in the  TiledLayer grid
	 */
	public final int getCellHeight()
	{
		return this.cellHeight;
	}

	/**
	 * Gets the number of columns in the TiledLayer grid.
	 * The overall width of the TiledLayer, in pixels,
	 * may be obtained by calling <A HREF="../../../../de/enough/polish/ui/game/Layer.html#getWidth()"><CODE>Layer.getWidth()</CODE></A>.
	 * 
	 * @return the width in columns of the  TiledLayer grid
	 */
	public final int getColumns()
	{
		return this.gridColumns;
	}

	/**
	 * Gets the number of rows in the TiledLayer grid.  The overall
	 * height of the TiledLayer, in pixels, may be obtained by
	 * calling <A HREF="../../../../de/enough/polish/ui/game/Layer.html#getHeight()"><CODE>Layer.getHeight()</CODE></A>.
	 * 
	 * @return the height in rows of the  TiledLayer grid
	 */
	public final int getRows()
	{
		return this.gridRows;
	}

	/**
	 * Draws the TiledLayer.
	 * 
	 * The entire TiledLayer is rendered subject to the clip region of
	 * the Graphics object.
	 * The TiledLayer's upper left corner is rendered at the
	 * TiledLayer's current
	 * position relative to the origin of the Graphics object.   The current
	 * position of the TiledLayer's upper-left corner can be retrieved by
	 * calling <A HREF="../../../../de/enough/polish/ui/game/Layer.html#getX()"><CODE>Layer.getX()</CODE></A> and <A HREF="../../../../javax/microedition/lcdui/game/Layer.html#getY()"><CODE>Layer.getY()</CODE></A>.
	 * The appropriate use of a clip region and/or translation allows
	 * an arbitrary region
	 * of the TiledLayer to be rendered.
	 * <p>
	 * If the TiledLayer's Image is mutable, the TiledLayer is rendered
	 * using the current contents of the Image, when the image has not been
	 * split into single tiles
	 *  (this is done when the "polish.TiledLayer.splitImage" or the "polish.TiledLayer.useBackBuffer" 
	 * variable is set to true).
	 * </p>
	 * <p>
	 * The TiledLayer implementation uses byte for the internal grid. This can be changed
	 * using the "polish.TiledLayer.GridType" variable, which can either be 
	 * "byte", "short" or "int". 
	 * When the byte-grid is used, less memory is used but only 128 different tiles can be used.  
	 * </p>
	 * 
	 * @param g the graphics object to draw the TiledLayer
	 * @throws NullPointerException if g is null
	 * @see Layer#paint(Graphics) in class Layer
	 */
	public final void paint( Graphics g)
	{
		if (!this.isVisible) {
			return;
		}
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();
		// set first and last column:
		int firstColumn = 0;
		int lastColumn;
		if (this.xPosition < clipX) {
			firstColumn = (clipX - this.xPosition) / this.cellWidth;
			lastColumn = ((clipX - this.xPosition + clipWidth) / this.cellWidth) + 1;
			//System.out.println("setting columnStart=" + firstColumn + " clipWidth=" + clipWidth + " cellWidth=" + this.cellWidth );
		} else {
			lastColumn = (clipWidth / this.cellWidth) + 1; 
		}
		if (lastColumn > this.gridColumns) {
			lastColumn = this.gridColumns;
		}
		// set first and last row:
		int firstRow = 0;
		int lastRow;
		if (this.yPosition < clipY) {
			firstRow = (clipY - this.yPosition) / this.cellHeight;
			lastRow = ((clipY - this.yPosition + clipHeight) / this.cellHeight) + 1;
		} else {
			lastRow = (clipHeight / this.cellHeight) + 1;
		}
		if (lastRow > this.gridRows) {
			lastRow = this.gridRows;
		}
		
		// set initial x and y positions:
		int xStart = this.xPosition + (firstColumn * this.cellWidth);
		int yStart = this.yPosition + (firstRow * this.cellHeight);
		
		 
		int y = yStart;
		int x = xStart;
		
		//#ifdef polish.TiledLayer.GridType:defined
			//#= ${ polish.TiledLayer.GridType }[][] gridTable = this.grid;
		//#else
			byte[][] gridTable = this.grid;
		//#endif
		//#ifdef tmp.useBackBuffer
			x = 0;
			y = 0;
			// create back buffer when it is null:
			boolean clearBuffer = false;
			if (this.bufferGrid == null) {
				clearBuffer = true;
				//#ifdef polish.TiledLayer.GridType:defined
					//#= this.bufferGrid = new ${ polish.TiledLayer.GridType }[ this.gridColumns ][ this.gridRows ];
				//#else
					this.bufferGrid = new byte[ this.gridColumns ][ this.gridRows ];
				//#endif
				this.bufferImage = Image.createImage( 
					this.cellWidth * ((clipWidth / this.cellWidth) + 2),
					this.cellHeight * ((clipHeight / this.cellHeight) + 2) );
				this.bufferGraphics = this.bufferImage.getGraphics();
				this.bufferFirstColumn = firstColumn;
				this.bufferFirstRow = firstRow;
			} else if ( this.bufferFirstColumn != firstColumn 
					|| this.bufferFirstRow != firstRow) 
			{
				//#debug
				System.out.println("clearing backbuffer");
				clearBuffer = true;
				this.bufferFirstColumn = firstColumn;
				this.bufferFirstRow = firstRow;
			}
			Graphics originalGraphics = g;
			g = this.bufferGraphics;
			g.setColor( this.bufferTransparentColor );
			if (clearBuffer) {
				g.fillRect( 0, 0, this.bufferImage.getWidth(), this.bufferImage.getHeight() );
			}
		//#elif tmp.splitTiles && polish.api.nokia-ui && ! tmp.useBackBuffer
			DirectGraphics dg = DirectUtils.getDirectGraphics(g);
		//#endif
		
		// now paint the single tiles either directly to the screen
		// or to the backbuffer:
		for (int column = firstColumn; column < lastColumn; column++) {
			//#ifdef polish.TiledLayer.GridType:defined
				//#= ${ polish.TiledLayer.GridType }[] gridColumn = gridTable[column];
			//#else
				byte[] gridColumn = gridTable[column];
			//#endif
			for (int row = firstRow; row < lastRow; row++) {
				int cellIndex = gridColumn[row];
				if (cellIndex != 0) {
					// okay this cell needs to be rendered:
					int tileIndex;
					if (cellIndex < 0) {
						tileIndex = this.animatedTiles[ (-1 * cellIndex ) -1  ] - 1;
					} else {
						tileIndex = cellIndex -1;
					}
					//#ifdef tmp.useBackBuffer
						if (!clearBuffer && tileIndex == this.bufferGrid[ column ][ row ]) {
							// the tile has been painted already to the backbuffer,
							// so continue this loop:
							if (!(row >= this.bufferLastRow || column >= this.bufferLastColumn)) {
								y += this.cellHeight;
								continue;
							}
						} 
					//#endif
					// now draw the tile:
					//#ifndef tmp.splitTiles
						// paint the tile the traditional way:
						// slower but using less memory,
						// this is also the only way for plain MIDP1-devices
						// to keep the transparency of tiles: 
						g.setClip( x, y, this.cellWidth, this.cellHeight );
						//#ifndef tmp.useBackBuffer
							// 4 variables would need to be compared, so set the 
							// intersection with the original clip in all cases: 
							g.clipRect( clipX, clipY, clipWidth, clipHeight );
						//#endif
						int tileColumn = tileIndex % this.numberOfColumns;
						int tileRow = tileIndex / this.numberOfColumns; 
						int tileX = x - this.tileXPositions[ tileColumn ];
						int tileY = y - this.tileYPositions[ tileRow ];
						g.drawImage( this.image, tileX, tileY, Graphics.TOP | Graphics.LEFT );
					//#else
						// paint a single tile: faster, but needs more memory.
						// Also any transparency is lost, when no 
						// Nokia-UI API is available:
						Image tile = this.tiles[ tileIndex ];
						//#ifdef tmp.useBackBuffer
							g.drawImage( tile, x, y, Graphics.TOP | Graphics.LEFT );
						//#elif polish.api.nokia-ui
							dg.drawImage( tile, x, y, Graphics.TOP | Graphics.LEFT, 0 );
						//#else
							g.drawImage( tile, x, y, Graphics.TOP | Graphics.LEFT );
						//#endif
					//#endif
					//#ifdef tmp.useBackBuffer
						this.bufferGrid[ column ][ row ] = (byte) tileIndex;
					//#endif
				//#ifndef tmp.useBackBuffer
					//# }
				//#else
					} else if (this.bufferGrid[ column ][ row ] != 0) {
						// fill this cell with the background-color,
						// the color has been set before this loop:
						g.fillRect( x, y, this.cellWidth, this.cellHeight );
						this.bufferGrid[ column ][ row ] = 0;
					}
				//#endif
				y += this.cellHeight;
			} // for each row
			//#ifdef tmp.useBackBuffer
				y = 0;
			//#else
				y = yStart;
			//#endif
			x += this.cellWidth;
		} // for each column
		
		//#ifdef tmp.useBackBuffer
			// now paint the backbuffer to the screen:
			originalGraphics.drawImage( this.bufferImage, xStart, yStart, Graphics.TOP | Graphics.LEFT );
			// save last row and column for the next paint() call:
			this.bufferLastColumn = lastColumn;
			this.bufferLastRow = lastRow; 
		//#endif
		
		// reset original clip:
		//#ifndef tmp.splitTiles
			g.setClip( clipX, clipY, clipWidth, clipHeight );
		//#endif
	}
	
	/**
	 * Retrieves the tile at the given positions.
	 * When the specified position is not within this
	 * tiled layer, 0 is returned.
	 * 
	 * @param x the x position
	 * @param y the y position
	 * @return the tile index which is at the given position:
	 * 		0 when there is no tile, a positive integer for a normal tile,
	 *      a negative integer for an animated tile.
	 */
	protected int getTileAt( int x, int y) {
		int column = (x - this.xPosition) / this.cellWidth;
		int row = (y - this.yPosition) / this.cellHeight;
		if (column < 0 || column >= this.gridColumns
				|| row < 0 || row >= this.gridRows ) {
			return 0;
		} else {
			return this.grid[column][row];
		}
	}

}
