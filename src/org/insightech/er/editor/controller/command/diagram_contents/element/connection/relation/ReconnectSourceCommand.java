package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public class ReconnectSourceCommand extends AbstractCommand {

    private final ConnectionElement connection;

    int xp;

    int yp;

    int oldXp;

    int oldYp;

    public ReconnectSourceCommand(final ConnectionElement connection, final int xp, final int yp) {
        this.connection = connection;

        this.xp = xp;
        this.yp = yp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        oldXp = connection.getSourceXp();
        oldYp = connection.getSourceYp();

        connection.setSourceLocationp(xp, yp);
        connection.refreshVisuals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        connection.setSourceLocationp(oldXp, oldYp);
        connection.refreshVisuals();
    }

}
