/*
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

package de.enough.polish.example;

import de.enough.polish.geometry2d.Enlarge2D;
import de.enough.polish.geometry2d.Intersection2D;
import de.enough.polish.geometry2d.Line2D;
import de.enough.polish.geometry2d.Point2D;
import de.enough.polish.geometry2d.Polygon2D;
import de.enough.polish.geometry2d.Render2D;
import de.enough.polish.geometry2d.Rotation2D;
import de.enough.polish.geometry2d.Translation2D;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

/**
 * Sample canvas that makes use of the geometry framework.
 *
 * @author Ovidiu Iliescu
 */
public class TestCanvas extends GameCanvas
{

    Polygon2D samplePolygon, work;
    Point2D rotationPoint ;
    Line2D intersectingLine ;
    Vector intersectionPoints = new Vector();

    public TestCanvas(boolean keys)
    {
        super(keys);
        
        // Define a very small sample polygon (only 7 units across).
        // It's a star with one of its "arms" enlarged and pointing upwards.
        // Cartesian coordinates are used. You can draw it on paper to better
        // understand what is going on.
        samplePolygon = new Polygon2D ()
        {{
             points.addElement( new Point2D(0,5));
             points.addElement( new Point2D(1,0));
             points.addElement( new Point2D(3,0));
             points.addElement( new Point2D(1,-1));
             points.addElement( new Point2D(2,-3));
             points.addElement( new Point2D(0,-1));
             points.addElement( new Point2D(-2,-3));
             points.addElement( new Point2D(-1,-1));
             points.addElement( new Point2D(-3,0));
             points.addElement( new Point2D(-1,0));

        }};

        // Clone it for the first call to paint()
        work = samplePolygon.clone();

        // Define the intersecting line
        Point2D p1 = new Point2D(0,0);
        Point2D p2 = new Point2D( getWidth(),getHeight());
        intersectingLine = new Line2D (p1,p2,true);
    }

    public void paint(Graphics g)
    {
        // Paint the background
        g.setColor(255, 255, 255);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw the polygon
        g.setColor(0, 0, 255);
        Render2D.paint(work, g);

        // Draw the intersection line
        g.setColor(255, 0, 0);
        Render2D.paint(intersectingLine, g);

        // Draw the intersection points
        g.setColor(0,0 , 0);
        if ( intersectionPoints != null )
        {
            Enumeration points = intersectionPoints.elements() ;

            Point2D point ;
            while (points.hasMoreElements())
            {
                point = (Point2D) points.nextElement();
                Render2D.paint(point, g, 5);
            }
        }

    }

    public void runTest() {

       double i = 0 ;
       while ( i < 360 )
       {
            // Clone the sample polygon. We will work on the clone.
            work = samplePolygon.clone();
            
            // Enlarge the polygon to a more visible size.
            Enlarge2D.enlarge(work, 10 + ( i / 360 * 40) );

            // Move the polygon around.
            Point2D position = new Point2D ( i / 360 * getWidth(),  i / 360 * getHeight());
            Translation2D.moveTo(work, position);

            // Rotate the polygon around the specified point.
            // Because the screen has 0,0 at the top-left corner,
            // whereas the carthesian system has 0,0 at the "bottom-left" corner,
            // the polygon will appear to be upside-down. To compensate,
            // add another 180 degrees to the rotation.
            rotationPoint = position.clone();
            Translation2D.translate(rotationPoint,  40 , 40);
            Rotation2D.rotate(work, rotationPoint, 180 + i);

            // Calculate the intersection points of the rotated polygon with the
            // intersection line.
            intersectionPoints = Intersection2D.intersection(work, intersectingLine);

            // To test intersection between two polygons, uncomment the line below
            // intersectionPoints = Intersection2D.intersection(work, work);

            
            // Boring stuff. Increment the angle, pause the app, repaint the
            // screen.
            i= i + 0.3;
            sleep();
            repaint();
            serviceRepaints();
       }
    }

    public void sleep()
    {
       // Let's pause the app for a weee bit of time!
       try {
                Thread.sleep(5);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
    }

}
