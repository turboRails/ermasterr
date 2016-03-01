package org.insightech.er.editor.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public abstract class AbstractModel implements Serializable, Cloneable {

    private static final long serialVersionUID = 4266969076408212298L;

    private PropertyChangeSupport support;

    private static boolean updateable = true;

    public static void setUpdateable(final boolean enabled) {
        updateable = enabled;
    }

    public static boolean isUpdateable() {
        return updateable;
    }

    public AbstractModel() {
        support = new PropertyChangeSupport(this);
    }

    protected void firePropertyChange(final String name, final Object oldValue, final Object newValue) {
        support.firePropertyChange(name, oldValue, newValue);
    }

    protected void firePropertyChange(final String name, final int oldValue, final int newValue) {
        support.firePropertyChange(name, oldValue, newValue);
    }

    protected void firePropertyChange(final String name, final boolean oldValue, final boolean newValue) {
        support.firePropertyChange(name, oldValue, newValue);
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public void refresh() {
        if (updateable) {
            this.firePropertyChange("refresh", null, null);
        }
    }

    public void refreshVisuals() {
        if (updateable) {
            this.firePropertyChange("refreshVisuals", null, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractModel clone() {
        AbstractModel clone = null;
        try {
            clone = (AbstractModel) super.clone();

            clone.support = new PropertyChangeSupport(clone);

        } catch (final CloneNotSupportedException e) {}

        return clone;
    }
}
