//#condition polish.usePolishGui && polish.api.3dapi
/*
 * Created on 26.08.2005 at 10:33:18.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package de.enough.polish.ui.screenanimations;

import java.io.IOException;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.m3g.*;
//import javax.microedition.lcdui.;

import de.enough.polish.ui.AccessibleCanvas;
import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;



public class CubeScreenChangeAnimation extends ScreenChangeAnimation {
	private Graphics3D  graphics3d;
	private Camera   camera;
	private Light   light;
	private float   angle = 0.0f;
	private Transform  transform = new Transform();
	private Background  background = new Background();
	private VertexBuffer vbuffer;
	private IndexBuffer  indexbuffer;
	private Appearance  appearance;
	private Material  material = new Material();
	private Image   image;
	private boolean first = true;
	public CubeScreenChangeAnimation() {
		super();
		// TODO Auto-generated constructor stub
	}
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#show(de.enough.polish.ui.Style, javax.microedition.lcdui.Display, int, int, javax.microedition.lcdui.Image, javax.microedition.lcdui.Image, de.enough.polish.ui.Screen)
	 */
	protected void show(Style style, Display dsplay, int width, int height,
			Image lstScreenImage, Image nxtScreenImage, AccessibleCanvas nxtCanvas, Displayable nxtDisplayable  ) 
	{
		 System.out.print("3DCube");

		 this.graphics3d = Graphics3D.getInstance();
		 System.out.print("3DCube2");
		 this.camera = new Camera();
		 this.camera.setPerspective( 60.0f,
		   (float)getWidth()/ (float)getHeight(),
		   1.0f,
		   1000.0f );
		 System.out.print("3DCube3");
		 this.light = new Light();
		 this.light.setColor(0xffffff);
		 this.light.setIntensity(1.25f);

		  short[] vert = {
		   5, 5, 5, -5, 5, 5, 5,-5, 5, -5,-5, 5,
		   -5, 5,-5, 5, 5,-5, -5,-5,-5, 5,-5,-5,
		   -5, 5, 5, -5, 5,-5, -5,-5, 5, -5,-5,-5,
		   5, 5,-5, 5, 5, 5, 5,-5,-5, 5,-5, 5,
		   5, 5,-5, -5, 5,-5, 5, 5, 5, -5, 5, 5,
		   5,-5, 5, -5,-5, 5, 5,-5,-5, -5,-5,-5 };

		  VertexArray vertArray = new VertexArray(vert.length / 3, 3, 2);
		  vertArray.set(0, vert.length/3, vert);

		  // The per-vertex normals for the cube
		  byte[] norm = {
		   0, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0, 127,
		   0, 0,-127, 0, 0,-127, 0, 0,-127, 0, 0,-127,
		   -127, 0, 0, -127, 0, 0, -127, 0, 0, -127, 0, 0,
		   127, 0, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0,
		   0, 127, 0, 0, 127, 0, 0, 127, 0, 0, 127, 0,
		   0,-127, 0, 0,-127, 0, 0,-127, 0, 0,-127, 0 };

		  VertexArray normArray = new VertexArray(norm.length / 3, 3, 1);
		  normArray.set(0, norm.length/3, norm);
		  System.out.print("3DCube4");
		  // per vertex texture coordinates
		  short[] tex = {
		   1, 0,  0, 0,  1, 1,  0, 1,
		   1, 0,  0, 0,  1, 1,  0, 1,
		   1, 0,  0, 0,  1, 1,  0, 1,
		   1, 0,  0, 0,  1, 1,  0, 1,
		   1, 0,  0, 0,  1, 1,  0, 1,
		   1, 0,  0, 0,  1, 1,  0, 1 };

		  VertexArray texArray = new VertexArray(tex.length / 2, 2, 2);
		  texArray.set(0, tex.length/2, tex);
		  System.out.print("3DCube5");
		  int[] stripLen = { 4, 4, 4, 4, 4, 4 };

		  // the VertexBuffer for our object
		  VertexBuffer vb = this.vbuffer = new VertexBuffer();
		  vb.setPositions(vertArray, 1.0f, null);
		  vb.setNormals(normArray);
		  vb.setTexCoords(0, texArray, 1.0f, null);
		  System.out.print("3DCube6");
		  this.indexbuffer = new TriangleStripArray( 0, stripLen );

		  // the image for the texture
		  
		  System.out.print("3DCube6.25");
		  try {
			image = Image.createImage("/tex.png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  System.out.print("3DCube6.35;;;;;");
		  Image2D image2D = new Image2D( Image2D.RGB, image );
		  System.out.print("3DCube6.5");
		  Texture2D texture = new Texture2D( image2D );
		  System.out.print("3DCube6.75");
		  texture.setFiltering(Texture2D.FILTER_NEAREST,
		        Texture2D.FILTER_NEAREST);
		  System.out.print("3DCube8.5");
		  texture.setWrapping(Texture2D.WRAP_CLAMP,
		       Texture2D.WRAP_CLAMP);
		  texture.setBlending(Texture2D.FUNC_MODULATE);
		  System.out.print("3DCube7");
		  // create the appearance
		  this.appearance = new Appearance();
//		  this.appearance.setTexture(0, texture);
		  this.appearance.setMaterial(this.material);
		  this.material.setColor(Material.DIFFUSE, 0xFFFFFFFF);
		  this.material.setColor(Material.SPECULAR, 0xFFFFFFFF);
		  this.material.setShininess(100.0f);
		  System.out.print("3DCube8");
		  this.background.setColor(0xffffcc);
		  this.first = false;
		  System.out.print("3DCubeEnd");
		
	}
	
	
	protected boolean animate() {
		
		// TODO Auto-generated method stub
		return true;
	}

	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		  this.graphics3d.bindTarget(g, true,
			      Graphics3D.DITHER |
			      Graphics3D.TRUE_COLOR);
		  this.graphics3d.clear(this.background);

			  // Set the camera
			  Transform transform = new Transform();
			  transform.postTranslate(0.0f, 0.0f, 30.0f);
			  this.graphics3d.setCamera(this.camera, transform);

			  // Set light
			  this.graphics3d.resetLights();
			  this.graphics3d.addLight(this.light, transform);

			  // see rotation
			  this.angle += 1.0f;
			  transform.setIdentity();
			  transform.postRotate(this.angle, 1.0f, 1.0f, 1.0f);

			  this.graphics3d.render(this.vbuffer, this.indexbuffer, this.appearance, transform);
			  this.graphics3d.releaseTarget();
	}

}
