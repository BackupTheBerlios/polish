/*
 * Created on 26-Jan-2006 at 00:30:41.
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
package de.enough.polish.ide.swing;

import java.awt.Component;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Document;

import de.enough.polish.devices.Configuration;
import de.enough.polish.devices.DeviceDatabase;
import de.enough.polish.devices.DeviceTree;
import de.enough.polish.devices.DeviceTreeItem;
import de.enough.polish.devices.Platform;
import de.enough.polish.devices.PolishComponent;
import de.enough.polish.swing.CheckBoxList;

/**
 * <p>Can be used for an easy selection of target devices.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        26-Jan-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DeviceSelectionComponent 
extends JPanel 
implements DocumentListener, PolishComponentSelectionListener 
{
	
	private static final long serialVersionUID = 3962911176522710954L;
	private DeviceDatabase database;
	private DeviceTree deviceTree;
	private PolishComponentCheckBoxList configurationsCheckBoxList;
	private PolishComponentCheckBoxList platformsCheckBoxList;
	private DeviceTreeView deviceTreeView;


	 /**
	  * Creates a new device selector.
	  *  
	  * @param polishHomePath the path to the J2ME Polish installation folder. 
	  *  
	  */
    public DeviceSelectionComponent( String polishHomePath ) {
    	init( polishHomePath );
        initComponents();
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">
    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        this.configurationsCheckBoxList = getConfigurationsCheckBoxList();
        configurationsLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        this.platformsList = getPlatformsCheckBoxList();
        platformsLabel = new javax.swing.JLabel();
        this.devicesListScrollPane = new javax.swing.JScrollPane();
        this.deviceTreeView = getDeviceTreeView();
        devicesLabel = new javax.swing.JLabel();
        this.descriptionLabel = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        this.description = new javax.swing.JTextArea();

//        configurationsList.setModel( getConfigurationsModel() );
//        configurationsList.addListSelectionListener( getConfigurationsModel() );
//        CheckBoxCellRenderer checkBoxCellRenderer = new CheckBoxCellRenderer();
//        configurationsList.setCellRenderer( checkBoxCellRenderer );
        jScrollPane1.setViewportView( getConfigurationsCheckBoxList() );

        configurationsLabel.setText( "Configuration:" );
        //org.openide.awt.Mnemonics.setLocalizedText(configurationsLabel, "Configuration:");

        jScrollPane2.setViewportView(platformsList);

        platformsLabel.setText( "Profiles/Platforms:" );
        //org.openide.awt.Mnemonics.setLocalizedText(platformsLabel, "Profiles/Platforms:");

        devicesListScrollPane.setViewportView(deviceTreeView);

        devicesLabel.setText( "Devices:" );
        //org.openide.awt.Mnemonics.setLocalizedText(devicesLabel, "Devices:");

        descriptionLabel.setText( "Description:" );
        //org.openide.awt.Mnemonics.setLocalizedText(descriptionLabel, "Description:");

        description.setBackground(new java.awt.Color(238, 238, 238));
        description.setColumns(20);
        description.setEditable(false);
        description.setLineWrap(true);
        description.setRows(2);
        description.setToolTipText("Description of the currently selected device");
        description.setWrapStyleWord(true);
        jScrollPane4.setViewportView(description);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(devicesListScrollPane)
                        .add(layout.createSequentialGroup()
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 219, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(configurationsLabel))
                            .add(25, 25, 25)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(platformsLabel)
                                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 219, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(devicesLabel)
                        .add(descriptionLabel)))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(configurationsLabel)
                    .add(platformsLabel))
                .add(4, 4, 4)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(devicesLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(devicesListScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(descriptionLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 51, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>
        

	private DeviceTreeView createDeviceTreeView() {
		DeviceTreeView view = new DeviceTreeView( this.deviceTree );
		view.addTreeSelectionListener( new TreeSelectionListenerImpl()  );
		return view;
	}
    

	private DeviceTreeView getDeviceTreeView() {
		if (this.deviceTreeView == null) {
			this.deviceTreeView = createDeviceTreeView();
		}
		return this.deviceTreeView;
	}



	// Variables declaration - do not modify
    private javax.swing.JLabel configurationsLabel;
    private javax.swing.JTextArea description;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JLabel devicesLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane devicesListScrollPane;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel platformsLabel;
    private CheckBoxList platformsList;
    // End of variables declaration
    
    public void init( String polishHomePath ) {
    	if (polishHomePath == null) {
    		throw new IllegalArgumentException("The installation directory of J2ME Polish has to be given.");
    	}
    	File polishHome = new File( polishHomePath );
    	this.database = new DeviceDatabase( polishHome );
    	this.deviceTree = new DeviceTree( this.database, null, null );
    	
    }
    
    public void insertUpdate(DocumentEvent e) {
    }

    public void removeUpdate(DocumentEvent e) {
    }

    public void changedUpdate(DocumentEvent e) {
    }
    
    private PolishComponentCheckBoxList getConfigurationsCheckBoxList() {    	
    	if (this.configurationsCheckBoxList == null) {
    		this.configurationsCheckBoxList = new PolishComponentCheckBoxList( this.database.getConfigurations() );
    		this.configurationsCheckBoxList.setSelectionListener( this );
    	}
        return this.configurationsCheckBoxList;
    }

    private PolishComponentCheckBoxList getPlatformsCheckBoxList() {    	
    	if (this.platformsCheckBoxList == null) {
    		this.platformsCheckBoxList = new PolishComponentCheckBoxList( this.database.getPlatforms() );
    		this.platformsCheckBoxList.setSelectionListener( this );
    	}
        return this.platformsCheckBoxList;
    }

    class CheckBoxCellRenderer implements ListCellRenderer {

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JCheckBox box = (JCheckBox) value; 
			if (isSelected) {
				box.setBackground(list.getSelectionBackground());
				box.setForeground(list.getSelectionForeground());
			} else {
				box.setBackground(list.getBackground());
	        	box.setForeground(list.getForeground());
	        }
			return box;
		}
    	
    }

    public String[] getSelectedDeviceIdentifiers(){
    	return this.deviceTree.getSelectedDeviceIdentifiers();
    }

    public Map getSelectedDeviceProperties(){
    	return this.deviceTree.getSelectedDeviceProperties();
    }

    public String[] getSelectedDeviceClassPaths(){
    	return this.deviceTree.getSelectedDeviceClassPaths();
    }

    
    public static void main(String[] args) {
		DeviceSelectionComponent c = new DeviceSelectionComponent("/home/enough/J2ME-Polish");
		
		JFrame frame = new JFrame("Hello World");
		frame.getContentPane().add(c);
		frame.pack();
		frame.show();
	}


	public void notifySelection( PolishComponentSelectionEvent event) {
		Object source = event.getSource();
		PolishComponent[] entries = event.getEntries();
		if (source == this.configurationsCheckBoxList) {
			this.deviceTree.rebuild( getConfigurations( entries ), getPlatforms( this.platformsCheckBoxList.getSelectedComponents() ) );
		} else if (source == this.platformsCheckBoxList) {
			this.deviceTree.rebuild( getConfigurations( this.configurationsCheckBoxList.getSelectedComponents() ), getPlatforms( entries ) );
		}
		this.deviceTreeView = createDeviceTreeView();
		this.devicesListScrollPane.setViewportView( this.deviceTreeView );
		Object changedComponent = event.getChangedComponent();
		if (changedComponent instanceof PolishComponentCheckBox) {
			setDescription(  ((PolishComponentCheckBox) changedComponent).getDescription() );
		}
	}
	
	public void setDescription(String text ) {
		this.description.setText( text );
	}



	private Configuration[] getConfigurations(PolishComponent[] entries) {
		if (entries == null) {
			return null;
		}
		Configuration[] configurations = new Configuration[ entries.length ];
		System.arraycopy( entries, 0, configurations, 0, entries.length );
		return configurations;
	}


	private Platform[] getPlatforms(PolishComponent[] entries) {
		if (entries == null) {
			return null;
		}
		Platform[] platforms = new Platform[ entries.length ];
		System.arraycopy( entries, 0, platforms, 0, entries.length );
		return platforms;
	}
	
	class TreeSelectionListenerImpl implements TreeSelectionListener {

		public void valueChanged(TreeSelectionEvent e) {
			Object source = e.getSource();
			if ( source instanceof DeviceTreeCheckBox ) {
				DeviceTreeItem item = ((DeviceTreeCheckBox)source).getDeviceTreeItem();
				DeviceSelectionComponent.this.setDescription( item.getDescription() );
			}
		}
		
	}
    
}