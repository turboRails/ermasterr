package org.insightech.er.editor.model.diagram_contents.element.node.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.ResourceString;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ObjectListModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class TableSet extends AbstractModel implements ObjectListModel, Iterable<ERTable> {

    private static final long serialVersionUID = 5264397678674390103L;

    private List<ERTable> tableList;

    public TableSet() {
        tableList = new ArrayList<ERTable>();
    }

    public void sort() {
        Collections.sort(tableList);

        for (final ERTable table : tableList) {
            Collections.sort(table.getOutgoings());
            Collections.sort(table.getIncomings());
        }
    }

    public void add(final ERTable table) {
        tableList.add(table);
    }

    public int remove(final ERTable table) {
        final int index = tableList.indexOf(table);
        tableList.remove(index);

        return index;
    }

    public void setDirty() {}

    public List<ERTable> getList() {
        return tableList;
    }

    @Override
    public Iterator<ERTable> iterator() {
        return tableList.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TableSet clone() {
        final TableSet tableSet = (TableSet) super.clone();
        final List<ERTable> newTableList = new ArrayList<ERTable>();

        for (final ERTable table : tableList) {
            final ERTable newTable = table.clone();
            newTableList.add(newTable);
        }

        tableSet.tableList = newTableList;

        return tableSet;
    }

    public List<String> getAutoSequenceNames(final String database) {
        final List<String> autoSequenceNames = new ArrayList<String>();

        for (final ERTable table : tableList) {
            final String prefix = table.getNameWithSchema(database) + "_";

            for (final NormalColumn column : table.getNormalColumns()) {
                final SqlType sqlType = column.getType();

                if (SqlType.valueOfId(SqlType.SQL_TYPE_ID_SERIAL).equals(sqlType) || SqlType.valueOfId(SqlType.SQL_TYPE_ID_BIG_SERIAL).equals(sqlType)) {
                    autoSequenceNames.add((prefix + column.getPhysicalName() + "_seq").toUpperCase());
                }
            }
        }

        return autoSequenceNames;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getName() {
        return ResourceString.getResourceString("label.object.type.table_list");
    }

    @Override
    public String getObjectType() {
        return "list";
    }

}
