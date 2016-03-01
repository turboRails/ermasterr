package org.insightech.er.editor.view.dialog.testdata.detail.tab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.ValidatableTabWrapper;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.RowHeaderTable;
import org.insightech.er.common.widgets.table.CellEditWorker;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.testdata.DirectTestData;
import org.insightech.er.editor.view.dialog.testdata.detail.TestDataDialog;
import org.insightech.er.util.Format;

public class DirectTestDataTabWrapper extends ValidatableTabWrapper {

    private final TestDataDialog dialog;

    private RowHeaderTable editColumnTable;

    private DirectTestData directTestData;

    private ERTable table;

    public DirectTestDataTabWrapper(final TestDataDialog dialog, final TabFolder parent) {
        super(dialog, parent, "label.testdata.direct.input");

        this.dialog = dialog;
    }

    @Override
    protected void initLayout(final GridLayout layout) {
        super.initLayout(layout);
        layout.numColumns = 2;
    }

    @Override
    public void initComposite() {
        final Text dummy = CompositeFactory.createNumText(dialog, this, "", 50);
        dummy.setVisible(false);

        createEditTable(this);
    }

    private void createEditTable(final Composite composite) {
        editColumnTable = CompositeFactory.createRowHeaderTable(composite, TestDataDialog.TABLE_WIDTH, TestDataDialog.TABLE_HEIGHT, 75, 25, 2, false, true);
        editColumnTable.setCellEditWorker(new CellEditWorker() {

            @Override
            public void addNewRow() {
                addNewRowToTable();
            }

            @Override
            public void changeRowNum() {
                dialog.resetTestDataNum();
            }

            @Override
            public boolean isModified(final int row, final int column) {
                return false;
            }

        });
    }

    @Override
    protected void setData() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void validatePage() throws InputException {
        saveTableData();
    }

    @Override
    public void setInitFocus() {}

    @Override
    public void reset() {
        saveTableData();

        table = dialog.getTargetTable();

        if (table != null) {
            directTestData = dialog.getTestData().getTableTestDataMap().get(table).getDirectTestData();

            initTable();

        } else {
            directTestData = null;
            editColumnTable.removeData();

        }
    }

    private void saveTableData() {
        if (directTestData != null) {
            final List<Map<NormalColumn, String>> dataList = new ArrayList<Map<NormalColumn, String>>();

            final List<NormalColumn> normalColumnList = table.getExpandedColumns();

            for (int row = 0; row < editColumnTable.getItemCount() - 1; row++) {
                final Map<NormalColumn, String> data = new HashMap<NormalColumn, String>();

                for (int column = 0; column < normalColumnList.size(); column++) {
                    final NormalColumn normalColumn = normalColumnList.get(column);
                    final String value = (String) editColumnTable.getValueAt(row, column);
                    data.put(normalColumn, value);
                }

                dataList.add(data);
            }

            directTestData.setDataList(dataList);
        }
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

        for (final Map<NormalColumn, String> data : directTestData.getDataList()) {
            addTableItem(data);
        }

        addNewRowToTable();

        editColumnTable.setVisible(true);
    }

    private void addNewRowToTable() {
        editColumnTable.addRow("+", null);
    }

    private void addTableItem(final Map<NormalColumn, String> data) {
        final List<NormalColumn> columns = table.getExpandedColumns();

        final String[] values = new String[columns.size()];

        for (int i = 0; i < columns.size(); i++) {
            values[i] = data.get(columns.get(i));
        }

        editColumnTable.addRow(String.valueOf(editColumnTable.getItemCount() + 1), values);
    }

    @Override
    public void perfomeOK() {}

    public int getTestDataNum() {
        return editColumnTable.getItemCount() - 1;
    }
}
