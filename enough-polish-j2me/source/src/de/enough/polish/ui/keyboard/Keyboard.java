//#condition polish.TextField.useVirtualKeyboard
package de.enough.polish.ui.keyboard;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.keyboard.keys.BlankItem;
import de.enough.polish.ui.keyboard.keys.ClearKeyItem;
import de.enough.polish.ui.keyboard.keys.DeleteKeyItem;
import de.enough.polish.ui.keyboard.keys.ModeKeyItem;
import de.enough.polish.ui.keyboard.keys.ShiftKeyItem;
import de.enough.polish.ui.keyboard.view.KeyboardView;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Arrays;
import de.enough.polish.util.IntHashMap;
import de.enough.polish.util.Properties;

public class Keyboard
	extends Container
{
	static String KEYS_ALPHA = "0,0=a\n0,1=b\n0,2=c\n0,3=d\n0,4=e\n1,0=f\n1,1=g\n1,2=h\n1,3=i\n1,4=j\n2,0=k\n2,1=l\n2,2=m\n2,3=n\n2,4=o\n3,0=p\n3,1=q\n3,2=r\n3,3=s\n3,4=t\n4,0=u\n4,1=v\n4,2=x\n4,3=y\n4,4=z\n5,0=[SHIFT]\n5,1=[SPACE]\n5,2=[DELETE]\n5,3=[123]\n";

	static String KEYS_NUMERIC = "0,0=1\n0,1=2\n0,2=3\n0,3=4\n0,4=5\n0,5=6\n1,0=7\n1,1=8\n1,2=9\n1,3=0\n1,4=!\n1,5=\"\n2,0=%\n2,1=&\n2,2=/\n2,3=(\n2,4=)\n2,5==\n3,0=?\n3,1=\'\n3,2=*\n3,3=+\n3,4=-\n3,5=#\n4,0=,\n4,1=.\n4,2=:\n4,3=;\n4,4=[\n4,5=]\n5,0=[]\n5,1=[SPACE]\n5,2=[CLEAR]\n5,3=[ABC]\n";
	
	public static final int MODE_NONE = Integer.MIN_VALUE;
	public static final int MODE_ALPHA = 0x00;
	public static final int MODE_NUMERIC = 0x01;
	public static final int MODE_NUMBERS = 0x02;
	public static final int MODE_CUSTOM = 0x03;
	
	public static final String ID_SHIFT = "[SHIFT]";
	public static final String ID_CLEAR = "[CLEAR]";
	public static final String ID_DELETE = "[DELETE]";
	public static final String ID_SPACE = "[SPACE]";
	public static final String ID_123 = "[123]";
	public static final String ID_ABC = "[ABC]";
	public static final String ID_BLANK = "[]";
	
	int primaryMode;
	
	int secondaryMode;
	
	int currentMode;
	
	IntHashMap layoutByMode;
	
	KeyboardView view;
	
	boolean shift;
	
	public KeyItem lastKey;
	
	public Keyboard() {
		//#style keyboard?
		super(false);
		
		this.layoutByMode = new IntHashMap();
	}
		
	public void prepare() {
		Properties keys;
		InputStream stream;
		try {
			keys = new Properties();
			stream = new ByteArrayInputStream(KEYS_ALPHA.getBytes());
			keys.load(stream);
			addMode(MODE_ALPHA,keys);
			this.primaryMode = MODE_ALPHA;
			
			keys = new Properties();
			stream = new ByteArrayInputStream(KEYS_NUMERIC.getBytes());
			keys.load(stream);
			addMode(MODE_NUMERIC,keys);
			this.secondaryMode = MODE_NUMERIC;
			
			setMode(this.primaryMode);
		} catch (IOException e) {
			//#debug error
			System.out.println("unable to load key definitions");
		}
	}
	
	public void prepare(int primaryMode, String primaryKeys) {
		prepare(primaryMode,primaryKeys,MODE_NONE,null);
	}
	
	public void prepare(int primaryMode, String primaryKeys, int secondaryMode, String secondaryKeys) {
		Properties keys;
		try {
			keys = new Properties(primaryKeys, "UTF8");
			addMode(primaryMode,keys);
			this.primaryMode = primaryMode;
			
			if(secondaryMode != MODE_NONE) {
				keys = new Properties(secondaryKeys, "UTF8");
				addMode(secondaryMode,keys);
				this.secondaryMode = secondaryMode;
			}
			
			setMode(primaryMode);
		} catch (IOException e) {
			//#debug error
			System.out.println("unable to load key definitions");
		}
	}
	
	public void addMode(int mode, Properties keys) {
		ArrayList rows = buildLayout(mode, keys);
		
		this.layoutByMode.put(mode, rows);
	}
	
	void setKeys(int mode) {
		ArrayList rows = (ArrayList)this.layoutByMode.get(mode);
		
		for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
			add((Container)rows.get(rowIndex));
		}
	}
	
	KeyItem[] getKeyItems(Properties layoutDescription) {
		Object[] positions = layoutDescription.keys();
		KeyItem[] items = new KeyItem[positions.length];
		
		for (int i = 0; i < positions.length; i++) {
			String position = (String)positions[i];
			String keys = layoutDescription.getProperty(position);
			
			KeyItem keyItem = getSpecialKeyItem(keys, position);
			
			if(keyItem == null) {
				keyItem = getKeyItem(keys,position);
			}
			
			items[i] = keyItem;
		}
		
		Arrays.sort(items, new KeyComparator());
		
		return items;
	}
	
	ArrayList buildLayout(int keyboardMode, Properties layoutDescription) {
		KeyItem[] keyItems = getKeyItems(layoutDescription);
		
		ArrayList rows = new ArrayList();
		
		int currentRow = 0;
		
		//#style keyrow
		Container row = new Container(false);
		
		for (int i = 0; i < keyItems.length; i++) {
			KeyItem keyItem = keyItems[i];
			
			if(currentRow < keyItem.getRow()) {
				currentRow = keyItem.getRow();
				rows.add(row);
				
				//#style keyrow
				row = new Container(false);
			}
			
			row.add(keyItem);
		}
		
		rows.add(row);
		
		return rows;
	}
	
	KeyItem getSpecialKeyItem(String keys, String position) {
		if(keys.equals(ID_SHIFT)) {
			//#style keyShift
			return new ShiftKeyItem(this, position);
		} else if(keys.equals(ID_123)) {
			//#style key123
			return new ModeKeyItem(this, position);
		} else if(keys.equals(ID_ABC)) {
			//#style keyAbc
			return new ModeKeyItem(this, position);
		} else if(keys.equals(ID_CLEAR)) {
			//#style keyClear
			return new ClearKeyItem(this, position);
		} else if (keys.equals(ID_DELETE)) {
			//#style keyDelete
			return new DeleteKeyItem(this, position);
		} else if(keys.equals(ID_BLANK)) {
			//#style keyBlank
			return new BlankItem(this, position);
		} else if(keys.equals(ID_SPACE)) {
			//#style keySpace
			return new KeyItem(this, position, " ");
		} else {
			return null;
		}
	}
	
	KeyItem getKeyItem(String keys,String position) {
		//#style key
		return new KeyItem(this,position,keys);
	}
	
	public KeyboardView getKeyboardView() {
		return this.view;
	}
	
	public void setKeyboardView(KeyboardView view) {
		this.view = view;
	}

	public void setMode(int mode) {
		clear();
		
		ArrayList rows = (ArrayList)this.layoutByMode.get(getMode());
		
		if(rows != null) {
			for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
				remove((Container)rows.get(rowIndex));
			}
		}

		this.currentMode = mode;
		
		setKeys(mode);
	}
	
	public void apply(char key) {
		apply(key,false,false);
	}
	
	public void apply(char key, boolean isDoubleclick, boolean isMultiple) {
		
		String source = this.view.getText();
		
		String text;
		
		if(isDoubleclick && isMultiple) {
			text = source.substring(0,source.length() - 1) + key;
		}
		else {
			text = source + key;
		}
		
		this.view.setText(text);
	}
	
	public void clearText() {
		this.view.setText("");
	}
	
	public void deleteText() {
		String text = this.view.getText();
				
		if(text.length() > 0)
		{
			this.view.setText(text.substring(0, text.length() - 1));
		}
	}
	
	public int getMode() {
		return this.currentMode;
	}
	
	public int getPrimaryMode() {
		return this.primaryMode;
	}
	
	public int getSecondaryMode() {
		return this.secondaryMode;
	}
	
	public void shift(boolean shift) {
		this.shift = shift;
	}
	
	public boolean isShift() {
		return this.shift;
	}
	
	protected String createCssSelector() {
		return null;
	}
	
}
