/*
 * Created on Dec 3, 2004 at 7:08:43 PM.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.polish.swing;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;


/**
 * <p>Base class for standalong swing applications.</p>
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        Dec 3, 2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */

public class SwingApplication 
extends JFrame 
implements Application
{
	
	/**
	 * <code>serialVersionUID</code> denotes the ID for serialization
	 */
	private static final long serialVersionUID = 1L;
	protected boolean systemExitOnQuit;
	protected boolean isMacOsX;
	protected NativeIntegration nativeIntegration;
	protected String applicationName;
	protected final ApplicationDropListener applicationDropListener;
	
	public SwingApplication( String title, boolean systemExitOnQuit ) {
		super( title );
		this.applicationName = title;
		this.systemExitOnQuit = systemExitOnQuit;
		this.isMacOsX = (System.getProperty("mrj.version") != null);
		loadNativeIntegration();

		// accept drop-events:
		this.applicationDropListener = new ApplicationDropListener();
		new DropTarget( this,  this.applicationDropListener );
		//new DropTarget( getRootPane(), this.applicationDropListener );
		
		//register window listener:
		registerWindowListener();
	}
	
	protected void registerDropTarget( Component component ) {
		new DropTarget( component,  this.applicationDropListener );
	}

	/**
	 * Loads the integration for the underlying OS.
	 */
	protected void loadNativeIntegration() {
		try {
			Class nativeIntegrationClass = getNativeIntegrationClass();
			if (nativeIntegrationClass != null) {
				this.nativeIntegration = (NativeIntegration) nativeIntegrationClass.newInstance();
				this.nativeIntegration.init( this, this.applicationName );
			}
		} catch (Exception e) {
			System.err.println("Unable to load native integration: " + e );
			e.printStackTrace();
		}
	}
	
	
	protected Class getNativeIntegrationClass() throws ClassNotFoundException {
		if (this.isMacOsX) {
			return Class.forName("de.enough.polish.swing.MacOsXIntegration");
		} else {
			return null;
		}
	}
	
	protected String getAboutText() {
		return this.applicationName + ": (c) 2005, Enough Software.";
	}
	
	protected JScrollPane createScrollPane( JComponent component ) {
		if (this.isMacOsX) {
			return new JScrollPane(component,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		} else {
			return new JScrollPane( component );
		}
	}
	
	protected void setWindowDirtyFlag( boolean isDirty ) {
		Boolean flag = Boolean.FALSE;
		if (isDirty) {
			flag = Boolean.TRUE;
		}
		getRootPane().putClientProperty("windowModified", flag );
	}
	
	protected int getNativeShortcutKeyMask() {
		return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	}

	/**
	 * Quits the application and calls saveSettings() first.
	 * 
	 * @see #saveSettings()
	 */
	public void quit() {
		if (saveSettings()) {
			if (this.systemExitOnQuit) {
				System.exit(0);
			} else {
				setVisible( false );
			}
		}
	}

	/**
	 * Saves the settings of the application.
	 * The default implementation always returns true.
	 * 
	 * @return true when the settings could be saved and the application can continue to quit.
	 */
	protected boolean saveSettings() {
		return true;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.swing.Application#about()
	 */
	public void about() {
		JOptionPane.showMessageDialog( this, getAboutText() );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.swing.Application#preferences()
	 */
	public void preferences() {
		// can be implemented by sub class
		System.out.println("preferences: not supported.");
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.swing.Application#openApplication()
	 */
	public void openApplication() {
		// can be implemented by sub class
		System.out.println("open application: not supported.");
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.swing.Application#openDocument( File )
	 */
	public void openDocument( File file ) {
		// can be implemented by sub class
		System.out.println("open document: not supported.");
	}
	
	
	/**
	 * Registers a standard window listener that in turns calls quit when the main window is closed.
	 */
	protected void registerWindowListener() {
		super.addWindowListener( new MyWindowListener() );		
	}
	
	
	class ApplicationDropListener extends DropTargetAdapter {

		/* (non-Javadoc)
		 * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
		 */
		public void drop(DropTargetDropEvent event) {
			final Transferable transferable = event.getTransferable(); 
			if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				event.acceptDrop(DnDConstants.ACTION_COPY);
				try {
					final List files = (List)transferable.getTransferData( DataFlavor.javaFileListFlavor);
					// load files in background:
					( new OpenFilesThread( files ) ).start();
					// Signal transfer success.
					event.dropComplete(true);
				} catch (Exception e) {
					// Signal transfer failure.
					event.dropComplete(false); 
				}
			} else {
				event.rejectDrop();
			} 
		}
		
	}
	
	class OpenFilesThread extends Thread {
		private final List files;
		public OpenFilesThread( List files ) {
			this.files = files;
		}
		public void run() {
			for (Iterator iter = this.files.iterator(); iter.hasNext();) {
				File file = (File) iter.next();
				openDocument( file );
			}
		}
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
