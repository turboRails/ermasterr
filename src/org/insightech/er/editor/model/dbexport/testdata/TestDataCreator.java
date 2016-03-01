package org.insightech.er.editor.model.dbexport.testdata;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.settings.export.ExportTestDataSetting;
import org.insightech.er.editor.model.testdata.DirectTestData;
import org.insightech.er.editor.model.testdata.RepeatTestData;
import org.insightech.er.editor.model.testdata.RepeatTestDataDef;
import org.insightech.er.editor.model.testdata.TableTestData;
import org.insightech.er.editor.model.testdata.TestData;

public abstract class TestDataCreator {

    protected ERDiagram diagram;

    protected File baseDir;

    protected ExportTestDataSetting exportTestDataSetting;

    protected TestData testData;

    protected Map<NormalColumn, List<String>> valueListMap;

    public TestDataCreator() {}

    public void init(final TestData testData, final File baseDir) {
        this.testData = testData;
        this.baseDir = baseDir;
        valueListMap = new HashMap<NormalColumn, List<String>>();
    }

    public String getMergedRepeatTestDataValue(final int count, final RepeatTestDataDef repeatTestDataDef, final NormalColumn column) {
        final String modifiedValue = repeatTestDataDef.getModifiedValues().get(count);

        if (modifiedValue != null) {
            return modifiedValue;

        } else {
            final String value = getRepeatTestDataValue(count, repeatTestDataDef, column);

            if (value == null) {
                return "null";
            }

            return value;
        }
    }

    public String getRepeatTestDataValue(final int count, final RepeatTestDataDef repeatTestDataDef, final NormalColumn column) {
        if (repeatTestDataDef == null) {
            return null;
        }

        final String type = repeatTestDataDef.getType();
        final int repeatNum = repeatTestDataDef.getRepeatNum();

        if (RepeatTestDataDef.TYPE_FORMAT.equals(type)) {
            final String fromStr = repeatTestDataDef.getFrom();
            final String incrementStr = repeatTestDataDef.getIncrement();
            final String toStr = repeatTestDataDef.getTo();

            int fromDecimalPlaces = 0;
            if (fromStr.indexOf(".") != -1) {
                fromDecimalPlaces = fromStr.length() - fromStr.indexOf(".") - 1;
            }
            int incrementDecimalPlaces = 0;
            if (incrementStr.indexOf(".") != -1) {
                incrementDecimalPlaces = incrementStr.length() - incrementStr.indexOf(".") - 1;
            }
            int toDecimalPlaces = 0;
            if (toStr.indexOf(".") != -1) {
                toDecimalPlaces = toStr.length() - toStr.indexOf(".") - 1;
            }

            final int decimalPlaces = Math.max(Math.max(fromDecimalPlaces, incrementDecimalPlaces), toDecimalPlaces);
            final int from = (int) (Double.parseDouble(fromStr) * Math.pow(10, decimalPlaces));
            final int increment = (int) (Double.parseDouble(incrementStr) * Math.pow(10, decimalPlaces));
            final int to = (int) (Double.parseDouble(toStr) * Math.pow(10, decimalPlaces));

            final String template = repeatTestDataDef.getTemplate();

            int num = from;

            if (repeatNum != 0 && to - from + 1 != 0) {
                num = from + (((count / repeatNum) * increment) % (to - from + 1));
            }

            String value = null;

            if (decimalPlaces == 0) {
                value = template.replaceAll("%", String.valueOf(num));

            } else {
                value = template.replaceAll("%", String.valueOf(num / Math.pow(10, decimalPlaces)));
            }

            if (column.getType() != null && column.getType().isTimestamp()) {
                final SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

                try {
                    value = format1.format(format1.parse(value));

                } catch (final ParseException e1) {
                    final SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    try {
                        value = format2.format(format2.parse(value));

                    } catch (final ParseException e2) {
                        final SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd");

                        try {
                            value = format3.format(format3.parse(value));

                        } catch (final ParseException e3) {}
                    }

                }

            }

            return value;

        } else if (RepeatTestDataDef.TYPE_FOREIGNKEY.equals(type)) {
            final NormalColumn referencedColumn = column.getFirstReferencedColumn();
            if (referencedColumn == null) {
                return null;
            }

            final List<String> referencedValueList = getValueList(referencedColumn);

            if (referencedValueList.size() == 0) {
                return null;
            }

            final int index = (count / repeatNum) % referencedValueList.size();

            return referencedValueList.get(index);

        } else if (RepeatTestDataDef.TYPE_ENUM.equals(type)) {
            final String[] selects = repeatTestDataDef.getSelects();

            if (selects.length == 0) {
                return null;
            }

            return selects[(count / repeatNum) % selects.length];
        }

        return null;
    }

