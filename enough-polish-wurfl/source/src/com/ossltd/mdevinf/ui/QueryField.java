/*
 * Class name : QueryField
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 21-Oct-2005
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
 * This class stores the comparator ("<", "<=", "=", ">=", ">") and the value
 * to be compared.
 * @author Jim McLachlan
 * @version 1.0
 */
public class QueryField
{
    /**
     * An enumeration used to describe the types of fields (boolean, string, int).
     */
    public enum field_type {boolean_field, alpha_field, numeric_field}
    
    /** Defines all the content items for the <code>comparisonField</code>. */
    private static final String[] COMPARISONS = {"n/a",
                                                 "<", 
                                                 "<=", 
                                                 "=", 
                                                 ">=",
                                                 ">"};
    
    /**
     * Defines the content items for <code>comparisonField</code> where only
     * true and false apply.
     */
    private static final String[] BOOLEAN_COMPARISONS = {"n/a", "="};
    
    /** Defines the content items for the true/false query types. */
    private static final String[] FALSE_TRUE = {"false", "true"};
    
    /** A <code>JComboBox</code> containing the comprison operator choices. */
    private JComboBox comparisonField;
    
    /** A <code>JTextField</code> containing the value for comparison. */
    private JComponent valueField;
    
    /**
     * This constructor creates the default comparisonField and empty
     * valueField items.
     * @param fieldType a value showing the kind of data held in the field
     */
    public QueryField(field_type fieldType)
    {
        if (fieldType == field_type.numeric_field)
        {
            comparisonField = new JComboBox(COMPARISONS);
        }
        else
        {
            comparisonField = new JComboBox(BOOLEAN_COMPARISONS);
        }
        
        comparisonField.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                int index = comparisonField.getSelectedIndex();
                if (index == 0)
                {
                    setValueDefault();
                }
            }
        });
        
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
                    if ((((JTextField)valueField).getText().length() > 0) &&
                        (comparisonField.getSelectedIndex() == 0))
                    {
                        comparisonField.setSelectedIndex(3);
                    }
                }

                public void insertUpdate(DocumentEvent e)
                {
                    if ((((JTextField)valueField).getText().length() > 0) &&
                        (comparisonField.getSelectedIndex() == 0))
                    {
                        comparisonField.setSelectedIndex(3);
                    }
                }

                public void removeUpdate(DocumentEvent e)
                {
                    if ((((JTextField)valueField).getText().length() > 0) &&
                        (comparisonField.getSelectedIndex() == 0))
                    {
                        comparisonField.setSelectedIndex(3);
                    }
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
                    if ((((JTextField)valueField).getText().length() > 0) &&
                        (comparisonField.getSelectedIndex() == 0))
                    {
                        comparisonField.setSelectedIndex(1);
                    }
                }

                public void insertUpdate(DocumentEvent e)
                {
                    if ((((JTextField)valueField).getText().length() > 0) &&
                        (comparisonField.getSelectedIndex() == 0))
                    {
                        comparisonField.setSelectedIndex(1);
                    }
                }

                public void removeUpdate(DocumentEvent e)
                {
                    if ((((JTextField)valueField).getText().length() > 0) &&
                        (comparisonField.getSelectedIndex() == 0))
                    {
                        comparisonField.setSelectedIndex(1);
                    }
                }

            });
        }
        else // boolean
        {
            valueField = new JComboBox(FALSE_TRUE);
            ((JComboBox)valueField).addActionListener(new ActionListener(){
                private int previousIndex = 0;
                
                public void actionPerformed(ActionEvent e)
                {
                    if (((JComboBox)valueField).getSelectedIndex() !=
                        previousIndex)
                    {
                        comparisonField.setSelectedIndex(1);
                    }
                }
            });
        }
    }

    /**
     * Accessor method
     * @return <code>comparisonField</code>
     */
    public JComboBox getComparisonField()
    {
        return (comparisonField);
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
     * @return the <code>String</code> representation of the value in this
     * <code>QueryField</code>.
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
     * Sets the default value in this <code>QueryField</code>.
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
    
    /**
     * Sets the value in this <code>QueryField</code>.
     * @param value the new value for this field
     */
    public void setValue(String value)
    {
        if (valueField instanceof JTextField)
        {
            ((JTextField)valueField).setText(value);
        }
        else // Must be the true/false JComboBox
        {
            ((JComboBox)valueField).setSelectedItem(value);
        }
    }
}
