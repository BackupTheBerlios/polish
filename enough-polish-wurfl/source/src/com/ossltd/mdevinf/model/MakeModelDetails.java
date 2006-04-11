/*
 * Class name : MakeModelDetails
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 23-Mar-2006
 */

package com.ossltd.mdevinf.model;

/**
 * Stores the device make and model information for display in the most
 * recently selected device list and in the configuration file. 
 * @author Jim McLachlan
 */
public class MakeModelDetails
{
    /** The brand_name of the device. */
    private String make;
    
    /** The model_name of the device. */
    private String model;
    
    /**
     * Creates an instance with the provided information.
     * @param make the device make
     * @param model the device model
     */
    public MakeModelDetails(String make, String model)
    {
        this.make = make;
        this.model = model;
    }

    /**
     * Accessor.
     * @return Returns the make.
     */
    public String getMake()
    {
        return (make);
    }

    /**
     * Accessor.
     * @return Returns the model.
     */
    public String getModel()
    {
        return (model);
    }

    /**
     * Provides the make and model combination in String format.
     * @return the make and model, separated by a space
     */
    public String toString()
    {
        return (make + " " + model);
    }
    
    /**
     * Checks two MakeModelDetails objects for equality
     * @param o the MakeModelDetails object to compare to this one
     * @return <code>true</code> if both make and model strings match, otherwise
     * <code>false</code>
     */
    public boolean equals(Object o)
    {
        boolean result = false;
        
        if (o instanceof MakeModelDetails)
        {
            MakeModelDetails obj = (MakeModelDetails)o;
            result = ((obj.make.equals(make)) && (obj.model.equals(model)));
        }
        
        return (result);
    }
}
