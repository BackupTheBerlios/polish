/*
 * Created on 22-Oct-2004 at 20:10:45.
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
package de.enough.polish.dataeditor.swing;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumnModel;

import de.enough.polish.dataeditor.DataManager;
import de.enough.polish.dataeditor.DataType;

/**
 * <p>Provides a dialog for entering a new type-definition.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        22-Oct-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CreateTypeDialog 
extends JDialog
implements ActionListener
{
	
	private final CreateTypeTableModel tableModel;
	private final JTable table;
	private final JTextField nameTextField;
	private final JButton addTypeButton = new JButton( "Add Subtype");
	private final JButton deleteTypeButton = new JButton( "Remove Subtype" );
	private final JButton okButton = new JButton( "OK" );
	private final JButton cancelButton = new JButton( "Cancel" );
	private boolean okPressed;

	/**
	 * Creates a new dialog
	 * @param owner the owner of this dialog, usually SwingDataEditor
	 * @param title the title
	 * @param manager the manager of already defined types and entries
	 * @throws java.awt.HeadlessException
	 */
	public CreateTypeDialog(Frame owner, String title, DataManager manager ) throws HeadlessException {
		super(owner, title, true );
		// create a table which lists all the subtypes:
		this.tableModel =  new CreateTypeTableModel( manager );
		this.table = new JTable( this.tableModel );
		TableColumnModel colModel = this.table.getColumnModel();
		// set combo-box for types:
		JComboBox comboBox = new JComboBox();
		DataType[] knownTypes = manager.getDataTypes();
		for (int i = 0; i < knownTypes.length; i++) {
			DataType type = knownTypes[i];
			comboBox.addItem( type.getName() + " (" + type.getNumberOfBytes() + ")" );
		}
		colModel.getColumn( 1 ).setCellEditor(new DefaultCellEditor(comboBox));
		colModel.getColumn( 0 ).setPreferredWidth( 20 ); // index
		colModel.getColumn( 1 ).setPreferredWidth( 80 ); // type
	
		this.nameTextField = new JTextField( "NewTypeName" );
		this.addTypeButton.addActionListener( this );
		this.deleteTypeButton.addActionListener( this );
		this.okButton.addActionListener( this );
		this.cancelButton.addActionListener( this );
		// add UI elements:
		Container contentPane = this.getContentPane();
		// set layout manager:
		contentPane.setLayout( new GridLayout( 6, 1 ) );
		contentPane.add( this.nameTextField );
		contentPane.add( this.addTypeButton );
		contentPane.add( this.deleteTypeButton );
		JScrollPane scrollPane = new JScrollPane(this.table );
		contentPane.add( scrollPane );
		contentPane.add( this.okButton );
		contentPane.add( this.cancelButton );
		
		setSize( 300, 600 );
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == this.addTypeButton ) {
			this.tableModel.addSubtype();
		} else if (source == this.deleteTypeButton ) {
			int index = this.table.getSelectedRow();
			if (index == -1 ) {
				showWarningMessage("Please select a row before deleting a subtype.");
			} else {
				this.tableModel.deleteSubtype( index );
			}
		} else if ( source == this.okButton ) {
			this.okPressed = true;
			this.setVisible( false );
		} else {
			this.setVisible( false );
		}
	}

	private void showWarningMessage( String message ) {
		JOptionPane.showMessageDialog( this, message, "Warning", JOptionPane.WARNING_MESSAGE );
	}

	/**
	 * @return
	 */
	public DataType getDataType() {
		DataType[] subtypes = this.tableModel.getSubtypes();
		if (!this.okPressed || subtypes.length < 2) {
			return null;
		}
		String name = this.nameTextField.getText();
		return new DataType( name, subtypes );
	}

}
