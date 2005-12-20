/*
 * Created on Dec 7, 2005 at 5:05:12 PM.
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

/**
 * This class is stolen from org.eclipse.jdt.internal.ui.text.PreferencesAdapter.
 * We copy the internal api to get what we want. We are mean.
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Dec 7, 2005 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
import org.eclipse.core.runtime.Preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * 
 * Adapts {@link org.eclipse.core.runtime.Preferences} to
 * {@link org.eclipse.jface.preference.IPreferenceStore}
 *
 * @since 3.0
 */
public class PreferencesAdapter implements IPreferenceStore {

    /**
     * Property change listener. Listens for events of type
     * {@link org.eclipse.core.runtime.Preferences.PropertyChangeEvent} and fires
     * a {@link org.eclipse.jface.util.PropertyChangeEvent} on the
     * adapter with arguments from the received event.
     */
    private class PropertyChangeListener implements Preferences.IPropertyChangeListener {

        /*
         * @see org.eclipse.core.runtime.Preferences.IPropertyChangeListener#propertyChange(org.eclipse.core.runtime.Preferences.PropertyChangeEvent)
         */
        public void propertyChange(Preferences.PropertyChangeEvent event) {
            firePropertyChangeEvent(event.getProperty(), event.getOldValue(), event.getNewValue());
        }
    }

    /** Listeners on the adapter */
    private ListenerList fListeners= new ListenerList();

    /** Listener on the adapted Preferences */
    private PropertyChangeListener fListener= new PropertyChangeListener();

    /** Adapted Preferences */
    private Preferences fPreferences;

    /** True iff no events should be forwarded */
    private boolean fSilent;

    /**
     * Initialize with empty Preferences.
     */
    public PreferencesAdapter() {
        this(new Preferences());
    }
    /**
     * Initialize with the given Preferences.
     *
     * @param preferences The preferences to wrap.
     */
    public PreferencesAdapter(Preferences preferences) {
        this.fPreferences= preferences;
    }

    /**
     * {@inheritDoc}
     */
    public void addPropertyChangeListener(IPropertyChangeListener listener) {
        if (this.fListeners.size() == 0)
            this.fPreferences.addPropertyChangeListener(this.fListener);
        this.fListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void removePropertyChangeListener(IPropertyChangeListener listener) {
        this.fListeners.remove(listener);
        if (this.fListeners.size() == 0)
            this.fPreferences.removePropertyChangeListener(this.fListener);
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(String name) {
        return this.fPreferences.contains(name);
    }

    /**
     * {@inheritDoc}
     */
    public void firePropertyChangeEvent(String name, Object oldValue, Object newValue) {
        if (!this.fSilent) {
            PropertyChangeEvent event= new PropertyChangeEvent(this, name, oldValue, newValue);
            Object[] listeners= this.fListeners.getListeners();
            for (int i= 0; i < listeners.length; i++)
                ((IPropertyChangeListener) listeners[i]).propertyChange(event);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean getBoolean(String name) {
        return this.fPreferences.getBoolean(name);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getDefaultBoolean(String name) {
        return this.fPreferences.getDefaultBoolean(name);
    }

    /**
     * {@inheritDoc}
     */
    public double getDefaultDouble(String name) {
        return this.fPreferences.getDefaultDouble(name);
    }

    /**
     * {@inheritDoc}
     */
    public float getDefaultFloat(String name) {
        return this.fPreferences.getDefaultFloat(name);
    }

    /**
     * {@inheritDoc}
     */
    public int getDefaultInt(String name) {
        return this.fPreferences.getDefaultInt(name);
    }

    /**
     * {@inheritDoc}
     */
    public long getDefaultLong(String name) {
        return this.fPreferences.getDefaultLong(name);
    }

    /**
     * {@inheritDoc}
     */
    public String getDefaultString(String name) {
        return this.fPreferences.getDefaultString(name);
    }

    /**
     * {@inheritDoc}
     */
    public double getDouble(String name) {
        return this.fPreferences.getDouble(name);
    }

    /**
     * {@inheritDoc}
     */
    public float getFloat(String name) {
        return this.fPreferences.getFloat(name);
    }

    /**
     * {@inheritDoc}
     */
    public int getInt(String name) {
        return this.fPreferences.getInt(name);
    }

    /**
     * {@inheritDoc}
     */
    public long getLong(String name) {
        return this.fPreferences.getLong(name);
    }

    /**
     * {@inheritDoc}
     */
    public String getString(String name) {
        return this.fPreferences.getString(name);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDefault(String name) {
        return this.fPreferences.isDefault(name);
    }

    /**
     * {@inheritDoc}
     */
    public boolean needsSaving() {
        return this.fPreferences.needsSaving();
    }

    /**
     * {@inheritDoc}
     */
    public void putValue(String name, String value) {
        try {
            this.fSilent= true;
            this.fPreferences.setValue(name, value);
        } finally {
            this.fSilent= false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setDefault(String name, double value) {
        this.fPreferences.setDefault(name, value);
    }

    /**
     * {@inheritDoc}
     */
    public void setDefault(String name, float value) {
        this.fPreferences.setDefault(name, value);
    }

    /**
     * {@inheritDoc}
     */
    public void setDefault(String name, int value) {
        this.fPreferences.setDefault(name, value);
    }

    /**
     * {@inheritDoc}
     */
    public void setDefault(String name, long value) {
        this.fPreferences.setDefault(name, value);
    }

    /**
     * {@inheritDoc}
     */
    public void setDefault(String name, String defaultObject) {
        this.fPreferences.setDefault(name, defaultObject);
    }

    /**
     * {@inheritDoc}
     */
    public void setDefault(String name, boolean value) {
        this.fPreferences.setDefault(name, value);
    }

    /**
     * {@inheritDoc}
     */
    public void setToDefault(String name) {
        this.fPreferences.setToDefault(name);
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(String name, double value) {
        this.fPreferences.setValue(name, value);
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(String name, float value) {
        this.fPreferences.setValue(name, value);
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(String name, int value) {
        this.fPreferences.setValue(name, value);
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(String name, long value) {
        this.fPreferences.setValue(name, value);
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(String name, String value) {
        this.fPreferences.setValue(name, value);
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(String name, boolean value) {
        this.fPreferences.setValue(name, value);
    }
}
