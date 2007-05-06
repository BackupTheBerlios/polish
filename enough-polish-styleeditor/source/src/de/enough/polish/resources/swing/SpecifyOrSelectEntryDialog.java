/*
 * Created on May 6, 2007 at 8:27:19 AM.
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
package de.enough.polish.resources.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        May 6, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SpecifyOrSelectEntryDialog 
extends JDialog 
implements ActionListener
{
	
	private final JComboBox selectionBox; 
	private final JButton okButton;
	private final JButton cancelButton;
	private String selectedText;

	
	public SpecifyOrSelectEntryDialog( Frame owner, String title, String text, Object[] options, String suggestion ) {
		super( owner, title, true );
		this.okButton = new JButton("Ok");
		this.okButton.setMnemonic('o');
		this.okButton.addActionListener(this);
		this.cancelButton = new JButton("Cancel");
		this.okButton.setMnemonic('c');
		this.cancelButton.addActionListener(this);
		JComboBox box = new JComboBox( options );
		this.selectionBox = box;
		box.setEditable(true);
		if (suggestion != null) {
			box.setSelectedItem(suggestion);
		}
		
		Container contentPane = getContentPane();
		contentPane.setLayout( new BorderLayout() );
		contentPane.add( new JLabel(text), BorderLayout.NORTH );
		contentPane.add( box, BorderLayout.CENTER );
		JPanel okCancelPanel = new JPanel( new GridLayout(1, 2, 5, 4 ));
		okCancelPanel.add( this.okButton );
		okCancelPanel.add( this.cancelButton );
		contentPane.add( okCancelPanel, BorderLayout.SOUTH );
		pack();		
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == this.okButton) {
			Object item = this.selectionBox.getSelectedItem();
			if (item != null) {
				this.selectedText = item.toString();
			}
		}
		setVisible(false);			
	}
	
	public String getSelectedText() {
		return this.selectedText;
	}
	
	public static String showDialog(Frame owner, String title, String text, Object[] options, String suggestion ) {
		SpecifyOrSelectEntryDialog dialog = new SpecifyOrSelectEntryDialog( owner, title, text, options, suggestion );
		dialog.setVisible(true);
		return dialog.getSelectedText();
	}

}
