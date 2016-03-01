package org.insightech.er.editor.controller.editpart.outline.group;

import java.util.List;

import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.group.ChangeGroupCommand;
import org.insightech.er.editor.controller.editpart.DeleteableEditPart;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.controller.editpolicy.not_element.group.GroupComponentEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.CopyGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.view.dialog.group.GroupDialog;

public class GroupOutlineEditPart extends AbstractOutlineEditPart implements DeleteableEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshOutlineVisuals() {
        final ColumnGroup columnGroup = (ColumnGroup) getModel();

        setWidgetText(getDiagram().filter(columnGroup.getName()));
        setWidgetImage(ERDiagramActivator.getImage(ImageKey.GROUP));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performRequest(final Request request) {
        final ColumnGroup columnGroup = (ColumnGroup) getModel();
        final ERDiagram diagram = getDiagram();

        final GroupSet groupSet = diagram.getDiagramContents().getGroups();

        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            final GroupDialog dialog = new GroupDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), groupSet, diagram, groupSet.indexOf(columnGroup));

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

    @Override
    public boolean isDeleteable() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new GroupComponentEditPolicy());
    }
}
