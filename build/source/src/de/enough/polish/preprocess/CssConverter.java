/*
 * Created on 04-Jan-2004 at 23:04:12.
 *
 * Copyright (c) 2004 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.preprocess;

import de.enough.polish.Device;
import de.enough.polish.util.AbbreviationsGenerator;
import de.enough.polish.util.StringList;
import de.enough.polish.util.TextUtil;

import org.apache.tools.ant.BuildException;

import java.util.*;

/**
 * <p>Converts CSS files to Java-Code.</p>
 *
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        04-Jan-2004 - rob creation
 * </pre>
 */
public class CssConverter extends Converter {
	
	private static final String INCLUDE_MARK = "//$$IncludeStyleSheetDefinitionHere$$//";
	private static final HashMap BACKGROUND_TYPES = new HashMap();
	static {
		BACKGROUND_TYPES.put( "simple", "de.enough.polish.preprocess.backgrounds.SimpleBackgroundConverter");
		BACKGROUND_TYPES.put( "plain", "de.enough.polish.preprocess.backgrounds.SimpleBackgroundConverter");
		BACKGROUND_TYPES.put( "image", "de.enough.polish.preprocess.backgrounds.ImageBackgroundConverter");
		BACKGROUND_TYPES.put( "roundrect", "de.enough.polish.preprocess.backgrounds.RoundRectBackgroundConverter");
		BACKGROUND_TYPES.put( "round-rect", "de.enough.polish.preprocess.backgrounds.RoundRectBackgroundConverter");
		BACKGROUND_TYPES.put( "pulsating", "de.enough.polish.preprocess.backgrounds.PulsatingBackgroundConverter");
		BACKGROUND_TYPES.put( "pulsating-circle", "de.enough.polish.preprocess.backgrounds.PulsatingCircleBackgroundConverter");
		BACKGROUND_TYPES.put( "pulsating-circles", "de.enough.polish.preprocess.backgrounds.PulsatingCirclesBackgroundConverter");
		BACKGROUND_TYPES.put( "circle", "de.enough.polish.preprocess.backgrounds.CircleBackgroundConverter");
		BACKGROUND_TYPES.put( "opening", "de.enough.polish.preprocess.backgrounds.OpeningBackgroundConverter");
		BACKGROUND_TYPES.put( "round-rect-opening", "de.enough.polish.preprocess.backgrounds.RoundRectOpeningBackgroundConverter");
		BACKGROUND_TYPES.put( "opening-round-rect", "de.enough.polish.preprocess.backgrounds.RoundRectOpeningBackgroundConverter");
	}
	private static final HashMap BORDER_TYPES = new HashMap();
	static {
		BORDER_TYPES.put( "simple", "de.enough.polish.preprocess.borders.SimpleBorderConverter");
		BORDER_TYPES.put( "plain", "de.enough.polish.preprocess.borders.SimpleBorderConverter");
		BORDER_TYPES.put( "bottom-right-shadow", "de.enough.polish.preprocess.borders.ShadowBorderConverter");
		BORDER_TYPES.put( "right-bottom-shadow", "de.enough.polish.preprocess.borders.ShadowBorderConverter");
		BORDER_TYPES.put( "shadow", "de.enough.polish.preprocess.borders.ShadowBorderConverter");
		BORDER_TYPES.put( "round-rect", "de.enough.polish.preprocess.borders.RoundRectBorderConverter");
		BORDER_TYPES.put( "circle", "de.enough.polish.preprocess.borders.CircleBorderConverter");
	}
	private static HashMap LAYOUTS = new HashMap();
	static {
		LAYOUTS.put( "default", "Item.LAYOUT_DEFAULT" );
		LAYOUTS.put( "plain", "Item.LAYOUT_DEFAULT" );
		LAYOUTS.put( "none", "Item.LAYOUT_DEFAULT" );
		LAYOUTS.put( "left", "Item.LAYOUT_LEFT" );
		LAYOUTS.put( "right", "Item.LAYOUT_RIGHT" );
		LAYOUTS.put( "center", "Item.LAYOUT_CENTER" );
		LAYOUTS.put( "hcenter", "Item.LAYOUT_CENTER" );
		LAYOUTS.put( "horizontal-center", "Item.LAYOUT_CENTER" );
		LAYOUTS.put( "top", "Item.LAYOUT_TOP" );
		LAYOUTS.put( "bottom", "Item.LAYOUT_BOTTOM" );
		LAYOUTS.put( "vcenter", "Item.LAYOUT_VCENTER" );
		LAYOUTS.put( "vertical-center", "Item.LAYOUT_VCENTER" );
		LAYOUTS.put( "newline-before", "Item.LAYOUT_NEWLINE_BEFORE" );
		LAYOUTS.put( "newline-after", "Item.LAYOUT_NEWLINE_AFTER" );
		LAYOUTS.put( "shrink", "Item.LAYOUT_SHRINK" );
		LAYOUTS.put( "hshrink", "Item.LAYOUT_SHRINK" );
		LAYOUTS.put( "horizontal-shrink", "Item.LAYOUT_SHRINK" );
		LAYOUTS.put( "expand", "Item.LAYOUT_EXPAND" );
		LAYOUTS.put( "hexpand", "Item.LAYOUT_EXPAND" );
		LAYOUTS.put( "horizontal-expand", "Item.LAYOUT_EXPAND" );
		LAYOUTS.put( "vshrink", "Item.LAYOUT_VSHRINK" );
		LAYOUTS.put( "vertical-shrink", "Item.LAYOUT_VSHRINK" );
		LAYOUTS.put( "vexpand", "Item.LAYOUT_VEXPAND" );
		LAYOUTS.put( "vertical-expand", "Item.LAYOUT_VEXPAND" );
	}
	private static final HashMap VIEW_TYPES = new HashMap();
	static {
		VIEW_TYPES.put( "shuffle", "de.enough.polish.ui.containerviews.ShuffleView");
		VIEW_TYPES.put( "ShuffleView", "de.enough.polish.ui.containerviews.ShuffleView");
		VIEW_TYPES.put( "dropping", "de.enough.polish.ui.containerviews.DroppingView");
		VIEW_TYPES.put( "DroppingView", "de.enough.polish.ui.containerviews.DroppingView");
		VIEW_TYPES.put( "plain", "none");
		VIEW_TYPES.put( "normal", "none");
	}
	private ArrayList referencedStyles;
	private AbbreviationsGenerator abbreviationGenerator;
	private CssAttributesManager attributesManager;
	
