/*
 * Created on 11-Jan-2006 at 19:20:28.
 * 
 * Copyright (c) 2007 Michael Koch / Enough Software
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
package de.enough.polish.xml;

public interface PullParser
{
  /**
   * Return value of getType before first call to next()
   */
  int START_DOCUMENT = 0;

  /**
   * Signal logical end of xml document
   */
  int END_DOCUMENT = 1;

  /**
   * Start tag was just read
   */
  int START_TAG = 2;

  /**
   * End tag was just read
   */
  int END_TAG = 3;

  /**
   * Text was just read
   */
  int TEXT = 4;

  int CDSECT = 5;

  int ENTITY_REF = 6;

  int LEGACY = 999;

  int getType();

  String getText();

  String getName();

  int getAttributeCount();

  String getAttributeName(int index);

  String getAttributeValue(int index);

  String getAttributeValue(String name);

  int next();

}