package org.insightech.er.editor.model.settings;

import java.io.Serializable;

public class Environment implements Serializable, Cloneable {

    private static final long serialVersionUID = 2894497911334351672L;

    private String name;

    public Environment(final String name) {
        this.name = name;
    }

    /**
     * name ���擾���܂�.
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * name ��ݒ肵�܂�.
     * 
     * @param name
     *            name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Environment clone() {
        try {
            final Environment environment = (Environment) super.clone();

            return environment;

        } catch (final CloneNotSupportedException e) {
            return null;
        }
    }

}
