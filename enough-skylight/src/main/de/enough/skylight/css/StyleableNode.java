package de.enough.skylight.css;

public interface StyleableNode {

	public Box getCssBox();
	public CssRule getInlineRule();
	public void setCssBox(Box box);
	/**
	 * 
	 * @param name the name of this box like :link or :hover
	 * @param box
	 */
	public void setCssBox(String name, Box box);
}
