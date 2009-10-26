package de.enough.skylight.renderer;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.StringItem;
import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.Event;
import de.enough.skylight.dom.EventListener;

public class HtmlContainer extends Container{

	class DomChangeListener implements EventListener{
		public void handleEvent(Event evt) {
			// TODO Auto-generated method stub
		}
	}

	private DomChangeListener domChangeListener;
	private Document document;
	
	public HtmlContainer() {
		super(false);
		this.domChangeListener = new DomChangeListener();
	}

	public void setDocument(Document document) {
		this.document = document;
		document.addEventListener("", this.domChangeListener, false);
		add(new StringItem("Document:",document.getNodeName()));
	}

	
}
