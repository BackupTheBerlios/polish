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

import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.text.TextFilter;
import de.enough.polish.ui.Style;


public class PolishOneLineField extends VerticalFieldManager implements PolishTextField, AccessibleField {
        
    private PolishEditField editField;
    public boolean processKeyEvents = true;
    public boolean ignoreLocalSetCurrentLocation = false;
    int oldX = 0;
    int oldY = 0;
    
    public PolishEditField getEditField()
    {
    	return this.editField;
    }   
         
    public void moveTo(int x, int y)
    {
        setPosition(x,y);
    }
    
    public void sublayout(int width, int height) 
    {        
        super.sublayout(width,height);   
        if ( this.editField != null )
        {     
            int textLength = this.editField.getFont().getAdvance( this.editField.getText() ) ;
            // Leave some room to see the cursor at the end and to prevent scroll bugs
            setVirtualExtent(textLength + width / 2 ,height);         }
    }
    
    public void relayout()
    {
        updateLayout();
    }

    public PolishOneLineField(String label, String text, int numChars, long style) {
	    super(HORIZONTAL_SCROLL | NO_VERTICAL_SCROLL);
	    //#if polish.JavaPlatform >= BlackBerry/6.0
	    	setScrollingInertial(false);
	    //#endif
	    this.editField = new PolishEditField(label, "", numChars, style | EditField.NO_NEWLINE ) 
	    {
	        protected boolean keyChar(char key, int status, int time)
	        {
	            boolean value = super.keyChar(key,status,time);     
	            relayout();                           
	            updateScroll();
	            return value;
	        }
	        
	        public void paint ( Graphics g )
	        {
	            super.paint( g ) ;
	        }
	        
	        protected void updateScroll()
	        {
	            int strLength = getFont().getAdvance( this.getText() ) ;
	            int currentScroll = getManager().getHorizontalScroll() ;
	            int strLengthToCursor =  getFont().getAdvance( this.getText().substring(0,getCursorPosition()) );
	            int strLengthFromCursorToEnd = getFont().getAdvance( this.getText().substring(getCursorPosition(), this.getText().length() ) ) ;
	            int currentManagerWidth = getManager().getVisibleWidth();
	            int neededOffset = 0;
	            
	            int cursorMargin = 30;
	            
	            if ( strLengthToCursor >= cursorMargin && currentManagerWidth > cursorMargin * 2 )
	            {
	            	// Cursor near the end of the string
	                if ( strLengthFromCursorToEnd < currentManagerWidth && strLength > currentManagerWidth )
	                {
	                    neededOffset = strLength - currentManagerWidth + 10; // Leave some pixels so the user can see the end of the string and the cursor
	                }
	                else
	                // Cursor close to right edge of the field
	                if ( currentScroll + currentManagerWidth - cursorMargin < strLengthToCursor )
	                {
	                    neededOffset = strLengthToCursor + cursorMargin - currentManagerWidth;
	                }
	                else // Cursor close to left edge of the field
	                if ( currentScroll + cursorMargin > strLengthToCursor )
	                {
	                    neededOffset = strLengthToCursor - cursorMargin ;
	                }                    
	            }
	            
	            
	            if ( neededOffset > 0 )
	            {
	                getManager().setHorizontalScroll(neededOffset);
	            }     
	                          
	            
	        }
	        
	        public void onFocus(int direction)
	        {              
	        	PolishOneLineField.this.ignoreLocalSetCurrentLocation = false;
	            super.onFocus(direction);
	        }
	        
	    };
	    
	    add(this.editField);
    }

	public String getText() 
	{
		return this.editField.getText();
	}
	        
	public void setText(String text) 
	{
	    this.editField.setText(text);
	}

    public void setStyle(Style style) 
    {
           this.editField.setStyle(style);            
    }
    
    public void setPaintPosition(int x, int y ) 
    {
            super.setPosition(x, y);
        	//#if polish.JavaPlatform >= BlackBerry/6.0
            if ( x != this.oldX || y!= this.oldY )
            {
                this.editField.resetRoundRect();
                this.oldX = x;
                this.oldY = y;
            }
            //#endif
    }               
            
    public void focusRemove()
    {
    	// Do nothing
    }
            
    public void focusAdd(boolean draw)
    {    	
    	// Do nothing
    }
    
    //#if polish.JavaPlatform >= BlackBerry/6.0
    public void setCurrentLocation(int x, int y)
    {
        if ( this.ignoreLocalSetCurrentLocation )
        {
            return;
        }
        
        
        super.setCurrentLocation(x,y);
              
    }
   //#endif

    
    protected void onUnfocus()
    {                  
    	this.ignoreLocalSetCurrentLocation = true;               
        setCursorPosition(0);   
        //#if polish.JavaPlatform >= BlackBerry/6.0
        	super.setCurrentLocation(0,0); 
        //#else
        	super.setHorizontalScroll(0);
        //#endif
        super.onUnfocus(); 
    }
    
    public boolean navigationMovement(int dx, int dy, int status, int time) {
        int curPos = getCursorPosition() + dx;
        setCursorPosition(curPos);   
        if ( dy == 0 )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void setCursorPosition(int pos) {
            this.editField.setCursorPosition(pos);
    }

    public int getInsertPositionOffset() {
            return this.editField.getCursorPosition();
    }
    
    public void setChangeListener( FieldChangeListener listener )
    {
        super.setChangeListener(listener);
        this.editField.setChangeListener(listener);
    }


    public int getCursorPosition() {
            return this.editField.getCursorPosition();
    }
    
    public void doLayout( int width, int height) 
    {
            layout(width, height);            
    }        

    //#if polish.JavaPlatform >= BlackBerry/6.0
    public boolean needsNavigationFix() {
            return this.editField.needsNavigationFix() ;               
    }
    //#endif
    
    
    public void updateInputStyle() {
		// TODO Besitzer implement updateInputStyle
		System.out.println("Update input Style!!!!!!!!!!!");
	}

	public void setFilter(TextFilter filter) {
		this.editField.setFilter(filter);		
	}

}