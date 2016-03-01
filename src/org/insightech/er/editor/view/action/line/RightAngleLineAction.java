package org.insightech.er.editor.view.action.line;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.bendpoint.RightAngleLineCommand;
import org.insightech.er.editor.controller.editpart.element.node.IResizable;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.view.action.AbstractBaseSelectionAction;

public class RightAngleLineAction extends AbstractBaseSelectionAction {

    public static final String ID = RightAngleLineAction.class.getName();

    public RightAngleLineAction(final ERDiagramEditor editor) {
        super(ID, ResourceString.getResourceString("action.title.right.angle.line"), editor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Command> getCommand(final EditPart editPart, final Event event) {
        final List<Command> commandList = new ArrayList<Command>();

        if (editPart instanceof IResizable) {
            final NodeElementEditPart nodeElementEditPart = (NodeElementEditPart) editPart;

            for (final Object obj : nodeElementEditPart.getSourceConnections()) {
                final AbstractConnectionEditPart connectionEditPart = (AbstractConnectionEditPart) obj;

                if (connectionEditPart.getSource() != connectionEditPart.getTarget()) {
                    commandList.add(getRightAngleLineCommand(connectionEditPart));
                }
            }

        } else if (editPart instanceof AbstractConnectionEditPart) {
            final AbstractConnectionEditPart connectionEditPart = (AbstractConnectionEditPart) editPart;

            if (connectionEditPart.getSource() != connectionEditPart.getTarget()) {
                commandList.add(getRightAngleLineCommand(connectionEditPart));
            }
        }

        return commandList;
    }

    private Command getRightAngleLineCommand(final AbstractConnectionEditPart connectionEditPart) {
        int sourceX = -1;
        int sourceY = -1;
        int targetX = -1;
        int targetY = -1;

        // ConnectionEditPart connectionEditPart = (ConnectionEditPart)
        // connectionEditPart;

        final ConnectionElement connection = (ConnectionElement) connectionEditPart.getModel();

        if (connection.getSourceXp() != -1) {
            final NodeEditPart editPart = (NodeEditPart) connectionEditPart.getSource();
            final Rectangle bounds = editPart.getFigure().getBounds();

            sourceX = bounds.x + (bounds.width * connection.getSourceXp() / 100);
            sourceY = bounds.y + (bounds.height * connection.getSourceYp() / 100);
        }

        if (connection.getTargetXp() != -1) {
            final NodeEditPart editPart = (NodeEditPart) connectionEditPart.getTarget();
            final Rectangle bounds = editPart.getFigure().getBounds();

            targetX = bounds.x + (bounds.width * connection.getTargetXp() / 100);
            targetY = bounds.y + (bounds.height * connection.getTargetYp() / 100);
        }

        if (sourceX == -1) {
            final NodeElementEditPart sourceEditPart = (NodeElementEditPart) connectionEditPart.getSource();

            final Point sourcePoint = sourceEditPart.getFigure().getBounds().getCenter();
            sourceX = sourcePoint.x;
            sourceY = sourcePoint.y;
        }

        if (targetX == -1) {
            final NodeElementEditPart targetEditPart = (NodeElementEditPart) connectionEditPart.getTarget();

            final Point targetPoint = targetEditPart.getFigure().getBounds().getCenter();
            targetX = targetPoint.x;
            targetY = targetPoint.y;
        }

        final RightAngleLineCommand command = new RightAngleLineCommand(sourceX, sourceY, targetX, targetY, connectionEditPart);

        return command;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean calculateEnabled() {
        final GraphicalViewer viewer = getGraphicalViewer();

        for (final Object object : viewer.getSelectedEditParts()) {
            if (object instanceof ConnectionEditPart) {
                return true;

            } else if (object instanceof NodeElementEditPart) {
                final NodeElementEditPart nodeElementEditPart = (NodeElementEditPart) object;

                if (!nodeElementEditPart.getSourceConnections().isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }
}
