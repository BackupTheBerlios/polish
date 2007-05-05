/*
 * Created on May 4, 2007 at 1:37:18 PM.
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
package de.enough.polish.styleeditor.swing.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.enough.polish.resources.ColorProvider;
import de.enough.polish.resources.ResourcesProvider;
import de.enough.polish.styleeditor.EditColor;
import de.enough.polish.styleeditor.swing.editors.attributes.SwingColorCssAttributeEditor;
import de.enough.polish.util.SwingUtil;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        May 4, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ColorChooserComponent 
extends JPanel
implements ChangeListener
{
	
	private final JColorChooser colorChooser;
	private final ResourcesProvider resourcesProvider;
	private final ColorProvider originalColorProvider;
	private ColorProvider selectedColorProvider;
	private int selectedOpacity; /* range between 0 .. 255 */
	private de.enough.polish.ui.Color originalColor;
	private ColorProvider originalColorReference;
	private JSlider opacitySlider;
	private JLabel opacityLabel;
	private List<ColorChooserListener> colorChooserListeners;
	private final boolean isTranslucent; 

	public ColorChooserComponent( ColorProvider colorProvider, ResourcesProvider resourcesProvider ) {
		this( false, colorProvider, resourcesProvider );
	}
	public ColorChooserComponent( boolean isTranslucent, ColorProvider colorProvider, ResourcesProvider resourcesProvider ) {
		super( new BorderLayout() );
		this.isTranslucent = isTranslucent;
		this.resourcesProvider = resourcesProvider;
		if (colorProvider == null) {
			colorProvider = new EditColor( new de.enough.polish.ui.Color( 0xffffffff ) );
		}
		this.originalColorProvider = colorProvider;
		this.originalColor = colorProvider.getColor();
		this.originalColorReference = colorProvider.getColorReference();
		this.selectedColorProvider = colorProvider;
		
		this.colorChooser = new JColorChooser( new Color( this.originalColor.getColor() ) );
		this.colorChooser.setDragEnabled(true);
		this.colorChooser.getSelectionModel().addChangeListener( this );
		this.colorChooser.setPreviewPanel( new JPanel() );
		if (resourcesProvider != null) {
			this.colorChooser.addChooserPanel( new NamedColorChooserPanel() );
		}
		add( this.colorChooser, BorderLayout.CENTER );
		if (isTranslucent) {
			int opacity = (this.originalColor.getColor() >>> 24); 
			this.selectedOpacity = opacity;
			opacity = (opacity * 100) / 255;
			this.opacitySlider = new JSlider(JSlider.VERTICAL, 0, 100, opacity );
			this.opacitySlider.addChangeListener( this );
			this.opacitySlider.setMajorTickSpacing(10);
			this.opacitySlider.setMinorTickSpacing(5);
			this.opacitySlider.setPaintTicks(true);
			this.opacitySlider.setLabelTable( this.opacitySlider.createStandardLabels(20));
			this.opacitySlider.setPaintLabels(true);
			this.opacityLabel = new JLabel( Integer.toString(opacity) + "%" );
			JPanel opacityPanel = new JPanel( new BorderLayout() );
			opacityPanel.add( this.opacitySlider, BorderLayout.CENTER );
			opacityPanel.add( this.opacityLabel, BorderLayout.SOUTH );
			SwingUtil.setTitle(opacityPanel, "opacity");
			add( opacityPanel, BorderLayout.WEST );
		}
	}
	
	public ColorProvider getColorProvider() {
		if (this.selectedColorProvider != null) {
			return this.selectedColorProvider;
		}
		int argb = this.colorChooser.getColor().getRGB();
		return new EditColor(null, new de.enough.polish.ui.Color( argb ));
	}
	public ColorProvider getOriginalColorProvider() {
		if (this.originalColorReference != null) {
			this.originalColorProvider.setColorReference( this.originalColorReference );
		} else {
			this.originalColorProvider.setColor(this.originalColor);
		}
		return this.originalColorProvider;
	}
	

	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent event) {
		Object source = event.getSource();
		if (source == this.opacitySlider) {
			this.selectedOpacity = (this.opacitySlider.getValue() * 255) / 100;
			de.enough.polish.ui.Color color = getColor();
			if (this.selectedColorProvider.getColorReference() != null ) {
				this.selectedColorProvider = new EditColor(getColor());
			} else {
				this.selectedColorProvider.setColor( getColor() );
			}
			this.colorChooser.setColor( color.getColor() );
			this.opacityLabel.setText( Integer.toString( this.opacitySlider.getValue() ) + "%");
		} else if (source == this.colorChooser.getSelectionModel()) {
			this.selectedColorProvider.setColor( getColor() );
		}
		if (this.colorChooserListeners != null) {
			for (ColorChooserListener listener : this.colorChooserListeners) {
				listener.notifyColorUpdated(this.selectedColorProvider );
			}
		}
	}
	
	public de.enough.polish.ui.Color getColor() {
		int argb = this.colorChooser.getColor().getRGB();
		if (this.isTranslucent) {
			int opacity = this.selectedOpacity << 24; 
			System.out.println("opacity=" + Integer.toHexString(opacity));
			argb = (argb & 0x00ffffff) | opacity;   
		}
		System.out.println("getColor: " + Integer.toHexString(argb));
		return new de.enough.polish.ui.Color( argb );
	}
	
	
	
	
	public static ColorProvider showDialog( String dialogTitle, ColorProvider currentColor, ResourcesProvider resourcesProvider,  boolean isTranslucentSupported ) {
		return showDialog(dialogTitle, currentColor, resourcesProvider, null, isTranslucentSupported);
	}
	
	public static ColorProvider showDialog( String dialogTitle, ColorProvider currentColor, ResourcesProvider resourcesProvider, ColorChooserListener listener, boolean isTranslucentSupported ) {
		ColorChooserComponent colorChooser = new ColorChooserComponent(isTranslucentSupported, currentColor, resourcesProvider );
		if (listener != null) {
			colorChooser.addColorChooserListener( listener );
		}
		ColorChooserDialog dialog = new ColorChooserDialog( dialogTitle, colorChooser );
		dialog.setVisible(true);
		ColorProvider result = dialog.getSelectedColor();
		return result; 
	}
	
	
	/**
	 * @param listener
	 */
	public void addColorChooserListener(ColorChooserListener listener) {
		if (this.colorChooserListeners == null) {
			this.colorChooserListeners = new ArrayList<ColorChooserListener>();
		}
		this.colorChooserListeners.add(listener);
	}


	class NamedColorChooserPanel 
	extends AbstractColorChooserPanel
	implements ActionListener
	{
		private final Map<JComponent, ColorProvider> colorProvidersByComponent;
		
				
		public NamedColorChooserPanel() {
			this.colorProvidersByComponent = new HashMap<JComponent, ColorProvider>();
		}

		/* (non-Javadoc)
		 * @see javax.swing.colorchooser.AbstractColorChooserPanel#buildChooser()
		 */
		protected void buildChooser() {
			this.colorProvidersByComponent.clear();
			super.removeAll();
			ColorProvider[] colors = ColorChooserComponent.this.resourcesProvider.getColors();
			//JPanel panel = new JPanel( new GridLayout( colors.length, 2, 5, 4 ));
			setLayout( new GridLayout( colors.length, 2, 5, 4 ));
			for (int i = 0; i < colors.length; i++) {
				ColorProvider colorProvider = colors[i];
				JButton button = new JButton("...");
				//System.out.println("color: " + colorProvider.getName() + "=" + Integer.toHexString(colorProvider.getColor().getColor() ));
				button.setBackground( new Color( colorProvider.getColor().getColor() ));
				button.addActionListener(this);
				add( button );
				this.colorProvidersByComponent.put(button, colorProvider);
				JLabel label = new JLabel( colorProvider.getName() );
				add( label );
			}
		}

		/* (non-Javadoc)
		 * @see javax.swing.colorchooser.AbstractColorChooserPanel#getDisplayName()
		 */
		public String getDisplayName() {
			return "defined colors";
		}

		/* (non-Javadoc)
		 * @see javax.swing.colorchooser.AbstractColorChooserPanel#getLargeDisplayIcon()
		 */
		public Icon getLargeDisplayIcon() {
			return null;
		}

		/* (non-Javadoc)
		 * @see javax.swing.colorchooser.AbstractColorChooserPanel#getSmallDisplayIcon()
		 */
		public Icon getSmallDisplayIcon() {
			return null;
		}

		/* (non-Javadoc)
		 * @see javax.swing.colorchooser.AbstractColorChooserPanel#updateChooser()
		 */
		public void updateChooser() {
			// nothing to update...
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent event) {
			if (event.getSource() instanceof JComponent) {
				JComponent component = (JComponent) event.getSource();
				Color color = component.getBackground();
				getColorSelectionModel().setSelectedColor(color);
				ColorProvider reference = this.colorProvidersByComponent.get(component);
				ColorChooserComponent.this.selectedColorProvider = new EditColor(reference);
			}
		}
		
	}
	
	static class ColorChooserDialog 
	extends JDialog
	implements ActionListener
	{
		private final JButton okButton;
		private final JButton cancelButton;
		private ColorProvider selectedColor;
		private final ColorChooserComponent colorChooser;
		
		public ColorChooserDialog( String title, ColorChooserComponent colorChooser ) {
			super( (Frame)null, title, true );
			this.colorChooser = colorChooser;
			this.okButton = new JButton("Ok");
			this.okButton.setMnemonic('o');
			this.okButton.addActionListener(this);
			this.cancelButton = new JButton("Cancel");
			this.okButton.setMnemonic('c');
			this.cancelButton.addActionListener(this);
			
			Container pane = getContentPane();
			pane.setLayout( new BorderLayout() );
			//pane.add( new JLabel( title ), BorderLayout.NORTH );
			pane.add( colorChooser, BorderLayout.CENTER );
			JPanel okCancelPanel = new JPanel( new GridLayout(1, 2, 5, 4 ));
			okCancelPanel.add( this.okButton );
			okCancelPanel.add( this.cancelButton );
			pane.add( okCancelPanel, BorderLayout.SOUTH );
			pack();
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent event) {
			if (event.getSource() == this.okButton) {
				this.selectedColor = this.colorChooser.getColorProvider();
			} else {
				this.selectedColor = this.colorChooser.getOriginalColorProvider();
			}
			setVisible(false);			
		}
		
		public ColorProvider getSelectedColor() {
			return this.selectedColor;
		}
	}

}
