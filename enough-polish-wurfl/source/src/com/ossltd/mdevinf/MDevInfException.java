/*
 * Class name : MDevInfException
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 23-Mar-2006
 */


package com.ossltd.mdevinf;

/**
 * Used for managing exceptions in the mDevInf application
 * @author Jim McLachlan
 */
public class MDevInfException extends Exception
{
    /**
     * All mDevInfExceptions raised must contain a description of the cause.
     * @param message the cause of the exception
     */
    public MDevInfException(String message)
    {
        super(message);
    }
}
