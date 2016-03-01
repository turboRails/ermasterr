package org.insightech.er.editor.view.dialog.testdata;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.testdata.TableTestData;
import org.insightech.er.editor.model.testdata.TestData;
import org.insightech.er.editor.view.dialog.dbexport.ExportToTestDataDialog;
import org.insightech.er.editor.view.dialog.testdata.detail.TestDataDialog;
import org.insightech.er.util.Format;

public class TestDataManageDialog extends AbstractDialog {

    private static final int GROUP_LIST_HEIGHT = 230;

    private final ERDiagram diagram;

    private org.eclipse.swt.widgets.List testDataListWidget;

    private Button addButton;

    private Button editButton;

    private Button deleteButton;

    private Button copyButton;

    private Button exportButton;

    private Table testDataTable;

    private final List<TestData> testDataList;

    public TestDataManageDialog(final Shell parentShell, final ERDiagram diagram, final List<TestData> testDataList) {
        super(parentShell);

        this.diagram = diagram;
        this.testDataList = testDataList;
    }

    @Override
    protected void initialize(final Composite composite) {
        createLeftComposite(composite);

        createRightComposite(composite);
    }

    private void createLeftComposite(final Composite parent) {
        final GridData gridData = new GridData();

        final GridLayout gridLayout = new GridLayout();
        gridLayout.verticalSpacing = 10;
        gridLayout.numColumns = 3;

        final Composite composite = new Composite(parent, SWT.BORDER);
        composite.setLayoutData(gridData);
        composite.setLayout(gridLayout);

        final GridData listCompGridData = new GridData();
        listCompGridData.horizontalSpan = 4;
        createTestDataList(composite, listCompGridData);

        addButton = CompositeFactory.createSmallButton(composite, "label.button.add");

        editButton = CompositeFactory.createSmallButton(composite, "label.button.edit");
        editButton.setEnabled(false);

        deleteButton = CompositeFactory.createSmallButton(composite, "label.button.delete");
        deleteButton.setEnabled(false);

        copyButton = CompositeFactory.createSmallButton(composite, "label.button.copy");
        copyButton.setEnabled(false);

        exportButton = CompositeFactory.createButton(composite, "label.button.testdata.export", 2, -1);
        exportButton.setEnabled(true);
    }

    private void createTestDataList(final Composite parent, final GridData gridData) {
        final GridLayout gridLayout = new GridLayout();

        final Group group = new Group(parent, SWT.NONE);
        group.setText(ResourceString.getResourceString("label.testdata.list"));
        group.setLayoutData(gridData);
        group.setLayout(gridLayout);

        final GridData listGridData = new GridData();
        listGridData.widthHint = 200;
        listGridData.heightHint = GROUP_LIST_HEIGHT;

        testDataListWidget = new org.eclipse.swt.widgets.List(group, SWT.BORDER | SWT.V_SCROLL);
        testDataListWidget.setLayoutData(listGridData);

        initTestDataList();
    }

    private void createRightComposite(final Composite parent) {
        final Composite composite = new Composite(parent, SWT.BORDER);

        final GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        composite.setLayoutData(gridData);

        final GridLayout gridLayout = new GridLayout();
        gridLayout.verticalSpacing = 8;
        composite.setLayout(gridLayout);

        final GridData tableGridData = new GridData();
        tableGridData.heightHint = GROUP_LIST_HEIGHT;
        tableGridData.verticalIndent = 15;

        testDataTable = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
        testDataTable.setHeaderVisible(true);
        testDataTable.setLayoutData(tableGridData);
        testDataTable.setLinesVisible(true);

        final TableColumn nameColumn = new TableColumn(testDataTable, SWT.NONE);
        nameColumn.setWidth(300);
        nameColumn.setResizable(false);
        nameColumn.setText(ResourceString.getResourceString("label.testdata.table.name"));

        final TableColumn dataNumColumn = new TableColumn(testDataTable, SWT.RIGHT);
        dataNumColumn.setResizable(false);
        dataNumColumn.setText(ResourceString.getResourceString("label.testdata.table.test.num"));
        dataNumColumn.pack();
    }

    private void initTestDataList() {
        Collections.sort(testDataList);

        testDataListWidget.removeAll();

        for (final TestData testData : testDataList) {
            testDataListWidget.add(Format.null2blank(testData.getName()));
        }
    }

