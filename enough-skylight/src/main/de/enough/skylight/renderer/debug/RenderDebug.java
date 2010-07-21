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
			set("x", g.getClipX()).
			set("y", g.getClipY()).
			set("width", g.getClipWidth()).
			set("height", g.getClipHeight()).
			toString()
		);
	}
}
