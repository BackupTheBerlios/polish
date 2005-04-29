/*
 * Created on Apr 20, 2005 at 3:15:08 PM.
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
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;

import de.enough.polish.plugin.eclipse.polishEditor.editor.IConfiguration;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Apr 20, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public abstract class AbstractOccurrenceAnnotationMarker implements IOccurrenceAnnotationMarker{

    public static class DefaultConfiguration implements IConfiguration{
        
        private IDocument document;
        private IAnnotationModel annotationModel;

        public void configure(Object objectToConfigure) {
            if( ! (objectToConfigure instanceof AbstractOccurrenceAnnotationMarker)) {
                System.out.println("AbstractOccurrenceAnnotationMarker.DefaultConfiguration.configure(...):parameter is not of type AbstractOccurrenceAnnotationMarker.");
                return;
            }
            AbstractOccurrenceAnnotationMarker occurrenceAnnotationMarker = (AbstractOccurrenceAnnotationMarker)objectToConfigure;
            if( ! isConfigured()) {
                System.out.println("AbstractOccurrenceAnnotationMarker.DefaultConfiguration.configure(...):can not configure that. this is not configured neither.");
                return;
            }
            
            occurrenceAnnotationMarker.setDocument(this.document);
            occurrenceAnnotationMarker.setAnnotationModel(this.annotationModel);
        }
        
        public boolean isConfigured() {
            if(this.document != null && this.annotationModel != null) {
                return true;
            }
            return false;
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
    
    private List currentAnnotations;
    private IDocument document;
    private IAnnotationModel annotationModel;

    public AbstractOccurrenceAnnotationMarker() {
        this.currentAnnotations = new ArrayList();
        //The other fields are configured through configure(...).
    }
    
    public void setDocument(IDocument newDocument) {
        Assert.isNotNull(newDocument);
        this.document= newDocument;
    }
    
    public void setAnnotationModel(IAnnotationModel newAnnotationModel) {
        Assert.isNotNull(newAnnotationModel);
        this.annotationModel = newAnnotationModel;
    }
    
    public void configure(IConfiguration configuration) {
        configuration.configure(this);
    }
    
    public boolean isConfigured() {
        return this.document != null & this.annotationModel != null && this.currentAnnotations != null;
    }
    
   public IDocument getDocument() {
       return this.document;
   }
    
    public IAnnotationModel getAnnotationModel() {
        return this.annotationModel;
    }

    public List getCurrentAnnotations() {
        return this.currentAnnotations;
    }
    
//    public  boolean ableToAnnotate(ITextSelection selection) {
//        if( ! isConfigured()) {
//            System.out.println("AbstractOccurrencesMarker.ableToAnnotate():this is not configured.");
//            return false;
//        }
//        return true;
//    }

    
    public  void updateAnnotations(ITextSelection selection) {
        if(! isConfigured()) {
            System.out.println("AbstractOccurrencesMarker.ableToAnnotate():this is not configured.");
            return;
        }
    }

    public void addAnnotation(Annotation annotation) {
        this.currentAnnotations.add(annotation);
    }
    
    public void removeAnnotations() {
        if( ! isConfigured()) {
            System.out.println("AbstractOccurrencesMarker.removeAnnotations():this is not configured.");
            return;
        }
        for (Iterator it= this.currentAnnotations.iterator(); it.hasNext();) {
			Annotation annotation= (Annotation) it.next();
			this.annotationModel.removeAnnotation(annotation);
		}
		this.currentAnnotations.clear();   
    }
}
