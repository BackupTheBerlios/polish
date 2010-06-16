package de.enough.ovidiu;

import de.enough.polish.util.ArrayList;

public class FloatBoxManager {
	
	public static final int FLOAT_RIGHT = 0;
	public static final int FLOAT_LEFT = 1;
	
	public class Float
	{
		int width;
		int height;
		int x;
		int y;
		int type;
                Box box;
	}
	
	ArrayList floats = new ArrayList();
	
	public Float addFloat(int x, int y, int width, int height, int type, Box box)
	{
		Float f = new Float();
		f.width = width;
		f.height = height;
		f.x = x;
		f.y = y;
		f.type = type;
                f.box = box;
		floats.add(f);
                System.out.println("FLOAT : " + x + " " + y + " " + width + " " + height + " " + type  );
                return f;

	}
	
	public int leftIntersectionPoint(int x, int y, int length, Box box)
	{		
		int i = 0;
		int endX = x + length;
		int count = floats.size();
		Float temp;
		int minX = 0;
		int tempLength = 0;

                if ( box.isInline )
                {
                    box = box.parent ;
                }
		
		while ( i < count )
		{
			temp = (Float) floats.get(i);
			if ( (temp.y<=y) && (temp.y+temp.height>y) &&
				  ( ( (temp.x>=x) && ( temp.x <= endX) ) || ( (temp.x+temp.width>x) && ( temp.x + temp.width< endX) ) ) && 
			      ( temp.type == FLOAT_LEFT) &&
                              (temp.box.parent == box)
                              )
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
	
	public int rightIntersectionPoint(int x, int y, int length, Box box)
	{
		int i = 0;
		int endX = x + length;
		int count = floats.size();
		Float temp;
		int maxX = 0;
		int tempLength = 0;
		int leftPoint = x + length ;

                if ( box.isInline )
                {
                    box = box.parent ;
                }

		while ( i < count )
		{
			temp = (Float) floats.get(i);
			if ( (temp.y<=y) && (temp.y+temp.height>y) &&
					  ( ( (temp.x>=x) && ( temp.x <= endX) ) || ( (temp.x+temp.width>x) && ( temp.x + temp.width< endX) ) ) && 
				      ( temp.type == FLOAT_RIGHT) &&
                                      (temp.box.parent == box)
                                      )
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

        public void removeFloatsForBox(Box b)
        {
            // Remove float for current box (if any)
            int i = 0;
            int size = floats.size();
            while ( i < size )
            {
                if ( ( (Float) floats.get(i) ).box == b )
                {
                    floats.remove(i);
                    size--;
                    continue;
                }
                i++;
            }

            i = 0;
            size = b.children.size();
            while ( i < size )
            {
                removeFloatsForBox( (Box) b.children.get(i) );
                i++;
            }
        }

        public boolean lineOverlapsAnyFloat(int x, int y, int length)
        {
                int i = 0;
		int endX = x + length;
		int count = floats.size();
		Float temp;


		while ( i < count )
		{
			temp = (Float) floats.get(i);
			if ( (temp.y<=y) && (temp.y+temp.height>y) &&
					  ( ( (temp.x>=x) && ( temp.x <= endX) ) || ( (temp.x+temp.width>x) && ( temp.x + temp.width< endX) ) )

                                      )
			{
				return true;
			}
			i++;
		}
		return false;
        }
	
	

}
