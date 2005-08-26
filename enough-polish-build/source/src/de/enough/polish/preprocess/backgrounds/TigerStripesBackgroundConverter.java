package de.enough.polish.preprocess.backgrounds;

import java.util.HashMap;

import org.apache.tools.ant.BuildException;

import de.enough.polish.preprocess.BackgroundConverter;
import de.enough.polish.preprocess.Style;
import de.enough.polish.preprocess.StyleSheet;

public class TigerStripesBackgroundConverter extends BackgroundConverter {

	protected String createNewStatement(HashMap map, Style style, StyleSheet styleSheet) 
	throws BuildException 
	{
		String result = "new de.enough.polish.ui.backgrounds.TigerStripesBackground(" 
			+ this.color + ", ";
		String stripesColor = "0";
		String stripesColorStr = (String) map.get("stripes-color");
		if ( stripesColorStr != null ) {
			stripesColor = parseColor( stripesColorStr );			
		}
		int stripesNumber = 6;
		String stripesNumberStr =  (String)map.get("number");
		if ( stripesNumberStr != null ) {
			stripesNumber =  parseInt(stripesNumberStr, stripesNumberStr);			
		}
		result += stripesColor + ","+stripesNumber+")";
		
		return result;
	}
	

}
