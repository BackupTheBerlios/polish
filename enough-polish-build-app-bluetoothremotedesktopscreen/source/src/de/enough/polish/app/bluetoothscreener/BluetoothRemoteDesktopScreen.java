/*
 * Created on Aug 4, 2008 at 9:58:32 PM.
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
package de.enough.polish.app.bluetoothscreener;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.L2CAPConnection;
import javax.bluetooth.LocalDevice;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.enough.polish.bluetooth.L2CapInputStream;
import de.enough.polish.swing.SwingApplication;
import de.enough.polish.util.StreamUtil;
import de.enough.polish.util.ZipUtil;

/**
 * <p>Allows to display log entries</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BluetoothRemoteDesktopScreen
extends SwingApplication
{

	private static final String VERSION_STR = "0.8.0";
	public static final int VERSION = 80;
	private L2CapServer server;

	/**
	 * @param title
	 * @param systemExitOnQuit
	 */
	public BluetoothRemoteDesktopScreen(String title, boolean systemExitOnQuit)
	{
		super(title, systemExitOnQuit);
		getContentPane().add( new JLabel("Waiting for incoming connection request...") );
		
	}
	
	/**
	 * Starts this proxy
	 */
	public void start()
	{
		try
		{
			System.out.println("Starting J2ME Polish Bluetooth Remote Desktop Screen " + VERSION_STR  );
			LocalDevice device = LocalDevice.getLocalDevice();
			device.setDiscoverable(DiscoveryAgent.GIAC);
			//String serviceUUID = "00000000000010008000006057028A06";
			String serviceUUID =   "5ab90370a64d11ddad8b0800200c9a66";
			String url = "btl2cap://localhost:" + serviceUUID;
			String max = LocalDevice.getProperty("bluetooth.l2cap.receiveMTU.max");
			if (max != null) {
				if (max.equals("0")) {
					max = "762";
				}
				 url += ";ReceiveMTU=" + max + ";TransmitMTU=" + max;
			}
			this.server = new L2CapServer( this, url );
			this.server.start();
		} catch (BluetoothStateException e)	{
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}
	
	/**
	 * Stops this proxy
	 */
	public void stop() {
		this.server.requestStop();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		BluetoothRemoteDesktopScreen remoteScreen = new BluetoothRemoteDesktopScreen("Bluetooth Remote Desktop Screen", true );
		remoteScreen.setVisible(true);
		remoteScreen.start();
	}

	/**
	 * @param connection
	 * @throws IOException 
	 */
	public void process(L2CAPConnection connection) throws IOException
	{
		RemoteScreenComponent component = new RemoteScreenComponent( connection );
		getContentPane().add(component);
		( new Thread( component)).start();
	}

	/**
	 * @param logServer
	 * @param e
	 */
	public void handleError(L2CapServer logServer, IOException e)
	{
		System.out.println("error: " + e.toString() );
		e.printStackTrace();
		
	}

}

class RemoteScreenComponent
extends JComponent
implements Runnable {
	private final L2CAPConnection connection;
	private BufferedImage image;

	public RemoteScreenComponent( L2CAPConnection connection) {
		this.connection = connection;
		setSize( 240, 320 );
	}
	public void run() {
		try {
			L2CapInputStream l2CapIn = new L2CapInputStream( this.connection );
			DataInputStream in = new DataInputStream( l2CapIn );
			int version = in.readInt();
			if (version != BluetoothRemoteDesktopScreen.VERSION) {
				System.out.println("unsupported version: " + version);
				return;
			}
			String midletName = in.readUTF();
			System.out.println("MIDlet: " + midletName);
			int width = in.readInt();
			int height = in.readInt();
			int degrees = in.readInt();
			System.out.println("width=" + width + " , height=" + height + ", degrees=" + degrees);
			BufferedImage img = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
			this.image = img;
			setSize(width, height);
			while (true) {
				System.out.println("Waiting for next event...");
				System.out.println("message=" + in.readUTF());
				int x = in.readShort();
				long time = System.currentTimeMillis();
				int y = in.readShort();
				width = in.readShort();
				height = in.readShort();
				int numberOfChunks = in.readShort();
				System.out.println("Update: " + x + ", " + y + ", " + width + ", " + height + ", chunks=" + numberOfChunks);
				int[] rgb = new int[ width * height];
				int rgbOffset = 0;
				for (int i=0; i<numberOfChunks; i++) {
					int len = in.readShort();
					byte[] data = new byte[len];
					in.readFully(data);
					int[] rgbPart = ZipUtil.convertByteToRgbArray(data); //decompressRgbArray(data);
					System.arraycopy( rgbPart, 0, rgb, rgbOffset, rgbPart.length );
					rgbOffset += rgbPart.length;
				}
				System.out.println("download took " + (System.currentTimeMillis() - time) + " ms.");
//				int len = in.readInt();
//				byte[] data = new byte[len];
//				in.readFully(data);
//				System.out.println("download took " + (System.currentTimeMillis() - time) + " ms.");
//				int[] rgb = ZipUtil.decompressRgbArray(data);
//				System.out.println("Update: " + x + ", " + y + ", " + width + ", " + height + ", bytes=" + len + ", rgb-bytes=" + (width*height*4) );
				try {
					img.setRGB(x, y, width, height, rgb, 0, width);
					repaint();
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Unable to call setRGB: " + e.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("connection failure or closed: " + e.toString());
		} finally {
			try {
				this.connection.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if (this.image != null) {
			g.drawImage(this.image, 0, 0, null);
		} else {
			g.drawString( "Waiting for data...", 2, 100);
		}
	}
}