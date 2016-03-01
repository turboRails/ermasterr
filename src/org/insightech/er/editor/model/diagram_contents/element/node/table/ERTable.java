package org.insightech.er.editor.model.diagram_contents.element.node.table;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.ResourceString;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.ColumnHolder;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.CopyIndex;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TablePropertiesHolder;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.CopyComplexUniqueKey;

public class ERTable extends TableView implements TablePropertiesHolder, ColumnHolder, ObjectModel {

    private static final long serialVersionUID = 11185865758118654L;

    public static final String NEW_PHYSICAL_NAME = ResourceString.getResourceString("new.table.physical.name");

    public static final String NEW_LOGICAL_NAME = ResourceString.getResourceString("new.table.logical.name");

    private String constraint;

    private String primaryKeyName;

    private String option;

    private List<Index> indexes;

    private List<ComplexUniqueKey> complexUniqueKeyList;

    public ERTable() {
        indexes = new ArrayList<Index>();
        complexUniqueKeyList = new ArrayList<ComplexUniqueKey>();
    }

    public NormalColumn getAutoIncrementColumn() {
        for (final Column column : columns) {
            if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;
                if (normalColumn.isAutoIncrement()) {
                    return normalColumn;
                }
            }
        }

        return null;
    }

    @Override
    public TableViewProperties getTableViewProperties() {
        tableViewProperties = DBManagerFactory.getDBManager(getDiagram()).createTableProperties((TableProperties) tableViewProperties);

        return tableViewProperties;
    }

    public TableViewProperties getTableViewProperties(final String database) {
        tableViewProperties = DBManagerFactory.getDBManager(database).createTableProperties((TableProperties) tableViewProperties);

        return tableViewProperties;
    }

    public void addIndex(final Index index) {
        indexes.add(index);
    }

    @Override
    public ERTable copyData() {
        final ERTable to = new ERTable();

        to.setConstraint(getConstraint());
        to.setPrimaryKeyName(getPrimaryKeyName());
        to.setOption(getOption());

        super.copyTableViewData(to);

        final List<Index> indexes = new ArrayList<Index>();

        for (final Index fromIndex : getIndexes()) {
            indexes.add(new CopyIndex(to, fromIndex, to.getColumns()));
        }

        to.setIndexes(indexes);

        final List<ComplexUniqueKey> complexUniqueKeyList = new ArrayList<ComplexUniqueKey>();

        for (final ComplexUniqueKey complexUniqueKey : getComplexUniqueKeyList()) {
            complexUniqueKeyList.add(new CopyComplexUniqueKey(complexUniqueKey, to.getColumns()));
        }

        to.complexUniqueKeyList = complexUniqueKeyList;

        to.tableViewProperties = this.getTableViewProperties().clone();

        return to;
    }

    @Override
    public void restructureData(final TableView to) {
        final ERTable table = (ERTable) to;

        table.setConstraint(getConstraint());
        table.setPrimaryKeyName(getPrimaryKeyName());
        table.setOption(getOption());

        super.restructureData(to);

        final List<Index> indexes = new ArrayList<Index>();

        for (final Index fromIndex : getIndexes()) {
            final CopyIndex copyIndex = (CopyIndex) fromIndex;
            final Index restructuredIndex = copyIndex.getRestructuredIndex(table);
            indexes.add(restructuredIndex);
        }
        table.setIndexes(indexes);

        final List<ComplexUniqueKey> complexUniqueKeyList = new ArrayList<ComplexUniqueKey>();

        for (final ComplexUniqueKey complexUniqueKey : getComplexUniqueKeyList()) {
            final CopyComplexUniqueKey copyComplexUniqueKey = (CopyComplexUniqueKey) complexUniqueKey;
            if (!copyComplexUniqueKey.isRemoved(getNormalColumns())) {
                final ComplexUniqueKey restructuredComplexUniqueKey = copyComplexUniqueKey.restructure();
                complexUniqueKeyList.add(restructuredComplexUniqueKey);
            }
        }
        table.complexUniqueKeyList = complexUniqueKeyList;

        table.tableViewProperties = tableViewProperties.clone();
    }

    public int getPrimaryKeySize() {
        int count = 0;

        for (final Column column : columns) {
            if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;

                if (normalColumn.isPrimaryKey()) {
                    count++;
                }
            }
        }

        return count;
    }

    public List<NormalColumn> getPrimaryKeys() {
        final List<NormalColumn> primaryKeys = new ArrayList<NormalColumn>();

        for (final Column column : columns) {
            if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;

                if (normalColumn.isPrimaryKey()) {
                    primaryKeys.add(normalColumn);
                }
            }
        }

        return primaryKeys;
    }

    public boolean isReferable() {
        if (getPrimaryKeySize() > 0) {
            return true;
        }

        if (complexUniqueKeyList.size() > 0) {
            return true;
        }

        for (final Column column : columns) {
            if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;

                if (normalColumn.isUniqueKey()) {
                    return true;
                }
            }
        }

        return false;
    }

    public Index getIndex(final int index) {
        return indexes.get(index);
    }

    public void removeIndex(final int index) {
        indexes.remove(index);
    }

    public List<Index> getIndexes() {
        return indexes;
    }

    public void setIndexes(final List<Index> indexes) {
        this.indexes = indexes;
    }

    public void setComplexUniqueKeyList(final List<ComplexUniqueKey> complexUniqueKeyList) {
        this.complexUniqueKeyList = complexUniqueKeyList;
    }

    public List<ComplexUniqueKey> getComplexUniqueKeyList() {
        return complexUniqueKeyList;
    }

    public void setTableViewProperties(final TableProperties tableProperties) {
        tableViewProperties = tableProperties;
    }

    public List<Relation> getSelfRelations() {
        final List<Relation> relations = new ArrayList<Relation>();

        for (final ConnectionElement connection : getOutgoings()) {
            if (connection instanceof Relation) {
                if (connection.getSource() == connection.getTarget()) {
                    relations.add((Relation) connection);
                }
            }
        }

        return relations;
    }

    @Override
    public ERTable clone() {
        final ERTable clone = (ERTable) super.clone();

        final TableProperties cloneTableProperties = (TableProperties) this.getTableViewProperties().clone();
        clone.tableViewProperties = cloneTableProperties;

        return clone;
    }

    public String getConstraint() {
        return constraint;
    }

    public void setConstraint(final String constraint) {
        this.constraint = constraint;
    }

    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    public void setPrimaryKeyName(final String primaryKeyName) {
        this.primaryKeyName = primaryKeyName;
    }

    public String getOption() {
        return option;
    }

    public void setOption(final String option) {
        this.option = option;
    }

    public static boolean isRecursive(final TableView source, final TableView target) {
        for (final Relation relation : source.getIncomingRelations()) {
            final TableView temp = relation.getSourceTableView();
            if (temp.equals(source)) {
                continue;
            }

            if (temp.equals(target)) {
                return true;
            }

            if (isRecursive(temp, target)) {
                return true;
            }
        }

        return false;
    }

    public Relation createRelation() {
        boolean referenceForPK = false;
        ComplexUniqueKey referencedComplexUniqueKey = null;
        NormalColumn referencedColumn = null;
        boolean notNull = false;

        if (getPrimaryKeySize() > 0) {
            referenceForPK = true;
            notNull = true;

        } else if (getComplexUniqueKeyList().size() > 0) {
            referencedComplexUniqueKey = getComplexUniqueKeyList().get(0);
            notNull = referencedComplexUniqueKey.getColumnList().get(0).isNotNull();

        } else {
            for (final NormalColumn normalColumn : getNormalColumns()) {
                if (normalColumn.isUniqueKey()) {
                    referencedColumn = normalColumn;
                    notNull = referencedColumn.isNotNull();
                    break;
                }
            }
        }

        return new Relation(referenceForPK, referencedComplexUniqueKey, referencedColumn, notNull, false);
    }

    @Override
    public String getObjectType() {
        return "table";
    }

    @Override
    public String toString() {
        return "name:" + getName() + ", " + super.toString();
    }

}
