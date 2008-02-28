/*
 * Created on Feb 19, 2008 at 8:04:13 PM.
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
package de.enough.polish.util;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import de.enough.polish.ConverterException;

/**
 * 
 * <br>Copyright Enough Software 2005-2007
 * <pre>
 * history
 *        Feb 19, 2008 - rickyn creation
 * </pre>
 * @author Richard Nkrumah
 */
public class XmlUtils {

    public static void outputDocument(OutputStream outputStream, Document dbDocument) throws TransformerFactoryConfigurationError, ConverterException {
        DOMSource domSource = new DOMSource(dbDocument);
        StreamResult streamResult = new StreamResult(outputStream);
        TransformerFactory tf = TransformerFactory.newInstance();
        tf.setAttribute("indent-number", 4);
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
        } catch (TransformerConfigurationException exception) {
            throw new ConverterException("Could not create transformer.",exception);
        }
        transformer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
        transformer.setOutputProperty(OutputKeys.METHOD,"xml");
        transformer.setOutputProperty(OutputKeys.INDENT,"yes");
//        serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
//        serializer.setOutputProperty(OutputKeys.STANDALONE,"yes");
        try {
          transformer.transform(domSource, streamResult);
        } catch (TransformerException exception) {
            throw new ConverterException("Could not write document to output stream",exception);
        }
    }
    
    public static Document getDocumentFromXmlStream(InputStream xmlStream) throws ConverterException {
        Document dbDocument;
        try {
            DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            dbDocument = newDocumentBuilder.parse(xmlStream);
        } catch (Exception exception) {
            throw new ConverterException("Could not create DOM Document",exception);
        }
        return dbDocument;
    }
}
