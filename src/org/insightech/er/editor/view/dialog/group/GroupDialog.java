package org.insightech.er.editor.view.dialog.group;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.CopyGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.view.dialog.common.ERTableComposite;
import org.insightech.er.editor.view.dialog.common.ERTableCompositeHolder;
import org.insightech.er.editor.view.dialog.word.column.real.GroupColumnDialog;

public class GroupDialog extends AbstractDialog implements ERTableCompositeHolder {

    private Text groupNameText;

    private final List<CopyGroup> copyColumnGroups;

    private int editTargetIndex = -1;

    private CopyGroup copyData;

    private final ERDiagram diagram;

    public GroupDialog(final Shell parentShell, final GroupSet columnGroups, final ERDiagram diagram, final int editTargetIndex) {
        super(parentShell);

        copyColumnGroups = new ArrayList<CopyGroup>();

        for (final ColumnGroup columnGroup : columnGroups) {
            copyColumnGroups.add(new CopyGroup(columnGroup));
        }

        this.diagram = diagram;

        this.editTargetIndex = editTargetIndex;

        if (this.editTargetIndex != -1) {
            copyData = copyColumnGroups.get(editTargetIndex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void initialize(final Composite composite) {
        groupNameText = CompositeFactory.createText(this, composite, "label.group.name", 1, 200, true, false);

        final GroupColumnDialog columnDialog = new GroupColumnDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram);

        new ERTableComposite(this, composite, diagram, null, (List) copyData.getColumns(), columnDialog, this, 2, true, true);

        groupNameText.setFocus();
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
    protected void perfomeOK() {
        copyData.setGroupName(groupNameText.getText());
    }

    @Override
    protected String getTitle() {
        return "dialog.title.group";
    }

    @Override
    protected void setData() {
        if (editTargetIndex != -1) {
            String text = copyData.getGroupName();

            if (text == null) {
                text = "";
            }

            groupNameText.setText(text);
        }
    }

    public List<CopyGroup> getCopyColumnGroups() {
        return copyColumnGroups;
    }

    @Override
    public void selectGroup(final ColumnGroup selectedColumn) {
        // do nothing
    }
}
