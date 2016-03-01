package org.insightech.er.util;

import java.io.Serializable;

public class NameValue implements Serializable, Cloneable {

    private static final long serialVersionUID = 7655291687176977202L;

    private String name;
    private String value;

    public NameValue(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public NameValue clone() {
        try {
            return (NameValue) super.clone();

        } catch (final CloneNotSupportedException e) {}

        return null;
    }

}
