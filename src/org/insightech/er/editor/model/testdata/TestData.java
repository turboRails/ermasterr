package org.insightech.er.editor.model.testdata;

import java.util.LinkedHashMap;
import java.util.Map;

import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public class TestData implements Cloneable, Comparable<TestData> {

    public static final int EXPORT_FORMT_SQL = 0;

    public static final int EXPORT_FORMT_DBUNIT = 1;

    public static final int EXPORT_FORMT_DBUNIT_FLAT_XML = 2;

    public static final int EXPORT_FORMT_DBUNIT_XLS = 3;

    public static final int EXPORT_ORDER_DIRECT_TO_REPEAT = 0;

    public static final int EXPORT_ORDER_REPEAT_TO_DIRECT = 1;

    private String name;

    private int exportOrder;

    private Map<ERTable, TableTestData> tableTestDataMap;

    public TestData() {
        tableTestDataMap = new LinkedHashMap<ERTable, TableTestData>();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getExportOrder() {
        return exportOrder;
    }

    public void setExportOrder(final int exportOrder) {
        this.exportOrder = exportOrder;
    }

    public Map<ERTable, TableTestData> getTableTestDataMap() {
        return tableTestDataMap;
    }

    public void setTableTestDataMap(final Map<ERTable, TableTestData> tableTestDataMap) {
        this.tableTestDataMap = tableTestDataMap;
    }

    public void putTableTestData(final ERTable table, final TableTestData tableTestData) {
        tableTestDataMap.put(table, tableTestData);
    }

    public boolean contains(final ERTable table) {
        return tableTestDataMap.containsKey(table);
    }

    public ERTable get(final int index) {
        int i = 0;

        for (final ERTable table : tableTestDataMap.keySet()) {
            if (i == index) {
                return table;
            }
            i++;
        }

        return null;
    }

    public void removeTableTestData(final int index) {
        int i = 0;

        for (final ERTable table : tableTestDataMap.keySet()) {
            if (i == index) {
                tableTestDataMap.remove(table);
                break;
            }

            i++;
        }
    }

    @Override
    public TestData clone() {
        final TestData clone = new TestData();

        clone.name = name;
        clone.exportOrder = exportOrder;

        for (final Map.Entry<ERTable, TableTestData> entry : tableTestDataMap.entrySet()) {
            final TableTestData cloneTableTestData = entry.getValue().clone();
            clone.tableTestDataMap.put(entry.getKey(), cloneTableTestData);
        }

        return clone;
    }

    @Override
    public int compareTo(final TestData other) {
        if (other == null) {
            return -1;
        }

        if (name == null) {
            return 1;
        }
        if (other.name == null) {
            return -1;
        }

        return name.toUpperCase().compareTo(other.name.toUpperCase());
    }
}
