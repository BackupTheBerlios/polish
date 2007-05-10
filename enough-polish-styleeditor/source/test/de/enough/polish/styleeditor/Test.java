/*
 * Created on Apr 23, 2007 at 11:55:03 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.styleeditor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.browser.rss.RssBrowser;
import de.enough.polish.devices.DeviceDatabase;
import de.enough.polish.preprocess.css.CssAttributesManager;
import de.enough.polish.resources.ColorProvider;
import de.enough.polish.resources.ResourcesProvider;
import de.enough.polish.resources.StyleProvider;
import de.enough.polish.resources.impl.ResourcesProviderImpl;
import de.enough.polish.resources.swing.ColorSelectionListener;
import de.enough.polish.resources.swing.SpecifyOrSelectEntryDialog;
import de.enough.polish.resources.swing.ResourcesTree;
import de.enough.polish.resources.swing.StyleSelectionListener;
import de.enough.polish.runtime.SelectionListener;
import de.enough.polish.runtime.Simulation;
import de.enough.polish.runtime.SimulationDevice;
import de.enough.polish.runtime.SimulationPanel;
import de.enough.polish.runtime.overlays.HoverOverlay;
import de.enough.polish.runtime.overlays.SelectionOverlay;
import de.enough.polish.styleeditor.editors.MarginPaddingEditor;
import de.enough.polish.styleeditor.swing.SwingStyleEditor;
import de.enough.polish.styleeditor.swing.components.ColorChooserComponent;
import de.enough.polish.styleeditor.swing.components.ColorChooserListener;
import de.enough.polish.styleeditor.util.StyleEditorUtil;
import de.enough.polish.swing.SwingApplication;
import de.enough.polish.ui.Background;
import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.ui.backgrounds.SimpleBackground;
import de.enough.polish.util.ResourceUtil;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 23, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Test extends SwingApplication
implements SelectionListener, StyleSelectionListener, ColorSelectionListener, ColorChooserListener,
ActionListener
{

	private StyleEditor editor;
	private Simulation simulation;
	private ResourcesProvider resourceProvider;
	private ResourcesTree resourcesTree;
	private JComboBox deviceSelector;

	/**
	 * @param title
	 * @param systemExitOnQuit
	 */
	public Test(String title, boolean systemExitOnQuit, File polishHome, File projectHome ) {
		super(title, systemExitOnQuit);
		
		this.deviceSelector = new JComboBox( new String[]{"Generic/MppPhone", "Generic/multi", "Nokia/6630"} );
		this.deviceSelector.addActionListener( this );

		DeviceDatabase deviceDB = DeviceDatabase.getInstance(polishHome);
		ResourceUtil resourceUtil = new ResourceUtil( getClass().getClassLoader() );
		CssAttributesManager attributesManager = CssAttributesManager.getInstance( polishHome, resourceUtil );
		Environment environment = new Environment(polishHome);
		environment.setBaseDir( projectHome );
		environment.initialize( deviceDB.getDevice("Generic/MppPhone"), null);

		this.resourceProvider = initResourceProvider( polishHome, environment, attributesManager );
				
		this.simulation  = new Simulation( new SimulationDevice() );
		Displayable form = initUi();
		initSimulation( this.simulation, form ); 
		
		this.editor = new StyleEditor( this.resourceProvider, attributesManager, environment );
		SwingStyleEditor swingEditor = new SwingStyleEditor(this.editor );
		this.editor.setVisual(swingEditor);
		this.editor.addDefaultPartEditors();
		ResourcesTree tree = ResourcesTree.getInstance( this.resourceProvider );
		tree.addStyleSelectionListener(this);
		tree.addColorSelectionListener( this );
		this.resourcesTree = tree;
		this.editor.addStyleListener( new MyStyleEditorListener( tree, this.simulation ) );
		
		JPanel panel = new JPanel( new BorderLayout() );
		//panel.add( this.simulation, BorderLayout.NORTH );
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new SimulationPanel( this.simulation ), new JScrollPane( tree ) );
		panel.add( splitPane, BorderLayout.SOUTH );
		panel.add( swingEditor, BorderLayout.CENTER );
		Container contentPane = getContentPane();
		contentPane.setLayout( new BorderLayout() );
		//contentPane.add( new JScrollPane(tree), BorderLayout.EAST );
		contentPane.add( panel, BorderLayout.CENTER );
		contentPane.add( this.deviceSelector, BorderLayout.NORTH );
		pack();
		
		// set style:
		//notifyScreenSelected( (Screen)form, UiAccess.getStyle( (Screen)form), 0, 0 );
	}
	
	/**
	 * @param polishHome
	 * @param environment 
	 * @param manager
	 * @return
	 */
	private ResourcesProvider initResourceProvider(File polishHome, Environment environment, CssAttributesManager manager) {
		try {
			ResourcesProvider provider = new ResourcesProviderImpl(polishHome, environment, manager);
//			StyleProvider[] styles = provider.getStyles();
//			for (int i = 0; i < styles.length; i++) {
//				StyleProvider style = styles[i];
//				System.out.println("\n===============================");
//				printStyle(style);
//			}
			return provider;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException("Unable to init ResourcesProvider: " +  e.toString());
		}
	}
	
	private void printStyle( StyleProvider styleProvider ) {
		Style style = styleProvider.getStyle();
		System.out.println(style.name + ".padding=" +  style.paddingLeft);
		if (style.font != null) {
			System.out.println(style.name + ".font.style=" +  style.font.getStyle());
			System.out.println(style.name + ".font.size=" +  style.font.getSize());
			System.out.println(style.name + ".font.face=" +  style.font.getFace());
		}
		System.out.println(style.name + ".font.layout=" +  style.layout);
		System.out.println(style.name + ".background=" +  style.background);
		System.out.println(style.name + ".border=" +  style.border);
		
	}

	/**
	 * @param form
	 */
	private void initSimulation(Simulation sim, Displayable form) {
		sim.setSelectionMode( Simulation.SELECTION_MODE_PASSIVE );
		sim.setCurrent( form );
		sim.addOverlay( new SelectionOverlay() );
		sim.addOverlay( new HoverOverlay() );
		sim.addSelectionListener( this );
	}

	/**
	 * 
	 */
	private Displayable initUi() {
		Form form = new Form("Hello World");
		form.append("first line");
		Style style = new Style();
		style.layout = Item.LAYOUT_RIGHT;
		form.append("second line", style );
		form.append("third line");
		form.append("fourth line");
		ChoiceGroup group = new ChoiceGroup( "choice:", ChoiceGroup.MULTIPLE );
		group.append( "first choice", null );
		group.append( "second choice", null );
		group.append( "third choice", null );
		group.append( "fourth choice", null );
		form.append( group );
		RssBrowser browser = new RssBrowser();
		browser.go(  "http://www.digg.com/rss/index.xml" );
		form.append( browser );
		form.addCommand( new Command("Command", Command.SCREEN, 2 ));
		//form._callShowNotify();
		Background background = new SimpleBackground( 0xffff00 );
		UiAccess.setBackground(form, background);
		return form;
	}

	public static void main(String[] args) {
		File polishHome;
		if (args.length > 0) {
			polishHome = new File( args[0]);
		} else {
			polishHome = new File("/Applications/J2ME-Polish");
		}
		Device device = DeviceDatabase.loadDevice( polishHome, "Generic/midp2");
		System.out.println("device loaded: " + device.getIdentifier() );
		File projectHome;
		if (args.length > 1) {
			projectHome = new File( args[1]);
		} else {
			projectHome = new File(".");
		}
		
		Test test = new Test( "Editor", true, polishHome, projectHome );
		test.setVisible(true);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.runtime.SelectionListener#clearSelection()
	 */
	public void clearSelection() {
		// TODO robertvirkus implement clearSelection
		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.runtime.SelectionListener#notifyItemSelected(de.enough.polish.ui.Item, de.enough.polish.ui.Style, int, int)
	 */
	public void notifyItemSelected(Item item, Style style, int x, int y) {
		ItemOrScreen itemOrScreen = new ItemOrScreen( item );
		editStyle( itemOrScreen, style );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.runtime.SelectionListener#notifyScreenSelected(de.enough.polish.ui.Screen, de.enough.polish.ui.Style, int, int)
	 */
	public void notifyScreenSelected(Screen screen, Style style, int x, int y) {
		StyleEditorUtil.setDefaultStyles(screen, this.resourceProvider);
		ItemOrScreen itemOrScreen = new ItemOrScreen( screen );
		editStyle( itemOrScreen, style );
	}

	/**
	 * @param itemOrScreen
	 * @param style
	 */
	private void editStyle(ItemOrScreen itemOrScreen, Style style) {
		EditStyle editStyle = this.editor.editStyle(itemOrScreen, style);
		if (editStyle != null) {
			this.resourcesTree.selectStyle(editStyle);
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StyleListener#notifyStyleUpdated(de.enough.polish.styleeditor.EditStyle)
	 */
	public void notifyStyleUpdated(EditStyle style) {
		//System.out.println("Test.notifyStyleUpdated(): layout=" + Integer.toHexString( style.getStyle().layout ));
		if (style.getItemOrScreen() != null) {
			style.getItemOrScreen().setStyle( style.getStyle() );
			style.getItemOrScreen().requestInit();
		} else {
			System.out.println("notifyStyleUpdated(EditStyle style):  !!!!!!!style.getItemOrScreen() is NULL !!!!!!!!");
		}
		this.simulation.getCurrentDisplayable()._requestRepaint();
		//this.simulation.setCurrent( this.simulation.getCurrentDisplayable() );
		
		//System.out.println();
		System.out.println( style.toSourceCode() );
		try {
			this.resourceProvider.saveResources();
		} catch (IOException e) {
			// TODO robertvirkus handle IOException
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.resources.swing.StyleSelectionListener#notifyStyleSelected(de.enough.polish.resources.StyleProvider)
	 */
	public void notifyStyleSelected(StyleProvider styleProvider) {
		System.out.println("notify from tree: style selected: " + styleProvider.getName() );
		if (styleProvider instanceof EditStyle) {
			this.editor.editStyle( (EditStyle)styleProvider );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.resources.swing.ColorSelectionListener#notifyColorSelected(de.enough.polish.resources.ColorProvider)
	 */
	public void notifyColorSelected(ColorProvider color) {
		System.out.println("notify from tree: color selected: " + color.getName() );
		ColorProvider colorProvider = ColorChooserComponent.showDialog(color.getName(), color, null, this, true );
		if (colorProvider != null) {
			color.setColor( color.getColor() );
//			this.colorButton.setBackground( new java.awt.Color( colorProvider.getColor().getColor() ) );
//			editor.setColor( colorProvider );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.components.ColorChooserListener#notifyColorUpdated(de.enough.polish.resources.ColorProvider)
	 */
	public void notifyColorUpdated(ColorProvider colorProvider) {
		// TODO robertvirkus implement notifyColorUpdated
		System.out.println("named color has changed: " + colorProvider.getName() + "=" + Integer.toHexString( colorProvider.getColor().getColor() ));
	}
	
    private EditStyle getEditStyle( ItemOrScreen itemOrScreen, Style style ) {       
        EditStyle editStyle = getEditStyleImpl(itemOrScreen, style);
        if (editStyle != null) {
            itemOrScreen.setStyle( editStyle.getStyle() );
            itemOrScreen.requestInit();
        }
        return editStyle;
    }
    private EditStyle getEditStyleImpl( ItemOrScreen itemOrScreen, Style style ) {       
        EditStyle editStyle = null;
        if (style != null && style.name != null) {
            editStyle = (EditStyle) this.resourceProvider.getStyle(style.name);
            if (editStyle == null) {
                editStyle = new EditStyle( style.name, style,  itemOrScreen );
                this.resourceProvider.addStyle(style.name, editStyle);
                this.resourcesTree.addStyle(editStyle);
            } else {
                editStyle.setItemOrScreen(itemOrScreen);
            }
        }         
        if (editStyle == null) {
            //StyleProvider[] knownStyles = this.resourceProvider.getStyles();
            //String sty
            Object itemOrScreenObj = itemOrScreen.getItemOrScreen();
            String defaultName = getDefaultStyleName( itemOrScreenObj.getClass() );
            
            String chosenName = SpecifyOrSelectEntryDialog.showDialog(
                    null,
                    "Choose Style Name",
                    itemOrScreenObj instanceof Item ? 
                        "This item has no attached style, please enter the desired name: "
                        :
                        "This screen has no attached style, please enter the desired name: ",
                    this.resourceProvider.getStyles(),
                    defaultName);
            if (chosenName == null) {
                return null;
            }
            // check if the user selected an existing style:
            editStyle = (EditStyle) this.resourceProvider.getStyle(chosenName);
            if (editStyle != null) {
                editStyle.setItemOrScreen(itemOrScreen);
            } else {
                if (style == null || style == StyleSheet.defaultStyle) {
                    style = new Style();
                }
                style.name = chosenName;
                if (itemOrScreenObj instanceof Item) {
                    Item item = (Item) itemOrScreenObj;
                    item.setStyle( style );
                    //UiAccess.setStyle( (Item)itemOrScreenObj, style );
                    //dc = (DesignComponent) UiAccess.getAttribute( (Item)itemOrScreenObj, "DesignComponent" );
                } else {
                    UiAccess.setStyle( (Screen)itemOrScreenObj, style );
                }
                editStyle = new EditStyle( style.name, style,  itemOrScreen );

                this.resourceProvider.addStyle(style.name, editStyle);
                this.resourcesTree.addStyle(editStyle);
            }
        }
        return editStyle;
    }
    
    private String getDefaultStyleName( Class itemOrScreenClass ) {
        String defaultName = itemOrScreenClass.getName();
        if (defaultName.lastIndexOf('.' ) != -1) {
            defaultName = defaultName.substring( defaultName.lastIndexOf('.' ) + 1 );
        }
        return "my" + defaultName;
    }

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StyleEditorListener#notifyStyleCreated(de.enough.polish.styleeditor.ItemOrScreen, de.enough.polish.styleeditor.EditStyle)
	 */
	public void notifyStyleCreated(ItemOrScreen itemOrScreen, EditStyle editStyle) {
		System.out.println("a new style has been created for " + itemOrScreen.getItemOrScreen() + ": " + editStyle.getName() );
		this.resourcesTree.selectStyle(editStyle);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StyleEditorListener#notifyStyleAttached(de.enough.polish.styleeditor.ItemOrScreen, de.enough.polish.styleeditor.EditStyle)
	 */
	public void notifyStyleAttached(ItemOrScreen itemOrScreen, EditStyle editStyle) {
		// TODO robertvirkus implement notifyStyleAttached
		this.simulation.getCurrentDisplayable()._requestRepaint();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == this.deviceSelector) {
			String identifier = (String) this.deviceSelector.getSelectedItem();
			SimulationDevice device = new SimulationDevice(identifier);
			this.simulation.setDevice(device);
		}
		 
	}
	
	public class MyStyleEditorListener extends DefaultStyleEditorListener {

		/**
		 * @param resourcesTree
		 * @param simulation
		 */
		public MyStyleEditorListener(ResourcesTree resourcesTree, Simulation simulation) {
			super(resourcesTree, simulation);
		}

		/* (non-Javadoc)
		 * @see de.enough.polish.styleeditor.DefaultStyleEditorListener#notifyStyleUpdated(de.enough.polish.styleeditor.EditStyle)
		 */
		public void notifyStyleUpdated(EditStyle style) {
			super.notifyStyleUpdated(style);
			
			System.out.println( style.toSourceCode() );
			try {
				Test.this.resourceProvider.saveResources();
			} catch (IOException e) {
				// TODO robertvirkus handle IOException
				e.printStackTrace();
			}

		}
		
		
		
	}

}
