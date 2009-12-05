package de.enough.skylight;

public class Frame {

	private static Frame frame;
	
	private JsEngine jsEngine;
	
	public static Frame getInstance() {
		if(frame == null) {
			frame = new Frame();
		}
		return frame;
	}
	
	private Frame() {
		this.jsEngine = new JsEngine();
	}
	
	public JsEngine getJsEngine() {
		return this.jsEngine;
	}
}
