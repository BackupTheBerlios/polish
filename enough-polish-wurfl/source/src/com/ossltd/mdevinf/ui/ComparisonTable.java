/*
 * Class name : ComparisonTable
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 19-Jan-2006
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
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * This class adds the feature of alternate row highlighting to the table.
 * @author Jim McLachlan
 * @version 1.0
 */
public class ComparisonTable extends JTable
{
    /** Constant defining the background colour to use on alternate rows. */
    private static final Color ALTERNATE_COLOUR1 = new Color(0xFF, 0xFF, 0xCC);
    
    /** Constant defining the background colour to use on alternate rows. */
    private static final Color ALTERNATE_COLOUR2 = new Color(0xCC, 0xFF, 0xFF);
    
    /** Constant defining the background colour to use for selected row/col. */
    private static final Color SELECTED_COLOUR = new Color(0xFF, 0xCC, 0xFF);
    
    /**
     * Simply calls the JTable version of this constructor.
     * @param model the model to be used as the basis of this table.
     */
    public ComparisonTable(TableModel model)
    {
        super(model);
        setAutoCreateColumnsFromModel(false);
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        ListSelectionModel lsm = getSelectionModel();
        lsm.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent e)
            {
                repaint();
            }
        });
    }
    
    /**
     * Prepares the renderer for the cell as normal, but then sets the
     * background colour depending upon the row.
     * @param renderer the renderer for the current cell
     * @param row the current row being renderered
     * @param col the current column being rendered
     * @return the prepared rendering component
     */
    public Component prepareRenderer(TableCellRenderer renderer,
                                     int row, int col)
    {
        Component result = super.prepareRenderer(renderer, row, col);

        if (col == 0)
        {
            if (getSelectedRow() == row)
            {
                result.setBackground(Color.white);
            }
        }
        else if ((getSelectedRow() == row) || (getSelectedColumn() == col))
        {
            result.setBackground(SELECTED_COLOUR);
        }
        else if ((row % 2) == 0)
        {
            result.setBackground(ALTERNATE_COLOUR1);
        }
        else
        {
            result.setBackground(ALTERNATE_COLOUR2);
        }
        
        return (result);
    }
}
