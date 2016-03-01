package org.insightech.er.editor.controller.editpart.outline.index;

import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.index.ChangeIndexCommand;
import org.insightech.er.editor.controller.editpart.DeleteableEditPart;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.controller.editpolicy.not_element.index.IndexComponentEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.view.dialog.element.table.sub.IndexDialog;

public class IndexOutlineEditPart extends AbstractOutlineEditPart implements DeleteableEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshOutlineVisuals() {
        final Index index = (Index) getModel();

        setWidgetText(getDiagram().filter(index.getName()));
        setWidgetImage(ERDiagramActivator.getImage(ImageKey.INDEX));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new IndexComponentEditPolicy());
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
    public void performRequest(final Request request) {
        final Index index = (Index) getModel();
        final ERDiagram diagram = getDiagram();

        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            final IndexDialog dialog = new IndexDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), index, index.getTable());

            if (dialog.open() == IDialogConstants.OK_ID) {
                final ChangeIndexCommand command = new ChangeIndexCommand(diagram, index, dialog.getResultIndex());

                execute(command);
            }
        }

        super.performRequest(request);
    }
}
