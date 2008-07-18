//#condition polish.android
package de.enough.polish.drone.resource;

import java.io.InputStream;
import java.util.HashMap;

import android.content.Resources;
import android.util.Log;
import de.enough.polish.drone.midlet.MIDlet;

public class RawResources {

	/**
	 *	The map to store the resource name 
	 */
	private static HashMap resourceMap;
	private static Resources resources;
	
	/**
	 *	Get the resource path to cut it off the incoming resourcename 
	 */
	private static final String resourcePath = "/res/raw"; 
	
	private static void initResources()
	{
		resourceMap = new HashMap();
		resources = MIDlet.current.getResources();
		
		try
		{	
//#=		for(int id=${polish.android.R}.raw._; id<${polish.android.R}.string.app_name; id++)
//#=			{
//#=				String name = resources.getString(id);
//#=			if(id != ${polish.android.R}.raw._)
//#=				{
//#=					name = name.substring(resourcePath.length() - 1);
//#=					resourceMap.put(name, new Integer(id));
//#=				}
//#=			}
		}
		catch(Resources.NotFoundException e){}
		catch(NullPointerException e)
		{
			Log.e(MIDlet.TAG, "Caught NullPointerException, either you're calling FileUtil.loadFileName(name) in the MIDlet constructur or you're just doing it wrong");
		}
	}
	
	public static int getResourceID(String resourceName)
	{
		if(resourceMap == null)
		{
			initResources();
		}
		
		return ((Integer)resourceMap.get(resourceName)).intValue();
	}
	
	public static InputStream getResourceAsStream(String resourceName)
	{
		int id = RawResources.getResourceID(resourceName);
		return MIDlet.current.getResources().openRawResource(id);
	}
}