    private void initTableData() {
        testDataTable.removeAll();

        final int targetIndex = testDataListWidget.getSelectionIndex();
        if (targetIndex == -1) {
            return;
        }

        final TestData testData = testDataList.get(targetIndex);

        for (final Map.Entry<ERTable, TableTestData> entry : testData.getTableTestDataMap().entrySet()) {
            final ERTable table = entry.getKey();
            final TableTestData tableTestData = entry.getValue();

            final TableItem tableItem = new TableItem(testDataTable, SWT.NONE);
            tableItem.setText(0, table.getName());
            tableItem.setText(1, String.valueOf(tableTestData.getTestDataNum()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getErrorMessage() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void perfomeOK() {}

    @Override
    protected String getTitle() {
        return "dialog.title.testdata";
    }

    @Override
    protected void setData() {}

    private void addTestData() {
        final TestData oldTestData = new TestData();

        final TestDataDialog testDataDialog = new TestDataDialog(getShell(), diagram, oldTestData);

        if (testDataDialog.open() == IDialogConstants.OK_ID) {
            final TestData newTestData = testDataDialog.getTestData();

            testDataList.add(newTestData);

            initTestDataList();

            for (int i = 0; i < testDataList.size(); i++) {
                final TestData testData = testDataList.get(i);

                if (testData == newTestData) {
                    testDataListWidget.setSelection(i);
                    break;
                }
            }

            editButton.setEnabled(true);
            deleteButton.setEnabled(true);
            copyButton.setEnabled(true);

            initTableData();
        }
    }

    private void exportTestData() {
        final int targetIndex = testDataListWidget.getSelectionIndex();

        final ExportToTestDataDialog exportTestDataDialog = new ExportToTestDataDialog(getShell(), testDataList, targetIndex);
        exportTestDataDialog.init(diagram);

        exportTestDataDialog.open();
    }

    private void editTestData(final int selectedTableIndex) {
        final int targetIndex = testDataListWidget.getSelectionIndex();
        if (targetIndex == -1) {
            return;
        }

        final TestData oldTestData = testDataList.get(targetIndex);

        final TestDataDialog testDataDialog = new TestDataDialog(getShell(), diagram, oldTestData);
        if (selectedTableIndex != -1) {
            testDataDialog.setSelectedTable(selectedTableIndex);
        }

        if (testDataDialog.open() == IDialogConstants.OK_ID) {
            final TestData newTestData = testDataDialog.getTestData();

            testDataList.remove(targetIndex);
            testDataList.add(targetIndex, newTestData);

            initTestDataList();

            for (int i = 0; i < testDataList.size(); i++) {
                final TestData testData = testDataList.get(i);

                if (testData == newTestData) {
                    testDataListWidget.setSelection(i);
                    break;
                }
            }

            initTableData();
        }
    }

    private void copyTestData() {
        final int targetIndex = testDataListWidget.getSelectionIndex();
        if (targetIndex == -1) {
            return;
        }

        final TestData oldTestData = testDataList.get(targetIndex);

        final TestData copyTestData = oldTestData.clone();

        testDataList.add(copyTestData);

        initTestDataList();

        for (int i = 0; i < testDataList.size(); i++) {
            final TestData testData = testDataList.get(i);

            if (testData == copyTestData) {
                testDataListWidget.setSelection(i);
                break;
            }
        }

        initTableData();
    }

    @Override
    protected void addListener() {
        super.addListener();

        addButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                addTestData();
            }
        });

        editButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                editTestData(testDataTable.getSelectionIndex());
            }
        });

        deleteButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                int targetIndex = testDataListWidget.getSelectionIndex();
                if (targetIndex == -1) {
                    return;
                }

                testDataList.remove(targetIndex);

                initTestDataList();

                if (targetIndex >= testDataList.size()) {
                    targetIndex = testDataList.size() - 1;
                }

                testDataListWidget.setSelection(targetIndex);
                if (targetIndex == -1) {
                    editButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                    copyButton.setEnabled(false);
                }

                initTableData();
            }
        });

        copyButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                copyTestData();
            }
        });

        exportButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                exportTestData();
            }
        });

        testDataListWidget.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (testDataListWidget.getSelectionIndex() != -1) {
                    initTableData();
                    editButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    copyButton.setEnabled(true);
                }
            }
        });

        testDataListWidget.addMouseListener(new MouseAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseDoubleClick(final MouseEvent e) {
                editTestData(testDataTable.getSelectionIndex());
            }
        });

        testDataTable.addMouseListener(new MouseAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseDoubleClick(final MouseEvent e) {
                editTestData(testDataTable.getSelectionIndex());
            }
        });

    }
}
