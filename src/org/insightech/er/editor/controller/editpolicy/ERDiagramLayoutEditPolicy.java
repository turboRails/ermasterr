package org.insightech.er.editor.controller.editpolicy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.editor.controller.command.NothingToDoCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.bendpoint.MoveBendpointCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.CreateElementCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.MoveElementCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.category.MoveCategoryCommand;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.controller.editpart.element.node.CategoryEditPart;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementSelectionEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public class ERDiagramLayoutEditPolicy extends XYLayoutEditPolicy {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void showSizeOnDropFeedback(final CreateRequest request) {
        final Point p = new Point(request.getLocation().getCopy());

        final ZoomManager zoomManager = ((ScalableFreeformRootEditPart) getHost().getRoot()).getZoomManager();
        final double zoom = zoomManager.getZoom();

        final IFigure feedback = getSizeOnDropFeedback(request);

        final Dimension size = request.getSize().getCopy();
        feedback.translateToRelative(size);
        feedback.setBounds(new Rectangle((int) (p.x * zoom), (int) (p.y * zoom), size.width, size.height).expand(getCreationFeedbackOffset(request)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command createChangeConstraintCommand(final ChangeBoundsRequest request, final EditPart child, final Object constraint) {
        final ERDiagram diagram = (ERDiagram) getHost().getModel();

        final List selectedEditParts = getHost().getViewer().getSelectedEditParts();

        if (!(child instanceof NodeElementEditPart)) {
            return null;
        }

        return createChangeConstraintCommand(diagram, selectedEditParts, (NodeElementEditPart) child, (Rectangle) constraint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command createChangeConstraintCommand(final EditPart child, final Object constraint) {
        final ERDiagram diagram = (ERDiagram) getHost().getModel();

        final List selectedEditParts = getHost().getViewer().getSelectedEditParts();

        return createChangeConstraintCommandForNodeElement(diagram, selectedEditParts, child, constraint);
    }

    public static Command createChangeConstraintCommand(final ERDiagram diagram, final List selectedEditParts, final NodeElementEditPart editPart, final Rectangle rectangle) {
        try {
            final NodeElement nodeElement = (NodeElement) editPart.getModel();
            final Rectangle currentRectangle = editPart.getFigure().getBounds();

            boolean move = false;

            if (rectangle.width == currentRectangle.width && rectangle.height == currentRectangle.height) {
                move = true;
            }

            boolean nothingToDo = false;

            if (move && !(editPart instanceof CategoryEditPart)) {
                for (final Object selectedEditPart : selectedEditParts) {
                    if (selectedEditPart instanceof CategoryEditPart) {
                        final CategoryEditPart categoryEditPart = (CategoryEditPart) selectedEditPart;
                        final Category category = (Category) categoryEditPart.getModel();

                        if (category.contains(nodeElement)) {
                            nothingToDo = true;
                        }
                    }
                }
            }

            final List<Command> bendpointMoveCommandList = new ArrayList<Command>();

            final int oldX = nodeElement.getX();
            final int oldY = nodeElement.getY();

            final int diffX = rectangle.x - oldX;
            final int diffY = rectangle.y - oldY;

            for (final Object obj : editPart.getSourceConnections()) {
                final AbstractConnectionEditPart connection = (AbstractConnectionEditPart) obj;

                if (selectedEditParts.contains(connection.getTarget())) {
                    final ConnectionElement connectionElement = (ConnectionElement) connection.getModel();

                    final List<Bendpoint> bendpointList = connectionElement.getBendpoints();

                    for (int index = 0; index < bendpointList.size(); index++) {
                        final Bendpoint bendPoint = bendpointList.get(index);

                        if (bendPoint.isRelative()) {
                            break;
                        }

                        final MoveBendpointCommand moveCommand = new MoveBendpointCommand(connection, bendPoint.getX() + diffX, bendPoint.getY() + diffY, index);
                        bendpointMoveCommandList.add(moveCommand);
                    }

                }
            }

            final CompoundCommand compoundCommand = new CompoundCommand();

            if (!nothingToDo) {
                final Command changeConstraintCommand = createChangeConstraintCommandForNodeElement(diagram, selectedEditParts, editPart, rectangle);

                if (bendpointMoveCommandList.isEmpty()) {
                    return changeConstraintCommand;

                }

                compoundCommand.add(changeConstraintCommand);

            } else {
                compoundCommand.add(new NothingToDoCommand());
            }

            for (final Command command : bendpointMoveCommandList) {
                compoundCommand.add(command);
            }

            return compoundCommand;

        } catch (final Exception e) {
            ERDiagramActivator.log(e);
            return null;
        }
    }

    private static Command createChangeConstraintCommandForNodeElement(final ERDiagram diagram, final List selectedEditParts, final EditPart child, final Object constraint) {

        final Rectangle rectangle = (Rectangle) constraint;

        final NodeElementEditPart editPart = (NodeElementEditPart) child;
        final NodeElement nodeElement = (NodeElement) editPart.getModel();
        final Rectangle currentRectangle = editPart.getFigure().getBounds();

        boolean move = false;

        if (rectangle.width == currentRectangle.width && rectangle.height == currentRectangle.height) {
            move = true;
        }

        if (nodeElement instanceof Category) {
            final Category category = (Category) nodeElement;

            List<Category> otherCategories = null;

            if (move) {
                if (getOtherCategory(diagram, selectedEditParts, (Category) nodeElement) != null) {
                    return null;
                }

                otherCategories = getOtherSelectedCategories(selectedEditParts, category);
            }

            return new MoveCategoryCommand(diagram, rectangle.x, rectangle.y, rectangle.width, rectangle.height, category, otherCategories, move);

        } else {
            return new MoveElementCommand(diagram, currentRectangle, rectangle.x, rectangle.y, rectangle.width, rectangle.height, nodeElement);
        }
    }

    private static Category getOtherCategory(final ERDiagram diagram, final List selectedEditParts, final Category category) {
        final List<Category> selectedCategories = diagram.getDiagramContents().getSettings().getCategorySetting().getSelectedCategories();

        for (final NodeElement nodeElement : category.getContents()) {
            for (final Category otherCategory : selectedCategories) {
                if (otherCategory != category && !isSelected(selectedEditParts, otherCategory)) {
                    if (otherCategory.contains(nodeElement)) {
                        return otherCategory;
                    }
                }
            }
        }

        return null;
    }

    private static List<Category> getOtherSelectedCategories(final List selectedEditParts, final Category category) {
        final List<Category> otherCategories = new ArrayList<Category>();

        for (final Object object : selectedEditParts) {
            if (object instanceof CategoryEditPart) {
                final CategoryEditPart categoryEditPart = (CategoryEditPart) object;
                final Category otherCategory = (Category) categoryEditPart.getModel();

                if (otherCategory == category) {
                    break;
                }

                otherCategories.add(otherCategory);
            }
        }

        return otherCategories;
    }

    private static boolean isSelected(final List selectedEditParts, final Category category) {
        for (final Object object : selectedEditParts) {
            if (object instanceof NodeElementEditPart) {
                final NodeElementEditPart editPart = (NodeElementEditPart) object;
                if (editPart.getModel() == category) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command getCreateCommand(final CreateRequest request) {
        final ERDiagramEditPart editPart = (ERDiagramEditPart) getHost();

        final Point point = request.getLocation();
        editPart.getFigure().translateToRelative(point);

        final NodeElement element = (NodeElement) request.getNewObject();
        final ERDiagram diagram = (ERDiagram) editPart.getModel();

        Dimension size = request.getSize();
        final List<NodeElement> enclosedElementList = new ArrayList<NodeElement>();

        if (size != null) {
            final ZoomManager zoomManager = ((ScalableFreeformRootEditPart) getHost().getRoot()).getZoomManager();
            final double zoom = zoomManager.getZoom();
            size = new Dimension((int) (size.width / zoom), (int) (size.height / zoom));

            for (final Object child : editPart.getChildren()) {
                if (child instanceof NodeElementEditPart) {
                    final NodeElementEditPart nodeElementEditPart = (NodeElementEditPart) child;
                    final Rectangle bounds = nodeElementEditPart.getFigure().getBounds();

                    if (bounds.x > point.x && bounds.x + bounds.width < point.x + size.width && bounds.y > point.y && bounds.y + bounds.height < point.y + size.height) {
                        enclosedElementList.add((NodeElement) nodeElementEditPart.getModel());
                    }
                }
            }
        }

        return new CreateElementCommand(diagram, element, point.x, point.y, size, enclosedElementList);
    }

    @Override
    protected EditPolicy createChildEditPolicy(final EditPart child) {
        return new NodeElementSelectionEditPolicy();
    }

}
