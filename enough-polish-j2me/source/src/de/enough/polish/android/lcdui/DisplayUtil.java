//#condition polish.usePolishGui && polish.android
package de.enough.polish.android.lcdui;

import android.view.KeyCharacterMap;
import android.view.KeyEvent;

public class DisplayUtil {
	
	KeyCharacterMap characterMap;
	
//	KeyEvent event;
	
	public DisplayUtil(int deviceId)
	{
		this.characterMap = KeyCharacterMap.load(deviceId);
	}
	
//	public KeyEvent getEvent() {
//		return this.event;
//	}
//
//	public void setEvent(KeyEvent event) {
//		this.event = event;
//	}
	
	public int handleKey(int keyCode, KeyEvent event, Canvas canvas)
	{
//		setEvent(event);
		
		int key = event.getKeyCode();
		int meta = event.getMetaState();
		
		int ascii = this.characterMap.get(key, meta);
		//#debug
		System.out.println("Mapped keycode '"+keyCode+"' to ascii char '"+ascii+"'");
		boolean isClearKey = isClear(keyCode);
		boolean isReturnKey = isReturn(keyCode);
		boolean isMenuKey = isMenu(keyCode);
		int gameActionKey = canvas.getGameAction(keyCode);
		boolean isGameActionKey = gameActionKey != 0;
		boolean notAsciiKey = ascii == 0;
		if( notAsciiKey && (isGameActionKey || isClearKey || isReturnKey ||	isMenuKey)
		){
			return keyCode;
		}
		else
		{
			return ascii;
		}
	}
	
	public boolean isClear(int keyCode)
	{
		//#ifdef polish.key.ClearKey:defined
		//#= if (keyCode == ${polish.key.ClearKey})
		//# {
		//#		return true; 
		//# }
		//#endif
		
		return false;
	}
	
	public boolean isReturn(int keyCode)
	{
		//#if polish.key.ReturnKey:defined
		//#= if (keyCode == ${polish.key.ReturnKey})
		//# {
		//#		return true; 
		//# }
		//#endif
			
		return false;
	}
	
	public boolean isMenu(int keyCode)
	{
		//#if polish.key.Menu:defined
		//#= if (keyCode == ${polish.key.Menu})
		//# {
		//#		return true; 
		//# }
		//#endif
			
		return false;
	}
}
