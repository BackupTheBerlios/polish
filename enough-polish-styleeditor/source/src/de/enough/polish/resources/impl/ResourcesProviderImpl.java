/*
 * Created on May 3, 2007 at 11:39:50 AM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.resources.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.lcdui.Font;

import de.enough.polish.Environment;
import de.enough.polish.ExtensionManager;
import de.enough.polish.ant.build.ResourceSetting;
import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.preprocess.Preprocessor;
import de.enough.polish.preprocess.css.ColorConverter;
import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.preprocess.css.CssAttributesManager;
import de.enough.polish.preprocess.css.ParameterizedCssMapping;
import de.enough.polish.preprocess.css.Style;
import de.enough.polish.preprocess.css.StyleSheet;
import de.enough.polish.preprocess.css.attributes.StyleCssAttribute;
import de.enough.polish.resources.ColorProvider;
import de.enough.polish.resources.ImageProvider;
import de.enough.polish.resources.ResourceManager;
import de.enough.polish.resources.ResourcesProvider;
import de.enough.polish.resources.StyleProvider;
import de.enough.polish.styleeditor.CssAttributeValue;
import de.enough.polish.styleeditor.EditColor;
import de.enough.polish.styleeditor.EditStyle;
import de.enough.polish.ui.Background;
import de.enough.polish.ui.Border;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Item;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.ReflectionUtil;
import de.enough.polish.util.ResourceUtil;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        May 3, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ResourcesProviderImpl implements ResourcesProvider {
	
	private final File polishHome;
	private final Environment environment;
	private ResourceManager resourceManager;
	private StyleSheet styleSheet;
	private final List<String> styleNamesList;
	private final Map< String, EditStyle> editStylesByName;
	private final CssAttributesManager attributesManager;
	private final ColorConverter colorConverter;
	private final Map< String, EditColor> editColorsByName;

	public ResourcesProviderImpl( File polishHome, Environment environment, CssAttributesManager attributesManager ) 
	throws IOException, InvalidComponentException 
	{
		if (environment.getBaseDir() == null) {
			throw new IllegalArgumentException("Environment has no attached baseDir");
		}
		if (environment.getDevice() == null) {
			throw new IllegalArgumentException("Environment has no attached device");
		}
		this.polishHome = polishHome;
		this.environment = environment;
		this.attributesManager = attributesManager;
		
		this.colorConverter = new ColorConverter();
		environment.set( ColorConverter.ENVIRONMENT_KEY, this.colorConverter );
		this.editColorsByName = new HashMap<String, EditColor>();
		
		this.styleNamesList = new ArrayList<String>();
		this.editStylesByName = new HashMap<String, EditStyle>();
		ResourceUtil resourceUtil = new ResourceUtil( getClass().getClassLoader() );
		ExtensionManager extensionManager = ExtensionManager.getInstance(polishHome, resourceUtil);
		ResourceSetting resourceSetting = new ResourceSetting( environment.getBaseDir() );
		this.resourceManager = new ResourceManager(resourceSetting, extensionManager, environment );
		// import existing polish.css file(s):
		loadCssStyleSheet();
	}
	
	private void loadCssStyleSheet() throws IOException {
		Preprocessor preprocessor = new Preprocessor(null, this.environment, null, false, false, false, null);
		this.styleSheet = this.resourceManager.loadStyleSheet( this.environment.getDevice(), this.environment.getLocale(), preprocessor, this.environment);
		loadColors();
		Style[] cssStyles = this.styleSheet.getAllStyles();
		for (int i = 0; i < cssStyles.length; i++) {
			Style style = cssStyles[i];
			this.styleNamesList.add( style.getSelector() );
		}
		StyleProvider focused = getStyle("focused");
		if (focused != null) {
			de.enough.polish.ui.StyleSheet.focusedStyle = focused.getStyle();
		}
	}

	/**
	 * 
	 */
	private void loadColors() {
		//System.out.println("loading " + this.styleSheet.getColors().size() + " colors...");
		Map colorDefinitions = this.styleSheet.getColors();
		this.colorConverter.setTemporaryColors( colorDefinitions );
		Object[] keys = colorDefinitions.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			String key = (String) keys[i];
			String hexValue = this.colorConverter.parseColor(key);
			int colorValue = Long.decode(hexValue).intValue();
			Color color = new Color( colorValue );
			EditColor editColor = new EditColor( key, color );
			this.editColorsByName.put( key, editColor );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.resources.ResourcesProvider#addStyle(java.lang.String, de.enough.polish.resources.StyleProvider)
	 */
	public void addStyle(String name, StyleProvider style) {
		this.editStylesByName.put( name, (EditStyle)style );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.resources.ResourcesProvider#getStyle(java.lang.String)
	 */
	public StyleProvider getStyle(String name) {
		EditStyle editStyle = this.editStylesByName.get(name);
		if (editStyle == null) {
			Style cssStyle = this.styleSheet.getStyle(name);
			if (cssStyle == null) {
				return null;
			}
			//de.enough.polish.ui.Style runtimeStyle = parseStyle( cssStyle );
			editStyle = parseStyle( name, cssStyle );
			this.editStylesByName.put( name, editStyle );
		}
		return editStyle;
	}

	/**
	 * @param cssStyle
	 * @return
	 */
	private EditStyle parseStyle(String name, Style cssStyle) {
		//TODO generate EditStyle here, so that background, border, font are set correctly (for later export):
		de.enough.polish.ui.Style style = new de.enough.polish.ui.Style();
		EditStyle editStyle = new EditStyle( name, style, null );
		style.name = cssStyle.getSelector();
		Map group = cssStyle.getGroup("margin");
		if (group != null) {
			int defaultValue = parseInt( group, "margin", 0 );
			style.marginLeft = parseInt( group, "left", defaultValue );
			style.marginRight = parseInt( group, "right", defaultValue );
			style.marginTop = parseInt( group, "top", defaultValue );
			style.marginBottom = parseInt( group, "bottom", defaultValue );
		}
		group = cssStyle.getGroup("padding");
		if (group != null) {
			int defaultValue = parseInt( group, "padding", 0 );
			style.paddingLeft = parseInt( group, "left", defaultValue );
			style.paddingRight = parseInt( group, "right", defaultValue );
			style.paddingTop = parseInt( group, "top", defaultValue );
			style.paddingBottom = parseInt( group, "bottom", defaultValue );
			style.paddingHorizontal = parseInt( group, "horizontal", defaultValue );
			style.paddingVertical = parseInt( group, "vertical", defaultValue );
		}
		group = cssStyle.getGroup("layout");
		if (group != null) {
			String valueStr = (String) group.get("layout");
			CssAttribute attribute = this.attributesManager.getAttribute("layout");
			style.layout = ((Integer) attribute.parseAndInstantiateValue(valueStr, this.environment)).intValue();
		}
		group = cssStyle.getGroup("font");
		if (group != null) {
			Font font = Font.getDefaultFont();
			int fface = font.getFace();
			int fstyle = font.getStyle();
			int fsize = font.getSize();
			String valueStr = (String) group.get("face");
			if (valueStr != null) {
				editStyle.setFontFace(valueStr);
				CssAttribute attribute = this.attributesManager.getAttribute("font-face");
				fface = ((Integer) attribute.parseAndInstantiateValue(valueStr, this.environment)).intValue();
			}
			valueStr = (String) group.get("style");
			if (valueStr != null) {
				editStyle.setFontStyle(valueStr);
				CssAttribute attribute = this.attributesManager.getAttribute("font-style");
				fstyle = ((Integer) attribute.parseAndInstantiateValue(valueStr, this.environment)).intValue();
			}
			valueStr = (String) group.get("size");
			if (valueStr != null) {
				editStyle.setFontSize(valueStr);
				CssAttribute attribute = this.attributesManager.getAttribute("font-size");
				fsize = ( (Integer) attribute.parseAndInstantiateValue(valueStr, this.environment)).intValue();
			}
			style.font = Font.getFont(fface, fstyle, fsize );
			
			valueStr = (String) group.get("color");
			if (valueStr != null) {
				editStyle.setFontColor(valueStr);
				CssAttribute attribute = this.attributesManager.getAttribute("font-color");
				style.fontColorObj = (Color) attribute.parseAndInstantiateValue(valueStr, this.environment);
				style.fontColor = style.fontColorObj.getColor();
			}
		}
		group = cssStyle.getGroup("background");
		if (group != null) {
			String mappingName = (String) group.get("type");
			if (mappingName == null) {
				if (group.get("image") != null) {
					mappingName = "image";
				} else {
					mappingName = "simple";
				}
			}
			CssAttribute attribute = this.attributesManager.getAttribute("background");
			try {
				ParameterizedParsingResult result = parseParameterizedAttribute( attribute, mappingName, group ); 
				style.background = (Background) result.value;
				editStyle.setBackground(result.mainValue, result.attributeValues );
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Warning: unable to instantiate background " + mappingName + ": " + e);
			}
		}
		group = cssStyle.getGroup("border");
		if (group != null) {
			String mappingName = (String) group.get("type");
			if (mappingName == null) {
				mappingName = "simple";
			}
			CssAttribute attribute = this.attributesManager.getAttribute("border");
			try {
				ParameterizedParsingResult result = parseParameterizedAttribute( attribute, mappingName, group ); 
				style.border = (Border) result.value;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Warning: unable to instantiate border " + mappingName + ": " + e);
			}
		}
		String[] groupNames = cssStyle.getGroupNames();
		for (int i = 0; i < groupNames.length; i++) {
			String groupName = groupNames[i];
			group = cssStyle.getGroup(groupName);
			Object[] groupKeys = group.keySet().toArray();
			for (int j = 0; j < groupKeys.length; j++) {
				String key = (String) groupKeys[j];
				String attributeName = groupName + "-" + key;
				CssAttribute attribute = this.attributesManager.getAttribute( attributeName );
				if (attribute == null) {
					//System.out.println("Warning: encountered unknown attribute " + attributeName );
					continue;
				}
				if (attribute.isBaseAttribute()) {
					//System.out.println("Ignoring base attribute " + attributeName);
					continue;
				}
				String value = (String) group.get(key);
				if (attribute instanceof StyleCssAttribute) {
					StyleProvider referencedStyle = getStyle( value );
					if (referencedStyle == null) {
						System.out.println("Warning: unable to resolve style reference \"" + value + "\".");
					} else {
						style.addAttribute( attribute.getId(), referencedStyle.getStyle() );
					}
				} else {
					try {
						style.addAttribute( attribute.getId(), attribute.parseAndInstantiateValue( value, this.environment ));
					} catch (IllegalArgumentException e) {
						System.out.println("Warning: unable to instantiate " + attributeName + ": " + value + "  in style " + style.name + ": " + e.toString() );
					}
				}
			}
		}
		return editStyle;
	}
	
	/**
	 * @param editStyle 
	 * @param attribute
	 * @param mapingName
	 * @param group
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IllegalArgumentException 
	 */
	private ParameterizedParsingResult parseParameterizedAttribute(CssAttribute attribute, String mappingName, Map group) 
	throws ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException 
	{
		ParameterizedCssMapping mapping = (ParameterizedCssMapping) attribute.getMapping(mappingName);
		if (mapping == null) {
			System.out.println("Warning: unable to find mapping for " + mappingName );
			return null;
		}
		CssAttributeValue mainValue = new CssAttributeValue( attribute, mappingName, mappingName );
		Class mappingClass = Class.forName( mapping.getTo() );
		CssAttribute[] parameterAttributes = mapping.getParameters();
		Object[] parameterValues = new Object[ parameterAttributes.length ];
		CssAttributeValue[] attributeValues = new CssAttributeValue[ parameterAttributes.length ];
		for (int i = 0; i < parameterAttributes.length; i++) {
			CssAttribute parameterAttribute = parameterAttributes[i];
			String cssValue = (String) group.get( parameterAttribute.getName() );
			if (cssValue == null) {
				System.out.println("Unable to find CSS code for parameter " + parameterAttribute.getName() );
				cssValue = parameterAttribute.getDefaultValue();
			}
			Object parameterValue = parameterAttribute.parseAndInstantiateValue(cssValue, this.environment);
			parameterValues[i] = parameterValue;
			attributeValues[i] = new CssAttributeValue(parameterAttribute, parameterValue, cssValue );
		}
		
		return new ParameterizedParsingResult( ReflectionUtil.newInstance(mappingClass, parameterValues),
				mainValue, attributeValues );
	}

	private int parseInt( Map map, String name, int defaultValue ) {
		String valueStr = (String) map.get(name);
		if (valueStr != null) {
			return Integer.parseInt(valueStr);
		}
		return defaultValue;
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.resources.ResourcesProvider#getStyles()
	 */
	public StyleProvider[] getStyles() {
		StyleProvider[] styles = new StyleProvider[ this.styleNamesList.size() ];
		Object[] styleNames = this.styleNamesList.toArray();
		Arrays.sort( styleNames );
		for (int i = 0; i < styleNames.length; i++) {
			String name = (String) styleNames[i];
			styles[i] = getStyle(name);
		}
		return styles;
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.resources.ResourcesProvider#getImage(java.lang.String)
	 */
	public ImageProvider getImage(String name) {
		// TODO robertvirkus implement getImage
		return null;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.resources.ResourcesProvider#getImages()
	 */
	public ImageProvider[] getImages() {
		// TODO robertvirkus implement getImages
		return null;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.resources.ResourcesProvider#addImage(java.lang.String, de.enough.polish.resources.ImageProvider)
	 */
	public void addImage(String name, ImageProvider image) {
		// TODO robertvirkus implement addImage

	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.resources.ResourcesProvider#saveResources()
	 */	
	public void saveResources() throws IOException {
		File output = new File( this.environment.getBaseDir(), "polish.css");
		StringBuffer buffer = new StringBuffer();
		saveColors( buffer );
		saveStyles( buffer );
		FileUtil.writeTextFile( output, new String[]{ buffer.toString() } );
		System.out.println("wrote CSS to " + output.getAbsolutePath() );
	}

	/**
	 * @param buffer
	 */
	private void saveColors(StringBuffer buffer) {
		buffer.append("colors {\n");
		ColorProvider[] colors = getColors();
		for (int i = 0; i < colors.length; i++) {
			ColorProvider color = colors[i];
			buffer.append("\t");
			color.generateCssSourceCode(buffer);
			buffer.append(";\n");
		}
		buffer.append("}\n");
		
	}
	
	/**
	 * @param buffer
	 */
	private void saveStyles(StringBuffer buffer) {
		StyleProvider[] styleProviders = getStyles();
		for (int i = 0; i < styleProviders.length; i++) {
			StyleProvider style = styleProviders[i];
			style.generateCssSourceCode(buffer);
		}
	}


	static class ParameterizedParsingResult {
		public Object value;
		public CssAttributeValue mainValue; 
		public CssAttributeValue[] attributeValues;
		/**
		 * @param value
		 * @param attribute
		 * @param attributeMapping
		 * @param attributeValues
		 */
		public ParameterizedParsingResult(Object value, CssAttributeValue mainValue, CssAttributeValue[] attributeValues) {
			super();
			this.value = value;
			this.mainValue = mainValue;
			mainValue.setValue(value);
			this.attributeValues = attributeValues;
		}
		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.resources.ResourcesProvider#addColor(java.lang.String, de.enough.polish.resources.ColorProvider)
	 */
	public void addColor(String name, ColorProvider color) {
		this.editColorsByName.put(name, (EditColor) color );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.resources.ResourcesProvider#getColor()
	 */
	public ColorProvider[] getColors() {
		return this.editColorsByName.values().toArray( new ColorProvider[ this.editColorsByName.size() ]);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.resources.ResourcesProvider#getColor(java.lang.String)
	 */
	public ColorProvider getColor(String name) {
		EditColor color = this.editColorsByName.get(name);
		if (color == null) {
			String valueStr = this.colorConverter.parseColor(name);
			int value;
			if ("Item.TRANSPARENT".equals(valueStr)) {
				value = Item.TRANSPARENT;
			} else {
				value = Long.decode(valueStr).intValue();
			}
			color = new EditColor(name, new Color( value ) );
		}
		return color;
	}

}
