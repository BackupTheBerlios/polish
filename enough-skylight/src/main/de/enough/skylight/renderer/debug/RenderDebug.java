package de.enough.skylight.renderer.debug;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.util.ToStringHelper;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.node.CssElement;

/**
 * @author Andre
 *
 */
public class RenderDebug {
	public static void printClipping(Graphics g) {
		System.out.println(
			new ToStringHelper("Clipping").
			add("x", g.getClipX()).
			add("y", g.getClipY()).
			add("width", g.getClipWidth()).
			add("height", g.getClipHeight()).
			toString()
		);
	}
}
