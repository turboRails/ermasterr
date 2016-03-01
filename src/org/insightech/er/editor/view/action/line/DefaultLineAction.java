package org.insightech.er.editor.view.action.line;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.bendpoint.DefaultLineCommand;
import org.insightech.er.editor.controller.editpart.element.node.IResizable;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.view.action.AbstractBaseSelectionAction;

public class DefaultLineAction extends AbstractBaseSelectionAction {

    public static final String ID = DefaultLineAction.class.getName();

    public DefaultLineAction(final ERDiagramEditor editor) {
        super(ID, ResourceString.getResourceString("action.title.default"), editor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Command> getCommand(final EditPart editPart, final Event event) {
        final List<Command> commandList = new ArrayList<Command>();

        if (editPart instanceof IResizable) {
            final NodeElementEditPart nodeElementEditPart = (NodeElementEditPart) editPart;

            for (final Object obj : nodeElementEditPart.getSourceConnections()) {
                final AbstractConnectionEditPart connectionEditPart = (AbstractConnectionEditPart) obj;

                if (connectionEditPart.getSource() != connectionEditPart.getTarget()) {
                    commandList.add(new DefaultLineCommand(getDiagram(), (ConnectionElement) connectionEditPart.getModel()));
                }
            }

        } else if (editPart instanceof AbstractConnectionEditPart) {
            final AbstractConnectionEditPart connectionEditPart = (AbstractConnectionEditPart) editPart;

            if (connectionEditPart.getSource() != connectionEditPart.getTarget()) {
                commandList.add(new DefaultLineCommand(getDiagram(), (ConnectionElement) connectionEditPart.getModel()));
            }
        }

        return commandList;
    }

    @Override
    protected boolean calculateEnabled() {
        final GraphicalViewer viewer = getGraphicalViewer();

        for (final Object object : viewer.getSelectedEditParts()) {
            if (object instanceof ConnectionEditPart) {
                return true;

            } else if (object instanceof NodeElementEditPart) {
                final NodeElementEditPart nodeElementEditPart = (NodeElementEditPart) object;

                if (!nodeElementEditPart.getSourceConnections().isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }
}
