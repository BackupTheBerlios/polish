/*
 * Created on 19-Oct-2004 at 17:17:11.
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

import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import de.enough.polish.dataeditor.DataEntry;
import de.enough.polish.dataeditor.DataManager;
import de.enough.polish.dataeditor.DataType;

/**
 * <p>Provides a swing-based GUI for the binary data editor.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        19-Oct-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SwingDataEditor 
extends JFrame
implements ActionListener
{
	private JMenuItem menuNewDefinition;	
	private JMenuItem menuOpenDefinition;
	private JMenuItem menuOpenData;
	private JMenuItem menuSaveDefinition;
	private JMenuItem menuSaveDefinitionAs;
	private JMenuItem menuSaveData;
	private JMenuItem menuSaveDataAs;
	private JMenuItem menuQuit;
	private JMenuItem menuAddEntry;
	private File definitionFile;
	private File dataFile;
	private final DataManager dataManager;
	private final DataTableModel dataTableModel;
	private final DataView dataView;
	private File currentDirectory = new File(".");
	

	/**
	 */
	public SwingDataEditor( ) {
		super( "J2ME Polish: Data Editor");
		setJMenuBar( createMenuBar() );
		super.addWindowListener( new MyWindowListener() );
		this.dataManager = new DataManager();
		DataEntry entry = new DataEntry( "noname", DataType.BYTE );
		this.dataManager.addDataEntry( entry );
		setSize( 900, 600 );
		// add data-view:
		this.dataTableModel = new DataTableModel( this.dataManager, this );
		this.dataView = new DataView( this.dataTableModel, this.dataManager );
		this.dataView.setPreferredScrollableViewportSize(new Dimension(900, 550));
		JScrollPane scrollPane = new JScrollPane(this.dataView);
		getContentPane().add( scrollPane );
	}
	
	private JMenuBar createMenuBar() {
		// create menu-bar:
		JMenuBar menuBar = new JMenuBar();
		menuBar.add( createFileMenu() );
		menuBar.add( createEditMenu() );
		return menuBar;
	}
	
	

	/**
	 * @param menuBar
	 * @return
	 */
	private JMenu createFileMenu() {
		// create file-menu:
		JMenu menu = new JMenu( "File" );
		menu.setMnemonic('f');
		JMenuItem item = new JMenuItem( "New", 'N' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'N', Event.CTRL_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuNewDefinition = item;
		item = new JMenuItem( "Save Definition", 's' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'S', Event.CTRL_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuSaveDefinition = item;
		item = new JMenuItem( "Save Definition As...", 'a' );
		item.addActionListener( this );
		menu.add( item );
		this.menuSaveDefinitionAs = item;
		item = new JMenuItem( "Open Definition", 'o' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'O', Event.CTRL_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuOpenDefinition = item;
		menu.addSeparator();
		item = new JMenuItem( "Save Data", 'd' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'S', Event.CTRL_MASK + Event.SHIFT_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuSaveData = item;
		item = new JMenuItem( "Save Data As...", 't' );
		item.addActionListener( this );
		menu.add( item );
		this.menuSaveDataAs = item;
		item = new JMenuItem( "Open Data", 'o' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'O', Event.CTRL_MASK + Event.SHIFT_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuOpenData = item;
		menu.addSeparator();
		item = new JMenuItem( "Quit", 'q' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'Q', Event.CTRL_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuQuit = item;
		return menu;
	}
	
	private JMenu createEditMenu() {
		// create file-menu:
		JMenu menu = new JMenu( "Edit" );
		menu.setMnemonic('e');
		JMenuItem item = new JMenuItem( "Add Entry", 'a' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'N', Event.CTRL_MASK + Event.SHIFT_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuAddEntry = item;
		return menu;
	}
	

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		System.out.println( event.getActionCommand() );
		Object source = event.getSource();
		if ( source == this.menuQuit ) {
			quit();
		} else if (source == this.menuNewDefinition ) {
			newDefinition();
		} else if (source == this.menuOpenDefinition ) {
			openDefinition();
		} else if (source == this.menuSaveDefinition ) {
			saveDefinition();
		} else if (source == this.menuSaveDefinitionAs ) {
			saveDefinitionAs();
		} else if (source == this.menuOpenData) {
			openData();
		} else if (source == this.menuSaveData ) {
			saveData();
		} else if (source == this.menuSaveDataAs ) {
			saveDataAs();
		} else if (source == this.menuAddEntry ) {
			addDataEntry();
		}
	}
	
	/**
	 * 
	 */
	private void newDefinition() {
		this.dataManager.clear();
		DataEntry entry = new DataEntry( "noname", DataType.BYTE );
		this.dataManager.addDataEntry( entry );
		this.dataTableModel.refresh();
	}

	/**
	 * 
	 */
	private void addDataEntry() {
		DataEntry entry = new DataEntry( "noname", DataType.BYTE );
		this.dataManager.addDataEntry( entry );
		this.dataTableModel.refresh();
	}

	/**
	 * 
	 */
	private void saveDefinition() {
		if (this.definitionFile == null) {
			saveDefinitionAs();
			return;
		} 
		try {
			this.dataManager.saveDefinition(this.definitionFile);
		} catch (Exception e) {
			showErrorMessage( e );
		}
	}

	/**
	 * 
	 */
	private void saveDefinitionAs() {
		File file = openFile( ".definition", false );
		if (file != null ) {
			try {
				if (file.getName().endsWith(".definition")) {
					file = new File( file.getAbsolutePath() + ".definition" );  
				}
				this.dataManager.saveDefinition( file );
				this.definitionFile = file;
			} catch (Exception e) {
				showErrorMessage( e );
			}
		}
	}

	private void openDefinition() {
		File file = openFile( ".definition", true );
		if (file != null) {
			try {
				this.dataManager.loadDefinition(file);
				this.dataView.updateTypes(this.dataManager);
				this.dataTableModel.refresh();
				this.definitionFile = file;
			} catch (Exception e) {
				showErrorMessage( e );
			}
		} else {
			System.out.println("no file selected.");
		}
	}
	
	private void saveData() {
		if (this.dataFile == null) {
			saveDataAs();
			return;
		}
		try {
			this.dataManager.saveData( this.dataFile );
		} catch (Exception e) {
			showErrorMessage( e );
		}
	}
	
	private void saveDataAs() {
		File file = openFile( null, false );
		if (file != null) {
			try {
				this.dataManager.saveData(file);
				this.dataFile = file;
			} catch (Exception e) {
				showErrorMessage( e );
			}
		}
	}
	
	private void openData() {
		File file = openFile( null, true );
		if (file != null) {
			try {
				this.dataManager.loadData(file);
				this.dataFile = file;
				this.dataTableModel.refresh();
			} catch (Exception e) {
				showErrorMessage( e );
			}
		}
	}
	
	/**
	 * @param extension
	 * @return
	 */
	private File openFile(String extension, boolean open ) {
		JFileChooser fileChooser = new JFileChooser( this.currentDirectory );
		if (extension != null) {
			fileChooser.setFileFilter( new CustomFileFilter( extension ) );
		}
		int result;
		if (open) {
			result = fileChooser.showOpenDialog( this );
		} else {
			result = fileChooser.showSaveDialog( this );
		}
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			if (selectedFile != null) {
				this.currentDirectory = selectedFile.getParentFile();
			} 
			return selectedFile;
		} else {
			return null;
		}
	}

	public void quit() {
		System.exit( 0 );
	}
	
	private void showErrorMessage( Throwable exception ) {
		exception.printStackTrace();
		JOptionPane.showMessageDialog( this, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
	}
	
	public static void main(String[] args) {
		SwingDataEditor editor = new SwingDataEditor();
		editor.setVisible( true );
	}

	
	class MyWindowListener implements WindowListener {
		public void windowActivated(WindowEvent e) {
			// ignore
		}

		public void windowClosed(WindowEvent e) {
			// ignore
		}

		public void windowClosing(WindowEvent e) {
			quit();
		}

		public void windowDeactivated(WindowEvent e) {
			// ignore
		}

		public void windowDeiconified(WindowEvent e) {
			// ignore
		}

		public void windowIconified(WindowEvent e) {
			// ignore
		}

		public void windowOpened(WindowEvent e) {
			// ignore
		}
	}
	
	class CustomFileFilter extends FileFilter {
		String type;
		public CustomFileFilter( String type ) {
			this.type = type;
		}
		/* (non-Javadoc)
		 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
		 */
		public boolean accept(File f) {
			return (f.isDirectory() || f.getName().endsWith( this.type ));
		}
		/* (non-Javadoc)
		 * @see javax.swing.filechooser.FileFilter#getDescription()
		 */
		public String getDescription() {
			return this.type;
		}	
	}

}
