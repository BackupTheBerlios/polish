/*
 * Created on Mar 22, 2006 at 10:53:48 AM.
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
package de.enough.polish.plugin.eclipse.polishEditor.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import de.enough.mepose.core.model.MeposeModel;
import de.enough.polish.Device;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Mar 22, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class DeviceDropdownChooserContributionItem implements IContributionItem {

    public class NewDeviceSelectionListener implements SelectionListener{

        public void widgetSelected(SelectionEvent e) {
            Combo comboTemp = (Combo)e.getSource();
            int selectedIndex = comboTemp.getSelectionIndex();
            Device device = getDeviceAtIndex(selectedIndex);
            DeviceDropdownChooserContributionItem.this.meposeModel.setCurrentDevice(device);
        }

        public void widgetDefaultSelected(SelectionEvent e) {
            // TODO rickyn implement widgetDefaultSelected
        }

    }
    
    public class SupportedDevicesChangeListener implements PropertyChangeListener{

        public void propertyChange(PropertyChangeEvent evt) {
            if( ! MeposeModel.ID_SUPPORTED_DEVICES.equals(evt.getPropertyName())){
                return;
            }
            updateState();
        }
        
    }
    
    private IContributionManager parent;
    private boolean visible;
    private Combo combo;
    protected MeposeModel meposeModel;
    protected Device[] supportedDevices;
    private NewDeviceSelectionListener deviceSelectionListener;
    private SupportedDevicesChangeListener supportedDevicesChangeListener;

    public DeviceDropdownChooserContributionItem(MeposeModel meposeModel) {
        this.meposeModel = meposeModel;
        this.supportedDevicesChangeListener = new SupportedDevicesChangeListener();
        this.meposeModel.addPropertyChangeListener(this.supportedDevicesChangeListener);
        this.visible = true;
    }
    
    public void dispose() {
//        System.out.println("DEBUG:DeviceDropdownChooserContributionItem.dispose(...):enter.");
        if(this.combo != null) {
            this.combo.dispose();
        }
        this.combo = null;
        if(this.meposeModel != null) {
            this.meposeModel.removePropertyChangeListener(this.supportedDevicesChangeListener);
        }
        this.meposeModel = null;
        this.parent = null;
    }

    public void fill(Composite parentToFill) {
        // TODO rickyn implement fill
        
    }

    public void fill(Menu parentToFill, int index) {
        // TODO rickyn implement fill
        
    }

    public void fill(ToolBar parentToFill, int index) {
//        System.out.println("DEBUG:DeviceDropdownChooserContributionItem.fill(...):enter.");
        this.combo = new Combo(parentToFill,SWT.DROP_DOWN|SWT.READ_ONLY);
        this.deviceSelectionListener = new NewDeviceSelectionListener();
        updateState(false);
        this.combo.addSelectionListener(this.deviceSelectionListener);
        this.combo.pack();
        ToolItem seperator = new ToolItem(parentToFill,SWT.SEPARATOR);
        seperator.setWidth(this.combo.getSize().x);
        seperator.setControl(this.combo);
    }

    public void fill(CoolBar parentToFill, int index) {
        // TODO rickyn implement fill
        
    }

    public String getId() {
        return getClass().getName();
    }

    public boolean isEnabled() {
        return true;
    }

    public boolean isDirty() {
        return false;
    }

    public boolean isDynamic() {
        return true;
    }

    public boolean isGroupMarker() {
        return false;
    }

    public boolean isSeparator() {
        return false;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void saveWidgetState() {
        // TODO rickyn implement saveWidgetState
    }

    public void setParent(IContributionManager parent) {
        this.parent = parent;
    }

    public void setVisible(boolean visible) {
//        System.out.println("DEBUG:DeviceDropdownChooserContributionItem.setVisible(...):visible:"+visible);
        this.visible = visible;
    }

    public void update() {
//        System.out.println("DEBUG:DeviceDropdownChooserContributionItem.update(...):update without parameter.enter.");
    }

    public void update(String id) {
        // TODO rickyn implement update
//        System.out.println("DEBUG:DeviceDropdownChooserContributionItem.update(...):id:"+id);
    }
    
    public void setMeposeModel(MeposeModel meposeModel) {
        this.meposeModel = meposeModel;
        updateState();
    }

    protected void updateState() {
        updateState(true);
    }
    
    protected void updateState(boolean updateParent) {
        if(this.combo == null ||  this.combo.isDisposed()) {
            // I know this is silly but is really happens that this method is called before the actual control is created.
            // Sad but true :(
            return;
        }
        this.combo.removeAll();
        this.supportedDevices = this.meposeModel.getSupportedDevices();
        boolean deviceSelected = false;
        Device currentDevice = this.meposeModel.getCurrentDevice();
        String currentDeviceName;
        if(currentDevice != null) {
            currentDeviceName = currentDevice.getIdentifier();
        }
        else {
            currentDeviceName = "";
        }
        for (int i = 0; i < this.supportedDevices.length; i++) {
            String identifier = this.supportedDevices[i].getIdentifier();
            this.combo.add(identifier);
            if(identifier.equals(currentDeviceName)) {
                this.combo.select(i);
                deviceSelected = true;
            }
        }
        if( ! deviceSelected) {
            if(this.supportedDevices.length >= 1) {
                this.combo.select(0);
            }
        }
        if(updateParent) {
            // This will relayout all children of the parent. This will also call
            // fill so do not call this method with parameter true in the fill
            // methods.
            this.parent.update(true);
        }
    }
    
    protected Device getDeviceAtIndex(int index) {
        return this.supportedDevices[index];
    }
    
    protected void setSupportedDevices(Device[] devices) {
        this.supportedDevices = devices;
    }

}