/*
 * Created on Apr 23, 2007 at 10:43:09 PM.
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
package de.enough.polish.styleeditor.swing.editors;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.enough.polish.styleeditor.StylePartEditor;
import de.enough.polish.styleeditor.editors.MarginPaddingEditor;
import de.enough.polish.styleeditor.swing.SwingStylePartEditor;
import de.enough.polish.swing.IntegerTextField;
import de.enough.polish.util.SwingUtil;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 23, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SwingMarginPaddingEditor 
extends SwingStylePartEditor
implements ChangeListener
{
	
	private final IntegerTextField marginLeftField;
	private final IntegerTextField marginRightField;
	private final IntegerTextField marginTopField;
	private final IntegerTextField marginBottomField;
	
	private final IntegerTextField paddingLeftField;
	private final IntegerTextField paddingRightField;
	private final IntegerTextField paddingTopField;
	private final IntegerTextField paddingBottomField;
	private final IntegerTextField paddingHorizontalField;
	private final IntegerTextField paddingVerticalField;

	public SwingMarginPaddingEditor() {
		this.paddingTopField = new IntegerTextField( this, 3 );
		this.paddingTopField.setActionListener( this );
		this.paddingBottomField = new IntegerTextField( this, 3 );
		this.paddingBottomField.setActionListener( this );
		this.paddingLeftField = new IntegerTextField( this, 3 );
		this.paddingLeftField.setActionListener( this );
		this.paddingRightField = new IntegerTextField( this, 3 );
		this.paddingRightField.setActionListener( this );
		this.paddingHorizontalField = new IntegerTextField( this, 3 );
		this.paddingHorizontalField.setActionListener( this );
		this.paddingVerticalField = new IntegerTextField( this, 3 );
		this.paddingVerticalField.setActionListener( this );
		
		this.marginTopField = new IntegerTextField( this, 3 );
		this.marginTopField.setActionListener( this );
		this.marginBottomField = new IntegerTextField( this, 3 );
		this.marginBottomField.setActionListener( this );
		this.marginLeftField = new IntegerTextField( this, 3 );
		this.marginLeftField.setActionListener( this );
		this.marginRightField = new IntegerTextField( this, 3 );
		this.marginRightField.setActionListener( this );

		JPanel marginPanel = new JPanel( new GridLayout( 4, 2, 5, 2 ));
		SwingUtil.setTitle(marginPanel, "margins");
		marginPanel.add( new JLabel("left") );
		marginPanel.add( this.marginLeftField );
		marginPanel.add( new JLabel("right") );
		marginPanel.add( this.marginRightField );
		marginPanel.add( new JLabel( "top") );
		marginPanel.add( this.marginTopField );
		marginPanel.add( new JLabel("bottom") );
		marginPanel.add( this.marginBottomField );
		
		JPanel paddingPanel = new JPanel( new GridLayout( 6, 2, 5, 2 ));
		SwingUtil.setTitle(paddingPanel, "paddings");
		paddingPanel.add( new JLabel("left") );
		paddingPanel.add( this.paddingLeftField );
		paddingPanel.add( new JLabel("right") );
		paddingPanel.add( this.paddingRightField );
		paddingPanel.add( new JLabel("top") );
		paddingPanel.add( this.paddingTopField );
		paddingPanel.add( new JLabel("bottom") );
		paddingPanel.add( this.paddingBottomField );
		paddingPanel.add( new JLabel("horizontal") );
		paddingPanel.add( this.paddingHorizontalField );
		paddingPanel.add( new JLabel("vertical") );
		paddingPanel.add( this.paddingVerticalField );
		
		setLayout( new BorderLayout() );
		add(marginPanel, BorderLayout.NORTH );
		add( paddingPanel, BorderLayout.CENTER );

	}



	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent event) {
		if (!this.isIgnoreActionEvents) {
			updateField( (IntegerTextField) event.getSource() );
		}
	}


	/**
	 * @param field
	 */
	private void updateField(IntegerTextField field) {
		if (field.getText() == null || field.getText().length() == 0) {
			return;
		}
		MarginPaddingEditor editor = (MarginPaddingEditor) this.partEditor;
		String value = field.getText();
		if (field == this.marginLeftField) {
			editor.setMarginLeft( value );
		} else if (field == this.marginRightField) {
			editor.setMarginRight( value );
		} else if (field == this.marginTopField) {
			editor.setMarginTop( value );
		} else if (field == this.marginBottomField) {
			editor.setMarginBottom( value );
		} else if (field == this.paddingLeftField) {
			editor.setPaddingLeft(value);
		} else if (field == this.paddingRightField) {
			editor.setPaddingRight(value);
		} else if (field == this.paddingTopField) {
			editor.setPaddingTop(value);
		} else if (field == this.paddingBottomField) {
			editor.setPaddingBottom(value);
		} else if (field == this.paddingHorizontalField) {
			editor.setPaddingHorizontal(value);
		} else if (field == this.paddingVerticalField) {
			editor.setPaddingVertical(value);
		}
		
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.SwingStylePartEditor#handleActionPerformed(de.enough.polish.styleeditor.StylePartEditor, java.awt.event.ActionEvent)
	 */
	protected void handleActionPerformed(StylePartEditor stylePartEditor, ActionEvent event) {
		updateField( (IntegerTextField) event.getSource() );		
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.SwingStylePartEditor#updateGui(de.enough.polish.styleeditor.StylePartEditor)
	 */
	protected void updateGui(StylePartEditor stylePartEditor) {
		MarginPaddingEditor editor = (MarginPaddingEditor) stylePartEditor;
		this.marginLeftField.setText( editor.getMarginLeft() );
		this.marginRightField.setText( editor.getMarginRight() );
		this.marginTopField.setText( editor.getMarginTop() );
		this.marginBottomField.setText( editor.getMarginBottom() );

		this.paddingLeftField.setText( editor.getPaddingLeft() );
		this.paddingRightField.setText( editor.getPaddingRight() );
		this.paddingTopField.setText( editor.getPaddingTop() );
		this.paddingBottomField.setText( editor.getPaddingBottom() );
		this.paddingHorizontalField.setText( editor.getPaddingHorizontal() );
		this.paddingVerticalField.setText( editor.getPaddingVertical() );
	}
	
	

	

}
