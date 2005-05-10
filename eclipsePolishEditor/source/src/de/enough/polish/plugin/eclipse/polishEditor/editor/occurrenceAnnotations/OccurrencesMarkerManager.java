/*
 * Created on Apr 18, 2005 at 1:36:19 PM.
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
package de.enough.polish.plugin.eclipse.polishEditor.editor.occurrenceAnnotations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.internal.utils.Assert;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.IAnnotationModel;

import de.enough.polish.plugin.eclipse.polishEditor.editor.IConfiguration;


// Typo verbessern: Manager.
public class OccurrencesMarkerManager{
        
    public static class DefaultConfiguration implements IConfiguration{
        
        private IDocument document;
        private IAnnotationModel annotationModel;
    
        public boolean isConfigured() {
            return this.document != null && this.annotationModel != null;
        }
        
        public void configure(Object objectToConfigure) {
            if( ! (objectToConfigure instanceof OccurrencesMarkerManager)) {
                System.out.println("OccurrencesMarkerManager.DefaultConfiguration.configure(...):can not configure that as it is not of type OccurrencesMarkerManager.");
                return;
            }
            OccurrencesMarkerManager occurrencesMarkerManager = (OccurrencesMarkerManager)objectToConfigure;
            if( ! isConfigured()) {
                System.out.println("OccurrencesMarkerManager.DefaultConfiguration.configure(...):this is not configured.");
                return;
            }
            occurrencesMarkerManager.setAnnotationModel(this.annotationModel);
            occurrencesMarkerManager.setDocument(this.document);
        }
        public IAnnotationModel getAnnotationModel() {
            return this.annotationModel;
        }

        public void setAnnotationModel(IAnnotationModel annotationModel) {
            this.annotationModel = annotationModel;
        }
        public IDocument getDocument() {
            return this.document;
        }

        public void setDocument(IDocument document) {
            this.document = document;
        }
        
    }
    
    private IDocument document;
    private IAnnotationModel annotationModel;
    private List listOfMarkers;
    private AbstractOccurrenceAnnotationMarker.DefaultConfiguration defaultMarkerConfiguration;
    
    public OccurrencesMarkerManager() {
        this.listOfMarkers = new ArrayList();
        //addMarker(new StandardMarker()); // This one should be placed in a init object like a configuration but single shot.
        //addMarker(new DummyOccurrenceAnnotationMarker());
        addMarker(new BlockMarker());
        this.defaultMarkerConfiguration = new AbstractOccurrenceAnnotationMarker.DefaultConfiguration();
    }

    public void updateAnnotations(ITextSelection selection, List listOfComments) {
        Assert.isNotNull(selection);
        Assert.isNotNull(listOfComments);
        
        if( ! isConfigured()) {
            System.out.println("OccurrencesMarkerManager:updateAnnotations(...):this is not configured.");
            return;
        }
        // TODO: editor.removeAnnotations(...) aufrufen irgendwo. Drauf achten, dass man es  nicht zu oft aufruft.
        for(Iterator iterator = this.listOfMarkers.iterator();iterator.hasNext();) {
            IOccurrenceAnnotationMarker aMarker = (IOccurrenceAnnotationMarker) iterator.next();
            aMarker.removeAnnotations();
            aMarker.updateAnnotations(selection);       
        }
    }
    
    // configure the manager and configure all its children.
    public void configure(IConfiguration configuration) {
        configuration.configure(this);
        this.defaultMarkerConfiguration.setAnnotationModel(this.annotationModel);
        this.defaultMarkerConfiguration.setDocument(this.document);
        for(Iterator iterator = this.listOfMarkers.iterator();iterator.hasNext();) {
            AbstractOccurrenceAnnotationMarker marker = (AbstractOccurrenceAnnotationMarker) iterator.next();
            marker.configure(this.defaultMarkerConfiguration);
        }
    }
    
    public boolean isConfigured() {
        return this.document != null && this.annotationModel != null;
    }
    
//  TODO: Rename to removeAnnotationsAndReset().
    public void removeAnnotations() {
        if( ! isConfigured()) {
            System.out.println("OccurrencesMarkerManager.removeAnnotations(...):this is not configured.");
            return;
        }
        for(Iterator iterator = this.listOfMarkers.iterator();iterator.hasNext();) {  
            AbstractOccurrenceAnnotationMarker marker = (AbstractOccurrenceAnnotationMarker) iterator.next();
            marker.removeAnnotations();
        }
    }
    
    public void addMarker(IOccurrenceAnnotationMarker occurrenceAnnotationMarker) {
        	if(occurrenceAnnotationMarker == null){
        	    System.out.println("OccurencesMarkerManager.addMarker(): Parameter is null.");
        	    return;
        	}
        	this.listOfMarkers.add(occurrenceAnnotationMarker);
    }
    
    public void removeMarker(IOccurrenceAnnotationMarker occurrenceAnnotationMarker) {
    		if(occurrenceAnnotationMarker != null){
    		    System.out.println("OccurencesMarkerManager.removeMarker(): Parameter is null.");
    		    return;
    		}
    		this.listOfMarkers.remove(occurrenceAnnotationMarker);   		
    }
    
    public void setDocument(IDocument newDocument) {
        Assert.isNotNull(newDocument);
        this.document = newDocument;
    }
    
    public void setAnnotationModel(IAnnotationModel newAnnotationModel) {
        Assert.isNotNull(newAnnotationModel);
        this.annotationModel = newAnnotationModel;
    }
    
    

    
}
        
    