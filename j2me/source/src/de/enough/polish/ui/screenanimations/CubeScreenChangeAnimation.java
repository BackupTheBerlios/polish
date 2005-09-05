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
		if(this.first){
		 this.graphics3d = Graphics3D.getInstance();
		 this.camera = new Camera();
		 this.camera.setPerspective( 60.0f,
		   (float)getWidth()/ (float)getHeight(),
		   1.0f,
		   100.0f );
		 this.light = new Light();
		 this.light.setColor(0xffffff);
		 this.light.setMode(this.light.SPOT);
		 this.light.setIntensity(1.25f);

		  short[] vert = {
				  5, 5, 5,  -5, 5, 5,   5,-5, 5,  -5,-5, 5, // 0,0,5,   5,0,5,   0,-5,5,  -5,0,5,  0,5,5,//front
				 -5, 5,-5,   5, 5,-5,  -5,-5,-5,   5,-5,-5, //back
//				 -5, 5, 5,  -5, 5,-5,  -5,-5, 5,  -5,-5,-5, //left
//				  5, 5,-5,   5, 5, 5,   5,-5,-5,   5,-5, 5,  //right
//				  5, 5,-5,  -5, 5,-5,   5, 5, 5,  -5, 5, 5,//up
//				  5,-5, 5,  -5,-5, 5,   5,-5,-5,  -5,-5,-5//down
		  };
		  VertexArray vertArray = new VertexArray(vert.length/3 , 3, 2);
		  vertArray.set(0, vert.length/3, vert);
		  int[] indices = { 
//				  			8,1,4,7,
//				  			4,7,6,3,
//				  			0,8,5,4,
//				  			5,4,2,6,
//				  			9,10,11,12
				  			0,1,2,3,
				  			4,5,6,7
	  			}; 
		  int[] stripLen = { 4,4};
		  // The per-vertex normals for the cube
		  byte[] norm = {
				   0, 0, 127,     0, 0, 127,     0, 0, 127,   0, 0, 127, //0, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0, 127,
				   0, 0,-127,     0, 0,-127,     0, 0,-127,   0, 0,-127,  
		  };
		  VertexArray normArray = new VertexArray(norm.length / 3, 3, 1);
		  normArray.set(0, norm.length/3, norm);
//		  // per vertex texture coordinates
// 			must build an Z
		  short[] tex = {
//	                256,0, 0,0, 256,256, 0,256 ,//128,128, 256,128, 128,256, 0,128 ,128,0,
				  256,0,  0,0,   256,256,   0,256,
				  0,0,  0,0, 0,0,0,0,    			 
				  };
		  VertexArray texArray = new VertexArray(tex.length / 2, 2, 2);
		  texArray.set(0, tex.length/2, tex);
		  short[] lsttex = {              
				  	0,0,  0,0,   0,0,   0,0,	//0,0, 0,0, 0,0, 0,0, 0,0,
	                256,0,  0,0,   256,256,   0,256,	
	                };

		  VertexArray lsttexArray = new VertexArray(tex.length / 2, 2, 2);
		  lsttexArray.set(0,tex.length/2, lsttex);
		  
		  

		  // the VertexBuffer for our object
		  VertexBuffer vb = this.vbuffer = new VertexBuffer();
		  vb.setPositions(vertArray,1.0f, null);
		  vb.setNormals(normArray);
		  vb.setTexCoords(0, texArray, 1.0f/256.0f, null);
//		  vb.setTexCoords(1, lsttexArray,(1.0f/256.0f),null);
		  this.indexbuffer = new TriangleStripArray( indices, stripLen );
		  int[] rgbData = new int[256*256];
		  int[] lstrgbData = new int[256*256];
		  int[] rgbbuffer = new int [nxtScreenImage.getWidth() * nxtScreenImage.getHeight()];
		  int[] lstrgbbuffer = new int [lstScreenImage.getWidth() * lstScreenImage.getHeight()];
		  nxtScreenImage.getRGB(rgbbuffer, 0, width, 0, 0, width, height );
		  lstScreenImage.getRGB(lstrgbbuffer, 0, width, 0, 0, width, height );
		  int row = 0,rgbCount=0;
		  for(int i = 0; i < rgbData.length;i++){
			  if(row < width && rgbCount < rgbbuffer.length){
				  rgbData[i] = rgbbuffer[rgbCount];
				  rgbCount ++;
			  }
			  row = (row + 1) % 256;		  
		  }
		  rgbCount = 0;row = 0;
		  for(int i = 0; i < lstrgbData.length;i++){
			  if(row < width && rgbCount < lstrgbbuffer.length){
				  lstrgbData[i] = lstrgbbuffer[rgbCount];
				  rgbCount ++;
			  }
			  row = (row + 1) % 256;		  
		  }
		  
		  // the image for the texture
		  Image image = Image.createRGBImage(rgbData,256,256,false);
//		  Image lstImage = Image.createRGBImage(lstrgbData,256,256,false);
		  
//		  Image2D lstImage2D = new Image2D (Image2D.RGB,lstImage);
		  Image2D image2D = new Image2D( Image2D.RGB, image );
		  
//		  Texture2D lsttexture = new Texture2D( lstImage2D );
		  Texture2D texture = new Texture2D( image2D );
		  
		  texture.setFiltering(Texture2D.FILTER_NEAREST,
		        Texture2D.FILTER_NEAREST);
		  texture.setWrapping(Texture2D.WRAP_CLAMP,
		       Texture2D.WRAP_CLAMP);
		  texture.setBlending(Texture2D.FUNC_MODULATE);
		  // create the appearance
		  this.appearance = new Appearance();	
		  this.appearance.setTexture(0,texture);  
//		  this.appearance.setTexture(1,lsttexture );	
		 
		  this.appearance.setMaterial(this.material);
//		  this.material.setVertexColorTrackingEnable(true);
		  this.material.setColor(Material.DIFFUSE, 0xFFFFFFcc);
		  this.material.setColor(Material.SPECULAR, 0xFFFFFFcc);
		  this.material.setShininess(100.0f);
		  this.background.setColor(0xFFFFFFcc);
		  this.first = false;
		
		  super.show(style, dsplay, width, height, lstScreenImage, nxtScreenImage, nxtCanvas, nxtDisplayable );
		}
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
			  transform.postRotate(this.angle,1.0f,this.angle, 1.0f);
			  transform.postTranslate(0.0f,0.0f,0.0f);
			  this.graphics3d.render(this.vbuffer, this.indexbuffer, this.appearance, transform);
			  this.graphics3d.releaseTarget();
			  this.display.callSerially( this );

	}

}