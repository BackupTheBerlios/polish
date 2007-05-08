/*
 * DisplayableParserManager.java
 * 
 * Created on Mar 27, 2007, 10:45:58 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans;

import de.enough.polish.plugin.netbeans.modelparser.*;
import de.enough.polish.plugin.netbeans.components.items.BrowserCD;
import de.enough.polish.plugin.netbeans.components.items.RssBrowserCD;
import de.enough.polish.resources.ResourcesProvider;
import de.enough.polish.ui.Item;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.midp.components.sources.ListElementEventSourceCD;
import org.netbeans.modules.vmd.midp.components.elements.ChoiceElementCD;
import org.netbeans.modules.vmd.midp.components.items.StringItemCD;
import org.netbeans.modules.vmd.midp.components.items.ImageItemCD;
import org.netbeans.modules.vmd.midp.components.items.ChoiceGroupCD;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.modules.vmd.midp.components.items.TextFieldCD;

/**
 * Manages screen parsers and item parsers and converts NetBeans data models to actual J2ME Polish screens.
 * 
 * @author robertvirkus
 */
public class ItemParserManager {
    
    private final static ItemParserManager INSTANCE = new ItemParserManager();
    private final Map<String, ItemParser> parsersByClassName;
    
    private ItemParserManager() {
        this.parsersByClassName = new HashMap<String, ItemParser>();
        this.parsersByClassName.put(ListElementEventSourceCD.TYPEID.getString (), new ChoiceItemParser() );
        this.parsersByClassName.put(ChoiceElementCD.TYPEID.getString (), new ChoiceItemParser() );
        this.parsersByClassName.put(BrowserCD.TYPEID.getString (), new HtmlBrowserParser() );
        this.parsersByClassName.put(RssBrowserCD.TYPEID.getString (), new RssBrowserParser() );
        this.parsersByClassName.put(StringItemCD.TYPEID.getString (), new StringItemParser() );
        this.parsersByClassName.put(ImageItemCD.TYPEID.getString (), new ImageItemParser() );
        this.parsersByClassName.put(ChoiceGroupCD.TYPEID.getString (), new ChoiceGroupParser() );
        this.parsersByClassName.put(TextFieldCD.TYPEID.getString (), new TextFieldParser() );
    }
    
    public static ItemParserManager getInstance() {
        return INSTANCE;
    }
    
    public static void registerParser( String className, ItemParser parser ) {
        System.out.println("registering " + className );
        INSTANCE.parsersByClassName.put( className, parser );
    }
    
    public Item parseItem( DesignComponent designComponent, ResourcesProvider resourcesProvider ) {
        String type = designComponent.getType().getString();
        ItemParser parser = this.parsersByClassName.get( type );
        if (parser != null) {
            return parser.parse(designComponent, resourcesProvider);
        } else {
            System.out.println("Warning: unable to locate parser for item type " + type );
        }
        return null;
    }

}
