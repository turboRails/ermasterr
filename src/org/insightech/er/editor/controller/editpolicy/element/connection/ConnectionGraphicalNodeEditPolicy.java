package org.insightech.er.editor.controller.editpolicy.element.connection;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.CreateCommentConnectionCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.CreateConnectionCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.CommentConnection;

public class ConnectionGraphicalNodeEditPolicy extends GraphicalNodeEditPolicy {

    @Override
    protected Command getConnectionCompleteCommand(final CreateConnectionRequest request) {
        final CreateCommentConnectionCommand command = (CreateCommentConnectionCommand) request.getStartCommand();

        command.setTarget(request.getTargetEditPart());

        if (!command.canExecute()) {
            return null;
        }

        return command;
    }

    @Override
    protected Command getConnectionCreateCommand(final CreateConnectionRequest request) {
        final Object object = request.getNewObject();

        if (object instanceof CommentConnection) {
            final CommentConnection connection = (CommentConnection) object;

            final CreateConnectionCommand command = new CreateCommentConnectionCommand(connection);

            command.setSource(request.getTargetEditPart());
            request.setStartCommand(command);

            return command;
        }

        return null;
    }

    @Override
    protected Command getReconnectTargetCommand(final ReconnectRequest paramReconnectRequest) {
        return null;
    }

    @Override
    protected Command getReconnectSourceCommand(final ReconnectRequest paramReconnectRequest) {
        return null;
    }

}
