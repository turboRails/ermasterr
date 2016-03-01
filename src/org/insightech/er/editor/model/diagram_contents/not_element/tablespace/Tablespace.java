package org.insightech.er.editor.model.diagram_contents.not_element.tablespace;

import java.util.HashMap;
import java.util.Map;

import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.AbstractObjectModel;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Environment;

public class Tablespace extends AbstractObjectModel {

    private static final long serialVersionUID = 1861168804265437031L;

    private Map<Environment, TablespaceProperties> propertiesMap = new HashMap<Environment, TablespaceProperties>();

    public void copyTo(final Tablespace to) {
        to.setName(getName());

        to.propertiesMap = new HashMap<Environment, TablespaceProperties>();
        for (final Map.Entry<Environment, TablespaceProperties> entry : propertiesMap.entrySet()) {
            to.propertiesMap.put(entry.getKey(), entry.getValue().clone());
        }
    }

    public TablespaceProperties getProperties(final Environment environment, final ERDiagram diagram) {
        return DBManagerFactory.getDBManager(diagram).checkTablespaceProperties(propertiesMap.get(environment));
    }

    public void putProperties(final Environment environment, final TablespaceProperties tablespaceProperties) {
        propertiesMap.put(environment, tablespaceProperties);
    }

    public Map<Environment, TablespaceProperties> getPropertiesMap() {
        return propertiesMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tablespace clone() {
        final Tablespace clone = (Tablespace) super.clone();

        copyTo(clone);

        return clone;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getObjectType() {
        return "tablespace";
    }

}
