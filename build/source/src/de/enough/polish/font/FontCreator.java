/*
 * Created on 09-Nov-2004 at 21:31:25.
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
import java.awt.Container;
import java.awt.Event;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import de.enough.polish.util.SwingUtil;

/**
 * <p></p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        09-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class FontCreator 
extends JFrame
implements ActionListener
{

	private JMenuItem menuSave;
	private JMenuItem menuOpen;
	private JMenuItem menuQuit;
	private JMenuItem menuSaveAs;
	private JMenuItem menuOpenTrueTypeFont;
	private JMenuItem menuOpenBitMapFont;
	private JMenuItem menuOpenImage;
	private JLabel statusBar;
	private JScrollPane scrollPane;
	private File currentDirectory = new File(".");

	/**
	 * 
	 */
	public FontCreator() {
		super();
		setJMenuBar( createMenuBar() );
		super.addWindowListener( new MyWindowListener() );
		setSize( 900, 600 );
		Container contentPane = getContentPane();
		this.scrollPane = new JScrollPane( new JLabel("Please open either a true type font, a bitmap font or an image."));
		contentPane.add( this.scrollPane );
		this.statusBar = new JLabel(" ");
		contentPane.add( this.statusBar, BorderLayout.SOUTH );
		updateTitle();
	}
	
	/**
	 * 
	 */
	private void updateTitle() {
		this.setTitle("J2ME Polish: Font Creator");
	}

	private JMenuBar createMenuBar() {
		// create menu-bar:
		JMenuBar menuBar = new JMenuBar();
		menuBar.add( createFileMenu() );
		menuBar.add( createEditMenu() );
		return menuBar;
	}
	
	private JMenu createFileMenu() {
		// create file-menu:
		JMenu menu = new JMenu( "File" );
		menu.setMnemonic('f');
 
		JMenuItem item = new JMenuItem( "Save", 'S' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'S', Event.CTRL_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuSave = item;

		item = new JMenuItem( "Save As", 'A' );
		item.addActionListener( this );
		menu.add( item );
		this.menuSaveAs = item;

		menu.addSeparator();

		item = new JMenuItem( "Open True-Type-Font", 't' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'T', Event.CTRL_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuOpenTrueTypeFont = item;

		item = new JMenuItem( "Open Bit-Map-Font", 'b' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'B', Event.CTRL_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuOpenBitMapFont = item;

		item = new JMenuItem( "Open PNG-Image", 'i' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'I', Event.CTRL_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuOpenImage = item;

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
		/*
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
		*/
		return menu;
	}

	public static void main(String[] args) {
		FontCreator creator = new FontCreator();
		creator.show();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		try {
			if ( source == this.menuQuit ) {
				quit();
			} else if ( source == this.menuOpenTrueTypeFont ) {
				openTrueTypeFont();
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.statusBar.setText( event.getActionCommand() + " failed: " + e.toString() );
		}	
	}
	
	
	
	/**
	 * @throws IOException
	 * 
	 */
	private void openTrueTypeFont() throws IOException {
		File fontFile = openFile(".ttf", true);
		if (fontFile != null) {
			TrueTypeFontViewer viewer = new TrueTypeFontViewer( fontFile );
			JLabel label = new JLabel("Hallo Welt");
			label.setFont( viewer.getFont() );
			Container contentPane = getContentPane();
			contentPane.remove( this.scrollPane );
			this.scrollPane = new JScrollPane( label );
			contentPane.add( this.scrollPane );
		}
	}

	private void quit() {
		//saveSettings();
		System.exit( 0 );
	}
	
	/**
	 * @param extension
	 * @return
	 */
	private File openFile(String extension, boolean open ) {
		File selectedFile = SwingUtil.openFile( extension, open, this.currentDirectory = new File("."), this );
		if (selectedFile != null) {
			this.currentDirectory = selectedFile.getParentFile();
		} 
		return selectedFile;
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
}
