package org.insightech.er.editor.view.dialog.element.table_view.tab;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.common.dialog.ValidatableTabWrapper;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.group.ChangeGroupCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.CopyGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.view.dialog.common.ERTableComposite;
import org.insightech.er.editor.view.dialog.common.ERTableCompositeHolder;
import org.insightech.er.editor.view.dialog.element.table_view.TableViewDialog;
import org.insightech.er.editor.view.dialog.group.GroupManageDialog;
import org.insightech.er.editor.view.dialog.word.column.AbstractColumnDialog;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public abstract class AbstractAttributeTabWrapper extends ValidatableTabWrapper implements ERTableCompositeHolder {

    private static final int GROUP_TABLE_HEIGHT = 75;

    private final TableView copyData;

    private Text physicalNameText;

    private Text logicalNameText;

    private String oldPhysicalName;

    private Combo groupCombo;

    private Button groupAddButton;

    private Button groupManageButton;

    private final TableViewDialog tableViewDialog;

    private ERTableComposite tableComposite;

    private ERTableComposite groupTableComposite;

    public AbstractAttributeTabWrapper(final TableViewDialog tableViewDialog, final TabFolder parent, final TableView copyData) {
        super(tableViewDialog, parent, "label.table.attribute");

        this.copyData = copyData;
        this.tableViewDialog = tableViewDialog;
    }

    @Override
    public void initComposite() {
        setLayout(new GridLayout());

        createHeader(this);
        createBody(this);
        createFooter(this);
        createGroup(this);
    }

    private void createHeader(final Composite parent) {
        final Composite header = new Composite(parent, SWT.NONE);

        final GridLayout gridLayout = new GridLayout(4, false);
        gridLayout.horizontalSpacing = 20;

        header.setLayout(gridLayout);

        physicalNameText = CompositeFactory.createText(tableViewDialog, header, "label.physical.name", 1, 200, false, false);
        logicalNameText = CompositeFactory.createText(tableViewDialog, header, "label.logical.name", 1, 200, true, false);

        physicalNameText.setText(Format.null2blank(copyData.getPhysicalName()));
        logicalNameText.setText(Format.null2blank(copyData.getLogicalName()));
        oldPhysicalName = physicalNameText.getText();
    }

    private void createBody(final Composite parent) {
        final Group content = new Group(parent, SWT.NONE);
        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        content.setLayoutData(gridData);

        content.setLayout(new GridLayout(1, false));

        initTable(content);
    }

    private void initTable(final Composite parent) {
        final AbstractColumnDialog columnDialog = createColumnDialog();

        ERTable table = null;
        if (copyData instanceof ERTable) {
            table = (ERTable) copyData;
        }

        tableComposite = new ERTableComposite(this, parent, copyData.getDiagram(), table, copyData.getColumns(), columnDialog, tableViewDialog, 1, true, true);
    }

    protected abstract AbstractColumnDialog createColumnDialog();

    /**
     * {@inheritDoc}
     */
    @Override
    public void validatePage() throws InputException {
        String text = logicalNameText.getText().trim();
        copyData.setLogicalName(text);

        if (text.equals("")) {
            throw new InputException("error.table.logical.name.empty");
        }

        text = physicalNameText.getText().trim();
        if (!Check.isAlphabet(text)) {
            if (copyData.getDiagram().getDiagramContents().getSettings().isValidatePhysicalName()) {
                throw new InputException("error.table.physical.name.not.alphabet");
            }
        }
        copyData.setPhysicalName(text);
    }

    private void createFooter(final Composite parent) {
        final Composite footer = new Composite(parent, SWT.NONE);

        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;

        footer.setLayout(gridLayout);

        createGroupCombo(footer);

        groupAddButton = CompositeFactory.createLargeButton(footer, getGroupAddButtonLabel());

        groupAddButton.setEnabled(false);

        initGroupCombo();
    }

    protected abstract String getGroupAddButtonLabel();

    /**
     * This method initializes combo
     */
    private void createGroupCombo(final Composite parent) {
        final GridData gridData = new GridData();
        gridData.widthHint = 200;

        groupCombo = new Combo(parent, SWT.READ_ONLY);
        groupCombo.setLayoutData(gridData);
    }

    private void initGroupCombo() {
        groupCombo.removeAll();

        for (final ColumnGroup columnGroup : getColumnGroups()) {
            groupCombo.add(columnGroup.getGroupName());
        }

        if (groupTableComposite != null) {
            groupTableComposite.setColumnList(null);
        }
    }

    private void restructGroup() {
        initGroupCombo();

        int index = 0;
        for (final Column column : new ArrayList<Column>(copyData.getColumns())) {
            if (column instanceof ColumnGroup) {
                if (!getColumnGroups().contains((ColumnGroup) column)) {
                    tableComposite.removeColumn(index);
                    continue;
                }
            }
            index++;
        }

        tableViewDialog.validate();
    }

    /**
     * This method initializes group
     */
    private void createGroup(final Composite parent) {
        // GridData gridData1 = new GridData();
        // gridData1.heightHint = 100;
        // gridData1.widthHint = -1;
        final GridData gridData = new GridData();
        gridData.heightHint = -1;
        gridData.horizontalSpan = 1;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        // FormToolkit toolkit = new FormToolkit(this.getDisplay());
        // Form root = toolkit.createForm(parent);
        // root.getBody().setLayout(new GridLayout());
        //
        // ExpandableComposite expandableComposite = toolkit
        // .createExpandableComposite(root.getBody(),
        // ExpandableComposite.TWISTIE);
        //
        // Composite inner = toolkit.createComposite(expandableComposite);
        // inner.setLayout(new GridLayout());
        // expandableComposite.setClient(inner);
        // toolkit.createLabel(inner, "aaa");

        final Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout());
        group.setLayoutData(gridData);

        groupTableComposite = new ERTableComposite(this, group, copyData.getDiagram(), null, null, null, null, 2, false, false, GROUP_TABLE_HEIGHT);

        groupManageButton = CompositeFactory.createLargeButton(group, "label.button.group.manage");

        groupTableComposite.setColumnList(null);
    }

    private GroupSet getColumnGroups() {
        return copyData.getDiagram().getDiagramContents().getGroups();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitFocus() {
        physicalNameText.setFocus();
    }

    @Override
    public void selectGroup(final ColumnGroup selectedColumn) {
        final int targetIndex = getColumnGroups().indexOf(selectedColumn);

        groupCombo.select(targetIndex);
        this.selectGroup(targetIndex);

        groupAddButton.setEnabled(false);
    }

    @SuppressWarnings("unchecked")
    private void selectGroup(final int targetIndex) {
        final ColumnGroup columnGroup = getColumnGroups().get(targetIndex);

        if (copyData.getColumns().contains(columnGroup)) {
            groupAddButton.setEnabled(false);
        } else {
            groupAddButton.setEnabled(true);
        }

        groupTableComposite.setColumnList((List) columnGroup.getColumns());
    }

    @Override
    public void perfomeOK() {}

    @Override
    protected void addListener() {
        super.addListener();

        physicalNameText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                final String logicalName = logicalNameText.getText();
                final String physicalName = physicalNameText.getText();

                if (oldPhysicalName.equals(logicalName) || logicalName.equals("")) {
                    logicalNameText.setText(physicalName);
                    oldPhysicalName = physicalName;
                }
            }
        });

        groupAddButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final int targetIndex = groupCombo.getSelectionIndex();
                if (targetIndex == -1) {
                    return;
                }

                final ColumnGroup columnGroup = getColumnGroups().get(targetIndex);
                tableComposite.addTableData(columnGroup);

                groupAddButton.setEnabled(false);
            }

        });

        groupCombo.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final int targetIndex = groupCombo.getSelectionIndex();
                if (targetIndex == -1) {
                    return;
                }

                selectGroup(targetIndex);
            }
        });

        groupManageButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final GroupSet groupSet = getColumnGroups();

                final GroupManageDialog dialog = new GroupManageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), groupSet, copyData.getDiagram(), false, -1);

                if (dialog.open() == IDialogConstants.OK_ID) {
                    final List<CopyGroup> newColumnGroups = dialog.getCopyColumnGroups();

                    final Command command = new ChangeGroupCommand(tableViewDialog.getDiagram(), groupSet, newColumnGroups);

                    tableViewDialog.getViewer().getEditDomain().getCommandStack().execute(command);

                    restructGroup();

                    groupAddButton.setEnabled(false);
                }
            }

        });

    }

}
