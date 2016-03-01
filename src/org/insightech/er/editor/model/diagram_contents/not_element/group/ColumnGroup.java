package org.insightech.er.editor.model.diagram_contents.not_element.group;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.ColumnHolder;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class ColumnGroup extends Column implements ObjectModel, Comparable<ColumnGroup>, ColumnHolder {

    private static final long serialVersionUID = -5923128797828160786L;

    private String groupName;

    private List<NormalColumn> columns;

    public ColumnGroup() {
        columns = new ArrayList<NormalColumn>();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }

    public List<NormalColumn> getColumns() {
        return columns;
    }

    public NormalColumn getColumn(final int index) {
        return columns.get(index);
    }

    public void addColumn(final NormalColumn column) {
        columns.add(column);
        column.setColumnHolder(this);
    }

    public void setColumns(final List<NormalColumn> columns) {
        this.columns = columns;
        for (final Column column : columns) {
            column.setColumnHolder(this);
        }
    }

    public void removeColumn(final NormalColumn column) {
        columns.remove(column);
    }

    public List<TableView> getUsedTalbeList(final ERDiagram diagram) {
        final List<TableView> usedTableList = new ArrayList<TableView>();

        for (final TableView table : diagram.getDiagramContents().getContents().getTableViewList()) {
            for (final Column tableColumn : table.getColumns()) {
                if (tableColumn == this) {
                    usedTableList.add(table);
                    break;
                }
            }
        }

        return usedTableList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return getGroupName();
    }

    @Override
    public int compareTo(final ColumnGroup other) {
        if (other == null) {
            return -1;
        }

        if (groupName == null) {
            return 1;
        }
        if (other.getGroupName() == null) {
            return -1;
        }

        return groupName.toUpperCase().compareTo(other.getGroupName().toUpperCase());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ColumnGroup other = (ColumnGroup) obj;
        if (groupName == null) {
            if (other.groupName != null)
                return false;
        } else if (!groupName.equals(other.groupName))
            return false;

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnGroup clone() {
        final ColumnGroup clone = (ColumnGroup) super.clone();

        final List<NormalColumn> cloneColumns = new ArrayList<NormalColumn>();

        for (final NormalColumn column : columns) {
            final NormalColumn cloneColumn = column.clone();
            cloneColumns.add(cloneColumn);
        }

        clone.setColumns(cloneColumns);

        return clone;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());

        sb.append(", groupName:" + groupName);
        sb.append(", columns:" + columns);

        return sb.toString();
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getObjectType() {
        return "group";
    }

}
