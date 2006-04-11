/*
 * Class name : EditField
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 08-Nov-2005
 * 
 * Copyright (c) 2005-2006, Jim McLachlan (jim@oss-ltd.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.ossltd.mdevinf.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * This class stores the type and value of a capability to be edited.
 * @author Jim McLachlan
 * @version 1.0
 */
public class EditField
{
    /**
     * An enumeration used to describe the types of fields (boolean, string,
     * int).
     */
    public enum field_type {boolean_field, alpha_field, numeric_field}
    
    /**
     * Colour used for the value field background when an update has
     * occurred.
     */
    public static final Color updatedColour = new Color(0xFF, 0xBB, 0x00);

    /** Defines the content items for the true/false query types. */
    private static final String[] FALSE_TRUE = {"false", "true"};
    
    /** The name of the group (sub-tab) in which this capability appears. */
    private String group;
    
    /** A <code>JTextField</code> containing the value for comparison. */
    private JComponent valueField;
    
    /** A flag indicating whether this value has been updated. */
    private boolean updated;

    /** The initial value (after the field is populated) of a text field. */
    private String startingValue;
    
    /** The initial value (after the combo is populated) of a boolean field. */
    private int startingIndex;
    
    /** The initial value (after a field is populated) of the background. */
    private Color startingColour;
    
    /**
     * This constructor creates the default empty valueField items.
     * @param group the name of the group in which the capability appears
     * @param fieldType a value showing the kind of data held in the field
     */
    public EditField(String group, field_type fieldType)
    {
        this.group = group;
        reset();
        
        if (fieldType == field_type.numeric_field)
        {
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(0);
            nf.setGroupingUsed(false);
            
            valueField = new JFormattedTextField(nf);
            JTextField sizeSource = new JTextField(6);
            valueField.setPreferredSize(sizeSource.getPreferredSize());

            ((JTextField)valueField).getDocument().
                                     addDocumentListener(new DocumentListener(){
                public void changedUpdate(DocumentEvent e)
                {
                    String curVal = ((JTextField)valueField).getText();
                    
                    if (startingValue == null)
                    {
                        startingColour = ((JTextField)valueField).
                                                                getBackground();
                    }
                    else
                    {
                        if (startingValue.equals(curVal))
                        {
                            ((JTextField)valueField).
                                                  setBackground(startingColour);
                            updated = false;
                        }
                        else // Value has been changed from it's initial content
                        {
                            ((JTextField)valueField).
                                                   setBackground(updatedColour);
                            updated = true;
                        }
                    }
                }

                public void insertUpdate(DocumentEvent e)
                {
                    changedUpdate(e);
                }

                public void removeUpdate(DocumentEvent e)
                {
                    changedUpdate(e);
                }

            });
        }
        else if (fieldType == field_type.alpha_field)
        {
            valueField = new JTextField(6);
            ((JTextField)valueField).getDocument().
                                     addDocumentListener(new DocumentListener(){
                public void changedUpdate(DocumentEvent e)
                {
                    String curVal = ((JTextField)valueField).getText();

                    if (startingValue == null)
                    {
                        startingColour = ((JTextField)valueField).
                                                                getBackground();
                    }
                    else
                    {
                        if (startingValue.equals(curVal))
                        {
                            ((JTextField)valueField).
                                                  setBackground(startingColour);
                            updated = false;
                        }
                        else // Value has been changed from it's initial content
                        {
                            ((JTextField)valueField).
                                                   setBackground(updatedColour);
                            updated = true;
                        }
                    }
                }

                public void insertUpdate(DocumentEvent e)
                {
                    changedUpdate(e);
                }

                public void removeUpdate(DocumentEvent e)
                {
                    changedUpdate(e);
                }
            });
        }
        else // boolean
        {
            valueField = new JComboBox(FALSE_TRUE);
            ((JComboBox)valueField).addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e)
                {
                    int curIndex = ((JComboBox)valueField).getSelectedIndex();

                    if (startingIndex == -1)
                    {
                        startingColour = ((JComboBox)valueField).
                                                                getBackground();
                    }
                    else
                    {
                        if (startingIndex == curIndex)
                        {
                            ((JComboBox)valueField).
                                                  setBackground(startingColour);
                            updated = false;
                        }
                        else // Value has been reverted to it's initial content
                        {
                            ((JComboBox)valueField).
                                                   setBackground(updatedColour);
                            updated = true;
                        }
                    }
                }
            });
        }
    }

    /**
     * Sets the edit field back to it's default state.
     */
    public void reset()
    {
        updated = false;
        startingValue = null;
        startingIndex = -1;
        startingColour = null;
    }

    /**
     * Accessor
     * @return the name of the group in which this capability appears
     */
    public String getGroup()
    {
        return (group);
    }
    
    /**
     * Accessor method
     * @return <code>valueField</code>
     */
    public JComponent getValueField()
    {
        return (valueField);
    }
    
    /**
     * Accessor
     * @return the flag indicating whether this capability has been updated.
     */
    public boolean isUpdated()
    {
        return (updated);
    }
    
    /**
     * Accessor
     * @return the <code>String</code> representation of the value in this
     * <code>EditField</code>.
     */
    public String getValueAsText()
    {
        String result = null;
        
        if (valueField instanceof JTextField)
        {
            result = ((JTextField)valueField).getText();
        }
        else // Must be the true/false JComboBox
        {
            result = (String)((JComboBox)valueField).getSelectedItem();
        }
        
        return (result);
    }
    
    /**
     * Mutator to set the initial value
     * @param textContent the initial content of the field
     */
    public void setValue(String textContent)
    {
        if (valueField instanceof JTextField)
        {
            ((JTextField)valueField).setText(textContent);
            startingValue = textContent;
        }
        else // Must be a boolean
        {
            ((JComboBox)valueField).setSelectedItem(textContent);
            startingIndex = ((JComboBox)valueField).getSelectedIndex();
        }
        startingColour = valueField.getBackground();
    }
    
    /**
     * Sets the default value in this <code>EditField</code>.
     * <p>A an empty <code>String</code> for text fields and index 0 for
     * combo boxes.
     */
    public void setValueDefault()
    {
        if (valueField instanceof JTextField)
        {
            ((JTextField)valueField).setText("");
        }
        else // Must be the true/false JComboBox
        {
            ((JComboBox)valueField).setSelectedIndex(0);
        }
    }
}
