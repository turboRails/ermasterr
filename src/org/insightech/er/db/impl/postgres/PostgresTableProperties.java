package org.insightech.er.db.impl.postgres;

import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;

public class PostgresTableProperties extends TableProperties {

    private static final long serialVersionUID = 2802345970023438938L;

    private boolean withoutOIDs;

    public PostgresTableProperties() {
        withoutOIDs = true;
    }

    public boolean isWithoutOIDs() {
        return withoutOIDs;
    }

    public void setWithoutOIDs(final boolean withoutOIDs) {
        this.withoutOIDs = withoutOIDs;
    }

}
