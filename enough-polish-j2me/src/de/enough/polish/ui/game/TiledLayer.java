// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Sat Dec 06 15:06:44 CET 2003
package de.enough.polish.ui.game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

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
 * at any time using the <A HREF="../../../../javax/microedition/lcdui/game/TiledLayer.html#setStaticTileSet(javax.microedition.lcdui.Image, int, int)"><CODE>setStaticTileSet(javax.microedition.lcdui.Image, int, int)</CODE></A> method.
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
 * Animated tiles are created using the <A HREF="../../../../javax/microedition/lcdui/game/TiledLayer.html#createAnimatedTile(int)"><CODE>createAnimatedTile(int)</CODE></A>
 * method, which returns the
 * index to be used for the new animated tile.  The animated tile
 * indices are always negative
 * and consecutive, beginning with -1.  Once created, the static tile
 * associated with an
 * animated tile can be changed using the <A HREF="../../../../javax/microedition/lcdui/game/TiledLayer.html#setAnimatedTile(int, int)"><CODE>setAnimatedTile(int, int)</CODE></A>
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
 * The contents of cells may be changed using <A HREF="../../../../javax/microedition/lcdui/game/TiledLayer.html#setCell(int, int, int)"><CODE>setCell(int, int, int)</CODE></A> and
 * <A HREF="../../../../javax/microedition/lcdui/game/TiledLayer.html#fillCells(int, int, int, int, int)"><CODE>fillCells(int, int, int, int, int)</CODE></A>.  Several
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
 * <HR>
 * 
 * 
 * @since MIDP 2.0
 */
public class TiledLayer extends Layer
{
	//following variables are implicitely defined by getter- or setter-methods:
	private int cellWidth;
	private int cellHeight;
	private int columns;
	private int rows;

	/**
	 * Creates a new TiledLayer.  <p>
	 * 
	 * The TiledLayer's grid will be <code>rows</code> cells high and
	 * <code>columns</code> cells wide.  All cells in the grid are initially
	 * empty (i.e. they contain tile index 0).  The contents of the grid may
	 * be modified through the use of <A HREF="../../../../javax/microedition/lcdui/game/TiledLayer.html#setCell(int, int, int)"><CODE>setCell(int, int, int)</CODE></A> and <A HREF="../../../../javax/microedition/lcdui/game/TiledLayer.html#fillCells(int, int, int, int, int)"><CODE>fillCells(int, int, int, int, int)</CODE></A>.
	 * <P>
	 * The static tile set for the TiledLayer is created from the specified
	 * Image with each tile having the dimensions of tileWidth x tileHeight.
	 * The width of the source image must be an integer multiple of
	 * the tile width, and the height of the source image must be an integer
	 * multiple of the tile height; otherwise, an IllegalArgumentException
	 * is thrown;<p>
	 * 
	 * The entire static tile set can be changed using
	 * <A HREF="../../../../javax/microedition/lcdui/game/TiledLayer.html#setStaticTileSet(javax.microedition.lcdui.Image, int, int)"><CODE>setStaticTileSet(Image, int, int)</CODE></A>.
	 * These methods should be used sparingly since they are both
	 * memory and time consuming.
	 * Where possible, animated tiles should be used instead to
	 * animate tile appearance.<p>
	 * 
	 * @param columns - the width of the TiledLayer, expressed as a number of cells
	 * @param rows - the height of the TiledLayer, expressed as a number of cells
	 * @param image - the Image to use for creating the static tile set
	 * @param tileWidth - the width in pixels of a single tile
	 * @param tileHeight - the height in pixels of a single tile
	 * @throws NullPointerException - if image is null
	 * @throws IllegalArgumentException - if the number of rows or columns is less than 1
	 *												      or if tileHeight or tileWidth is less than 1
	 *												      or if the image width is not an integer multiple of the tileWidth
	 *												      or if the image height is not an integer multiple of the tileHeight
	 */
	public TiledLayer(int columns, int rows, Image image, int tileWidth, int tileHeight)
	{
		//TODO implement TiledLayer
	}

	/**
	 * Creates a new animated tile and returns the index that refers
	 * to the new animated tile.  It is initially associated with
	 * the specified tile index (either a static tile or 0).
	 * <P>
	 * The indices for animated tiles are always negative.  The first
	 * animated tile shall have the index -1, the second, -2, etc.
	 * 
	 * @param staticTileIndex - the index of the associated tile  (must be 0 or a valid static tile index)
	 * @return the index of newly created animated tile
	 * @throws IndexOutOfBoundsException - if the staticTileIndex is invalid
	 */
	public int createAnimatedTile(int staticTileIndex)
	{
		return 0;
		//TODO implement createAnimatedTile
	}

	/**
	 * Associates an animated tile with the specified static tile.  <p>
	 * 
	 * @param animatedTileIndex - the index of the animated tile
	 * @param staticTileIndex - the index of the associated tile (must be 0 or a valid static tile index)
	 * @throws IndexOutOfBoundsException - if the staticTileIndex is invalid
	 *												      or if the animated tile index is invalid
	 * @see #getAnimatedTile(int)
	 */
	public void setAnimatedTile(int animatedTileIndex, int staticTileIndex)
	{
		//TODO implement setAnimatedTile
	}

