package org.insightech.er.editor.view.dialog.element.table.sub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class IndexDialog extends AbstractDialog {

    private Text tableNameText;

    private Text nameText;

    private Button addButton;

    private Button removeButton;

    private Button upButton;

    private Button downButton;

    private org.eclipse.swt.widgets.List allColumnList;

    private Table indexColumnList;

    private final List<NormalColumn> selectedColumns;

    private final List<NormalColumn> allColumns;

    private final ERTable table;

    private Combo typeCombo;

    private Text descriptionText;

    private Button uniqueCheckBox;

    private Button fullTextCheckBox;

    private final Index targetIndex;

    private Index resultIndex;

    private final Map<Column, Button> descCheckBoxMap = new HashMap<Column, Button>();

    private final Map<Column, TableEditor> columnCheckMap = new HashMap<Column, TableEditor>();

    public IndexDialog(final Shell parentShell, final Index targetIndex, final ERTable table) {
        super(parentShell);

        this.targetIndex = targetIndex;
        this.table = table;
        allColumns = table.getExpandedColumns();
        selectedColumns = new ArrayList<NormalColumn>();
    }

    @Override
    protected void initLayout(final GridLayout layout) {
        super.initLayout(layout);

        layout.numColumns = 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize(final Composite composite) {
        createHeaderComposite(composite);
        createColumnListComposite(composite);

        nameText.setFocus();
    }

    private void createHeaderComposite(final Composite parent) {
        final Composite composite = CompositeFactory.createChildComposite(parent, 1, 2);
        createCheckComposite(composite);

        tableNameText = CompositeFactory.createText(this, composite, "label.table.name", 1, -1, SWT.READ_ONLY | SWT.BORDER, false, true);
        nameText = CompositeFactory.createText(this, composite, "label.index.name", false, true);
        typeCombo = CompositeFactory.createReadOnlyCombo(this, composite, "label.index.type");

        initTypeCombo();

        descriptionText = CompositeFactory.createTextArea(this, composite, "label.description", -1, 100, 1, true);
    }

    private void initTypeCombo() {
        final java.util.List<String> indexTypeList = DBManagerFactory.getDBManager(table.getDiagram()).getIndexTypeList(table);

        typeCombo.add("");

        for (final String indexType : indexTypeList) {
            typeCombo.add(indexType);
        }
    }

    private void createCheckComposite(final Composite composite) {
        final Composite checkComposite = CompositeFactory.createChildComposite(composite, 2, 4);

        uniqueCheckBox = CompositeFactory.createCheckbox(this, checkComposite, "label.index.unique", false);

        final DBManager dbManager = DBManagerFactory.getDBManager(table.getDiagram());

        if (dbManager.isSupported(DBManager.SUPPORT_FULLTEXT_INDEX)) {
            fullTextCheckBox = CompositeFactory.createCheckbox(this, checkComposite, "label.index.fulltext", false);
        }
    }

    private void createColumnListComposite(final Composite parent) {
        final Composite composite = CompositeFactory.createChildComposite(parent, 220, 1, 3);

        createAllColumnsGroup(composite);

        addButton = CompositeFactory.createAddButton(composite);

        createIndexColumnGroup(composite);

        removeButton = CompositeFactory.createRemoveButton(composite);
    }

    private void createAllColumnsGroup(final Composite composite) {
        final Group group = new Group(composite, SWT.NONE);

        final GridData gridData = new GridData();
        gridData.verticalSpan = 2;
        gridData.horizontalAlignment = GridData.BEGINNING;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessVerticalSpace = true;

        group.setLayoutData(gridData);

        final GridLayout layout = new GridLayout();
        layout.verticalSpacing = 5;
        layout.marginHeight = 10;

        group.setLayout(layout);

        group.setText(ResourceString.getResourceString("label.all.column.list"));

        allColumnList = new org.eclipse.swt.widgets.List(group, SWT.BORDER | SWT.V_SCROLL);

        final GridData allColumnListGridData = new GridData();
        allColumnListGridData.widthHint = 150;
        allColumnListGridData.verticalAlignment = GridData.FILL;
        allColumnListGridData.grabExcessVerticalSpace = true;

        allColumnList.setLayoutData(allColumnListGridData);

        initializeAllList();
    }

    private void initializeAllList() {
        for (final NormalColumn column : allColumns) {
            allColumnList.add(column.getPhysicalName());
        }
    }

    private void createIndexColumnGroup(final Composite composite) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        gridLayout.verticalSpacing = 20;
        gridLayout.marginHeight = 10;

        final GridData gridData = new GridData();
        gridData.verticalSpan = 2;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessVerticalSpace = true;

        final Group group = new Group(composite, SWT.NONE);
        group.setText(ResourceString.getResourceString("label.index.column.list"));
        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        initializeIndexColumnList(group);

        // indexColumnList = new List(group, SWT.BORDER | SWT.V_SCROLL);
        // indexColumnList.setLayoutData(gridData5);

        upButton = CompositeFactory.createUpButton(group);
        downButton = CompositeFactory.createDownButton(group);
    }

    private void initializeIndexColumnList(final Composite parent) {
        final GridData gridData = new GridData();
        gridData.verticalSpan = 2;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessVerticalSpace = true;

        indexColumnList = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER);
        indexColumnList.setHeaderVisible(true);
        indexColumnList.setLayoutData(gridData);
        indexColumnList.setLinesVisible(false);

        final TableColumn tableColumn = new TableColumn(indexColumnList, SWT.CENTER);
        tableColumn.setWidth(150);
        tableColumn.setText(ResourceString.getResourceString("label.column.name"));

        if (DBManagerFactory.getDBManager(table.getDiagram()).isSupported(DBManager.SUPPORT_DESC_INDEX)) {
            final TableColumn tableColumn1 = new TableColumn(indexColumnList, SWT.CENTER);
            tableColumn1.setWidth(50);
            tableColumn1.setText(ResourceString.getResourceString("label.order.desc"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setData() {
        tableNameText.setText(Format.null2blank(table.getPhysicalName()));

        if (targetIndex != null) {
            tableNameText.setText(Format.null2blank(targetIndex.getTable().getPhysicalName()));

            nameText.setText(targetIndex.getName());

            descriptionText.setText(Format.null2blank(targetIndex.getDescription()));

            if (!Check.isEmpty(targetIndex.getType())) {
                boolean selected = false;

                for (int i = 0; i < typeCombo.getItemCount(); i++) {
                    if (typeCombo.getItem(i).equals(targetIndex.getType())) {
                        typeCombo.select(i);
                        selected = true;
                        break;
                    }
                }

                if (!selected) {
                    typeCombo.setText(targetIndex.getType());
                }
            }

            final java.util.List<Boolean> descs = targetIndex.getDescs();
            int i = 0;

            for (final NormalColumn column : targetIndex.getColumns()) {
                Boolean desc = Boolean.FALSE;

                if (descs.size() > i && descs.get(i) != null) {
                    desc = descs.get(i);
                }

                addIndexColumn(column, desc);
                i++;
            }

            uniqueCheckBox.setSelection(!targetIndex.isNonUnique());

            final DBManager dbManager = DBManagerFactory.getDBManager(table.getDiagram());
            if (dbManager.isSupported(DBManager.SUPPORT_FULLTEXT_INDEX)) {
                fullTextCheckBox.setSelection(targetIndex.isFullText());
            }
        }
    }

    private void addIndexColumn(final NormalColumn column, final Boolean desc) {
        final TableItem tableItem = new TableItem(indexColumnList, SWT.NONE);

        tableItem.setText(0, column.getPhysicalName());

        setTableEditor(column, tableItem, desc);

        selectedColumns.add(column);

    }

    private void setTableEditor(final NormalColumn normalColumn, final TableItem tableItem, final Boolean desc) {
        final Button descCheckButton = new Button(indexColumnList, SWT.CHECK);
        descCheckButton.pack();

        if (DBManagerFactory.getDBManager(table.getDiagram()).isSupported(DBManager.SUPPORT_DESC_INDEX)) {

            final TableEditor editor = new TableEditor(indexColumnList);

            editor.minimumWidth = descCheckButton.getSize().x;
            editor.horizontalAlignment = SWT.CENTER;
            editor.setEditor(descCheckButton, tableItem, 1);

            columnCheckMap.put(normalColumn, editor);
        }

        descCheckBoxMap.put(normalColumn, descCheckButton);
        descCheckButton.setSelection(desc.booleanValue());
    }

    @Override
    protected void addListener() {
        upButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final int index = indexColumnList.getSelectionIndex();

                if (index == -1 || index == 0) {
                    return;
                }

                changeColumn(index - 1, index);
                indexColumnList.setSelection(index - 1);
            }

        });

        downButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final int index = indexColumnList.getSelectionIndex();

                if (index == -1 || index == indexColumnList.getItemCount() - 1) {
                    return;
                }

                changeColumn(index, index + 1);
                indexColumnList.setSelection(index + 1);
            }

        });

        addButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final int index = allColumnList.getSelectionIndex();

                if (index == -1) {
                    return;
                }

                final NormalColumn column = allColumns.get(index);
                if (selectedColumns.contains(column)) {
                    return;
                }

                addIndexColumn(column, Boolean.FALSE);

                validate();
            }

        });

        removeButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final int index = indexColumnList.getSelectionIndex();

                if (index == -1) {
                    return;
                }

                indexColumnList.remove(index);
                NormalColumn column = selectedColumns.remove(index);
                descCheckBoxMap.remove(column);

                disposeCheckBox(column);

                for (int i = index; i < indexColumnList.getItemCount(); i++) {
                    column = selectedColumns.get(i);

                    final Button descCheckBox = descCheckBoxMap.get(column);
                    final boolean desc = descCheckBox.getSelection();
                    disposeCheckBox(column);

                    final TableItem tableItem = indexColumnList.getItem(i);
                    setTableEditor(column, tableItem, desc);
                }

                validate();
            }

        });

    }

    public void changeColumn(final int index1, final int index2) {
        final NormalColumn column1 = selectedColumns.remove(index1);
        NormalColumn column2 = null;

        if (index1 < index2) {
            column2 = selectedColumns.remove(index2 - 1);
            selectedColumns.add(index1, column2);
            selectedColumns.add(index2, column1);

        } else if (index1 > index2) {
            column2 = selectedColumns.remove(index2);
            selectedColumns.add(index1 - 1, column2);
            selectedColumns.add(index2, column1);
        }

        final boolean desc1 = descCheckBoxMap.get(column1).getSelection();
        final boolean desc2 = descCheckBoxMap.get(column2).getSelection();

        final TableItem[] tableItems = indexColumnList.getItems();

        column2TableItem(column1, desc1, tableItems[index2]);
        column2TableItem(column2, desc2, tableItems[index1]);

    }

    private void column2TableItem(final NormalColumn column, final boolean desc, final TableItem tableItem) {
        disposeCheckBox(column);

        tableItem.setText(0, column.getPhysicalName());

        setTableEditor(column, tableItem, new Boolean(desc));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void perfomeOK() {
        final String text = nameText.getText();

        resultIndex = new Index(table, text, !uniqueCheckBox.getSelection(), typeCombo.getText(), null);
        resultIndex.setDescription(descriptionText.getText().trim());

        for (final NormalColumn selectedColumn : selectedColumns) {
            final Boolean desc = Boolean.valueOf(descCheckBoxMap.get(selectedColumn).getSelection());
            resultIndex.addColumn(selectedColumn, desc);
        }

        final DBManager dbManager = DBManagerFactory.getDBManager(table.getDiagram());
        if (dbManager.isSupported(DBManager.SUPPORT_FULLTEXT_INDEX)) {
            resultIndex.setFullText(fullTextCheckBox.getSelection());
        }
    }

    public Index getResultIndex() {
        return resultIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getErrorMessage() {
        final String text = nameText.getText().trim();

        if (text.equals("")) {
            return "error.index.name.empty";
        }

        if (!Check.isAlphabet(text)) {
            return "error.index.name.not.alphabet";
        }

        if (indexColumnList.getItemCount() == 0) {
            return "error.index.column.empty";
        }

        final DBManager dbManager = DBManagerFactory.getDBManager(table.getDiagram());

        if (dbManager.isSupported(DBManager.SUPPORT_FULLTEXT_INDEX)) {
            if (fullTextCheckBox.getSelection()) {
                for (final NormalColumn indexColumn : selectedColumns) {
                    if (!indexColumn.isFullTextIndexable()) {
                        return "error.index.fulltext.impossible";
                    }
                }
            }
        }

        return null;
    }

    @Override
    protected String getTitle() {
        return "dialog.title.index";
    }

    private void disposeCheckBox(final Column column) {
        final TableEditor oldEditor = columnCheckMap.get(column);

        if (oldEditor != null) {
            if (oldEditor.getEditor() != null) {
                oldEditor.getEditor().dispose();
            }
            oldEditor.dispose();
        }

        columnCheckMap.remove(column);
    }
}
