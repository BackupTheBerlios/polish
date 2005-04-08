/*
 * Created on Apr 8, 2005 at 2:37:33 PM.
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
package de.enough.polish.plugin.eclipse.polishEditor.preference;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.enough.polish.plugin.eclipse.polishEditor.PolishEditorPlugin;
import de.enough.polish.plugin.eclipse.polishEditor.editor.IPolishConstants;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Apr 8, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class PolishRootPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage{
    
    public PolishRootPage() {
        super("Titel of Polish Root Page",AbstractUIPlugin.imageDescriptorFromPlugin(PolishEditorPlugin.ID,"icons/sample.gif"),GRID);
        setPreferenceStore(PolishEditorPlugin.getDefault().getPreferenceStore());
        setDescription("Syntax Highlighting");
    }
 
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
     */
    protected void createFieldEditors() {
        System.out.println("PolishRootPage.createFieldEditors(...):enter.");
        FieldEditor colorDirectiveEditor = new ColorFieldEditor(IPolishConstants.POLISH_COLOR_DIRECTIVE,"Directive Color",getFieldEditorParent());
        FieldEditor colorDefaultEditor = new ColorFieldEditor(IPolishConstants.POLISH_COLOR_DEFAULT,"Default Color",getFieldEditorParent());
        FieldEditor colorVariableEditor = new ColorFieldEditor(IPolishConstants.POLISH_COLOR_VARIABLE,"Variable Color",getFieldEditorParent());
        FieldEditor colorSymbolEditor = new ColorFieldEditor(IPolishConstants.POLISH_COLOR_SYMBOL,"Symbol Color",getFieldEditorParent());
        FieldEditor colorFunctionPunctationEditor = new ColorFieldEditor(IPolishConstants.POLISH_COLOR_FUNCTION_PUNCTATION,"Function Punctation Color",getFieldEditorParent());
        
        
        addField(colorDirectiveEditor);
        addField(colorDefaultEditor);
        addField(colorVariableEditor);
        addField(colorSymbolEditor);
        addField(colorFunctionPunctationEditor);
    }
        
    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
        System.out.println("PolishRootPage.init(...):enter.");
    }

	public boolean performOk() {
        System.out.println("PolishRootPage.performOK():enter.");
       
        boolean value = super.performOk();
        PolishEditorPlugin.getDefault().savePluginPreferences();

        return value;
    }
}
