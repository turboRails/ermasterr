package org.insightech.er.editor.model.testdata;

import java.util.HashMap;
import java.util.Map;

import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class RepeatTestData implements Cloneable {

    private int testDataNum;

    private final Map<NormalColumn, RepeatTestDataDef> dataDefMap;

    public RepeatTestData() {
        dataDefMap = new HashMap<NormalColumn, RepeatTestDataDef>();
    }

    public RepeatTestDataDef getDataDef(final NormalColumn normalColumn) {
        RepeatTestDataDef dataDef = dataDefMap.get(normalColumn);

        if (dataDef == null) {
            dataDef = createDataDef(normalColumn);
            dataDefMap.put(normalColumn, dataDef);
        }

        return dataDef;
    }

    public RepeatTestDataDef setDataDef(final NormalColumn normalColumn, final RepeatTestDataDef dataDef) {
        return dataDefMap.put(normalColumn, dataDef);
    }

    public int getTestDataNum() {
        return testDataNum;
    }

    public void setTestDataNum(final int testDataNum) {
        this.testDataNum = testDataNum;
    }

    @Override
    public RepeatTestData clone() {
        final RepeatTestData clone = new RepeatTestData();

        clone.testDataNum = testDataNum;

        for (final Map.Entry<NormalColumn, RepeatTestDataDef> entry : dataDefMap.entrySet()) {
            final RepeatTestDataDef cloneTemplateTestDataDef = entry.getValue().clone();
            clone.dataDefMap.put(entry.getKey(), cloneTemplateTestDataDef);
        }

        return clone;
    }

    private RepeatTestDataDef createDataDef(final NormalColumn normalColumn) {
        final RepeatTestDataDef dataDef = new RepeatTestDataDef();

        final SqlType sqlType = normalColumn.getType();
        final Integer length = normalColumn.getTypeData().getLength();

        dataDef.setFrom("1");
        dataDef.setIncrement("1");
        dataDef.setRepeatNum(1);

        if (length != null) {
            if (length == 1) {
                dataDef.setTo("9");

            } else if (length == 2) {
                dataDef.setTo("99");

            } else {
                dataDef.setTo("100");
            }

        } else {
            dataDef.setTo("100");
        }

        if (normalColumn.isForeignKey()) {
            dataDef.setType(RepeatTestDataDef.TYPE_FOREIGNKEY);

        } else {
            dataDef.setType(RepeatTestDataDef.TYPE_FORMAT);

        }

        String template = null;
        String[] selects = null;

        if (sqlType == null) {
            final String prefix = normalColumn.getName() + "_";

            template = prefix + "%";
            selects = new String[] {prefix + "1", prefix + "2", prefix + "3", prefix + "4"};

        } else if (sqlType.isNumber()) {
            template = "%";
            selects = new String[] {"1", "2", "3", "4"};

        } else if (sqlType.isTimestamp()) {
            template = "2000-01-% 12:00:00.000";
            selects = new String[] {"2000-01-01 12:00:00.000", "2000-01-02 12:00:00.000", "2000-01-03 12:00:00.000", "2000-01-04 12:00:00.000"};

        } else {
            String prefix = normalColumn.getName();

            if (length != null) {
                if (length < 4) {
                    prefix = "";

                } else {
                    if (prefix.length() > length - 3) {
                        prefix = prefix.substring(0, length - 3) + "_";

                    } else {
                        prefix = prefix + "_";
                    }
                }

            }

            template = prefix + "%";
            selects = new String[] {prefix + "1", prefix + "2", prefix + "3", prefix + "4"};

        }

        dataDef.setTemplate(template);
        dataDef.setSelects(selects);

        return dataDef;
    }
}
