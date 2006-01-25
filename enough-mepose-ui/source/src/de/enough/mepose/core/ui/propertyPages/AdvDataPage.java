/*
 * Created on Jan 24, 2006 at 4:09:50 PM.
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
package de.enough.mepose.core.ui.propertyPages;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import de.enough.mepose.core.CorePlugin;
import de.enough.mepose.core.model.MeposeModel;
import de.enough.mepose.core.ui.plugin.UIPluginActivator;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Jan 24, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class AdvDataPage extends PropertyPage {

    
    
    private List textList;
    private MeposeModel model;

    public AdvDataPage() {
        this.textList = new LinkedList();
    }
    
    protected Control createContents(Composite parent) {
        Composite main = new Composite(parent,SWT.NONE);
//        main.setLayout(new GridLayout(2,false));
        TableWrapLayout layout = new TableWrapLayout();
        layout.numColumns = 2;
        main.setLayout(layout);
        IAdaptable adaptable = getElement();
        IProject project = (IProject)adaptable.getAdapter(IProject.class);
        if(project == null) {
            UIPluginActivator.log("No IProject found.");
            return main;
        }
        
        this.model = CorePlugin.getDefault().getMeposeModelManager().getModel(project);
        if(this.model == null) {
            UIPluginActivator.log("No model in project.");
            throw new IllegalStateException("No model in project.");
        }
        Map map = this.model.getStoreableProperties();
        for (Iterator iterator = map.keySet().iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            if(key == null) {
                continue;
            }
            String value = (String)map.get(key);
            Label label = new Label(main,SWT.NONE);
//            label.setLayoutData(new TableWrapData(SWT.BEGINNING,SWT.CENTER));
            label.setLayoutData(new TableWrapData(TableWrapData.LEFT,TableWrapData.MIDDLE));
            label.setText(key);
            Text text = new Text(main,SWT.WRAP);
            text.setText(value==null?"":value);
            text.setData(key);
//            text.setLayoutData(new TableWrapData(SWT.FILL,SWT.CENTER));
            text.setLayoutData(new TableWrapData(TableWrapData.FILL,TableWrapData.MIDDLE));
            this.textList.add(text);
        }
        return main;
    }

    public boolean performOk() {
        Map p = new HashMap();
        for (Iterator iterator = this.textList.iterator(); iterator.hasNext(); ) {
            Text text = (Text) iterator.next();
            String key = (String)text.getData();
            p.put(key,text.getText());
        }
        this.model.restoreFromProperties(p);
        return true;
    }

    
    
}
