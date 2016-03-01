package org.insightech.er.editor.model.diagram_contents.element.node.table.index;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class Index extends AbstractModel implements ObjectModel, Comparable<Index> {

    private static final long serialVersionUID = -6734284409681329690L;

    private String name;

    private boolean nonUnique;

    private boolean fullText;

    private String type;

    private String description;

    private List<Boolean> descs;

    private List<NormalColumn> columns;

    private List<String> columnNames;

    private ERTable table;

    public Index(final ERTable table, final String name, final boolean nonUnique, final String type, final String description) {
        this.table = table;

        this.nonUnique = nonUnique;
        this.type = type;
        this.description = description;

        descs = new ArrayList<Boolean>();

        columns = new ArrayList<NormalColumn>();
        columnNames = new ArrayList<String>();

        this.name = name;
    }

    public void setDescs(final List<Boolean> descs) {
        this.descs = descs;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setNonUnique(final boolean nonUnique) {
        this.nonUnique = nonUnique;
    }

    public void setColumns(final List<NormalColumn> columns) {
        this.columns = columns;
    }

    public void addColumn(final NormalColumn column) {
        columns.add(column);
    }

    public void addColumn(final NormalColumn column, final Boolean desc) {
        columns.add(column);
        descs.add(desc);
    }

    public List<NormalColumn> getColumns() {
        final List<NormalColumn> list = new ArrayList<NormalColumn>();

        for (int i = 0; i < columns.size(); i++) {
            final NormalColumn column = columns.get(i);

            if (table.getExpandedColumns().contains(column)) {
                list.add(column);
            }
        }

        return list;
    }

    public void clearColumns() {
        columns.clear();
        descs.clear();
        columnNames.clear();
    }

    public boolean isNonUnique() {
        return nonUnique;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void addColumnName(final String columnName, final Boolean desc) {
        columnNames.add(columnName);
        descs.add(desc);
    }

    public List<Boolean> getDescs() {
        final List<Boolean> list = new ArrayList<Boolean>();

        for (int i = 0; i < columns.size(); i++) {
            final NormalColumn column = columns.get(i);

            if (table.getExpandedColumns().contains(column)) {
                list.add(descs.get(i));
            }
        }

        return list;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isFullText() {
        return fullText;
    }

    public void setFullText(final boolean fullText) {
        this.fullText = fullText;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Index clone() {
        final Index clone = (Index) super.clone();

        final List<Boolean> cloneDescs = new ArrayList<Boolean>();
        for (final Boolean desc : descs) {
            cloneDescs.add(desc);
        }

        clone.descs = cloneDescs;

        final List<String> cloneColumnNames = new ArrayList<String>();
        for (final String columnName : columnNames) {
            cloneColumnNames.add(columnName);
        }

        clone.columnNames = cloneColumnNames;

        return clone;
    }

    @Override
    public int compareTo(final Index other) {
        return name.toUpperCase().compareTo(other.name.toUpperCase());
    }

    public ERTable getTable() {
        return table;
    }

    protected void setTable(final ERTable table) {
        this.table = table;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public String getObjectType() {
        return "index";
    }
}
