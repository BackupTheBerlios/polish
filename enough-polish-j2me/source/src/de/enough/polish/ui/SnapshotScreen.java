//#condition polish.usePolishGui && polish.api.mmapi

/*
 * Created on Sep 8, 2006 at 5:00:10 PM.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
package de.enough.polish.ui;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.VideoControl;

import de.enough.polish.util.Arrays;
import de.enough.polish.util.TextUtil;

/**
 * <p>A convenience screen for taking snapshots. This screen requires support of the MMAPI by the current target device!</p>
 * <pre>
 * //#if polish.api.mmapi
 *    import de.enough.polish.ui.SnapshotScreen;
 * //#endif
 * ...
 * //#if polish.api.mmapi
 *    //#style snapshotScreen
 *    SnapshotScreen screen = new SnapshotScreen("Snapshot");
 * //#endif
 * </pre>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Sep 8, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SnapshotScreen extends Screen implements Runnable {
	
	private Player player;
	private VideoControl videoControl;
	private boolean isHiding;
	private boolean takeSnapshot;
	private String snapshotEncoding;
	private byte[] snapshotData;
	private MediaException error;


	/**
	 * Creates a new screen for taking screenshots.
	 * 
	 * @param title the title of the screen
	 */
	public SnapshotScreen(String title) {
		this(title, null);
	}
	
	/**
	 * Creates a new screen for taking screenshots.
	 * 
	 * @param title the title of the screen
	 * @param style the style
	 */
	public SnapshotScreen(String title, Style style ) {
		super(title, false, style );
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#hideNotify()
	 */
	public void hideNotify() {
		super.hideNotify();
		this.isHiding = true;
		//#if !polish.Bugs.SingleCapturePlayer
			Thread thread = new Thread( this );
			thread.start();
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#showNotify()
	 */
	public void showNotify() {
		super.showNotify();
		this.isHiding = false;
		Thread thread = new Thread( this );
		thread.start();
	}

	public void run() {
		if (!this.isHiding && this.player == null) {
			try {
				//#if polish.mmapi.ImageCaptureLocator:defined
					// on Series 40 this is for example "capture://image"
					//#= this.player = Manager.createPlayer("${polish.mmapi.ImageCaptureLocator}");
				//#else
					this.player = Manager.createPlayer("capture://video");
				//#endif
				this.player.realize();
				this.videoControl = (VideoControl) this.player.getControl("VideoControl");
				if (this.videoControl != null) {
					//#if polish.Bugs.displaySetCurrentFlickers && polish.useFullScreen
						this.videoControl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO, MasterCanvas.instance);
					//#else
						this.videoControl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO, this);
					//#endif
					int width = this.contentWidth;//getWidth() - 5;
					int height = this.contentHeight; //getHeight() - (this.titleHeight + 2);
					this.videoControl.setDisplayLocation(this.contentX, this.contentY );
					this.videoControl.setDisplaySize( width, height );
					this.videoControl.setVisible(true);
					this.player.prefetch();
					this.player.start();
				}
			} catch (IOException e) {
				//#debug error
				System.out.println("Unable to establish player" + e);
				this.error = new MediaException( e.toString() );
			} catch (MediaException e) {
				//#debug error
				System.out.println("Unable to establish player" + e);
				this.error = e;
			}
		}
		if (this.takeSnapshot) {
			if (this.videoControl == null) {
				this.error = new MediaException("Unable to init player: " + (this.error != null ? this.error.toString() : "unknown"));
			} else {
				try {
					this.snapshotData = this.videoControl.getSnapshot(this.snapshotEncoding);
				} catch (MediaException e) {
					this.error = e;
				}
			}
		}
		//#if !polish.Bugs.SingleCapturePlayer
			if (this.isHiding && this.player != null) {
				try {
					try {
						this.player.stop();
					} catch (MediaException e) {
						//#debug error
						System.out.println("Unable to stop player" + e);
					}
					this.player.close();
					this.player.deallocate();
				} catch (Exception e) {
					//#debug error
					System.out.println("Unable to deallocate playzer" + e);
				} finally {
					this.player = null;					
				}
			}
		//#endif
	}
	
	/**
	 * Retrieves the supported snapshot encodings available on the current device.
	 * 
	 * @return an array of encodings. 
	 *         When the "video.snapshot.encodings" system property is null, null is returned.
	 */
	public static String[] getSnapshotEncodings(){
		String supportedEncodingsStr = System.getProperty("video.snapshot.encodings");
		if(supportedEncodingsStr == null){
			return new String[0];
		}
		
		String[] encodings = TextUtil.split(supportedEncodingsStr, ' ');
		for (int i = 0; i < encodings.length; i++) {
			String encoding = encodings[i];
			if (encoding.startsWith("encoding=")) {
				encodings[i] = encoding.substring( "encoding=".length() );
			}
		}	
		Arrays.sort(encodings);
		return encodings;
	}
	
	/**
	 * Takes a snapshot in the default encoding
	 * 
	 * @return the image taken
	 * @throws MediaException when taking the snapshot fails
	 */
	public Image getSnapshotImage() throws MediaException {
		return getSnapshotImage( null );
	}
	
	/**
	 * Takes a snapshot in the desired encoding/settings
	 * 
	 * @param encoding the encoding and optionally size
	 * @return the image taken
	 * @throws MediaException when taking the snapshot fails
	 */
	public Image getSnapshotImage( String encoding ) throws MediaException {
		byte[] data = getSnapshot(encoding);
		return Image.createImage( data, 0, data.length );
	}
	

	/**
	 * Takes a snapshot in the default encoding
	 * 
	 * @return the image taken
	 * @throws MediaException when taking the snapshot fails
	 */
	public byte[] getSnapshot() throws MediaException {
		return getSnapshot( null );
	}
	
	/**
	 * Takes a snapshot in the desired encoding/settings
	 * 
	 * @param encoding the encoding and optionally size
	 * @return the image taken
	 * @throws MediaException when taking the snapshot fails
	 */
	public byte[] getSnapshot( String encoding ) throws MediaException {
		if (this.error != null) {
			throw this.error;
		}
		this.snapshotEncoding = encoding;
		this.takeSnapshot = true;
		Thread thread = new Thread( this );
		thread.start();
		while (this.snapshotData == null) {
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				// ignore
			}
		}
//		synchronized ( thread ) {
//			try {
//				thread.wait();
//			} catch (InterruptedException e) {
//				// ignore
//			}
//		}
		if (this.error != null) {
			throw this.error;
		}
		this.takeSnapshot = false;
		byte[] data = this.snapshotData;
		this.snapshotData = null;
		return data;
	}
	
	



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#paintScreen(javax.microedition.lcdui.Graphics)
	 */
	protected void paintScreen(Graphics g) {
		if (this.error != null) {
			g.drawString( this.error.toString(), getWidth() - 10, getHeight()/2, Graphics.RIGHT | Graphics.TOP );
		}
	}

	//#ifdef polish.useDynamicStyles	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#createCssSelector()
	 */
	protected String createCssSelector() {
		return "snapshotscreen";
	}
	//#endif

	
}
