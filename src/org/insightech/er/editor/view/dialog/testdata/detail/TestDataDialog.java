package org.insightech.er.editor.view.dialog.testdata.detail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractTabbedDialog;
import org.insightech.er.common.dialog.ValidatableTabWrapper;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.testdata.TableTestData;
import org.insightech.er.editor.model.testdata.TestData;
import org.insightech.er.editor.view.dialog.testdata.detail.tab.DirectTestDataTabWrapper;
import org.insightech.er.editor.view.dialog.testdata.detail.tab.RepeatTestDataTabWrapper;
import org.insightech.er.util.Format;

public class TestDataDialog extends AbstractTabbedDialog {

    public static final int TABLE_WIDTH = 800;

    public static final int TABLE_HEIGHT = 300;

    private Button addButton;

    private Button removeButton;

    private org.eclipse.swt.widgets.List allTableListWidget;

    private Table selectedTableTable;

    private Button repeatToDirectRadio;

    private Button directToRepeatRadio;

    private DirectTestDataTabWrapper directTestDataTabWrapper;

    private RepeatTestDataTabWrapper repeatTestDataTabWrapper;

    private final ERDiagram diagram;

    private final TestData testData;

    private final List<ERTable> allTableList;

    private Text nameText;

    private int selectedTableIndex = -1;

    public ERDiagram getDiagram() {
        return diagram;
    }

    public TestDataDialog(final Shell parentShell, final ERDiagram diagram, final TestData testData) {
        super(parentShell);

        this.diagram = diagram;

        this.testData = testData.clone();

        allTableList = diagram.getDiagramContents().getContents().getTableSet().getList();
    }

    @Override
    protected void initLayout(final GridLayout layout) {
        super.initLayout(layout);

        layout.numColumns = 1;
    }

    @Override
    protected void initialize(final Composite composite) {
        createNameComposite(composite);
        createTopComposite(composite);
        createBottomComposite(composite);
    }

    private void createNameComposite(final Composite parent) {
        final Composite nameComposite = new Composite(parent, SWT.NONE);
        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        nameComposite.setLayoutData(gridData);

        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        nameComposite.setLayout(layout);

        nameText = CompositeFactory.createText(this, nameComposite, "label.testdata.name", true, true);
    }

    private void createTopComposite(final Composite parent) {
        final Composite topComposite = new Composite(parent, SWT.NONE);

        final GridLayout mainLayout = new GridLayout();
        mainLayout.numColumns = 3;
        topComposite.setLayout(mainLayout);

        final GridData topGridData = new GridData();
        topGridData.grabExcessHorizontalSpace = true;
        topGridData.horizontalAlignment = GridData.FILL;
        topGridData.heightHint = 200;
        topComposite.setLayoutData(topGridData);

        createAllTableList(topComposite);

        addButton = CompositeFactory.createAddButton(topComposite);
        addButton.setEnabled(false);

        createSelectedTableTable(topComposite);

        removeButton = CompositeFactory.createRemoveButton(topComposite);
        removeButton.setEnabled(false);
    }

