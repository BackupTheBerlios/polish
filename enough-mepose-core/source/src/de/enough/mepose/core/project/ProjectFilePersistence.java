package de.enough.mepose.core.project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.tools.ant.filters.StringInputStream;
import org.eclipse.core.internal.resources.XMLWriter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ProjectFilePersistence
  implements IProjectPersistence
{
  private static final String TAG_MEPOSEPROJECT = "meposeproject";

  private static final String TAG_MEPOSEPROPERTY = "meposeproperty";

  private static final String MEPOSE_FILENAME = ".mepose";

  private static final String MEPOSE_ENCODING = "UTF8";
  
  public Map getMapFromProject(IProject project) throws CoreException
  {
    String data = null;
    IFile file = project.getFile(MEPOSE_FILENAME);
    
    if (file.exists())
    {
      InputStream in = file.getContents(true);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      try
      {
        int bytesRead;
        byte[] buffer = new byte[1024];
      
        do
        {
          bytesRead = in.read(buffer);
        
          if (bytesRead > 0)
          {
            baos.write(buffer, 0, bytesRead);
          }
        }
        while (bytesRead >= 0);
      }
      catch (IOException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
      byte[] value = baos.toByteArray();

      try
      {
        data = new String(value, MEPOSE_ENCODING);
      }
      catch (UnsupportedEncodingException e)
      {
        data = new String(value);
      }
      
      return decodeMap(data);
    }
    else
    {
      return Collections.EMPTY_MAP;
    }
  }

  public void putMapInProject(Map map, IProject project) throws CoreException
  {
    IFile file = project.getFile(MEPOSE_FILENAME);
    String data = encodeMap(map);
    byte[] value;
    
    try
    {
      value = data.getBytes(MEPOSE_ENCODING);
    }
    catch(UnsupportedEncodingException e)
    {
      // TODO: log this.
      
      value = data.getBytes();
    }
    
    ByteArrayInputStream bais = new ByteArrayInputStream(value);
    
    if (file.exists())
    {
      file.setContents(bais, IResource.FORCE, null);
    }
    else
    {
      file.create(bais, IResource.FORCE, null);
    }
  }
  
  private Map decodeMap(String data)
    throws CoreException
  {
    HashMap map = new HashMap();
    
    try
    {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document document = builder.parse(new StringInputStream(data));
      
      Element rootElement = document.getDocumentElement();
      NodeList nodes = rootElement.getElementsByTagName(TAG_MEPOSEPROPERTY);
      
      for (int i = 0; i < nodes.getLength(); i++)
      {
        Element element = (Element) nodes.item(i);
        String name = element.getAttribute("name");
        String value = element.getAttribute("value");
        
        if (name != null && value != null)
        {
          IStringVariableManager stringVariableManager =
            VariablesPlugin.getDefault().getStringVariableManager();
          String newValue = stringVariableManager.performStringSubstitution(value);
          
          System.out.println("Michael: name=" + name + ", value=" + value);
          System.out.println("Michael: substituted value=" + newValue);
          
          map.put(name, value);
        }
      }
    }
    catch (SAXException e)
    {
      return Collections.EMPTY_MAP;
    }
    catch (IOException e)
    {
      return Collections.EMPTY_MAP;
    }
    catch (ParserConfigurationException e)
    {
      return Collections.EMPTY_MAP;
    }

    return map;
  }

  private String encodeMap(Map map)
  {
    try
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos, MEPOSE_ENCODING));
      Iterator it = map.entrySet().iterator();
      
      writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      writer.println("<" + TAG_MEPOSEPROJECT + ">");

      while (it.hasNext())
      {
        Map.Entry entry = (Map.Entry) it.next();

        writer.println("\t<" + TAG_MEPOSEPROPERTY + " name=\"" + entry.getKey() + "\" value=\"" + entry.getValue() + "\" />");
      }
      
      writer.println("</" + TAG_MEPOSEPROJECT + ">");
      writer.flush();
      return baos.toString(MEPOSE_ENCODING);
    }
    catch (IOException e)
    {
      return null;
    }
  }
}
