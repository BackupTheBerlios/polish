package de.enough.skylight.js;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class Alert extends BaseFunction{

	@Override
	public Object call(Context cx, Scriptable scope, Scriptable thisObj,
			Object[] args) {
		int length = args.length;
		int i = 0;
		while(i < length-1) {
			System.out.print(args[i]);
			System.out.print(",");
			i++;
		}
		if(length > 0) {
			System.out.println(args[length-1]);
		}
		return null;
	}

	
	
}