    private void createAllTableList(final Composite composite) {
        final Group group = new Group(composite, SWT.NONE);

        final GridData gridData = new GridData();
        gridData.verticalSpan = 2;
        gridData.horizontalAlignment = GridData.BEGINNING;
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.FILL;
        group.setLayoutData(gridData);

        final GridLayout groupLayout = new GridLayout();
        group.setLayout(groupLayout);
        group.setText(ResourceString.getResourceString("label.all.table"));

        final GridData comboGridData = new GridData();
        comboGridData.widthHint = 300;
        comboGridData.grabExcessVerticalSpace = true;
        comboGridData.verticalAlignment = GridData.FILL;

        allTableListWidget = new org.eclipse.swt.widgets.List(group, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
        allTableListWidget.setLayoutData(comboGridData);
    }

    private void createSelectedTableTable(final Composite composite) {
        final GridData gridData = new GridData();
        gridData.verticalSpan = 2;
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.FILL;

        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;

        final Group group = new Group(composite, SWT.NONE);
        group.setText(ResourceString.getResourceString("label.testdata.table.list"));
        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        final GridData tableGridData = new GridData();
        tableGridData.grabExcessVerticalSpace = true;
        tableGridData.verticalAlignment = GridData.FILL;
        tableGridData.widthHint = 300;
        tableGridData.verticalSpan = 2;

        selectedTableTable = new Table(group, SWT.FULL_SELECTION | SWT.BORDER | SWT.MULTI);
        selectedTableTable.setHeaderVisible(false);
        selectedTableTable.setLayoutData(tableGridData);
        selectedTableTable.setLinesVisible(false);

        final TableColumn tableColumn = new TableColumn(selectedTableTable, SWT.CENTER);
        tableColumn.setWidth(200);
        tableColumn.setText(ResourceString.getResourceString("label.testdata.table.name"));

        final TableColumn numColumn = new TableColumn(selectedTableTable, SWT.CENTER);
        numColumn.setWidth(80);
        numColumn.setText(ResourceString.getResourceString("label.testdata.table.test.num"));
    }

    private void createBottomComposite(final Composite composite) {
        createOutputOrderGroup(composite);
        createTabFolder(composite);
    }

    private void createOutputOrderGroup(final Composite parent) {
        final GridData groupGridData = new GridData();
        groupGridData.horizontalAlignment = GridData.FILL;
        groupGridData.grabExcessHorizontalSpace = true;

        final GridLayout groupLayout = new GridLayout();
        groupLayout.marginWidth = 15;
        groupLayout.marginHeight = 15;
        groupLayout.numColumns = 4;

        final Group group = new Group(parent, SWT.NONE);
        group.setText(ResourceString.getResourceString("label.output.order"));
        group.setLayoutData(groupGridData);
        group.setLayout(groupLayout);

        directToRepeatRadio = CompositeFactory.createRadio(this, group, "label.output.order.direct.to.repeat");
        repeatToDirectRadio = CompositeFactory.createRadio(this, group, "label.output.order.repeat.to.direct");
    }

    private void initSelectedTableTable() {
        selectedTableTable.removeAll();

        for (final Map.Entry<ERTable, TableTestData> entry : testData.getTableTestDataMap().entrySet()) {
            final ERTable table = entry.getKey();
            final TableTestData tableTestData = entry.getValue();

            final TableItem tableItem = new TableItem(selectedTableTable, SWT.NONE);
            tableItem.setText(0, table.getName());
            tableItem.setText(1, String.valueOf(tableTestData.getTestDataNum()));
        }
    }

    public void resetTestDataNum() {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                final int targetIndex = selectedTableTable.getSelectionIndex();

                if (targetIndex != -1) {

                    final int num = directTestDataTabWrapper.getTestDataNum() + repeatTestDataTabWrapper.getTestDataNum();

                    final TableItem tableItem = selectedTableTable.getItem(targetIndex);
                    tableItem.setText(1, String.valueOf(num));
                }
            }

        });

    }

    @Override
    protected void setData() {
        nameText.setText(Format.null2blank(testData.getName()));

        for (final ERTable table : allTableList) {
            allTableListWidget.add(Format.null2blank(table.getName()));
        }

        initSelectedTableTable();

        if (selectedTableIndex != -1) {
            selectedTableTable.select(selectedTableIndex);
            removeButton.setEnabled(true);
            resetTabs();
            selectedTableIndex = -1;
        }

        if (testData.getExportOrder() == TestData.EXPORT_ORDER_DIRECT_TO_REPEAT) {
            directToRepeatRadio.setSelection(true);

        } else {
            repeatToDirectRadio.setSelection(true);

        }
    }

    @Override
    protected String getTitle() {
        return "dialog.title.testdata.edit";
    }

    @Override
    protected String getErrorMessage() {
        final String text = nameText.getText().trim();

        if (text.equals("")) {
            return "error.testdata.name.empty";
        }

        if (selectedTableTable.getItemCount() == 0) {
            return "error.testdata.table.empty";
        }

        return super.getErrorMessage();
    }

    @Override
    protected void perfomeOK() throws InputException {
        final String text = nameText.getText().trim();

        testData.setName(text);

        if (repeatToDirectRadio.getSelection()) {
            testData.setExportOrder(TestData.EXPORT_ORDER_REPEAT_TO_DIRECT);

        } else if (directToRepeatRadio.getSelection()) {
            testData.setExportOrder(TestData.EXPORT_ORDER_DIRECT_TO_REPEAT);

        }

        super.perfomeOK();
    }

    @Override
    protected void addListener() {
        super.addListener();

        allTableListWidget.addSelectionListener(new SelectionAdapter() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent evt) {
                final int index = allTableListWidget.getSelectionIndex();

                if (index == -1) {
                    addButton.setEnabled(false);
                } else {
                    addButton.setEnabled(true);
                }
            }
        });

        addButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final int[] indexes = allTableListWidget.getSelectionIndices();

                if (indexes.length < 1) {
                    return;
                }

                for (final int index : indexes) {
                    final ERTable table = allTableList.get(index);
                    if (!testData.contains(table)) {
                        final TableTestData tableTestData = new TableTestData();

                        testData.putTableTestData(table, tableTestData);
                    }
                }

                initSelectedTableTable();
                validate();
            }

        });

        removeButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                int index = selectedTableTable.getSelectionIndex();

                if (index == -1) {
                    return;
                }

                testData.removeTableTestData(index);

                initSelectedTableTable();
                validate();

                if (selectedTableTable.getItemCount() <= index) {
                    index--;
                }

                selectedTableTable.setSelection(index);
                if (index == -1) {
                    removeButton.setEnabled(false);
                }

                resetTabs();
            }

        });

        selectedTableTable.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent evt) {

                final int index = selectedTableTable.getSelectionIndex();

                if (index == -1) {
                    removeButton.setEnabled(false);
                    return;

                } else {
                    removeButton.setEnabled(true);
                }

                resetTabs();
            }

        });

    }

    public ERTable getTargetTable() {
        final int targetIndex = selectedTableTable.getSelectionIndex();
        return testData.get(targetIndex);
    }

    public TestData getTestData() {
        return testData;
    }

    public void setSelectedTable(final int selectedTableIndex) {
        this.selectedTableIndex = selectedTableIndex;
    }

    @Override
    protected List<ValidatableTabWrapper> createTabWrapperList(final TabFolder tabFolder) {
        final List<ValidatableTabWrapper> list = new ArrayList<ValidatableTabWrapper>();

        directTestDataTabWrapper = new DirectTestDataTabWrapper(this, tabFolder);
        list.add(directTestDataTabWrapper);

        repeatTestDataTabWrapper = new RepeatTestDataTabWrapper(this, tabFolder);
        list.add(repeatTestDataTabWrapper);

        return list;
    }
}
