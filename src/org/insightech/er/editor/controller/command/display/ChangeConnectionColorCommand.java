package org.insightech.er.editor.controller.command.display;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public class ChangeConnectionColorCommand extends AbstractCommand {

    private final ConnectionElement connection;

    private final int red;

    private final int green;

    private final int blue;

    private int[] oldColor;

    public ChangeConnectionColorCommand(final ConnectionElement connection, final int red, final int green, final int blue) {
        this.connection = connection;

        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        oldColor = connection.getColor();

        connection.setColor(red, green, blue);

        connection.refreshVisuals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        if (oldColor == null) {
            oldColor = new int[3];
            oldColor[0] = 0;
            oldColor[1] = 0;
            oldColor[2] = 0;
        }

        connection.setColor(oldColor[0], oldColor[1], oldColor[2]);

        connection.refreshVisuals();
    }
}
