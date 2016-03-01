package org.insightech.er.editor.controller.command.diagram_contents.element.connection.bendpoint;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public class CreateBendpointCommand extends AbstractCommand {

    private final ConnectionElement connection;

    int x;

    int y;

    private final int index;

    public CreateBendpointCommand(final ConnectionElement connection, final int x, final int y, final int index) {
        this.connection = connection;
        this.x = x;
        this.y = y;
        this.index = index;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        final Bendpoint bendpoint = new Bendpoint(x, y);
        connection.addBendpoint(index, bendpoint);

        connection.refreshBendpoint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        connection.removeBendpoint(index);

        connection.refreshBendpoint();
    }
}
