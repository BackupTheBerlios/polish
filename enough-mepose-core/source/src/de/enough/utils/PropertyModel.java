/*
 * Created on Dec 22, 2005 at 4:43:08 PM.
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This class is a generic model to associate a property (a string at the moment)
 * to a value. It maintains the following:
 * - A property-value pair. The property may have a null value.
 * - A default value for a property. The default value may be null.
 * - A Status object for a property. This is never null for a property.
 * - A Status object for the whole model. This is never null.
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Dec 22, 2005 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class PropertyModel {

    public static Logger logger = Logger.getLogger(PropertyModel.class);
    
    private Map propertyToValue;
    private Map propertyToDefaultValue;
    private Map propertyToState;
    
    private Status modelStatus;

    protected String[] propertiesWithAtLeastOKStatus;
    protected String[] propertiesWithAtLeastWARNINGStatus;

    private boolean optionGetDefaultTransparently;
    private boolean optionDefaultStatusIsOK;

    private MutableBoolean dummyMutableBoolean = new MutableBoolean(true);

    private List statusListeners;
    

    public interface StatusProvider{
        public Status getStatusForProperty(String property);
    }
    
    public PropertyModel() {
        resetPropertyModel();
    }

    /**
     * May be called several times to reset this object.
     */
    private void resetPropertyModel() {
        if(this.propertyToValue == null) {
            this.propertyToValue = new HashMap();
        }
        else {
            this.propertyToValue.clear();
        }
        if(this.propertyToDefaultValue == null) {
            this.propertyToDefaultValue = new HashMap();
        }
        else {
            this.propertyToDefaultValue.clear();
        }
        if(this.propertyToState == null) {
            this.propertyToState = new HashMap();
        }
        else {
            this.propertyToState.clear();
        }
        this.optionGetDefaultTransparently = true;
        this.optionDefaultStatusIsOK = true;
        this.modelStatus = Status.OK;
        
        this.statusListeners = new LinkedList();
    }
    
    public void addStatusListener(StatusListener statusListener) {
        this.statusListeners.add(statusListener);
    }
    
    public void removeStatusListener(StatusListener statusListener) {
        this.statusListeners.remove(statusListener);
    }
    
    protected void fireStatusEvent(StatusEvent statusEvent) {
        for (Iterator iterator = this.statusListeners.iterator(); iterator.hasNext(); ) {
            StatusListener statusListener = (StatusListener) iterator.next();
            statusListener.handleStatusEvent(statusEvent);
        }
    }
    
    /**
     * If set the default value is retrieved when the value of a property was not found.
     * The user do not need to get the default value by himself. But on the other hand
     * he cant tell if a value is the default or a regular value.
     * @param optionGetDefaultTransparent
     */
    public void setOptionGetDefaultTransparent(boolean optionGetDefaultTransparent) {
        this.optionGetDefaultTransparently = optionGetDefaultTransparent;
    }
    
    /**
     * If set the default status is ok for properties which do not have a status set.
     * @param optionDefaultStatusOK
     */
    public void setOptionDefaultStatusOK(boolean optionDefaultStatusOK) {
        this.optionDefaultStatusIsOK = optionDefaultStatusOK;
    }
    
    public Object getPropertyValue(String property) {
        return getPropertyValue(property,this.dummyMutableBoolean);
    }
    
    public Object getPropertyValue(String property, MutableBoolean isDefaultValue) {
        if(property == null){
            throw new IllegalArgumentException("Parameter 'property' is null contrary to API.");
        }
        
        Object object = this.propertyToValue.get(property);
        if(this.optionGetDefaultTransparently && object == null) {
            object = this.propertyToDefaultValue.get(property);
            if(isDefaultValue != null) {
                isDefaultValue.setBooolean(true);
            }
        }
        else {
            if(isDefaultValue != null) {
                isDefaultValue.setBooolean(false);
            }
        }
        return object;
    }
    
    public void setPropertyValue(String property, Object value) {
        if(property == null){
            throw new IllegalArgumentException("Parameter 'property' is null contrary to API.");
        }
        
        Status status = getStatusForPropertyAndValue(property,value);
        if(status == null) {
            throw new IllegalArgumentException("Method checkPropertyAndValue must not return null.");
        }
        // TODO: Fire pre event. Decide if StatusEvent,ValueEvent or both. Do both!
        setPropertyStatus(property,status);
        this.propertyToValue.put(property,value);
        // TODO: Fire post event.
    }
    
    /**
     * Customize this method to handle specific properties and their valid values.
     * This method may alter the value.
     * TODO: This is extension through subclassing. Using composition is better.
     * So encapsulte this in an object and ask this object for a status
     * @param property
     * @param value
     * @return Never null.
     */
    protected Status getStatusForPropertyAndValue(String property, Object value) {
        if(property != null && value != null) {
            return Status.OK;
        }
        return Status.ERROR;
    }
    
    /**
     * 
     * @param property
     * @return Never null.
     */
    public Status getPropertyStatus(String property) {
        Status status = (Status)this.propertyToState.get(property);
        if(status == null) {
            if(this.optionDefaultStatusIsOK) {
                return Status.OK;
            }
            return Status.ERROR;
        }
        return status;
    }
    
    public void setPropertyStatus(String property, Status status) {
        if(property == null || status == null){
            logger.error("Parameter 'property' or 'status' is null contrary to API.");
            return;
        }
        this.propertyToValue.put(property,status);
        fireStatusEvent(new StatusEvent(property,null,status));
        checkModelStatus();
    }
    
    
    protected void checkModelStatus() {
        if(this.propertiesWithAtLeastOKStatus != null) {
            for (int i = 0; i < this.propertiesWithAtLeastOKStatus.length; i++) {
                String property = this.propertiesWithAtLeastOKStatus[i];
                Status status = getPropertyStatus(property);
                if(status.getType() != Status.TYPE_OK) {
                    setModelStatus(Status.ERROR);
                    return;
                }
            }
        }
        if(this.propertiesWithAtLeastWARNINGStatus != null) {
            for (int i = 0; i < this.propertiesWithAtLeastWARNINGStatus.length; i++) {
                String property = this.propertiesWithAtLeastWARNINGStatus[i];
                Status status = getPropertyStatus(property);
                if(status.getType() != Status.TYPE_OK || status.getType() != Status.TYPE_WARNING) {
                    setModelStatus(Status.ERROR);
                    return;
                }
            }
        }
        setModelStatus(Status.OK);
    }

    public Object getPropertyDefaultValue(String property) {
        if(property == null){
            logger.error("Parameter 'property' is null contrary to API.");
            return null;
        }
        return this.propertyToDefaultValue.get(property);
    }
    
    public void setPropertyDefaultValue(String property,Object defaultValue) {
        if(property == null){
            logger.error("Parameter 'property' is null contrary to API.");
            return;
        }
        this.propertyToDefaultValue.put(property,defaultValue);
    }
    
    public Status getModelStatus() {
        return this.modelStatus;
    }
    
    public void setModelStatus(Status status) {
        if(status == null){
            logger.error("Parameter 'status' is null contrary to API.");
            return;
        }
        this.modelStatus = status;
    }
}
