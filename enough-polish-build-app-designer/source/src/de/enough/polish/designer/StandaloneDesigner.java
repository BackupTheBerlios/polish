/*
 * Created on Apr 23, 2007 at 11:55:03 PM.
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
package de.enough.polish.designer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import de.enough.polish.swing.ContextJMenuItem;
import de.enough.polish.swing.SwingApplication;
import de.enough.polish.util.CommandLineArguments;
import de.enough.polish.util.PathClassLoader;
import de.enough.polish.util.ReflectionUtil;

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
public class StandaloneDesigner extends SwingApplication
implements ActionListener
{

	private static final long serialVersionUID = 7311067142720931112L;
	private File polishHome;
	private JMenuItem menuOpen;
	private JMenuItem menuSave;
	private JMenuItem menuSaveAs;
	private JMenuItem menuQuit;
	private JComponent designer;

	/**
	 * @param title
	 * @param systemExitOnQuit
	 * @param polishHome 
	 */
	public StandaloneDesigner( String title, boolean systemExitOnQuit, File polishHome ) {
		super(title, systemExitOnQuit);
		this.polishHome = polishHome;
		
		
		setJMenuBar( createMenuBar() );
		JLabel label = new JLabel( "     Please open or drag your application (JAR or JAD) to design it.");
		label.setFont( new Font("Arial", Font.BOLD, 24 ));
		//TODO add previously edited applications

		Container contentPane = getContentPane();
		contentPane.setLayout( new BorderLayout() );
		//contentPane.add( new JScrollPane(tree), BorderLayout.EAST );
		contentPane.add( label, BorderLayout.CENTER );
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int inset = 60;
    	int availableWidth = Math.min( 800, screenSize.width  - 2 * inset );
    	int availableHeight = screenSize.height - 2 * inset;
        if ( File.separatorChar == '\\' ) {
        	// on Windows the Toolkit does not consider the height of the 
        	// menu bar for some reason...
        	availableHeight -= 30;
        }
    	setBounds(inset, inset, availableWidth, availableHeight );	
    }
	
	private JMenuBar createMenuBar() {
		// create menu-bar:
		JMenuBar menuBar = new JMenuBar();
		int shortcutKeyMask = getNativeShortcutKeyMask();
		menuBar.add( createFileMenu(shortcutKeyMask) );
		//menuBar.add( createEditMenu(shortcutKeyMask) );
		menuBar.add( createDeviceMenu(shortcutKeyMask) );
		return menuBar;
	}

	private JMenu createFileMenu(int shortcutKeyMask) {
		// create file-menu:
		JMenu menu = new JMenu( "File" );
		menu.setMnemonic('f');
		JMenuItem item = new JMenuItem( "Open Application...", 'O' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'O', shortcutKeyMask ));
		item.addActionListener( this );
		menu.add( item );
		this.menuOpen = item;
		item = new JMenuItem( "Save Design", 'S' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'S', shortcutKeyMask ));
		item.addActionListener( this );
		menu.add( item );
		this.menuSave = item;
		item = new JMenuItem( "Save Design As...", 'A' );
		//item.setAccelerator( KeyStroke.getKeyStroke( 'S', shortcutKeyMask ));
		item.addActionListener( this );
		//menu.add( item );
		this.menuSaveAs = item;
		menu.addSeparator();
		item = new JMenuItem( "Quit", 'q' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'Q', shortcutKeyMask ));
		item.addActionListener( this );
		menu.add( item );
		this.menuQuit = item;
		return menu;
	}
	
	protected JMenu createEditMenu(int shortcutKeyMask) {
		// create edit-menu:
		JMenu menu = new JMenu( "Edit" );
		menu.setMnemonic('e');
		return menu;
	}
	

	
	private JMenu createDeviceMenu(int shortcutKeyMask) {
		// create edit-menu:
		JMenu menu = new JMenu( "Device" );
		menu.setMnemonic('d');
//		Vendor[] vendors = new Vendor[0]; // this.simulationManager.getVendors();
//		for (int i = 0; i < vendors.length; i++) {
//			Vendor vendor = vendors[i];
//			JMenu vendorMenu = new JMenu( vendor.getIdentifier() );
//			Device[] devices = new Device[0]; //this.simulationManager.getDevices(vendor);
//			for (int j = 0; j < devices.length; j++) {
//				Device device = devices[j];
//				JMenuItem item = new ContextJMenuItem( device.getName(), device );
//				item.addActionListener( this );
//				vendorMenu.add( item );
//			}
//			menu.add( vendorMenu );
//		}
		return menu;
	}
	
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.swing.SwingApplication#openApplication()
	 */
	public void openApplication() {
		// TODO robertvirkus implement openApplication
		super.openApplication();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.swing.SwingApplication#openDocument(java.io.File)
	 */
	public void openDocument(File file) {
		try {
			edit( file );
		} catch (Exception e) {
			// TODO robertvirkus handle IOException
			e.printStackTrace();
		}
		
	}

	public void edit( File jadOrJarFile  ) throws IOException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		//TODO check if the jad/jar is really within the ${project.home}/dist folder...
		//System.out.println("loading application from " + jadOrJarFile.getAbsolutePath() );
		PathClassLoader classLoader = new PathClassLoader( new File( this.polishHome, "/lib/enough-j2mepolish-runtime.jar") );
		if (jadOrJarFile.getName().endsWith(".jar")) {
			classLoader.addPathFile(jadOrJarFile);
		} else {
			Properties properties = new Properties();
			properties.load( new FileInputStream( jadOrJarFile ));
			String jarUrl = properties.getProperty("MIDlet-Jar-URL");
			//TODO add support for absolute/web based MIDlet-Jar-URLs
			classLoader.addPathFile( new File( jadOrJarFile.getParentFile(), jarUrl ));
		}
		classLoader.addPathFile( new File( this.polishHome, "/lib/enough-j2mepolish-styleeditor.jar") );
		classLoader.addPathFile( new File( this.polishHome, "/lib/enough-j2mepolish-build.jar") );
		classLoader.addPathFile( new File( this.polishHome, "/lib/jdom.jar") );
		classLoader.addPathFile( new File( this.polishHome, "/lib/ant.jar") );
		this.designer = (JComponent) ReflectionUtil.newInstance( classLoader.findClass( "de.enough.polish.styleeditor.swing.SwingEditorSuite" ), new Object[]{ this.polishHome, jadOrJarFile.getParentFile().getParentFile(), jadOrJarFile} );
		Container contentPane = getContentPane();
		contentPane.removeAll();
		contentPane.add( this.designer );
		SwingUtilities.updateComponentTreeUI(contentPane);
		//pack();	
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == this.menuOpen) {
			
		} else if (source == this.menuQuit) {
			super.quit();
		} else if (source == this.menuSave) {
			try {
				ReflectionUtil.callMethod("save", this.designer );
			} catch (Exception e) {
				e.printStackTrace();
				showErrorMessageDialog( "Unable to save: " + e.toString(), "Error");
			}
		}
	}



	public static void main(String[] args) {
		CommandLineArguments arguments = new CommandLineArguments( args, new String[0], new String[0], new String[]{ "-polish.home" }, new String[]{"--polish.home"} );
		String polishHomeStr = arguments.getParameter("-polish.home", "--polish.home");
		if (polishHomeStr == null) {
			polishHomeStr = System.getProperty("polish.home");
			if (polishHomeStr == null) {
				// try if the current directory is polish.home/bin:
				File polishHomeTest = new File("../lib/enough-j2mepolish-build.jar");
				if (polishHomeTest.exists()) {
					polishHomeStr = "..";
				} else {
					throw new IllegalStateException("No --polish.home parameter nor polish.home system property found.");
				}
			}
		}
		File polishHome = new File( polishHomeStr );
		if (!polishHome.exists()) {
			throw new IllegalStateException("polish.home points to an invalid location: " + polishHomeStr );
		}
		
		StandaloneDesigner test = new StandaloneDesigner( "J2ME Polish Designer", true, polishHome );
		test.setVisible(true);
	}



}
