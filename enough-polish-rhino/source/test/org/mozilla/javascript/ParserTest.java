/*
 * Created on Dec 12, 2008 at 10:18:12 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package org.mozilla.javascript;

import junit.framework.TestCase;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ParserTest extends TestCase
implements ErrorReporter
{

	public void testParser() {
		CompilerEnvirons compilerEnvirons = new CompilerEnvirons();
		Parser parser = new Parser( compilerEnvirons, this );
		String code;
		ScriptOrFnNode node;
		Interpreter interpreter = new Interpreter();
		int count = 1;

		code = "x = 10; y = 20; z = (x + y) >> 1;";
		node = parser.parse(code, "http://enough.de/test.js", 0);
		InterpreterData data = interpreter.compile(compilerEnvirons, node, code, false);
		Context ctx = new Context();
		ctx.topCallScope = new NativeObject();
		Scriptable scope = new NativeObject();
		InterpretedFunction function = InterpretedFunction.createFunction( ctx, scope, data );
		Object[] args = new Object[0];
		Object returnValue = Interpreter.interpret(function, ctx, scope, null, args);
		assertEquals( (10 + 20) >> 1, Double.parseDouble(returnValue.toString()), 0.0D );
		
		
		code = "x = 0x10; y = 0x20; z = (x + y) >> 2;";
		node = parser.parse(code, "http://enough.de/test.js", 0);
		interpreter = new Interpreter();
		data = interpreter.compile(compilerEnvirons, node, code, false);
		function = InterpretedFunction.createFunction( ctx, scope, data );
		returnValue = Interpreter.interpret(function, ctx, scope, null, args);
		assertEquals( (double)((0x10 + 0x20) >> 2), Double.parseDouble(returnValue.toString()), 0.0D );
		
	}

	/* (non-Javadoc)
	 * @see org.mozilla.javascript.ErrorReporter#error(java.lang.String, java.lang.String, int, java.lang.String, int)
	 */
	public void error(String message, String sourceName, int line,
			String lineSource, int lineOffset)
	{
		// TODO robertvirkus implement error
		
	}

	/* (non-Javadoc)
	 * @see org.mozilla.javascript.ErrorReporter#runtimeError(java.lang.String, java.lang.String, int, java.lang.String, int)
	 */
	public EvaluatorException runtimeError(String message, String sourceName,
			int line, String lineSource, int lineOffset)
	{
		// TODO robertvirkus implement runtimeError
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mozilla.javascript.ErrorReporter#warning(java.lang.String, java.lang.String, int, java.lang.String, int)
	 */
	public void warning(String message, String sourceName, int line,
			String lineSource, int lineOffset)
	{
		// TODO robertvirkus implement warning
		
	}
}
