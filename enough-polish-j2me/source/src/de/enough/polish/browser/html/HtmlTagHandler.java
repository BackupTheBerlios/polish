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
import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.ImageItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.HashMap;
import de.enough.polish.util.Locale;
import de.enough.polish.util.TextUtil;
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
	/** title tag */
  public static final String TAG_TITLE = "title";
	/** style tag */
  public static final String TAG_STYLE = "style";
	/** br tag */
  public static final String TAG_BR = "br";
	/** p tag */
  public static final String TAG_P = "p";
	/** img tag */
  public static final String TAG_IMG = "img";
	/** div tag */
  public static final String TAG_DIV = "div";
	/** span tag */
  public static final String TAG_SPAN = "span";
	/** a tag */
  public static final String TAG_A = "a";
	/** b tag */
  public static final String TAG_B = "b";
	/** strong tag */
  public static final String TAG_STRONG = "strong";
	/** i tag */
  public static final String TAG_I = "i";
	/** em tag */
  public static final String TAG_EM = "em";
	/** form tag */
  public static final String TAG_FORM = "form";
	/** input tag */
  public static final String TAG_INPUT = "input";
	/** select tag */
  public static final String TAG_SELECT = "select";
	/** option tag */
  public static final String TAG_OPTION = "option";
	/** script tag */
  public static final String TAG_SCRIPT = "script";
  
	/** type attribute */
  public static final String INPUT_TYPE = "type";
	/** name attribute */
  public static final String INPUT_NAME = "name";
	/** value attribute */
  public static final String INPUT_VALUE = "value";

	/** text type-value */
  public static final String INPUTTYPE_TEXT = "text";
	/** submit type-value */
  public static final String INPUTTYPE_SUBMIT = "submit";
  
	/** href attribute */
  public static final String ATTR_HREF = "href";
	/** form attribute */
  public static final String ATTR_FORM = "polish_form";
	/** type attribute */
  public static final String ATTR_TYPE = "type";
	/** value attribute */
  public static final String ATTR_VALUE = "value";
	/** name attribute */
  public static final String ATTR_NAME = "name";

  /** default link command */
	//#ifdef polish.i18n.useDynamicTranslations
		public static Command CMD_LINK = new Command( Locale.get("polish.command.followlink"), Command.OK, 2 );
	//#elifdef polish.command.followlink:defined
		//#= public static final Command CMD_LINK = new Command("${polish.command.followlink}", Command.OK, 1 );
	//#else
		//# public static final Command CMD_LINK = new Command("Go", Command.OK, 1);
	//#endif
	/** default submit command */
	//#ifdef polish.i18n.useDynamicTranslations
		public static Command CMD_SUBMIT = new Command( Locale.get("polish.command.submit"), Command.ITEM, 2 );
	//#elifdef polish.command.submit:defined
		//#= public static final Command CMD_SUBMIT = new Command("${polish.command.submit}", Command.ITEM, 2 );
	//#else
		//# public static final Command CMD_SUBMIT = new Command("Submit", Command.ITEM, 2);
	//#endif
	/** default back command */
	//#ifdef polish.i18n.useDynamicTranslations
		public static Command CMD_BACK = new Command( Locale.get("polish.command.back"), Command.BACK, 10 );
	//#elifdef polish.command.back:defined
		//#= public static final Command CMD_BACK = new Command("${polish.command.back}", Command.BACK, 10 );
	//#else
		//# public static final Command CMD_BACK = new Command("Back", Command.BACK, 10);
	//#endif
  
  private HtmlForm currentForm;
  private HtmlSelect currentSelect;

  protected HtmlBrowser browser;

  /* next text should be added in bold font style */
  public boolean textBold;
  /* next text should be added in italic font style */
  public boolean textItalic;
  /* style for the forthcoming text */
  public Style textStyle;
  
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
    browser.addTagHandler(TAG_SCRIPT, this);
  }

  /* (non-Javadoc)
   * @see de.enough.polish.browser.TagHandler#handleTag(de.enough.polish.ui.Container, de.enough.polish.xml.PullParser, java.lang.String, boolean, de.enough.polish.util.HashMap, de.enough.polish.ui.Style)
   */
  public boolean handleTag(Container parentItem, SimplePullParser parser, String tagName, boolean opening, HashMap attributeMap, Style style)
  {
	  //#debug
	  System.out.println("checking tag " + tagName );
	  tagName = tagName.toLowerCase();
	  if (TextUtil.equalsIgnoreCase(TAG_DIV, tagName) || TextUtil.equalsIgnoreCase(TAG_SPAN, tagName)) {
		  if (opening) {
			  this.textStyle = style;
 		  } else {
			  this.textStyle = null;
		  }
	  } else if (TAG_SELECT.equals(tagName)) {
    	  if (opening) {
    		  if (this.currentSelect != null) {
    			  //#debug error
    			  System.out.println("Error in HTML-Code: You cannot open a <select>-tag inside another <select>-tag.");

    			  ChoiceGroup choiceGroup = this.currentSelect.getChoiceGroup();
    			  this.browser.add(choiceGroup);
    			  if (this.currentForm == null) {
    				  //#debug error
    				  System.out.println("Error in HTML-Code: no <form> for <select> element found!");
    			  } else {
    				  this.currentForm.addItem(choiceGroup);
    			  }
    			  this.currentSelect = null;
    		  }

    		  String name = parser.getAttributeValue(ATTR_NAME);
    		  this.currentSelect = new HtmlSelect(name, style);
    	  } else { // tag is closed
    		  if (this.currentSelect != null) {
    			  ChoiceGroup choiceGroup = this.currentSelect.getChoiceGroup();
    			  this.browser.add(choiceGroup);
    			  if (this.currentForm == null) {
    				  //#debug error
    				  System.out.println("Error in HTML-Code: no <form> for <select> element found!");
    			  } else {
    				  this.currentForm.addItem(choiceGroup);
    			  }
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
    		  // TODO: handle "selected" attribute.
    		  String value = parser.getAttributeValue(ATTR_VALUE);
    		  String selected = parser.getAttributeValue("selected");
    		  parser.next();
    		  String name = parser.getText();

    		  if (value == null) {
    			  value = name;
    		  }

    		  this.currentSelect.addOption(name, value, selected != null, style);
    	  }
    	  return true;
      }

    if (opening)
    {    
      if (TAG_TITLE.equals(tagName))
      {
        // Hack to read title.
        parser.next();
        String name = parser.getText();
        Screen myScreen = this.browser.getScreen();
        if (name != null && myScreen != null) {
        	myScreen.setTitle( name );
        }
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
        String href = (String) attributeMap.get(ATTR_HREF);
        parser.next();
        Item linkItem;
        if (href != null)
        {
        	String anchorText = parser.getText();
        	// hack for image links:
        	if ("".equals(anchorText) && TAG_IMG.equals(parser.getName())) {
        		// this is an image link:
        		attributeMap.clear();
        		for (int i = 0; i < parser.getAttributeCount(); i++)
        	    {
        	      String attributeName = parser.getAttributeName(i);
        	      String attributeValue = parser.getAttributeValue(i);
        	      attributeMap.put(attributeName, attributeValue);
        	    }
                String src = (String) attributeMap.get("src");
                String url = this.browser.makeAbsoluteURL(src);
                Image image = this.browser.loadImage(url);
                //#style browserLink
                linkItem = new ImageItem(null, image, 0, (String) attributeMap.get("alt") );
        		//this.browser.loadImageLater( url, (ImageItem) linkItem );
        		
        	} else {
        		//#style browserLink
        		linkItem = new StringItem(null, anchorText);
        	}
    	    linkItem.setDefaultCommand(CMD_LINK);
    	    linkItem.setItemCommandListener( this );
    	    linkItem.setAttribute(ATTR_HREF, href != null ? href : "");
    	    addCommands(TAG_A, linkItem);
        }
        else
        {
        	//#style browserText
        	linkItem = new StringItem(null, parser.getText());
        }
		if (style != null) {
			linkItem.setStyle(style);
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
        if (opening) {
        	this.textStyle = style;
        }
        return true;
      }
      else if (TAG_IMG.equals(tagName))
      {
        String src = (String) attributeMap.get("src");
        String url = this.browser.makeAbsoluteURL(src);
        Image image = this.browser.loadImage(url);
        if (image != null) {
        	ImageItem item = new ImageItem(null, image, Item.LAYOUT_DEFAULT, "");
    		if (style != null) {
    			item.setStyle(style);
    		}

        	this.browser.add(item);
        }
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

            if (value == null) {
            	value = name;
            }

            //#style browserInput
            TextField textField = new TextField(null, value, 100, TextField.ANY);
            if (style != null) {
            	textField.setStyle(style);
            }
            this.browser.add(textField);
            
            this.currentForm.addItem(textField);
            textField.setAttribute(ATTR_FORM, this.currentForm);

            if (name != null) {
            	textField.setAttribute(ATTR_NAME, name);
            	textField.setAttribute(ATTR_VALUE, value);
            }
          }
          //#if polish.cldc1.1
          else if (INPUTTYPE_SUBMIT.equalsIgnoreCase(type))
          //#else
          //# if (TextUtil.equalsIgnoreCase(INPUTTYPE_SUBMIT, type))
          //#endif
          {
            String name = (String) attributeMap.get(INPUT_NAME);
            String value = (String) attributeMap.get(INPUT_VALUE);

            if (value == null) {
            	value = name;
            }

            //#style browserLink
            StringItem buttonItem = new StringItem(null, value);
            if (style != null) {
            	buttonItem.setStyle(style);
            }
            buttonItem.setDefaultCommand(CMD_SUBMIT);
            buttonItem.setItemCommandListener(this);
            addCommands(TAG_INPUT, INPUT_TYPE, INPUTTYPE_SUBMIT, buttonItem);
            this.browser.add(buttonItem);
            
            this.currentForm.addItem(buttonItem);
            buttonItem.setAttribute(ATTR_FORM, this.currentForm);
            buttonItem.setAttribute(ATTR_TYPE, "submit");

            if (name != null) {
            	buttonItem.setAttribute(ATTR_NAME, name);
            	buttonItem.setAttribute(ATTR_VALUE, value);
            }
          }
          //#if polish.debug.debug
          else
          {
            //#debug
            System.out.println("unhandled html form input type: " + type);
          }
          //#endif
        }

        return true;
      }
      else if (TAG_SCRIPT.equals(tagName)) {
    	  // Consume javascript code.
    	  parser.next();
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
    HtmlForm form = (HtmlForm) submitItem.getAttribute(ATTR_FORM);

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
      
      if ("submit".equals(item.getAttribute(ATTR_TYPE))
          && item != submitItem)
      {
        continue;
      }
      
      String name = (String) item.getAttribute(ATTR_NAME);
      String value = (String) item.getAttribute(ATTR_VALUE);
      
      if (item instanceof TextField)
      {
        TextField textField = (TextField) item;
        value = textField.getText();
      }
      else if (item instanceof ChoiceGroup) {
    	  ChoiceGroup choiceGroup = (ChoiceGroup) item;
    	  HtmlSelect htmlSelect = (HtmlSelect) choiceGroup.getAttribute(HtmlSelect.SELECT);
    	  value = htmlSelect.getValue(choiceGroup.getSelectedIndex());
      }
      
      sb.append(separatorChar);
      sb.append(name);
      sb.append('=');
      sb.append(TextUtil.encodeUrl(value));
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
    String href = (String) linkItem.getAttribute(ATTR_HREF);
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
