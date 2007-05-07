/*
 * DisplayableParserManager.java
 * 
 * Created on Mar 27, 2007, 10:45:58 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans;

import de.enough.polish.plugin.netbeans.modelparser.AlertParser;
import de.enough.polish.plugin.netbeans.modelparser.DisplayableParser;
import de.enough.polish.plugin.netbeans.modelparser.FormParser;
import de.enough.polish.plugin.netbeans.modelparser.ListParser;
import de.enough.polish.plugin.netbeans.modelparser.TextBoxParser;
import de.enough.polish.resources.ResourcesProvider;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.midp.components.displayables.TextBoxCD;
import org.netbeans.modules.vmd.midp.components.displayables.ListCD;
import org.netbeans.modules.vmd.midp.components.displayables.FormCD;

import javax.microedition.lcdui.Displayable;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.modules.vmd.midp.components.displayables.AlertCD;

/**
 * Manages screen parsers and item parsers and converts NetBeans data models to actual J2ME Polish screens.
 * 
 * @author robertvirkus
 */
public class DisplayableParserManager {
    
    private final static DisplayableParserManager INSTANCE = new DisplayableParserManager();
    private final Map<String, DisplayableParser> parsersByClassName;
    
    private DisplayableParserManager() {
        this.parsersByClassName = new HashMap<String, DisplayableParser>();
        this.parsersByClassName.put(TextBoxCD.TYPEID.getString (), new TextBoxParser() );
        this.parsersByClassName.put(ListCD.TYPEID.getString (), new ListParser() );
        this.parsersByClassName.put(FormCD.TYPEID.getString (), new FormParser() );
        this.parsersByClassName.put(AlertCD.TYPEID.getString (), new AlertParser() );
    }
    
    public static DisplayableParserManager getInstance() {
        return INSTANCE;
    }
    
    public static void registerParser( String className, DisplayableParser parser ) {
        System.out.println("registering " + className );
        INSTANCE.parsersByClassName.put( className, parser );
    }
    
    public Displayable parseDisplayable( DesignComponent designComponent, ResourcesProvider resourcesProvider  ) {
        //        displayable.getComponentDescriptor().getTypeDescriptor().getSuperType();
//        displayable.getDocument().getDescriptorRegistry().getComponentDescriptor(componentType);
//        disayable.getComponentDescriptor().getPropertyDescriptors();
        String type = designComponent.getType().getString();
        DisplayableParser parser = this.parsersByClassName.get( type );
        if (parser != null) {
            return parser.parse(designComponent, resourcesProvider);
        } else {
            System.out.println("Warning: unable to locate parser for type " + type );
        }
       

//        DataObjectContext context = ProjectUtils.getDataObjectContextForDocument(displayable.getDocument());
//        Project project = ProjectUtils.getProject(context);
//        FileObject projectRoot = project.getProjectDirectory();
//        FileObject object = projectRoot.getFileObject("resources/polish.css");
//        object.getInputStream();
        
        // see http://wiki.netbeans.org/wiki/view/MobilityDesigner2MIDP
        //  http://wiki.netbeans.org/wiki/view/MobilityDesignerHome
        // for a description of all MIDP components, document structure that you can crawl
        return null;
    }

}
