package org.insightech.er.editor.view.action.line;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.LabelRetargetAction;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.MoveElementCommand;
import org.insightech.er.editor.controller.editpart.element.AbstractModelEditPart;
import org.insightech.er.editor.controller.editpart.element.node.ERTableEditPart;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.controller.editpart.element.node.NoteEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.view.action.AbstractBaseSelectionAction;

public class HorizontalLineAction extends AbstractBaseSelectionAction {

    public static final String ID = HorizontalLineAction.class.getName();

    public HorizontalLineAction(final ERDiagramEditor editor) {
        super(ID, ResourceString.getResourceString("action.title.horizontal.line"), editor);

        setImageDescriptor(ERDiagramActivator.getImageDescriptor(ImageKey.HORIZONTAL_LINE));
        // this.setDisabledImageDescriptor(Activator
        // .getImageDescriptor(ImageKey.HORIZONTAL_LINE_DISABLED));
        setToolTipText(ResourceString.getResourceString("action.title.horizontal.line"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean calculateEnabled() {
        final Command cmd = createCommand();
        if (cmd == null) {
            return false;
        }
        return cmd.canExecute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute(final Event event) {
        execute(createCommand());
    }

    private Command createCommand() {
        Command command = null;

        final List<NodeElementEditPart> list = new ArrayList<NodeElementEditPart>();

        for (final Object object : getSelectedObjects()) {
            if (object instanceof ERTableEditPart || object instanceof NoteEditPart) {
                list.add((NodeElementEditPart) object);
            }
        }

        if (list.size() < 3) {
            return null;
        }

        final NodeElementEditPart firstEditPart = getFirstEditPart(list);
        list.remove(firstEditPart);

        Collections.sort(list, comparator);

        final Rectangle firstRectangle = firstEditPart.getFigure().getBounds();
        final int start = firstRectangle.x;
        final int left = firstRectangle.x + firstRectangle.width;

        final Rectangle lastRectangle = list.remove(list.size() - 1).getFigure().getBounds();
        final int right = lastRectangle.x;

        if (left > right) {
            command = alignToStart(start, list);

        } else {
            command = adjustSpace(start, left, right, list);
        }

        return command;
    }

    private Command alignToStart(final int start, final List<NodeElementEditPart> list) {
        final CompoundCommand command = new CompoundCommand();

        final ERDiagram diagram = getDiagram();

        for (final AbstractModelEditPart editPart : list) {
            final NodeElement nodeElement = (NodeElement) editPart.getModel();

            final MoveElementCommand moveCommand = new MoveElementCommand(diagram, editPart.getFigure().getBounds(), start, nodeElement.getY(), nodeElement.getWidth(), nodeElement.getHeight(), nodeElement);

            command.add(moveCommand);

        }

        return command.unwrap();
    }

    private Command adjustSpace(final int start, final int left, final int right, final List<NodeElementEditPart> list) {
        final CompoundCommand command = new CompoundCommand();

        final ERDiagram diagram = getDiagram();

        int totalWidth = 0;

        for (final AbstractModelEditPart editPart : list) {
            totalWidth += editPart.getFigure().getBounds().width;
        }

        final int space = (right - left - totalWidth) / (list.size() + 1);

        int x = left;

        for (final AbstractModelEditPart editPart : list) {
            final NodeElement nodeElement = (NodeElement) editPart.getModel();

            x += space;

            final int nextX = x + editPart.getFigure().getBounds().width;

            if (x < start) {
                x = start;
            }

            final MoveElementCommand moveCommand = new MoveElementCommand(diagram, editPart.getFigure().getBounds(), x, nodeElement.getY(), nodeElement.getWidth(), nodeElement.getHeight(), nodeElement);
            command.add(moveCommand);

            x = nextX;
        }

        return command.unwrap();
    }

    private NodeElementEditPart getFirstEditPart(final List<NodeElementEditPart> list) {
        NodeElementEditPart firstEditPart = null;

        for (final NodeElementEditPart editPart : list) {
            if (firstEditPart == null) {
                firstEditPart = editPart;

            } else {
                if (firstEditPart.getFigure().getBounds().x > editPart.getFigure().getBounds().x) {
                    firstEditPart = editPart;
                }
            }
        }

        return firstEditPart;
    }

    private static final Comparator<AbstractModelEditPart> comparator = new AbstractModelEditPartHorizontalComparator();

    private static class AbstractModelEditPartHorizontalComparator implements Comparator<AbstractModelEditPart> {

        @Override
        public int compare(final AbstractModelEditPart o1, final AbstractModelEditPart o2) {
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }

            final Rectangle bounds1 = o1.getFigure().getBounds();
            final Rectangle bounds2 = o2.getFigure().getBounds();

            final int rightX1 = bounds1.x + bounds1.width;
            final int rightX2 = bounds2.x + bounds2.width;

            return rightX1 - rightX2;
        }

    }

    public static class HorizontalLineRetargetAction extends LabelRetargetAction {
        public HorizontalLineRetargetAction() {
            super(ID, ResourceString.getResourceString("action.title.horizontal.line"));

            setImageDescriptor(ERDiagramActivator.getImageDescriptor(ImageKey.HORIZONTAL_LINE));
            // this.setDisabledImageDescriptor(Activator
            // .getImageDescriptor(ImageKey.HORIZONTAL_LINE_DISABLED));
            setToolTipText(ResourceString.getResourceString("action.title.horizontal.line"));
        }

    }

    @Override
    protected List<Command> getCommand(final EditPart editPart, final Event event) {
        return null;
    }
}
