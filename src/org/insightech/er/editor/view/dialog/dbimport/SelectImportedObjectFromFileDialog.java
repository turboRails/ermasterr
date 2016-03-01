package org.insightech.er.editor.view.dialog.dbimport;

import java.util.List;

import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbimport.DBObject;
import org.insightech.er.editor.model.dbimport.DBObjectSet;

public class SelectImportedObjectFromFileDialog extends AbstractSelectImportedObjectDialog {

    private Button mergeGroupButton;

    private boolean resultMergeGroup;

    public SelectImportedObjectFromFileDialog(final Shell parentShell, final ERDiagram diagram, final DBObjectSet allObjectSet) {
        super(parentShell, diagram, allObjectSet);
    }

    @Override
    protected void initializeOptionGroup(final Group group) {
        super.initializeOptionGroup(group);

        mergeGroupButton = CompositeFactory.createCheckbox(this, group, "label.merge.group", false);
        mergeGroupButton.setSelection(true);
    }

    @Override
    protected List<TreeNode> createTreeNodeList() {
        final List<TreeNode> treeNodeList = super.createTreeNodeList();

        TreeNode topNode = createTopNode(DBObject.TYPE_NOTE, dbObjectSet.getNoteList());
        treeNodeList.add(topNode);
        topNode = createTopNode(DBObject.TYPE_GROUP, dbObjectSet.getGroupList());
        treeNodeList.add(topNode);

        return treeNodeList;
    }

    @Override
    protected void perfomeOK() throws InputException {
        super.perfomeOK();

        resultMergeGroup = mergeGroupButton.getSelection();
    }

    @Override
    public boolean isMergeGroup() {
        return resultMergeGroup;
    }

}
