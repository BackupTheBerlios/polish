/*
 * Created on Mar 29, 2005 at 1:33:09 PM.
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
package de.enough.polish.plugin.eclipse.polishEditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.ui.text.AbstractJavaScanner;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.IJavaColorConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Mar 29, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class PolishSingleLineCommentScanner extends AbstractJavaScanner {

    /**
     * <p></p>
     *
     * <p>Copyright Enough Software 2005</p>
     * <pre>
     * history
     *        Apr 5, 2005 - ricky creation
     * </pre>
     * @author Richard Nkrumah, Richard.Nkrumah@enough.de
     */
    public class PropertyChangeListener implements IPropertyChangeListener {

        /* (non-Javadoc)
         * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
         */
        public void propertyChange(PropertyChangeEvent event) {
            System.out.println("polishScanner.PropertyChangeListener.propertyChange:"+event.getProperty()+".old:"+event.getOldValue().getClass()+".new:"+event.getNewValue().getClass());
            //TODO: Take care for the token cache.
        }

    }
    // TODO: Have to understand the difference between PartitionType, ContentType and PositionType.
    // Maybe JAVA_PARTITIONING should have a sibling like POLISH_PARTITIONING.
    
    private String[] tokenProperties = {
            IJavaColorConstants.JAVA_SINGLE_LINE_COMMENT,
            IJavaColorConstants.JAVA_KEYWORD,
            IJavaColorConstants.JAVA_STRING,
            IJavaColorConstants.JAVA_METHOD_NAME};
    private IPropertyChangeListener propertyChangeListener;
    
    
    
    /**
     * @param colorManager
     * @param preferenceStore
     */
    public PolishSingleLineCommentScanner(IColorManager colorManager, IPreferenceStore preferenceStore) {
        super(colorManager,preferenceStore);
        initialize(); // This is needed because the super constructor does not call it !
        //TODO: Register this object at the preferenceStore to listen for property changes.
        this.propertyChangeListener = new PropertyChangeListener();
        preferenceStore.addPropertyChangeListener(this.propertyChangeListener);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.text.AbstractJavaScanner#getTokenProperties()
     */
    protected String[] getTokenProperties() {
        return this.tokenProperties;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.text.AbstractJavaScanner#createRules()
     */
    protected List createRules() {
        System.out.println("PolishSingleLineCommentScanner.createRules():enter.this:"+this);

        PolishDirectiveRule rule = new PolishDirectiveRule();
        rule.setCommentToken(getToken(IJavaColorConstants.JAVA_SINGLE_LINE_COMMENT));
        rule.setDirectiveToken(getToken(IJavaColorConstants.JAVA_KEYWORD));
        rule.setNameToken(getToken(IJavaColorConstants.JAVA_STRING));
        rule.setMethodToken(getToken(IJavaColorConstants.JAVA_METHOD_NAME));
        setDefaultReturnToken(getToken(IJavaColorConstants.JAVA_SINGLE_LINE_COMMENT));
        
        List rules = new ArrayList();
        rules.add(rule);
        
        return rules;
    }
}
