package de.enough.skylight.renderer.debug;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.node.CssElement;

/**
 * @author Andre
 *
 */
public class BuildDebug {
	static String INDENT = "    ";
	
	public static void printCssElement(CssElement element) {
		printCssElement(element,0);
	}
	
	static void printCssElement(CssElement element, int depth) {
		String indent = getIndent(depth);
		
		//#debug sl.debug.build
		System.out.println(indent + element);
		
		for (int index = 0; index < element.size(); index++) {
			CssElement childElement = element.get(index);
			printCssElement(childElement,depth+1);
		}
	}
	
	public static void printBlock(ContainingBlock block) {
		//#debug sl.debug.build
		System.out.println("");
		
		printBlock(block,0);
	}
	
	static void printBlock(ContainingBlock block, int depth) {
		String indent = getIndent(depth);
		Container blockContainer = block;
		
		//#debug sl.debug.build
		System.out.println(indent + block);
		
		for (int index = 0; index < blockContainer.size(); index++) {
			Item item = blockContainer.get(index);
			
			if(item instanceof ContainingBlock) {
				printBlock((ContainingBlock)item,depth+1);
			} else {
				//#debug sl.debug.build
				System.out.println(getIndent(depth+1) + item);
			}
		}
	}
	
	static String getIndent(int depth) {
		StringBuffer indent = new StringBuffer();
		
		for (int index = 0; index < depth; index++) {
			indent.append(INDENT);
		}
		
		return indent.toString();
	}
}
