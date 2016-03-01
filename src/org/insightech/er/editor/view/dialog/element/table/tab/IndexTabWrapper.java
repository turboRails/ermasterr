package org.insightech.er.editor.view.dialog.element.table.tab;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.common.dialog.AbstractTabbedDialog;
import org.insightech.er.common.dialog.ValidatableTabWrapper;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.CopyIndex;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.view.dialog.element.table.sub.IndexDialog;
import org.insightech.er.util.Format;

public class IndexTabWrapper extends ValidatableTabWrapper {

    private final ERTable copyData;

    private Table indexTable;

    private final List<Button> checkButtonList;

    private final List<TableEditor> editorList;

    private Button addButton;

    private Button editButton;

    private Button deleteButton;

    public IndexTabWrapper(final AbstractTabbedDialog dialog, final TabFolder parent, final ERTable copyData) {
        super(dialog, parent, "label.index");

        this.copyData = copyData;

        checkButtonList = new ArrayList<Button>();
        editorList = new ArrayList<TableEditor>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validatePage() throws InputException {
        resutuctIndexData();
    }

    @Override
    protected void initLayout(final GridLayout layout) {
        super.initLayout(layout);
        layout.numColumns = 3;
    }

    @Override
    public void initComposite() {
        initTable(this);
        initTableButton(this);

        setTableData();
    }

    private void initTable(final Composite parent) {
        final GridData gridData = new GridData();
        gridData.horizontalSpan = 3;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.heightHint = 200;

        indexTable = new Table(parent, SWT.BORDER | SWT.HIDE_SELECTION);

        indexTable.setHeaderVisible(true);
        indexTable.setLayoutData(gridData);
        indexTable.setLinesVisible(true);

        CompositeFactory.createTableColumn(indexTable, "label.column.name", -1);
        final TableColumn separatorColumn = CompositeFactory.createTableColumn(indexTable, "", 3);
        separatorColumn.setResizable(false);
    }

    private void initTableButton(final Composite parent) {
        final Composite buttonComposite = CompositeFactory.createChildComposite(parent, 1, 3);

        addButton = CompositeFactory.createSmallButton(buttonComposite, "label.button.add");
        editButton = CompositeFactory.createSmallButton(buttonComposite, "label.button.edit");
        deleteButton = CompositeFactory.createSmallButton(buttonComposite, "label.button.delete");
    }

    @Override
    protected void addListener() {
        addButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent event) {
                final IndexDialog dialog = new IndexDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null, copyData);

                if (dialog.open() == IDialogConstants.OK_ID) {
                    addIndexData(dialog.getResultIndex(), true);
                }
            }
        });

        editButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent event) {
                final Index targetIndex = getTargetIndex();
                if (targetIndex == null) {
                    return;
                }

                final IndexDialog dialog = new IndexDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), targetIndex, copyData);

                if (dialog.open() == IDialogConstants.OK_ID) {
                    addIndexData(dialog.getResultIndex(), false);
                }
            }
        });

        deleteButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent event) {
                removeIndex();
            }
        });
    }

    private void setTableData() {
        final List<Index> indexes = copyData.getIndexes();

        final TableItem radioTableItem = new TableItem(indexTable, SWT.NONE);

        for (int i = 0; i < indexes.size(); i++) {
            final TableColumn tableColumn = CompositeFactory.createTableColumn(indexTable, "Index" + (i + 1), -1, SWT.CENTER);
            tableColumn.setResizable(false);

            final TableEditor editor = new TableEditor(indexTable);

            final Button radioButton = new Button(indexTable, SWT.RADIO);
            radioButton.addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void widgetSelected(final SelectionEvent event) {
                    setButtonEnabled(true);
                }
            });

            radioButton.pack();

            editor.minimumWidth = radioButton.getSize().x;
            editor.horizontalAlignment = SWT.CENTER;
            editor.setEditor(radioButton, radioTableItem, i + 2);

            checkButtonList.add(radioButton);
            editorList.add(editor);
        }

        for (final NormalColumn normalColumn : copyData.getExpandedColumns()) {
            final TableItem tableItem = new TableItem(indexTable, SWT.NONE);
            tableItem.setText(0, Format.null2blank(normalColumn.getName()));

            for (int i = 0; i < indexes.size(); i++) {
                final Index index = indexes.get(i);

                final List<NormalColumn> indexColumns = index.getColumns();
                for (int j = 0; j < indexColumns.size(); j++) {
                    final NormalColumn indexColumn = indexColumns.get(j);

                    if (normalColumn.equals(indexColumn)) {
                        tableItem.setText(i + 2, String.valueOf(j + 1));
                        break;
                    }
                }
            }
        }

        indexTable.getColumns()[0].pack();

        setButtonEnabled(false);
    }

    public void addIndexData(final Index index, final boolean add) {
        int selectedIndex = -1;

        for (int i = 0; i < checkButtonList.size(); i++) {
            final Button checkButton = checkButtonList.get(i);
            if (checkButton.getSelection()) {
                selectedIndex = i;
                break;
            }
        }

        Index copyIndex = null;

        if (add || selectedIndex == -1) {
            copyIndex = new CopyIndex(copyData, index, null);
            copyData.addIndex(copyIndex);

        } else {
            copyIndex = copyData.getIndex(selectedIndex);
            CopyIndex.copyData(index, copyIndex);

        }

        restruct();
    }

    public void removeIndex() {
        int selectedIndex = -1;

        for (int i = 0; i < checkButtonList.size(); i++) {
            final Button checkButton = checkButtonList.get(i);
            if (checkButton.getSelection()) {
                selectedIndex = i;
                break;
            }
        }

        if (selectedIndex == -1) {
            return;
        }

        copyData.removeIndex(selectedIndex);

        restruct();
    }

    @Override
    public void restruct() {
        clearButtonAndEditor();

        while (indexTable.getColumnCount() > 2) {
            final TableColumn tableColumn = indexTable.getColumn(2);
            tableColumn.dispose();
        }

        indexTable.removeAll();

        resutuctIndexData();

        setTableData();
    }

    private void resutuctIndexData() {
        for (final Index index : copyData.getIndexes()) {
            final List<NormalColumn> indexColumns = index.getColumns();

            final Iterator<NormalColumn> columnIterator = indexColumns.iterator();
            final Iterator<Boolean> descIterator = index.getDescs().iterator();

            while (columnIterator.hasNext()) {
                final NormalColumn indexColumn = columnIterator.next();
                descIterator.next();

                if (!copyData.getExpandedColumns().contains(indexColumn)) {
                    columnIterator.remove();
                    descIterator.remove();
                }
            }
        }
    }

    private void clearButtonAndEditor() {
        for (final Button checkButton : checkButtonList) {
            checkButton.dispose();
        }

        checkButtonList.clear();

        for (final TableEditor editor : editorList) {
            editor.dispose();
        }

        editorList.clear();
    }

    public Index getTargetIndex() {
        int selectedIndex = -1;

        for (int i = 0; i < checkButtonList.size(); i++) {
            final Button checkButton = checkButtonList.get(i);
            if (checkButton.getSelection()) {
                selectedIndex = i;
                break;
            }
        }

        if (selectedIndex == -1) {
            return null;
        }

        return copyData.getIndex(selectedIndex);
    }

    private void setButtonEnabled(final boolean enabled) {
        editButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
    }

    @Override
    public void setInitFocus() {}

    @Override
    public void perfomeOK() {}

}
