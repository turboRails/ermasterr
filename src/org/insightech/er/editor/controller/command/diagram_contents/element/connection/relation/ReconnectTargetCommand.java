package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public class ReconnectTargetCommand extends AbstractCommand {

    private final ConnectionElement connection;

    int xp;

    int yp;

    int oldXp;

    int oldYp;

    public ReconnectTargetCommand(final ConnectionElement connection, final int xp, final int yp) {
        this.connection = connection;

        this.xp = xp;
        this.yp = yp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        oldXp = connection.getTargetXp();
        oldYp = connection.getTargetYp();

        connection.setTargetLocationp(xp, yp);
        connection.refreshVisuals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        connection.setTargetLocationp(oldXp, oldYp);
        connection.refreshVisuals();
    }
}
