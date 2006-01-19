/*
 * Created on Dec 22, 2005 at 12:56:20 PM.
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
package de.enough.utils;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Dec 22, 2005 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class Status {

    private static final int TYPE_RANGE_START = 0;
    public static final int TYPE_OK = 1;
    public static final int TYPE_WARNING = 2;
    public static final int TYPE_INFO = 3;
    public static final int TYPE_ERROR = 4;
    public static final int TYPE_UNKNOWN = 5;
    private static final int TYPE_RANGE_END = 6;
    
    public static final Status OK = new Status(TYPE_OK,"",null);
    public static final Status WARNING = new Status(TYPE_WARNING,"",null);
    public static final Status INFO = new Status(TYPE_INFO,"",null);
    public static final Status ERROR = new Status(TYPE_ERROR,"",null);
    public static final Status UNKNOWN = new Status(TYPE_UNKNOWN,"",null);
    
    private int type;
    private String message;
    private Throwable exception;
    
    public Status() {
        this(TYPE_OK,"",null);
    }
    
    public Status(int type) {
        this(type,"",null);
    }

    public Status(int type, String message) {
        this(type,message,null);
    }
    
    public Status(int type,String message, Throwable exception) {
        if(type <= TYPE_RANGE_START || type >= TYPE_RANGE_END) {
            throw new IllegalArgumentException("type is wrong.type:"+type);
        }
        this.type = type;
        this.message = message;
        this.exception = exception;
    }
    
    /**
     * 
     * @return May be null.
     */
    public Throwable getException() {
        return this.exception;
    }
    
    public void setException(Throwable exception) {
        this.exception = exception;
    }
    
    /**
     * 
     * @return May be null.
     */
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * 
     * @return One of Status.OK,Status.INFO,Status.WARNING,Status.ERROR,
     */
    public int getType() {
        return this.type;
    }
    
    public void setType(int type) {
        this.type = type;
    }


}
