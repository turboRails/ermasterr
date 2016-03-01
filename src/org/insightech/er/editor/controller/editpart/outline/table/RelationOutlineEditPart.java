package org.insightech.er.editor.controller.editpart.outline.table;

import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.ChangeRelationPropertyCommand;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.controller.editpolicy.element.connection.RelationEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.element.relation.RelationDialog;
import org.insightech.er.util.Format;

public class RelationOutlineEditPart extends AbstractOutlineEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshOutlineVisuals() {
        final Relation model = (Relation) getModel();

        final ERDiagram diagram = (ERDiagram) getRoot().getContents().getModel();

        final int viewMode = diagram.getDiagramContents().getSettings().getOutlineViewMode();

        boolean first = true;
        final StringBuilder sb = new StringBuilder();

        for (final NormalColumn foreignKeyColumn : model.getForeignKeyColumns()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }

            if (viewMode == Settings.VIEW_MODE_PHYSICAL) {
                sb.append(Format.null2blank(foreignKeyColumn.getPhysicalName()));

            } else if (viewMode == Settings.VIEW_MODE_LOGICAL) {
                sb.append(Format.null2blank(foreignKeyColumn.getLogicalName()));

            } else {
                sb.append(Format.null2blank(foreignKeyColumn.getLogicalName()));
                sb.append("/");
                sb.append(Format.null2blank(foreignKeyColumn.getPhysicalName()));
            }
        }

        setWidgetText(sb.toString());
        setWidgetImage(ERDiagramActivator.getImage(ImageKey.FOREIGN_KEY));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.CONNECTION_ROLE, new RelationEditPolicy());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performRequest(final Request request) {
        final Relation relation = (Relation) getModel();

        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            final Relation copy = relation.copy();

            final RelationDialog dialog = new RelationDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), copy);

            if (dialog.open() == IDialogConstants.OK_ID) {
                final ChangeRelationPropertyCommand command = new ChangeRelationPropertyCommand(relation, copy);
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
