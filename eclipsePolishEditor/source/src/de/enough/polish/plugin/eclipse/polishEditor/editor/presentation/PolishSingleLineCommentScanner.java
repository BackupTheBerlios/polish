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
package de.enough.polish.plugin.eclipse.polishEditor.editor.presentation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.ui.text.AbstractJavaScanner;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.IJavaColorConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import de.enough.polish.plugin.eclipse.polishEditor.IPolishConstants;
import de.enough.polish.plugin.eclipse.utils.TokenStore;

/**
 * <p>There ist a problem with the update of tokens. After a preference change, the change is not reflected in the presentation.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Mar 29, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class PolishSingleLineCommentScanner extends AbstractJavaScanner {

    public class PropertyChangeListener implements IPropertyChangeListener {

        public void propertyChange(PropertyChangeEvent event) {
            System.out.println("polishScanner.PropertyChangeListener.propertyChange:"+event.getProperty()+".old:"+event.getOldValue().getClass()+".new:"+event.getNewValue().getClass());
            //TODO: Take care of the token cache.
        }

    }

 //  TODO: NEW_COLOR add here.
    private String[] tokenProperties = {
            IJavaColorConstants.JAVA_SINGLE_LINE_COMMENT,
            IJavaColorConstants.JAVA_KEYWORD,
            IJavaColorConstants.JAVA_STRING,
            IJavaColorConstants.JAVA_METHOD_NAME,
            IJavaColorConstants.JAVA_DEFAULT,
            IPolishConstants.POLISH_COLOR_DEFAULT,
            IPolishConstants.POLISH_COLOR_DIRECTIVE,
            IPolishConstants.POLISH_COLOR_FUNCTION_PUNCTATION,
            };
    
    private TokenStore tokenStore; 
   
    /**
     * @param colorManager
     * @param preferenceStore
     */
    public PolishSingleLineCommentScanner(IColorManager colorManager, IPreferenceStore preferenceStore) {
        super(colorManager,preferenceStore);
        this.tokenStore = new TokenStore();
        initialize(); // This call needed because the super constructor does not call it !
    }

    protected String[] getTokenProperties() {
        return this.tokenProperties;
    }

    protected List createRules() {
        setDefaultReturnToken(getToken(IPolishConstants.POLISH_COLOR_DEFAULT));
        //TODO: NEW_COLOR add here.
        this.tokenStore.addToken(IJavaColorConstants.JAVA_KEYWORD,getToken(IJavaColorConstants.JAVA_KEYWORD));
        this.tokenStore.addToken(IJavaColorConstants.JAVA_SINGLE_LINE_COMMENT,getToken(IJavaColorConstants.JAVA_SINGLE_LINE_COMMENT));  
        this.tokenStore.addToken(IJavaColorConstants.JAVA_DEFAULT,getToken(IJavaColorConstants.JAVA_DEFAULT));  
        this.tokenStore.addToken(IJavaColorConstants.JAVA_STRING,getToken(IJavaColorConstants.JAVA_STRING));
        this.tokenStore.addToken(IPolishConstants.POLISH_COLOR_DIRECTIVE,getToken(IPolishConstants.POLISH_COLOR_DIRECTIVE));
        this.tokenStore.addToken(IPolishConstants.POLISH_COLOR_DEFAULT,getToken(IPolishConstants.POLISH_COLOR_DEFAULT));
        this.tokenStore.addToken(IPolishConstants.POLISH_COLOR_FUNCTION_PUNCTATION,getToken(IPolishConstants.POLISH_COLOR_FUNCTION_PUNCTATION));
          
        // The token store is only needed to pass all tokens to the rule in one attempt.
        IRule rule;
        rule = new PolishDirectiveRule(this.tokenStore);
        List rules = new ArrayList();
        //TODO: Only testing. Uncomment it to get it to work again.
        rules.add(rule);
        
        return rules;
    }
    
    
    public void adaptToPreferenceChange(PropertyChangeEvent event) {
        super.adaptToPreferenceChange(event);
    }
    
   
    //I know this is evil but there is no other way. The situation:The chainedPreferenceStore from JavaEditor is not writeable.
    // So we have to store our values somewhere else. We get the preferenceStore from PolishEditorPlugin directly :( So we have two stores
    // and this is going to be a manager nightmare. But I do not see another way. Having a writeable chained store is semanticly problematic
    //
    public boolean affectsBehavior(PropertyChangeEvent event) {
        String property = event.getProperty();
        if(property.equals(IPolishConstants.POLISH_COLOR_DIRECTIVE) ||
           property.equals(IPolishConstants.POLISH_COLOR_DEFAULT) ||
           property.equals(IPolishConstants.POLISH_COLOR_FUNCTION_PUNCTATION)) {
            return true;
        }
        
        return super.affectsBehavior(event);
    }
}
