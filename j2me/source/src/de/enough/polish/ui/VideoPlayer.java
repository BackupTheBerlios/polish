//#condition polish.api.mmapi
/*
 * Created on 18.11.2005 at 10:58:44.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
import java.io.InputStream;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.ToneControl;
import javax.microedition.media.control.VideoControl;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class VideoPlayer extends Canvas{
private Player p;
private PlayerListener pListener;
	public VideoPlayer() {
		super();
		// TODO Auto-generated constructor stub
//		this.pListener = new PlayerListener();
	}
	
	public void start(String url){
		
			try {
				VideoControl vc;
//				InputStream is = getClass().getResourceAsStream(url);
				System.out.print("1\n");
				this.p = Manager.createPlayer(url);
				System.out.print("2\n");
				vc = (VideoControl)this.p.getControl("VideoControl");
				System.out.print("3\n");
				vc.initDisplayMode(vc.USE_GUI_PRIMITIVE, null);
				this.p.setLoopCount(5);
				this.p.start();
			} catch (MediaException e) {
				System.out.print("Error\n");
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void stop(){
		try {
			this.p.stop();
			this.p.setMediaTime(0);
		} catch (MediaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void pause(){
		try {
			this.p.stop();
		} catch (MediaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void paint(Graphics g) {
		// TODO Auto-generated method stub
		g.fillRect(0,0,getWidth(),getHeight());
	}
}
