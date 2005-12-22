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
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 
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

    protected String[] propertiesLeastOK;
    protected String[] propertiesLeastWARNING;

    private boolean optionGetDefaultTransparently;
    private boolean optionDefaultStatusIsOK;
    

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
    }
    
    public void setOptionGetDefaultTransparent(boolean optionGetDefaultTransparent) {
        this.optionGetDefaultTransparently = optionGetDefaultTransparent;
    }
    
    public void setOptionDefaultStatusOK(boolean optionDefaultStatusOK) {
        this.optionDefaultStatusIsOK = optionDefaultStatusOK;
    }
    
    public Object getPropertyValue(String property) {
        if(property == null){
            throw new IllegalArgumentException("Parameter 'property' is null contrary to API.");
        }
        
        Object object = this.propertyToValue.get(property);
        if(this.optionGetDefaultTransparently && object == null) {
            object = this.propertyToDefaultValue.get(property);
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
     * TODO: This is extension through subclassing. Using composition is stronger.
     * So encapsulte this in an object and as this object for a status
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
        checkModelStatus();
    }
    
    
    protected void checkModelStatus() {
        if(this.propertiesLeastOK != null) {
            for (int i = 0; i < this.propertiesLeastOK.length; i++) {
                String property = this.propertiesLeastOK[i];
                Status status = getPropertyStatus(property);
                if(status.getType() != Status.TYPE_OK) {
                    setModelStatus(Status.ERROR);
                    return;
                }
            }
        }
        if(this.propertiesLeastWARNING != null) {
            for (int i = 0; i < this.propertiesLeastWARNING.length; i++) {
                String property = this.propertiesLeastWARNING[i];
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
