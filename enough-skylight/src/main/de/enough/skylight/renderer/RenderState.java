package de.enough.skylight.renderer;

public interface RenderState {
	public static int VOID = Integer.MIN_VALUE;
	
	public static int START = 0x01;
	
	public static int BUILD_DOCUMENT = 0x02;
	
	public static int BUILD_VIEW = 0x03;
	
	public static int PREFETCH = 0x04;
	
	public static int READY = 0x05;
	
	public void onRenderState(Renderer renderer, int state);
}
