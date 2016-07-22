package org.insightech.er.editor.view.tool;

import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.tools.PanningSelectionTool;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.controller.editpolicy.ERDiagramLayoutEditPolicy;
import org.insightech.er.editor.model.ERDiagram;

public class MovablePanningSelectionTool extends PanningSelectionTool {

    public static boolean shift = false;

    @Override
    protected boolean handleKeyUp(final KeyEvent event) {
        if (event.keyCode == SWT.SHIFT) {
            shift = true;
        }

        return super.handleKeyUp(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean handleKeyDown(final KeyEvent event) {
        int dx = 0;
        int dy = 0;

        if (event.keyCode == SWT.SHIFT) {
            shift = true;
        }

        if (event.keyCode == SWT.ARROW_DOWN) {
            dy = 1;

        } else if (event.keyCode == SWT.ARROW_LEFT) {
            dx = -1;

        } else if (event.keyCode == SWT.ARROW_RIGHT) {
            dx = 1;

        } else if (event.keyCode == SWT.ARROW_UP) {
            dy = -1;
        }

        if (dx != 0 || dy != 0) {
            final CompoundCommand compoundCommand = new CompoundCommand();

            final ERDiagram diagram = (ERDiagram) getCurrentViewer().getContents().getModel();

            final List selectedEditParts = getCurrentViewer().getSelectedEditParts();

            for (final Object object : selectedEditParts) {
                if (!(object instanceof NodeElementEditPart)) {
                    continue;
                }

                final NodeElementEditPart editPart = (NodeElementEditPart) object;

                final Rectangle rectangle = editPart.getFigure().getBounds().getCopy();

                rectangle.x += dx;
                rectangle.y += dy;

                final Command command = ERDiagramLayoutEditPolicy.createChangeConstraintCommand(diagram, selectedEditParts, editPart, rectangle);

                if (command != null) {
                    compoundCommand.add(command);
                }
            }

            getCurrentViewer().getEditDomain().getCommandStack().execute(compoundCommand.unwrap());
        } else {
            // [ermasterr] to open editor when the enter key pressed
            if (event.keyCode == SWT.Selection) {
                NodeElementEditPart targetEditPart = null;
                final List selectedEditParts = getCurrentViewer().getSelectedEditParts();
                for (final Object object : selectedEditParts) {
                    final NodeElementEditPart editPart = (NodeElementEditPart) object;
                    targetEditPart = editPart;
                }
                if (targetEditPart != null) {
                    Request request = new Request();
                    request.setType(RequestConstants.REQ_OPEN);
                    targetEditPart.performRequest(request);
                }
            }
        }

        return super.handleKeyDown(event);
    }

    @Override
    public void mouseDown(final MouseEvent e, final EditPartViewer viewer) {
        if (viewer.getContents() instanceof ERDiagramEditPart) {
            final ERDiagramEditPart editPart = (ERDiagramEditPart) viewer.getContents();
            final ERDiagram diagram = (ERDiagram) editPart.getModel();

            diagram.mousePoint = new Point(e.x, e.y);

            editPart.getFigure().translateToRelative(diagram.mousePoint);
        }

        super.mouseDown(e, viewer);
    }

}
