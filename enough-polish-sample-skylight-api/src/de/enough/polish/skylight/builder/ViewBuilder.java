package de.enough.polish.skylight.builder;

import de.enough.polish.skylight.builder.view.HandlerDirectory;
import de.enough.polish.skylight.builder.view.NodeHandler;
import de.enough.polish.ui.Container;
import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.NodeList;

public class ViewBuilder {
	Document document;
	Container root;
	
	public ViewBuilder(Container root, Document document) {
		this.root = root;
		this.document = document;
	}
	
	public void build() {
		try {
			this.root.clear();
			
			handleNode(this.root,this.document.getDocumentElement());
		} catch(Exception e) {
			//#debug error
			System.out.println("error while building view : " + e);
			e.printStackTrace();
		}
	}
	
	protected void handleNode(Container parent, DomNode domNode) {
		NodeHandler handler = HandlerDirectory.getHandler(domNode);
		
		if(handler != null) {
			handler.handleNode(parent, domNode);
			
			if(domNode.hasChildNodes())
			{
				NodeList nodes = domNode.getChildNodes();
				
				Container containingBlock = handler.createContainingBlock();
				for (int index = 0; index < nodes.getLength(); index++) {
					DomNode node = nodes.item(index);
					
					handleNode(containingBlock, node);
				}
				
				parent.add(containingBlock);
			}
		} else {
			//#debug error
			System.out.println("no handler found for " + domNode.getNodeName());
		}
	}
}
