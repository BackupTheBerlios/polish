/*
 * Created on 29-Jun-2004 at 21:40:55.
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.jdom.JDOMException;

import de.enough.polish.Device;
import de.enough.polish.DeviceGroupManager;
import de.enough.polish.DeviceManager;
import de.enough.polish.IdentifierComparator;
import de.enough.polish.Library;
import de.enough.polish.LibraryManager;
import de.enough.polish.Variable;
import de.enough.polish.VendorManager;
import de.enough.polish.ant.requirements.Requirements;
import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.TextUtil;

/**
 * <p>Exports the device database to a HTML format.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        29-Jun-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class HtmlExporterTask extends Task {
	
	private String wtkHome = "/home/enough/dev/WTK2.1";
	private String preverifyHome = this.wtkHome + "/bin/preverify";
	private String targetDir = "../enough-polish-website/tmp/devices/";
	private HashMap deviceLinks = new HashMap();
	private Comparator caseInsensitiveComparator = new CaseInsensitiveComparator();
	private String databaseDir = "../enough-polish-build/";

	/**
	 * Creates a new uninitialised task
	 */
	public HtmlExporterTask() {
		super();
	}
	
	public void execute() throws BuildException {
		// create LibraryManager:
		try {
			LibraryManager libraryManager = new LibraryManager(this.project.getProperties(), "import", this.wtkHome, this.preverifyHome, open( "apis.xml" ) );
			VendorManager vendorManager = new VendorManager( null, open("vendors.xml"));
			DeviceGroupManager groupManager = new DeviceGroupManager( open("groups.xml") ); 
			DeviceManager deviceManager = new DeviceManager( vendorManager, groupManager, libraryManager, open("devices.xml") );
			Device[] devices = deviceManager.getDevices();
			
			// create detailed device pages:
			for (int i = 0; i < devices.length; i++) {
				Device device = devices[i];
				writeDevicePage( device );
			}
			
			// sort by vedor and name:
			Arrays.sort( devices, new IdentifierComparator() );
			process( "Devices by Vendor", "devices-vendor.html", 
					devices, 
					new VendorIndexGenerator(),
					null, "vendor");
			
			// generate Index of used APIs:
			HashMap apisByName = new HashMap();
			for (int i = 0; i < devices.length; i++) {
				Device device = devices[i];
				String[] apis = device.getSupportedApis();
				if (apis != null) {
					for (int j = 0; j < apis.length; j++) {
						String api = apis[j];
						apisByName.put( api, Boolean.TRUE );
					}
				}
			}
			String[] apis = (String[]) apisByName.keySet().toArray( new String[ apisByName.size()] );
			Arrays.sort( apis );
			String[] apiLinks = new String[ apis.length ];
		 	// get the devices for each api:
			IndexGenerator indexGenerator = new NoIndexGenerator();
			for (int i = 0; i < apis.length; i++) {
				String api = apis[i];
				Requirements requirements = new Requirements();
				requirements.addConfiguredRequirement( new Variable("JavaPackage", api ));
				Device[] filteredDevices = requirements.filterDevices(devices);
				Library lib = libraryManager.getLibrary( api );
				String fullApiName;
				String introText = null;
				if (lib != null) {
					fullApiName = lib.getFullName();
					introText = lib.getDescription();
				} else {
					if (api.indexOf("api") == -1) {
						fullApiName = api + " API";
					} else {
						fullApiName = api;
					}
				}
				String fileName = "devices-" + clean( api ) + ".html";
				process( "Devices supporting the " + fullApiName,  fileName,
						filteredDevices, indexGenerator, introText, "api" );
				apiLinks[i] = "<a href=\"" + fileName + "\" class=\"h2index\">" + fullApiName + "</a><br/>";
			}
			writeApiOverview( apiLinks );
			
			// now select devices by platform:
			writePlatformOverview();
			Requirements requirements = new Requirements();
			requirements.addConfiguredRequirement( new Variable("JavaPlatform", "MIDP/1.0" ));
			Device[] filteredDevices = requirements.filterDevices(devices);
	 		process("MIDP/1.0 Devices", "midp1.html", filteredDevices, indexGenerator, 
					"The following devices support the MIDP/1.0 standard.", "platform");

			requirements = new Requirements();
			requirements.addConfiguredRequirement( new Variable("JavaPlatform", "MIDP/2.0" ));
			filteredDevices = requirements.filterDevices(devices);
			process("MIDP/2.0 Devices", "midp2.html", filteredDevices, indexGenerator, 
					"The following devices support the MIDP/2.0 standard.", "platform");			

			requirements = new Requirements();
			requirements.addConfiguredRequirement( new Variable("JavaConfiguration", "CLDC/1.0" ));
			filteredDevices = requirements.filterDevices(devices);
			process("CLDC/1.0 Devices", "cldc10.html", filteredDevices, indexGenerator, 
					"The following devices support the CLDC/1.0 configuration.", "platform");			

			requirements = new Requirements();
			requirements.addConfiguredRequirement( new Variable("JavaConfiguration", "CLDC/1.1" ));
			filteredDevices = requirements.filterDevices(devices);
			process("CLDC/1.1 Devices", "cldc11.html", filteredDevices, indexGenerator, 
					"The following devices support the CLDC/1.1 configuration.", "platform");			
	} catch (IOException e) {
			throw new BuildException("unable to init manager: " + e.getMessage(), e );
		} catch (JDOMException e) {
			throw new BuildException("unable to init manager: " + e.getMessage(), e );
		} catch (InvalidComponentException e) {
			throw new BuildException("unable to create device manager: " + e.getMessage(), e );
		}
	}
	
	/**
	 * @param device
	 * @throws IOException
	 */
	private void writeDevicePage(Device device) throws IOException {
		String vendor = clean( device.getVendorName() );
		String name = clean( device.getName() );
		String fileName = vendor + "/" + name + ".html";
		this.deviceLinks.put( device.getIdentifier(), fileName);
		
		System.out.println("Creating " + fileName);
		ArrayList lines = new ArrayList();
		lines.add("<%define inDevicesSection %>");
		lines.add("<%set title = J2ME Polish: " + device.getIdentifier() +" %>");
		lines.add("<%set basedir = ../../ %>");
		lines.add("<%include start.txt %>" );
		lines.add("");
		lines.add("<div id=\"content\">" );
		lines.add("<h1 id=\"top\">" + device.getIdentifier() + "</h1>" );
		lines.add("<p>");
		lines.add("<%index %>");
		lines.add("<br/></p>");
		if (device.hasFeature("polish.isVirtual")) {
			lines.add( "<p>This device is a virtual device which combines several features of real devices. A virtual device represents a group of devices.</p>");
		}
		startTable( lines );
		addDeviceInfo(lines, device, "oddRow", false);		
		endTable( lines );
		String[] groups = device.getGroupNames();
		if (groups != null && groups.length > 0) {
			lines.add("<h2 id=\"groups\">Groups</h2>");
			lines.add("<p>Groups can be used to compose the resources (like images or sound-files) for an application." +
					"<br/>Have a look at the " +
					"<a href=\"<%= basedir %>docs/resource-assembling.html\">documentation</a> for more info.</p>");
			lines.add("<table class=\"borderedTable\"><tr><th>Group</th><th>Resource Folder</th></tr>");
			for (int i = 0; i < groups.length; i++) {
				String group = groups[i];
				lines.add("<tr><td>" + group + "</td><td>resources/" + group + "</td></tr>");
			}
			lines.add("</table>");
		}
		
		// add capabilities:
		HashMap capabilitiesByName = device.getCapabilities();
		String[] capabilities = (String[]) capabilitiesByName.keySet().toArray( new String[ capabilitiesByName.size()] );
		Arrays.sort( capabilities, this.caseInsensitiveComparator );
		lines.add("<h2 id=\"capabilities\">Preprocessing Capabilities</h2>");
		lines.add("<p>Capabilities can be used with the &quot;//#=&quot; " +
				"preprocessing directive and can be compared with the &quot;//#if&quot; " +
				"directive. For each defined capability a preprocessing symbol " +
				"with the same name will be defined." +
				"<br/>Have a look at the <a href=\"<%= basedir %>docs/preprocessing.html\">preprocessing documentation</a> " +
				"for more information.</p>");
		lines.add("<p>Examples:");
		lines.add("<pre>");
		lines.add("//#if polish.Vendor == " + device.getVendorName() );
		lines.add("	// this is a " + device.getVendorName() + " device");
		lines.add("//#endif");
		lines.add("//#ifdef polish.ScreenWidth:defined");
		lines.add("	//#= int screenWidth = ${ polish.ScreenWidth };");
		lines.add("//#else");
		lines.add("	int screenWidth = UNKNOWN_WIDTH;");
		lines.add("//#endif");
		lines.add("</pre></p>");
		lines.add("<table class=\"borderedTable\"><tr><th>Capability</th><th>Value</th></tr>");
		for (int i = 0; i < capabilities.length; i++) {
			String cap = capabilities[i];
			String value = (String) capabilitiesByName.get( cap );
			lines.add("<tr><td>" + cap + "</td><td>" + value + "</td></tr>");
		}
		lines.add("</table>");
		
		// add symbols:
		HashMap symbolsByName = device.getFeatures();
		String[] symbols = (String[]) symbolsByName.keySet().toArray( new String[ symbolsByName.size()] );
		Arrays.sort( symbols, this.caseInsensitiveComparator );
		lines.add("<h2 id=\"symbols\">Preprocessing Symbols</h2>");
		lines.add("<p>Symbols can be evaluated with the &quot;//#ifdef&quot; " +
				"and related preprocessing directives." +
				"<br/>Have a look at the <a href=\"<%= basedir %>docs/preprocessing.html\">preprocessing documentation</a> " +
				"for more information.</p>");
		lines.add("<p>Example:");
		lines.add("<pre>");
		lines.add("//#if polish.api.mmapi || polish.midp2" );
		lines.add("	// this device supports the Mobile Media API");
		lines.add("//#endif");
		lines.add("</pre></p>");
		lines.add("<table class=\"borderedTable\"><tr><th>Symbol</th></tr>");
		for (int i = 0; i < symbols.length; i++) {
			String symbol = symbols[i];
			lines.add("<tr><td>" + symbol + "</td></tr>");
		}
		lines.add("</table>");
		
		
		// add the end:
		lines.add("<%include end.txt %>");		
		// write the file:
		String[] htmlCode = (String[] ) lines.toArray( new String[ lines.size() ] );
		FileUtil.writeTextFile( new File( this.targetDir + fileName), htmlCode );	
	}

	/**
	 * @throws IOException
	 * 
	 */
	private void writePlatformOverview() throws IOException {
		String fileName = "platform.html";
		System.out.println("Creating " + fileName);
		ArrayList lines = new ArrayList();
		lines.add("<%define inDevicesSection %>");
		lines.add("<%define inDevicesSection.platform %>");
		lines.add("<%set title = J2ME Polish: Device Database %>");
		lines.add("<%set basedir = ../ %>");
		lines.add("<%include start.txt %>" );
		lines.add("");
		lines.add("<div id=\"content\">" );
		lines.add("<h1 id=\"top\">Device by APIs</h1>" );
		lines.add( "<p>Following platforms are supported by J2ME devices:</p><ul>");
		lines.add("<li><a href=\"midp1.html\">MIDP/1.0</a></li>");
		lines.add("<li><a href=\"midp2.html\">MIDP/2.0</a></li>");
		lines.add("</ul>");
		lines.add( "<p>Following configurations are supported by J2ME devices:</p><ul>");
		lines.add("<li><a href=\"cldc10.html\">CLDC/1.0</a></li>");
		lines.add("<li><a href=\"cldc11.html\">CLDC/1.1</a></li>");
		lines.add("</ul>");
		// add the end:
		lines.add("<%include end.txt %>");
		
		// write the file:
		String[] htmlCode = (String[] ) lines.toArray( new String[ lines.size() ] );
		FileUtil.writeTextFile( new File( this.targetDir + fileName), htmlCode );	
	}

	/**
	 * @param apiLinks
	 * @throws IOException
	 */
	private void writeApiOverview(String[] apiLinks) throws IOException {
		String fileName = "apis.html";
		System.out.println("Creating " + fileName);
		ArrayList lines = new ArrayList();
		lines.add("<%define inDevicesSection %>");
		lines.add("<%define inDevicesSection.api %>");
		lines.add("<%set title = J2ME Polish: APIs %>");
		lines.add("<%set basedir = ../ %>");
		lines.add("<%include start.txt %>" );
		lines.add("");
		lines.add("<div id=\"content\">" );
		lines.add("<h1 id=\"top\">Device by APIs</h1>" );
		lines.add( "<p>Following APIs are supported by J2ME devices:</p><p>");
		for (int i = 0; i < apiLinks.length; i++) {
			lines.add( apiLinks[i] );
		}
		lines.add("</p>");
		// add the end:
		lines.add("<%include end.txt %>");
		
		// write the file:
		String[] htmlCode = (String[] ) lines.toArray( new String[ lines.size() ] );
		FileUtil.writeTextFile( new File( this.targetDir + fileName), htmlCode );
	}
	

	private String clean( String fileName) {
		fileName = TextUtil.replace( fileName, ' ', '_');
		fileName = TextUtil.replace( fileName, '/', '_');
		return fileName;
	}

	/**
	 * Opens a file
	 * 
	 * @param fileName the name of the file which should be opened
	 * @return an input stream of the specified file
	 * @throws FileNotFoundException when the specified file was not found
	 */
	private InputStream open(String fileName) 
	throws FileNotFoundException 
	{
		File file = new File( this.databaseDir + fileName );
		return new FileInputStream( file );
	}
	
	private void process( String heading, String fileName, Device[] devices, 
			IndexGenerator indexGenerator,
			String introText, String subSection ) 
	throws IOException 
	{
		if (devices.length == 0) {
			throw new BuildException("unable to create [" + fileName + "]: got only 0 devices.");
		}
		System.out.println("Creating " + fileName);
		String[] cssRowClasses = new String[]{"oddRow", "evenRow" };
		ArrayList lines = new ArrayList();
		lines.add("<%define inDevicesSection %>");
		lines.add("<%define inDevicesSection." + subSection + " %>");
		lines.add("<%set title = J2ME Polish: " + heading + " %>");
		lines.add("<%set basedir = ../ %>");
		lines.add("<%include start.txt %>" );
		lines.add("");
		lines.add("<div id=\"content\">" );
		lines.add("<h1 id=\"top\">" + heading + "</h1>" );
		if (introText != null) {
			lines.add("<p>" + introText + "</p>");
		}
		if (devices.length > 1) {
			lines.add("<p>There are " + devices.length + " devices in this list.</p>");
		}
		int indexRow = lines.size();
		startTable( lines );
		for (int i = 0; i < devices.length; i++) {
			Device device = devices[i];
			indexGenerator.update(device, lines);
			String cssClass = cssRowClasses[ i % 2 ];
			addDeviceInfo(lines, device, cssClass, true);
		}
		endTable( lines );
		
		// now insert the index:
		String[] index = indexGenerator.generateIndex();
		for (int i = 0; i < index.length; i++) {
			String line = index[i];
			lines.add(indexRow, line);
			indexRow++;
		}
		
		// add the end:
		lines.add("<%include end.txt %>");
		
		// write the file:
		String[] htmlCode = (String[] ) lines.toArray( new String[ lines.size() ] );
		FileUtil.writeTextFile( new File( this.targetDir + fileName), htmlCode );
	}
	
	private void addDeviceInfo( ArrayList lines, Device device, String cssClass, boolean addDeviceLink ) {
		lines.add("<tr class=\"" + cssClass + "\">");
		StringBuffer buffer = new StringBuffer();
		buffer.append( "<td>" ).append( device.getVendorName() ).append( "</td><td>" );
		if (addDeviceLink) {
			String link = (String) this.deviceLinks.get( device.getIdentifier());
			buffer.append("<a href=\"").append( link ).append("\">");
		}
		buffer.append( device.getName() );
		if (addDeviceLink) {
			buffer.append("</a></td><td>" );
		} else {
			buffer.append("</td><td>" );			
		}
		if (device.supportsPolishGui()) {
			buffer.append("<img src=\"<%= basedir %>images/checked.png\" width=\"17\" height=\"17\" />");
		} else {
			buffer.append("-");
		}
		buffer.append( "</td><td>" );
		buffer.append( device.getCapability("JavaPlatform") );
		String configuration = device.getCapability( "JavaConfiguration" );
		if ( configuration != null) {
			buffer.append( " ").append( configuration );
		}
		buffer.append( "</td><td>" );
		String apis = device.getSupportedApisAsString();
		if (apis == null) {
			apis = "-";
		}
		buffer.append( apis  ).append( "</td><td>" );
		
		String screenSize = device.getCapability("ScreenSize");
		if (screenSize != null) {
			buffer.append( screenSize );
		} else {
			buffer.append( "-");
		}
		buffer.append("</td><td>");
		
		String bitsPerPixel = device.getCapability( "BitsPerPixel" );
		if (bitsPerPixel != null) {
			buffer.append( bitsPerPixel );
		} else {
			buffer.append( "-");
		}
		buffer.append("</td><td>");
		
		String features = device.getFeaturesAsString();
		if (features != null) {
			buffer.append( features );
		} else {
			buffer.append( "-");
		}
		buffer.append("</td>");
		
		lines.add( buffer.toString() );
		lines.add("</tr>");
	}
	
	private void endTable( ArrayList list ) {
		list.add("</table><p>GUI: device supports the J2ME Polish GUI.</p>");
	}
	private void startTable( ArrayList list ) {
		list.add("<table width=\"100%\" class=\"borderedTable\">");
		list.add("<tr><th>Vendor</th><th>Device</th><th>GUI</th><th>Platform</th><th>APIs</th>" +
				"<th>ScreenSize</th><th>BitsPerPixel</th><th>Features</th></tr>");
	}
	
	abstract class IndexGenerator {
		protected ArrayList indeces = new ArrayList();
		
		public abstract void update( Device device, ArrayList list);
		
		protected void addIndex( String index, ArrayList list ) {
			boolean firstEntry = (this.indeces.size() == 0);
			this.indeces.add( index );
			if (!firstEntry) {
				endTable( list );
			}
			list.add("<h2 id=\"" + index + "\">" + index + "</h2>");
			if (!firstEntry) {
				startTable( list );
			}
		}
		
		public String[] generateIndex(){
			String[] index = new String[ this.indeces.size() ];
			for (int i = 0; i < index.length; i++) {
				String heading = (String) this.indeces.get( i );
				index[i] = "<a href=\"#" + heading + "\" class=\"h2index\">" + heading + "</a><br/>";
			}
			return index;
	 	}
	}
	
	class VendorIndexGenerator extends IndexGenerator {
		
		private String lastVendor;
		
		/* (non-Javadoc)
		 * @see de.enough.polish.ant.HtmlExporterTask.IndexGenerator#update(de.enough.polish.Device, java.util.ArrayList)
		 */
		public void update(Device device, ArrayList list) {
			String vendor = device.getVendorName();
			if (!vendor.equals( this.lastVendor)) {
				addIndex( vendor, list );
				this.lastVendor = vendor;
			}
		}

	}
	
	class NoIndexGenerator extends IndexGenerator {

		/* (non-Javadoc)
		 * @see de.enough.polish.ant.HtmlExporterTask.IndexGenerator#update(de.enough.polish.Device, java.util.ArrayList)
		 */
		public void update(Device device, ArrayList list) {
			// nothing to update
		}
		
	}

	class CaseInsensitiveComparator implements Comparator {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object o1, Object o2) {
			return o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase());
		}
		
	}

}
