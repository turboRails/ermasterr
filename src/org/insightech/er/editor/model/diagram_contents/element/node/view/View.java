package org.insightech.er.editor.model.diagram_contents.element.node.view;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.ColumnHolder;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.view.properties.ViewProperties;

public class View extends TableView implements ObjectModel, ColumnHolder {

    private static final long serialVersionUID = -4492787972500741281L;

    public static final String NEW_PHYSICAL_NAME = ResourceString.getResourceString("new.view.physical.name");

    public static final String NEW_LOGICAL_NAME = ResourceString.getResourceString("new.view.logical.name");

    private String sql;

    public View() {
        tableViewProperties = new ViewProperties();
    }

    @Override
    public TableViewProperties getTableViewProperties() {
        return tableViewProperties;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(final String sql) {
        this.sql = sql;
    }

    @Override
    public void addColumn(final Column column) {
        if (column instanceof NormalColumn) {
            final NormalColumn normalColumn = (NormalColumn) column;
            normalColumn.setAutoIncrement(false);
            normalColumn.setPrimaryKey(false);
            normalColumn.setUniqueKey(false);
            normalColumn.setNotNull(false);
        }

        columns.add(column);
        column.setColumnHolder(this);
    }

    @Override
    public View copyData() {
        final View to = new View();
        to.setSql(getSql());

        super.copyTableViewData(to);

        to.tableViewProperties = getTableViewProperties().clone();

        return to;
    }

    @Override
    public void restructureData(final TableView to) {
        final View view = (View) to;

        view.setSql(getSql());

        super.restructureData(to);

        view.tableViewProperties = tableViewProperties.clone();

    }

    @Override
    public View clone() {
        final View clone = (View) super.clone();

        final TableViewProperties cloneViewProperties = tableViewProperties.clone();
        clone.tableViewProperties = cloneViewProperties;

        return clone;
    }

    @Override
    public String getObjectType() {
        return "view";
    }
}
