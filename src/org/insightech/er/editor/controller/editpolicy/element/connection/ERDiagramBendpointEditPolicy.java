package org.insightech.er.editor.controller.editpolicy.element.connection;

import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.BendpointEditPolicy;
import org.eclipse.gef.requests.BendpointRequest;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.bendpoint.CreateBendpointCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.bendpoint.DeleteBendpointCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.bendpoint.MoveBendpointCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.view.figure.connection.ERDiagramConnection;

public class ERDiagramBendpointEditPolicy extends BendpointEditPolicy {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command getCreateBendpointCommand(final BendpointRequest bendpointrequest) {
        final AbstractConnectionEditPart connectionEditPart = (AbstractConnectionEditPart) getHost();
        final ConnectionElement connection = (ConnectionElement) connectionEditPart.getModel();

        if (connection.getSource() == connection.getTarget()) {
            return null;
        }

        final Point point = bendpointrequest.getLocation();
        getConnection().translateToRelative(point);

        final CreateBendpointCommand createBendpointCommand = new CreateBendpointCommand(connection, point.x, point.y, bendpointrequest.getIndex());

        return createBendpointCommand;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command getDeleteBendpointCommand(final BendpointRequest bendpointrequest) {
        final ConnectionElement connection = (ConnectionElement) getHost().getModel();

        if (connection.getSource() == connection.getTarget()) {
            return null;
        }

        final DeleteBendpointCommand command = new DeleteBendpointCommand(connection, bendpointrequest.getIndex());

        return command;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command getMoveBendpointCommand(final BendpointRequest bendpointrequest) {
        final ConnectionEditPart editPart = (ConnectionEditPart) getHost();

        final Point point = bendpointrequest.getLocation();
        getConnection().translateToRelative(point);

        final MoveBendpointCommand command = new MoveBendpointCommand(editPart, point.x, point.y, bendpointrequest.getIndex());

        return command;
    }

    @Override
    protected List createSelectionHandles() {
        showSelectedLine();
        return super.createSelectionHandles();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void showSelection() {
        // ERDiagramEditPart diagramEditPart = (ERDiagramEditPart)
        // this.getHost()
        // .getRoot().getContents();
        // diagramEditPart.refreshVisuals();

        super.showSelection();
    }

    protected void showSelectedLine() {
        final ERDiagramConnection connection = (ERDiagramConnection) getHostFigure();
        connection.setSelected(true);
    }

    @Override
    protected void removeSelectionHandles() {
        final ERDiagramConnection connection = (ERDiagramConnection) getHostFigure();
        connection.setSelected(false);

        super.removeSelectionHandles();
    }
}
