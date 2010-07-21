package de.enough.skylight.renderer;

/**
 * An interface to inform implementing instance
 * about processes in a renderer
 * @author Andre Schmidt
 *
 */
public interface RendererListener {
	
	/**
	 * @param state the state of the renderer
	 */
	public void onRenderStateChange(int state);
}
