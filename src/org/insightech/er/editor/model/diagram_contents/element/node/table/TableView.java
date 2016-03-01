package org.insightech.er.editor.model.diagram_contents.element.node.table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.ColumnHolder;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.CopyWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.util.Format;

public abstract class TableView extends NodeElement implements ObjectModel, ColumnHolder, Comparable<TableView> {

    private static final long serialVersionUID = -4492787972500741281L;

    public static final int DEFAULT_WIDTH = 120;

    public static final int DEFAULT_HEIGHT = 75;

    public static final Comparator<TableView> PHYSICAL_NAME_COMPARATOR = new TableViewPhysicalNameComparator();

    public static final Comparator<TableView> LOGICAL_NAME_COMPARATOR = new TableViewLogicalNameComparator();

    private String physicalName;

    private String logicalName;

    private String description;

    protected List<Column> columns;

    protected TableViewProperties tableViewProperties;

    public TableView() {
        columns = new ArrayList<Column>();
    }

    public String getPhysicalName() {
        return physicalName;
    }

    public void setPhysicalName(final String physicalName) {
        this.physicalName = physicalName;
    }

    public String getLogicalName() {
        return logicalName;
    }

    public void setLogicalName(final String logicalName) {
        this.logicalName = logicalName;
    }

    @Override
    public String getName() {
        return getLogicalName();
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public abstract TableViewProperties getTableViewProperties();

    public List<NormalColumn> getExpandedColumns() {
        final List<NormalColumn> expandedColumns = new ArrayList<NormalColumn>();

        for (final Column column : getColumns()) {
            if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;
                expandedColumns.add(normalColumn);

            } else if (column instanceof ColumnGroup) {
                final ColumnGroup groupColumn = (ColumnGroup) column;

                expandedColumns.addAll(groupColumn.getColumns());
            }
        }

        return expandedColumns;
    }

    public List<Relation> getIncomingRelations() {
        final List<Relation> relations = new ArrayList<Relation>();

        for (final ConnectionElement connection : getIncomings()) {
            if (connection instanceof Relation) {
                relations.add((Relation) connection);
            }
        }

        return relations;
    }

    public List<Relation> getOutgoingRelations() {
        final List<Relation> relations = new ArrayList<Relation>();

        for (final ConnectionElement connection : getOutgoings()) {
            if (connection instanceof Relation) {
                relations.add((Relation) connection);
            }
        }

        return relations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLocation(final Location location) {
        super.setLocation(location);
    }

    public List<NormalColumn> getNormalColumns() {
        final List<NormalColumn> normalColumns = new ArrayList<NormalColumn>();

        for (final Column column : columns) {
            if (column instanceof NormalColumn) {
                normalColumns.add((NormalColumn) column);
            }
        }
        return normalColumns;
    }

    public Column getColumn(final int index) {
        return columns.get(index);
    }

    public void setColumns(final List<Column> columns) {
        this.columns = columns;

        for (final Column column : columns) {
            column.setColumnHolder(this);
        }
    }

    public void setDirty() {}

    public void addColumn(final Column column) {
        columns.add(column);
        column.setColumnHolder(this);
    }

    public void addColumn(final int index, final Column column) {
        columns.add(index, column);
        column.setColumnHolder(this);
    }

    public void removeColumn(final Column column) {
        columns.remove(column);
    }

    public TableView copyTableViewData(final TableView to) {
        to.setDiagram(getDiagram());

        to.setPhysicalName(getPhysicalName());
        to.setLogicalName(getLogicalName());
        to.setDescription(getDescription());

        final List<Column> columns = new ArrayList<Column>();

        for (final Column fromColumn : getColumns()) {
            if (fromColumn instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) fromColumn;
                final NormalColumn copyColumn = new CopyColumn(normalColumn);
                if (normalColumn.getWord() != null) {
                    copyColumn.setWord(new CopyWord(normalColumn.getWord()));
                }
                columns.add(copyColumn);

            } else {
                columns.add(fromColumn);
            }
        }

        to.setColumns(columns);

        to.setOutgoing(getOutgoings());
        to.setIncoming(getIncomings());

        return to;
    }

    public void restructureData(final TableView to) {
        final Dictionary dictionary = getDiagram().getDiagramContents().getDictionary();

        to.setPhysicalName(getPhysicalName());
        to.setLogicalName(getLogicalName());
        to.setDescription(getDescription());

        for (final NormalColumn toColumn : to.getNormalColumns()) {
            dictionary.remove(toColumn);
        }

        final List<Column> columns = new ArrayList<Column>();

        final List<NormalColumn> newPrimaryKeyColumns = new ArrayList<NormalColumn>();

        for (final Column fromColumn : getColumns()) {
            if (fromColumn instanceof NormalColumn) {
                final CopyColumn copyColumn = (CopyColumn) fromColumn;

                CopyWord copyWord = copyColumn.getWord();
                if (copyColumn.isForeignKey()) {
                    copyWord = null;
                }

                if (copyWord != null) {
                    final Word originalWord = copyColumn.getOriginalWord();
                    dictionary.copyTo(copyWord, originalWord);
                }

                final NormalColumn restructuredColumn = copyColumn.getRestructuredColumn();

                restructuredColumn.setColumnHolder(this);
                if (copyWord == null) {
                    restructuredColumn.setWord(null);
                }
                columns.add(restructuredColumn);

                if (restructuredColumn.isPrimaryKey()) {
                    newPrimaryKeyColumns.add(restructuredColumn);
                }

                dictionary.add(restructuredColumn);

                if (restructuredColumn.isForeignKey()) {
                    for (final Relation relation : restructuredColumn.getRelationList()) {
                        relation.setNotNullOfForeignKey(restructuredColumn.isNotNull());
                    }
                }

            } else {
                columns.add(fromColumn);
            }
        }

        to.setColumns(columns);

        setTargetTableRelation(to, newPrimaryKeyColumns);
    }

