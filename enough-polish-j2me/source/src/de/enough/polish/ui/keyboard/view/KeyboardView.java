//#condition polish.TextField.useVirtualKeyboard
package de.enough.polish.ui.keyboard.view;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Command;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.FramedForm;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TextField;
import de.enough.polish.ui.keyboard.Keyboard;

public class KeyboardView extends FramedForm implements ItemCommandListener 
{
	Command cmdDelete = new Command("",Command.ITEM,0);
	
	Command cmdClear = new Command("",Command.ITEM,0);
	
	Command cmdSubmit = new Command("",Command.ITEM,0);
	
	Container headerItem;
	
	StringItem submitItem;
	
	StringItem displayItem;
	
	TextField field;
	
	Displayable ancestor;
	
	final static int CHARACTER_RETURN = 10;
	
	protected Keyboard keyboard;
	
	StringItem deleteItem;
	
	StringItem clearItem;
	
	int mode;
	
	String text;
	
	static KeyboardView instance;
	
	public static KeyboardView getInstance(String title, TextField field, Displayable ancestor, int mode) {
		if(instance == null) {
			instance = new KeyboardView();
		} 
		
		instance.init(title, field, ancestor, mode);
		
		return instance;
	}
	
	public KeyboardView() {
		//#style keyboardView
		super(null);
	}
	
	protected void init(String title, TextField field, Displayable ancestor, int mode) {
		setTitle(title);
		
		this.field = field;
		
		this.ancestor = ancestor;
		
		this.mode = mode;
		
		buildHeader();
		
		build();
		
		String value = field.getString();
		setText(value);
	}
	
	void buildHeader() {
		deleteAll(Graphics.TOP);
		
		//#style keyboardViewSubmit
		this.submitItem = new StringItem(null,"OK");
		this.submitItem.setDefaultCommand(cmdSubmit);
		this.submitItem.setItemCommandListener(this);
		
		//#style keyboardViewDisplay
		this.displayItem = new StringItem(null,"");
		
		//#style keyboardViewHeader
		this.headerItem = new Container(false);
			
		this.headerItem.add(this.displayItem);
		this.headerItem.add(this.submitItem);
		
		append(Graphics.TOP,this.headerItem);
	}
	
	protected void build() {
		deleteAll(-1);
		
		this.keyboard = new Keyboard();
		String modeAlphaFile = "";
		String modeNumericFile = "";
		
		//#if polish.TextField.VirtualKeyboard.Mode.Alpha:defined
			//#= modeAlphaFile = "${polish.TextField.VirtualKeyboard.Mode.Alpha}";
			//#if ${polish.TextField.VirtualKeyboard.Mode.Numeric}:defined
				//#= modeNumericFile = "${polish.TextField.VirtualKeyboard.Mode.Numeric}";
				this.keyboard.prepare(Keyboard.MODE_ALPHA, modeAlphaFile, Keyboard.MODE_NUMERIC, modeNumericFile);
			//#else
				this.keyboard.prepare(Keyboard.MODE_ALPHA, modeAlphaFile);
			//#endif
		//#elif polish.TextField.VirtualKeyboard.Mode.Numeric:defined
			//#= modeNumericFile = "${polish.TextField.VirtualKeyboard.Mode.Numeric}";
			this.keyboard.prepare(Keyboard.MODE_NUMERIC, modeNumericFile);
		//#else
			this.keyboard.prepare();
		//#endif
		
		
		this.keyboard.setKeyboardView(this);
		
		this.keyboard.setMode(this.mode);
		
		append(this.keyboard);
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setText(String text) {
		this.text = text;
		this.displayItem.setText(text);
	}

	public void commandAction(Command c, Item item) {
		if(c == cmdSubmit) {
			this.field.setString(text);
			Display.getInstance().setCurrent(this.ancestor);
		}
		
		if(c == cmdDelete) {
			this.keyboard.deleteText();
		} 
		
		if (c == cmdClear) {
			this.keyboard.clearText();
		}
	}
}
