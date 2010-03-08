package de.enough.skylight.renderer.element.render;

import de.enough.skylight.renderer.linebox.LineBox;
import de.enough.skylight.renderer.partition.Partition;

public class Culling {
	public static boolean isVisible(Partition partition, LineBox linebox) {
		return !(partition.getInlineRelativeRight() <= linebox.getInlineRelativeLeft() || partition.getInlineRelativeLeft() >= linebox.getInlineRelativeRight());
	}
}
