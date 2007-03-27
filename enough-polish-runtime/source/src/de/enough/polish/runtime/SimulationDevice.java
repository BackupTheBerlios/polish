package de.enough.polish.runtime;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.apache.tools.ant.AntClassLoader;
import org.jdom.Element;

import de.enough.polish.util.StringUtil;

/**
 * <p>Represents a J2ME device.</p>
 * 
 * <p>
 * copyright Enough Software 2004, 2005
 * </p>
 * 
 * <pre>
 * 
 *  history
 *         15-Jan-2004 - rob creation
 *  
 * </pre>
 * 
 * @author Robert Virkus, robert@enough.de
 */
public class SimulationDevice
{

	public static final String IDENTIFIER = "polish.Identifier";

	public static final String VENDOR = "polish.Vendor";

	public static final String NAME = "polish.Name";

	public static final String SCREEN_SIZE = "polish.ScreenSize";

	public static final String SCREEN_WIDTH = "polish.ScreenWidth";

	public static final String SCREEN_HEIGHT = "polish.ScreenHeigth";

	public static final String CANVAS_SIZE = "polish.CanvasSize";

	public static final String CANVAS_WIDTH = "polish.CanvasWidth";

	public static final String CANVAS_HEIGHT = "polish.CanvasHeigth";

	public static final String FULL_CANVAS_SIZE = "polish.FullCanvasSize";

	public static final String BITS_PER_PIXEL = "polish.BitsPerPixel";

	public static final String JAVA_PLATFORM = "polish.JavaPlatform";
	public static final String JAVA_CONFIGURATION = "polish.JavaConfiguration";

	public static final String JAVA_PROTOCOL = "polish.JavaProtocol";

	public static final String JAVA_PACKAGE = "polish.JavaPackage";

	public static final String HEAP_SIZE = "polish.HeapSize";

	public static final String USER_AGENT = "polish.UserAgent";

	public static final String VIDEO_FORMAT = "polish.VideoFormat";

	public static final String SOUND_FORMAT = "polish.SoundFormat";

	public static final String BUGS = "polish.Bugs";
	
	public static final String SUPPORTS_POLISH_GUI = "polish.supportsPolishGui";

	public static final int MIDP_1 = 1;

	public static final int MIDP_2 = 2;

	public static final int MIDP_3 = 3;

	private static final int POLISH_GUI_MIN_BITS_PER_PIXEL = 8;

	private static final String INVALID_GROUP_NAME_MESSAGE = "The device \"{0}\" contains the undefined group \""
									+ "{1}\" - please check either [devices.xml] or [groups.xml].";

	private String name;

	private String vendorName;

	private int midpVersion;

	private String[] supportedApis;

	private String supportedApisString;

	private String sourceDir;

	private String classesDir;

	private String baseDir;

	private String[] groupNames;

	private File jarFile;

	private int numberOfChangedFiles;

	private boolean isCldc10;
	private boolean isCldc11;

	private ClassLoader classLoader;

	private final boolean isVirtual;

	private SimulationDevice parentDevice;

  private File resourceDir;

  private String identifier;

  private Hashtable capabilities = new Hashtable();

  public SimulationDevice() {
		this.identifier = "Generic/AppletDevice";
		this.isVirtual = false;
    
		this.capabilities.put("polish.FullCanvasWidth", "240");
		this.capabilities.put("polish.FullCanvasHeight", "320");
		this.capabilities.put("polish.ScreenWidth", "240");
    this.capabilities.put("polish.ScreenHeight", "320");
    this.capabilities.put("polish.BitsPerPixel", "18");
    this.capabilities.put("polish.Emulator.control", "Series60");
    this.capabilities.put("hasPointerEvents", "true");
	}
  
  public String getCapability(String key)
  {
    return (String) this.capabilities.get(key);
  }

  public String getIdentifier()
  {
    return this.identifier;
  }
}