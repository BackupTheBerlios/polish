package de.enough.ovidiu;

import de.enough.polish.util.ArrayList;

public class FloatBoxManager {
	
	public static final int FLOAT_RIGHT = 0;
	public static final int FLOAT_LEFT = 1;
	
	class Float
	{
		int width;
		int height;
		int x;
		int y;
		int type;
	}
	
	ArrayList floats = new ArrayList();
	
	public void addFloat(int x, int y, int width, int height, int type)
	{
		Float f = new Float();
		f.width = width;
		f.height = height;
		f.x = x;
		f.y = y;
		f.type = type;
		floats.add(f);
	}
	
	public int leftIntersectionPoint(int x, int y, int length)
	{		
		int i = 0;
		int endX = x + length;
		int count = floats.size();
		Float temp;
		int minX = 0;
		int tempLength = 0;
		
		while ( i < count )
		{
			temp = (Float) floats.get(i);
			if ( (temp.y<=y) && (temp.y+temp.height>y) &&
				  ( ( (temp.x>=x) && ( temp.x <= endX) ) || ( (temp.x+temp.width>x) && ( temp.x + temp.width< endX) ) ) && 
			      ( temp.type == FLOAT_LEFT))
			{
				tempLength = temp.x+temp.width - x;
				if ( tempLength > minX )
				{
					minX = tempLength ;
				}
			}
			i++;
		}
		
		return minX;
	}
	
	public int rightIntersectionPoint(int x, int y, int length)
	{
		int i = 0;
		int endX = x + length;
		int count = floats.size();
		Float temp;
		int maxX = 0;
		int tempLength = 0;
		int leftPoint = x + length ;
		while ( i < count )
		{
			temp = (Float) floats.get(i);
			if ( (temp.y<=y) && (temp.y+temp.height>y) &&
					  ( ( (temp.x>=x) && ( temp.x <= endX) ) || ( (temp.x+temp.width>x) && ( temp.x + temp.width< endX) ) ) && 
				      ( temp.type == FLOAT_RIGHT))
			{
				tempLength = leftPoint - temp.x ;
				if ( tempLength > maxX )					
				{
					maxX = tempLength ;
					break;
				}
			}
			i++;
		}
		return length - maxX;
	}
	
	

}
