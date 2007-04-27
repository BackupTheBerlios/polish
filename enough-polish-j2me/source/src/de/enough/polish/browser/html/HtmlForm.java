//#condition polish.usePolishGui

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
package de.enough.polish.browser.html;

import de.enough.polish.ui.Item;
import de.enough.polish.util.ArrayList;

public class HtmlForm
{
  public static final int GET = 1;
  
  public static final int POST = 2;

  private String targetUrl;
  private int method;
  
  //if polish.java5
  //private ArrayList<Item> formItems = new ArrayList<Item>();
  //else
  private ArrayList formItems = new ArrayList();
  //endif

  
  public HtmlForm(String targetUrl, int method)
  {
    this.targetUrl = targetUrl;
    this.method = method;
  }

  public String getTarget()
  {
    return this.targetUrl;
  }
  
  public int getMethod()
  {
    return this.method;
  }
  
  public void addItem(Item item)
  {
    this.formItems.add(item);
  }
  
  public Item[] getItems()
  {
    //if polish.java5
    //return this.formItems.toArray(new Item[this.formItems.size()]);
    //else
    return (Item[]) this.formItems.toArray(new Item[this.formItems.size()]);
    //endif
  }
}
