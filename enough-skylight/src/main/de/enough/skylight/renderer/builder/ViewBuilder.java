package de.enough.skylight.renderer.builder;

import de.enough.polish.ui.Container;
import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.NodeList;
import de.enough.skylight.renderer.builder.view.HandlerDirectory;
import de.enough.skylight.renderer.builder.view.NodeHandler;
import de.enough.skylight.renderer.builder.view.NodeUtils;

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
	
	protected void handleNode(Container parent, DomNode node) {
		NodeHandler handler = HandlerDirectory.getHandler(node);
		
		if(handler != null) {
			handler.handleNode(parent, node);
			
			if(node.hasChildNodes())
			{
				NodeList nodes = node.getChildNodes();
				
				Container containingBlock = handler.createContainingBlock();
				for (int index = 0; index < nodes.getLength(); index++) {
					DomNode childNode = nodes.item(index);
					
					handleNodeByType(handler, containingBlock, childNode);
				}
				
				parent.add(containingBlock);
			}
		} else {
			//#debug error
			System.out.println("no handler found for " + node.getNodeName());
		}
	}
	
	protected void handleNodeByType(NodeHandler handler, Container containingBlock, DomNode node) {
		switch(node.getNodeType()) {
			case DomNode.TEXT_NODE : 
				handler.handleText(containingBlock, node.getNodeValue()); 
				break;
			default: 
				handleNode(containingBlock, node);  
		}
	}
}
