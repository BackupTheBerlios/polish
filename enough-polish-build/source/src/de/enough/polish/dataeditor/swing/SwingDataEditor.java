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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import de.enough.polish.dataeditor.DataEntry;
import de.enough.polish.dataeditor.DataManager;
import de.enough.polish.dataeditor.DataType;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.SwingUtil;

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
	private JMenuItem menuSaveAll;
	private JMenuItem menuQuit;
	private JMenuItem menuAddEntry;
	private JMenuItem menuDeleteEntry;
	private JMenuItem menuMoveUpEntry;
	private JMenuItem menuMoveDownEntry;
	private JMenuItem menuAddType;
	private final JLabel statusBar;
	private File definitionFile;
	private File dataFile;
	private final DataManager dataManager;
	private final DataTableModel dataTableModel;
	private final DataView dataView;
	private File currentDirectory = new File(".");
	private JMenuItem menuGenerateCode;
	
	private CreateCodeDialog createCodeDialog;
	private Image icon;
	private String packageName;
	

	/**
	 */
	public SwingDataEditor( ) {
		super();
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
		Container contentPane = getContentPane();
		contentPane.add( scrollPane );
		this.statusBar = new JLabel(" ");
		contentPane.add( this.statusBar, BorderLayout.SOUTH );
		updateTitle();
		
		this.icon = getToolkit().createImage("binaryeditor.png");
		if (this.icon != null) {
			this.setIconImage( this.icon );
		}
		
		loadSettings();
	}
	
	private JMenuBar createMenuBar() {
		// create menu-bar:
		JMenuBar menuBar = new JMenuBar();
		menuBar.add( createFileMenu() );
		menuBar.add( createEditMenu() );
		menuBar.add( createCodeMenu() );
		return menuBar;
	}
	
	


	/**
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
		item = new JMenuItem( "Save All", 'A' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'S', Event.CTRL_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuSaveAll = item;
		menu.addSeparator();
		item = new JMenuItem( "Save Definition", 's' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'S', Event.ALT_MASK ));
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
		item.setAccelerator( KeyStroke.getKeyStroke( 'S', Event.ALT_MASK + Event.SHIFT_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuSaveData = item;
		item = new JMenuItem( "Save Data As...", 't' );
		item.addActionListener( this );
		menu.add( item );
		this.menuSaveDataAs = item;
		item = new JMenuItem( "Open Data", 'o' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'O', Event.ALT_MASK + Event.SHIFT_MASK ));
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
		// create edit-menu:
		JMenu menu = new JMenu( "Edit" );
		menu.setMnemonic('e');
		JMenuItem item = new JMenuItem( "Add Entry", 'a' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'N', Event.CTRL_MASK + Event.SHIFT_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuAddEntry = item;
		item = new JMenuItem( "Delete Entry", 't' );
		item.addActionListener( this );
		menu.add( item );
		this.menuDeleteEntry = item;
		item = new JMenuItem( "Move Entry Down", 'd' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'D', Event.CTRL_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuMoveDownEntry = item;
		item = new JMenuItem( "Move Entry Up", 'u' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'U', Event.CTRL_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuMoveUpEntry = item;
		menu.addSeparator();
		item = new JMenuItem( "Add Custom Type", 't' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'T', Event.CTRL_MASK + Event.SHIFT_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuAddType = item;
		return menu;
	}
	
	/**
	 * @return
	 */
	private JMenu createCodeMenu() {
		// create edit-menu:
		JMenu menu = new JMenu( "Code" );
		menu.setMnemonic('c');
		JMenuItem item = new JMenuItem( "Generate Code", 'g' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'G', Event.CTRL_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuGenerateCode = item;
		return menu;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		try {
			if ( source == this.menuQuit ) {
				quit();
			} else if (source == this.menuNewDefinition ) {
				newDefinition();
			} else if (source == this.menuSaveAll ) {
				saveAll();
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
			} else if (source == this.menuDeleteEntry ) {
				deleteDataEntry();
			} else if (source == this.menuMoveDownEntry ) {
				moveDownDataEntry();
			} else if (source == this.menuMoveUpEntry ) {
				moveUpDataEntry();
			} else if (source == this.menuAddType ) {
				addDataType();
			} else if (source == this.menuGenerateCode ) {
				generateCode();
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.statusBar.setText( event.getActionCommand() + " failed: " + e.toString() );
		}
	}
	
	/**
	 * 
	 */
	private void generateCode() {
		if (this.createCodeDialog == null) {
			String className = "DataClass";
			if (this.definitionFile != null) {
				String fileName = this.definitionFile.getName();
				fileName = fileName.substring(0,1).toUpperCase() + fileName.substring( 1 );
				int index = fileName.lastIndexOf('.');
				if (index != -1) {
					className = fileName.substring( 0, index );
				} else {
					className = fileName;
				}
			}
			String pack = "com.company.data";
			if (this.packageName != null) {
				pack = this.packageName;
			}
			this.createCodeDialog = new CreateCodeDialog( this, "Generate Code", pack, className );
		}
		this.createCodeDialog.setVisible( true );
		if (this.createCodeDialog.okPressed()) {
			String className = this.createCodeDialog.getClassName();
			this.packageName = this.createCodeDialog.getPackageName();
			String code = this.dataManager.generateJavaCode( this.packageName, className );
			CodeEditor codeEditor = new CodeEditor( className + ".java", code, this.icon );
			codeEditor.setVisible(true);
		}
	}

	/**
	 * 
	 */
	private void moveUpDataEntry() {
		int row = this.dataView.getSelectedRow();
		if (row != -1) {
			if (this.dataManager.pushUpDataEntry(row)) {
				this.dataTableModel.refresh( this.dataView );
				this.dataView.setRowSelectionInterval(row - 1, row - 1 );
			}
		}
	}

	/**
	 * 
	 */
	private void moveDownDataEntry() {
		int row = this.dataView.getSelectedRow();
		if (row != -1) {
			if (this.dataManager.pushDownDataEntry(row)) {
				this.dataTableModel.refresh( this.dataView );
				this.dataView.setRowSelectionInterval(row + 1, row + 1 );
			}
		}
	}

	/**
	 * 
	 */
	private void deleteDataEntry() {
		int row = this.dataView.getSelectedRow();
		if (row != -1) {
			this.dataManager.removeDataEntry(row);
			this.dataTableModel.refresh( this.dataView );
			if (this.dataManager.getNumberOfEntries() > 0) {
				this.dataView.setRowSelectionInterval(row -1, row -1);
			}
		}
	}

	/**
	 * 
	 */
	private void addDataType() {
		CreateTypeDialog dialog = new CreateTypeDialog( this, "Add a custom type", this.dataManager );
		dialog.show();
		DataType newType = dialog.getDataType();
		if (newType != null ) {
			this.dataManager.addDataType(newType);
			this.dataView.updateTypes(this.dataManager);
			this.dataTableModel.refresh( this.dataView );
		}
	}

	/**
	 * 
	 */
	private void saveAll() {
		if (this.definitionFile != null) {
			saveDefinition();
			saveData();
		} else { 
			saveDefinitionAs();
			saveDataAs();
		}
		
	}

	/**
	 * 
	 */
	private void newDefinition() {
		this.dataManager.clear();
		DataEntry entry = new DataEntry( "noname", DataType.BYTE );
		this.dataManager.addDataEntry( entry );
		this.dataTableModel.refresh( this.dataView );
	}

	/**
	 * 
	 */
	private void addDataEntry() {
		DataEntry entry = new DataEntry( "noname", DataType.BYTE );
		this.dataManager.addDataEntry( entry );
		if (this.dataView.isEditing()) {
			this.dataView.getCellEditor().stopCellEditing();
		}
		this.dataTableModel.refresh( this.dataView );
		this.dataView.setRowSelectionInterval( this.dataManager.getNumberOfEntries() -1, this.dataManager.getNumberOfEntries() -1);
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
			this.statusBar.setText( "saved " + this.definitionFile.getName() );
			updateTitle();
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
				if (!file.getName().endsWith(".definition")) {
					file = new File( file.getAbsolutePath() + ".definition" );  
				}
				this.dataManager.saveDefinition( file );
				this.definitionFile = file;
				this.statusBar.setText("saved " + file.getName() );
				updateTitle();
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
				this.dataTableModel.refresh( this.dataView );
				this.definitionFile = file;
				updateTitle();
				this.statusBar.setText("Loaded " + file.getName() );
				this.createCodeDialog = null;
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
			this.statusBar.setText("saved " + this.dataFile.getName() );
			updateTitle();
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
				this.statusBar.setText("saved " + file.getName() );
				updateTitle();
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
				this.dataTableModel.refresh( this.dataView );
				updateTitle();
				this.statusBar.setText("Loaded " + file.getName() );
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
		File selectedFile = SwingUtil.openFile( extension, open, this.currentDirectory, this );
		if (selectedFile != null) {
			this.currentDirectory = selectedFile.getParentFile();
		} 
		return selectedFile;
	}
	
	private void updateTitle() {
		if (this.definitionFile != null) {
			if (this.dataFile != null) {
				setTitle("J2ME Polish: Binary Data Editor: " + this.definitionFile.getName() + " - " + this.dataFile.getName() );
			} else {
				setTitle("J2ME Polish: Binary Data Editor: " + this.definitionFile.getName() );
			}
		} else {
			setTitle("J2ME Polish: Binary Data Editor");
		}
	}

	private void quit() {
		saveSettings();
		System.exit( 0 );
	}
	
	private void saveSettings() {
		if (this.definitionFile != null) {
			ArrayList lines = new ArrayList();
			// save start of file:
			lines.add( "<!-- Settings of the J2ME Polish binary editor from " + (new Date()).toString() + " -->" );
			lines.add( "<binaryeditor-settings>" );
			lines.add( "\t<setting" );
			lines.add( "\t\tdefinitionPath=\"" + this.definitionFile.getAbsolutePath() + "\"" );
			if (this.dataFile != null) {
				lines.add( "\t\tdataPath=\"" + this.dataFile.getAbsolutePath() + "\"" );
			}
			if (this.packageName != null) {
				lines.add( "\t\tpackage=\"" + this.packageName + "\"" );
			}
			lines.add( "\t/>");
			lines.add( "</binaryeditor-settings>");
			try {
				String[] textLines = (String[]) lines.toArray( new String[ lines.size() ] );
				FileUtil.writeTextFile( new File(".binaryeditor.settings"), textLines );
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Unable to save settings: " + e.toString() );
			}
		}		
	}
	
	private void loadSettings() {
		try {
			File settingsFile = new File(".binaryeditor.settings");
			if (settingsFile.exists()) {
				FileInputStream in = new FileInputStream( settingsFile );
				SAXBuilder builder = new SAXBuilder( false );
				Document document = builder.build( in );
				Element setting = document.getRootElement().getChild("setting");
				if (setting != null) {
					String definitionPath = setting.getAttributeValue("definitionPath");
					if (definitionPath != null) {
						File defFile = new File( definitionPath );
						if (defFile.exists()) {
							this.dataManager.loadDefinition(defFile);
							this.definitionFile = defFile;
							this.currentDirectory = this.definitionFile.getParentFile();
							String dataPath = setting.getAttributeValue("dataPath");
							if (dataPath != null) {
								File file = new File( dataPath );
								if (file.exists()) {
									this.dataManager.loadData(file);
									this.dataFile = file;
								}
							}
						}
					}
					this.packageName = setting.getAttributeValue("package");
					updateTitle();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Unable to load the settings: " + e.toString() );
		}
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

	/**
	 * Sets the status bar message.
	 * 
	 * @param message the message which should be shown on the status bar
	 */
	public void setStatusBar(String message) {
		this.statusBar.setText( message );
	}

}
