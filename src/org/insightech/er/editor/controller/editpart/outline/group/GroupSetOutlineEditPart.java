package org.insightech.er.editor.controller.editpart.outline.group;

import java.util.Collections;
import java.util.List;

import org.eclipse.gef.DragTracker;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.group.ChangeGroupCommand;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.CopyGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.view.dialog.group.GroupManageDialog;

public class GroupSetOutlineEditPart extends AbstractOutlineEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected List getModelChildren() {
        final GroupSet columnGroupSet = (GroupSet) getModel();

        final List<ColumnGroup> columnGroupList = columnGroupSet.getGroupList();

        Collections.sort(columnGroupList);

        return columnGroupList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshOutlineVisuals() {
        setWidgetText(ResourceString.getResourceString("label.column.group") + " (" + getModelChildren().size() + ")");
        setWidgetImage(ERDiagramActivator.getImage(ImageKey.DICTIONARY));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performRequest(final Request request) {
        final ERDiagram diagram = getDiagram();

        final GroupSet groupSet = diagram.getDiagramContents().getGroups();

        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            final GroupManageDialog dialog = new GroupManageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), groupSet, diagram, false, -1);

            if (dialog.open() == IDialogConstants.OK_ID) {
                final List<CopyGroup> newColumnGroups = dialog.getCopyColumnGroups();

                final Command command = new ChangeGroupCommand(diagram, groupSet, newColumnGroups);

                execute(command);
            }
        }

        super.performRequest(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DragTracker getDragTracker(final Request req) {
        return new SelectEditPartTracker(this);
    }

}
