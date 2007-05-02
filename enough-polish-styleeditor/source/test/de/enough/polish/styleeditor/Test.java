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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.swing.JPanel;

import de.enough.polish.Environment;
import de.enough.polish.preprocess.css.CssAttributesManager;
import de.enough.polish.runtime.SelectionListener;
import de.enough.polish.runtime.Simulation;
import de.enough.polish.runtime.SimulationDevice;
import de.enough.polish.runtime.overlays.HoverOverlay;
import de.enough.polish.runtime.overlays.SelectionOverlay;
import de.enough.polish.styleeditor.editors.MarginPaddingEditor;
import de.enough.polish.styleeditor.swing.SwingStyleEditor;
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
implements SelectionListener, StyleEditorListener
{

	private StyleEditor editor;
	private Simulation simulation;

	/**
	 * @param title
	 * @param systemExitOnQuit
	 */
	public Test(String title, boolean systemExitOnQuit, File polishHome ) {
		super(title, systemExitOnQuit);
		Container contentPane = getContentPane();
		this.simulation = new Simulation( new SimulationDevice() );
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
		simulation.setCurrent( form );
		simulation.addOverlay( new SelectionOverlay() );
		simulation.addOverlay( new HoverOverlay() );
		simulation.addSelectionListener( this );
		
		SwingStyleEditor swingEditor = new SwingStyleEditor();
		ResourceUtil resourceUtil = new ResourceUtil( getClass().getClassLoader() );
		CssAttributesManager manager = CssAttributesManager.getInstance( polishHome, resourceUtil );
		Environment environment = new Environment(polishHome);
		this.editor = new StyleEditor( manager, environment, swingEditor );
		this.editor.addDefaultPartEditors();
		this.editor.addStyleListener( this );
		JPanel panel = new JPanel( new BorderLayout() );
		panel.add( simulation, BorderLayout.NORTH );
		panel.add( swingEditor, BorderLayout.CENTER );
		contentPane.add( panel );
		pack();
	}
	
	public static void main(String[] args) {
		Test test = new Test( "Editor", true, null );
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
		EditStyle editStyle = new EditStyle( style, itemOrScreen );
		this.editor.setStyle(editStyle);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StyleListener#notifyStyleUpdated(de.enough.polish.styleeditor.EditStyle)
	 */
	public void notifyStyleUpdated(EditStyle style) {
		//System.out.println("Test.notifyStyleUpdated(): layout=" + Integer.toHexString( style.getStyle().layout ));
		style.getItemOrScreen().setStyle( style.getStyle() );
		style.getItemOrScreen().requestInit();
		this.simulation.getCurrentDisplayable()._requestRepaint();
		//this.simulation.setCurrent( this.simulation.getCurrentDisplayable() );
	}

}
