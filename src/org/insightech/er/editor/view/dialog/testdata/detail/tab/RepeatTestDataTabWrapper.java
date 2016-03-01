package org.insightech.er.editor.view.dialog.testdata.detail.tab;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.ValidatableTabWrapper;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.RowHeaderTable;
import org.insightech.er.common.widgets.table.CellEditWorker;
import org.insightech.er.common.widgets.table.HeaderClickListener;
import org.insightech.er.editor.model.dbexport.testdata.TestDataCreator;
import org.insightech.er.editor.model.dbexport.testdata.impl.SQLTestDataCreator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.testdata.RepeatTestData;
import org.insightech.er.editor.model.testdata.RepeatTestDataDef;
import org.insightech.er.editor.view.dialog.testdata.detail.RepeatTestDataSettingDialog;
import org.insightech.er.editor.view.dialog.testdata.detail.TestDataDialog;
import org.insightech.er.util.Format;

public class RepeatTestDataTabWrapper extends ValidatableTabWrapper {

    private static final int MAX_REPEAT_PREVIEW_NUM = 50;

    private final TestDataDialog dialog;

    private Text testDataNumText;

    private RowHeaderTable editColumnTable;

    private RepeatTestData repeatTestData;

    private ERTable table;

    public RepeatTestDataTabWrapper(final TestDataDialog dialog, final TabFolder parent) {
        super(dialog, parent, "label.testdata.repeat.input");

        this.dialog = dialog;
    }

    @Override
    protected void initLayout(final GridLayout layout) {
        super.initLayout(layout);
        layout.numColumns = 2;
    }

    @Override
    public void initComposite() {
        testDataNumText = CompositeFactory.createNumText(dialog, this, "label.record.num", 50);
        testDataNumText.setEnabled(false);

        createEditTable(this);
    }

    private void createEditTable(final Composite composite) {
        editColumnTable = CompositeFactory.createRowHeaderTable(composite, TestDataDialog.TABLE_WIDTH, TestDataDialog.TABLE_HEIGHT, 75, 25, 2, true, true);

        editColumnTable.setCellEditWorker(new CellEditWorker() {

            @Override
            public void addNewRow() {}

            @Override
            public void changeRowNum() {
                dialog.resetTestDataNum();
            }

            @Override
            public boolean isModified(final int row, final int column) {
                final TestDataCreator testDataCreator = new SQLTestDataCreator();
                testDataCreator.init(dialog.getTestData(), null);

                if (column >= table.getExpandedColumns().size()) {
                    return false;
                }

                final NormalColumn normalColumn = table.getExpandedColumns().get(column);

                final RepeatTestDataDef dataDef = repeatTestData.getDataDef(normalColumn);

                String defaultValue = testDataCreator.getRepeatTestDataValue(row, dataDef, normalColumn);
                Object value = editColumnTable.getValueAt(row, column);

                if (defaultValue == null) {
                    defaultValue = "null";
                }
                if (value == null) {
                    value = "null";
                }

                if (!defaultValue.equals(value)) {
                    dataDef.setModifiedValue(row, value.toString());
                    return true;

                } else {
                    dataDef.removeModifiedValue(row);
                }

                return false;
            }

        });

        editColumnTable.setHeaderClickListener(new HeaderClickListener() {

            @Override
            public void onHeaderClick(final int column) {
                getDisplay().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        final RepeatTestDataSettingDialog dialog = new RepeatTestDataSettingDialog(getShell(), column, RepeatTestDataTabWrapper.this, table);
                        dialog.open();
                    }
                });
            }
        });
    }

    private void initTable() {
        editColumnTable.setVisible(false);

        editColumnTable.removeData();

        for (final NormalColumn normalColumn : table.getExpandedColumns()) {
            final String name = normalColumn.getName();
            String type = null;

            if (normalColumn.getType() == null) {
                type = "";

            } else {
                type = Format.formatType(normalColumn.getType(), normalColumn.getTypeData(), dialog.getDiagram().getDatabase(), true);
            }

            editColumnTable.addColumnHeader(name + "\r\n" + type, 100);
        }

        initTableData();

        editColumnTable.setVisible(true);
    }

    @Override
    public void reset() {
        if (repeatTestData != null) {
            perfomeOK();
        }

        table = dialog.getTargetTable();

        if (table != null) {
            repeatTestData = dialog.getTestData().getTableTestDataMap().get(table).getRepeatTestData();
            testDataNumText.setText(Format.toString(repeatTestData.getTestDataNum()));

            testDataNumText.setEnabled(true);

            initTable();

        } else {
            repeatTestData = null;
            testDataNumText.setText("");
            testDataNumText.setEnabled(false);

            editColumnTable.removeData();

        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validatePage() throws InputException {}

    @Override
    public void setInitFocus() {}

    @Override
    public void perfomeOK() {
        if (repeatTestData != null) {
            repeatTestData.setTestDataNum(getTestDataNum());
        }
    }

    @Override
    protected void addListener() {
        super.addListener();

        testDataNumText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent modifyevent) {
                initTableData();
            }
        });
    }

    public void initTableData() {
        if (table != null) {
            editColumnTable.setVisible(false);

            final TestDataCreator testDataCreator = new SQLTestDataCreator();
            testDataCreator.init(dialog.getTestData(), null);

            editColumnTable.removeAllRow();

            int num = getTestDataNum();

            if (num > MAX_REPEAT_PREVIEW_NUM) {
                num = MAX_REPEAT_PREVIEW_NUM;
            }

            for (int i = 0; i < num; i++) {
                final Object[] values = new Object[table.getExpandedColumns().size()];

                int columnIndex = 0;

                for (final NormalColumn column : table.getExpandedColumns()) {
                    values[columnIndex++] = testDataCreator.getMergedRepeatTestDataValue(i, repeatTestData.getDataDef(column), column);
                }

                editColumnTable.addRow(String.valueOf(editColumnTable.getItemCount() + 1), values);
            }

            editColumnTable.setVisible(true);
        }
    }

    public void setRepeatTestDataDef(final NormalColumn column, final RepeatTestDataDef repeatTestDataDef) {
        repeatTestData.setDataDef(column, repeatTestDataDef);
    }

    public RepeatTestData getRepeatTestData() {
        return repeatTestData;
    }

    public int getTestDataNum() {
        final String text = testDataNumText.getText();
        int num = 0;
        if (!text.equals("")) {
            try {
                num = Integer.parseInt(text);
            } catch (final Exception e) {}
        }

        return num;
    }
}
