package org.insightech.er.editor.view.action.line;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.MoveElementCommand;
import org.insightech.er.editor.controller.editpart.element.node.ERTableEditPart;
import org.insightech.er.editor.controller.editpart.element.node.IResizable;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.controller.editpart.element.node.NoteEditPart;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.view.action.AbstractBaseSelectionAction;

public class ResizeModelAction extends AbstractBaseSelectionAction {

    public static final String ID = ResizeModelAction.class.getName();

    public ResizeModelAction(final ERDiagramEditor editor) {
        super(ID, ResourceString.getResourceString("action.title.auto.resize"), editor);
        setImageDescriptor(ERDiagramActivator.getImageDescriptor(ImageKey.RESIZE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Command> getCommand(final EditPart editPart, final Event event) {
        final List<Command> commandList = new ArrayList<Command>();

        if (editPart instanceof IResizable) {
            final NodeElement nodeElement = (NodeElement) editPart.getModel();

            final MoveElementCommand command = new MoveElementCommand(getDiagram(), ((NodeElementEditPart) editPart).getFigure().getBounds(), nodeElement.getX(), nodeElement.getY(), -1, -1, nodeElement);

            commandList.add(command);
        }

        return commandList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean calculateEnabled() {
        final GraphicalViewer viewer = getGraphicalViewer();

        for (final Object object : viewer.getSelectedEditParts()) {
            if (object instanceof NodeElementEditPart) {
                final NodeElementEditPart nodeElementEditPart = (NodeElementEditPart) object;

                if (nodeElementEditPart instanceof ERTableEditPart || nodeElementEditPart instanceof NoteEditPart) {
                    return true;
                }
            }
        }

        return false;
    }
}