    private void setTargetTableRelation(final TableView sourceTable, final List<NormalColumn> newPrimaryKeyColumns) {
        for (final Relation relation : sourceTable.getOutgoingRelations()) {

            if (relation.isReferenceForPK()) {
                final TableView targetTable = relation.getTargetTableView();

                final List<NormalColumn> foreignKeyColumns = relation.getForeignKeyColumns();

                boolean isPrimary = true;
                boolean isPrimaryChanged = false;

                for (final NormalColumn newPrimaryKeyColumn : newPrimaryKeyColumns) {
                    boolean isNewPrimaryKeyReferenced = false;

                    for (final Iterator<NormalColumn> iter = foreignKeyColumns.iterator(); iter.hasNext();) {

                        final NormalColumn foreignKeyColumn = iter.next();

                        if (isPrimary) {
                            isPrimary = foreignKeyColumn.isPrimaryKey();
                        }

                        for (final NormalColumn referencedColumn : foreignKeyColumn.getReferencedColumnList()) {

                            if (referencedColumn == newPrimaryKeyColumn) {
                                isNewPrimaryKeyReferenced = true;
                                iter.remove();
                                break;
                            }
                        }

                        if (isNewPrimaryKeyReferenced) {
                            break;
                        }
                    }

                    if (!isNewPrimaryKeyReferenced) {
                        if (isPrimary) {
                            isPrimaryChanged = true;
                        }
                        final NormalColumn foreignKeyColumn = newPrimaryKeyColumn.createForeignKey(relation, isPrimary);

                        targetTable.addColumn(foreignKeyColumn);
                    }
                }

                for (final NormalColumn removedColumn : foreignKeyColumns) {
                    if (removedColumn.isPrimaryKey()) {
                        isPrimaryChanged = true;
                    }
                    targetTable.removeColumn(removedColumn);
                }

                if (isPrimaryChanged) {
                    final List<NormalColumn> nextNewPrimaryKeyColumns = ((ERTable) targetTable).getPrimaryKeys();

                    setTargetTableRelation(targetTable, nextNewPrimaryKeyColumns);
                }

                // targetTable.setDirty();
            }
        }
    }

    @Override
    public int compareTo(final TableView other) {
        return PHYSICAL_NAME_COMPARATOR.compare(this, other);
    }

    public void replaceColumnGroup(final ColumnGroup oldColumnGroup, final ColumnGroup newColumnGroup) {
        final int index = columns.indexOf(oldColumnGroup);
        if (index != -1) {
            columns.remove(index);
            columns.add(index, newColumnGroup);
        }
    }

    public String getNameWithSchema(final String database) {
        final StringBuilder sb = new StringBuilder();

        final DBManager dbManager = DBManagerFactory.getDBManager(database);

        if (!dbManager.isSupported(DBManager.SUPPORT_SCHEMA)) {
            return Format.null2blank(getPhysicalName());
        }

        final TableViewProperties commonTableViewProperties = getDiagram().getDiagramContents().getSettings().getTableViewProperties();

        String schema = getTableViewProperties().getSchema();

        if (schema == null || schema.equals("")) {
            schema = commonTableViewProperties.getSchema();
        }

        if (schema != null && !schema.equals("")) {
            sb.append(schema);
            sb.append(".");
        }

        sb.append(getPhysicalName());

        return sb.toString();
    }

    public abstract TableView copyData();

    private static class TableViewPhysicalNameComparator implements Comparator<TableView> {

        @Override
        public int compare(final TableView o1, final TableView o2) {
            if (o1 == o2) {
                return 0;
            }
            if (o2 == null) {
                return -1;
            }
            if (o1 == null) {
                return 1;
            }

            final int compareTo = Format.null2blank(o1.getTableViewProperties().getSchema()).toUpperCase().compareTo(Format.null2blank(o2.getTableViewProperties().getSchema()).toUpperCase());

            if (compareTo != 0) {
                return compareTo;
            }

            int value = 0;

            value = Format.null2blank(o1.physicalName).toUpperCase().compareTo(Format.null2blank(o2.physicalName).toUpperCase());
            if (value != 0) {
                return value;
            }

            value = Format.null2blank(o1.logicalName).toUpperCase().compareTo(Format.null2blank(o2.logicalName).toUpperCase());
            if (value != 0) {
                return value;
            }

            return 0;
        }

    }

    private static class TableViewLogicalNameComparator implements Comparator<TableView> {

        @Override
        public int compare(final TableView o1, final TableView o2) {
            if (o1 == o2) {
                return 0;
            }
            if (o2 == null) {
                return -1;
            }
            if (o1 == null) {
                return 1;
            }

            final int compareTo = Format.null2blank(o1.getTableViewProperties().getSchema()).toUpperCase().compareTo(Format.null2blank(o2.getTableViewProperties().getSchema()).toUpperCase());

            if (compareTo != 0) {
                return compareTo;
            }

            int value = 0;

            value = Format.null2blank(o1.logicalName).toUpperCase().compareTo(Format.null2blank(o2.logicalName).toUpperCase());
            if (value != 0) {
                return value;
            }

            value = Format.null2blank(o1.physicalName).toUpperCase().compareTo(Format.null2blank(o2.physicalName).toUpperCase());
            if (value != 0) {
                return value;
            }

            return 0;
        }
    }
}
