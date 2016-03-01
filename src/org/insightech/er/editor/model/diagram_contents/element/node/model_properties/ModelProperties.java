package org.insightech.er.editor.model.diagram_contents.element.node.model_properties;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.util.NameValue;

public class ModelProperties extends NodeElement implements Cloneable {

    private static final long serialVersionUID = 5311013351131568260L;

    private boolean display;

    private List<NameValue> properties;

    private Date creationDate;

    private Date updatedDate;

    public ModelProperties() {
        creationDate = new Date();
        updatedDate = new Date();

        setLocation(new Location(50, 50, -1, -1));

        properties = new ArrayList<NameValue>();
    }

    public void init() {
        properties.add(new NameValue(ResourceString.getResourceString("label.project.name"), ""));
        properties.add(new NameValue(ResourceString.getResourceString("label.model.name"), ""));
        properties.add(new NameValue(ResourceString.getResourceString("label.version"), ""));
        properties.add(new NameValue(ResourceString.getResourceString("label.company.name"), ""));
        properties.add(new NameValue(ResourceString.getResourceString("label.author"), ""));
    }

    public void clear() {
        properties.clear();
    }

    public List<NameValue> getProperties() {
        return properties;
    }

    public void addProperty(final NameValue property) {
        properties.add(property);
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(final Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(final boolean display) {
        this.display = display;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLocation(final Location location) {
        location.width = -1;
        location.height = -1;

        super.setLocation(location);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelProperties clone() {
        final ModelProperties clone = (ModelProperties) super.clone();

        final List<NameValue> list = new ArrayList<NameValue>();

        for (final NameValue nameValue : properties) {
            list.add(nameValue.clone());
        }

        clone.properties = list;

        return clone;
    }

    public void setProperties(final List<NameValue> properties) {
        this.properties = properties;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getObjectType() {
        return "model_properties";
    }
}
