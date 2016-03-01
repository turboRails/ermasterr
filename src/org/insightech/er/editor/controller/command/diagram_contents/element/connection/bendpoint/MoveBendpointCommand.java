package org.insightech.er.editor.controller.command.diagram_contents.element.connection.bendpoint;

import org.eclipse.gef.ConnectionEditPart;
import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public class MoveBendpointCommand extends AbstractCommand {

    private final ConnectionEditPart editPart;

    private final Bendpoint bendPoint;

    private Bendpoint oldBendpoint;

    private final int index;

    public MoveBendpointCommand(final ConnectionEditPart editPart, final int x, final int y, final int index) {
        this.editPart = editPart;
        bendPoint = new Bendpoint(x, y);
        this.index = index;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        final ConnectionElement connection = (ConnectionElement) editPart.getModel();

        oldBendpoint = connection.getBendpoints().get(index);
        connection.replaceBendpoint(index, bendPoint);

        connection.refreshBendpoint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        final ConnectionElement connection = (ConnectionElement) editPart.getModel();
        connection.replaceBendpoint(index, oldBendpoint);

        connection.refreshBendpoint();
    }

}
