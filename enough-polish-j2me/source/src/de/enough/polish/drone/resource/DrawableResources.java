package de.enough.polish.drone.resource;

import java.io.InputStream;
import java.util.HashMap;

import android.content.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import de.enough.polish.drone.midlet.MIDlet;

public class DrawableResources {

	/**
	 *	The map to store the resource name 
	 */
	private static HashMap resourceMap;
	private static Resources resources;
	
	/**
	 *	Get the resource path to cut it off the incoming resourcename 
	 */
	private static final String resourcePath = "/res/drawable"; 
	
	private static void initResources()
	{
		resourceMap = new HashMap();
		resources = MIDlet.current.getResources();
		
		try
		{	
//#=		for(int id=${polish.android.R}.drawable._; id<${polish.android.R}.layout.main; id++)
//#=			{
//#=				String name = resources.getString(id);
//#=			if(id != ${polish.android.R}.drawable._)
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
	
	public static Bitmap getResourceAsBitmap(String resourceName)
	{
		int id = getResourceID(resourceName);
		
		return BitmapFactory.decodeResource(resources, id);
	}
}
