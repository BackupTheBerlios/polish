/*
 * Created on Mar 2, 2005 at 12:57:26 PM.
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
package de.enought.polish.plugin.eclipse.css.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;

import org.eclipse.jface.text.Document;

import de.enough.polish.plugin.eclipse.css.model.ASTNode;
import de.enough.polish.plugin.eclipse.css.model.CssModel;
import de.enough.polish.plugin.eclipse.css.parser.CssParser;
import de.enough.polish.plugin.eclipse.css.parser.CssTokenizer;
import junit.framework.TestCase;



/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Mar 2, 2005 - ricky creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CssParserTest extends TestCase {
	
	CssModel cssModel;
	CssTokenizer tokenizer;
	
	public void setUp(){
		System.out.println("CssTokenizerTest.setUp().enter.");
		
		String cssFile = "/Users/ricky/workspace/enough-polish-plugin-eclipse/misc/example.css";
		
		BufferedReader bufferedReader;
		String input = "";
		File file = new File(cssFile);
		try{
			bufferedReader = new BufferedReader(new FileReader(file));
			StringBuffer buffer = new StringBuffer();
			String line;
			while((line=bufferedReader.readLine())!=null){
				buffer.append(line).append('\n');
			}
			input = buffer.toString();
		}
		catch(Exception exception){
			System.out.println("CssTokenizerTest.setUp().File not found:"+cssFile);
			fail();
			return;
		}
		
		this.cssModel = new CssModel(new Document());
		this.tokenizer = new CssTokenizer(input,this.cssModel);
		this.tokenizer.tokenize();
	}
	
	public void testParseAll(){
		CssParser parser = new CssParser(this.cssModel);
		parser.parseAll();
		walkModel(this.cssModel.getRoot());
		
	}
	
	private void walkModel(ASTNode root){
		System.out.println("-->"+root);
		ASTNode nextNode;
		Iterator iterator = root.getChildren().iterator();
		while(iterator.hasNext()){
			nextNode = (ASTNode) iterator.next();
			walkModel(nextNode);
		}
		System.out.println("<--"+root);
	}


}
