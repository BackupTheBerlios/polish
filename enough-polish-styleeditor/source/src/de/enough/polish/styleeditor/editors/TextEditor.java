/*
 * Created on Apr 23, 2007 at 9:49:59 PM.
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
package de.enough.polish.styleeditor.editors;

import java.util.Arrays;

import javax.microedition.lcdui.Font;

import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.preprocess.css.CssMapping;
import de.enough.polish.resources.ColorProvider;
import de.enough.polish.styleeditor.CssAttributeValue;
import de.enough.polish.styleeditor.EditColor;
import de.enough.polish.styleeditor.EditStyle;
import de.enough.polish.styleeditor.StylePartEditor;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.TextEffect;

/**
 * <p>Edits text settings of a style</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 23, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TextEditor extends StylePartEditor {

	private Font font;
	private CssAttribute textEffectAttribute;
	private String textEffectName;
	private CssAttributesEditor textEffectEditor;
	private TextEffect textEffect;
	private EditColor fontColorProvider;


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#writeStyle(EditStyle)
	 */
	public void writeStyle( EditStyle style ) {
		style.getStyle().font = this.font;
		if (this.fontColorProvider != null && this.fontColorProvider.getColor() != null) {
			style.getStyle().fontColor = this.fontColorProvider.getColor().getColor();
			style.getStyle().fontColorObj = this.fontColorProvider.getColor();
		}
		style.setFontFace( getFontFaceAsString() );
		style.setFontSize( getFontSizeAsString() );
		style.setFontStyle( getFontStyleAsString() );
		style.setFontColor( getFontColorAsString() );
		if (this.textEffectName == null) {
			style.removeAttribute( this.textEffectAttribute );
		} else {
			style.addAttribute( new CssAttributeValue( this.textEffectAttribute, this.textEffect, this.textEffectName ) );
		}
	}
	

	private String getFontColorAsString() {
		if (this.fontColorProvider == null) {
			return null;
		}
		return ((EditColor)this.fontColorProvider).toStringValue();
	}


	/**
	 * @return
	 */
	private String getFontSizeAsString() {
		if (this.font == null) {
			return null;
		}
		switch (this.font.getSize()) {
		case Font.SIZE_SMALL: return "small";
		case Font.SIZE_MEDIUM: return "medium";
		case Font.SIZE_LARGE: return "large";
		}
		return null;
	}
	
	private String getFontStyleAsString() {
		if (this.font == null) {
			return null;
		}
		StringBuffer buffer = new StringBuffer();
		if (isStyleBold()) {
			buffer.append("bold");
		} else {
			buffer.append("plain");
		}
		if (isStyleItalic()) {
			buffer.append(" | italic");
		}
		if (isStyleUnderlined()) {
			buffer.append(" | underlined");
		}
		return buffer.toString();
	}


	/**
	 * @return
	 */
	private String getFontFaceAsString() {
		if (this.font == null) {
			return null;
		}
		switch (this.font.getFace()) {
		case Font.FACE_SYSTEM: return "system";
		case Font.FACE_PROPORTIONAL: return "proportional";
		case Font.FACE_MONOSPACE: return "monospace";
		}
		return null;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#readStyle(de.enough.polish.styleeditor.EditStyle)
	 */
	public void readStyle(EditStyle style) {
		this.font = style.getStyle().font;
		if (this.font == null) {
			this.font = Font.getDefaultFont();
		}
		String fontColorStr  = style.getFontColor();
		if (fontColorStr != null) {
			ColorProvider fontColor = getResourcesProvider().getColor(fontColorStr);
			if (fontColor != null) {
				this.fontColorProvider = (EditColor) fontColor; // style.getStyle().getFontColor();
			} else {
				this.fontColorProvider = new EditColor( style.getStyle().fontColorObj );
			}
		}
		this.textEffect = (TextEffect) style.getStyle().getObjectProperty( this.textEffectAttribute.getId() );
		this.textEffectName = null;
		if (this.textEffect == null) {
			this.textEffectEditor = null;
			if (this.visual != null) {
				this.visual.setCssAttributes(null, null);
			}
		} else {
			CssMapping mapping = this.textEffectAttribute.getMappingByTo( this.textEffect.getClass().getName() );
			if (mapping != null) {
				setTextEffect( mapping.getFrom() );
				this.textEffectEditor.setStyle(style);
				if (this.visual != null) {
					this.visual.setCssAttributes( this.textEffectEditor.getAttributes(),this.textEffectEditor.getEditors() );
				}
			}
		}
	}
	
	public boolean isSizeSmall() {
		return this.font.getSize() == Font.SIZE_SMALL;
	}

	public boolean isSizeMedium() {
		return this.font.getSize() == Font.SIZE_MEDIUM;
	}
	
	public boolean isSizeLarge() {
		return this.font.getSize() == Font.SIZE_LARGE;
	}
	
	public boolean isFaceSystem() {
		return this.font.getFace() == Font.FACE_SYSTEM;
	}

	public boolean isFaceProportional() {
		return this.font.getFace() == Font.FACE_PROPORTIONAL;
	}
	
	public boolean isFaceMonospace() {
		return this.font.getFace() == Font.FACE_MONOSPACE;
	}
	
	public boolean isStyleBold() {
		return (this.font.getStyle() & Font.STYLE_BOLD) == Font.STYLE_BOLD;
	}
	
	public boolean isStyleItalic() {
		return (this.font.getStyle() & Font.STYLE_ITALIC) == Font.STYLE_ITALIC;
	}
	
	public boolean isStyleUnderlined() {
		return (this.font.getStyle() & Font.STYLE_UNDERLINED) == Font.STYLE_UNDERLINED;
	}
	
	public ColorProvider getColorProvider() {
		return this.fontColorProvider;
	}
	
	public void setColorProvider( ColorProvider color ) {
		this.fontColorProvider = (EditColor) color;
		update();
	}

	
	public void setSize( int size ) {
		this.font = Font.getFont( this.font.getFace(), this.font.getStyle(), size);
		update();
	}
	
	public void setFace( int face ) {
		this.font = Font.getFont( face, this.font.getStyle(), this.font.getSize() );
		update();
	}
	
	public void setStyleBold( boolean enable ) {
		setStyle( Font.STYLE_BOLD, enable );
	}

	public void setStyleItalic( boolean enable ) {
		setStyle( Font.STYLE_ITALIC, enable );
	}

	public void setStyleUnderlined( boolean enable ) {
		setStyle( Font.STYLE_UNDERLINED, enable );
	}
	
	
	private void initTextEffectAttribute() {
		if (this.textEffectAttribute == null) {
			this.textEffectAttribute = getAttribute( "text-effect" );
		}
	}
	
	public String[] getTextEffectNames() {
		initTextEffectAttribute();
		CssMapping[] mappings = this.textEffectAttribute.getMappings();
		String[] names = new String[ mappings.length ];
		for (int i = 0; i < names.length; i++) {
			CssMapping mapping = mappings[i];
			names[i] = mapping.getFrom();
		}
		Arrays.sort(names);
		return names;
	}
	
	public String getTextEffect() {
		return this.textEffectName;
	}
	
	/**
	 * @param textEffectName
	 */
	public void setTextEffect(String textEffectName) {
		// remove previous settings from style:
		if (this.textEffectEditor != null) {
			this.editStyle.removeAttributes( this.textEffectEditor.getAttributes() );
			if (this.visual != null) {
				this.visual.setCssAttributes(null, null);
			}
		}
		initTextEffectAttribute();
		this.textEffectName = textEffectName;
		if (textEffectName == null) {
			this.textEffectEditor = null;
			this.textEffect = null;
		} else {
			this.textEffectEditor = new CssAttributesEditor(this.styleEditor);
			CssMapping mapping = this.textEffectAttribute.getMapping(textEffectName);
			if (mapping != null) {
				try {
					Class effectClass = Class.forName( mapping.getToClassName() );
					this.textEffect = (TextEffect) effectClass.newInstance();
					CssAttribute[] attributes = getApplicableAttributes(effectClass);
					this.textEffectEditor.setCssAttributes(attributes);
					this.textEffectEditor.setStyle(this.editStyle);
					if (this.visual != null) {
						this.visual.setCssAttributes( attributes, this.textEffectEditor.getEditors() );
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		update();
	}
	

	/**
	 * @param style_bold
	 * @param enable
	 */
	private void setStyle(int styleModifier, boolean enable) {
		int fontStyle = this.font.getStyle();
		if (enable) {
			fontStyle |= styleModifier;
		} else {
			fontStyle &= ~styleModifier;
		}
		this.font = Font.getFont( this.font.getFace(), fontStyle, this.font.getSize() );
		update();
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#getName()
	 */
	public String createName() {
		return "text";
	}


	/**
	 * @return
	 */
	public java.awt.Color getColorForAwt() {
		if (this.fontColorProvider == null) {
			return null;
		}
		Color color = this.fontColorProvider.getColor();
		if (color == null) {
			return null;
		}
		return new java.awt.Color( color.getColor() );
	}


	
	

}
