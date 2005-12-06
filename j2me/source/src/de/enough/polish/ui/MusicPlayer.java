/*
 * Created on 25.11.2005 at 15:58:16.
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
import java.util.Enumeration;
import javax.microedition.lcdui.List;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import de.enough.polish.util.Debug;
import de.enough.polish.util.Locale;

public class MusicPlayer extends Canvas implements CommandListener{
	private Item item;
	private Player p;
	public List list;
	private String url;
	private Form form;
	private int index = 0;
	private Command cmdPlay, cmdStop,cmdExit,cmdChoice,cmdBack;
	private FileConnection fileCon;	
	private PlayerListener pListener;
		public MusicPlayer() {
			super();
			// TODO Auto-generated constructor stub
//			this.pListener = new PlayerListener();
			this.form = new Form("");
			this.list = new List("Music List", List.IMPLICIT);
			this.url = "file:///C:/Nokia/";
			try {
				this.fileCon = (FileConnection) Connector.open( url, Connector.READ );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    if( this.fileCon.isDirectory() && this.fileCon.canRead() ){
		        Enumeration names;
				try {
					names = this.fileCon.list();
			        while( names.hasMoreElements() ){
			            String name = (String) names.nextElement();
			            this.list.append(name, null);
			        }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    this.list.setSelectedIndex(this.index,true);
		    this.cmdExit = new Command("Stop", Command.STOP, 1);
		    this.cmdPlay = new Command("Play", Command.ITEM, 1 );
		    this.cmdStop = new Command("Stop", Command.ITEM, 1 );
		    this.cmdChoice = new Command("Choice", Command.ITEM,1);
		    this.cmdBack = new Command("Back", Command.ITEM,1);
		    this.addCommand(this.cmdExit);
		    this.addCommand(this.cmdPlay);
		    this.addCommand(this.cmdStop);
		    this.list.setSelectCommand(this.cmdChoice);
		    this.list.addCommand(this.cmdBack);
		    this.list.addCommand(this.cmdChoice);
		    this.list.setCommandListener(this);
//		    this.setCommandListener(this);
		}
		
		public void start(String url){
				try {
//					InputStream is = getClass().getResourceAsStream(url);
					this.p = Manager.createPlayer(url);
					this.p.setLoopCount(5);
					this.p.start();
				} catch (MediaException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		public void stop(){
			System.out.print("stop()\n");
			this.repaint();
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
			//g.fillRect(0,0,getWidth(),getHeight());      
		}
		

		protected void startApp() throws MIDletStateChangeException {
			// TODO Auto-generated method stub
			
		}

		protected void pauseApp() {
			// TODO Auto-generated method stub
			
		}

		protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
			// TODO Auto-generated method stub
			
		}

		private void backUrl(){
			this.url.length();
		}
		
		private void openUrl(){
			System.out.print("choiceCommand\n");
			System.out.print(this.url+this.list.getString(this.list.getSelectedIndex())+"\n");
			this.url = this.url+this.list.getString(this.list.getSelectedIndex());
			System.out.print("URL:"+url+"\n");
			try {
				this.fileCon = (FileConnection) Connector.open( url, Connector.READ );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    if( this.fileCon.isDirectory() && this.fileCon.canRead() ){
		        Enumeration names;
				try {
					names = this.fileCon.list();
			        this.list.deleteAll();
					while( names.hasMoreElements() ){
			            String name = (String) names.nextElement();
			            this.list.append(name, null);
			        }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
		
		public void commandAction(Command cmd, Displayable screen) {	
			System.out.print("CommandoPimPerle\n");
				if(cmd == this.cmdChoice){
					this.openUrl();
				}
				else if(cmd == this.cmdBack){
					this.backUrl();
				}
				else if (cmd == this.list.SELECT_COMMAND) {
					int selectedItem = this.list.getSelectedIndex();
					switch (selectedItem) {
						case 0 : System.out.print("Commando0\n"); break;
						case 1 : System.out.print("Commando1\n"); break;
						case 2 : System.out.print("Commando2\n");  break;
						default: System.out.print("CommandoDefault\n");  
				}
			}
		}
	}