	/**
	 * Creates a new CSS converter
	 * 
	 * @param attributesManager the manager for CSS attributes
	 */
	public CssConverter( CssAttributesManager attributesManager ) {
		this.colorConverter = new ColorConverter();
		this.attributesManager = attributesManager;
	}
	

	public void convertStyleSheet( StringList sourceCode, 
								   StyleSheet styleSheet, 
								   Device device,
								   Preprocessor preprocessor ) 
	{
		this.abbreviationGenerator = null;
		// search for the position to include the style-sheet definitions:
		int index = -1;
		while (sourceCode.next()) {
			String line = sourceCode.getCurrent();
			if (INCLUDE_MARK.equals(line)) {
				index = sourceCode.getCurrentIndex() + 1;
				break;
			}
		}
		if (index == -1) {
			throw new BuildException("Unable to modify StyleSheet.java, include point [" + INCLUDE_MARK + "] not found.");
		}
		// okay start with the creation of source code:
		ArrayList codeList = new ArrayList();
		this.referencedStyles = new ArrayList();
		// initialise the syle sheet:
		styleSheet.inherit();
		// set the color-definitions:
		this.colorConverter.setTemporaryColors( styleSheet.getColors() );
		// add the font-definitions:
		boolean defaultFontDefined = false;
		HashMap fonts = styleSheet.getFonts();
		Set keys = fonts.keySet();
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			String groupName = (String) iter.next();
			if ("default".equals(groupName)) {
				defaultFontDefined = true;
				HashMap group = (HashMap) fonts.get( groupName );
				processFont(group, groupName, null, codeList, styleSheet, true );
			} else {
				HashMap group = (HashMap) fonts.get( groupName );
				processFont(group, groupName, null, codeList, styleSheet, true );
			}
		}
		
