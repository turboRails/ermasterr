package org.insightech.er.editor.view.dialog.dbimport;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.StringObjectModel;

public class SelectImportedSchemaDialog extends AbstractDialog {

    private ContainerCheckedTreeViewer viewer;

    private final List<String> schemaList;

    private final List<String> selectedSchemaList;

    private final List<String> resultSelectedSchemas;

    private final String importDB;

    public SelectImportedSchemaDialog(final Shell parentShell, final ERDiagram diagram, final String importDB, final List<String> schemaList, final List<String> selectedSchemaList) {
        super(parentShell);

        this.schemaList = schemaList;
        this.selectedSchemaList = selectedSchemaList;
        resultSelectedSchemas = new ArrayList<String>();
        this.importDB = importDB;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize(final Composite composite) {
        createObjectListComposite(composite);
    }

    private void createObjectListComposite(final Composite parent) {
        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        gridLayout.verticalSpacing = 20;

        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(gridLayout);
        composite.setLayoutData(gridData);

        viewer = CompositeFactory.createCheckedTreeViewer(this, composite, 300, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
        createButton(parent, IDialogConstants.BACK_ID, IDialogConstants.BACK_LABEL, false);
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.NEXT_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void perfomeOK() throws InputException {
        final Object[] selectedNodes = viewer.getCheckedElements();

        resultSelectedSchemas.clear();

        for (int i = 0; i < selectedNodes.length; i++) {
            final Object value = ((TreeNode) selectedNodes[i]).getValue();
            if (value instanceof String) {
                resultSelectedSchemas.add((String) value);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getErrorMessage() {
        if (viewer.getCheckedElements().length == 0) {
            return "error.import.schema.empty";
        }

        return null;
    }

    @Override
    protected String getTitle() {
        return "dialog.title.select.import.schema";
    }

    @Override
    protected void setData() {
        final List<TreeNode> treeNodeList = createTreeNodeList();

        final TreeNode[] treeNodes = treeNodeList.toArray(new TreeNode[treeNodeList.size()]);
        viewer.setInput(treeNodes);

        final List<TreeNode> checkedList = new ArrayList<TreeNode>();

        final TreeNode[] schemaNodes = treeNodes[0].getChildren();

        if (selectedSchemaList.isEmpty()) {
            for (final TreeNode schemaNode : schemaNodes) {
                if (!DBManagerFactory.getDBManager(importDB).getSystemSchemaList().contains(String.valueOf(schemaNode.getValue()).toLowerCase())) {
                    checkedList.add(schemaNode);
                }
            }

        } else {
            for (final TreeNode schemaNode : schemaNodes) {
                if (selectedSchemaList.contains(schemaNode.getValue())) {
                    checkedList.add(schemaNode);
                }
            }

        }

        viewer.setCheckedElements(checkedList.toArray(new TreeNode[checkedList.size()]));

        viewer.expandAll();
    }

    protected List<TreeNode> createTreeNodeList() {

        final List<TreeNode> treeNodeList = new ArrayList<TreeNode>();

        final TreeNode topNode = new TreeNode(new StringObjectModel(ResourceString.getResourceString("label.schema")));
        treeNodeList.add(topNode);

        final List<TreeNode> schemaNodeList = new ArrayList<TreeNode>();

        for (final String schemaName : schemaList) {
            final TreeNode schemaNode = new TreeNode(schemaName);
            schemaNode.setParent(topNode);
            schemaNodeList.add(schemaNode);
        }

        topNode.setChildren(schemaNodeList.toArray(new TreeNode[schemaNodeList.size()]));

        return treeNodeList;
    }

    public List<String> getSelectedSchemas() {
        return resultSelectedSchemas;
    }

}
