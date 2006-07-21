/**
 * 
 */
package de.enough.sudoku;


import javax.microedition.lcdui.Canvas;

import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextField;

/**
 * @author jinny
 *
 */
public class SudokuTextField
//#if polish.usePolishGui
	//# extends TextField
//#else
	extends de.enough.polish.ui.FakeTextFieldCustomItem
//#endif
{

	private final int minimum;
	private final int maximum;

	public SudokuTextField( int minimum, int maximum ) {
		this( minimum, maximum, null );
	}

	public SudokuTextField( int minimum, int maximum,Style style  ) {
		super(null, null, 1, TextField.NUMERIC, style );
		this.minimum = minimum;
		this.maximum = maximum;
		this.showCaret = false;
	}
	
	public void setValue( int value ) {
		setString( "" + value );
	}
	
	public int getValue() {
		String strValue = getString();
		if (strValue.length() == 0) {
			return -1;
		}
		return Integer.parseInt( strValue  );
	}


	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		if (this.isUneditable) {
			return false;
		}
		int minCode = this.minimum + Canvas.KEY_NUM0;
		if (keyCode >= minCode && keyCode <= Canvas.KEY_NUM9 ) {
			setString( Integer.toString( keyCode - Canvas.KEY_NUM0 ) );
			notifyStateChanged();
			this.flashCaret = false;
			return true;
		} else if (keyCode == Canvas.KEY_NUM0 ){
			setString("");
			notifyStateChanged();
			this.flashCaret = false;
		}
		return false;
	}

	
	

}
