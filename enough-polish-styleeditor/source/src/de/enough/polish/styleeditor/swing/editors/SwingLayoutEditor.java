/*
 * Created on Apr 24, 2007 at 2:06:25 AM.
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

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import de.enough.polish.styleeditor.StylePartEditor;
import de.enough.polish.styleeditor.editors.LayoutEditor;
import de.enough.polish.styleeditor.swing.SwingStylePartEditor;
import de.enough.polish.util.SwingUtil;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 24, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SwingLayoutEditor 
extends SwingStylePartEditor
{
	
	private final JRadioButton topLeft; 
	private final JRadioButton topCenter; 
	private final JRadioButton topRight; 
	private final JRadioButton middleLeft; 
	private final JRadioButton middleCenter; 
	private final JRadioButton middleRight; 
	private final JRadioButton bottomLeft; 
	private final JRadioButton bottomCenter; 
	private final JRadioButton bottomRight; 
	private final JCheckBox horizontalExpand;
	private final JCheckBox horizontalShrink;
	private final JCheckBox verticalExpand;
	private final JCheckBox verticalShrink;
	private final JCheckBox newLineBefore;
	private final JCheckBox newLineAfter;
	private final JPanel orientationPanel;
	
	public SwingLayoutEditor() {
		this.orientationPanel = new JPanel( new GridLayout( 3, 3));
		SwingUtil.setTitle( this.orientationPanel, "orientation");
		
		this.topLeft = new JRadioButton();
		this.topLeft.addActionListener( this );
		this.topCenter = new JRadioButton();
		this.topCenter.addActionListener( this );
		this.topRight = new JRadioButton();
		this.topRight.addActionListener( this );
		this.middleLeft = new JRadioButton();
		this.middleLeft.addActionListener( this );
		this.middleCenter = new JRadioButton();
		this.middleCenter.addActionListener( this );
		this.middleRight = new JRadioButton();
		this.middleRight.addActionListener( this );
		this.bottomLeft = new JRadioButton();
		this.bottomLeft.addActionListener( this );
		this.bottomCenter = new JRadioButton();
		this.bottomCenter.addActionListener( this );
		this.bottomRight = new JRadioButton();
		this.bottomRight.addActionListener( this );
		this.horizontalExpand = new JCheckBox( "horizontal expand" );
		this.horizontalExpand.addActionListener( this );
		this.horizontalShrink = new JCheckBox( "horizontal shrink" );
		this.horizontalShrink.addActionListener( this );
		this.verticalExpand = new JCheckBox( "vertical expand" );
		this.verticalExpand.addActionListener( this );
		this.verticalShrink = new JCheckBox( "vertical shrink" );
		this.verticalShrink.addActionListener( this );
		this.newLineBefore = new JCheckBox( "newline before" );
		this.newLineBefore.addActionListener( this );
		this.newLineAfter = new JCheckBox( "newline after" );
		this.newLineAfter.addActionListener( this );
		
		
		ButtonGroup orientationGroup = new ButtonGroup();
		this.orientationPanel.add( this.topLeft );
		orientationGroup.add( this.topLeft );
		this.orientationPanel.add( this.topCenter );
		orientationGroup.add( this.topCenter );
		this.orientationPanel.add( this.topRight );
		orientationGroup.add( this.topRight );
		this.orientationPanel.add( this.middleLeft );
		orientationGroup.add( this.middleLeft );
		this.orientationPanel.add( this.middleCenter );
		orientationGroup.add( this.middleCenter );
		this.orientationPanel.add( this.middleRight );
		orientationGroup.add( this.middleRight );
		this.orientationPanel.add( this.bottomLeft );
		orientationGroup.add( this.bottomLeft );
		this.orientationPanel.add( this.bottomCenter );
		orientationGroup.add( this.bottomCenter );
		this.orientationPanel.add( this.bottomRight );
		orientationGroup.add( this.bottomRight );
		
		JPanel otherPanel = new JPanel( new GridLayout( 6, 1, 5, 2 ));
		otherPanel.add( this.horizontalExpand );
		otherPanel.add( this.horizontalShrink );
		otherPanel.add( this.verticalExpand );
		otherPanel.add( this.verticalShrink );
		otherPanel.add( this.newLineBefore );
		otherPanel.add( this.newLineAfter );
		setLayout( new BorderLayout() );
		JPanel panel = new JPanel( new BorderLayout() );
		panel.add( this.orientationPanel, BorderLayout.CENTER );
		panel.add( otherPanel, BorderLayout.EAST );
		add( panel, BorderLayout.NORTH );
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.SwingStylePartEditor#handleActionPerformed(de.enough.polish.styleeditor.StylePartEditor, java.awt.event.ActionEvent)
	 */
	protected void handleActionPerformed(StylePartEditor stylePartEditor, ActionEvent event) {
		LayoutEditor editor = (LayoutEditor) stylePartEditor;
		Object source = event.getSource();
		if (source == this.topLeft) {
			editor.setVerticalTop();
			editor.setHorizontalLeft();
		} else if (source == this.topCenter) {
			editor.setVerticalTop();
			editor.setHorizontalCenter();
		} else if (source == this.topRight) {
			editor.setVerticalTop();
			editor.setHorizontalRight();
		} else if (source == this.middleLeft) {
			editor.setVerticalCenter();
			editor.setHorizontalLeft();
		} else if (source == this.middleCenter) {
			editor.setVerticalCenter();
			editor.setHorizontalCenter();
		} else if (source == this.middleRight) {
			editor.setVerticalCenter();
			editor.setHorizontalRight();
		} else if (source == this.bottomLeft) {
			editor.setVerticalBottom();
			editor.setHorizontalLeft();
		} else if (source == this.bottomCenter) {
			editor.setVerticalBottom();
			editor.setHorizontalCenter();
		} else if (source == this.bottomRight) {
			editor.setVerticalBottom();
			editor.setHorizontalRight();
		} else if (source == this.horizontalExpand) {
			boolean isSelected =  this.horizontalExpand.isSelected();
			editor.setHorizontalExpand( isSelected );
			if (isSelected && this.horizontalShrink.isSelected()) {
				this.horizontalShrink.setSelected(false);
			}
		} else if (source == this.horizontalShrink) {
			boolean isSelected =  this.horizontalShrink.isSelected();
			editor.setHorizontalShrink( isSelected );
			if (isSelected && this.horizontalExpand.isSelected()) {
				this.horizontalExpand.setSelected(false);
			}
		} else if (source == this.verticalExpand) {
			boolean isSelected =  this.verticalExpand.isSelected();
			editor.setVerticalExpand( isSelected );
			if (isSelected && this.verticalShrink.isSelected()) {
				this.verticalShrink.setSelected(false);
			}
		} else if (source == this.verticalShrink) {
			boolean isSelected =  this.verticalShrink.isSelected();
			editor.setVerticalShrink( isSelected );
			if (isSelected && this.verticalExpand.isSelected()) {
				this.verticalExpand.setSelected(false);
			}
		} else if (source == this.newLineBefore) {
			editor.setNewLineBefore( this.newLineBefore.isSelected() );
		} else if (source == this.newLineAfter) {
			editor.setNewLineAfter( this.newLineAfter.isSelected() );
		} 
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.SwingStylePartEditor#updateGui(de.enough.polish.styleeditor.StylePartEditor)
	 */
	protected void updateGui(StylePartEditor stylePartEditor) {
		LayoutEditor editor = (LayoutEditor) stylePartEditor;
		if ( editor.isHorizontalDefault()) {
			this.topLeft.setSelected(true);
		} else if ( editor.isVerticalTopAndHorizontalLeft()) {
			this.topLeft.setSelected(true);
		} else if ( editor.isVerticalTopAndHorizontalCenter() ) {
			this.topCenter.setSelected(true);
		} else if ( editor.isVerticalTopAndHorizontalRight() ) {
			this.topRight.setSelected(true);
		} else if ( editor.isVerticalCenterAndHorizontalLeft()) {
			this.middleLeft.setSelected(true);
		} else if ( editor.isVerticalCenterAndHorizontalCenter() ) {
			this.middleCenter.setSelected(true);
		} else if ( editor.isVerticalCenterAndHorizontalRight() ) {
			this.middleRight.setSelected(true);
		} else if ( editor.isVerticalBottomAndHorizontalLeft()) {
			this.bottomLeft.setSelected(true);
		} else if ( editor.isVerticalBottomAndHorizontalCenter() ) {
			this.bottomCenter.setSelected(true);
		} else if ( editor.isVerticalBottomHorizontalRight() ) {
			this.bottomRight.setSelected(true);			
		} else if (editor.isHorizontalLeft()) {
			this.topLeft.setSelected(true);
		} else if ( editor.isHorizontalCenter() ) {
			this.topCenter.setSelected(true);
		} else if ( editor.isHorizontalRight() ) {
			this.topRight.setSelected(true);
		}
		this.horizontalExpand.setSelected(editor.isHorizontalExpand());
		this.horizontalShrink.setSelected(editor.isHorizontalShrink());
		this.verticalExpand.setSelected(editor.isVerticalExpand());
		this.verticalShrink.setSelected(editor.isVerticalShrink());
		this.newLineBefore.setSelected(editor.isNewLineBefore() );
		this.newLineAfter.setSelected(editor.isNewLineAfter() );	}

}
