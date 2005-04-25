/*
 * Created on 15-Jan-2004 at 15:00:29.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.polish;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom.Element;

import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.StringUtil;

/**
 * <p>Provides common functionalities for PolishProject, Vendor, DeviceGroup and Device.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        15-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class PolishComponent
implements Comparable
{
	
	protected String identifier;
	protected PolishComponent parent;
	protected boolean supportsPolishGui;
	private HashMap features;
	private HashMap capabilities;
	private String featuresAsString;
	
	/**
	 * Creates a new component.
	 */
	public PolishComponent() {
		this( null );
	}
	/**
	 * Creates a new component.
	 * 
	 * @param parent the parent, e.g. is the parent of a vendor a project,
	 *              the parent of a device is a vendor.
	 */
	public PolishComponent( PolishComponent parent ) {
		this.parent = parent;
		this.capabilities = new HashMap();
		//this.capabilitiesList = new ArrayList();
		this.features = new HashMap();
		//this.featuresList = new ArrayList();
		if (parent != null) {
			this.capabilities.putAll( parent.getCapabilities() );
			this.features.putAll(  parent.getFeatures() );
		}
	}

	/**
	 * Loads all found capabilities of this component.
	 * 
	 * @param definition The xml definition.
	 * @param componentName The name of the component, e.g. "Nokia/3650" for a device.
	 * @param fileName The name of the source-file, e.g. "devices.xml".
	 * @throws InvalidComponentException when the defintion contains errors.
	 */
	protected void loadCapabilities(Element definition, String componentName, String fileName ) 
	throws InvalidComponentException 
	{
		// read capabilities:
		List capDefinitions = definition.getChildren("capability");
		for (Iterator iter = capDefinitions.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			String capName = element.getAttributeValue( "name" );
			if (capName == null) {
				capName = element.getChildTextTrim("capability-name");
			}
			if (capName == null) {
				throw new InvalidComponentException("The component [" + componentName + "] has an invalid [capability] - every capability needs to define the attribute [name]. Please check you [" + fileName + "].");
			}
			String capValue = element.getAttributeValue( "value" );
			if (capValue == null) {
				capValue = element.getChildTextTrim("capability-value");
			}
			if (capName == null) {
				throw new InvalidComponentException("The component [" + componentName + "] has an invalid [capability] - every capability needs to define the attribute [value]. Please check you [" + fileName + "].");
			}
			// add the capability:
			addCapability( capName, capValue );
		} // end of reading all capabilties
		
		// now set features:
		String featureDefinition = definition.getChildTextTrim( "features");
		if (featureDefinition != null) {
			String[] definedFeatures = StringUtil.splitAndTrim( featureDefinition, ',');
			for (int i = 0; i < definedFeatures.length; i++) {
				addFeature( definedFeatures[i] );
			}
			if (this.featuresAsString != null) {
				this.featuresAsString += ", " + featureDefinition;
			} else {
				this.featuresAsString = featureDefinition;
			}
		}
	}

	/**
	 * Adds a sub-component to this component.
	 * All capabilities will only be set when they have not been defined so far.
	 * JavaPackage- and JavaProtocol-definitions will be added, though.
	 * 
	 * @param component The component which definitions should be added 
	 */
	public void addComponent(PolishComponent component ) {
		// 1. set the capabilities:
		HashMap caps = component.getCapabilities();
		for (Iterator iter = caps.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			//System.out.println("adding component-key " + key);
			String currentValue = (String) this.capabilities.get( key);
			//System.out.println("current value: " + currentValue);
			String componentValue = (String) caps.get( key );
			//System.out.println("component value: " + componentValue);
			if (currentValue == null) {
				// okay, this capability has not been defined so far:
				addCapability( key, componentValue );
			} else if ( (Device.JAVA_PACKAGE.equals(key) ) 
					|| (Device.JAVA_PROTOCOL.equals(key)) 
					|| (Device.VIDEO_FORMAT.equals(key))
					|| (Device.SOUND_FORMAT.equals(key)) 
					|| (Device.BUGS.equals(key)) ) {
				// add additional package/protocol definitions:
				String newValue = currentValue + "," + componentValue;
				addCapability(key, newValue);
			} // else do not overwrite weaker capability
		}
		
		// 2. set all features (overwriting will do no harm):
		Set feats = component.features.keySet();
		for (Iterator iter = feats.iterator(); iter.hasNext();) {
			String feature = (String) iter.next();
			this.features.put( feature, Boolean.TRUE );
		}
		
		// 3. set the features-string:
		if (component.featuresAsString != null) {
			if (this.featuresAsString != null) {
				this.featuresAsString += ", " + component.featuresAsString;
			} else {
				this.featuresAsString = component.featuresAsString;
			}
		}
	}
	
	/**
	 * Retrieves all preprocessing-symbols of this component and its parent component.
	 * The symbols are arranged in a HashMap, so one can check for the definition
	 * of a symbol with <code> if (map.get( "symbol-name") != null) { // symbol is defined</code>.
	 * <p>Symbols can be retrieved in different ways:
	 * <ul>
	 * 	<li><b>polish.symbol-name</b>: this is the recommended way to check for a symbol.
	 * 				The symbols starting with "polish" can be defined by  the project, the vendor, 
	 * 				group or device used.</li>
	 * 	<li><b>project.symbol-name</b>: this name can be used to check for symbols which
	 * 				need to be defined in the project itself.</li>
	 * 	<li><b>vendor.symbol-name</b>: this name can be used to check for symbols which
	 * 				need to be defined in the current manufacturer definition (e.g. Nokia) itself.</li>
	 * 	<li><b>group.symbol-name</b>: this name can be used to check for symbols which
	 * 				need to be defined in the current groups (e.g. Series 60) itself.</li>
	 * 	<li><b>device.symbol-name</b>: this name can be used to check for symbols which
	 * 				need to be defined in the current device itself.</li>
	 * </ul>
	 * </p>
	 * @return the HashMap containing all names of the defined symbols as keys.
	 */
	public HashMap getFeatures() {
		return new HashMap( this.features );
	}
	
	/**
	 * Retrieves all preprocessing-variables of this component and its parent component.
	 * The values defined by a variable can be retrieved by calling 
	 * <code>String value = (String) map.get("variable-name")</code>.
	 * 
	 * <p>Variables which are defined in the device-database all start with "polish.":
	 * <ul>
	 * 	<li><b>polish.variable-name</b>: 
	 * 				The variables starting with "polish" can be defined by  the project, the vendor, 
	 * 				group or device used. They also can be overriden by specific settings.
	 * 			    For example a color "color.focus" could be defined in the project and
	 * 				also be defined in a device definition. "polish.color.focus" would then
	 * 			 	return the device definition while "project.color " would return the original 
	 * 				project definition, when the application is preprocessed for that
	 * 				device.</li>
	 * </ul>
	 * </p>
	 * @return a HashMap containing all names of the defined variables as the keys.
	 */
	public HashMap getCapabilities() {
		//HashMap copy = new HashMap( this.capabilities.size() + 20 );
		//copy.putAll( this.capabilities );
		return new HashMap( this.capabilities );
	}
	
	/**
	 * Adds a capability to this component.
	 * 
	 * @param name the name of the capability
	 * @param value the value of the capability
	 */
	public void addCapability( String name, String value ) {
		//System.out.println("adding capability " + name + " with value " + value );
		// when the capability starts with "SoftwarePlatform." or similiar, 
		// make it also accessible without it:
		if (name.startsWith("SoftwarePlatform.")) {
			name = name.substring( 17 );
		} else if (name.startsWith("HardwarePlatform.")) {
			name = name.substring( 17 );
		}
		
		if (!name.startsWith("polish.")) {
			name = "polish." + name;
		}
		
		if ( (Device.JAVA_PACKAGE.equals(name) ) 
				|| (Device.JAVA_PROTOCOL.equals(name)) 
				|| (Device.VIDEO_FORMAT.equals(name))
				|| (Device.SOUND_FORMAT.equals(name)) ) {
			value = value.toLowerCase();
			String existingValue = getCapability( name );
			if (existingValue != null) {
				value += "," + existingValue;
			}
		}
		addSingleCapability( name, value );
		
		// when the capability is a size, then also add a height and a width:
		if (name.endsWith("Size") && value.indexOf('x') > 0) {
			String[] values = StringUtil.splitAndTrim( value, 'x' );
			String nameStart = name.substring(0, name.length() - 4);
			String width = nameStart + "Width";
			addSingleCapability( width, values[0]);
			String height = nameStart + "Height";
			addSingleCapability( height, values[1]);
			if (values.length == 3) {
				String depth = nameStart + "Depth";
				addSingleCapability( depth, values[1]);
			}
		}
		
		// add all capability-values as symbols/features:
		String[] values = StringUtil.splitAndTrim( value, ',' );
		for (int i = 0; i < values.length; i++) {
			addFeature( name + "." + values[i] );
		}
	}
	
	/**
	 * Adds a single capability to this component.
	 * 
	 * @param name the name of the capability
	 * @param value the value of the capability
	 */
	private void addSingleCapability( String name, String value ) {
		name = name.toLowerCase();
		this.capabilities.put( name, value );
		this.features.put( name + ":defined", Boolean.TRUE );
	}
	
	/**
	 * Adds a capability without changing its name to this component.
	 * 
	 * @param capability The capability which should be added
	 */
	public void addDirectCapability(Variable capability) {
		addDirectCapability( capability.getName(), capability.getValue()  ); 
	}
	

	/**
	 * Adds a capability without changing its name to this component.
	 * 
	 * @param name The name of the capability.
	 * @param value The value of the capability.
	 */
	public void addDirectCapability(String name, String value) {
		//name = name.toLowerCase();
		addSingleCapability(name, value);
		// add all capability-values as symbols/features:
		String[] values = StringUtil.splitAndTrim( value, ',' );
		for (int i = 0; i < values.length; i++) {
			addDirectFeature( name + "." + values[i] );
		}
	}
	
	/**
	 * Adds a feature this this component.
	 * 
	 * @param feature the name of the feature
	 */
	public void addFeature( String feature ) {
		if (feature.length() == 0) {
			return;
		}
		feature = feature.toLowerCase();
		if (!feature.startsWith("polish.")) {
			feature = "polish." + feature;
		}
		this.features.put( feature, Boolean.TRUE );
	}
	
	/**
	 * Adds a feature without inserting a ".polish" before the feature-name.
	 * 
	 * @param feature The feature which should be added.
	 */
	public void addDirectFeature( String feature ) {
		feature = feature.toLowerCase();
		this.features.put( feature, Boolean.TRUE );
	}

	/**
	 * Checks if this component has a specific feature.
	 * A feature is a capability without a value.
	 * 
	 * @param feature the feature which should be defined, e.g. "hardware.camera"
	 * @return true when this feature is defined.
	 */
	public boolean hasFeature(String feature) {
		if ("supportsPolishGui".equals(feature)) {
			return this.supportsPolishGui;
		}
		feature = feature.toLowerCase();
		return ( (this.features.get(feature) != null) );
	}

	/**
	 * Determines whether this device supports the polish-gui-framework.
	 * Usually this is the case when the device meets some capabilities like
	 * the bits per pixel and the possible size of the heap.
	 * Devices can also define this directly by setting the attribute [supportsPolishGui]. 
	 * 
	 * @return true when this device supports the polish-gui.
	 */
	public boolean supportsPolishGui() {
		return this.supportsPolishGui;
	}
	
	/**
	 * Retrieves a specific capability of this component.
	 * 
	 * @param key the name of the capability. 
	 * @return the value of the capability or null when the given capability is not defined.
	 */
	public String getCapability(String key) {
		key = key.toLowerCase();
		String capability = (String) this.capabilities.get( key );
		if (capability == null) {
			capability = (String) this.capabilities.get( "polish." + key );
		}
		return capability;
	}

	/**
	 * Retrieves the identifier or name of this component.
	 * 
	 * @return The identifier of this component, e.g. "Nokia". 
	 */
	public String getIdentifier() {
		return this.identifier;
	}

	/**
	 * Retrieves the defined features of this component.
	 * 
	 * @return the features which have been set in the appropriate component.xml file
	 */
	public String getFeaturesAsString() {
		return this.featuresAsString;
	}
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if (this.identifier != null && o instanceof PolishComponent) {
			String otherIdentifier = ((PolishComponent)o).identifier;
			if (otherIdentifier != null) {
				return this.identifier.compareTo(otherIdentifier);
			//} else {
			//	System.out.println("Other's identifier == null!");
			}
		}
		//System.out.println("Unable to compare  " + getClass().getName() + " with " + o.getClass().getName() + ": this.identifier == null: " + (this.identifier == null) );
		return 0;
	}
}
