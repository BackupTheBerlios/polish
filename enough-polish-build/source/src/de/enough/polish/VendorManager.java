/*
 * Created on 16-Feb-2004 at 19:09:20.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish;

import de.enough.polish.exceptions.InvalidComponentException;

import org.apache.tools.ant.BuildException;
import org.jdom.*;
import org.jdom.input.SAXBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * <p>Manages all known vendors.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        16-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class VendorManager {
	
	private HashMap vendors;
	
	/**
	 * Creates a new vendor manager.
	 * 
	 * @param project The j2me project settings.
	 * @param vendorsIS The input stream containing the vendor-definitions. This is usually "./vendors.xml".
	 * @throws JDOMException when there are syntax errors in devices.xml
	 * @throws IOException when devices.xml could not be read
	 * @throws InvalidComponentException when a vendor definition has errors
	 */
	public VendorManager( PolishProject project, InputStream vendorsIS ) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		this.vendors = new HashMap();
		loadVendors( project, vendorsIS );
		vendorsIS.close();
	}
	
	/**
	 * Loads all known vendors from the given file.
	 * 
	 * @param project The j2me project settings.
	 * @param vendorsIS The input stream containing the vendor-definitions. This is usually "./vendors.xml".
	 * @throws JDOMException when there are syntax errors in devices.xml
	 * @throws IOException when devices.xml could not be read
	 * @throws InvalidComponentException when a vendor definition has errors
	 */
	private void loadVendors(PolishProject project, InputStream vendorsIS) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		if (vendorsIS == null) {
			throw new BuildException("Unable to load vendors.xml, no file found.");
		}
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( vendorsIS );
		List xmlList = document.getRootElement().getChildren();
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element deviceElement = (Element) iter.next();
			Vendor vendor = new Vendor( project, deviceElement );
			this.vendors.put( vendor.getIdentifier(), vendor );
		}
	}

	/**
	 * Retrieves the specified vendor.
	 * 
	 * @param name The name of the vendor, e.g. Nokia, Siemens, Motorola, etc.
	 * @return The vendor or null of that vendor has not been defined.
	 */
	public Vendor getVendor( String name ) {
		return (Vendor) this.vendors.get( name );
	}
}
