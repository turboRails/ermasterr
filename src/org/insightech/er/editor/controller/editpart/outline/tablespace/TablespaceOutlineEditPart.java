package org.insightech.er.editor.controller.editpart.outline.tablespace;

import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.db.EclipseDBManagerFactory;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.tablespace.EditTablespaceCommand;
import org.insightech.er.editor.controller.editpart.DeleteableEditPart;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.controller.editpolicy.not_element.tablespace.TablespaceComponentEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;

public class TablespaceOutlineEditPart extends AbstractOutlineEditPart implements DeleteableEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshOutlineVisuals() {
        final Tablespace tablespace = (Tablespace) getModel();

        setWidgetText(getDiagram().filter(tablespace.getName()));
        setWidgetImage(ERDiagramActivator.getImage(ImageKey.TABLESPACE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performRequest(final Request request) {
        final Tablespace tablespace = (Tablespace) getModel();
        final ERDiagram diagram = getDiagram();

        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            final TablespaceDialog dialog = EclipseDBManagerFactory.getEclipseDBManager(diagram).createTablespaceDialog();

            if (dialog == null) {
                ERDiagramActivator.showMessageDialog("dialog.message.tablespace.not.supported");
            } else {
                dialog.init(tablespace, diagram);

                if (dialog.open() == IDialogConstants.OK_ID) {
                    final EditTablespaceCommand command = new EditTablespaceCommand(diagram, tablespace, dialog.getResult());
                    execute(command);
                }
            }
        }

        super.performRequest(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new TablespaceComponentEditPolicy());
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
}
