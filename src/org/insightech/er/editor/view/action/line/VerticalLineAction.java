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
import org.insightech.er.editor.controller.editpart.element.node.ERTableEditPart;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.controller.editpart.element.node.NoteEditPart;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.view.action.AbstractBaseSelectionAction;

public class VerticalLineAction extends AbstractBaseSelectionAction {

    public static final String ID = VerticalLineAction.class.getName();

    public VerticalLineAction(final ERDiagramEditor editor) {
        super(ID, ResourceString.getResourceString("action.title.vertical.line"), editor);

        setImageDescriptor(ERDiagramActivator.getImageDescriptor(ImageKey.VERTICAL_LINE));
//		this.setDisabledImageDescriptor(Activator
//				.getImageDescriptor(ImageKey.VERTICAL_LINE_DISABLED));
        setToolTipText(ResourceString.getResourceString("action.title.vertical.line"));
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
        try {
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
            final int start = firstRectangle.y;
            final int top = firstRectangle.y + firstRectangle.height;

            final Rectangle lastRectangle = list.remove(list.size() - 1).getFigure().getBounds();
            final int bottom = lastRectangle.y;

            if (top > bottom) {
                command = alignToStart(start, list);

            } else {
                command = adjustSpace(start, top, bottom, list);
            }
        } catch (final Exception e) {
            ERDiagramActivator.log(e);
        }

        return command;
    }

    private Command alignToStart(final int start, final List<NodeElementEditPart> list) {
        final CompoundCommand command = new CompoundCommand();

        for (final NodeElementEditPart editPart : list) {
            final NodeElement nodeElement = (NodeElement) editPart.getModel();

            final MoveElementCommand moveCommand = new MoveElementCommand(getDiagram(), editPart.getFigure().getBounds(), nodeElement.getX(), start, nodeElement.getWidth(), nodeElement.getHeight(), nodeElement);

            command.add(moveCommand);
        }

        return command.unwrap();
    }

    private Command adjustSpace(final int start, final int top, final int bottom, final List<NodeElementEditPart> list) {
        final CompoundCommand command = new CompoundCommand();

        int totalHeight = 0;

        for (final NodeElementEditPart editPart : list) {
            totalHeight += editPart.getFigure().getBounds().height;
        }

        final int space = (bottom - top - totalHeight) / (list.size() + 1);

        int y = top;

        for (final NodeElementEditPart editPart : list) {
            final NodeElement nodeElement = (NodeElement) editPart.getModel();

            y += space;

            final int nextY = y + editPart.getFigure().getBounds().height;

            if (y < start) {
                y = start;
            }

            final MoveElementCommand moveCommand = new MoveElementCommand(getDiagram(), editPart.getFigure().getBounds(), nodeElement.getX(), y, nodeElement.getWidth(), nodeElement.getHeight(), nodeElement);

            command.add(moveCommand);

            y = nextY;
        }

        return command.unwrap();
    }

    private NodeElementEditPart getFirstEditPart(final List<NodeElementEditPart> list) {
        NodeElementEditPart firstEditPart = null;

        for (final NodeElementEditPart editPart : list) {
            if (firstEditPart == null) {
                firstEditPart = editPart;

            } else {
                if (firstEditPart.getFigure().getBounds().y > editPart.getFigure().getBounds().y) {
                    firstEditPart = editPart;
                }
            }
        }

        return firstEditPart;
    }

    private static final Comparator<NodeElementEditPart> comparator = new NodeElementEditPartVerticalComparator();

    private static class NodeElementEditPartVerticalComparator implements Comparator<NodeElementEditPart> {

        @Override
        public int compare(final NodeElementEditPart o1, final NodeElementEditPart o2) {
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }

            final Rectangle bounds1 = o1.getFigure().getBounds();
            final Rectangle bounds2 = o2.getFigure().getBounds();

            final int rightY1 = bounds1.y + bounds1.height;
            final int rightY2 = bounds2.y + bounds2.height;

            return rightY1 - rightY2;
        }

    }

    public static class VerticalLineRetargetAction extends LabelRetargetAction {
        public VerticalLineRetargetAction() {
            super(ID, ResourceString.getResourceString("action.title.vertical.line"));

            setImageDescriptor(ERDiagramActivator.getImageDescriptor(ImageKey.VERTICAL_LINE));
//			this.setDisabledImageDescriptor(Activator
//					.getImageDescriptor(ImageKey.VERTICAL_LINE_DISABLED));
            setToolTipText(ResourceString.getResourceString("action.title.vertical.line"));
        }
    }

    @Override
    protected List<Command> getCommand(final EditPart editPart, final Event event) {
        return null;
    }
}
