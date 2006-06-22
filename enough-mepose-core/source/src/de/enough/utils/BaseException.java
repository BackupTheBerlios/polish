/*
 * Created on Jun 21, 2006 at 12:04:19 PM.
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
 * Modeled after http://errconv.sourceforge.net/definition.html. Not yet needed.
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Jun 21, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class BaseException {

    // #########################################################################
    // Responses
    
    /** Extremely bad error. Try to stop everything before it worsens. */
    public static final int RESPONSE_PANIC = 1;
    
    /** Abort process nicely. */
    public static final int RESPONSE_ABORT = 2;
    
    /** Retry action. */
    public static final int RESPONSE_RETRY = 3;
    
    /** Fall over to alternative resource otherwise retry. */
    public static final int RESPONSE_FALLOVER = 4;
    
    /** Inform the user/administrator of the event but continue. */
    public static final int RESPONSE_ALERT = 5;
    
    /** Ignore this error. */
    public static final int RESPONSE_IGNORE = 6;
    
    
    // #########################################################################
    // Error levels.
    
    /** System is unusable. */
    public static final int LEVEL_EMERGENCY = 1;
    
    /** Action must be taken immediately. */
    public static final int LEVEL_ALERT = 2;
    
    /** Critical conditions. */
    public static final int LEVEL_CRIT = 3;
    
    /** Error conditions. */
    public static final int LEVEL_ERROR = 4;
    
    /** Warning conditions. */
    public static final int LEVEL_WARNING = 5;
    
    /** Normal but significant condition. */
    public static final int LEVEL_NOTICE = 6;
    
    /** Informational. */
    public static final int LEVEL_INFO = 7;
    
    /** Debug-level messages. */
    public static final int LEVEL_DEBUG = 8;
}
