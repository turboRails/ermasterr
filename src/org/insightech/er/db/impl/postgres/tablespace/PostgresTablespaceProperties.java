package org.insightech.er.db.impl.postgres.tablespace;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.util.Check;

public class PostgresTablespaceProperties implements TablespaceProperties {

    private static final long serialVersionUID = -1168759105844875794L;

    private String location;

    private String owner;

    /**
     * location を取得します.
     * 
     * @return location
     */
    public String getLocation() {
        return location;
    }

    /**
     * location を設定します.
     * 
     * @param location
     *            location
     */
    public void setLocation(final String location) {
        this.location = location;
    }

    /**
     * owner を取得します.
     * 
     * @return owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * owner を設定します.
     * 
     * @param owner
     *            owner
     */
    public void setOwner(final String owner) {
        this.owner = owner;
    }

    @Override
    public TablespaceProperties clone() {
        final PostgresTablespaceProperties properties = new PostgresTablespaceProperties();

        properties.location = location;
        properties.owner = owner;

        return properties;
    }

    @Override
    public LinkedHashMap<String, String> getPropertiesMap() {
        final LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

        map.put("label.tablespace.location", getLocation());
        map.put("label.tablespace.owner", getOwner());

        return map;
    }

    @Override
    public List<String> validate() {
        final List<String> errorMessage = new ArrayList<String>();

        if (Check.isEmptyTrim(getLocation())) {
            errorMessage.add("error.tablespace.location.empty");
        }

        return errorMessage;
    }
}
