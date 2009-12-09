package de.enough.skylight.js;

import junit.framework.Assert;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class AssertScriptableObject extends ScriptableObject{

	public class NotNullFunction extends BaseFunction {
		@Override
		public Object call(Context cx, Scriptable scope,Scriptable thisObj, Object[] args) {
			Object expected = args[0];
			Assert.assertNotNull(expected);
			return null;
		}

	}

	protected final class EqualsFunction extends BaseFunction {
		@Override
		public Object call(Context cx, Scriptable scope,Scriptable thisObj, Object[] args) {
			Object expected = args[0];
			Object actual = args[1];
			Assert.assertEquals(expected, actual);
			return null;
		}
	}

	public void init() {
		defineProperty("equals", new EqualsFunction(), PERMANENT);
		defineProperty("notNull",new NotNullFunction(),PERMANENT);
	}
	
	@Override
	public String getClassName() {
		return "Assert";
	}

	
	
}
