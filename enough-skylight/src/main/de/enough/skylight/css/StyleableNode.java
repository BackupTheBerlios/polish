package de.enough.skylight.css;

public interface StyleableNode {

	public Box getCssBox();
	public CssRule getInlineRule();
	public void setCssBox(Box box);
	/**
	 * Give a box a special name. It is possible for an element to have several boxes like :link and :hover
	 * @param name the name of this box like :link or :hover. If null, a normal box is assumed.
	 * @param box
	 */
	public void setCssBox(String name, Box box);
}
