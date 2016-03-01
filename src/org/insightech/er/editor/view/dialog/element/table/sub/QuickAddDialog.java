package org.insightech.er.editor.view.dialog.element.table.sub;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.RowHeaderTable;
import org.insightech.er.common.widgets.table.CellEditWorker;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.CopyWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;

public class QuickAddDialog extends AbstractDialog {

    private RowHeaderTable editColumnTable;

    private final ERDiagram diagram;

    private final List<NormalColumn> columnList;

    public QuickAddDialog(final Shell parentShell, final ERDiagram diagram) {
        super(parentShell);

        this.diagram = diagram;
        columnList = new ArrayList<NormalColumn>();
    }

    @Override
    protected void initialize(final Composite composite) {
        editColumnTable = CompositeFactory.createRowHeaderTable(composite, 695, 350, 75, 25, 1, false, true);

        editColumnTable.setCellEditWorker(new CellEditWorker() {

            @Override
            public void addNewRow() {
                addNewRowToTable();
            }

            @Override
            public void changeRowNum() {}

            @Override
            public boolean isModified(final int row, final int column) {
                return false;
            }

        });
    }

    private void addNewRowToTable() {
        editColumnTable.addRow("+", null);
    }

    @Override
    protected String getErrorMessage() {
        return null;
    }

    @Override
    protected String getTitle() {
        return "label.button.quick.add";
    }

    @Override
    protected void perfomeOK() throws InputException {
        for (int row = 0; row < editColumnTable.getItemCount() - 1; row++) {
            final String logicalName = (String) editColumnTable.getValueAt(row, 0);
            final String physicalName = (String) editColumnTable.getValueAt(row, 1);
            final String type = (String) editColumnTable.getValueAt(row, 2);

            int length = 0;
            try {
                length = Integer.parseInt((String) editColumnTable.getValueAt(row, 3));
            } catch (final NumberFormatException e) {}

            int decimal = 0;
            try {
                decimal = Integer.parseInt((String) editColumnTable.getValueAt(row, 4));
            } catch (final NumberFormatException e) {}

            final SqlType sqlType = SqlType.valueOf(diagram.getDatabase(), type, length, decimal);

            final TypeData typeData = new TypeData(length, decimal, false, null, false, false, false, null, false);

            final Word word = new CopyWord(new Word(physicalName, logicalName, sqlType, typeData, null, diagram.getDatabase()));

            final NormalColumn column = new NormalColumn(word, false, false, false, false, null, null, null, null, null);

            columnList.add(column);
        }
    }

    @Override
    protected void setData() {
        initTable();
    }

    private void initTable() {
        editColumnTable.setVisible(false);

        editColumnTable.removeData();

        editColumnTable.addColumnHeader(ResourceString.getResourceString("label.logical.name"), 150);
        editColumnTable.addColumnHeader(ResourceString.getResourceString("label.physical.name"), 150);
        editColumnTable.addColumnHeader(ResourceString.getResourceString("label.column.type"), 100);
        editColumnTable.addColumnHeader(ResourceString.getResourceString("label.column.length"), 100);
        editColumnTable.addColumnHeader(ResourceString.getResourceString("label.column.decimal"), 100);

        addNewRowToTable();

        editColumnTable.setVisible(true);
    }

    public List<NormalColumn> getColumnList() {
        return columnList;
    }

}
