package de.enough.skylight.renderer.layout;

public class LayoutUtils {
	public static int getMiddle(int y, int availableHeight, int height) {
		return y + ((availableHeight - height) / 2);
	}
	
	public static int getBottom(int y, int availableHeight, int height) {
		return y + (availableHeight - height);
	}
}
