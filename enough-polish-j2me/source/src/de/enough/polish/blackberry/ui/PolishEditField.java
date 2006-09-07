//#condition polish.usePolishGui && polish.blackberry

/*
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
package de.enough.polish.blackberry.ui;

import de.enough.polish.ui.Screen;
import de.enough.polish.ui.StyleSheet;
import net.rim.device.api.ui.component.EditField;

public class PolishEditField extends EditField {


        private boolean isFocused;
        public boolean processKeyEvents = true;

        public PolishEditField(String label, String text, int numChars, long style) {
                super(label, text, numChars, style);
        }

        public void focusAdd( boolean draw ) {
                //System.out.println("EditField: focusAdd (" + getText() + ")");
                super.focusAdd( draw );
                this.isFocused = true;
        }

        public void focusRemove() {
                //System.out.println("EditField: focusRemove (" + getText() + ")");
                super.focusRemove();
                this.isFocused = false;
        }

        public void layout( int width, int height) {
                super.layout( width, height );
        }
        
        public void paint( net.rim.device.api.ui.Graphics g ) {
                if (this.isFocused ) { // && !StyleSheet.currentScreen.isMenuOpened()) {
                      super.paint( g );
              }
        }
        
        
        public void paint( int x, int y, Graphics g ) {
                this.isFocused = true;
                setPosition(x, y);
                //paint( g.g );
        }
        
        //#if false
        public void paint( int x, int y, javax.microedition.lcdui.Graphics g ) {
        	// just a dummy method which is just needed for IDEs
        }
        //#endif

//      /* (non-Javadoc)
//       * @see net.rim.device.api.ui.component.EditField#keyDown(int, int)
//       */
//      protected boolean keyDown(int arg0, int arg1) {
//              System.out.println("EditField: keyDown");
//              // TODO Auto-generated method stub
//              return super.keyDown(arg0, arg1);
//      }
//
//      /* (non-Javadoc)
//       * @see net.rim.device.api.ui.component.EditField#keyRepeat(int, int)
//       */
//      protected boolean keyRepeat(int arg0, int arg1) {
//              System.out.println("EditField: keyRepeat");
//              // TODO Auto-generated method stub
//              return super.keyRepeat(arg0, arg1);
//      }
//
//      /* (non-Javadoc)
//       * @see net.rim.device.api.ui.component.EditField#moveFocus(int, int, int)
//       */
//      protected int moveFocus(int arg0, int arg1, int arg2) {
//              System.out.println("EditField: moveFocus");
//              // TODO Auto-generated method stub
//              return super.moveFocus(arg0, arg1, arg2);
//      }
//
//      /* (non-Javadoc)
//       * @see net.rim.device.api.ui.Field#fieldChangeNotify(int)
//       */
//      protected void fieldChangeNotify(int arg0) {
//              System.out.println("EditField: fieldChangeNotify");
//              // TODO Auto-generated method stub
//              super.fieldChangeNotify(arg0);
//      }
//
//      /* (non-Javadoc)
//       * @see net.rim.device.api.ui.Field#invalidate()
//       */
//      public void invalidate() {
//              System.out.println("EditField: invalidate");
//              // TODO Auto-generated method stub
//              super.invalidate();
//      }
//
//      /* (non-Javadoc)
//       * @see net.rim.device.api.ui.Field#invalidate(int, int, int, int)
//       */
//      protected void invalidate(int arg0, int arg1, int arg2, int arg3) {
//              System.out.println("EditField: invalidate(x,y,w,h)");
//              // TODO Auto-generated method stub
//              super.invalidate(arg0, arg1, arg2, arg3);
//      }
//
//      /* (non-Javadoc)
//       * @see net.rim.device.api.ui.Field#onDisplay()
//       */
//      public void onDisplay() {
//              System.out.println("EditField: onDisplay");
//              // TODO Auto-generated method stub
//              super.onDisplay();
//      }
//
//      /* (non-Javadoc)
//       * @see net.rim.device.api.ui.Field#onExposed()
//       */
//      public void onExposed() {
//              System.out.println("EditField: onExposed");
//              // TODO Auto-generated method stub
//              super.onExposed();
//      }
//
//      /* (non-Javadoc)
//       * @see net.rim.device.api.ui.Field#onMenuDismissed()
//       */
//      protected void onMenuDismissed() {
//              System.out.println("EditField: onMenuDismissed");
//              // TODO Auto-generated method stub
//              super.onMenuDismissed();
//      }
//
//      /* (non-Javadoc)
//       * @see net.rim.device.api.ui.Field#onObscured()
//       */
//      protected void onObscured() {
//              System.out.println("EditField: onObscured");
//              // TODO Auto-generated method stub
//              super.onObscured();
//      }
//
//      /* (non-Javadoc)
//       * @see net.rim.device.api.ui.Field#onUndisplay()
//       */
//      public void onUndisplay() {
//              System.out.println("EditField: onUndisplay");
//              // TODO Auto-generated method stub
//              super.onUndisplay();
//      }
//
//      /* (non-Javadoc)
//       * @see net.rim.device.api.ui.Field#trackwheelClick(int, int)
//       */
//      protected boolean trackwheelClick(int arg0, int arg1) {
//              System.out.println("EditField: trackwheelClick");
//              // TODO Auto-generated method stub
//              return super.trackwheelClick(arg0, arg1);
//      }
//
//      /* (non-Javadoc)
//       * @see net.rim.device.api.ui.Field#trackwheelUnclick(int, int)
//       */
//      protected boolean trackwheelUnclick(int arg0, int arg1) {
//              System.out.println("EditField: trackwheelUnclick");
//              // TODO Auto-generated method stub
//              return super.trackwheelUnclick(arg0, arg1);
//      }
//
//      public void onFocus(int direction) {
//              System.out.println("EditField: onFocus");
//              super.onFocus( direction );
//      }
        

}
