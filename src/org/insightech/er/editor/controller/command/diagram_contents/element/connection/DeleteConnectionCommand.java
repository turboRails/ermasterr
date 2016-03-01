package org.insightech.er.editor.controller.command.diagram_contents.element.connection;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public class DeleteConnectionCommand extends AbstractCommand {

    private final ConnectionElement connection;

    public DeleteConnectionCommand(final ConnectionElement connection) {
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        connection.delete();

        connection.getTarget().refreshTargetConnections();
        connection.getSource().refreshSourceConnections();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        connection.connect();

        connection.getTarget().refreshTargetConnections();
        connection.getSource().refreshSourceConnections();
    }
}
