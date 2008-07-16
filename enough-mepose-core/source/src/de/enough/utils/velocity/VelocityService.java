package de.enough.utils.velocity;


import java.io.InputStream;

import org.apache.velocity.app.VelocityEngine;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Dec 16, 2005 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public interface VelocityService {

    public void setTemplate(String name, InputStream resource);
    public VelocityEngine getNewVelocityEngine();
}
