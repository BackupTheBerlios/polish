package de.enough.skylight.js.scriptableobjects;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import de.enough.skylight.js.form.Alert;

public class AlertScriptableObject extends BaseFunction{

	@Override
	public Object call(Context cx, Scriptable scope, Scriptable thisObj,
			Object[] args) {
		int length = args.length;
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < length; i++) {
			buffer.append(args[i]);
		}
		
		Alert alert = new Alert();
		alert.init(buffer.toString());
		alert.show();
		
//		while(i < length-1) {
//			System.out.print(args[i]);
//			System.out.print(",");
//			i++;
//		}
//		if(length > 0) {
//			System.out.println(args[length-1]);
//		}
		return null;
	}

	
	
}
