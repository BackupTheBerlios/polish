/*
 * Created on 09-Nov-2004 at 23:01:09.
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
package de.enough.polish.font;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import de.enough.polish.util.TextUtil;


/**
 * <p>Shows and manipulates a true type font.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        09-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TrueTypeFontViewer 
extends Container
implements ActionListener
{

	private static final String[] FONT_SIZES = new String[]{ "8", "9", "10", "10.5", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "22", "24", "26", "28", "30", "32", "34", "36" };
	private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
	private static final String PUNCTUATION = ":,.!";
	private static final String NUMBERS = "0123456789";
	private static final String SPACE = " "; 
	private static final String STANDARD_TEXT =  LOWERCASE + SPACE + PUNCTUATION + NUMBERS;
	private final Font basicFont;
	private Font derivedFont;
	private float currentSize = 12F;
	private final JTextField characterMap;
	private final JComboBox sizeChooser;
	private final JTextField sizeTextField;
	private final JButton colorButton;
	private final JLabel imageLabel;
	private Color currentColor = Color.BLACK;
	private final JCheckBox optionUppercase;
	private final JCheckBox optionLowercase;
	private final JCheckBox optionPunctuation;
	private final JCheckBox optionNumbers;
	private final JCheckBox optionSpace;

	/**
	 * @param fontFile
	 * @throws IOException
	 */
	public TrueTypeFontViewer( File fontFile ) throws IOException {
		super();
		
		InputStream in = new FileInputStream( fontFile );
		try {
			this.basicFont = Font.createFont( Font.TRUETYPE_FONT, in);
			this.derivedFont = this.basicFont.deriveFont( this.currentSize );
			this.characterMap = new JTextField( STANDARD_TEXT );
			this.characterMap.addActionListener( this );
			this.sizeChooser = new JComboBox( FONT_SIZES );
			this.sizeChooser.setSelectedIndex( 5 );
			this.sizeChooser.addActionListener( this );
			this.sizeTextField = new JTextField( "12" );
			this.sizeTextField.addActionListener( this );
			this.colorButton = new JButton("...");
			this.colorButton.setBackground( Color.BLACK );
			this.colorButton.addActionListener( this );
			this.imageLabel = new JLabel("");
			this.optionUppercase = new JCheckBox("Uppercase");
			this.optionUppercase.addActionListener( this );
			this.optionLowercase = new JCheckBox("Lowercase");
			this.optionLowercase.setSelected( true );
			this.optionLowercase.addActionListener( this );
			this.optionPunctuation = new JCheckBox("Punctuation");
			this.optionPunctuation.setSelected( true );
			this.optionPunctuation.addActionListener( this );
			this.optionNumbers = new JCheckBox("Numbers");
			this.optionNumbers.setSelected( true );
			this.optionNumbers.addActionListener( this );
			this.optionSpace = new JCheckBox("Space");
			this.optionSpace.setSelected( true );
			this.optionSpace.addActionListener( this );
			
			// adding items:
			setLayout( new BorderLayout() );
			JPanel sizePanel = new JPanel( new GridLayout( 1, 4 ));
			sizePanel.add( this.colorButton );
			sizePanel.add( new JLabel("  Size:"));
			sizePanel.add( this.sizeChooser );
			sizePanel.add( this.sizeTextField ); 
			sizePanel.setBorder( new EtchedBorder( EtchedBorder.LOWERED ) );
			add( sizePanel, BorderLayout.NORTH );
			add( this.imageLabel, BorderLayout.CENTER );
			JPanel inputPanel = new JPanel( new GridLayout( 6, 1));
			inputPanel.add( this.optionUppercase );
			inputPanel.add( this.optionLowercase );
			inputPanel.add( this.optionPunctuation );
			inputPanel.add( this.optionNumbers );
			inputPanel.add( this.optionSpace );
			inputPanel.add( this.characterMap );
			add( inputPanel , BorderLayout.SOUTH );
			updateImage();
		} catch (FontFormatException e) {
			e.printStackTrace();
			throw new IOException( "Unable to init true type font: " + e.toString() );
		}
	}
	
	
	public Font getFont() {
		return this.derivedFont;
	}


	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == this.sizeChooser) {
			int selectedIndex = this.sizeChooser.getSelectedIndex();
			String fontSizeStr = FONT_SIZES[ selectedIndex ];
			float size = Float.parseFloat( fontSizeStr );
			this.derivedFont = this.basicFont.deriveFont(size);
			this.sizeTextField.setText( fontSizeStr );
			updateImage();
		} else if (source == this.sizeTextField) {
			float size = Float.parseFloat( this.sizeTextField.getText() );
			this.derivedFont = this.basicFont.deriveFont(size);
			updateImage();
		} else if (source == this.characterMap) {
			updateImage();
		} else if (source == this.colorButton) {
			this.currentColor = JColorChooser.showDialog(this, "Font-Color", this.currentColor);
			this.colorButton.setBackground( this.currentColor );
			updateImage();
		} else if (source == this.optionUppercase ) {
			String text = this.characterMap.getText();
			if (!this.optionUppercase.isSelected()) {
				text = TextUtil.replace(text, UPPERCASE, "" );
			} else {
				if (text.indexOf(UPPERCASE) == -1) {
					text += UPPERCASE;
				}
			}
			this.characterMap.setText( text );
			updateImage();
		} else if (source == this.optionLowercase ) {
			String text = this.characterMap.getText();
			if (!this.optionLowercase.isSelected()) {
				text = TextUtil.replace(text, LOWERCASE, "" );
			} else {
				if (text.indexOf(LOWERCASE) == -1) {
					text += LOWERCASE;
				}
			}
			this.characterMap.setText( text );
			updateImage();
		} else if (source == this.optionPunctuation ) {
			String text = this.characterMap.getText();
			if (!this.optionPunctuation.isSelected()) {
				text = TextUtil.replace(text, PUNCTUATION, "" );
			} else {
				if (text.indexOf(PUNCTUATION) == -1) {
					text += PUNCTUATION;
				}
			}
			this.characterMap.setText( text );
			updateImage();
		} else if (source == this.optionNumbers ) {
			String text = this.characterMap.getText();
			if (!this.optionNumbers.isSelected()) {
				text = TextUtil.replace(text, NUMBERS, "" );
			} else {
				if (text.indexOf(NUMBERS) == -1) {
					text += NUMBERS;
				}
			}
			this.characterMap.setText( text );
			updateImage();
		} else if (source == this.optionSpace ) {
			String text = this.characterMap.getText();
			if (!this.optionSpace.isSelected()) {
				text = TextUtil.replace(text, SPACE, "" );
			} else {
				if (text.indexOf(SPACE) == -1) {
					text += SPACE;
				}
			}
			this.characterMap.setText( text );
			updateImage();
		}
	}


	/**
	 * Saving this font as a bitmap font.
	 * 
	 * @param file the target file
	 * @throws IOException
	 */
	public void saveBitMapFont(File file) 
	throws IOException 
	{
		BufferedImage image = createImage();
		FileOutputStream out = new FileOutputStream( file );
		String text = this.characterMap.getText();
		DataOutputStream dataOut = new DataOutputStream( out );
		// write whether there are mixed case characters included:
		boolean hasMixedCase = hasMixedCase(text);
		dataOut.writeBoolean( hasMixedCase );
		// write the character-map:
		String charMap = text;
		if (!hasMixedCase) {
			charMap = text.toLowerCase();
		}
		dataOut.writeUTF(charMap);
		// write the widths of each character:
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		                    RenderingHints.VALUE_ANTIALIAS_ON);
		FontRenderContext fc = g.getFontRenderContext();
		for (int i = 0; i < text.length(); i++) {
			String substring = text.substring( i, i + 1);
			Rectangle2D bounds = this.derivedFont.getStringBounds(substring, fc);
			dataOut.writeByte( (int) bounds.getWidth() );
		}
		// write the image itself:
		ImageIO.write( image, "png", out );
		out.close();
		
		this.imageLabel.setIcon( new ImageIcon( image ) );
	}
	
	public void savePngFile( File file ) throws IOException {
		BufferedImage image = createImage();
		FileOutputStream out = new FileOutputStream( file );
		ImageIO.write( image, "png", out );
		out.close();
		this.imageLabel.setIcon( new ImageIcon( image ) );
	}
	
	public BufferedImage createImage() {
		String text = this.characterMap.getText();
		if (text.length() == 0) {
			return null;
		}
		// use dummy buffer for get a render context:
		BufferedImage image =
		    new BufferedImage(1,1,BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		                    RenderingHints.VALUE_ANTIALIAS_ON);
		FontRenderContext fc = g.getFontRenderContext();
		Rectangle2D bounds = this.derivedFont.getStringBounds(text,fc);
		double height = bounds.getHeight();
		double width = bounds.getWidth();
		// now create real image:
		//DirectColorModel colorModel = new DirectColorModel(8+8+8+1, 0x00ff0000, 0x0000ff00, 0x000000ff, 0x01000000);
		image = new BufferedImage( (int) width, (int) height, BufferedImage.TYPE_4BYTE_ABGR);
		g = image.createGraphics();
		Color transparent = new Color( 1, 1, 1, 0 );
		g.setBackground( transparent );
		g.clearRect(0, 0, (int) width, (int) height );
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont( this.derivedFont );
		g.setColor( this.currentColor );
		g.drawString(text,0,(int)-bounds.getY());
		return image;
	}
	
	private void updateImage() {
		BufferedImage image = createImage();
		if (image == null) {
			this.imageLabel.setIcon( null );
		} else {
			this.imageLabel.setIcon( new ImageIcon( image ) );
		}
	}
	
	private boolean hasMixedCase( String map ) {
		boolean mixedCase = true;
		char[] characters = map.toCharArray();
		for (int i = 0; i < characters.length; i++) {
			char c = characters[i];
			if (Character.isLetter(c)) {
				char differentCase;
				if (Character.isUpperCase(c)) {
					differentCase = Character.toLowerCase(c);
				} else {
					differentCase = Character.toUpperCase(c);
				}
				boolean differentCaseFound = false;
				for (int j = 0; j < characters.length; j++) {
					char d = characters[j];
					if (d == differentCase) {
						differentCaseFound = true;
					}
				}
				if (!differentCaseFound) {
					return false;
				}
			}
		}
		return mixedCase;
	}
}
