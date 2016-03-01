package org.insightech.er.editor.controller.command.diagram_contents.element.connection.bendpoint;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public class DeleteBendpointCommand extends AbstractCommand {

    private final ConnectionElement connection;

    private Bendpoint oldBendpoint;

    private final int index;

    public DeleteBendpointCommand(final ConnectionElement connection, final int index) {
        this.connection = connection;
        this.index = index;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        oldBendpoint = connection.getBendpoints().get(index);
        connection.removeBendpoint(index);

        connection.refreshBendpoint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        connection.addBendpoint(index, oldBendpoint);

        connection.refreshBendpoint();
    }
}
