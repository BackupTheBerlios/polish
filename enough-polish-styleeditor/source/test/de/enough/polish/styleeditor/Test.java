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
import java.io.File;
import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import de.enough.polish.Environment;
import de.enough.polish.devices.DeviceDatabase;
import de.enough.polish.preprocess.css.CssAttributesManager;
import de.enough.polish.resources.ColorProvider;
import de.enough.polish.resources.ResourcesProvider;
import de.enough.polish.resources.StyleProvider;
import de.enough.polish.resources.impl.ResourcesProviderImpl;
import de.enough.polish.resources.swing.ColorSelectionListener;
import de.enough.polish.resources.swing.ResourcesTree;
import de.enough.polish.resources.swing.StyleSelectionListener;
import de.enough.polish.runtime.SelectionListener;
import de.enough.polish.runtime.Simulation;
import de.enough.polish.runtime.SimulationDevice;
import de.enough.polish.runtime.overlays.HoverOverlay;
import de.enough.polish.runtime.overlays.SelectionOverlay;
import de.enough.polish.styleeditor.editors.MarginPaddingEditor;
import de.enough.polish.styleeditor.swing.SwingStyleEditor;
import de.enough.polish.styleeditor.swing.components.ColorChooserComponent;
import de.enough.polish.styleeditor.swing.components.ColorChooserListener;
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
implements SelectionListener, StyleEditorListener, StyleSelectionListener, ColorSelectionListener, ColorChooserListener
{

	private StyleEditor editor;
	private Simulation simulation;
	private ResourcesProvider resourceProvider;
	private ResourcesTree resourcesTree;

	/**
	 * @param title
	 * @param systemExitOnQuit
	 */
	public Test(String title, boolean systemExitOnQuit, File polishHome, File projectHome ) {
		super(title, systemExitOnQuit);

		DeviceDatabase deviceDB = DeviceDatabase.getInstance(polishHome);
		ResourceUtil resourceUtil = new ResourceUtil( getClass().getClassLoader() );
		CssAttributesManager attributesManager = CssAttributesManager.getInstance( polishHome, resourceUtil );
		Environment environment = new Environment(polishHome);
		environment.setBaseDir( projectHome );
		environment.initialize( deviceDB.getDevice("Nokia/6630"), null);

		this.resourceProvider = initResourceProvider( polishHome, environment, attributesManager );
		
		this.simulation  = new Simulation( new SimulationDevice() );
		Displayable form = initUi();
		initSimulation( this.simulation, form ); 
		
		SwingStyleEditor swingEditor = new SwingStyleEditor();
		this.editor = new StyleEditor( this.resourceProvider, attributesManager, environment, swingEditor );
		this.editor.addDefaultPartEditors();
		this.editor.addStyleListener( this );
		JPanel panel = new JPanel( new BorderLayout() );
		panel.add( this.simulation, BorderLayout.NORTH );
		panel.add( swingEditor, BorderLayout.CENTER );
		ResourcesTree tree = ResourcesTree.getInstance( this.resourceProvider );
		panel.add( tree, BorderLayout.EAST );
		tree.addStyleSelectionListener(this);
		tree.addColorSelectionListener( this );
		this.resourcesTree = tree;
		Container contentPane = getContentPane();
		contentPane.add( panel );
		pack();
		
		// set style:
		notifyScreenSelected( (Screen)form, UiAccess.getStyle( (Screen)form), 0, 0 );
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
		form.append( group );
		form.addCommand( new Command("Command", Command.SCREEN, 2 ));
		form._callShowNotify();
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
		ItemOrScreen itemOrScreen = new ItemOrScreen( screen );
		editStyle( itemOrScreen, style );
	}

	/**
	 * @param itemOrScreen
	 * @param style
	 */
	private void editStyle(ItemOrScreen itemOrScreen, Style style) {
		if (style == null || style == StyleSheet.defaultStyle) {
			style = new Style();
		}
		EditStyle editStyle;
		if (style.name != null) {
			editStyle = (EditStyle) this.resourceProvider.getStyle( style.name );
			editStyle.setItemOrScreen( itemOrScreen );
		} else {
			editStyle = new EditStyle( itemOrScreen.getItemOrScreen().getClass().getName(), style, itemOrScreen );
		}
		this.editor.setStyle(editStyle);
		this.resourcesTree.selectStyle(editStyle);
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
			this.editor.setStyle( (EditStyle)styleProvider );
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

}
