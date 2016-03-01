package org.insightech.er.editor.view.dialog.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.CopyGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GlobalGroupSet;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.view.dialog.common.ERTableComposite;
import org.insightech.er.editor.view.dialog.common.ERTableCompositeHolder;
import org.insightech.er.editor.view.dialog.word.column.real.GroupColumnDialog;

public class GroupManageDialog extends AbstractDialog implements ERTableCompositeHolder {

    private Text groupNameText;

    private org.eclipse.swt.widgets.List groupList;

    private Button groupUpdateButton;

    private Button groupCancelButton;

    private Button groupAddButton;

    private Button groupEditButton;

    private Button groupDeleteButton;

    private Button addToGlobalGroupButton;

    private final List<CopyGroup> copyGroups;

    private int editTargetIndex = -1;

    private CopyGroup copyData;

    private final ERDiagram diagram;

    private final boolean globalGroup;

    private ERTableComposite tableComposite;

    public GroupManageDialog(final Shell parentShell, final GroupSet columnGroups, final ERDiagram diagram, final boolean globalGroup, final int editTargetIndex) {
        super(parentShell);

        copyGroups = new ArrayList<CopyGroup>();

        for (final ColumnGroup columnGroup : columnGroups) {
            copyGroups.add(new CopyGroup(columnGroup));
        }

        this.diagram = diagram;

        this.globalGroup = globalGroup;

        this.editTargetIndex = editTargetIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize(final Composite composite) {
        createGroupListComposite(composite);
        createGroupDetailComposite(composite);

        setGroupEditEnabled(false);
    }

    /**
     * This method initializes composite
     */
    private void createGroupListComposite(final Composite parent) {
        final Group composite = CompositeFactory.createGroup(parent, "label.group.list", 1, 3);

        createGroupList(composite);

        groupAddButton = CompositeFactory.createMiddleButton(composite, "label.button.group.add");

        groupEditButton = CompositeFactory.createMiddleButton(composite, "label.button.group.edit");

        groupDeleteButton = CompositeFactory.createMiddleButton(composite, "label.button.group.delete");

        addToGlobalGroupButton = CompositeFactory.createLargeButton(composite, "label.button.add.to.global.group", 3);

        if (globalGroup) {
            addToGlobalGroupButton.setVisible(false);
        }

        setButtonEnabled(false);
    }

    /**
     * This method initializes group
     */
    private void createGroupList(final Composite parent) {
        final GridData listGridData = new GridData();
        listGridData.grabExcessHorizontalSpace = true;
        listGridData.horizontalAlignment = GridData.FILL;
        listGridData.grabExcessVerticalSpace = true;
        listGridData.verticalAlignment = GridData.FILL;
        listGridData.horizontalSpan = 3;

        groupList = new org.eclipse.swt.widgets.List(parent, SWT.BORDER | SWT.V_SCROLL);
        groupList.setLayoutData(listGridData);

        initGroupList();
    }

    private void initGroupList() {
        Collections.sort(copyGroups);

        groupList.removeAll();

        for (final ColumnGroup columnGroup : copyGroups) {
            groupList.add(columnGroup.getGroupName());
        }
    }

    /**
     * This method initializes composite1
     */
    private void createGroupDetailComposite(final Composite parent) {
        final Group composite = CompositeFactory.createGroup(parent, "label.group.info", 1, 2);

        groupNameText = CompositeFactory.createText(this, composite, "label.group.name", 1, 200, true, false);

        final GroupColumnDialog columnDialog = new GroupColumnDialog(getShell(), diagram);

        tableComposite = new ERTableComposite(this, composite, diagram, null, null, columnDialog, this, 2, true, true);

        createDetailButtonComposite(composite);
    }

    private void createDetailButtonComposite(final Composite parent) {
        final Composite composite = CompositeFactory.createChildComposite(parent, 2, 2);

        groupUpdateButton = CompositeFactory.createLargeButton(composite, "label.button.update");

        groupCancelButton = CompositeFactory.createLargeButton(composite, "label.button.cancel");
    }

    @SuppressWarnings("unchecked")
    private void initColumnGroup() {
        String text = copyData.getGroupName();

        if (text == null) {
            text = "";
        }

        groupNameText.setText(text);

        tableComposite.setColumnList((List) copyData.getColumns());
    }

    private void setGroupEditEnabled(final boolean enabled) {
        tableComposite.setEnabled(enabled);

        groupUpdateButton.setEnabled(enabled);
        groupCancelButton.setEnabled(enabled);
        groupNameText.setEnabled(enabled);

        groupList.setEnabled(!enabled);

        groupAddButton.setEnabled(!enabled);
        if (groupList.getSelectionIndex() != -1 && !enabled) {
            setButtonEnabled(true);

        } else {
            setButtonEnabled(false);
        }

        if (enabled) {
            groupNameText.setFocus();
        } else {
            groupList.setFocus();
        }

        enabledButton(!enabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getErrorMessage() {
        if (groupNameText.getEnabled()) {
            final String text = groupNameText.getText().trim();

            if (text.equals("")) {
                return "error.group.name.empty";
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void perfomeOK() {}

    @Override
    protected String getTitle() {
        if (globalGroup) {
            return "dialog.title.manage.global.group";
        }
        return "dialog.title.manage.group";
    }

    @Override
    protected void setData() {
        if (editTargetIndex != -1) {
            groupList.setSelection(editTargetIndex);

            copyData = new CopyGroup(copyGroups.get(editTargetIndex));
            initColumnGroup();

            setGroupEditEnabled(true);
        }
    }

    public List<CopyGroup> getCopyColumnGroups() {
        return copyGroups;
    }

    private void setButtonEnabled(final boolean enabled) {
        groupEditButton.setEnabled(enabled);
        groupDeleteButton.setEnabled(enabled);
        addToGlobalGroupButton.setEnabled(enabled);
    }

    @Override
    public void selectGroup(final ColumnGroup selectedColumn) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addListener() {
        super.addListener();

        groupAddButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                editTargetIndex = -1;

                copyData = new CopyGroup(new ColumnGroup());
                initColumnGroup();
                setGroupEditEnabled(true);
            }
        });

        groupEditButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                editTargetIndex = groupList.getSelectionIndex();
                if (editTargetIndex == -1) {
                    return;
                }

                setGroupEditEnabled(true);
            }
        });

        groupDeleteButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                editTargetIndex = groupList.getSelectionIndex();
                if (editTargetIndex == -1) {
                    return;
                }

                copyGroups.remove(editTargetIndex);

                initGroupList();

                if (copyGroups.size() == 0) {
                    editTargetIndex = -1;

                } else if (editTargetIndex >= copyGroups.size()) {
                    editTargetIndex = copyGroups.size() - 1;
                }

                if (editTargetIndex != -1) {
                    groupList.setSelection(editTargetIndex);
                    copyData = new CopyGroup(copyGroups.get(editTargetIndex));
                    initColumnGroup();

                } else {
                    copyData = new CopyGroup(new ColumnGroup());
                    initColumnGroup();
                    setButtonEnabled(false);
                }

            }
        });

        addToGlobalGroupButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                editTargetIndex = groupList.getSelectionIndex();
                if (editTargetIndex == -1) {
                    return;
                }

                final MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
                messageBox.setText(ResourceString.getResourceString("label.button.add.to.global.group"));
                messageBox.setMessage(ResourceString.getResourceString("dialog.message.add.to.global.group"));

                if (messageBox.open() == SWT.OK) {
                    final CopyGroup columnGroup = copyGroups.get(editTargetIndex);

                    final GroupSet columnGroups = GlobalGroupSet.load();

                    columnGroups.add(columnGroup);

                    GlobalGroupSet.save(columnGroups);
                }

            }
        });

        groupList.addMouseListener(new MouseAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseDoubleClick(final MouseEvent e) {
                editTargetIndex = groupList.getSelectionIndex();
                if (editTargetIndex == -1) {
                    return;
                }

                setGroupEditEnabled(true);
            }
        });

        groupList.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                try {
                    editTargetIndex = groupList.getSelectionIndex();
                    if (editTargetIndex == -1) {
                        return;
                    }
                    copyData = new CopyGroup(copyGroups.get(editTargetIndex));
                    initColumnGroup();
                    setButtonEnabled(true);

                } catch (final Exception ex) {
                    ERDiagramActivator.showExceptionDialog(ex);
                }
            }
        });

        groupUpdateButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                try {
                    if (validate()) {
                        final String text = groupNameText.getText().trim();
                        copyData.setGroupName(text);

                        if (editTargetIndex == -1) {
                            copyGroups.add(copyData);

                        } else {
                            copyGroups.remove(editTargetIndex);
                            copyData = (CopyGroup) copyData.restructure(null);

                            copyGroups.add(editTargetIndex, copyData);
                        }

                        setGroupEditEnabled(false);
                        initGroupList();

                        for (int i = 0; i < copyGroups.size(); i++) {
                            final ColumnGroup columnGroup = copyGroups.get(i);

                            if (columnGroup == copyData) {
                                groupList.setSelection(i);
                                copyData = new CopyGroup(copyGroups.get(i));
                                initColumnGroup();
                                setButtonEnabled(true);
                                break;
                            }

                        }
                    }
                } catch (final Exception ex) {
                    ERDiagramActivator.showExceptionDialog(ex);
                }
            }

        });

        groupCancelButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                setGroupEditEnabled(false);
                if (editTargetIndex != -1) {
                    copyData = new CopyGroup(copyGroups.get(editTargetIndex));
                    initColumnGroup();
                }
            }
        });
    }

}
