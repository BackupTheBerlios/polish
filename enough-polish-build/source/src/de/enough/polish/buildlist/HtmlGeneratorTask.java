package de.enough.polish.buildlist;

import de.enough.polish.ant.ConditionalTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.tools.ant.BuildException;

public class HtmlGeneratorTask
	extends ConditionalTask
{
	public static class VendorComparator implements Comparator
	{
		public int compare(Object v1, Object v2)
		{
			String s1 = (String) v1;
			String s2 = (String) v2;

			if ("Generic".equals(s1)) {
				return "Generic".equals(s2) ? 0 : -1;
			}

			return "Generic".equals(s2) ? -1 : s1.compareTo(s2);
		}
	}

	private static final String DEFAULT_BUILDLIST = "dist/buildlist.txt";
	private static final String DEFAULT_TITLE = "Device list";

	private String buildlist = DEFAULT_BUILDLIST;
	private String title = DEFAULT_TITLE;
	private String intro = "";
	private String outputFile = "dist/index.html";

	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException
	{
		File buildListFile = new File(this.buildlist);
		
		if (!buildListFile.exists()) {
			throw new BuildException("buildlist file \"" + this.buildlist + "\" not found");
		}
		
		try
		{
			TreeMap devicesByVendor = readDeviceList(buildListFile);

			generateVendorList(devicesByVendor);
			Iterator it = devicesByVendor.keySet().iterator();

			while (it.hasNext()) {
				String vendorName = (String) it.next();
				generateVendorFile(vendorName, devicesByVendor);
			}
		}
		catch (IOException e)
		{
			throw new BuildException(e);
		}
	}

	private TreeMap readDeviceList(File buildListFile)
		throws IOException
	{
		TreeMap devicesByVendor = new TreeMap(new VendorComparator());
		BufferedReader reader = new BufferedReader(new FileReader(buildListFile));
		String line;

		while ((line = reader.readLine()) != null) {
			int pos = line.indexOf('/');
			StringTokenizer st = new StringTokenizer(line, "\t");
			String deviceName = (String) st.nextElement();
			st.nextElement();
			st.nextElement();
			String jadName = (String) st.nextElement();
			String vendorName = deviceName.substring(0, pos);
			
			TreeMap deviceList = (TreeMap) devicesByVendor.get(vendorName);

			if (deviceList == null) {
				deviceList = new TreeMap();
				devicesByVendor.put(vendorName, deviceList);
			}
			deviceList.put(deviceName, jadName);
		}

		reader.close();
		return devicesByVendor;
	}

	protected void generateVendorList(TreeMap devicesByVendor)
		throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputFile));

		writer.write("<html>\n");
		writer.write("<head>\n");
		writer.write("<title>\n");
		writer.write(this.title);
		writer.write("\n</title>\n");
		writer.write("</head>\n");
		writer.write("<body>\n");
		writer.write("<p>");
		writer.write(this.intro);
		writer.write("</p>");
		writer.write("<p>");

		Iterator it = devicesByVendor.keySet().iterator();

		while (it.hasNext()) {
			String vendorName = (String) it.next();

			writer.write("<a href=\"");
			writer.write(vendorName);
			writer.write(".html\">");
			writer.write(vendorName);
			writer.write("</a><br />\n");
		}
		
		writer.write("</p>");
		writer.write("</body>\n");
		writer.write("</html>\n");
		writer.close();
	}

	protected void generateVendorFile(String vendorName, TreeMap devicesByVendor)
		throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter("dist/" + vendorName + ".html"));
		writer.write("<html>\n");
		writer.write("<head>\n");
		writer.write("<title>\n");
		writer.write(this.title);
		writer.write("\n</title>\n");
		writer.write("</head>\n");
		writer.write("<body>\n");
		writer.write("<p>\n");

		TreeMap deviceList = (TreeMap) devicesByVendor.get(vendorName);
		Iterator it = deviceList.keySet().iterator();

		while (it.hasNext()) {
			String deviceName = (String) it.next();
			String jadName = (String) deviceList.get(deviceName);

			writer.write("<a href=\"");
			writer.write(jadName);
			writer.write("\">");
			writer.write(deviceName);
			writer.write("</a><br />\n");
		}

		writer.write("</p>\n");
		writer.write("<p>\n");
		writer.write("<a href=\"index.html\">Back</a>");
		writer.write("</p>\n");
		writer.write("</body>\n");
		writer.write("</html>\n");
		writer.close();
	}

	public String getBuildlist()
	{
		return this.buildlist;
	}

	public void setBuildlist(String buildlist)
	{
		this.buildlist = buildlist;
	}

	public String getTitle()
	{
		return this.title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getIntro()
	{
		return this.intro;
	}

	public void setIntro(String intro)
	{
		this.intro = intro;
	}

	public String getOutputFile()
	{
		return this.outputFile;
	}

	public void setOutputFile(String outputFile)
	{
		this.outputFile = outputFile;
	}
}
