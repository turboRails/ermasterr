package org.insightech.er.editor.model.tracking;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class UpdatedNodeElement implements Serializable {

    private static final long serialVersionUID = -1547406607441505291L;

    private final NodeElement nodeElement;

    private final Set<Column> addedColumns;

    private final Set<Column> updatedColumns;

    private final Set<Column> removedColumns;

    public UpdatedNodeElement(final NodeElement nodeElement) {
        this.nodeElement = nodeElement;

        addedColumns = new HashSet<Column>();
        updatedColumns = new HashSet<Column>();
        removedColumns = new HashSet<Column>();
    }

    public NodeElement getNodeElement() {
        return nodeElement;
    }

    public void setAddedColumns(final Collection<NormalColumn> columns) {
        addedColumns.clear();
        addedColumns.addAll(columns);
    }

    public void setUpdatedColumns(final Collection<NormalColumn> columns) {
        updatedColumns.clear();
        updatedColumns.addAll(columns);
    }

    public void setRemovedColumns(final Collection<NormalColumn> columns) {
        removedColumns.clear();
        removedColumns.addAll(columns);
    }

    public boolean isAdded(final Column column) {
        if (addedColumns.contains(column)) {
            return true;
        }

        return false;
    }

    public boolean isUpdated(final Column column) {
        if (updatedColumns.contains(column)) {
            return true;
        }

        return false;
    }

    public Set<Column> getRemovedColumns() {
        return removedColumns;
    }

}