		// add the backgrounds-definition:
		boolean defaultBackgroundDefined = false;
		HashMap backgrounds = styleSheet.getBackgrounds();
		keys = backgrounds.keySet();
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			String groupName = (String) iter.next();
			if ("default".equals(groupName)) {
				defaultBackgroundDefined = true;
			} 
			HashMap group = (HashMap) backgrounds.get( groupName );
			processBackground(groupName, group, null, codeList, styleSheet, true );
		}
		
		// add the borders-definition:
		boolean defaultBorderDefined = false;
		HashMap borders = styleSheet.getBorders();
		keys = borders.keySet();
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			String groupName = (String) iter.next();
			if ("default".equals(groupName)) {
				defaultBorderDefined = true;
			} 
			HashMap group = (HashMap) borders.get( groupName );
			processBorder(groupName, group, null, codeList, styleSheet, true );
		}
		String test = preprocessor.getVariable("polish.license");
		if ( "GPL".equals(test) ) {
			// GPL license is fine.
		} else if (test == null || test.length() != 12) {
			throw new BuildException("Encountered invalid license.");
		} else {
			try {
				Long.parseLong(test, 0x10);
			} catch (Exception e) {
				throw new BuildException("Encountered invalid license.");
			}
		}
		
		// add the default style:
		processDefaultStyle( defaultFontDefined,  
				defaultBackgroundDefined, defaultBorderDefined,
				codeList, styleSheet, device );
		
		
		// now add all other static and referenced styles:
		ArrayList defaultStyleNames = new ArrayList();
		defaultStyleNames.add( "default");
		defaultStyleNames.add( "label");
		defaultStyleNames.add( "focused");
		// check if fullscreen mode is enabled with menu:
		if ((preprocessor.hasSymbol("polish.useMenuFullScreen") 
			&& ((device.getCapability("polish.classes.fullscreen") != null) 
					|| (device.isMidp2() && preprocessor.hasSymbol("polish.hasCommandKeyEvents") ) ))
			|| preprocessor.hasSymbol("polish.needsManualMenu") 
			) 
		{
			defaultStyleNames.add( "menu");
			defaultStyleNames.add( "menuitem");
		}
		
		String[] defaultNames = (String[]) defaultStyleNames.toArray( new String[ defaultStyleNames.size() ]);
		Style[] styles = styleSheet.getUsedAndReferencedStyles(defaultNames);
		boolean isLabelStyleReferenced = false;
		codeList.add("\t//static and referenced styles:");
		for (int i = 0; i < styles.length; i++) {
			Style style = styles[i];
			if (!"default".equals(style.getSelector())) {
				if ("label".equals(style.getSelector())) {
					isLabelStyleReferenced = true;
				}
				processStyle( style, codeList, styleSheet, device );
			}
		}
		codeList.add( STANDALONE_MODIFIER + "String lic=\"" + test +"\";");
		
		/*
		String[] styleNames = styleSheet.getUsedStyleNames();
		//System.out.println("processing [" + styleNames.length + "] styles.");
		int insertIndexForReferencedStyles = codeList.size();
		codeList.add("\t//normal used styles:");
		for (int i = 0; i < styleNames.length; i++) {
			String styleName = styleNames[i];
			if (!styleName.equals("default")) {
				Style style = styleSheet.getStyle( styleName );
				processStyle( style, codeList, styleSheet, device );
			}
		}
		codeList.add( STANDALONE_MODIFIER + "String lic=\"" + test +"\";");
		
		// process referenced styles:
		styles = (Style[]) this.referencedStyles.toArray( new Style[ this.referencedStyles.size() ] );
		boolean isLabelStyleReferenced = false;
		if (styles.length > 0) {
			ArrayList referencedCode = new ArrayList();
			referencedCode.add("\t//referenced styles:");
			for (int i = styles.length -1; i >= 0; i--) {
				Style style = styles[i];
				if (style.getSelector().equals("label")) {
					isLabelStyleReferenced = true;
				}
				if (!styleSheet.isUsed(style.getSelector())) {
					processStyle( style, referencedCode, styleSheet, device );
				}
			}
			codeList.addAll( insertIndexForReferencedStyles, referencedCode );
		}
		*/
		
		// check if label-style has been defined:
		if (!isLabelStyleReferenced) {
			//if (styleSheet.getStyle("label" ) == null) {
				codeList.add( STANDALONE_MODIFIER + "Style labelStyle = defaultStyle; // no specific label-style has been defined");
			/*} else {
				processStyle( styleSheet.getStyle("label" ), codeList, styleSheet, device );
			}*/
		}
		
		
		// check if fullscreen mode is enabled with menu:
		/*
		if ((preprocessor.hasSymbol("polish.useMenuFullScreen") 
			&& ((device.getCapability("polish.classes.fullscreen") != null) 
					|| (device.isMidp2() && preprocessor.hasSymbol("polish.hasCommandKeyEvents") ) ))
			|| preprocessor.hasSymbol("polish.needsManualMenu") 
			) {
			if (styleSheet.getStyle("menu" ) == null) {
				System.out.println("Warning: CSS style [menu] not found, you should define it for designing the FullScreen-menu.");
			} else {
				//processStyle( styleSheet.getStyle("menu" ), codeList, styleSheet, device );
				this.referencedStyles.add(styleSheet.getStyle("menu" ));
			}
		}
		*/
		
		// process dynamic styles:
		if (styleSheet.containsDynamicStyles()) {
			codeList.add("\t//dynamic styles:");
			Style[] dynamicStyles = styleSheet.getDynamicStyles(); 
			for (int i = 0; i < dynamicStyles.length; i++) {
				Style style = dynamicStyles[i];
				//processStyle( style, codeList, styleSheet, device );
				this.referencedStyles.add( style );
			}
		}
		
		
		// register referenced and dynamic styles:
		codeList.add("\tstatic final Hashtable stylesByName = new Hashtable(" + styles.length + ");");
		if (this.referencedStyles.size() > 0) {
			styles = (Style[]) this.referencedStyles.toArray( new Style[ this.referencedStyles.size() ] );
			codeList.add("\tstatic { \t//register referenced and dynamic styles:");
			for (int i = 0; i < styles.length; i++) {
				Style style = styles[i];
				String name = style.getAbbreviation();
				if (name == null) {
					name = style.getSelector();
				}
				codeList.add("\t\tstylesByName.put( \"" + name + "\", " + style.getStyleName() + "Style );");
			}
			codeList.add("\t}");
		}

		
		// create focused style if necessary:
		Style focusedStyle = styleSheet.getStyle("focused"); 
		if (focusedStyle == null) {
			System.out.println("Warning: CSS-Style [focused] not found, now using the default style instead. If you use Forms or Lists, you should define the style [focused].");
			codeList.add( STANDALONE_MODIFIER + "Style focusedStyle = defaultStyle;\t// the focused-style is not defined.");
		//} else {
		//	processStyle( focusedStyle, codeList, styleSheet, device );
		}
		
		// generate general warnings and hints:
		// check if title style has beend defined:
		if (styleSheet.getStyle("title" ) == null) {
			System.out.println("Warning: CSS style [title] not found, you should define it for designing the titles of screens.");
		}
		
		
		// now insert the created source code into the source of the polish-StyleSheet.java:
		String[] code = (String[]) codeList.toArray( new String[ codeList.size()]);
		sourceCode.insert(code);
	}


	/**
	 * Processes the default style:
	 * 
	 * @param defaultFontDefined true when the default font has been defined already
	 * @param defaultBackgroundDefined true when the default background has been defined already
	 * @param defaultBorderDefined true when the default border has been defined already
	 * @param codeList the list to which the declarations should be added
	 * @param styleSheet the parent style sheet
	 * @param device the device for which the style should be processed
	 */
	private void processDefaultStyle(boolean defaultFontDefined, boolean defaultBackgroundDefined, boolean defaultBorderDefined, ArrayList codeList, StyleSheet styleSheet, Device device) {
		//System.out.println("PROCESSSING DEFAULT STYLE " + styleSheet.getStyle("default").toString() );
		Style copy = new Style( styleSheet.getStyle("default"));
		HashMap group = copy.getGroup("font");
		if (!defaultFontDefined) {
			if (group == null) {
				codeList.add( STANDALONE_MODIFIER + "int defaultFontColor = 0x000000;");
				codeList.add( STANDALONE_MODIFIER + "Font defaultFont = Font.getDefaultFont();");
			} else {
				processFont(group, "default", copy, codeList, styleSheet, true );
			}
		}
		group = copy.getGroup("background");
		if (!defaultBackgroundDefined) {
			if (group == null) {
				codeList.add( STANDALONE_MODIFIER + "Background defaultBackground = null;");
			} else {
				processBackground("default", group, copy, codeList, styleSheet, true );
			}
		}
		group = copy.getGroup("border");
		if (!defaultBorderDefined) {
			if (group == null) {
				codeList.add( STANDALONE_MODIFIER + "Border defaultBorder = null;");
			} else {
				processBorder("default", group, copy, codeList, styleSheet, true );
			}
		}
		// set default values:
		//copy.setSelector("defaultStyle");
		group = new HashMap();
		group.put("font", "default");
		copy.addGroup("font", group );
		group = new HashMap();
		group.put("label", "default");
		copy.addGroup("label", group );
		group = new HashMap();
		group.put("background", "default");
		copy.addGroup("background", group );
		group = new HashMap();
		group.put("border", "default");
		copy.addGroup("border", group );
		// now process the rest of the style completely normal:
		processStyle(copy, codeList, styleSheet, device);
	}


	/**
	 * Processes the give style and includes the generated code to the codeList.
	 * 
	 * @param style the style
	 * @param codeList the array list into which generated code is written
	 * @param styleSheet the parent style sheet
	 * @param device the device for which the style should be processed
	 */
	private void processStyle(Style style, ArrayList codeList, StyleSheet styleSheet, Device device) {
		String styleName = style.getStyleName();
		//System.out.println("processing style " + style.getStyleName() + ": " + style.toString() );
		// create a new style:
		codeList.add( STANDALONE_MODIFIER + "Style " + styleName + "Style = new Style (");
		// process the margins:
		HashMap group = style.removeGroup("margin");
		if ( group != null ) {
			processFields( 0, false, group, "margin", style, codeList, styleSheet, device );
		} else {
			codeList.add("\t\t0,0,0,0,\t// default margin");
		}
		// process the paddings:
		group = style.removeGroup("padding");
		if ( group != null ) {
			processFields( 1, true, group, "padding", style, codeList, styleSheet, device );
		} else {
			codeList.add("\t\t1,1,1,1,1,1,\t// default padding");
		}
		// process the layout:
		group = style.removeGroup("layout");
		if ( group != null ) {
			processLayout( group, style, codeList, styleSheet );
		} else {
			codeList.add("\t\tItem.LAYOUT_DEFAULT,\t// default layout");
		}
		// process the content-font:
		group = style.removeGroup("font");
		String bitMapFontUrl = null;
		if ( group != null ) {
			// if a bitmap-font is defined,
			// add a extended (string) attribute:
			bitMapFontUrl = (String) group.get("bitmap");
			if (bitMapFontUrl != null && !"none".equals( bitMapFontUrl)) {
				if (!bitMapFontUrl.endsWith(".bmf")) {
					bitMapFontUrl += ".bmf";
				}
				if (bitMapFontUrl.charAt(0) != '/') {
					bitMapFontUrl = "/" + bitMapFontUrl;
				}
				HashMap bitMapFontGroup = new HashMap(1);
				bitMapFontGroup.put("bitmap", bitMapFontUrl );
				style.addGroup( "font", bitMapFontGroup );
				//System.out.println("adding bitmap-font " + bitMapFontUrl);
			}
			processFont( group, "font", style, codeList, styleSheet, false );
		} else {
			codeList.add("\t\tdefaultFontColor,\t// font-color is not defined");
			codeList.add("\t\tdefaultFont,");
		}
		
		// process the content-font:
		boolean removeLabelGroup = true;
		group = style.getGroup("label");
		if (group != null) {
			String labelStyle = (String) group.get("style"); 
			if ( labelStyle != null ) {
				if (styleSheet.isDefined(labelStyle)) {
					// okay this is a valid reference:
					removeLabelGroup = false;
				}
			}
		}
		if (removeLabelGroup) {
			style.removeGroup("label");			
		}
		
		// process the background:
		group = style.removeGroup("background");
		if ( group != null ) {
			processBackground( style.getSelector(), group, style, codeList, styleSheet, false );
		} else {
			codeList.add("\t\tnull,\t// no background");
		}
		// process the background:
		group = style.removeGroup("border");
		if ( group != null ) {
			processBorder( style.getSelector(), group, style, codeList, styleSheet, false );
		} else {
			codeList.add("\t\tnull\t// no border");
		}
		// add the URL of the before-image but only when at least one before-element has been defined:
		if (styleSheet.containsBeforeStyle()) {
			group = style.removeGroup("before");
			if (group != null) {
				codeList.add("\t\t, \"" + getUrl( (String) group.get("before") ) + "\" // URL of the before element" );								
			} else {
				codeList.add("\t\t, null\t// no before element has been defined");				
			}
		}
		// add the URL of the after-image but only when at least one after-element has been defined:
		if (styleSheet.containsAfterStyle()) {
			group = style.removeGroup("after");
			if (group != null) {
				codeList.add("\t\t, \"" + getUrl( (String) group.get("after") ) + "\"\t// URL of the after element" );								
			} else {
				codeList.add("\t\t, null\t// no after element has been defined");				
			}
		}
		// add the selector of the style, but only when dynamic styles are used:
		if (styleSheet.containsDynamicStyles()) {
			codeList.add("\t\t, \"" + style.getSelector() + "\"\t// the selector of this style");
		}
		
		// now add all additional non standard-properties:
		String[] groupNames = style.getGroupNames();
		if (groupNames.length == 0) {
			codeList.add( "\t\t, null, null\t// no additional attributes have been defined" );
		} else {
			// counting the number of special attributes:
			int numberOfAttributes = 0;
			for (int i = 0; i < groupNames.length; i++) {
				String groupName = groupNames[i];
				group = style.getGroup(groupName);
				numberOfAttributes += group.size();
			}
			StringBuffer keyList = new StringBuffer();
			keyList.append("new short[]{ ");
			StringBuffer valueList = new StringBuffer();
			valueList.append("new Object[]{ ");
			int currentAttribute = 0;
			for (int i = 0; i < groupNames.length; i++) {
				String groupName = groupNames[i];
				group = style.getGroup(groupName);
				if (group == null) {
					System.err.println("unable to get group [" + groupName + "] of style : " + style.toString());
				}
				Set keys = group.keySet();
				for (Iterator iter = keys.iterator(); iter.hasNext();) {
					currentAttribute++;
					String key = (String) iter.next();
					String value = (String) group.get( key );
					String attributeName;
					if (key.equals(groupName)) {
						attributeName = groupName;
					} else {
						attributeName = groupName + "-" + key;
					}
					short attributesId = styleSheet.getAttributeId(attributeName);
					if (attributesId == -1) {
						throw new BuildException("Invalid CSS: The CSS-attribute [" + attributeName + "] is not supported. Please check your [polish.css] file(s).");
					}
					keyList.append( attributesId );
					if ( currentAttribute < numberOfAttributes) {
						keyList.append(", ");
					}
					
					int attributeType = CssAttribute.STRING;
					CssAttribute attribute = this.attributesManager.getAttribute( attributeName );
					if (attribute != null) {
						attributeType = attribute.getType();
						attribute.checkValue(value);
						if (attributeType == CssAttribute.INTEGER && attribute.hasFixValues()) {
							value = Integer.toString( attribute.getValuePosition(value) );
						}
					} else {
						// the attribute is not registered anywhere:
						System.out.println("Recommendation: It is advised to register the CSS-attribute [" + attributeName + "] in the [custom-css-attributes.xml].");
					}
					// process columns-width value:
					// remove all spaces and check for the star-value (e.g. "20, *"):
					if (key.equals("width") && groupName.equals("columns")) {
						value = TextUtil.replace( value, " ", "" );
						int starPos = value.indexOf('*');
						if (starPos != -1) {
							String screenWidthStr = device.getCapability("ScreenWidth");
							if (screenWidthStr != null) {
								int screenWidth = Integer.parseInt( screenWidthStr );
								String[] valueChunks = TextUtil.split( value, ',' );
								int combinedWidth = 0;
								int starIndex = -1;
								for (int j = 0; j < valueChunks.length; j++) {
									String widthStr = valueChunks[j];
									if ("*".equals( widthStr)) {
										starIndex = j;
									} else {
										combinedWidth += Integer.parseInt( widthStr );
									}
								}
								valueChunks[ starIndex ] = "" + ( screenWidth - combinedWidth);
								StringBuffer buffer = new StringBuffer();
								for (int j = 0; j < valueChunks.length; j++) {
									buffer.append( valueChunks[j] );
									if (j != valueChunks.length -1) {
										buffer.append(',');
									}
								}
								value = buffer.toString();
							}
						}
					}
					if (key.endsWith("style")) {
						value = getStyleReference( value, style, styleSheet );
					} else if (attributeType == CssAttribute.COLOR || key.endsWith("color")) {
						value = getColor( value );
						attributeType = CssAttribute.COLOR;
					} else if (key.equals("url")) {
						value = getUrl( value );
					}
					if (value.startsWith("url")) {
						value = getUrl( value );
					} else if (value.startsWith("style(")) {
						value = getStyleReference( value, style, styleSheet );
					}					
					if (attributeType == CssAttribute.STRING || attributeType == CssAttribute.CHAR) {
						valueList.append('"')
						.append( value )
						.append('"');
					} else if (attributeType == CssAttribute.STYLE) {
						valueList.append( value );
					} else if (attributeType == CssAttribute.COLOR 
							|| attributeType == CssAttribute.INTEGER ) {
						// check integer value:
						if (attributeType == CssAttribute.INTEGER ) {
							try {
								Integer.parseInt( value );
							} catch (NumberFormatException e) {
								throw new BuildException("Invalid CSS: The attribute [" + attributeName + "] needs an integer value. The value [" + value + "] cannot be accepted.");
							}
						}
						valueList.append( "new Integer( ")
						.append( value )
						.append(")");
					} else if (attributeType == CssAttribute.BOOLEAN) {
						if (value.equals("true") || value.equals("yes")) {
							valueList.append( "Style.TRUE" );
						} else if (value.equals("false") || value.equals("no")) {
							valueList.append( "Style.FALSE" );
						} else {
							throw new BuildException("Invalid CSS Code: invalid value for the boolean attribute [" + attributeName + "]:  use either [true]/[yes] or [false]/[no].");
						}
					} else if (attributeType == CssAttribute.OBJECT) {
						if (groupName.equals("view") && key.equals("type") ) {
							String translatedViewType = (String) VIEW_TYPES.get( value );
							if (translatedViewType == null ) {
								// the value represents a full class:
								valueList.append("new ").append( value ).append("()");
							} else if ("none".equals( translatedViewType)) {
								valueList.append("null");
							} else {
								// a standard view is used:
								valueList.append("new ").append( translatedViewType ).append("()");
							}
						}
					} else {
						throw new BuildException("Error while processing CSS code. Encountered unknown attributes-type [" + attributeType + "]: please report this error to j2mepolish@enough.de.");
					}
					if ( currentAttribute < numberOfAttributes) {
						valueList.append(", ");
					}					
				}
			}
			keyList.append("}");
			valueList.append("}");
			codeList.add( "\t\t, " + keyList.toString());
			codeList.add( "\t\t, " + valueList.toString());
		} // if there are any non-standard attribute-groups
		
		// close the style definition:
		codeList.add("\t);");
	}


	/**
	 * Retrieves the color value as a decimal integer value.
	 * 
	 * @param value the color
	 * @return the color as a decimal integer value
	 */
	private String getColor(String value) {
		String color = this.colorConverter.parseColor(value);
		if (color == ColorConverter.TRANSPARENT ) {
			return "-1";
		}
		//TODO rob this won't work with argb-colors
		return Integer.decode(color).toString();
	}


	/**
	 * Gets a reference to another style.
	 * 
	 * @param value the reference
	 * @param parent the parent style
	 * @param styleSheet the sheet in which the style is embedded
	 * @return the name of the reference style
	 */
	private String getStyleReference(String value, Style parent, StyleSheet styleSheet) {
		String reference = value.toLowerCase();
		if (value.startsWith("style(")) {
			reference = reference.substring( 6 );
			int closingPos = reference.indexOf(')');
			if (closingPos == -1) {
				throw new BuildException("Invalid CSS: the style-reference [" + value + "] in style [" + parent.getSelector() + "] needs to be closed by a parenthesis.");
			}
			reference = reference.substring( 0, closingPos ).trim();
		}
		if (reference.charAt(0) == '.') {
			reference = reference.substring( 1 );
		}
		if (this.abbreviationGenerator == null) {
			this.abbreviationGenerator = new AbbreviationsGenerator();
		}
		String abbreviation = this.abbreviationGenerator.getAbbreviation(reference, true );
		Style style = styleSheet.getStyle(reference);
		if (style == null) {
			throw new BuildException("Invalid CSS: the style-reference to [" + value + "] in style [" + parent.getSelector() + "] refers to a non-existing style.");
		}
		style.setAbbreviation( abbreviation );
		// add it to the list of referenced styles, 
		// but only when it has not been added before:
		if (! this.referencedStyles.contains(style)) {
			this.referencedStyles.add( style );
		}
		return reference + "Style"; //abbreviation;
	}


	/**
	 * Creates the layout-declaration for the style.
	 * 
	 * @param group the layout directive
	 * @param style the parent style
	 * @param codeList the source code
	 * @param styleSheet the parent style sheet
	 */
	private void processLayout(HashMap group, Style style, ArrayList codeList, 
			StyleSheet styleSheet) 
	{
		String layoutValue = (String) group.get("layout");
		if (layoutValue == null) {
			// this must be some other CSS definitions:
			style.addGroup( "layout", group);
			return;
		}
		layoutValue = layoutValue.toLowerCase();
		String[] layouts;
		// the layout value can combine several directives, e.g. "vcenter | hcenter"
		if ( layoutValue.indexOf('|') != -1 ) {
			layouts = TextUtil.splitAndTrim( layoutValue, '|');
		} else if ( layoutValue.indexOf('&') != -1 ) {
				layouts = TextUtil.splitAndTrim( layoutValue, '&');
		} else if ( layoutValue.indexOf(',') != -1 ) {
			layouts = TextUtil.splitAndTrim( layoutValue, ',');
		} else if ( layoutValue.indexOf(" and ") != -1 ) {
			layouts = TextUtil.splitAndTrim( layoutValue, " and ");
		} else if ( layoutValue.indexOf(" or ") != -1 ) {
			layouts = TextUtil.splitAndTrim( layoutValue, " or ");
		} else {
			layouts = new String[]{ layoutValue };
		}
		// now add definition for each layout:
		StringBuffer buffer = new StringBuffer();
		buffer.append( "\t\t" );
		for (int i = 0; i < layouts.length; i++) {
			boolean finished = (i == layouts.length -1 );
			String name = layouts[i];
			String layout = (String) LAYOUTS.get( name );
			if (layout == null) {
				if (layouts.length > 1) {
					throw new BuildException("Invalid CSS: the layout directive [" + layoutValue + "] contains the invalid layout [" + name +"]. Please use a valid value instead.");
				} else {
					throw new BuildException("Invalid CSS: the layout directive [" + name +"] is not valid. Please use a valid value instead.");
				}
			}
			buffer.append( layout );
			if (!finished) {
				buffer.append(" | ");
			}
		}
		buffer.append(",");
		codeList.add( buffer.toString() );
	}


	/**
	 * Creates the border-definition.
	 * 
	 * @param borderName the name of the border
	 * @param group the map containing the border information
	 * @param style the style
	 * @param codeList the array list into which generated code is written
	 * @param styleSheet the parent style sheet
	 * @param isStandalone true when a new public border-field should be created,
	 *        otherwise the border will be embedded in a style instantiation. 
	 */
	private void processBorder(String borderName, HashMap group, Style style, ArrayList codeList, StyleSheet styleSheet, boolean isStandalone ) {
		//System.out.println("processing border " + borderName + " = "+ group.toString() );
		String reference = (String) group.get("border");
		if (reference != null && group.size() == 1) {
			if ("none".equals(reference)) {
				if (isStandalone) {
					codeList.add( STANDALONE_MODIFIER + "Border " + borderName + "Border = null;\t// border:none was specified");
				} else {
					codeList.add( "\t\tnull\t// border:none was specified"); // no comma since border is the last argument
				}
			} else {
				// a reference to an existing border is given:
				if (isStandalone) {
					codeList.add( STANDALONE_MODIFIER + "Border " + borderName + "Border = " + reference + "Border;");
				} else {
					codeList.add( "\t\t" + reference + "Border "); // no comma since border is the last argument
				}
			}
			return;
		}
		String type = (String) group.get("type");
		String originalType = type;
		if (type == null) {
			type = "simple";
		} else {
			type = type.toLowerCase();
		}
		String className = (String) BORDER_TYPES.get(type);
		if (className == null) {
			className = originalType;
		}
		try {
			BorderConverter creator =  (BorderConverter) Class.forName(className).newInstance();
			creator.setColorConverter(this.colorConverter);
			creator.addBorder( codeList, group, borderName, style, styleSheet, isStandalone );
		} catch (BuildException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("Invalid CSS: unable to load background-type [" + type + "] with class [" + className + "]:" + e.getMessage() +  " (" + e.getClass().getName() + ")\nMaybe you need to adjust the CLASSPATH setting.", e );
		}
	}


	/**
	 * Creates the background-definition.
	 * 
	 * @param backgroundName the name of the background
	 * @param group the map containing the background information
	 * @param style the style
	 * @param codeList the array list into which generated code is written
	 * @param styleSheet the parent style sheet
	 * @param isStandalone true when a new public background-field should be created,
	 *        otherwise the background will be embedded in a style instantiation. 
	 */
	private void processBackground(String backgroundName, HashMap group, Style style, ArrayList codeList, StyleSheet styleSheet, boolean isStandalone) {
		//System.out.println("processing background " + backgroundName + " = " + group.toString() );
		// check if the background is just a reference to another background:
		String reference = (String) group.get("background");
		if (reference != null && group.size() == 1) {
			if ("none".equals(reference)) {
				if (isStandalone) {
					codeList.add( STANDALONE_MODIFIER + "Background " + backgroundName + "Background = null;\t// background:none was specified");
				} else {
					codeList.add( "\t\tnull,\t// background:none was specified");
				}
			} else {
				// a reference to an existing border is given:
				if (isStandalone) {
					codeList.add( STANDALONE_MODIFIER + "Background " + backgroundName + "Background = " + reference + "Background;");
				} else {
					codeList.add( "\t\t" + reference + "Background, ");
				}
			}
			return;
		}
		String type = (String) group.get("type");
		String originalType = type;
		if (type == null) {
			// this should be a simple background:
			String imageUrl = (String) group.get("image");
			if (imageUrl == null || "none".equals(imageUrl)) {
				// this a simple background:
				type = "simple";
			} else {
				// this is a image background:
				type = "image";
			}
		} else {
			type = type.toLowerCase();
			if ("image".equals( type )) {
				String imageUrl = (String) group.get("image");
				if ("none".equals(imageUrl)) {
					// this a simple background, even though the type is specified:
					type = "simple";
				}
			}
		}
		String className = (String) BACKGROUND_TYPES.get(type);
		if (className == null) {
			className = originalType;
		}
		try {
			BackgroundConverter creator =  (BackgroundConverter) Class.forName(className).newInstance();
			creator.setColorConverter(this.colorConverter);
			creator.addBackground( codeList, group, backgroundName, style, styleSheet, isStandalone );
		} catch (BuildException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("Invalid CSS: unable to load background-type [" + type + "] with class [" + className + "]:" + e.getMessage() +  " (" + e.getClass().getName() + ")\nMaybe you need to adjust the CLASSPATH setting.", e );
		}		
	}

	/**
	 * Adds a font definition.
	 * 
	 * @param group the font definition
	 * @param groupName the name of this font - usually "font" or "labelFont"
	 * @param style the style
	 * @param codeList the array list into which generated code is written
	 * @param styleSheet the parent style sheet
	 * @param isStandalone true when a new public font-field should be created,
	 *        otherwise the font will be embedded in a style instantiation. 
	 */
	private void processFont(HashMap group, String groupName, Style style, 
			ArrayList codeList, StyleSheet styleSheet, boolean isStandalone ) 
	{
		//System.out.println("processing font: "  + groupName +  " = " + group.toString() );
		processFontOrLabel( "Font", group, groupName, style, codeList, styleSheet, isStandalone);
	}

	/**
	 * Adds a font definition.
	 * 
	 * @param typeName the type name, "Label" or "Font"
	 * @param group the font definition
	 * @param groupName the name of this font - usually "font" or "label"
	 * @param style the style
	 * @param codeList the array list into which generated code is written
	 * @param styleSheet the parent style sheet
	 * @param isStandalone true when a new public font-field should be created,
	 *        otherwise the font will be embedded in a style instantiation. 
	 */
	private void processFontOrLabel(String typeName, HashMap group, String groupName, Style style, 
			ArrayList codeList, StyleSheet styleSheet, boolean isStandalone ) 
	{
		// check if this is a reference to another font:
		String reference = (String) group.get(groupName);
		if (reference != null  && group.size() == 1) {
			if (isStandalone) {
				codeList.add( STANDALONE_MODIFIER + "int " + groupName + typeName 
						+ "Color = " + reference + typeName + "Color;" );
				codeList.add( STANDALONE_MODIFIER + "Font " + groupName + typeName 
						+ " = " + reference + typeName + ";" );
			} else {
				codeList.add( "\t\t" + reference + typeName + "Color," );
				codeList.add( "\t\t" + reference + typeName + "," );
			}
			return;
		}
		// get font color:
		String fontColor = (String) group.get("color");
		if (isStandalone) {
			String newStatement = STANDALONE_MODIFIER + "int " + groupName + typeName + "Color = "; 
			if (fontColor != null) {
				newStatement +=  this.colorConverter.parseColor(fontColor) + ";";
			} else {
				newStatement +=  "0x000000; // default font color is black";
			}
			codeList.add( newStatement );
		} else {
			if (fontColor != null) {
				codeList.add( "\t\t" + this.colorConverter.parseColor(fontColor) + ",\t// " + groupName + "-color");
			} else {
				codeList.add( "\t\t0x000000,\t// " + groupName + "-color (default is black)");
			}
		}
		// get the font:
		String face = (String) group.get("face");
		String styleStr = (String) group.get("style");
		String size = (String) group.get("size");
		String newStatement;
		if (face == null && styleStr == null && size == null) {
			newStatement = "Font.getDefaultFont()";
		} else {
			// at least one font property is defined:
			FontConverter font = new FontConverter();  
			if ( face != null) {
				font.setFace(face);
			}
			if ( styleStr != null) {
				font.setStyle(styleStr);
			}
			if ( size != null) {
				font.setSize(size);
			}
			newStatement =  font.createNewStatement();
		}
		if (isStandalone) {
			newStatement = STANDALONE_MODIFIER + "Font " 
				+ groupName + typeName + " = " + newStatement + ";";
		} else {
			newStatement = "\t\t" + newStatement + ", //" + groupName;
			
		}
		
		codeList.add( newStatement  );
	}

	/**
	 * Processes the given fields - currently either "margins" or "paddings".  
	 * 
	 * @param defaultValue the default value for unset fields
	 * @param includeVerticalHorizontal true when vertical and horizontal fields should
	 * 		  also be processed (this is the case for paddings).
	 * @param fields the definition of the fields
	 * @param groupName the name of the group
	 * @param style the style
	 * @param codeList the array list into which generated code is written
	 * @param styleSheet the parent style sheet
	 * @param device the device for which the fields should be processed
	 */
	private void processFields(int defaultValue, boolean includeVerticalHorizontal, HashMap fields, String groupName, Style style, ArrayList codeList, StyleSheet styleSheet, Device device) {
		StringBuffer result = new StringBuffer();
		result.append("\t\t");
		String styleName = style.getSelector();
		int screenWidth = -1;
		String screenWidthStr = device.getCapability("ScreenWidth");
		if (screenWidthStr != null) {
			screenWidth = Integer.parseInt( screenWidthStr );
		}
		int screenHeight = -1;
		String screenHeightStr = device.getCapability("ScreenHeight");
		if (screenHeightStr != null) {
			screenHeight = Integer.parseInt( screenHeightStr );
		}
				
		String defaultValueStr = (String) fields.get(groupName);
		if (defaultValueStr != null) {
			defaultValue = parseInt(styleName, groupName, "", defaultValueStr);
		}
		String value = (String) fields.get("left");
		if (value != null) {
			result.append( parseInt( styleName, groupName, "left", value, screenWidth )).append( ',');
		} else {
			result.append( defaultValue ).append( ',');
		}
		value = (String) fields.get("right");
		if (value != null) {
			result.append( parseInt( styleName, groupName, "right", value, screenWidth )).append( ',');
		} else {
			result.append( defaultValue ).append( ',');
		}
		value = (String) fields.get("top");
		if (value != null) {
			result.append( parseInt( styleName, groupName, "top",  value, screenHeight )).append( ',');
		} else {
			result.append( defaultValue ).append( ',');
		}
		value = (String) fields.get("bottom");
		if (value != null) {
			result.append( parseInt( styleName, groupName, "bottom",  value, screenHeight )).append( ',');
		} else {
			result.append( defaultValue ).append( ',');
		}
		if (includeVerticalHorizontal) {
			value = (String) fields.get("vertical");
			if (value != null) {
				result.append( parseInt( styleName, groupName, "vertical",  value, screenWidth )).append( ',');
			} else {
				result.append( defaultValue ).append( ',');
			}
			value = (String) fields.get("horizontal");
			if (value != null) {
				result.append( parseInt( styleName, groupName, "horizontal",  value, screenHeight )).append( ',');
			} else {
				result.append( defaultValue ).append( ',');
			}
		}
		result.append("\t// ").append(groupName);
		// add to the code:
		codeList.add( result.toString() );
	}


}
