package de.enough.skylight.renderer.css;

public interface StyledObject {

	/**
	 * The specified value is directly written in the style document.
	 * @param propertyName
	 * @return
	 */
	public CSSValue getSpecifiedValue(String propertyName);
	public void setSpecifiedValue(String propertyName, CSSValue value);
	
	/**
	 * The computed value is used by the modeller. All urls are absolute, the 'em' sizes are computed to a pixel value.
	 * Values are inherited if the property allows it. But percentages are not yet computed.
	 * @param propertyName
	 * @return
	 */
	public CSSValue getComputedValue(String propertyName);
	
	/**
	 * The used value is used by the renderer. All values are resolved to actual pixel values.
	 * @param propertyName
	 * @return
	 */
	public int getUsedintValue(String propertyName);
	public String getUsedStringValue(String propertyName);
}
