package org.insightech.er.editor.controller.command.diagram_contents.element.connection;

import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public class CreateConnectionCommand extends AbstractCreateConnectionCommand {

    private final ConnectionElement connection;

    public CreateConnectionCommand(final ConnectionElement connection) {
        super();
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        connection.setSource(getSourceModel());
        connection.setTarget(getTargetModel());

        getTargetModel().refreshTargetConnections();
        getSourceModel().refreshSourceConnections();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        connection.setSource(null);
        connection.setTarget(null);

        getTargetModel().refreshTargetConnections();
        getSourceModel().refreshSourceConnections();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String validate() {
        return null;
    }

}
