package de.enought.polish.plugin.eclipse.css.parser;

import junit.framework.TestCase;

/*
 * Created on Mar 1, 2005 at 10:15:22 AM.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */

/**
 * <p>This testclass is not a unittest. Its for visual debugging only. That means looking at the output and
 * let the brain decide if it is correct or not.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Mar 1, 2005 - ricky creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CssTokenizerTest extends TestCase {
/*
	String input;
	CssModel cssModel;
	
	public void setUp(){
		System.out.println("CssTokenizerTest.setUp().enter.");
		
		String cssFile = "misc/example.css";
		
		BufferedReader bufferedReader;
		this.input = "";
		File file = new File(cssFile);
		try{
			bufferedReader = new BufferedReader(new FileReader(file));
			StringBuffer buffer = new StringBuffer();
			String line;
			while((line=bufferedReader.readLine())!=null){
				buffer.append(line).append('\n');
			}
			this.input = buffer.toString();
		}
		catch(Exception exception){
			System.out.println("CssTokenizerTest.setUp().File not found:"+cssFile);
			fail();
			return;
		}
		
		this.cssModel = new CssModel(new Document());
	}
	
	public void XtestGetNext(){
		CssTokenizer cssTokenizer = new CssTokenizer(this.input,this.cssModel);
		CssToken cssToken;
		cssToken = cssTokenizer.getNextToken();
		System.out.println(cssToken);
		cssToken = cssTokenizer.getNextToken();
		System.out.println(cssToken);
		cssToken = cssTokenizer.getNextToken();
		System.out.println(cssToken);
	}
	
	public void testTokenize(){
		
		CssTokenizer cssTokenizer = new CssTokenizer(this.input,this.cssModel);
		cssTokenizer.tokenize();
		//List cssTokens = cssTokenizer.getCssTokens();
		List cssTokens = this.cssModel.getTokenList();
		
		System.out.println("CssTokenizerTest.testTokenize():enter.");
		Iterator i = cssTokens.iterator();
		while(i.hasNext()){
			CssToken cssToken = (CssToken)i.next();
			System.out.println(cssToken);
		}
		System.out.println("CssTokenizerTest.testTokenize():# of tokens:"+cssTokens.size());
		System.out.println("CssTokenizerTest.testTokenize():end.");
	}
	*/
}