	/**
	 * Gets the tile referenced by an animated tile.  <p>
	 * 
	 * Returns the tile index currently associated with the
	 * animated tile.
	 * 
	 * @param animatedTileIndex - the index of the animated tile
	 * @return the index of the tile reference by the animated tile
	 * @throws IndexOutOfBoundsException - if the animated tile index is invalid
	 * @see #setAnimatedTile(int, int)
	 */
	public int getAnimatedTile(int animatedTileIndex)
	{
		return 0;
		//TODO implement getAnimatedTile
	}

	/**
	 * Sets the contents of a cell.  <P>
	 * 
	 * The contents may be set to a static tile index, an animated
	 * tile index, or it may be left empty (index 0)
	 * 
	 * @param col - the column of cell to set
	 * @param row - the row of cell to set
	 * @param tileIndex - the index of tile to place in cell
	 * @throws IndexOutOfBoundsException - if there is no tile with index tileIndex
	 *												      or if row or col is outside the bounds of the TiledLayer grid
	 * @see #getCell(int, int), #fillCells(int, int, int, int, int)
	 */
	public void setCell(int col, int row, int tileIndex)
	{
		//TODO implement setCell
	}

	/**
	 * Gets the contents of a cell.  <p>
	 * 
	 * Gets the index of the static or animated tile currently displayed in
	 * a cell.  The returned index will be 0 if the cell is empty.
	 * 
	 * @param col - the column of cell to check
	 * @param row - the row of cell to check
	 * @return the index of tile in cell
	 * @throws IndexOutOfBoundsException - if row or col is outside the bounds of the TiledLayer grid
	 * @see #setCell(int, int, int), #fillCells(int, int, int, int, int)
	 */
	public int getCell(int col, int row)
	{
		return 0;
		//TODO implement getCell
	}

	/**
	 * Fills a region cells with the specific tile.  The cells may be filled
	 * with a static tile index, an animated tile index, or they may be left
	 * empty (index <code>0</code>).
	 * 
	 * @param col - the column of top-left cell in the region
	 * @param row - the row of top-left cell in the region
	 * @param numCols - the number of columns in the region
	 * @param numRows - the number of rows in the region
	 * @param tileIndex - the Index of the tile to place in all cells in the  specified region
	 * @throws IndexOutOfBoundsException - if the rectangular region defined by the parameters extends beyond the bounds of the TiledLayer grid
	 *														   or if there is no tile with index tileIndex
	 * @throws IllegalArgumentException - if numCols is less than zero
	 *												      or if numRows is less than zero
	 * @see #setCell(int, int, int), #getCell(int, int)
	 */
	public void fillCells(int col, int row, int numCols, int numRows, int tileIndex)
	{
		//TODO implement fillCells
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
	 * may be obtained by calling <A HREF="../../../../javax/microedition/lcdui/game/Layer.html#getWidth()"><CODE>Layer.getWidth()</CODE></A>.
	 * 
	 * @return the width in columns of the  TiledLayer grid
	 */
	public final int getColumns()
	{
		return this.columns;
	}

	/**
	 * Gets the number of rows in the TiledLayer grid.  The overall
	 * height of the TiledLayer, in pixels, may be obtained by
	 * calling <A HREF="../../../../javax/microedition/lcdui/game/Layer.html#getHeight()"><CODE>Layer.getHeight()</CODE></A>.
	 * 
	 * @return the height in rows of the  TiledLayer grid
	 */
	public final int getRows()
	{
		return this.rows;
	}

	/**
	 * Change the static tile set.  <p>
	 * 
	 * Replaces the current static tile set with a new static tile set.
	 * See the constructor <A HREF="../../../../javax/microedition/lcdui/game/TiledLayer.html#TiledLayer(int, int, javax.microedition.lcdui.Image, int, int)"><CODE>TiledLayer(int, int, Image, int, int)</CODE></A>
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
	 * @param image - the Image to use for creating the static tile set
	 * @param tileWidth - the width in pixels of a single tile
	 * @param tileHeight - the height in pixels of a single tile
	 * @throws NullPointerException - if image is null
	 * @throws IllegalArgumentException - if tileHeight or tileWidth is less than 1
	 * 												   or if the image width is not an integer  multiple of the tileWidth
	 *												       or if the image height is not an integer  multiple of the tileHeight
	 */
	public void setStaticTileSet( Image image, int tileWidth, int tileHeight)
	{
		//TODO implement setStaticTileSet
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
	 * calling <A HREF="../../../../javax/microedition/lcdui/game/Layer.html#getX()"><CODE>Layer.getX()</CODE></A> and <A HREF="../../../../javax/microedition/lcdui/game/Layer.html#getY()"><CODE>Layer.getY()</CODE></A>.
	 * The appropriate use of a clip region and/or translation allows
	 * an arbitrary region
	 * of the TiledLayer to be rendered.
	 * <p>
	 * If the TiledLayer's Image is mutable, the TiledLayer is rendered
	 * using the current contents of the Image.
	 * 
	 * @param g - the graphics object to draw the TiledLayer
	 * @throws NullPointerException - if g is null
	 * @see Layer#paint(Graphics) in class Layer
	 */
	public final void paint( Graphics g)
	{
		//TODO implement paint
	}

}
