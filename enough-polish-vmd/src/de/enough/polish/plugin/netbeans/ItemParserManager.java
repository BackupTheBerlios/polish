/*
 * DisplayableParserManager.java
 * 
 * Created on Mar 27, 2007, 10:45:58 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans;

import de.enough.polish.plugin.netbeans.modelparser.ChoiceGroupParser;
import de.enough.polish.plugin.netbeans.modelparser.ChoiceItemParser;
import de.enough.polish.plugin.netbeans.modelparser.HtmlBrowserParser;
import de.enough.polish.plugin.netbeans.modelparser.ImageItemParser;
import de.enough.polish.plugin.netbeans.modelparser.ItemParser;
import de.enough.polish.plugin.netbeans.modelparser.StringItemParser;
import de.enough.polish.ui.Item;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.modules.vmd.api.model.DesignComponent;

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
        this.parsersByClassName.put("#ListElementEventSource", new ChoiceItemParser() );
        this.parsersByClassName.put("#ChoiceElement", new ChoiceItemParser() );
        this.parsersByClassName.put("de.enough.polish.browser.html.HtmlBrowser", new HtmlBrowserParser() );
        this.parsersByClassName.put("javax.microedition.lcdui.StringItem", new StringItemParser() );
        this.parsersByClassName.put("javax.microedition.lcdui.ImageItem", new ImageItemParser() );
        this.parsersByClassName.put("javax.microedition.lcdui.ChoiceGroup", new ChoiceGroupParser() );
    }
    
    public static ItemParserManager getInstance() {
        return INSTANCE;
    }
    
    public static void registerParser( String className, ItemParser parser ) {
        System.out.println("registering " + className );
        INSTANCE.parsersByClassName.put( className, parser );
    }
    
    public Item parseItem( DesignComponent designComponent ) {
        String type = designComponent.getType().getString();
        ItemParser parser = this.parsersByClassName.get( type );
        if (parser != null) {
            return parser.parse(designComponent);
        } else {
            System.out.println("Warning: unable to locate parser for item type " + type );
        }
        return null;
    }

}
