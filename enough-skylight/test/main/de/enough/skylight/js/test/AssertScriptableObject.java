package de.enough.skylight.js.test;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * This class helps with unit testing the javascript side of the DOM. It will only work with J2SE, not J2ME.
 * @author rickyn
 *
 */
public class AssertScriptableObject extends ScriptableObject{

	public class FailFunction extends BaseFunction {
		@Override
		public Object call(Context cx, Scriptable scope,Scriptable thisObj, Object[] args) {
			if(args.length > 0) {
				throw new AssertionFailedError((String)args[0]);
			}
			throw new AssertionFailedError();
		}
	}

	public final class NotNullFunction extends BaseFunction {
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
		defineProperty("fail",new FailFunction(),PERMANENT);
	}
	
	@Override
	public String getClassName() {
		return "Assert";
	}

	
	
}