    private List<String> getValueList(final NormalColumn column) {
        List<String> valueList = valueListMap.get(column);

        if (valueList == null) {
            valueList = new ArrayList<String>();

            final ERTable table = (ERTable) column.getColumnHolder();
            final TableTestData tableTestData = testData.getTableTestDataMap().get(table);

            if (tableTestData != null) {
                final DirectTestData directTestData = tableTestData.getDirectTestData();
                final RepeatTestData repeatTestData = tableTestData.getRepeatTestData();

                if (testData.getExportOrder() == TestData.EXPORT_ORDER_DIRECT_TO_REPEAT) {
                    for (final Map<NormalColumn, String> data : directTestData.getDataList()) {
                        final String value = data.get(column);
                        valueList.add(value);
                    }

                    for (int i = 0; i < repeatTestData.getTestDataNum(); i++) {
                        final String value = getMergedRepeatTestDataValue(i, repeatTestData.getDataDef(column), column);
                        valueList.add(value);
                    }

                } else {
                    for (int i = 0; i < repeatTestData.getTestDataNum(); i++) {
                        final String value = getRepeatTestDataValue(i, repeatTestData.getDataDef(column), column);
                        valueList.add(value);
                    }

                    for (final Map<NormalColumn, String> data : directTestData.getDataList()) {
                        final String value = data.get(column);
                        valueList.add(value);
                    }

                }
            }
        }

        return valueList;
    }

    final public void write(final ExportTestDataSetting exportTestDataSetting, final ERDiagram diagram) throws Exception {
        this.exportTestDataSetting = exportTestDataSetting;
        this.diagram = diagram;
        this.diagram.getDiagramContents().sort();

        try {
            openFile();

            this.write();

        } finally {
            closeFile();
        }
    }

    protected abstract void openFile() throws IOException;

    protected void write() throws Exception {
        for (final Map.Entry<ERTable, TableTestData> entry : testData.getTableTestDataMap().entrySet()) {
            final ERTable table = entry.getKey();

            if (skipTable(table)) {
                continue;
            }

            final TableTestData tableTestData = entry.getValue();

            final DirectTestData directTestData = tableTestData.getDirectTestData();
            final RepeatTestData repeatTestData = tableTestData.getRepeatTestData();

            writeTableHeader(diagram, table);

            if (testData.getExportOrder() == TestData.EXPORT_ORDER_DIRECT_TO_REPEAT) {
                for (final Map<NormalColumn, String> data : directTestData.getDataList()) {
                    writeDirectTestData(table, data, diagram.getDatabase());
                }

                writeRepeatTestData(table, repeatTestData, diagram.getDatabase());

            } else {
                writeRepeatTestData(table, repeatTestData, diagram.getDatabase());

                for (final Map<NormalColumn, String> data : directTestData.getDataList()) {
                    writeDirectTestData(table, data, diagram.getDatabase());
                }
            }

            writeTableFooter(table);
        }

    }

    protected abstract boolean skipTable(ERTable table);

    protected abstract void writeTableHeader(ERDiagram diagram, ERTable table);

    protected abstract void writeTableFooter(ERTable table);

    protected abstract void writeDirectTestData(ERTable table, Map<NormalColumn, String> data, String database);

    protected abstract void writeRepeatTestData(ERTable table, RepeatTestData repeatTestData, String database);

    protected abstract void closeFile() throws IOException;
}
