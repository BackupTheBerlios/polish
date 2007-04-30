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

import de.enough.polish.browser.Browser;
import de.enough.polish.browser.TagHandler;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.ImageItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.HashMap;
import de.enough.polish.xml.SimplePullParser;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;

//#if polish.cldc1.0
//# import de.enough.polish.util.TextUtil;
//#endif

public class HtmlTagHandler
  extends TagHandler
  implements ItemCommandListener
{
  public static final String TAG_TITLE = "title";
  public static final String TAG_STYLE = "style";
  public static final String TAG_BR = "br";
  public static final String TAG_P = "p";
  public static final String TAG_IMG = "img";
  public static final String TAG_DIV = "div";
  public static final String TAG_SPAN = "span";
  public static final String TAG_A = "a";
  public static final String TAG_B = "b";
  public static final String TAG_STRONG = "strong";
  public static final String TAG_I = "i";
  public static final String TAG_EM = "em";
  public static final String TAG_FORM = "form";
  public static final String TAG_INPUT = "input";
  public static final String TAG_SELECT = "select";
  public static final String TAG_OPTION = "option";
  
  public static final String INPUT_TYPE = "type";
  public static final String INPUT_NAME = "name";
  public static final String INPUT_VALUE = "value";

  public static final String INPUTTYPE_TEXT = "text";
  public static final String INPUTTYPE_SUBMIT = "submit";
  
  public static final String KEY_FORM = "polish_form";
  
  public static final Command CMD_LINK = new Command("Go To", Command.ITEM, 1);
  public static final Command CMD_SUBMIT = new Command("Submit", Command.ITEM, 2);
  public static final Command CMD_BACK = new Command("Back", Command.BACK, 10);
  
  private HtmlForm currentForm;
  private HtmlSelect currentSelect;

  protected HtmlBrowser browser;

  public boolean textBold;
  public boolean textItalic;
  
  public void register(Browser browser)
  {
    this.browser = (HtmlBrowser) browser;
    this.textBold = false;
    this.textItalic = false;
    
    browser.addTagHandler(TAG_TITLE, this);
    browser.addTagHandler(TAG_STYLE, this);
    
    browser.addTagHandler(TAG_BR, this);
    browser.addTagHandler(TAG_P, this);
    browser.addTagHandler(TAG_IMG, this);
    browser.addTagHandler(TAG_DIV, this);
    browser.addTagHandler(TAG_SPAN, this);
    browser.addTagHandler(TAG_A, this);
    browser.addTagHandler(TAG_B, this);
    browser.addTagHandler(TAG_STRONG, this);
    browser.addTagHandler(TAG_I, this);
    browser.addTagHandler(TAG_EM, this);
    browser.addTagHandler(TAG_FORM, this);
    browser.addTagHandler(TAG_INPUT, this);
    browser.addTagHandler(TAG_SELECT, this);
    browser.addTagHandler(TAG_OPTION, this);
  }

  /* (non-Javadoc)
   * @see de.enough.polish.browser.TagHandler#handleTag(de.enough.polish.ui.Container, de.enough.polish.xml.PullParser, java.lang.String, boolean, de.enough.polish.util.HashMap)
   */
  public boolean handleTag(Container parentItem, SimplePullParser parser, String tagName, boolean opening, HashMap attributeMap)
  {
	  //#debug
	  System.out.println("checking tag " + tagName );
	  tagName = tagName.toLowerCase();

	  if (TAG_SELECT.equals(tagName)) {
    	  if (opening) {
    		  if (this.currentSelect != null) {
    			  //debug error
    			  System.out.println("Error in HTML-Code. You cannot open a <select>-tag inside another <select>-tag.");

    			  this.browser.add(this.currentSelect.getChoiceGroup());
    			  this.currentSelect = null;
    		  }

    		  String name = parser.getAttributeValue("name");
    		  this.currentSelect = new HtmlSelect(name);
    	  }
    	  else {
    		  if (this.currentSelect != null) {
    			  this.browser.add(this.currentSelect.getChoiceGroup());
    			  this.currentSelect = null;
    		  }
    		  //#mdebug error
    		  else {
    			  System.out.println("Error in HTML-Code. You cannot close a <select>-tag without opening one.");
    		  }
    		  //#enddebug
    	  }
    	  return true;
      }
      else if (TAG_OPTION.equals(tagName)) {
    	  if (this.currentSelect != null && opening) {
    		  // TODO: handle "seclected" attribute.
    		  String value = parser.getAttributeValue("value");
    		  parser.next();
    		  String name = parser.getText();

    		  if (value == null) {
    			  value = name;
    		  }

    		  this.currentSelect.addOption(name, value);
    	  }
    	  return true;
      }

    if (opening)
    {    
      if (TAG_TITLE.equals(tagName))
      {
        // Hack to read title.
        parser.next();
        return true;
      }
      else if (TAG_STYLE.equals(tagName))
      {
        // Hack to read style content.
        parser.next();
        return true;
      }
      else if (TAG_A.equals(tagName))
      {
        String href = (String) attributeMap.get("href");
        parser.next();
        StringItem linkItem;
        if (href != null)
        {
	     	  //#style browserLink
  	      linkItem = new StringItem(null, parser.getText());
    	    linkItem.setDefaultCommand(CMD_LINK);
    	    linkItem.setItemCommandListener( this );
      	  linkItem.setAttribute("href", href != null ? href : "");
      	  addCommands(TAG_A, linkItem);

// TODO
//          if (!this.firstInteractiveElementAdded
//              && this.browser.getScreen() != null)
//          {
//            UiAccess.focus(this.browser.getScreen(), this.browser);
//            this.firstInteractiveElementAdded = true;
//          }
        }
        else
        {
        	//#style browserText
        	linkItem = new StringItem(null, parser.getText());
        }
        this.browser.add(linkItem);
        return true;
      }
      else if (TAG_BR.equals(tagName))
      {
        // TODO: Can we do this without adding a dummy StringItem?
        StringItem stringItem = new StringItem(null, null);
        stringItem.setLayout(Item.LAYOUT_NEWLINE_BEFORE);
        this.browser.add(stringItem);
        return true;
      }
      else if (TAG_P.equals(tagName))
      {
        StringItem stringItem = new StringItem(null, null);
        stringItem.setLayout(Item.LAYOUT_NEWLINE_BEFORE);
        this.browser.add(stringItem);
        return true;
      }
      else if (TAG_IMG.equals(tagName))
      {
        String src = (String) attributeMap.get("src");
        String url = this.browser.makeAbsoluteURL(src);
        Image image = this.browser.loadImage(url);
        this.browser.add(new ImageItem(null, image, Item.LAYOUT_DEFAULT, ""));
        return true;
      }
      else if (TAG_INPUT.equals(tagName))
      {
        if (this.currentForm != null)
        {
          String type = (String) attributeMap.get(INPUT_TYPE);
          
          //#if polish.cldc1.1
          if (INPUTTYPE_TEXT.equalsIgnoreCase(type))
          //#else
          //# if (TextUtil.equalsIgnoreCase(INPUTTYPE_TEXT, type))
          //#endif
          {
            String name = (String) attributeMap.get(INPUT_NAME);
            String value = (String) attributeMap.get(INPUT_VALUE);
            //#style browserInput
            TextField textField = new TextField(null, value, 100, TextField.ANY);
            this.browser.add(textField);
            
            this.currentForm.addItem(textField);
            textField.setAttribute(KEY_FORM, this.currentForm);
            textField.setAttribute("name", name);
            textField.setAttribute("value", value);
          }
          //#if polish.cldc1.1
          else if (INPUTTYPE_SUBMIT.equalsIgnoreCase(type))
          //#else
          //# if (TextUtil.equalsIgnoreCase(INPUTTYPE_SUBMIT, type))
          //#endif
          {
            String name = (String) attributeMap.get(INPUT_NAME);
            String value = (String) attributeMap.get(INPUT_VALUE);
            
            //#style browserLink
            StringItem buttonItem = new StringItem(null, value);
            buttonItem.setDefaultCommand(CMD_SUBMIT);
            buttonItem.setItemCommandListener(this);
            addCommands(TAG_INPUT, INPUT_TYPE, INPUTTYPE_SUBMIT, buttonItem);
            this.browser.add(buttonItem);
            
            this.currentForm.addItem(buttonItem);
            buttonItem.setAttribute(KEY_FORM, this.currentForm);
            buttonItem.setAttribute("type", "submit");
            buttonItem.setAttribute("name", name);
            buttonItem.setAttribute("value", value);
          }
          //#if polish.debug.debug
          else
          {
            //#debug
            System.out.println("unhandled html form input type: " + type);
          }
          //#endif
        }

//        if (!this.firstInteractiveElementAdded
//            && this.browser.getScreen() != null)
//        {
//          UiAccess.focus(this.browser.getScreen(), this.browser);
//          this.firstInteractiveElementAdded = true;
//        }

        return true;
      }
    }
    
    if (TAG_B.equals(tagName)
      || TAG_STRONG.equals(tagName))
    {
      this.textBold = opening;
      return true;
    }
    else if (TAG_I.equals(tagName)
      || TAG_EM.equals(tagName))
    {
      this.textItalic = opening;
      return true;
    }
    else if (TAG_FORM.equals(tagName))
    {
      if (opening)
      {
        String target = (String) attributeMap.get("action");
        String method = (String) attributeMap.get("method");
        
        if (method == null)
        {
          method = "GET";
        }
        
        this.currentForm = new HtmlForm(target, method.toUpperCase().equals("POST")
                                        ? HtmlForm.POST : HtmlForm.GET);
      }
      else
      {
        this.currentForm = null;
      }
      
      return true;
    }

    return false;
  }

  /* (non-Javadoc)
   * @see de.enough.polish.browser.TagHandler#handleCommand(javax.microedition.lcdui.Command)
   */
  public boolean handleCommand(Command command)
  {
    if (command == CMD_LINK)
    {
      handleLinkCommand();
      return true;
    }
    else if (command == CMD_SUBMIT)
    {
      handleSubmitCommand();
      return true;
    }
    else if (command == CMD_BACK)
    {
      handleBackCommand();
    }
    
    return false;
  }

  protected void handleBackCommand()
  {
    this.browser.goBack();
  }
  
  /**
   * Creates a Form GET method URL for the specified browser.
   * 
   * @param browser the browser
   * @return the GET URL or null when the browser's current item is not a Submit button
   */
  public static String createGetSubmitCall(Browser browser)
  {
    Item submitItem = browser.getFocusedItem();
    HtmlForm form = (HtmlForm) submitItem.getAttribute(KEY_FORM);

	  if (form == null)
	  {
	  	return null;
	  }

    StringBuffer sb = new StringBuffer(form.getTarget());
    Item[] items = form.getItems();
    int numItems = items.length;
    char separatorChar = '?';
    
    for (int i = 0; i < numItems; i++)
    {
      Item item = items[i];
      
      if ("submit".equals(item.getAttribute("type"))
          && item != submitItem)
      {
        continue;
      }
      
      String name = (String) item.getAttribute("name");
      String value = (String) item.getAttribute("value");
      
      if (item instanceof TextField)
      {
        TextField textField = (TextField) item;
        value = textField.getText();
      }
      
      sb.append(separatorChar);
      sb.append(name);
      sb.append('=');
      sb.append(value);
      separatorChar = '&';
    }
    return sb.toString();
  }

  protected void handleSubmitCommand()
  {
	  String url = createGetSubmitCall(this.browser);
    
    // TODO: Implement form submit for POST method.
    
    this.browser.go(url);
  }

  protected void handleLinkCommand()
  {
    Item linkItem = this.browser.getFocusedItem();
    String href = (String) linkItem.getAttribute("href");
    this.browser.go(this.browser.makeAbsoluteURL(href));
  }
  
  /**
   * Handles item commands (implements ItemCommandListener).
   * 
   * @param command the command
   * @param item the item from which the command originates
   */
	public void commandAction(Command command, Item item)
	{
		handleCommand(command);
	}
}
