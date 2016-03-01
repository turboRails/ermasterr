package org.insightech.er.editor.controller.editpolicy.element.node;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.AbstractCreateConnectionCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.CreateCommentConnectionCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.CreateConnectionCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.AbstractCreateRelationCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.CreateRelatedTableCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.CreateRelationByExistingColumnsCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.CreateRelationCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.CreateSelfRelationCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.ReconnectSourceCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.ReconnectTargetCommand;
import org.insightech.er.editor.controller.editpart.element.node.ERTableEditPart;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.controller.editpart.element.node.TableViewEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.CommentConnection;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.RelatedTable;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.connection.RelationByExistingColumns;
import org.insightech.er.editor.model.diagram_contents.element.connection.SelfRelation;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public class NodeElementGraphicalNodeEditPolicy extends GraphicalNodeEditPolicy {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command getConnectionCompleteCommand(final CreateConnectionRequest request) {
        final AbstractCreateConnectionCommand command = (AbstractCreateConnectionCommand) request.getStartCommand();

        final NodeElementEditPart targetEditPart = (NodeElementEditPart) request.getTargetEditPart();

        if (command instanceof AbstractCreateRelationCommand) {
            if (!(targetEditPart instanceof TableViewEditPart)) {
                return null;
            }
        }

        final String validatedMessage = command.validate();
        if (validatedMessage != null) {
            ERDiagramActivator.showErrorDialog(validatedMessage);

            return null;
        }

        command.setTarget(targetEditPart);

        if (!command.canExecute()) {
            return null;
        }

        return command;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command getConnectionCreateCommand(final CreateConnectionRequest request) {
        final EditPart editPart = request.getTargetEditPart();
        final Object object = request.getNewObject();

        if (editPart instanceof ERTableEditPart) {
            final Command command = getRelationCreateCommand(request, object);

            if (command != null) {
                return command;
            }
        }

        if (object instanceof CommentConnection) {
            final CommentConnection connection = (CommentConnection) object;

            final CreateConnectionCommand command = new CreateCommentConnectionCommand(connection);

            command.setSource(request.getTargetEditPart());
            request.setStartCommand(command);

            return command;
        }

        return null;
    }

    private Command getRelationCreateCommand(final CreateConnectionRequest request, final Object object) {
        if (object instanceof Relation) {
            final Relation relation = (Relation) object;
            final CreateRelationCommand command = new CreateRelationCommand(relation);

            final EditPart source = request.getTargetEditPart();
            command.setSource(source);

            final ERTable sourceTable = (ERTable) source.getModel();

            final Relation temp = sourceTable.createRelation();
            relation.setReferenceForPK(temp.isReferenceForPK());
            relation.setReferencedComplexUniqueKey(temp.getReferencedComplexUniqueKey());
            relation.setReferencedColumn(temp.getReferencedColumn());

            request.setStartCommand(command);

            return command;

        } else if (object instanceof RelatedTable) {
            final ERDiagram diagram = (ERDiagram) getHost().getRoot().getContents().getModel();

            final CreateRelatedTableCommand command = new CreateRelatedTableCommand(diagram);

            final ERTableEditPart sourceEditPart = (ERTableEditPart) request.getTargetEditPart();

            command.setSource(sourceEditPart);

            if (sourceEditPart != null) {
                final Point point = sourceEditPart.getFigure().getBounds().getCenter();
                command.setSourcePoint(point.x, point.y);
            }

            request.setStartCommand(command);

            return command;

        } else if (object instanceof SelfRelation) {
            final ERTableEditPart sourceEditPart = (ERTableEditPart) request.getTargetEditPart();
            final ERTable sourceTable = (ERTable) sourceEditPart.getModel();

            final CreateSelfRelationCommand command = new CreateSelfRelationCommand(sourceTable.createRelation());

            command.setSource(sourceEditPart);

            request.setStartCommand(command);

            return command;

        } else if (object instanceof RelationByExistingColumns) {
            final CreateRelationByExistingColumnsCommand command = new CreateRelationByExistingColumnsCommand();

            final EditPart source = request.getTargetEditPart();
            command.setSource(source);

            request.setStartCommand(command);

            return command;
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command getReconnectSourceCommand(final ReconnectRequest reconnectrequest) {
        final ConnectionElement connection = (ConnectionElement) reconnectrequest.getConnectionEditPart().getModel();

        if (connection.getSource() == connection.getTarget()) {
            return null;
        }

        final NodeElement newSource = (NodeElement) reconnectrequest.getTarget().getModel();
        if (connection.getSource() != newSource) {
            return null;
        }

        final NodeElementEditPart sourceEditPart = (NodeElementEditPart) reconnectrequest.getConnectionEditPart().getSource();

        final Point location = new Point(reconnectrequest.getLocation());

        final IFigure sourceFigure = sourceEditPart.getFigure();
        sourceFigure.translateToRelative(location);

        int xp = -1;
        int yp = -1;

        final Rectangle bounds = sourceFigure.getBounds();

        final Rectangle centerRectangle = new Rectangle(bounds.x + (bounds.width / 4), bounds.y + (bounds.height / 4), bounds.width / 2, bounds.height / 2);

        if (!centerRectangle.contains(location)) {
            final Point point = NodeElementEditPart.getIntersectionPoint(location, sourceFigure);
            xp = 100 * (point.x - bounds.x) / bounds.width;
            yp = 100 * (point.y - bounds.y) / bounds.height;
        }

        final ReconnectSourceCommand command = new ReconnectSourceCommand(connection, xp, yp);

        return command;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command getReconnectTargetCommand(final ReconnectRequest reconnectrequest) {
        final ConnectionElement connection = (ConnectionElement) reconnectrequest.getConnectionEditPart().getModel();

        if (connection.getSource() == connection.getTarget()) {
            return null;
        }

        final NodeElement newTarget = (NodeElement) reconnectrequest.getTarget().getModel();
        if (connection.getTarget() != newTarget) {
            return null;
        }

        final NodeElementEditPart targetEditPart = (NodeElementEditPart) reconnectrequest.getConnectionEditPart().getTarget();

        final Point location = new Point(reconnectrequest.getLocation());

        final IFigure targetFigure = targetEditPart.getFigure();
        targetFigure.translateToRelative(location);

        int xp = -1;
        int yp = -1;

        final Rectangle bounds = targetFigure.getBounds();

        final Rectangle centerRectangle = new Rectangle(bounds.x + (bounds.width / 4), bounds.y + (bounds.height / 4), bounds.width / 2, bounds.height / 2);

        if (!centerRectangle.contains(location)) {
            final Point point = NodeElementEditPart.getIntersectionPoint(location, targetFigure);

            xp = 100 * (point.x - bounds.x) / bounds.width;
            yp = 100 * (point.y - bounds.y) / bounds.height;
        }
        final ReconnectTargetCommand command = new ReconnectTargetCommand(connection, xp, yp);

        return command;
    }

}
